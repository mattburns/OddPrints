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
    <jsp:param name="titleText" value=" - Easy printing for tricky frames." />
    <jsp:param name="descriptionText" value="Free and easy way to print photos at ANY size for passports or strange frames. Download the images to print at home, your local lab or online." />
</jsp:include>

<body>

<div data-role="page" id="page-intro">
    
    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content" class="full-width">
        <div class="thin-column">
    
            <h1>Easy printing for tricky frames</h1>
            
            <p>The simple way to print photos
            for your unusual picture frames or passport.
            </p>
        
            <div id="slider-code">
                <div class="viewport">
                    <ul class="overview">
                        <li>
                            <div class="step-number">1</div>
                            <img src="/images/sample-small.png"/>
                            <h2>Upload a photo</h2>
                        </li>
                        <li>
                            <div class="step-number">2</div>
                            <img src="/images/frame-size.png"/>
                            <h2>Enter the size of your frame</h2>
                            <p>Or for passport photos, select your country.</p>
                        </li>
                        <li>
                            <div class="step-number">3</div>
                            <img src="/images/trimming.png"/>
                            <h2>Print your photo</h2>
                            <p>Your print will ready for you to cut at just the right size.</p>
                        </li>
                    </ul>
                </div>
                <ul class="pager">
                    <li><a rel="0" class="pagenum" href="#"></a></li>
                    <li><a rel="1" class="pagenum" href="#"></a></li>
                    <li><a rel="2" class="pagenum" href="#"></a></li>
                </ul>
            </div>
        
            <div class="supported-browser">
                <p><a href="/upload/basic" id="get-started" data-role="button" data-theme="b" data-icon="arrow-r" data-iconpos="right"  data-ajax="false">Get Started!</a></p>
            </div>
        </div>

        <div class="after-fold">
            <div class="thin-column">
                <h3>How it works</h3>
                <p>Photo labs only offer printing at standard sizes such as 6"×4" or 7"×5" but
                sometimes you want to print your photos at different dimensions or ratios.</p>
                
                <p>With OddPrints you can choose any size you like. We then generate a new image
                to print at a standard size which is ready to be cut down to the size you want.</p>
                
                <p>For example, if you want to print a photo for a 2"×2" frame you simply upload
                your photo and select a frame size of 2"×2". You can then either order prints from
                us, or just download the new image ready to print yourself at 6"×4".
                The print will have the correct border around your image so that you can cut it
                down to the correct size.
                <img src="/images/workflow.png" /></p>
                
                <h3>Print your own passport photos</h3>
                <p>Did you know you can use OddPrints to print your own passport photos?
                Just upload your photo and choose the preset for your country. Easy!</p>
                <p>Unlike other online services, OddPrints is free and the image has no watermark or
                any other nonsense. Just download the generated image and print it at 6"×4".</p>
                <img style="box-shadow: 0px 0px 15px #222222;" src="/images/passport.png"/>
                
                <img class="osi-logo" src="/images/osi_standard_logo.png"/>
                <h3>Free</h3>
                <p>We believe in freedom. This site is free to use and the images you
                download have no watermark or branding. There's no annoying captcha,
                advertisements or registration process. Even this code for this website
                is open source.</p>
                <p>If you want to support this site, you can order your prints directly from us.
                </p>
                <p>Or not. It's up to you. :)</p>
            </div>
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

    playCarousel();

    $(window).resize(function() {
        restartCarousel();
    });
});

function restartCarousel() {
    $('#slider-code').tinycarousel_stop();
    playCarousel();
}

function playCarousel() {
    var width = $("#slider-code .viewport").width(); // don't need to use 'px'
    jQuery("#slider-code .overview li").css('width', width);
    $('#slider-code').tinycarousel({ pager: true, interval: true, controls: false, duration: 500, intervaltime: 3000});
}

</script>

</body>
</html>
