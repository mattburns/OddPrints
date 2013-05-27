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
    <jsp:param name="titleText" value=" - PayPal redirect" />
    <jsp:param name="descriptionText" value="Redirecting to PayPal." />
</jsp:include>
<body>

<div data-role="page" id="page-orders">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />
    
    <div data-role="content" class="">
        <c:choose>                       
            <c:when test="${it.basket.environment.sandbox}">
                <c:set var="paypalurl" value="https://www.sandbox.paypal.com/cgi-bin/webscr"/>
                <c:set var="notifyurl" value="http://www.oddprints.com/ipn/sandbox"/>
            </c:when>
            <c:otherwise>
                <c:set var="paypalurl" value="https://www.paypal.com/cgi-bin/webscr"/>
                <c:set var="notifyurl" value="http://www.oddprints.com/ipn/live"/>
            </c:otherwise>
        </c:choose>
        
        <p>Redirecting to PayPal...</p>
        
        <form action="${paypalurl}" method="post" id="paypal-form">  
            <input type="hidden" name="cmd" value="_cart"> 
            <input type="hidden" name="upload" value="1"> 
            <input type="hidden" name="shipping_1" value="2.99">
            <c:choose>                       
                <c:when test="${it.basket.environment.sandbox}">
                    <input type="hidden" name="business" value="seller_sellerpass@mattburns.co.uk">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="business" value="matt@mattburns.co.uk">
                </c:otherwise>
            </c:choose>
            
            <input type="hidden" name="currency_code" value="GBP"> 
            <input type="hidden" name="no_shipping" value="2"> 
            <input type="hidden" name="custom" value="${it.basket.idString}"> 
            <input type="hidden" name="notify_url" value="${notifyurl}"> 
            
            <c:forEach var="basketItem" items="${it.basket.items}" varStatus="basketItemNumber">
                <input type="hidden" name="item_name_${basketItemNumber.index + 1}" value="Image ${basketItemNumber.index + 1}"> 
                <input type="hidden" name="amount_${basketItemNumber.index + 1}" value="${basketItem.unitPriceStringNoSymbol}">
                <input type="hidden" name="quantity_${basketItemNumber.index + 1}" value="${basketItem.quantity}">
            </c:forEach>
            
            <input data-role="none" type="hidden" name="submit-paypal"> 
        </form>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

<script type="text/javascript">
    $(document).ready(function() {
        $("#paypal-form").submit();
    });
</script>
