/*******************************************************************************
 * Copyright 2011 Matt Burns
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.oddprints.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.servlet.http.HttpServletRequest;

import uk.co.mattburns.pwinty.v2.Catalogue;
import uk.co.mattburns.pwinty.v2.Country;
import uk.co.mattburns.pwinty.v2.CountryCode;
import uk.co.mattburns.pwinty.v2.Order;
import uk.co.mattburns.pwinty.v2.Order.QualityLevel;
import uk.co.mattburns.pwinty.v2.Photo.Sizing;
import uk.co.mattburns.pwinty.v2.Pwinty;
import uk.co.mattburns.pwinty.v2.PwintyError;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.oddprints.Environment;
import com.oddprints.PMF;
import com.oddprints.PrintSize;
import com.oddprints.checkout.Address;
import com.oddprints.dao.ApplicationSetting.Settings;
import com.oddprints.util.EmailSender;
import com.oddprints.util.ServerUtils;
import com.oddprints.util.StringUtils;
import com.sun.jersey.api.client.ClientHandlerException;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Basket {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key id;

    @Persistent
    private String secret;

    @Persistent
    private Integer version;

    @Persistent
    private String checkoutSystemOrderNumber;

    @Persistent
    private Integer pwintyOrderNumber;

    @Persistent
    private State state;

    @Persistent(mappedBy = "basket")
    @Element(dependent = "true")
    private List<BasketItem> items;

    @Persistent
    private Date created;

    @Persistent
    private Integer shipping;

    @Persistent
    private Environment environment;

    @Persistent
    private CheckoutSystem checkoutSystem;

    private Order tempPwintyOrder;

    @Persistent
    private String buyerEmail;

    @Persistent
    private String couponKeyString;

    private Coupon coupon;

    /** allow this order to be auto-submitted after this time */
    @Persistent
    private Date submitAfter;

    public static final int LATEST_VERSION = 3;

    public static final int SUBMIT_DELAY = 30;
    public static int FLAT_RATE_SHIPPING = 299;

    public enum State {
        draft, awaiting_payment, payment_received, problem_submitting_to_lab, submitted_to_lab, dispatched_from_lab, cancelled;

        public String getUserFriendlyStatus() {
            if (this == awaiting_payment) {
                return "processing payment";
            } else {
                return toString().replaceAll("_", " ");
            }
        }
    }

    public enum CheckoutSystem {
        google, paypal;
    }

    public Basket(Environment environment) {
        this.state = State.draft;
        this.version = LATEST_VERSION;
        this.secret = UUID.randomUUID().toString();
        this.created = new Date();
        this.items = new ArrayList<BasketItem>();
        this.shipping = FLAT_RATE_SHIPPING;
        this.environment = environment;
    }

    public Key getId() {
        return id;
    }

    public String getIdString() {
        return KeyFactory.keyToString(id);
    }

    public String getSecret() {
        return secret;
    }

    public Integer getVersion() {
        return version;
    }

    public String getCheckoutSystemOrderNumber() {
        return checkoutSystemOrderNumber;
    }

    public void setCheckoutSystemOrderNumber(String checkoutSystemOrderNumber) {
        this.checkoutSystemOrderNumber = checkoutSystemOrderNumber;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public Integer getPwintyOrderNumber() {
        return pwintyOrderNumber;
    }

    public void setPwintyOrderNumber(Integer pwintyOrderNumber) {
        this.pwintyOrderNumber = pwintyOrderNumber;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        if (state == State.payment_received) {
            Date now = new Date();
            Date submitAfter = new Date(now.getTime()
                    + (SUBMIT_DELAY * 60 * 1000));
            setSubmitAfter(submitAfter);
        }
    }

    public Integer getShipping() {
        return shipping;
    }

    public static List<Basket> getBasketsByState(State state,
            PersistenceManager pm, long maxRecords) {
        Query query = pm.newQuery(Basket.class);

        query.setRange(0, maxRecords);

        query.setFilter("state == stateParam");
        query.declareParameters("Enum stateParam");

        @SuppressWarnings("unchecked")
        List<Basket> baskets = (List<Basket>) query.execute(state);

        return baskets;
    }

    public Date getCreated() {
        return created;
    }

    public List<BasketItem> getItems() {
        return items;
    }

    // helpful for EL access
    public int getSize() {
        return items.size();
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void addItem(BlobKey blobImage, long blobSize, String frameSize,
            PrintSize printSize) {
        addItem(blobImage, blobSize, frameSize, printSize, 1);
    }

    public void addItem(BlobKey blobImage, long blobSize, String frameSize,
            PrintSize printSize, int quantity) {
        if (state != State.draft) {
            throw new RuntimeException("cant edit draft");
        }

        BasketItem item = new BasketItem(this, blobImage, blobSize, frameSize,
                printSize, quantity);
        if (item.getBlobSize() == 0) {
            throw new RuntimeException("Attempted to add empty image");
        }
        items.add(item);
    }

    public void updateQuantity(int index, int quantity) {
        if (state != State.draft) {
            throw new RuntimeException("cant edit draft");
        }
        items.get(index).setQuantity(quantity);
    }

    public void delete(int itemNumber) {
        if (state != State.draft) {
            throw new RuntimeException("cant edit draft");
        }
        items.remove(itemNumber);
    }

    public void delete(BasketItem item) {
        if (state != State.draft) {
            throw new RuntimeException("cant edit draft");
        }
        items.remove(item);
    }

    public String getPrintPriceString() {
        return StringUtils.formatMoney(getPriceOfPrintsInPennies());
    }

    public int getPriceOfPrintsInPennies() {
        int orderPricePence = 0;
        for (BasketItem item : getItems()) {
            orderPricePence += item.getPrintSize().getPrice()
                    * item.getQuantity();
        }
        return orderPricePence;
    }

    public String getShippingPriceString() {
        return StringUtils.formatMoney(shipping);
    }

    public String getTotalPriceString() {
        return StringUtils.formatMoney(getTotalPrice());
    }

    public String getTotalShippingPriceStringNoSymbols() {
        return StringUtils.formatMoneyNoSymbol(shipping
                - getShippingDiscountAmount());
    }

    public String getTotalPriceStringNoSymbols() {
        return StringUtils.formatMoneyNoSymbol(getTotalPrice());
    }

    public int getTotalPrice() {
        return Math.max(0, getPriceOfPrintsInPennies() + shipping
                - getDiscountAmount());
    }

    public static Basket getBasketByKeyString(String basketKeyString,
            PersistenceManager pm) {
        if (basketKeyString == null) {
            return null;
        }
        try {
            return pm.getObjectById(Basket.class,
                    KeyFactory.stringToKey(basketKeyString));
        } catch (JDOObjectNotFoundException e) {
            return null;
        }

    }

    public static Basket fromSession(HttpServletRequest req,
            PersistenceManager pm) {
        String basketKeyString = (String) req.getSession().getAttribute(
                "basketKeyString");

        Basket basket = getBasketByKeyString(basketKeyString, pm);
        if (basket != null && basket.getState() != State.draft) {
            return copyBasketToNewDraftInSession(basket, req, pm);
        } else {
            return basket;
        }
    }

    public static Basket getOrCreateBasket(HttpServletRequest req,
            PersistenceManager pm) {
        Basket basket = Basket.fromSession(req, pm);
        if (basket == null) {
            Environment env = null;
            try {
                env = Environment.getDefault();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                return null;
            }
            basket = new Basket(env);
            pm.makePersistent(basket);
            String basketKeyString = KeyFactory.keyToString(basket.getId());
            req.getSession().setAttribute("basketKeyString", basketKeyString);
        }
        return basket;
    }

    /**
     * If user has progressed the Basket in the Session past the draft state,
     * let's clone it to a new draft to prevent accidental editing of submitted
     * orders
     */
    private static Basket copyBasketToNewDraftInSession(Basket basket,
            HttpServletRequest req, PersistenceManager pm) {
        Basket newDraftBasket = fromBasket(basket);
        pm.makePersistent(newDraftBasket);
        String basketKeyString = KeyFactory.keyToString(newDraftBasket.getId());
        req.getSession().setAttribute("basketKeyString", basketKeyString);
        return newDraftBasket;
    }

    public static Basket fromBasket(Basket basket) {
        Basket newBasket = new Basket(basket.getEnvironment());
        for (BasketItem item : basket.getItems()) {
            newBasket.addItem(item.getBlobImage(), item.getBlobSize(),
                    item.getFrameSize(), item.getPrintSize(),
                    item.getQuantity());
        }
        newBasket.setCoupon(basket.getCoupon());
        return newBasket;
    }

    public Order getPwintyOrder() {
        if (pwintyOrderNumber == null) {
            return null;
        }
        return getEnvironment().getPwinty().getOrder(pwintyOrderNumber);
    }

    /**
     * Safer and faster version for calling from EL
     * 
     * @return will return null on pwinty timeout
     */
    public Order getPwintyOrderEL() {
        if (pwintyOrderNumber == null) {
            return null;
        }
        if (tempPwintyOrder == null) {
            fetchPwintyOrder();
        }
        if (tempPwintyOrder == null) { // Timeout? Try one more time
            fetchPwintyOrder();
        }
        return tempPwintyOrder;
    }

    private void fetchPwintyOrder() {
        try {
            tempPwintyOrder = getEnvironment().getPwinty().getOrder(
                    pwintyOrderNumber);
        } catch (ClientHandlerException e) {
            // do nothing, leave it as null
        } catch (PwintyError pe) {
            if (getEnvironment() == Environment.SANDBOX && pe.getCode() == 404) {
                // ignore because they are from old api keys.
                // TODO: clear my dev db, then can just remove this.
            } else {
                throw pe;
            }
        }
    }

    public Date getSubmitAfter() {
        return submitAfter;
    }

    public void setSubmitAfter(Date submitAfter) {
        this.submitAfter = submitAfter;
    }

    /**
     * Short delay window between payment received and being ready to submit has
     * finished. No changing address after this point.
     * 
     * @return true if we can no longer change the address
     */
    public boolean isAddressConfirmed() {
        Date now = new Date();
        return version < 3 || submitAfter != null && submitAfter.before(now);
    }

    public boolean isAddressEditable() {
        Date now = new Date();
        return submitAfter != null && submitAfter.after(now)
                && getState() == State.payment_received;
    }

    public int getAddressEditableRemaining() {
        if (!isAddressEditable()) {
            return 0;
        }
        Date now = new Date();
        long result = (submitAfter.getTime() - now.getTime()) / 60000;
        // result -= 2; // reduce remaining time by 2 minutes just to
        return Math.max(0, (int) result);
    }

    public URL getUrl() {
        return getUrlWithPath("/orders/");
    }

    public URL getSubmitUrl() {
        return getUrlWithPath("/orders/submit/");
    }

    public CheckoutSystem getCheckoutSystem() {
        return checkoutSystem;
    }

    public void setCheckoutSystem(CheckoutSystem checkoutSystem) {
        this.checkoutSystem = checkoutSystem;
    }

    private URL getUrlWithPath(String path) {
        try {
            return new URL(ServerUtils.getCleanHostUrl() + path + getSecret()
                    + "/" + KeyFactory.keyToString(id));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCouponKeyString() {
        return couponKeyString;
    }

    public void setCouponKeyString(String couponKeyString) {
        this.couponKeyString = couponKeyString;
    }

    public Coupon getCoupon() {
        if (couponKeyString == null) {
            return null;
        }
        if (coupon == null) {
            PersistenceManager pm = PMF.get().getPersistenceManager();
            coupon = Coupon.getCouponByKeyString(couponKeyString, pm);
        }
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        if (coupon != null) {
            this.couponKeyString = coupon.getIdString();
        }
    }

    public Integer getDiscountAmount() {
        return getCartDiscountAmount() + getShippingDiscountAmount();
    }

    public Integer getCartDiscountAmount() {
        int amount = 0;
        if (getCoupon() != null) {
            switch (getCoupon().getDiscountType()) {
                case percentage:
                    amount = (int) Math
                            .round((getCoupon().getDiscountAmount() * getPriceOfPrintsInPennies()) / 100.0);
                    break;
                case pence:
                    amount = getCoupon().getDiscountAmount();
                    break;
            }
        }
        amount = Math.min(amount, getPriceOfPrintsInPennies() - 1);

        return amount;
    }

    public Integer getShippingDiscountAmount() {
        int amount = 0;
        if (getCoupon() != null) {
            switch (getCoupon().getDiscountType()) {
                case percentage:
                    break;
                case pence:
                    amount = getCoupon().getDiscountAmount();

                    int surplus = amount - getCartDiscountAmount();
                    amount = Math.min(surplus, shipping);
                    break;
                default:
                    break;
            }
        }

        return amount;
    }

    public String getDiscountAmountString() {
        return StringUtils.formatMoney(getDiscountAmount());
    }

    public String getCartDiscountAmountStringNoSymbol() {
        return StringUtils.formatMoneyNoSymbol(getCartDiscountAmount());
    }

    public boolean isHasWarning() {
        return !getWarning().isEmpty();
    }

    public String getWarning() {
        String warning = null;
        for (BasketItem item : getItems()) {
            if (item.getPrintSize() == PrintSize._4x18) {
                warning = ApplicationSetting
                        .getSetting(Settings.CHECKOUT_WARNING);
                break;
            }
        }
        return (warning == null) ? "" : warning;
    }

    public Order createOrderOnPwinty(Address address) {
        Pwinty pwinty = getEnvironment().getPwinty();
        QualityLevel quality = QualityLevel.Standard; // Default

        CountryCode labCountry = CountryCode.valueOf(address.getCountryCode());
        CountryCode destinationCountry = labCountry;

        // If destination has no lab, use GB and Pro
        List<Country> countries = pwinty.getCountries();
        for (Country country : countries) {
            if (country.getCountryCode() == destinationCountry) {
                if (!country.getHasProducts()) {
                    labCountry = CountryCode.GB;
                    quality = QualityLevel.Pro;
                    break;
                }
            }
        }

        Catalogue catalogue = pwinty.getCatalogue(labCountry, quality);

        if (!canBePrintedWithCatalogue(catalogue)) {
            // Can't do it standard? Try Pro...
            quality = QualityLevel.Pro;
            catalogue = pwinty.getCatalogue(labCountry, quality);
            if (!canBePrintedWithCatalogue(catalogue)) {
                // Still can't? Abort...
                String error = "Cannot fulfil this Basket Order with any Lab";
                EmailSender.INSTANCE.sendToAdmin(error, error);
                throw new RuntimeException(error);
            }
        }

        Order newOrder = new Order(pwinty, labCountry, destinationCountry,
                quality);
        newOrder.setRecipientName(address.getRecipientName());
        newOrder.setAddress1(address.getAddress1());
        newOrder.setAddress2(address.getAddress2());
        newOrder.setAddressTownOrCity(address.getTownOrCity());
        newOrder.setStateOrCounty(address.getStateOrCounty());
        newOrder.setPostalOrZipCode(address.getPostalOrZipCode());

        List<BasketItem> basketItems = getItems();

        for (BasketItem item : basketItems) {
            newOrder.addPhoto(item.getFullImageUrl(), item.getPrintSize()
                    .toPwintyType(), item.getQuantity(), Sizing.Crop);
        }

        setPwintyOrderNumber(newOrder.getId());

        return newOrder;
    }

    private boolean canBePrintedWithCatalogue(Catalogue catalogue) {
        for (BasketItem item : getItems()) {
            if (!catalogue.containsType(item.getPrintSize().toPwintyType())) {
                return false;
            }
        }
        return true;
    }

}
