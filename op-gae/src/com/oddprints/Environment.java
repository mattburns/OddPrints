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

import uk.co.mattburns.pwinty.v2.Pwinty;

import com.google.checkout.sdk.commands.ApiContext;
import com.oddprints.dao.ApplicationSetting;
import com.oddprints.dao.ApplicationSetting.Settings;

public enum Environment {
    LIVE(uk.co.mattburns.pwinty.v2.Pwinty.Environment.LIVE,
            com.google.checkout.sdk.commands.Environment.PRODUCTION), SANDBOX(
            uk.co.mattburns.pwinty.v2.Pwinty.Environment.SANDBOX,
            com.google.checkout.sdk.commands.Environment.SANDBOX);

    private final uk.co.mattburns.pwinty.v2.Pwinty.Environment pwintyEnvironment;
    private final com.google.checkout.sdk.commands.Environment googleCheckoutEnvironment;

    private final Settings pwintyMerchantIdSetting;
    private final Settings pwintyMerchantKeySetting;

    private final Settings googleCheckoutMerchantKeySetting;
    private final Settings googleCheckoutMerchantIdSetting;

    private Environment(
            uk.co.mattburns.pwinty.v2.Pwinty.Environment pwintyEnvironment,
            com.google.checkout.sdk.commands.Environment googleCheckoutEnvironment) {
        this.pwintyEnvironment = pwintyEnvironment;
        this.googleCheckoutEnvironment = googleCheckoutEnvironment;

        if (googleCheckoutEnvironment
                .equals(com.google.checkout.sdk.commands.Environment.SANDBOX)) {
            googleCheckoutMerchantKeySetting = Settings.GOOGLE_CHECKOUT_MERCHANT_KEY_SANDBOX;
            googleCheckoutMerchantIdSetting = Settings.GOOGLE_CHECKOUT_MERCHANT_ID_SANDBOX;
        } else {
            googleCheckoutMerchantKeySetting = Settings.GOOGLE_CHECKOUT_MERCHANT_KEY_LIVE;
            googleCheckoutMerchantIdSetting = Settings.GOOGLE_CHECKOUT_MERCHANT_ID_LIVE;
        }

        if (pwintyEnvironment == uk.co.mattburns.pwinty.v2.Pwinty.Environment.SANDBOX) {
            pwintyMerchantKeySetting = Settings.PWINTY_MERCHANT_KEY_SANDBOX;
            pwintyMerchantIdSetting = Settings.PWINTY_MERCHANT_ID_SANDBOX;
        } else {
            pwintyMerchantKeySetting = Settings.PWINTY_MERCHANT_KEY_LIVE;
            pwintyMerchantIdSetting = Settings.PWINTY_MERCHANT_ID_LIVE;
        }
    }

    public ApiContext getGoogleCheckoutAPIContext() {
        return new ApiContext(googleCheckoutEnvironment,
                getGoogleCheckoutMerchantId(), getGoogleCheckoutMerchantKey(),
                "GBP");
    }

    public Pwinty getPwinty() {
        return new Pwinty(pwintyEnvironment, getPwintyMerchantId(),
                getPwintyMerchantKey());
    }

    public boolean isSandbox() {
        return this == SANDBOX;
    }

    public String getGoogleCheckoutMerchantId() {
        return ApplicationSetting.getSetting(googleCheckoutMerchantIdSetting);
    }

    public String getGoogleCheckoutMerchantKey() {
        return ApplicationSetting.getSetting(googleCheckoutMerchantKeySetting);
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
