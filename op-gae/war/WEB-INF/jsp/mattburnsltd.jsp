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
    <jsp:param name="titleText" value=" - Matt Burns Ltd" />
    <jsp:param name="descriptionText" value="Details about the company, Matt Burns Ltd." />
</jsp:include>
<body>

<div data-role="page" id="page-mattburnsltd">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <h2>Matt Burns Ltd</h2>
        <p>Matt Burns Ltd, Registered in England and Wales No. 07734891</p>
        <p>Registered Office: Engine Shed, Station Approach, Temple Meads, Bristol, BS1 6QH.</p>
        
        <h2>Creators of</h2>
        <p><a href="http://www.stolencamerafinder.com/"><img src="/images/scf-logo.png"/></a></p>
        <h2>Credits</h2>
        <p>This project is open source and wouldn't be possible without:</p>
        <ul>
            <li>An <a href="https://github.com/stomita/ios-imagefile-megapixel">iOS bug fix</a> by stomita.</li>
            <li><a href="http://jquery.com/">jquery</a> and <a href="http://jquerymobile.com/">jquerymobile</a>.</li>
            <li><a href="http://labs.abeautifulsite.net/jquery-miniColors/">miniColors</a>.</li>
            <li><a href="http://modernizr.com/">modernizr</a>.</li>
            <li><a href="http://baijs.nl/tinycarousel/">tinycarousel</a>.</li>
        </ul>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

</body>
</html>
