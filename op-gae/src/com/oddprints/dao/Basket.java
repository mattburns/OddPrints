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

import uk.co.mattburns.pwinty.Order;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.oddprints.Environment;
import com.oddprints.PrintSize;
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

    public static final int LATEST_VERSION = 2;
    public static int FLAT_RATE_SHIPPING = 299;

    public enum State {
        draft, awaiting_payment, payment_received, problem_submitting_to_lab, submitted_to_lab, dispatched_from_lab;

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
    }

    public Integer getShipping() {
        return shipping;
    }

    public static List<Basket> getBasketsByState(State state,
            PersistenceManager pm) {
        Query query = pm.newQuery(Basket.class);

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

    public void updateSticker(BlobKey blobImage, long blobSize) {
        BasketItem currentSticker = null;
        for (BasketItem item : getItems()) {
            if (item.getPrintSize() == PrintSize._2x4) {
                currentSticker = item;
            }
        }
        if (currentSticker != null) {
            delete(currentSticker);
        }
        addItem(blobImage, blobSize, "", PrintSize._2x4, 1);
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

        if (printSize == PrintSize._2x4) {
            frameSize = "Custom sticker";
        }

        BasketItem item = new BasketItem(this, blobImage, blobSize, frameSize,
                printSize, quantity);
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
        int totalPricePence = getPriceOfPrintsInPennies() + shipping;

        return StringUtils.formatMoney(totalPricePence);
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
        }
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
}
