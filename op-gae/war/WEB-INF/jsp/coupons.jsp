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
    <jsp:param name="titleText" value=" - Coupons" />
    <jsp:param name="descriptionText" value="Claim a coupon." />
</jsp:include>
<body>

<div data-role="page" id="page-coupons">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <c:choose>
            <c:when test="${not empty it.coupon}">
                <c:choose>
                    <c:when test="${it.coupon.valid}">
                        <h1>${it.coupon.displayString}</h1>
                        <p>
                        Discount will be visible at the checkout.</p>
                        <a href="/" data-role="button" data-ajax="false" data-theme="b">Claim discount</a>
                    </c:when>
                    <c:otherwise>
                        <p>This coupon has expired.</p>
                        <a href="/" data-role="button" data-ajax="false" data-theme="b">Back</a>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:when test="${it.noCodeGiven}">
                <h1>Claim a coupon</h1>
                <form action="/">
                    <div data-role="fieldcontain">
                        <label for="code">Code:</label>
                        <input type="text" name="code" id="code" value=""/>
                    </div>
                    <input type="submit" value="Submit">
                </form>
            </c:when>
            <c:otherwise>
                <h1>Coupon code not found.</h1>
                <form action="/">
                    <div data-role="fieldcontain">
                        <label for="code">Code:</label>
                        <input type="text" name="code" id="code" value=""/>
                    </div>
                    <input type="submit" value="Submit">
                </form>
                <a href="/" data-role="button" data-ajax="false" data-theme="b">Back</a>
            </c:otherwise>
        </c:choose>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

<script type="text/javascript">
$(document).ready(function() {  
    // Attach a submit handler to the form
    $( "form" ).submit(function( event ) {
         
        // Stop form from submitting normally
        event.preventDefault();
        
        window.location.href = "/coupons/" + $(this).find( "[name=code]" ).val();
    });
});
</script>

</body>
</html>
