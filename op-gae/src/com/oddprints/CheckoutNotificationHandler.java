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
package com.oddprints;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.co.mattburns.pwinty.Order;
import uk.co.mattburns.pwinty.Photo.Sizing;
import uk.co.mattburns.pwinty.Pwinty;

import com.google.checkout.sdk.domain.AuthorizationAmountNotification;
import com.google.checkout.sdk.domain.OrderSummary;
import com.google.checkout.sdk.notifications.BaseNotificationDispatcher;
import com.google.checkout.sdk.notifications.Notification;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.State;
import com.oddprints.dao.BasketItem;
import com.oddprints.util.EmailSender;
import com.oddprints.util.EmailTemplates;

public class CheckoutNotificationHandler extends BaseNotificationDispatcher {

    public CheckoutNotificationHandler(HttpServletRequest request,
            HttpServletResponse response) {
        super(request, response);
    }

    @Override
    protected void onAllNotifications(OrderSummary orderSummary,
            Notification notification) throws Exception {
    };

    @Override
    public void onAuthorizationAmountNotification(OrderSummary orderSummary,
            AuthorizationAmountNotification notification) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        String basketKeyString = (String) orderSummary.getShoppingCart()
                .getMerchantPrivateData().getContent().get(0);
        Basket basket = Basket.getBasketByKeyString(basketKeyString, pm);

        if (basket.getState() == State.awaiting_payment) {
            basket.setState(State.payment_received);
            basket.setGoogleOrderNumber(orderSummary.getGoogleOrderNumber());

            // create order on lab
            createOrderOnPwinty(basket, orderSummary, pm);

            String msg = EmailTemplates.orderReadyToSubmit(basket.getUrl());
            EmailSender.INSTANCE.sendToAdmin(msg,
                    "Order ready to submit to pwinty!");
        } else {
            String msg = "Auth notice received but not doing anything because basket state is "
                    + basket.getState()
                    + " gon:"
                    + orderSummary.getGoogleOrderNumber();
            EmailSender.INSTANCE.sendToAdmin(msg, msg);
        }

        pm.close();
    }

    @Override
    protected void onNewOrderNotification(OrderSummary orderSummary,
            com.google.checkout.sdk.domain.NewOrderNotification notification)
            throws Exception {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        String basketKeyString = (String) orderSummary.getShoppingCart()
                .getMerchantPrivateData().getContent().get(0);

        Basket basket = Basket.getBasketByKeyString(basketKeyString, pm);
        String googleOrderNumber = orderSummary.getGoogleOrderNumber();
        basket.setGoogleOrderNumber(googleOrderNumber);

        String subject = "OddPrints Confirmation for Order #"
                + googleOrderNumber;
        String msg = EmailTemplates
                .newOrder(googleOrderNumber, basket.getUrl());
        EmailSender.INSTANCE.send(orderSummary.getBuyerShippingAddress()
                .getEmail(), msg, subject);

        pm.close();
    };

    @Override
    protected boolean hasAlreadyHandled(String serialNumber,
            OrderSummary orderSummary, Notification notification)
            throws Exception {

        return false;
    }

    @Override
    protected void rememberSerialNumber(String serialNumber,
            OrderSummary orderSummary, Notification notification)
            throws Exception {
        // TODO Auto-generated method stub

    }

    public void createOrderOnPwinty(Basket basket, OrderSummary orderSummary,
            PersistenceManager pm) {

        Pwinty pwinty = basket.getEnvironment().getPwinty();

        Order newOrder = new Order(pwinty);
        newOrder.setRecipientName(orderSummary.getBuyerShippingAddress()
                .getContactName());
        newOrder.setAddress1(orderSummary.getBuyerShippingAddress()
                .getAddress1());
        newOrder.setAddress2(orderSummary.getBuyerShippingAddress()
                .getAddress2());
        newOrder.setAddressTownOrCity(orderSummary.getBuyerShippingAddress()
                .getCity());
        newOrder.setStateOrCounty(orderSummary.getBuyerShippingAddress()
                .getRegion());
        newOrder.setPostalOrZipCode(orderSummary.getBuyerShippingAddress()
                .getPostalCode());
        newOrder.setCountry(orderSummary.getBuyerShippingAddress()
                .getCountryCode());

        List<BasketItem> basketItems = basket.getItems();

        for (BasketItem item : basketItems) {
            newOrder.addPhoto(item.getFullImageUrl(), item.getPrintSize()
                    .toPwintyType(), item.getQuantity(), Sizing.Crop);
        }

        basket.setPwintyOrderNumber(newOrder.getId());
    }
}
