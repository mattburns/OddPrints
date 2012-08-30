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
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.checkout.sdk.commands.ApiContext;
import com.oddprints.Environment;
import com.oddprints.checkout.CheckoutNotificationHandler;
import com.oddprints.checkout.GoogleCheckoutNotificationHandler;
import com.oddprints.checkout.PaypalCheckoutNotificationHandler;
import com.oddprints.util.EmailSender;

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

    @SuppressWarnings("unchecked")
    @POST
    @Path("/paypal/{environment}")
    public Response handlePaypalNotification(
            @PathParam("environment") String environment,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response) throws IOException {

        // The following ugly slug of java was taken from paypal sample code:
        // https://www.x.com/developers/PayPal/documentation-tools/code-sample/216623

        @SuppressWarnings("rawtypes")
        Enumeration en = request.getParameterNames();
        String str = "cmd=_notify-validate";
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            String paramValue = request.getParameter(paramName);
            try {
                str = str + "&" + paramName + "="
                        + URLEncoder.encode(paramValue, "UTF-8");
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
        String paymentStatus = request.getParameter("payment_status");
        String paymentAmount = request.getParameter("mc_gross");
        String paymentCurrency = request.getParameter("mc_currency");
        String txnId = request.getParameter("txn_id");
        String receiverEmail = request.getParameter("receiver_email");
        String payerEmail = request.getParameter("payer_email");

        // send email to admin
        String subject = "Notification recieved from PayPal " + paymentStatus
                + " " + txnId;
        String msg = "Payment of " + paymentCurrency + paymentAmount + ".<br/>"
                + "receiverEmail " + receiverEmail + ".<br/>" + "payerEmail "
                + payerEmail + ".<br/>" + "Parameters from paypal were: " + str;
        EmailSender.INSTANCE.sendToAdmin(msg, subject);

        // check notification validation
        if (res.equals("VERIFIED")) {
            CheckoutNotificationHandler handler = new CheckoutNotificationHandler();
            PaypalCheckoutNotificationHandler paypalHandler = new PaypalCheckoutNotificationHandler(
                    handler);
            if (paymentStatus.equals("Completed")) {
                paypalHandler.onAuthorizationAmountNotification(request
                        .getParameterMap());
            } else if (paymentStatus.equals("Pending")) {
                paypalHandler.onNewOrderNotification(request.getParameterMap());
            }
            return Response.ok().build();
            // check that paymentStatus=Completed
            // check that txnId has not been previously processed
            // check that receiverEmail is your Primary PayPal email
            // check that paymentAmount/paymentCurrency are correct
            // process payment
        } else {
            EmailSender.INSTANCE.sendToAdmin("response was " + res,
                    "failed to validate paypal IPN");
            // error
            return Response.serverError().build();
        }

    }
}
