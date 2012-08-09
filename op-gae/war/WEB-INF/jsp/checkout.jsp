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
    <jsp:param name="titleText" value=" - Checkout" />
    <jsp:param name="descriptionText" value="View and amend items in your basket before you checkout." />
</jsp:include>
<body>

<div data-role="page" id="page-checkout">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

	<div data-role="content" class="">
	
		<c:choose>
			<c:when test="${empty it.basket.items}">
			    <h1>Basket is empty</h1>
			    <p><a href="${it.editurl}" data-ajax="false">upload something</a></p>
			</c:when>
			<c:otherwise>
				<h1>Checkout</h1>
				
				<table>
					<thead>
					  <tr>
					      <th></th>
					      <th></th>
					      <th class="quantity-input-column">Quantity</th>
					      <th>Price</th>
					  </tr>
					</thead>
					<tbody>
				        <c:forEach var="basketItem" items="${it.basket.items}" varStatus="basketItemNumber">
				            <tr>
					    		<td class="text-align-left">
						    		<a href="/image/basket/${basketItemNumber.index}" data-ajax="false" title="Preview"><img class="checkout-thumb" style="background:url(/image/basket/thumb/${basketItemNumber.index}) no-repeat center;" src="/images/mag.png" alt="" /></a>
					    		</td>
					    		<td>
                                    ${basketItem.printSize.displayString} print / ${basketItem.frameSize} frame
                                    (<a href="/checkout/delete/${basketItemNumber.index}" data-mini="true" data-inline="true" data-ajax="false">remove</a>)
                                </td>
					    		<td>
					    		<input type="number" name="quantity" class="quantity-input" id="quantity-${basketItemNumber.index}" data-mini="true" pattern="[0-9]*" min="1" value="${basketItem.quantity}">
					    		</td>
					    		<td>${basketItem.priceString}</td>
				    		</tr>
				  		</c:forEach>
			  		</tbody>
		  		</table>
		  		<p>
    		  		<a href="${it.editurl}" data-ajax="false" data-role="button" data-theme="b" data-icon="plus" data-inline="true" data-mini="true">Upload more</a>
                </p>
		  		
		        <div>
		            <div class="text-align-right">
    		            <a href="/checkout">update</a>
		                <p class="checkout-subtotal">prints: ${it.basket.printPriceString}</p>
		                <p class="checkout-subtotal">shipping: ${it.basket.shippingPriceString}</p>
		                <p class="checkout-total">Total: ${it.basket.totalPriceString}</p>
		            </div>
		        </div>
		        
		        <c:if test="${it.basket.environment.sandbox}">
			        <div style="background-color: #cfc">
			            <h4>Hey there beta tester!</h4>
			            <p>This is currently set in sandbox mode so no real payments are possible
			            and no photos are ever printed or posted.
			            You can even go right through the checkout with your sandbox buyer account (<a href="https://developers.google.com/checkout/developer/Google_Checkout_Basic_HTML_Sandbox#Create_Sandbox_Accounts" target="_blank"><i>more info</i></a>).</p>
			            <p>Want the real experience? 
			            <a href="/checkout/environment/live" data-ajax="false">Switch this basket live.</a>
			            </p>
		            </div>
		        </c:if>
		
		        <div>
		            <div class="text-align-right">
			  		   <p><a href="/purchase" data-ajax="false"><img src="https://checkout.google.com/buttons/checkout.gif?merchant_id=${it.merchantId}&w=180&h=46&style=trans&variant=text&loc=en_GB" alt="Proceed to Google Checkout"/></a></p>
			  		</div>
		  		</div>
	  		</c:otherwise>
  		</c:choose>
	</div>
	
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>


<script type="text/javascript">

$(document).ready(function() {    
    $( ".quantity-input" ).bind( "change", function(event, ui) {
    	var id = event.target.id;
    	var basketItemId = id.split('-')[1];
    	window.location.href = "/checkout/update/" + basketItemId + "/" + event.target.value;
   	});
});
</script>
</body>
</html>