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
    <jsp:param name="titleText" value=" - Error" />
    <jsp:param name="descriptionText" value="Error page." />
</jsp:include>
<body>

<div data-role="page" id="page-error">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <h2>Oh fudgecakes.</h2>
        
        <p><em>
            <c:choose>
                <c:when test="${empty it.message}">
                   Something went wrong.
                </c:when>
                <c:otherwise>
                   ${it.message}
                </c:otherwise>
            </c:choose>
        </em></p>
        
        <p>The sirens are going off at OddPrints towers waking all the nerds from their afternoon naps.</p>
        <p>We shall track down the cause of the problem and
        fire whoever was responsible (unless it was me, then it was probably an honest mistake).</p>
        
        <p>If you've tried a few times and you keep getting this error, <a href="/contact">get in touch</a>
        and we will help you out.</p>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

</body>
</html>
