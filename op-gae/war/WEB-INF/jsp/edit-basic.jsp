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

<div data-role="page" id="page-edit">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <div id="file-chosen">
            <c:if test="${not empty it.basket and it.basket.size gt 0}">
                <div class="text-align-right">
                    <a href="/checkout/basic">basket (${it.basket.size})</a>
                </div>
            </c:if>
    
            <form action="#" method="get">
                <div data-role="fieldcontain" title="Width of picture frame">                
                    <h2 id="frame-size-text"></h2>
                    <label for="frame-width">Width:</label>
                    <span class="span-slider"><input type="range" name="slider" id="frame-width" value="4" step="0.1" min="0.1" max="10" data-highlight="true"/></span>
                </div>
                <div data-role="fieldcontain" title="Height of picture frame">
                    <label for="frame-height">Height:</label>
                    <span class="span-slider"><input type="range" name="slider" id="frame-height" value="2" step="0.1" min="0.1" max="8" data-highlight="true"/></span>
                </div>
                
                <div data-role="collapsible" data-collapsed="true"  data-content-theme="c" >
                    <h3 title="Advanced control of the generated image">Extra options</h3>
                    
                    <div data-role="fieldcontain" >    
                        <fieldset data-role="controlgroup" data-type="horizontal" data-mini="true">
                            <legend>Units:</legend>                    
    
                            <input type="radio" name="radio-frame-units" id="radio-inches" value="inches" checked="checked" />
                            <label for="radio-inches" title="Frame is measured in inches">Inches</label>
    
                            <input type="radio" name="radio-frame-units" id="radio-cm" value="cm" />
                            <label for="radio-cm" title="Frame is measured in centimetres">Centimetres</label>
                        </fieldset>
                    </div>
                        
                    <div data-role="fieldcontain">
                        <fieldset data-role="controlgroup" data-type="horizontal" data-mini="true">
                            <legend>Zooming:</legend>
                            
                            <input type="radio" name="radio-crop-fit" id="radio-fill" value="fit"/>
                            <label for="radio-fill" title="Ensure the picture fills the frame, however, some of the picture may not fit in the frame">
                                Fill
                            </label>
    
                            <input type="radio" name="radio-crop-fit" id="radio-fit" value="fit" />
                            <label for="radio-fit" title="Ensure the picture fits in the frame, however, some margin may be visible in the frame">
                                Fit
                            </label>
                                                    
                            <input type="radio" name="radio-crop-fit" id="radio-crop" value="crop" checked="checked" />
                            <label for="radio-crop" title="Crop parts of the image that are outside the frame">
                                Crop
                            </label>
                        </fieldset>
                    </div>
                    
                    <div data-role="fieldcontain">
                        <fieldset data-role="controlgroup" data-type="horizontal" data-mini="true">
                            <legend>Print orientation:</legend>
                            
                            <input type="radio" name="radio-orient" id="radio-orient-auto" value="auto" checked="checked" />
                            <label for="radio-orient-auto" title="Match the orientation of the frame">
                                Auto
                            </label>
    
                            <input type="radio" name="radio-orient" id="radio-orient-portrait" value="portrait" />
                            <label for="radio-orient-portrait">Portrait</label>
                                                    
                            <input type="radio" name="radio-orient" id="radio-orient-landscape" value="landscape" />
                            <label for="radio-orient-landscape">Landscape</label>
                        </fieldset>
                    </div>
                </div>
                
                <img id="img-preview" src="/images/grey.jpg" />
                <div class="text-align-right">
                    <a id="change-picture-link" href="/upload/basic">change picture</a>
                </div>
                
                <c:choose>
                    <c:when test="${not empty it.basket and it.basket.size gt 0}">
                        <a href="#" id="img-upload" data-role="button" data-theme="b">Add to basket</a>
                    </c:when>
                    <c:otherwise>
                        <h2 id="print-size-text"></h2>
                        <a href="#" id="img-link" data-role="button" data-theme="b">Download</a>
                        Or just <a href="#" id="img-upload" data-theme="b">order prints from us</a>.
                    </c:otherwise>
                </c:choose>
            </form>
        </div>
    </div>
       
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />
</div>

<script type="text/javascript">

var frameSize = "";
    
var cmToInches = 0.393700787;
var dpiRender = 100;
var dpiFull = 300;

$(document).ready(function() {
    
    $("input").click(updateImage);
    $("input").change(updateImage);
    $("input").keyup(updateImage);
    $(".span-slider").change(updateImage);
    $("#radio-cm, #radio-inches").change(updateImage);
    
    $("#img-upload").click(uploadImage);

    var sURL = window.document.URL.toString();  
    if (sURL.indexOf("showCanvas") > 0) {
        $('#debugging').show();
    }
    
    updateImage();
});

function toCm(inches) {
    return inches / cmToInches;
}

function toInches(cm) {
    return cm * cmToInches;
}

function frameSizeString() {
    var frameWidth = getFrameWidthString();
    var frameHeight = getFrameHeightString();
    var isInches = $("#radio-inches").attr('checked');
    return frameWidth + (isInches? '"' : '') + 'x' + frameHeight + (isInches? '"' : 'cm');
}

function updateFrameHeader() {
    $("#frame-size-text").html('Enter your frame size (currently ' + frameSizeString() + ')');
}

var renderDelay = 100;
var renderTimeoutId = 0;

// if there are multiple call in quick succession, only that last call does the real work
function queueRenderPreview() {
    $.mobile.showPageLoadingMsg();
    updateFrameHeader();
    restrictSliders();
    clearTimeout(renderTimeoutId);
    
    renderTimeoutId = window.setTimeout(
        function() {
            renderPreview();
        },
        renderDelay
    );
}

function getZooming() {
    var zooming = 'FIT';
    if ($("#radio-fill").attr('checked')) {
        zooming = 'FILL';
    }
    if ($("#radio-crop").attr('checked')) {
        zooming = 'CROP';
    }
    return zooming;
}

function getOrientation() {
    return $('input:radio[name=radio-orient]:checked').val().toUpperCase(); 
}

function updateImage() {
    var zooming = getZooming();
    
    var frameWidthInInches = getFrameWidth();
    var frameHeightInInches = getFrameHeight();
    if (getFrameUnits() === "cm") {
        frameWidthInInches = toInches(frameWidthInInches);
        frameHeightInInches = toInches(frameHeightInInches);
    }
    
    var previewImageUrl = "/transformer/" + dpiRender + "/" + frameWidthInInches + "/" + frameHeightInInches + "/" + zooming + "/" + getOrientation() + "/JPEG/95";
    var finalImageUrl = "/transformer/" + dpiFull + "/" + frameWidthInInches + "/" + frameHeightInInches + "/" + zooming + "/" + getOrientation() + "/JPEG/95";
    
    $("#img-preview").attr("src", previewImageUrl);
    $("#img-link").attr("href", finalImageUrl);
    $.mobile.showPageLoadingMsg();
    var img = new Image();
    img.src = previewImageUrl;
    img.onload = function(){
        $.mobile.hidePageLoadingMsg();
    };
    
}

function restrictSliders() {
    var maxFrameWidth = 10;
    var maxFrameHeight = 8;
    if (canvasIsCurrentlyPortrait()) {
        maxFrameWidth = 8;
        maxFrameHeight = 10;
    }
    
    var visibleMaxFrameWidth = maxFrameWidth;
    var visibleMaxFrameHeight = maxFrameHeight;
    
    if (getFrameUnits() === "cm") {
        visibleMaxFrameWidth = toCm(maxFrameWidth);
        visibleMaxFrameHeight = toCm(maxFrameHeight);
    }
    
    $("#frame-width").attr("max", visibleMaxFrameWidth);
    $("#frame-height").attr("max", visibleMaxFrameHeight);
    
    if (getFrameWidth() > visibleMaxFrameWidth) {
        $("#frame-width").val(visibleMaxFrameWidth);
    }
    if (getFrameHeight() > visibleMaxFrameHeight) {
        $("#frame-height").val(visibleMaxFrameHeight);
    }
}

function canvasIsCurrentlyPortrait() {
    return parseInt($("canvas").attr("height")) > parseInt($("canvas").attr("width"));
}

function getFrameUnits() {
    return $('input:radio[name=radio-frame-units]:checked').val();
}

function getFrameWidth() {
    return parseFloat($("#frame-width").val());
}
function getFrameHeight() {
    return parseFloat($("#frame-height").val());
}
function getFrameWidthString() {
    return getFrameWidth().toFixed(1);
}
function getFrameHeightString() {
    return getFrameHeight().toFixed(1);
}

function uploadImage() {
    var zooming = getZooming();
    var frameWidthInInches = getFrameWidth();
    var frameHeightInInches = getFrameHeight();
    if (getFrameUnits() === "cm") {
        frameWidthInInches = toInches(frameWidthInInches);
        frameHeightInInches = toInches(frameHeightInInches);
    }

    var settings = calculatePrintSize(frameWidthInInches, frameHeightInInches, getOrientation().toLowerCase());
    
    $.post("/upload/basic",
       { dpi: dpiFull,
         frameWidthInInches: frameWidthInInches,
         frameHeightInInches: frameHeightInInches,
         zooming: zooming,
         orientation: getOrientation(),
         outputEncoding: 'JPEG',
         quality: 95,
         frameSize: frameSizeString(),
         printWidth: settings.printWidth,
         printHeight: settings.printHeight
       }
    )
    .success(
            function() { 
            	window.location.href = "/checkout/basic";
            }
    )
    .error(
    		function() { 
    			window.location.href = "/error?message=Failed+to+upload+image.";
		    }
	);
    
}

</script>
</body>
</html>