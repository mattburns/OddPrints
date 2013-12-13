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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.oddprints.util.StringUtils;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Coupon {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key id;

    @Persistent
    private String code;

    @Persistent
    private Integer version;

    @Persistent
    private Date created;

    @Persistent
    private Date expires;

    @Persistent
    private String email;

    @Persistent
    private DiscountType discountType;

    @Persistent
    private Integer discountAmount;

    @Persistent
    private Integer minimumOrderValue;

    public static final int LATEST_VERSION = 1;

    private static final SimpleDateFormat ISO_DATE_FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd'T'hh:mm");

    public enum DiscountType {
        percentage, pence;
    }

    public Coupon(DiscountType discountType, Integer discountAmount) {
        super();
        this.code = UUID.randomUUID().toString().toUpperCase();
        this.created = Calendar.getInstance().getTime();
        this.discountType = discountType;
        this.discountAmount = discountAmount;
        this.version = LATEST_VERSION;
    }

    public Key getId() {
        return id;
    }

    public String getIdString() {
        return KeyFactory.keyToString(id);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code.toUpperCase();
    }

    public Date getCreated() {
        return created;
    }

    public String getCreatedString() {
        if (created != null) {
            return ISO_DATE_FORMATTER.format(created);
        } else {
            return "";
        }
    }

    public void setCreatedString(String createdString) throws ParseException {
        created = ISO_DATE_FORMATTER.parse(createdString);
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public String getExpiresString() {
        if (expires != null) {
            return ISO_DATE_FORMATTER.format(expires);
        } else {
            return "";
        }
    }

    public void setExpiresString(String expiresString) throws ParseException {
        if (expiresString == null || expiresString.isEmpty()) {
            expires = null;
        } else {
            expires = ISO_DATE_FORMATTER.parse(expiresString);
        }
    }

    public Integer getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public void setMinimumOrderValue(Integer minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getVersion() {
        return version;
    }

    public boolean isValid() {
        return expires == null
                || expires.after(Calendar.getInstance().getTime());
    }

    public String getDisplayString() {
        String s = "";
        switch (discountType) {
            case percentage:
                s += discountAmount + "%";
                break;
            case pence:
                s += StringUtils.formatMoney(discountAmount);
                break;
        }
        s += " discount.";
        return s;
    }

    public static Coupon getCouponByCode(String code, PersistenceManager pm) {
        Query query = pm.newQuery(Coupon.class);

        query.setFilter("code == codeParam");
        query.declareParameters("String codeParam");

        @SuppressWarnings("unchecked")
        List<Coupon> coupons = (List<Coupon>) query.execute(code.toUpperCase());

        if (coupons.size() == 1) {
            return coupons.get(0);
        } else {
            if (coupons.size() >= 1) {
                throw new RuntimeException("Expected one coupon, got "
                        + coupons.size());
            } else {
                return null;
            }
        }
    }

    public static boolean isCodeAvailable(String code, PersistenceManager pm) {
        Query query = pm.newQuery(Coupon.class);

        query.setFilter("code == codeParam");
        query.declareParameters("String codeParam");

        @SuppressWarnings("unchecked")
        List<Coupon> coupons = (List<Coupon>) query.execute(code);

        return coupons.isEmpty();
    }

    public static Coupon getCouponByKeyString(String couponKeyString,
            PersistenceManager pm) {
        if (couponKeyString == null) {
            return null;
        }
        try {
            return pm.getObjectById(Coupon.class,
                    KeyFactory.stringToKey(couponKeyString));
        } catch (JDOObjectNotFoundException e) {
            return null;
        }

    }

    public static List<Coupon> getCoupons(PersistenceManager pm) {
        Query query = pm.newQuery(Coupon.class);

        @SuppressWarnings("unchecked")
        List<Coupon> coupons = (List<Coupon>) query.execute();
        return coupons;
    }
}
