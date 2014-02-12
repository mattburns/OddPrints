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

import javax.jdo.PersistenceManager;

import com.oddprints.PMF;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.CheckoutSystem;
import com.oddprints.dao.Basket.State;
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
            basket.createOrderOnPwinty(address);

            String subject = "OddPrints Confirmation for Order #"
                    + checkoutSystemOrderNumber;
            String msg = EmailTemplates.paymentRecieved(
                    checkoutSystemOrderNumber, basket.getUrl(), address);
            EmailSender.INSTANCE.send(buyerEmail, msg, subject);

            String adminMsg = EmailTemplates.orderReadyToSubmit(
                    basket.getUrl(), basket.getCheckoutSystem());
            EmailSender.INSTANCE.sendToAdmin(adminMsg,
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
        pm.makePersistent(basket);
        pm.close();
    };
}
