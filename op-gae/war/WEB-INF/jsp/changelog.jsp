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
        <h2>v.22 - 2013/03/10</h2>
        <ul>
            <li>Configure PayPal buy app setting.</li>
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
