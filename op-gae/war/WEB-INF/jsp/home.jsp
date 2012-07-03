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
      
<jsp:include page="/WEB-INF/jsp/parts/html-head.jsp" />
<body>

<div data-role="page" id="page-intro">
    
    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <h1>Easy printing for tricky frames.</h1>
        <p>OddPrints is the simple way to print your photos at non-standard sizes for your unusual picture frames.</p>
        <h2>Step 1: Upload a photo</h2>
        <p>Picking your favourite is the hard part.</p>
        <h2>Step 2: Enter the size of your frame</h2>
        <p>This is the size in real inches (or centimetres) you would like your photo printed.</p>
        <h2>Step 3: Print your photo</h2>
        <p>Your print will ready for you to cut at just the right size.</p>

        <div class="supported-browser">
            <p><a href="/upload/basic" id="get-started" data-role="button" data-theme="b" data-icon="arrow-r" data-iconpos="right"  data-ajax="false">Get Started!</a></p>
        </div>
            
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />
    
</div>

<script type="text/javascript">
$(document).ready(function() {
    var basicModeVar = ${not empty basicMode and basicMode};
    if (isSupportedBrowser() && !basicModeVar) {
        $("#get-started").attr("href", "/edit");
    } else if (!isFileInputSupported()) {
        $("#get-started").attr("href", "/mobile-safari-error?agent=" + navigator.userAgent);
    } else {
        // stick with basic upload
    }
});
</script>

</body>
</html>