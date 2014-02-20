<%--
Copyright 2011 Matt Burns

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>

<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:og="http://ogp.me/ns#"
      xmlns:fb="http://www.facebook.com/2008/fbml"
      xmlns:c="http://java.sun.com/jsp/jstl/core">
      
<jsp:include page="/WEB-INF/jsp/parts/html-head.jsp">
    <jsp:param name="titleText" value=" - Change log" />
    <jsp:param name="descriptionText" value="History of site improvements." />
</jsp:include>
<body>

<div data-role="page" id="page-changelog">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <h2>v.45 - 2014/02/19</h2>
        <ul>
            <li>Trim blank lines from delivery address.</li>
            <li>Ask for referrals in email.</li>
        </ul>
        <h2>v.44 - 2014/02/12</h2>
        <ul>
            <li>Show delivery address and delivery time estimate in confirmation email.</li>
            <li>Save settings between photo upload.</li>
        </ul>
        <h2>v.43 - 2014/02/11</h2>
        <ul>
            <li>Allow users to update addresses.</li>
            <li>Separate settings for sandbox and live pwinty api keys.</li>
        </ul>
        <h2>v.42 - 2014/01/15</h2>
        <ul>
            <li>Pwinty API v2.</li>
        </ul>
        <h2>v.41 - 2014/01/10</h2>
        <ul>
            <li>New universal Google analytics.</li>
        </ul>
        <h2>v.40 - 2014/01/05</h2>
        <ul>
            <li>Hide xmas shipping.</li>
        </ul>
        <h2>v.39 - 2013/12/13</h2>
        <ul>
            <li>Added coupons.</li>
        </ul>
        <h2>v.38 - 2013/11/29</h2>
        <ul>
            <li>Added twitter box.</li>
        </ul>
        <h2>v.37 - 2013/11/28</h2>
        <ul>
            <li>Removed Google checkout.</li>
            <li>Added facebook like box.</li>
        </ul>
        <h2>v.36 - 2013/11/11</h2>
        <ul>
            <li>Added competition to email templates.</li>
        </ul>
        <h2>v.35 - 2013/09/18</h2>
        <ul>
            <li>Added google satisfaction survey.</li>
            <li>Updated Pwinty SDK jar to 1.6.</li>
            <li>Killed off all references to the deprecated Sticker feature.<li>
        </ul>
        <h2>v.33 - 2013/09/05</h2>
        <ul>
            <li>Fix bugs on safari on iOS.</li>
            <li>Added pfb article to homepage.</li>
        </ul>
        <h2>v.32 - 2013/09/04</h2>
        <ul>
            <li>Simplified homepage text.</li>
            <li>Added promo code mechanism to admin settings.</li>
        </ul>
        <h2>v.31 - 2013/08/28</h2>
        <ul>
            <li>Disable mouse scroll wheel from zooming.</li>
        </ul>
        <h2>v.30 - 2013/08/14</h2>
        <ul>
            <li>Remove line breaks from PayPal addresses.</li>
        </ul>
        <h2>v.29 - 2013/07/31</h2>
        <ul>
            <li>Send order confirmation to PayPal orders.</li>
        </ul>
        <h2>v.28 - 2013/06/11</h2>
        <ul>
            <li>Fixed iOS6 image squash bug.</li>
            <li>Added credits.</li>
        </ul>
        <h2>v.27 - 2013/06/05</h2>
        <ul>
            <li>Add promo page for PFB.</li>
        </ul>
        <h2>v.26 - 2013/05/29</h2>
        <ul>
            <li>Re-add guideline canvas between paints (fixes IE10 bug).</li>
        </ul>
        <h2>v.25 - 2013/05/24</h2>
        <ul>
            <li>Try standard servlet to hand IPNs.</li>
        </ul>
        <h2>v.24 - 2013/05/23</h2>
        <ul>
            <li>Don't sort IPN keys.</li>
            <li>Upgrade to GAE 1.8.0 using jarjar jars.</li>
        </ul>   
        <h2>v.23 - 2013/05/22</h2>
        <ul>
            <li>Handle PayPal notifications.</li>
        </ul>
        <h2>v.22 - 2013/03/10</h2>
        <ul>
            <li>Configure PayPal by query param.</li>
            <li>Hide broken sticker link.</li>
            <li>If no postcode given, just send space.</li>
        </ul>
        <h2>v.21 - 2013/03/01</h2>
        <ul>
            <li>Fixed purchase link bug in IE.</li>
        </ul>
        <h2>v.20 - 2013/02/14</h2>
        <ul>
            <li>Replace image logo with @fontface. Looks better on good browsers and worse on old ones.</li>
            <li>Added tracking code for eCommerce feature of google analytics.</li>
        </ul>
        <h2>v.19 - 2013/02/14</h2>
        <ul>
            <li>Prevent ajax-y linking by default.</li>
        </ul>
        <h2>v.18 - 2013/02/14</h2>
        <ul>
            <li>Removed MacRoman file encoding.</li>
        </ul>
        <h2>v.17 - 2013/02/14</h2>
        <ul>
            <li>Remove confusing feature of cursors controlling panning.</li>
        </ul>
        <h2>v.16 - 2013/01/22</h2>
        <ul>
            <li>Continue to send dispatched email even if charging payment threw an error (will email admin).</li>
        </ul>
        <h2>v.15 - 2013/01/10</h2>
        <ul>
            <li>Manually process PayPal payments (temp workaraound as part of live paypal testing).</li>
        </ul>
        <h2>v.14 - 2012/12/14</h2>
        <ul>
            <li>Replace frame size sliders with simple textboxes.</li>
        </ul>
        <h2>v.13 - 2012/12/12</h2>
        <ul>
            <li>Specific page for <a href="/panoramic">panoramics</a> and <a href="/custom">custom</a> prints.</li>            
            <li>Set panoramic price to £3.99.</li>
            <li>Restored missing title on turbo edit page.</li>
        </ul>
        <h2>v.12 - 2012/12/07</h2>
        <ul>
            <li>Made the order button more prominent.</li>
            <li>Added thanks page.</li>
        </ul>
        <h2>v.11 - 2012/11/23</h2>
        <ul>
            <li>Users can choose a custom sticker for the envelope.</li>
        </ul>
        <h2>v.10 - 2012/11/22</h2>
        <ul>
            <li>Fixed bug with margin on UK passport photos in basic mode.</li>
        </ul>
        <h2>v.9 - 2012/11/22</h2>
        <ul>
            <li>Fixed bug that sometimes corrupted images > 1MB in basic bode.</li>
            <li>Added panoramic print support.</li>
            <li>Configurable background color.</li>
            <li>Configurable margin in Tile mode.</li>
        </ul>
        
        <h2>v.8 - 2012/10/15</h2>
        <ul>
            <li>Image panning and zooming.</li>
        </ul>

        <h2>v.7 - 2012/10/09</h2>
        <ul>
            <li>Redesigned homepage.</li>
            <li>Reshuffled help page and added shipping times.</li>
        </ul>
        
        <h2>v.6 - 2012/10/05</h2>
        <ul>
            <li>Ensure basket is updated to submitted state after submitting.</li>
        </ul>
        
        <h2>v.5 - 2012/08/22</h2>
        <ul>
            <li>Allow payments to be made with PayPal.</li>
        </ul>

        <h2>v.4 - 2012/08/13</h2>
        <ul>
            <li>Don't auto-submit orders to Pwinty.</li>
        </ul>

        <h2>v.3 - 2012/07/30</h2>
        <ul>
            <li>Added presets for a few common passport sizes.</li>
        </ul>

        <h2>v.2 - 2012/06/26</h2>
        <ul>
            <li>Added a server-side implementation to support more browsers.</li>
        </ul>

        <h2>v.1 - 2012/06/12</h2>
        <ul>
            <li>Initial version.</li>
        </ul>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

</body>
</html>
