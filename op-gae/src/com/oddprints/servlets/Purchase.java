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

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.checkout.sdk.commands.ApiContext;
import com.google.checkout.sdk.commands.CartPoster.CheckoutShoppingCartBuilder;
import com.google.checkout.sdk.domain.AnyMultiple;
import com.google.checkout.sdk.domain.CheckoutRedirect;
import com.google.checkout.sdk.domain.CheckoutShoppingCart;
import com.google.checkout.sdk.domain.CheckoutShoppingCart.CheckoutFlowSupport;
import com.google.checkout.sdk.domain.FlatRateShipping;
import com.google.checkout.sdk.domain.FlatRateShipping.Price;
import com.google.checkout.sdk.domain.MerchantCheckoutFlowSupport;
import com.google.checkout.sdk.domain.MerchantCheckoutFlowSupport.ShippingMethods;
import com.google.checkout.sdk.domain.ShippingRestrictions;
import com.google.checkout.sdk.domain.ShippingRestrictions.AllowedAreas;
import com.google.checkout.sdk.domain.WorldArea;
import com.google.common.collect.Maps;
import com.oddprints.PMF;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.State;
import com.oddprints.dao.BasketItem;
import com.oddprints.util.EmailSender;
import com.sun.jersey.api.view.Viewable;

@Path("/purchase")
public class Purchase {

    @GET
    @Path("/google")
    @Produces(MediaType.TEXT_HTML)
    public Response google(@Context HttpServletRequest req,
            @QueryParam("analyticsData") String analyticsData)
            throws URISyntaxException {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        Basket basket = Basket.fromSession(req, pm);
        ApiContext apiContext = basket.getEnvironment()
                .getGoogleCheckoutAPIContext();

        CheckoutShoppingCartBuilder cartBuilder = apiContext.cartPoster()
                .makeCart();

        for (BasketItem item : basket.getItems()) {
            if (item.getQuantity() > 0) {
                cartBuilder.addItem("Print", item.toString(), (double) item
                        .getPrintSize().getPrice() / 100, item.getQuantity());
            }
        }

        CheckoutShoppingCart cart = cartBuilder.build();

        CheckoutFlowSupport flowSupport = new CheckoutFlowSupport();
        MerchantCheckoutFlowSupport merchantflowSupport = new MerchantCheckoutFlowSupport();
        merchantflowSupport
                .setContinueShoppingUrl("http://www.oddprints.com/thanks");
        flowSupport.setMerchantCheckoutFlowSupport(merchantflowSupport);
        ShippingMethods sm = new ShippingMethods();
        sm.getFlatRateShippingOrMerchantCalculatedShippingOrPickup();
        FlatRateShipping frs = new FlatRateShipping();
        frs.setName("flat rate");

        ShippingRestrictions shippingRestrictions = new ShippingRestrictions();
        AllowedAreas allowedAreas = new AllowedAreas();
        allowedAreas.getUsStateAreaOrUsZipAreaOrUsCountryArea().add(
                new WorldArea());
        shippingRestrictions.setAllowedAreas(allowedAreas);
        frs.setShippingRestrictions(shippingRestrictions);

        Price frsprice = new Price();
        frsprice.setValue(BigDecimal.valueOf((double) basket.getShipping() / 100));
        frsprice.setCurrency("GBP");

        frs.setPrice(frsprice);
        sm.getFlatRateShippingOrMerchantCalculatedShippingOrPickup().add(frs);
        merchantflowSupport.setShippingMethods(sm);
        
        merchantflowSupport.setAnalyticsData(analyticsData);

        cart.setCheckoutFlowSupport(flowSupport);

        MerchantData md = new MerchantData();
        md.addString(basket.getIdString());
        cart.getShoppingCart().setMerchantPrivateData(md);

        CheckoutRedirect redirect = apiContext.cartPoster().postCart(cart);

        updateBasketState(basket, pm);
        pm.close();

        return Response.temporaryRedirect(new URI(redirect.getRedirectUrl()))
                .build();
    }

    @GET
    @Path("/paypal")
    @Produces(MediaType.TEXT_HTML)
    public Viewable paypal(@Context HttpServletRequest req)
            throws URISyntaxException {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        Basket basket = Basket.fromSession(req, pm);
        updateBasketState(basket, pm);
        // close and re-fetch to persist updated state
        pm.close();
        pm = PMF.get().getPersistenceManager();
        basket = Basket.fromSession(req, pm);

        EmailSender.INSTANCE.sendToAdmin(
                "paypal checkout -" + basket.getIdString(), "paypal checkout -"
                        + basket.getIdString());

        Map<String, Object> it = Maps.newHashMap();
        it.put("basket", basket);

        return new Viewable("/paypalcheckout", it);
    }

    private class MerchantData extends AnyMultiple {
        private void addString(String s) {
            getContent().add(s);
        }
    }

    private void updateBasketState(Basket basket, PersistenceManager pm) {
        basket.setState(State.awaiting_payment);
        pm.makePersistent(basket);
    }
}
