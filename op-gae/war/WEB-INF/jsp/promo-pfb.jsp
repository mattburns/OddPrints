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
    <jsp:param name="titleText" value=" - Panoramic Prints" />
    <jsp:param name="descriptionText" value="Use OddPrints to print stunning panoramas." />
</jsp:include>
<body>

<div data-role="page" id="page-panoramic">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <h2>Exclusive 15% offer!</h2>
        <p>
        For this month only, OddPrints and <strong>Photography for beginners</strong> magazine have teamed up to offer you an exclusive 15%
        off all orders placed here.</p>
        <a href="/" data-role="button" data-ajax="false" data-theme="b">Claim 15% discount (applied at checkout)</a>
        <a href="http://www.photoforbeginners.com/"><img src="/images/pfb-logo.png"/></a>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

</body>
</html>
