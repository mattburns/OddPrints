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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.checkout.sdk.notifications.Notification;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class NotificationLog {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key id;

    @Persistent
    private Integer version;

    @Persistent
    private String notificationSerialNumber;

    @Persistent
    private String googleOrderNumber;

    @Persistent
    private String email;

    @Persistent
    private double price;

    public static final int LATEST_VERSION = 1;

    public enum Type {
        authorized;
    }

    public NotificationLog(Notification notification) {
        this.notificationSerialNumber = notification.getSerialNumber();
        this.googleOrderNumber = notification.getGoogleOrderNumber();
        this.price = notification.getOrderSummary().getAuthorization()
                .getAuthorizationAmount().getValue().doubleValue();
        this.email = notification.getOrderSummary().getBuyerShippingAddress()
                .getEmail();
        this.version = LATEST_VERSION;
    }

    public Key getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public String getNotificationSerialNumber() {
        return notificationSerialNumber;
    }

    public String getGoogleOrderNumber() {
        return googleOrderNumber;
    }

    public String getEmail() {
        return email;
    }

    public double getPrice() {
        return price;
    }
}
