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

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.checkout.sdk.commands.ApiContext;
import com.oddprints.Environment;
import com.oddprints.PMF;
import com.oddprints.checkout.Address;
import com.oddprints.checkout.CheckoutNotificationHandler;
import com.oddprints.checkout.GoogleCheckoutNotificationHandler;
import com.oddprints.checkout.ManualCheckoutNotificationHandler;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.CheckoutSystem;
import com.oddprints.dao.Basket.State;
import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * Currently only handles Google checkout notifications, PayPal IPNs are handled
 * in the oldstyle servlet
 * 
 */
@Path("/checkoutnotification")
public class CheckoutNotification {

    @POST
    @Path("/{environment}")
    public Response handleGoogleNotification(
            @PathParam("environment") String environment,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response) {
        ApiContext apiContext = Environment.valueOf(environment.toUpperCase())
                .getGoogleCheckoutAPIContext();

        apiContext.handleNotification(new GoogleCheckoutNotificationHandler(
                request, response, new CheckoutNotificationHandler()));
        return Response.ok().build();
    }

    @GET
    @Path("/manual/{environment}/{checkoutSystem}/{checkoutSystemOrderNumber}/{basketKeyString}")
    public Response handleManualNotification(
            @PathParam("environment") String environment,
            @PathParam("checkoutSystem") String checkoutSystemString,
            @PathParam("checkoutSystemOrderNumber") String checkoutSystemOrderNumber,
            @PathParam("basketKeyString") String basketKeyString,
            @QueryParam("buyerEmail") String buyerEmail,
            @QueryParam("addressName") String addressName,
            @QueryParam("addressStreet1") String addressStreet1,
            @QueryParam("addressStreet2") String addressStreet2,
            @QueryParam("addressCity") String addressCity,
            @QueryParam("addressState") String addressState,
            @QueryParam("addressZip") String addressZip,
            @QueryParam("addressCountry") String addressCountry,

            @Context HttpServletRequest request,
            @Context HttpServletResponse response) throws IOException {

        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn() || !userService.isUserAdmin()) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        PersistenceManager pm = PMF.get().getPersistenceManager();

        Basket basket = Basket.getBasketByKeyString(basketKeyString, pm);
        basket.setState(State.awaiting_payment);
        pm.makePersistent(basket);
        pm.close();

        CheckoutNotificationHandler handler = new CheckoutNotificationHandler();
        ManualCheckoutNotificationHandler manualHandler = new ManualCheckoutNotificationHandler(
                handler);

        CheckoutSystem checkoutSystem = CheckoutSystem
                .valueOf(checkoutSystemString);
        Address address = new Address();

        address.setRecipientName(addressName);
        address.setAddress1(addressStreet1);
        address.setAddress2(addressStreet2);
        address.setTownOrCity(addressCity);
        address.setStateOrCounty(addressState);
        address.setPostalOrZipCode(addressZip);
        address.setCountry(addressCountry);

        manualHandler
                .manuallyAuthorizeOrder(checkoutSystem,
                        checkoutSystemOrderNumber, basketKeyString, buyerEmail,
                        address);
        return Response.ok().build();
    }
}
