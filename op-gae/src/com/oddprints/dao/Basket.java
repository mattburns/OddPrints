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
import com.google.checkout.sdk.domain.OrderSummary;
import com.google.common.collect.Lists;
import com.oddprints.Environment;
import com.oddprints.PrintSize;
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
    private String googleOrderNumber;

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

    private Order tempPwintyOrder;

    public static final int LATEST_VERSION = 1;
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

    public String getSecret() {
        return secret;
    }

    public Integer getVersion() {
        return version;
    }

    public String getGoogleOrderNumber() {
        return googleOrderNumber;
    }

    public void setGoogleOrderNumber(String googleOrderNumber) {
        this.googleOrderNumber = googleOrderNumber;
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

    public static Basket getBasketByOrderNumber(String googleOrderNumber,
            PersistenceManager pm) {
        Query query = pm.newQuery(Basket.class);

        query.setFilter("googleOrderNumber == '" + googleOrderNumber + "'");

        @SuppressWarnings("unchecked")
        List<Basket> baskets = (List<Basket>) query.execute();
        if (baskets.size() != 1) {
            throw new RuntimeException(
                    "I expected exactly one basket for a given google order number");
        }
        return baskets.get(0);
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

    public void addItem(BlobKey blobImage, long blobSize, String frameSize,
            PrintSize printSize) {
        if (state != State.draft) {
            throw new RuntimeException("cant edit draft");
        }

        BasketItem item = new BasketItem(this, blobImage, blobSize, frameSize,
                printSize, 1);
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
        items.get(itemNumber).deleteBlob();
        items.remove(itemNumber);
    }

    public String getPrintPriceString() {
        return StringUtils.formatMoney(getPrintPrice());
    }

    private int getPrintPrice() {
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
        int totalPricePence = getPrintPrice() + shipping;

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
                    item.getFrameSize(), item.getPrintSize());
        }
        return newBasket;
    }

    public OrderSummary getGoogleOrderSummary() {
        if (googleOrderNumber == null) {
            return null;
        }
        List<String> gons = Lists.newArrayList(googleOrderNumber);

        List<OrderSummary> summaries = getEnvironment().getCheckoutAPIContext()
                .reportsRequester().requestOrderSummaries(gons);
        if (summaries.size() != 1) {
            return null;
        }
        return summaries.get(0);
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
        String hostUrl;
        String environment = System
                .getProperty("com.google.appengine.runtime.environment");
        if ("Production".equals(environment)) {
            hostUrl = "http://www.oddprints.com";
        } else {
            hostUrl = "http://localhost:8888";
        }

        try {
            return new URL(hostUrl + "/orders/" + getSecret() + "/"
                    + KeyFactory.keyToString(id));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
