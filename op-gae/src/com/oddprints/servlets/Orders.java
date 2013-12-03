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
import javax.ws.rs.QueryParam;
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
import com.oddprints.dao.Basket.CheckoutSystem;
import com.oddprints.dao.Basket.State;
import com.oddprints.util.EmailSender;
import com.oddprints.util.EmailTemplates;
import com.sun.jersey.api.view.Viewable;

@Path("/orders")
public class Orders {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getAllOrders(@PathParam("state") String state,
            @QueryParam("hidePwinty") boolean hidePwinty) {
        return getOrdersByState(State.payment_received.toString(), hidePwinty);
    }

    @GET
    @Path("/{state}")
    @Produces(MediaType.TEXT_HTML)
    public Viewable getOrdersByState(@PathParam("state") String state,
            @QueryParam("hidePwinty") boolean hidePwinty) {

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
            baskets = Basket.getBasketsByState(State.valueOf(state), pm, 10);
        }

        it.put("states", State.values());
        it.put("currentState", state);

        it.put("orders", baskets);

        it.put("hidePwinty", hidePwinty);

        return new Viewable("/orders", it);

    }

    @GET
    @Path("/{secret}/{key}")
    @Produces(MediaType.TEXT_HTML)
    public Viewable getOrder(@PathParam("secret") String secret,
            @PathParam("key") String key,
            @QueryParam("hidePwinty") boolean hidePwinty) {

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
        it.put("hidePwinty", hidePwinty);

        return new Viewable("/orders", it);
    }

    @GET
    @Path("/submit/{secret}/{key}")
    @Produces(MediaType.TEXT_HTML)
    public Response submitOrderToPwinty(@PathParam("secret") String secret,
            @PathParam("key") String key) {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        Basket basket = Basket.getBasketByKeyString(key, pm);

        if (basket.getState() != State.payment_received) {
            return Response
                    .status(com.sun.jersey.api.client.ClientResponse.Status.PRECONDITION_FAILED)
                    .build();
        }

        UserService userService = UserServiceFactory.getUserService();
        boolean userIsAdmin = userService.isUserLoggedIn()
                && userService.isUserAdmin();

        if (!userIsAdmin) {
            return Response
                    .status(com.sun.jersey.api.client.ClientResponse.Status.UNAUTHORIZED)
                    .build();
        }

        Order pwintyOrder = basket.getPwintyOrder();
        if (pwintyOrder.getSubmissionStatus().isValid()) {
            pwintyOrder.submit();
            basket.setState(State.submitted_to_lab);
        } else {
            // TODO: Ultimately, I want to handle this better, but for now,
            // lets just see what the common problems are (if any).
            // Don't bother changing order state, as it prevents us retrying
            String msg = "**** Error submitting to pwinty: "
                    + basket.getCheckoutSystemOrderNumber();
            EmailSender.INSTANCE.sendToAdmin(msg, msg);
        }
        pm.makePersistent(basket);
        pm.close();

        return Response.ok().build();
    }

    @GET
    @Path("/charge")
    @Produces(MediaType.TEXT_HTML)
    public Response chargeAndShipOrders() {

        int basketsToProcess = 5; // process no more than this to prevent
                                  // timeouts when there are lots of orders

        PersistenceManager pm = PMF.get().getPersistenceManager();
        List<Basket> baskets = Basket.getBasketsByState(State.submitted_to_lab,
                pm, basketsToProcess);
        for (Basket basket : baskets) {
            Order pwintyOrder = basket.getPwintyOrderEL();
            if (pwintyOrder != null
                    && pwintyOrder.getStatus() == Status.Complete) {

                String checkoutSystemOrderNumber = basket
                        .getCheckoutSystemOrderNumber();

                // For google orders, we only charge once the order has
                // shipped
                if (basket.getCheckoutSystem() == CheckoutSystem.google) {
                    try {
                        // charge and ship
                        ApiContext apiContext = basket.getEnvironment()
                                .getGoogleCheckoutAPIContext();
                        apiContext.orderCommands(
                                basket.getCheckoutSystemOrderNumber())
                                .chargeAndShipOrder();
                    } catch (Throwable t) {
                        EmailSender.INSTANCE.sendToAdmin(
                                "basket: " + basket.getUrl() + " error was "
                                        + t.getMessage(),
                                "Problem charging customer");
                    }
                }
                basket.setState(State.dispatched_from_lab);

                String subject = "OddPrints Dispatched Order #"
                        + checkoutSystemOrderNumber;
                String msg = EmailTemplates.shippedOrder(
                        checkoutSystemOrderNumber, basket.getUrl());
                EmailSender.INSTANCE.send(basket.getBuyerEmail(), msg, subject);
            }
        }

        pm.close();
        return Response.ok().build();

    }
}
