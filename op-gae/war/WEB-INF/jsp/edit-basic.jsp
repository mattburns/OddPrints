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
    <jsp:param name="titleText" value=" - Edit" />
    <jsp:param name="descriptionText" value="Upload a photo and choose the size of your frame. You can also tile and crop your image." />
</jsp:include>
<body>

<div data-role="page" id="page-edit">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <c:if test="${not empty it.basket and it.basket.size gt 0}">
            <div class="text-align-right">
                <a href="/checkout">basket (${it.basket.size})</a>
            </div>
        </c:if>

        <form action="#" method="get">
            <div data-role="fieldcontain" title="Width of picture frame">
                <h2 id="frame-size-text"></h2>
                <label for="frame-width" id="width-label">Width:</label>
                <span class="span-slider"><input type="range" name="slider" id="frame-width" value="4" step="0.1" min="0.1" max="10" data-highlight="true"/></span>
            </div>
            <div data-role="fieldcontain" title="Height of picture frame">
                <label for="frame-height" id="height-label">Height:</label>
                <span class="span-slider"><input type="range" name="slider" id="frame-height" value="2" step="0.1" min="0.1" max="8" data-highlight="true"/></span>
            </div>
            
            <div data-role="fieldcontain" title="Preset">
                <label for="select-preset">Or select a preset:</label>
            
                <select name="select-preset" id="select-preset" >
                    <option value="custom" selected>Custom</option>
                    <option value="canada" >Passport - Canada (50mm x 70mm)</option>
                    <option value="india" >Passport - India (35mm x 35mm)</option>
                    <option value="uk" >Passport - UK (35mm x 45mm)</option>
                    <option value="us" >Passport - US (2" x 2")</option>
                </select>
            </div>
        
            <img id="img-preview" src="/images/grey.jpg" />
            <div id="error-loading-preview">
                <p>Error loading preview. Retrying...</p>
            </div>
            <div class="text-align-right">
                <a id="change-picture-link" href="/upload/basic">change picture</a>
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
                        
                        <input type="radio" name="radio-crop-fit" id="radio-fill" value="FILL"/>
                        <label for="radio-fill" title="Ensure the picture fills the frame, however, some of the picture may not fit in the frame">
                            Fill
                        </label>

                        <input type="radio" name="radio-crop-fit" id="radio-fit" value="FIT" />
                        <label for="radio-fit" title="Ensure the picture fits in the frame, however, some margin may be visible in the frame">
                            Fit
                        </label>
                                                
                        <input type="radio" name="radio-crop-fit" id="radio-crop" value="CROP" checked="checked" />
                        <label for="radio-crop" title="Crop parts of the image that are outside the frame">
                            Crop
                        </label>
                        
                        <input type="radio" name="radio-crop-fit" id="radio-tile" value="TILE" />
                        <label for="radio-tile" title="Tile the image">
                            Tile
                        </label>
                    </fieldset>
                </div>
                
                <div data-role="fieldcontain">
                    <fieldset data-role="controlgroup" data-type="horizontal" data-mini="true">
                        <legend>Print orientation:</legend>
                        
                        <input type="radio" name="radio-orient" id="radio-orient-auto" value="AUTO" checked="checked" />
                        <label for="radio-orient-auto" title="Match the orientation of the frame">
                            Auto
                        </label>

                        <input type="radio" name="radio-orient" id="radio-orient-portrait" value="PORTRAIT" />
                        <label for="radio-orient-portrait">Portrait</label>
                                                
                        <input type="radio" name="radio-orient" id="radio-orient-landscape" value="LANDSCAPE" />
                        <label for="radio-orient-landscape">Landscape</label>
                    </fieldset>
                </div>
            </div>
            
            <c:choose>
                <c:when test="${not empty it.basket and it.basket.size gt 0}">
                    <a href="#" id="img-upload" data-role="button" data-theme="b">Add to basket</a>
                </c:when>
                <c:otherwise>
                    <h2 id="print-size-text"></h2>
                    <a href="#" id="img-download" data-role="button" data-theme="b" target="_blank">Download</a>
                    Or just <a href="#" id="img-upload" data-theme="b">order prints from us</a>.
                </c:otherwise>
            </c:choose>
        </form>
    </div>
       
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />
</div>

<script type="text/javascript">

var frameSize = "";
var tileMargin = 10;

$(document).ready(function() {
    $("#error-loading-preview").hide();
    $("input").click(queueRenderPreview);
    $("input").change(queueRenderPreview);
    $("input").keyup(queueRenderPreview);
    $(".span-slider").change(queueRenderPreview);
    $("#radio-cm, #radio-inches").change(renderPreview);
    
    $("#img-upload").click(uploadImage);
    $("#select-preset").change(handlePresetSelect);

    var sURL = window.document.URL.toString();  
    if (sURL.indexOf("showCanvas") > 0) {
        $('#debugging').show();
    }
    
    renderPreview();
});

function renderPreview() {
    $.mobile.showPageLoadingMsg();
    updateTextAndControls();

    var urlEnding = "/" + getFrameWidthInInches() + "/" + getFrameHeightInInches() + "/" + getZooming() + "/" + getOrientation() + "/JPEG/95";
    var previewImageUrl = "/transformer/" + dpiRender + urlEnding + "/" + tileMargin;
    var finalImageUrl = "/transformer/" + dpiFull + urlEnding + "/" + (tileMargin*3) + "?download=true";
    
    $("#img-preview").attr("src", previewImageUrl);
    $("#img-download").attr("href", finalImageUrl);
    var img = new Image();
    img.src = previewImageUrl;
    img.onload = function(){
        $("#error-loading-preview").hide();
        $.mobile.hidePageLoadingMsg();
    };
    img.onerror = function(){
        $.mobile.hidePageLoadingMsg();
        $("#error-loading-preview").show();
        renderPreview(); // try again
    };
}

function uploadImage() {
    $.mobile.showPageLoadingMsg();
    var settings = calculatePrintSize(getFrameWidthInInches(), getFrameHeightInInches(), getOrientation());
    
    $.post("/upload/basic",
       { dpi: dpiFull,
         frameWidthInInches: getFrameWidthInInches(),
         frameHeightInInches: getFrameHeightInInches(),
         zooming: getZooming(),
         orientation: getOrientation(),
         outputEncoding: 'JPEG',
         quality: 95,
         tileMargin: tileMargin*3,
         frameSize: frameSizeString(),
         printWidth: settings.printWidth,
         printHeight: settings.printHeight
       }
    )
    .success(
            function() { 
                window.location.href = "/checkout";
            }
    )
    .error(
            function() { 
                window.location.href = "/error?message=Failed+to+upload+original+image.";
            }
    );
}

</script>
</body>
</html>
