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

import java.net.URISyntaxException;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.common.collect.Maps;
import com.oddprints.PMF;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.State;
import com.oddprints.util.EmailSender;
import com.sun.jersey.api.view.Viewable;

@Path("/purchase")
public class Purchase {

    // @POST
    // @Path("/stripe")
    // @Produces(MediaType.TEXT_HTML)
    // public Response stripe(@Context HttpServletRequest req,
    // @FormParam("token") String token) throws URISyntaxException,
    // AuthenticationException, InvalidRequestException,
    // APIConnectionException, CardException, APIException {
    //
    // Stripe.apiKey = "sk_test_8MsGxSB6d8Axlq2OuTajMGEO";
    //
    // Map<String, Object> params = new HashMap<String, Object>();
    // Charge charge = Charge.create(params);
    //
    // return Response.ok("ok").build();
    // }

    @GET
    @Path("/paypal")
    @Produces(MediaType.TEXT_HTML)
    public Viewable paypal(@Context HttpServletRequest req)
            throws URISyntaxException {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        Basket basket = Basket.fromSession(req, pm);

        updateBasketState(basket, pm);
        pm.close();
        pm = PMF.get().getPersistenceManager();
        String basketKeyString = (String) req.getSession().getAttribute(
                "basketKeyString");

        basket = Basket.getBasketByKeyString(basketKeyString, pm);

        EmailSender.INSTANCE
                .sendToAdmin(
                        "PayPal purchase attempted for order: www.oddprints.com/orders/"
                                + basket.getSecret() + "/"
                                + basket.getIdString() + ".",
                        "paypal purchase initiated -" + basket.getIdString());

        Map<String, Object> it = Maps.newHashMap();
        it.put("basket", basket);

        return new Viewable("/paypalcheckout", it);
    }

    private void updateBasketState(Basket basket, PersistenceManager pm) {
        basket.setState(State.awaiting_payment);
        pm.makePersistent(basket);
    }
}
