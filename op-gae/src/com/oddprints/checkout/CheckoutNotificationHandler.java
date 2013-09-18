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
package com.oddprints.checkout;

import java.util.List;

import javax.jdo.PersistenceManager;

import uk.co.mattburns.pwinty.Order;
import uk.co.mattburns.pwinty.Photo.Sizing;
import uk.co.mattburns.pwinty.Pwinty;

import com.oddprints.PMF;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.CheckoutSystem;
import com.oddprints.dao.Basket.State;
import com.oddprints.dao.BasketItem;
import com.oddprints.util.EmailSender;
import com.oddprints.util.EmailTemplates;

public class CheckoutNotificationHandler {

    void onAuthorizationAmountNotification(CheckoutSystem checkoutSystem,
            String checkoutSystemOrderNumber, String basketKeyString,
            String buyerEmail, Address address) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        Basket basket = Basket.getBasketByKeyString(basketKeyString, pm);

        if (basket.getState() == State.awaiting_payment) {
            basket.setState(State.payment_received);
            basket.setCheckoutSystem(checkoutSystem);
            basket.setCheckoutSystemOrderNumber(checkoutSystemOrderNumber);
            basket.setBuyerEmail(buyerEmail);

            // create order on lab
            createOrderOnPwinty(basket, address, pm);

            String msg = EmailTemplates.orderReadyToSubmit(basket.getUrl(),
                    basket.getCheckoutSystem());
            EmailSender.INSTANCE.sendToAdmin(msg,
                    "Order ready to submit to pwinty!");
        } else {
            String msg = "Auth notice received but not doing anything because basket state is "
                    + basket.getState()
                    + " google/paypal order number:"
                    + checkoutSystemOrderNumber;
            EmailSender.INSTANCE.sendToAdmin(msg, msg);
        }

        pm.close();
    }

    void onNewOrderNotification(CheckoutSystem checkoutSystem,
            String checkoutSystemOrderNumber, String basketKeyString,
            String buyerEmail) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        Basket basket = Basket.getBasketByKeyString(basketKeyString, pm);
        basket.setCheckoutSystem(checkoutSystem);
        basket.setCheckoutSystemOrderNumber(checkoutSystemOrderNumber);
        basket.setBuyerEmail(buyerEmail);

        String subject = "OddPrints Confirmation for Order #"
                + checkoutSystemOrderNumber;
        String msg = EmailTemplates.newOrder(checkoutSystemOrderNumber,
                basket.getUrl());
        EmailSender.INSTANCE.send(buyerEmail, msg, subject);
        pm.makePersistent(basket);
        pm.close();
    };

    private void createOrderOnPwinty(Basket basket, Address address,
            PersistenceManager pm) {

        Pwinty pwinty = basket.getEnvironment().getPwinty();

        Order newOrder = new Order(pwinty);
        newOrder.setRecipientName(address.getRecipientName());
        newOrder.setAddress1(address.getAddress1());
        newOrder.setAddress2(address.getAddress2());
        newOrder.setAddressTownOrCity(address.getTownOrCity());
        newOrder.setStateOrCounty(address.getStateOrCounty());
        newOrder.setPostalOrZipCode(address.getPostalOrZipCode());
        newOrder.setCountry(address.getCountry());

        List<BasketItem> basketItems = basket.getItems();

        for (BasketItem item : basketItems) {
            newOrder.addPhoto(item.getFullImageUrl(), item.getPrintSize()
                    .toPwintyType(), item.getQuantity(), Sizing.Crop);
        }

        basket.setPwintyOrderNumber(newOrder.getId());
    }
}
