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
    <jsp:param name="titleText" value=" - Admin" />
    <jsp:param name="descriptionText" value="Admin page." />
</jsp:include>
<body>

<div data-role="page" id="page-admin">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
    
        <h1>Coupons</h1>
            
        <div>
            <c:forEach var="coupon" items="${it.coupons}">
                <h4>${coupon.idString}</h4>
                <form action="/admin/coupons/post" id="updateForm">
                    <div data-role="fieldcontain">
                        <input type="hidden" name="couponKeyString" id="couponKeyString" value="${coupon.idString}"/>
                    </div>
                    <div data-role="fieldcontain">
                        <label for="code">Code:</label>
                        <input type="text" name="code" id="code" value="${coupon.code}"/>
                    </div>
                    <div data-role="fieldcontain">
                        <label for="email">Email:</label>
                        <input type="email" name="email" id="email" value="${coupon.email}"/>
                    </div>
                    <div data-role="fieldcontain">
                        <label for="createdString">Created:</label>
                        <input type="datetime-local" name="createdString" id="createdString" value="${coupon.createdString}"/>
                    </div>
                    <div data-role="fieldcontain">
                        <label for="expiresString">Expires:</label>
                        <input type="datetime-local" name="expiresString" id="expiresString" value="${coupon.expiresString}"/>
                    </div>
                    <div data-role="fieldcontain">
                        <label for="discountAmount">Amount:</label>
                        <input type="text" name="discountAmount" id="discountAmount" value="${coupon.discountAmount}"/>
                    </div>
                    <div data-role="fieldcontain">    
                        <fieldset data-role="controlgroup" data-type="horizontal" data-mini="true">
                            <legend>Discount Type:</legend>                    
    
                            <input type="radio" name="discountType" id="radio-percentage" value="percentage" ${coupon.discountType=='percentage'?'checked':''} />
                            <label for="radio-percentage">Percentage</label>
    
                            <input type="radio" name="discountType" id="radio-pence" value="pence" ${coupon.discountType=='pence'?'checked':''}/>
                            <label for="radio-pence">Pence</label>
                        </fieldset>
                    </div>
                    <div data-role="fieldcontain">
                        <label for="minimumOrderValue">Minimum order value (pence):</label>
                        <input type="number" name="minimumOrderValue" id="minimumOrderValue" value="${coupon.minimumOrderValue}"/>
                    </div>
                    <div data-role="fieldcontain">    
                        <fieldset data-role="controlgroup" data-type="horizontal" data-mini="true">
                            <legend> </legend>                    
    
                            <input type="radio" name="updateOrNew" id="radio-update" value="update" checked />
                            <label for="radio-update">Update</label>
    
                            <input type="radio" name="updateOrNew" id="radio-new" value="new"/>
                            <label for="radio-new">Save as new</label>
                        </fieldset>
                    </div>
                    <input type="submit" value="Submit">
                </form>
                <br/>
                <br/>
                <br/>
            </c:forEach>
        </div>
        
    </div>    
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

<script type="text/javascript">
$(document).ready(function() {  
    // Attach a submit handler to the form
    $( "form" ).submit(function( event ) {
         
        // Stop form from submitting normally
        event.preventDefault();
        
        // Get some values from elements on the page:
        var form = $(this),
            url = form.attr( "action" );
        
        // Send the data using post
        var posting = $.post(url, form.serialize());
        
        // Put the results in a div
        posting.done(function( data ) {
                location.reload();
        }).fail(function(data) {
            alert( data.statusText );
        })
    });
});
</script>
</body>
</html>
