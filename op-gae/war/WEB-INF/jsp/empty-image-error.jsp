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

<div data-role="page" id="page-error2">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <h2>Oh fudgecakes.</h2>
        
        <p>Something went wrong uploading that image. Please try again.
            <a href="/upload/basic" class="force-basic"
                    data-role="button"
                    data-theme="b" data-icon="arrow-r"
                    data-iconpos="right" data-ajax="false">Try again in 'basic' mode</a> 
        </p>
        
        <p>Hopefully you won't see this error again, but if you do, please try another web browser.
        If you want to shout at someone, <a href="/contact">shout at us</a>, it'll make you feel better.</p>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

</body>
</html>
