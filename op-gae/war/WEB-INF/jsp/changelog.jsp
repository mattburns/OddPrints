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
    <jsp:param name="titleText" value=" - Change log" />
    <jsp:param name="descriptionText" value="History of site improvements." />
</jsp:include>
<body>

<div data-role="page" id="page-changelog">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <h2>v.9 - 2012/11/22</h2>
        <ul>
            <li>Fixed bug that sometimes corrupted images > 1MB in basic bode.</li>
            <li>Added panoramic print support.</li>
        </ul>
        
        <h2>v.8 - 2012/10/15</h2>
        <ul>
            <li>Image panning and zooming.</li>
        </ul>

        <h2>v.7 - 2012/10/09</h2>
        <ul>
            <li>Redesigned homepage.</li>
            <li>Reshuffled help page and added shipping times.</li>
        </ul>
        
        <h2>v.6 - 2012/10/05</h2>
        <ul>
            <li>Ensure basket is updated to submitted state after submitting.</li>
        </ul>
        
        <h2>v.5 - 2012/08/22</h2>
        <ul>
            <li>Allow payments to be made with PayPal.</li>
        </ul>

        <h2>v.4 - 2012/08/13</h2>
        <ul>
            <li>Don't auto-submit orders to Pwinty.</li>
        </ul>

        <h2>v.3 - 2012/07/30</h2>
        <ul>
            <li>Added presets for a few common passport sizes.</li>
        </ul>

        <h2>v.2 - 2012/06/26</h2>
        <ul>
            <li>Added a server-side implementation to support more browsers.</li>
        </ul>

        <h2>v.1 - 2012/06/12</h2>
        <ul>
            <li>Initial version.</li>
        </ul>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

</body>
</html>
