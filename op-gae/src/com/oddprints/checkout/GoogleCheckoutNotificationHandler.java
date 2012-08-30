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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.checkout.sdk.domain.AuthorizationAmountNotification;
import com.google.checkout.sdk.domain.OrderSummary;
import com.google.checkout.sdk.notifications.BaseNotificationDispatcher;
import com.google.checkout.sdk.notifications.Notification;
import com.oddprints.dao.Basket.CheckoutSystem;

public class GoogleCheckoutNotificationHandler extends
        BaseNotificationDispatcher {

    private CheckoutNotificationHandler checkoutNotificationHandler;

    public GoogleCheckoutNotificationHandler(HttpServletRequest request,
            HttpServletResponse response,
            CheckoutNotificationHandler checkoutNotificationHandler) {
        super(request, response);
        this.checkoutNotificationHandler = checkoutNotificationHandler;
    }

    @Override
    protected void onAllNotifications(OrderSummary orderSummary,
            Notification notification) throws Exception {
    };

    @Override
    public void onAuthorizationAmountNotification(OrderSummary orderSummary,
            AuthorizationAmountNotification notification) {
        String checkoutSystemOrderNumber = orderSummary.getGoogleOrderNumber();

        String basketKeyString = (String) orderSummary.getShoppingCart()
                .getMerchantPrivateData().getContent().get(0);
        String buyerEmail = orderSummary.getBuyerShippingAddress().getEmail();

        Address address = extractAddress(orderSummary);

        checkoutNotificationHandler.onAuthorizationAmountNotification(
                CheckoutSystem.google, checkoutSystemOrderNumber,
                basketKeyString, buyerEmail, address);

    }

    @Override
    protected void onNewOrderNotification(OrderSummary orderSummary,
            com.google.checkout.sdk.domain.NewOrderNotification notification)
            throws Exception {

        String checkoutSystemOrderNumber = orderSummary.getGoogleOrderNumber();

        String basketKeyString = (String) orderSummary.getShoppingCart()
                .getMerchantPrivateData().getContent().get(0);

        String buyerEmail = orderSummary.getBuyerShippingAddress().getEmail();
        checkoutNotificationHandler.onNewOrderNotification(
                CheckoutSystem.google, checkoutSystemOrderNumber,
                basketKeyString, buyerEmail);
    };

    private Address extractAddress(OrderSummary orderSummary) {
        Address address = new Address();

        address.setRecipientName(orderSummary.getBuyerShippingAddress()
                .getContactName());
        address.setAddress1(orderSummary.getBuyerShippingAddress()
                .getAddress1());
        address.setAddress2(orderSummary.getBuyerShippingAddress()
                .getAddress2());
        address.setTownOrCity(orderSummary.getBuyerShippingAddress().getCity());
        address.setStateOrCounty(orderSummary.getBuyerShippingAddress()
                .getRegion());
        address.setPostalOrZipCode(orderSummary.getBuyerShippingAddress()
                .getPostalCode());
        address.setCountry(orderSummary.getBuyerShippingAddress()
                .getCountryCode());
        return address;
    }

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

}
