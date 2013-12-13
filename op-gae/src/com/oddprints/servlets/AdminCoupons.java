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
package com.oddprints.servlets;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.Maps;
import com.oddprints.PMF;
import com.oddprints.dao.Coupon;
import com.oddprints.dao.Coupon.DiscountType;
import com.sun.jersey.api.view.Viewable;

@Path("/admin/coupons")
public class AdminCoupons {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable get() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        List<Coupon> coupons = Coupon.getCoupons(pm);
        Map<String, Object> it = Maps.newHashMap();

        if (coupons.isEmpty()) {
            // this only applies to brand new database...
            Coupon c = new Coupon(DiscountType.pence, 10);
            pm.makePersistent(c);
        }

        it.put("coupons", coupons);
        return new Viewable("/admin-coupons", it);

    }

    @POST
    @Path("/post")
    @Produces(MediaType.TEXT_HTML)
    public Response post(@FormParam("code") String code,
            @FormParam("couponKeyString") String couponKeyString,
            @FormParam("createdString") String createdString,
            @FormParam("expiresString") String expiresString,
            @FormParam("email") String email,
            @FormParam("discountAmount") int discountAmount,
            @FormParam("updateOrNew") String updateOrNew,
            @FormParam("discountType") DiscountType discountType)
            throws ParseException {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Coupon c;
        if (updateOrNew.equals("new")) {
            c = new Coupon(discountType, discountAmount);
        } else {
            c = Coupon.getCouponByKeyString(couponKeyString, pm);
        }
        if (!code.equals(c.getCode()) && !Coupon.isCodeAvailable(code, pm)) {
            throw new RuntimeException("Coupon code already in use");
        } else {
            c.setCode(code);
        }

        c.setCreatedString(createdString);
        c.setExpiresString(expiresString);
        c.setEmail(email);
        c.setDiscountAmount(discountAmount);
        c.setDiscountType(discountType);

        pm.makePersistent(c);
        pm.close();

        return Response.ok().build();
    }
}
