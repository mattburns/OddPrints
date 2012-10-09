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
    <jsp:param name="descriptionText" value="Use OddPrints to print your own passport photos at a fraction of the price. Also handy if you have unwilling subjects like small children." />
</jsp:include>
<body>

<div data-role="page" id="page-passport">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <h2>Print your own passport photos</h2>
        <p>Did you know you can use OddPrints to print your own passport photos?
      Just upload your photo and choose the preset for your country. Easy!</p>
        <p>Unlike other online services, OddPrints is free and the image has no watermark or
        any other nonsense. Just download the generated image and print it at 6"×4".</p>
        <a class="get-started" data-ajax="false" href="/upload/basic"><img style="box-shadow: 0px 0px 15px #222222;" src="/images/passport.png"/></a>
        
        <div class="supported-browser">
            <p>Want to save money? <a href="/upload/basic" class="get-started" data-ajax="false">Get started</a>.</p>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />

</div>

<script type="text/javascript">
$(document).ready(function() {
    var basicModeVar = ${not empty basicMode and basicMode};
    if (isSupportedBrowser() && !basicModeVar) {
        $(".get-started").attr("href", "/edit");
    } else if (!isFileInputSupported()) {
        $(".get-started").attr("href", "/mobile-safari-error?agent=" + navigator.userAgent);
    } else {
        // stick with basic upload
    }
});
</script>

</body>
</html>
