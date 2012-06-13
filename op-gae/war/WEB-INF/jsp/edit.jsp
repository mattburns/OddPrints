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

<div data-role="page" id="page-upload">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <div id="choose-a-file">
            <h2>Upload a photo</h2>
            <div data-role="fieldcontain" >        
                <label for="files">Choose</label>
                <input type="file" id="files" name="files[]" />
                <output id="list"></output>
            </div>
            <p>Or just play with the <a id="sample-photo-link" href="/edit">sample photo</a>.</p>
        </div>
 
        <div id="file-chosen">
            <c:if test="${not empty it.basket and it.basket.size gt 0}">
                <div class="text-align-right">
                    <a href="/checkout">basket (${it.basket.size})</a>
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
                            
                            <input type="radio" name="radio-crop-fit" id="radio-fill" value="fit" checked="checked"/>
                            <label for="radio-fill" title="Ensure the picture fills the frame, however, some of the picture may not fit in the frame">
                                Fill
                            </label>
    
                            <input type="radio" name="radio-crop-fit" id="radio-fit" value="fit" />
                            <label for="radio-fit" title="Ensure the picture fits in the frame, however, some margin may be visible in the frame">
                                Fit
                            </label>
                                                    
                            <input type="radio" name="radio-crop-fit" id="radio-crop" value="crop" />
                            <label for="radio-crop" title="Crop parts of the image that are outside the frame">
                                Crop
                            </label>
                        </fieldset>
                    </div>
                    
                    <div data-role="fieldcontain">
                        <fieldset data-role="controlgroup" data-type="horizontal" data-mini="true">
                            <legend>Show guidelines:</legend>
                            
                            <input type="radio" name="radio-guides" id="radio-guides-on-top" value="guides-on-top" checked="checked" />
                            <label for="radio-guides-on-top" title="Guidelines are drawn on top of the image">
                                Top
                            </label>
    
                            <input type="radio" name="radio-guides" id="radio-guides-on-bottom" value="guides-on-bottom"  />
                            <label for="radio-guides-on-bottom" title="Guidelines are drawn underneath the image">
                                Bottom
                            </label>
                            
                            <input type="radio" name="radio-guides" id="radio-guides-off" value="guides-off"  />
                            <label for="radio-guides-off" title="No guidelines are drawn">
                                Off
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
                
                <img id="img-preview" src="" />
                <div class="text-align-right">
                    <a id="change-picture-link" href="/edit">change picture</a>
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
    
            <div data-role="collapsible" data-collapsed="false" id="debugging" style="display:none;" >
                <h3>Debugging</h3>
                <p id="results">results</p>
                
                <p>View <a href="test.html">test results</a>.</p>
                
                <canvas id="myCanvas" width="100" height="100" style="border:5px solid red;">
                Your browser does not support the canvas element.
                </canvas>
                
            </div>
        </div>
    </div>
       
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />
</div>

<div data-role="page" id="page-error">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <h3>Oh fudgecakes.</h3>
        
        <!-- generated with http://h5bp.github.com/caniuse/ -->
        <div id="h5p-message"></div><script>Modernizr.browserPrompt=function(a,b){if(a.agents){Modernizr.browserPrompt.cb(a);return}var c=!0,d=a.features.split(" "),e=a.options,f;for(var g=-1,h=d.length;++g<h;)f=d[g],!Modernizr[f]&&(c=!1);if(c)return c;var i=document.createElement("script"),j=document.getElementsByTagName("script")[0],k="http://api.html5please.com/"+d.join("+")+".json?callback=Modernizr.browserPrompt&html&"+e;return Modernizr.browserPrompt.cb=b,i.src=k,j.parentNode.insertBefore(i,j),!1},Modernizr.browserPrompt({features: "filereader+canvas", options:"texticon&0"},function(a){var b=document.getElementById("h5p-message");b.innerHTML=a.html})</script>
    </div>
       
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />
</div>

<script type="text/javascript">

var myCanvas = document.getElementById("myCanvas");
var ctx;
var img = new Image();
var frameSize = "";
    
var cmToInches = 0.393700787;
var dpiRender = 100;
var dpiFull = 300;

$(document).ready(function() {
    var supportedBrowser = !!window.FileReader && Modernizr.canvas;
    
    fileChooser();
    
    if (!supportedBrowser) {    
        $.mobile.changePage("#page-error");
        return;
    }
    
    document.getElementById('files').addEventListener('change', handleFileSelect, false);
    
    ctx = myCanvas.getContext("2d");
    
    $("#img-link").click(download);
    $("#sample-photo-link").click(function(e) {
        e.preventDefault();
        loadSample();
    });
    
    $("#change-picture-link").click(function(e) {
        e.preventDefault();
        fileChooser();
    });

    img.src = "images/grey.jpg";
    img.onload = function(){
        queueRenderPreview();  
    };

    $("input").click(renderPreview);
    $("input").change(queueRenderPreview);
    $("input").keyup(renderPreview);
    $(".span-slider").change(queueRenderPreview);
    $("#radio-cm, #radio-inches").change(renderPreview);
    
    $("#img-upload").click(uploadImage);

    var sURL = window.document.URL.toString();  
    if (sURL.indexOf("showCanvas") > 0) {
        $('#debugging').show();
    }
});

function handleFileSelect(evt) {
    var files = evt.target.files; // FileList object

    // files is a FileList of File objects.
    var output = [];
    loadFile(files[0]);
}

function fileChooser() {
	$("#choose-a-file").show();
    $("#file-chosen").hide();
}
function fileChosen() {
	$("#choose-a-file").hide();
    $("#file-chosen").show();
}

function loadSample() {
	loadFileUrl("images/sample.jpg");
}

function loadFile(file) {
    var reader = new FileReader();
    reader.onload = function (event) {
    	loadFileUrl(event.target.result);
        //$.mobile.changePage("/edit");
        //fileChosen();
    };
    reader.readAsDataURL(file);
}

function loadFileUrl(url) {
    img.src = url;
    fileChosen();
}

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

function drawImage(settings) {
    // image rotation experiment:
    // ctx.save(); 
    // ctx.translate(settings.destinationX, settings.destinationY); 
    // ctx.rotate(90 * Math.PI/180);
    // ctx.translate(0, -settings.destinationHeight); 
    // ctx.drawImage(img, settings.sourceX, settings.sourceY, settings.sourceWidth, settings.sourceHeight, 0,0, settings.destinationWidth, settings.destinationHeight);
    // ctx.restore();
    
    ctx.drawImage(img, settings.sourceX, settings.sourceY, settings.sourceWidth, settings.sourceHeight, settings.destinationX, settings.destinationY, settings.destinationWidth, settings.destinationHeight);
}

function drawGuidelines(settings) {
    // top
    drawHorizontalLine(settings.frameY, settings);
    // bottom
    drawHorizontalLine(settings.frameY + settings.frameHeightPx - 1, settings);
    // left
    drawVerticalLine(settings.frameX, settings);
    // right
    drawVerticalLine(settings.frameX + settings.frameWidthPx - 1, settings);
}

function drawVerticalLine(x, settings) {
    drawLine(x, 0, x, settings.canvasHeight);
}

function drawHorizontalLine(y, settings) {
    drawLine(0, y, settings.canvasWidth, y);
}

function drawLine(x1, y1, x2, y2) {
    ctx.moveTo(x1 + 0.5, y1 + 0.5);
    ctx.lineTo(x2 + 0.5, y2 + 0.5);
    ctx.stroke();
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

function renderPreview() {
    var jpegData = calculate(dpiRender);
    $("#img-preview").attr("src", jpegData);
}

function renderFull() {
    var jpegData = calculate(dpiFull);
    $("#img-preview").attr("src", jpegData);
    $.mobile.showPageLoadingMsg();
    return jpegData;
}

function download() {
    $.mobile.showPageLoadingMsg();
    var t=setTimeout("downloadImpl()", 50);
}

function downloadImpl() {
    var jpegData = renderFull();
    $.mobile.hidePageLoadingMsg();
    window.open(jpegData, "_newtab");
}

function calculate(dpi) {
    $.mobile.showPageLoadingMsg();
    updateFrameHeader();
    
    var settings = calculateSettings(dpi);
    
    $("#print-size-text").html("Image must be printed at " + settings.printWidth + "\"x" + settings.printHeight + "\"");
    
    $("canvas").attr("width", settings.canvasWidth);
    $("canvas").attr("height", settings.canvasHeight);
    
    restrictSliders();
      
    ctx.fillStyle="#dddddd";
    ctx.fillRect(0, 0, settings.canvasWidth, settings.canvasHeight);
    
    var drawGuides = !$("#radio-guides-off").attr('checked');
    var guidesOnTop = drawGuides && $("#radio-guides-on-top").attr('checked');
    var guidesOnBottom = drawGuides && $("#radio-guides-on-bottom").attr('checked');
    
    if (guidesOnBottom) {
        drawGuidelines(settings);
    }
    drawImage(settings);
    if (guidesOnTop) {
        drawGuidelines(settings);
    }

    var jpegData = myCanvas.toDataURL('image/jpeg');
    
    $("#img-preview").attr("src", jpegData);
    $.mobile.hidePageLoadingMsg();
    return jpegData;
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

function calculateSettings(dpi) {
    var zooming = 'fit';
    if ($("#radio-fill").attr('checked')) {
        zooming = 'fill';
    }
    if ($("#radio-crop").attr('checked')) {
        zooming = 'crop';
    }
        
    var printOrientation = $('input:radio[name=radio-orient]:checked').val(); 
    
    var frameWidthInInches = getFrameWidth();
    var frameHeightInInches = getFrameHeight();
    if (getFrameUnits() === "cm") {
        frameWidthInInches = toInches(frameWidthInInches);
        frameHeightInInches = toInches(frameHeightInInches);
    }
    
    if (!(frameWidthInInches > 0 && frameHeightInInches > 0)) {
        return;
    }
    
    var settings = calculatePrintSize(frameWidthInInches, frameHeightInInches, printOrientation);
    settings = calculateCanvasSize(settings.printWidth, settings.printHeight, dpi, settings);
    settings = calculateFramePixelSize(frameWidthInInches, frameHeightInInches, dpi, settings);
    settings = calculateFrameXY(settings.canvasWidth, settings.canvasHeight, settings.frameWidthPx, settings.frameHeightPx, settings);
    settings = calculateDestination(zooming, settings.frameWidthPx, settings.frameHeightPx, settings.frameX, settings.frameY, img.width, img.height, settings);

    return settings;
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
	$.mobile.showPageLoadingMsg();
	var t=setTimeout("uploadImageImpl()", 50);
}

function uploadImageImpl() {
    var settings = calculateSettings(dpiFull);
    
    $.post("/upload",
       { imageData: renderFull(),
         frameSize: frameSizeString(),
         printWidth: settings.printWidth,
         printHeight: settings.printHeight
       },
       function(data) {
           window.location.href = "/checkout";
       }
    );
    
}

</script>
</body>
</html>