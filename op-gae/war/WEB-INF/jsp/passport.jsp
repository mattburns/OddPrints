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
    <jsp:param name="titleText" value=" - Passport Photos" />
    <jsp:param name="descriptionText" value="Use OddPrints to print your own passport photos for free. Also handy if you have unwilling subjects like small children." />
</jsp:include>
<body>

<div data-role="page" id="page-passport">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <h2>Print your own passport photos</h2>
        <p>Did you know you can use OddPrints to print your own passport photos?</p>
        
        <jsp:include page="/WEB-INF/jsp/parts/part-passport.jsp"/>

        <p>You can print passport or visa photos at any size you require
        but to keep things simple, there are default templates for the US, Canada,
        India and the UK.</p>
        
        <div class="supported-browser">
            <div class="text-align-right">
                <p><a href="/upload/basic" class="get-started" data-role="button" data-theme="b" data-icon="arrow-r" data-iconpos="right" data-inline="true" data-ajax="false">Get started</a></p>
            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

</body>
</html>
