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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Maps;
import com.oddprints.Environment;
import com.oddprints.PMF;
import com.oddprints.dao.Basket;
import com.sun.jersey.api.view.Viewable;

@Path("/checkout")
public class Checkout {

    private boolean basicMode(HttpServletRequest req) {
        Object basicMode = req.getSession().getAttribute("basicMode");
        return basicMode != null && (Boolean) basicMode;
    }

    private String editUrl(HttpServletRequest req) {
        if (basicMode(req)) {
            return "/upload/basic";
        } else {
            return "/edit";
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable viewCheckout(@Context HttpServletRequest req) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        Basket basket = Basket.fromSession(req, pm);

        Map<String, Object> it = Maps.newHashMap();
        it.put("basket", basket);
        it.put("editurl", editUrl(req));
        UserService userService = UserServiceFactory.getUserService();
        it.put("userIsAdmin",
                userService.isUserLoggedIn() && userService.isUserAdmin());

        if (basket != null) {
            it.put("merchantId", basket.getEnvironment()
                    .getGoogleCheckoutAPIContext().getMerchantId());
        }

        it.put("paypalEnabled", true);

        return new Viewable("/checkout", it);
    }

    @GET
    @Path("/delete/{basketItem}")
    @Produces(MediaType.TEXT_HTML)
    public Response delete(@PathParam("basketItem") int basketItemToDelete,
            @Context HttpServletRequest req) throws URISyntaxException {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Basket basket = Basket.fromSession(req, pm);
        basket.delete(basketItemToDelete);
        pm.close();
        return Response.temporaryRedirect(new URI("/checkout")).build();
    }

    @GET
    @Path("/environment/{environment}")
    @Produces(MediaType.TEXT_HTML)
    public Response toggleEnvironment(
            @PathParam("environment") String environment,
            @Context HttpServletRequest req) throws URISyntaxException {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Basket basket = Basket.fromSession(req, pm);
        basket.setEnvironment(Environment.valueOf(environment.toUpperCase()));
        pm.close();
        return Response.temporaryRedirect(new URI("/checkout")).build();
    }

    @GET
    @Path("/update/{basketItem}/{quantity}")
    @Produces(MediaType.TEXT_HTML)
    public Response update(@PathParam("basketItem") int basketItemToUpdate,
            @PathParam("quantity") int quantity, @Context HttpServletRequest req)
            throws URISyntaxException {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Basket basket = Basket.fromSession(req, pm);

        basket.updateQuantity(basketItemToUpdate, quantity);
        pm.close();

        return Response.temporaryRedirect(new URI("/checkout")).build();
    }

}
