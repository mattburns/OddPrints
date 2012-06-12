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

import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import uk.co.mattburns.pwinty.Order;
import uk.co.mattburns.pwinty.Order.Status;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.checkout.sdk.commands.ApiContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oddprints.PMF;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.State;
import com.oddprints.util.EmailSender;
import com.oddprints.util.EmailTemplates;
import com.sun.jersey.api.view.Viewable;

@Path("/orders")
public class Orders {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getAllOrders(@PathParam("state") String state) {
        return getOrdersByState(State.payment_received.toString());
    }

    @GET
    @Path("/{state}")
    @Produces(MediaType.TEXT_HTML)
    public Viewable getOrdersByState(@PathParam("state") String state) {

        String thisURL = "/orders";

        Map<String, Object> it = Maps.newHashMap();
        PersistenceManager pm = PMF.get().getPersistenceManager();
        List<Basket> baskets = Lists.newArrayList();

        UserService userService = UserServiceFactory.getUserService();
        boolean userIsAdmin = userService.isUserLoggedIn()
                && userService.isUserAdmin();

        it.put("userIsAdmin", userIsAdmin);
        if (userService.isUserLoggedIn()) {
            it.put("logoutUrl", userService.createLogoutURL(thisURL));
        } else {
            it.put("loginUrl", userService.createLoginURL(thisURL));
        }

        if (userIsAdmin) {
            baskets = Basket.getBasketsByState(State.valueOf(state), pm);
        }

        it.put("states", State.values());
        it.put("currentState", state);

        it.put("orders", baskets);

        return new Viewable("/orders", it);

    }

    @GET
    @Path("/{secret}/{key}")
    @Produces(MediaType.TEXT_HTML)
    public Viewable getOrder(@PathParam("secret") String secret,
            @PathParam("key") String key) {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        Basket basket = Basket.getBasketByKeyString(key, pm);

        Map<String, Object> it = Maps.newHashMap();
        UserService userService = UserServiceFactory.getUserService();
        it.put("userIsAdmin",
                userService.isUserLoggedIn() && userService.isUserAdmin());

        if (basket.getSecret().equals(secret)) {
            List<Basket> baskets = Lists.newArrayList(basket);
            it.put("orders", baskets);
        }

        return new Viewable("/orders", it);
    }

    @GET
    @Path("/charge")
    @Produces(MediaType.TEXT_HTML)
    public Response chargeAndShipOrders() {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        List<Basket> baskets = Basket.getBasketsByState(State.submitted_to_lab,
                pm);
        for (Basket basket : baskets) {
            Order pwintyOrder = basket.getPwintyOrderEL();
            if (pwintyOrder != null
                    && pwintyOrder.getStatus() == Status.Complete) {
                // charge and ship
                ApiContext apiContext = basket.getEnvironment()
                        .getCheckoutAPIContext();
                apiContext.orderCommands(basket.getGoogleOrderNumber())
                        .chargeAndShipOrder();
                basket.setState(State.dispatched_from_lab);
                String googleOrderNumber = basket.getGoogleOrderNumber();

                String subject = "OddPrints Dispatched Order #"
                        + googleOrderNumber;
                String msg = EmailTemplates.shippedOrder(googleOrderNumber,
                        basket.getUrl());
                EmailSender.INSTANCE.send(basket.getGoogleOrderSummary()
                        .getBuyerShippingAddress().getEmail(), msg, subject);
            }
        }

        pm.close();
        return Response.ok().build();

    }

}
