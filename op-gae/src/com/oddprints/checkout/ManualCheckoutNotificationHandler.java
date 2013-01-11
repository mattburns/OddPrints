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

import com.google.checkout.sdk.domain.OrderSummary;
import com.oddprints.dao.Basket.CheckoutSystem;

public class ManualCheckoutNotificationHandler {

    private CheckoutNotificationHandler checkoutNotificationHandler;

    public ManualCheckoutNotificationHandler(
            CheckoutNotificationHandler checkoutNotificationHandler) {
        this.checkoutNotificationHandler = checkoutNotificationHandler;
    }

    public void manuallyAuthorizeOrder(CheckoutSystem checkout,
            String checkoutSystemOrderNumber, String basketKeyString,
            String buyerEmail, Address address) {

        checkoutNotificationHandler
                .onAuthorizationAmountNotification(checkout,
                        checkoutSystemOrderNumber, basketKeyString, buyerEmail,
                        address);

    }

    protected void onNewOrderNotification(OrderSummary orderSummary,
            com.google.checkout.sdk.domain.NewOrderNotification notification)
            throws Exception {
        throw new RuntimeException("Not impl yet");

    };

}
