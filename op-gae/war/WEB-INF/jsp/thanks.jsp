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
    <jsp:param name="titleText" value=" - Thanks" />
    <jsp:param name="descriptionText" value="Checkout complete." />
</jsp:include>
<body>
<div id="fb-root"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_GB/all.js#xfbml=1&appId=154500891318156";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));


// Track ecommerce in Google Analytics...
// see https://developers.google.com/analytics/devguides/collection/analyticsjs/ecommerce
ga('require', 'ecommerce', 'ecommerce.js');

ga('ecommerce:addTransaction', {
    'id': '${it.basket.idString}',
    'revenue': '${it.basket.totalPriceStringNoSymbols}',
    'shipping': '${it.basket.totalShippingPriceStringNoSymbols}'
  });
  
<c:forEach var="basketItem" items="${it.basket.items}" varStatus="basketItemNumber">
  ga('ecommerce:addItem', {
      'id': '${it.basket.idString}',
      'name': '${basketItem.printSize.displayString}',
      'sku': '${basketItem.printSize.displayString}',
      'price': '${basketItem.unitPriceStringNoSymbol}',
      'quantity': '${basketItem.quantity}'
  });
</c:forEach>

ga('ecommerce:send');

</script>

<div data-role="page" id="page-thanks">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
       
        <h2>Thanks!</h2>
        <p>Massive thanks from us at OddPrints.
        You will receive a confirmation email which includes a link to the current status of your order.
        Generally prints arrive within three working days.</p>
        
        <p>We're only a <em>teeny-tiny</em> company so if you'd like to help support the site,
        please share your experience:<p>
        
        <a href="https://twitter.com/share" class="twitter-share-button" data-url="http://www.oddprints.com" data-text="Clever photo printing website..." data-via="OddPrints" data-size="large" data-dnt="true">Tweet</a>
        <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
        <div class="fb-like" data-href="http://www.OddPrints.com" data-send="true" data-width="450" data-show-faces="true"></div>
        
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

</body>
</html>
