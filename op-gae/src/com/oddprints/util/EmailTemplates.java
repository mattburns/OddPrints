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
package com.oddprints.util;

import java.net.URL;

public class EmailTemplates {

    public static String newOrder(String googleOrderNumber, URL url) {
        return "<font face=\"arial, helvetica, sans-serif\"><h2>Thank You</h2> "
                + "<p>We have recieved your order and are rushing around to get it to you as soon as possible."
                + " You can see how we're getting on by checking the <a href=\""
                + url.toExternalForm()
                + "\">Order Status (#"
                + googleOrderNumber
                + ")</a></p>"
                + "<p>Big thanks from us and if you have any questions, just reply to this email.<p>"

                + "<p>-Matt</p>" + "</font>";
    }

    public static String orderReadyToSubmit(URL url) {
        return "<font face=\"arial, helvetica, sans-serif\"><h2>Admin action</h2> "
                + "<p>Payment has been recieved, check it is ok, "
                + "then submit to Pwinty from the <a href=\""
                + url.toExternalForm() + "\">Order Page</a>.</p>"

                + "<p>-Matt</p>" + "</font>";
    }

    public static String shippedOrder(String googleOrderNumber, URL url) {
        return "<font face=\"arial, helvetica, sans-serif\"><h2>Order Shipped!</h2> "
                + "<p>Just a little email to let you know <a href=\""
                + url.toExternalForm()
                + "\">your order</a> has shipped. You will be enjoying your OddPrints in no time.</p>"
                + "<p>Big thanks from us and if you have any questions, just reply to this email.<p>"

                + "<p>-Matt</p>" + "</font>";
    }

    public static String footer() {
        return ""
                + "<font style=\"background-color:transparent\" face=\"arial, helvetica, sans-serif\">"
                + "<div><br />"
                + "<b>"
                + "<font style=\"color: #5f9cc5; font-size:large\">Odd</font>"
                + "<font style=\"color: #396b9e; font-size:large\">Prints</font>"
                + "</b>"
                + "&nbsp;&nbsp;"
                + "<b><i><font style=\"color:#666666; font-size:small\">easy printing for tricky frames</font></i></b>"
                + "</div>"

                + "<div>"
                + "<font style=\"color:#999999; font-size:11px\">"
                + "<a href=\"http://www.oddprints.com\" style=\"color:#999999\" target=\"_blank\">"
                + "www.oddprints.com"
                + "</a>"
                + "&nbsp;|&nbsp;"
                + "Follow us on Twitter <a href=\"https://twitter.com/#!/oddprints\" target=\"_blank\">@oddprints</a>"
                + "</font>"
                + "</div>"

                + "<div><br />"
                + "<span style=\"color:#999999; font-size:11px;\">&copy;&nbsp;Matt Burns Ltd, Registered in England and Wales No. 07734891</span>"
                + "</div>" + "</font>";
    }
}
