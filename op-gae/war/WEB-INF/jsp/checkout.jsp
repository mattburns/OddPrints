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
                                    <c:choose>
                                        <c:when test="${basketItem.printSize eq '_2x4'}">
                                            ${basketItem.frameSize}
                                            <c:set var="hasSticker" value="true"/>
                                        </c:when>
                                        <c:otherwise>
                                            ${basketItem.printSize.displayString} print / ${basketItem.frameSize} frame
                                        </c:otherwise>
                                    </c:choose>
                                    (<a href="/checkout/delete/${basketItemNumber.index}" data-ajax="false">remove</a>)
                                </td>
                                <td>
                                    <input type="number" name="quantity" class="quantity-input" id="quantity-${basketItemNumber.index}" data-mini="true" pattern="[0-9]*" min="1" value="${basketItem.quantity}">
                                </td>
                                <td>${basketItem.priceString}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${it.basket.discountPercentage gt 0}">
                            <tr>
                                <td>
                                </td>
                                <td>
                                    <em>${it.basket.discountText} (${it.basket.discountPercentage}%)</em>
                                </td>
                                <td>
                                </td>
                                <td>${it.basket.discountAmountString}</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
                <p>
                    <a href="${it.editurl}" data-ajax="false" data-role="button" data-theme="b" data-icon="plus" data-inline="true" data-mini="true">Upload more</a>
                </p>
                  
                <div>
                    <div class="text-align-right">
                        <a href="/checkout">update</a>
                        <p class="checkout-subtotal">prints: ${it.basket.printPriceString}</p>
                        <c:if test="${it.basket.discountPercentage gt 0}">
                            <p class="checkout-subtotal">discount: ${it.basket.discountAmountString}</p>
                        </c:if>
                        <p class="checkout-subtotal">shipping: ${it.basket.shippingPriceString}</p>
                        <p class="checkout-total">Total: ${it.basket.totalPriceString}</p>
                    </div>
                </div>
                
                <c:if test="${it.basket.environment.sandbox}">
                    <div style="background-color: #cfc">
                        <h4>Hey there beta tester!</h4>
                        <p>This is currently set in sandbox mode so no real payments are possible
                        and no photos are ever printed or posted.
                        You can even go right through the checkout with your paypal or google sandbox buyer account (<a href="https://developers.google.com/checkout/developer/Google_Checkout_Basic_HTML_Sandbox#Create_Sandbox_Accounts" target="_blank"><i>more info</i></a>).</p>
                        <p>Want the real experience? 
                            <a href="/checkout/environment/live" data-ajax="false">Switch this basket live.</a>
                        </p>
                    </div>
                </c:if>
        
                <div>
                    <div class="text-align-right payment-buttons">
                        <p>
                            <a id="google-purchase-link" href="/purchase/google" data-ajax="false"><img src="https://checkout.google.com/buttons/checkout.gif?merchant_id=${it.merchantId}&w=180&h=46&style=trans&variant=text&loc=en_GB" alt="Proceed to Google Checkout"/></a>
                            <c:if test="${it.userIsAdmin or it.paypalEnabled}">
                                <span>or</span>
                                <a href="/purchase/paypal" data-ajax="false"><img src="https://www.paypalobjects.com/en_US/i/btn/btn_xpressCheckout.gif" alt="Proceed to PayPal checkout"/></a>
                            </c:if>
                        
                            <%--
                                <form action="" method="POST">
                                  <script
                                    src="https://checkout.stripe.com/v2/checkout.js" class="stripe-button"
                                    data-key="pk_test_wAvVqWKntaEUz0wOHGrgSFkC"
                                    data-amount="${it.basket.totalPrice}"
                                    data-name="OddPrints"
                                    data-description="OddPrints ${it.basket.totalPriceString}"
                                    data-image="/128x128.png">
                                  </script>
                                </form>
                                
                                <form action="purchase/paymill" method="post">
                                    <script
                                        src="https://button.paymill.com/v1/"
                                        id="button"
                                        data-title="Pay with Card"
                                        data-description="OddPrints"
                                        data-amount="${it.basket.totalPrice}"
                                        data-currency="GBP"
                                        data-submit-button="Pay ${it.basket.totalPriceString}"
                                        data-public-key="eadff9e18d6ba987f265c245862e658c">
                                    </script>
                                </form>
                                <button data-inline="true" id="stripe-button" data-theme="b" data-mini="true">Pay with Card</button>
                             --%>
                        </p>
                        
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp">
        <jsp:param name="hasCheckoutButtons" value="true" />
    </jsp:include>
    
</div>


<script type="text/javascript">
$(document).ready(function() {    
    $( ".quantity-input" ).bind( "change", function(event, ui) {
        var id = event.target.id;
        var basketItemId = id.split('-')[1];
        window.location.href = "/checkout/update/" + basketItemId + "/" + event.target.value;
    });
    
    $("#google-purchase-link").click(function(e){   
      _gaq.push(function() {
          var pageTracker = _gaq._getAsyncTracker();
          setUrchinInputCode(pageTracker);
          window.location.href = "/purchase/google?analyticsData=" + getUrchinFieldValue();
      });
      e.preventDefault();
    });
    
    $('body').on('token', function(event) {
        $.post('/purchase/stripe', {
            "token": event.token
        });
    });
    
    $('#stripe-button').click(function(){
        var token = function(res){
            var $input = $('<input type=hidden name=stripeToken />').val(res.id);
            $('form').append($input).submit();
        };
        
        StripeCheckout.open({
            key:         'pk_test_wAvVqWKntaEUz0wOHGrgSFkC',
            address:     false,
            
            currency:    'gbp',
            name:        'OddPrints',
            description: 'OddPrints order',
            panelLabel:  'Next',
            image:       '/images/icon128.png',
            token:       token
        });
        
        return false;
      });
});
</script>
<script src="https://checkout.stripe.com/v2/checkout.js"></script>
</body>
</html>
