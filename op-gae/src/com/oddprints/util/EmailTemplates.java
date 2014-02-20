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
import java.util.Date;

import uk.co.mattburns.pwinty.v2.CountryCode;

import com.oddprints.checkout.Address;
import com.oddprints.dao.Basket;
import com.oddprints.dao.Basket.CheckoutSystem;

public class EmailTemplates {

    public static String paymentRecieved(String checkoutSystemOrderNumber,
            URL url, Address address) {
        return "<font face=\"arial, helvetica, sans-serif\"><h2>Thank You</h2> "
                + "<p>We have received the payment for your order and are rushing around to get it to you as soon as possible."
                + " You can see how we're getting on by checking the "
                + "<a href=\""
                + url.toExternalForm()
                + "\">Order Status (#"
                + checkoutSystemOrderNumber
                + ")</a></p>"

                + "<p>Estimated delivery date:</p>"
                + "<blockquote>"
                + StringUtils.estimatedDeliveryDate(new Date(),
                        CountryCode.valueOf(address.getCountryCode()))
                + "</blockquote>"

                + "<p>Your order will be shipped to:</p>"
                + "<blockquote>"
                + address.toEmailHtml()
                + "</blockquote>"
                + "<p>If this is not correct, you have "
                + Basket.SUBMIT_DELAY
                + " minutes to <a href=\""
                + url.toExternalForm()
                + "\">change your address</a>.</p>"

                + "<p>Big thanks from us and if you have any questions, just reply to this email.<p>"

                + "<p>-Matt</p>" + "</font>";
    }

    public static String orderReadyToSubmit(URL url, CheckoutSystem checkout) {
        String next = "<strong>ERROR</strong>";
        switch (checkout) {
            case google:
                next = "<p>Payment method has been authorised, check the order looks ok, "
                        + "then submit to Pwinty from the <a href=\""
                        + url.toExternalForm() + "\">Order Page</a>.</p>";
                break;
            case paypal:
                next = "<p>Payment has been taken, just submit to Pwinty from the <a href=\""
                        + url.toExternalForm() + "\">Order Page</a>.</p>";
                break;
        }
        return "<font face=\"arial, helvetica, sans-serif\"><h2>Admin action</h2> "
                + "<h3>"
                + checkout
                + " payment</h3>"
                + next
                + "<p>-Matt</p>"
                + "</font>";
    }

    public static String shippedOrder(String checkoutSystemOrderNumber, URL url) {
        return "<font face=\"arial, helvetica, sans-serif\"><h2>Order Shipped!</h2> "
                + "<p>Just a little email to let you know <a href=\""
                + url.toExternalForm()
                + "\">your order</a> has shipped. You will be enjoying your OddPrints in no time.</p>"
                + "<p>Big thanks from us and if you have any questions, just reply to this email.<p>"
                + "<p>-Matt</p>" + "</font>" + referral();
    }

    public static String addressUpdated(Basket basket, Address address) {
        return "<font face=\"arial, helvetica, sans-serif\"><h2>Address Updated</h2> "
                + "<p>You have entered a new address for <a href=\""
                + basket.getUrl().toExternalForm()
                + "\">your order</a>.</p>"
                + "<blockquote>"
                + address.toEmailHtml()
                + "</blockquote>"
                + "<p>Thanks again, and if you have any questions, just reply to this email.<p>"
                + "<p>-Matt</p>" + "</font>";
    }

    public static String referral() {
        return "<font face=\"arial, helvetica, sans-serif\"><h3>Get money back!</h3> "
                + "<p>We're only a tiny company and working really hard to give the best customer service "
                + "<i>on the planet</i>. However, because we're so young, no-one's heard of us :(</p>"
                + "<p>We'd love you to share your experience about OddPrints online through Twitter or Facebook (or something else)."
                + " It doesn't have to be positive, we just want it to be honest. Once you've done that, just reply "
                + "to this email with a link to it "
                + "and we'll refund £1 from your order. &lt;3</p>" + "</font>";
    }

    public static String competition() {
        return "<font face=\"arial, helvetica, sans-serif\"><h3>Win a £20 OddPrints voucher in November</h3> "
                + "<p>We’d love to see a photo of any odd sized frames"
                + " you’ve been able to fill using OddPrints."
                + " Post them to our <a href=\"https://www.facebook.com/OddPrints\">facebook page</a>"
                + " and the one with the most likes in November"
                + " gets a £20 OddPrints voucher!<p>"

                + "<p>See: https://www.facebook.com/OddPrints</p>" + "</font>";
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
