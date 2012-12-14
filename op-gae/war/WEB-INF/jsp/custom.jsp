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
    <jsp:param name="titleText" value=" - Custom Size Prints" />
    <jsp:param name="descriptionText" value="Print photos at any size you like for non-standard frames." />
</jsp:include>
<body>

<div data-role="page" id="page-custom">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <h2>Custom Size Prints</h2>
                
        <jsp:include page="/WEB-INF/jsp/parts/part-custom.jsp"/>
        
        <h3>Why?</h3>
        <p>Unfortunately, at smaller sizes, "non-standard" frames are actually very common.
        You will often see frames for 4"×4", 3"×3", 2"×2", 4"×3", 4×4cm, 3×3cm etc.
        However, almost all printing labs only offer a smallest printing size of 6"×4".
        Printing at these sizes is very difficult because you have to understand
        print DPI, canvas sizes, exposure auto-correction problems.
        OddPrints makes it easy.</p>
        
        <h3>Completely Free</h3>
        <p>OddPrints is free to use, you just download the image and prints them at a standard size,
        or you can order prints directly. Either way, there are no nags/ads/watermarks or any other nonsense.</p>
      
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
