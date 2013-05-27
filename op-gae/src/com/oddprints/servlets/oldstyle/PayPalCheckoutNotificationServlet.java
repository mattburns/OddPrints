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
package com.oddprints.servlets.oldstyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oddprints.Environment;
import com.oddprints.PMF;
import com.oddprints.checkout.CheckoutNotificationHandler;
import com.oddprints.checkout.PaypalCheckoutNotificationHandler;
import com.oddprints.dao.Basket;
import com.oddprints.util.EmailSender;

public class PayPalCheckoutNotificationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // The following ugly slug of java was initially taken from paypal
        // sample code:
        // https://www.x.com/developers/PayPal/documentation-tools/code-sample/216623

        Map<String, String> formParams = new HashMap<String, String>();
        StringBuilder strBuffer = new StringBuilder("cmd=_notify-validate");
        String paramName;
        String paramValue;

        Enumeration<?> en = request.getParameterNames();
        while (en.hasMoreElements()) {
            paramName = (String) en.nextElement();
            paramValue = request.getParameter(paramName);
            strBuffer.append("&").append(paramName).append("=")
                    .append(URLEncoder.encode(paramValue, "UTF-8"));
            formParams.put(paramName, paramValue);
        }

        String str = strBuffer.toString();

        // post back to PayPal system to validate
        URL u = null;

        Environment environment = Environment.SANDBOX;
        if (getServletConfig().getInitParameter("env").equals("live")) {
            environment = Environment.LIVE;
        }

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

        } else {
            EmailSender.INSTANCE.sendToAdmin("<h2>Received:</h2>" + "<p>"
                    + formParams + "</p>" + "<h2>Sent:</h2>" + str
                    + "<p>Response was <strong>" + res + "</strong><p>",
                    "ERROR : Failed to validate paypal IPN (payment_status="
                            + formParams.get("payment_status") + ")");

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter()
                    .print("<html><head><title>Oops an error happened!</title></head>");
            response.getWriter().print(
                    "<body>Something bad happened uh-oh!</body>");
            response.getWriter().println("</html>");
        }
    }
}
