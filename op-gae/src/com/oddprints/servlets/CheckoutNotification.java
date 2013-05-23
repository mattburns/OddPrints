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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
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
import com.oddprints.checkout.PaypalCheckoutNotificationHandler;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.CheckoutSystem;
import com.oddprints.dao.Basket.State;
import com.oddprints.util.EmailSender;
import com.sun.jersey.api.client.ClientResponse.Status;

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

    @POST
    @Path("/paypal/{environment}")
    @Consumes("application/x-www-form-urlencoded")
    public Response handlePaypalNotification(
            MultivaluedMap<String, String> form,
            @PathParam("environment") String environment) throws IOException {

        // The following ugly slug of java was initially taken from paypal
        // sample code:
        // https://www.x.com/developers/PayPal/documentation-tools/code-sample/216623

        String str = "cmd=_notify-validate";

        Map<String, String> formParams = new HashMap<String, String>();
        for (Map.Entry<String, List<String>> m : form.entrySet()) {
            if (m.getValue().size() != 1) {
                EmailSender.INSTANCE.sendToAdmin("key: " + m.getKey()
                        + " values: " + m.getValue(), "IPN >1");
            } else {
                formParams.put(m.getKey(), m.getValue().get(0));
            }
        }

        SortedSet<String> sortedKeys = new TreeSet<String>(formParams.keySet());
        for (String key : sortedKeys) {
            try {
                str = str + "&" + key + "="
                        + URLEncoder.encode(formParams.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        // post back to PayPal system to validate
        URL u = null;

        try {
            if (environment.equals(Environment.LIVE)) {
                u = new URL("https://www.paypal.com/cgi-bin/webscr");
            } else {
                u = new URL("https://www.sandbox.paypal.com/cgi-bin/webscr");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        HttpURLConnection uc = (HttpURLConnection) u.openConnection();
        uc.setDoOutput(true);
        uc.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        uc.setRequestProperty("Host", "www.paypal.com");
        PrintWriter pw = new PrintWriter(uc.getOutputStream());
        pw.println(str);
        pw.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                uc.getInputStream()));
        String res = in.readLine();
        in.close();

        // assign posted variables to local variables
        String paymentStatus = formParams.get("payment_status");

        // check notification validation
        if (res.equals("VERIFIED")) {
            String nextAction = "<h2>Next Action:</h2>";

            CheckoutNotificationHandler handler = new CheckoutNotificationHandler();
            PaypalCheckoutNotificationHandler paypalHandler = new PaypalCheckoutNotificationHandler(
                    handler);

            String basketKeyString = formParams.get("custom");
            PersistenceManager pm = PMF.get().getPersistenceManager();
            URL url = Basket.getBasketByKeyString(basketKeyString, pm).getUrl();

            if (paymentStatus.equals("Completed")) {
                nextAction += "Submit <a href='" + url
                        + "'>the order</a> to Pwinty";
                paypalHandler.onAuthorizationAmountNotification(formParams);
            } else if (paymentStatus.equals("Pending")) {

                nextAction += "<p>Check if <a href='"
                        + url
                        + "'>the order</a> is ok, if so, charge the amount in PayPal</p>";
                paypalHandler.onNewOrderNotification(formParams);
            } else {
                nextAction += "Unknown";
            }

            EmailSender.INSTANCE
                    .sendToAdmin(
                            nextAction + "<h2>Received:</h2>" + "<p>"
                                    + formParams + "</p>" + "<h2>Sent:</h2>"
                                    + str + "<p>Response was <strong>" + res
                                    + "</strong><p>",
                            "IPN received (payment_status="
                                    + formParams.get("payment_status") + ")");

            return Response.ok().build();
        } else {
            EmailSender.INSTANCE.sendToAdmin("Sent: '" + str
                    + "' but response was " + res,
                    "ERROR : Failed to validate paypal IPN");
            return Response.serverError().build();
        }

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
