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

import uk.co.mattburns.pwinty.Pwinty;

import com.google.checkout.sdk.commands.ApiContext;
import com.oddprints.dao.ApplicationSetting;
import com.oddprints.dao.ApplicationSetting.Settings;

public enum Environment {
    LIVE(uk.co.mattburns.pwinty.Pwinty.Environment.LIVE,
            com.google.checkout.sdk.commands.Environment.PRODUCTION), SANDBOX(
            uk.co.mattburns.pwinty.Pwinty.Environment.SANDBOX,
            com.google.checkout.sdk.commands.Environment.SANDBOX);

    private final uk.co.mattburns.pwinty.Pwinty.Environment pwintyEnvironment;
    private final com.google.checkout.sdk.commands.Environment checkoutEnvironment;

    private final Settings pwintyMerchantIdSetting = Settings.PWINTY_MERCHANT_ID;
    private final Settings pwintyMerchantKeySetting = Settings.PWINTY_MERCHANT_KEY;

    private final Settings checkoutMerchantKeySetting;
    private final Settings checkoutMerchantIdSetting;

    private Environment(
            uk.co.mattburns.pwinty.Pwinty.Environment pwintyEnvironment,
            com.google.checkout.sdk.commands.Environment checkoutEnvironment) {
        this.pwintyEnvironment = pwintyEnvironment;
        this.checkoutEnvironment = checkoutEnvironment;

        if (checkoutEnvironment
                .equals(com.google.checkout.sdk.commands.Environment.SANDBOX)) {
            checkoutMerchantKeySetting = Settings.CHECKOUT_MERCHANT_KEY_SANDBOX;
            checkoutMerchantIdSetting = Settings.CHECKOUT_MERCHANT_ID_SANDBOX;
        } else {
            checkoutMerchantKeySetting = Settings.CHECKOUT_MERCHANT_KEY_LIVE;
            checkoutMerchantIdSetting = Settings.CHECKOUT_MERCHANT_ID_LIVE;
        }
    }

    public ApiContext getCheckoutAPIContext() {
        return new ApiContext(checkoutEnvironment, getCheckoutMerchantId(),
                getCheckoutMerchantKey(), "GBP");
    }

    public Pwinty getPwinty() {
        return new Pwinty(pwintyEnvironment, getPwintyMerchantId(),
                getPwintyMerchantKey());
    }

    public boolean isSandbox() {
        return this == SANDBOX;
    }

    public String getCheckoutMerchantId() {
        return ApplicationSetting.getSetting(checkoutMerchantIdSetting);
    }

    public String getCheckoutMerchantKey() {
        return ApplicationSetting.getSetting(checkoutMerchantKeySetting);
    }

    public String getPwintyMerchantId() {
        return ApplicationSetting.getSetting(pwintyMerchantIdSetting);
    }

    public String getPwintyMerchantKey() {
        return ApplicationSetting.getSetting(pwintyMerchantKeySetting
                .toString());
    }

    public static Environment getDefault() {
        if (ApplicationSetting.getSetting(Settings.APPLICATION_ENVIRONMENT)
                .equals("LIVE")) {
            return Environment.LIVE;
        } else {
            return Environment.SANDBOX;
        }
    }
}
