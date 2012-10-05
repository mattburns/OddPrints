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
      
<head>
    <script type="text/javascript" src="/js/jquery-1.7.1.min.js"></script>
</head>
<body>
    <c:choose>                       
        <c:when test="${it.basket.environment.sandbox}">
            <c:set var="paypalurl" value="https://www.sandbox.paypal.com/cgi-bin/webscr"/>
            <c:set var="notifyurl" value="http://www.oddprints.com/checkoutnotification/paypal/sandbox"/>
        </c:when>
        <c:otherwise>
            <c:set var="paypalurl" value="https://www.paypal.com/cgi-bin/webscr"/>
            <c:set var="notifyurl" value="http://www.oddprints.com/checkoutnotification/paypal/live"/>
        </c:otherwise>
    </c:choose>
    
    <p>Redirecting...</p>
    
    <form action="${paypalurl}" method="post" id="paypal-form">  
        <input type="hidden" name="cmd" value="_cart"> 
        <input type="hidden" name="upload" value="1"> 
        <input type="hidden" name="shipping_1" value="2.99"> 
        <input type="hidden" name="business" value="matt@mattburns.co.uk"> 
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

    <script type="text/javascript">
        $(document).ready(function() {
            $("#paypal-form").submit();
        });
    </script>
</body>
</html>