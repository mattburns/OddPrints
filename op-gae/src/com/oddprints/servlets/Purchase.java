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

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.KeyFactory;
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
import com.oddprints.PMF;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.State;
import com.oddprints.dao.BasketItem;

@Path("/purchase")
public class Purchase {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response view(@Context HttpServletRequest req)
            throws URISyntaxException {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        Basket basket = Basket.fromSession(req, pm);
        ApiContext apiContext = basket.getEnvironment().getCheckoutAPIContext();

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
        flowSupport.setMerchantCheckoutFlowSupport(merchantflowSupport);
        ShippingMethods sm = new ShippingMethods();
        sm.getFlatRateShippingOrMerchantCalculatedShippingOrPickup();
        FlatRateShipping frs = new FlatRateShipping();
        frs.setName("flat rate");
        Price frsprice = new Price();
        frsprice.setValue(BigDecimal.valueOf((double) basket.getShipping() / 100));
        frsprice.setCurrency("GBP");

        frs.setPrice(frsprice);
        sm.getFlatRateShippingOrMerchantCalculatedShippingOrPickup().add(frs);
        merchantflowSupport.setShippingMethods(sm);

        cart.setCheckoutFlowSupport(flowSupport);

        MerchantData md = new MerchantData();
        md.addString(KeyFactory.keyToString(basket.getId()));
        cart.getShoppingCart().setMerchantPrivateData(md);

        CheckoutRedirect redirect = apiContext.cartPoster().postCart(cart);

        String googleOrderNumber = redirect.getSerialNumber();
        persistOrder(basket, googleOrderNumber, pm);
        pm.close();

        return Response.temporaryRedirect(new URI(redirect.getRedirectUrl()))
                .build();
    }

    private class MerchantData extends AnyMultiple {
        private void addString(String s) {
            getContent().add(s);
        }
    }

    private void persistOrder(Basket basket, String googleOrderNumber,
            PersistenceManager pm) {
        basket.setState(State.awaiting_payment);
        pm.makePersistent(basket);
    }
}
