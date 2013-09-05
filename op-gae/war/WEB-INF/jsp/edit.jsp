<!DOCTYPE html>
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

<div data-role="page" id="page-upload">

    <jsp:include page="/WEB-INF/jsp/parts/page-header.jsp" />

    <div data-role="content">
        <div id="choose-a-file">
            <h1>Upload a photo</h1>
            <div data-role="fieldcontain" >        
                <label for="files"></label>
                <input type="file" id="files" name="files[]" />
                <output id="list"></output>
            </div>
            <p class="not-sticker-mode">Or just play with the <a id="sample-photo-link" href="#">sample photo</a>.</p>
        </div>
 
        <div id="file-chosen">
            <c:if test="${not empty it.basket and it.basket.size gt 0}">
                <div class="text-align-right">
                    <a href="/checkout">basket (${it.basket.size})</a>
                </div>
            </c:if>
            
            <c:if test="${stickerMode}">
                <h2>Custom Sticker</h2>
                <p>We will print this photo at 2"×4" and stick it to the envelope!
                Be creative and make it fun. No invoices are posted (it's all online)
                so you can the prints send directly.</p>
            </c:if>
    
            <form action="#" method="get">
                <div class="not-sticker-mode">
                    <h1 id="frame-size-text"></h1>
                    <div data-role="fieldcontain" title="Width of picture frame">
                        <label for="frame-width">Width:</label>
                        <input type="number" id="frame-width" value="4" step="0.1" min="0.1" max="100" data-highlight="true" class="frame-size-input"/>
                    </div>
                    <div data-role="fieldcontain" title="Height of picture frame">
                        <label for="frame-height">Height:</label>
                        <input type="number" id="frame-height" value="2" step="0.1" min="0.1" max="100" data-highlight="true" class="frame-size-input"/>
                    </div>
                    <div data-role="fieldcontain" title="PrintsizeError" id="PrintsizeErrorInches">
                        <p class="error-text">Frame too big. Maximum sizes are 18"×4", or 12"×8"</p>
                    </div>
                    <div data-role="fieldcontain" title="PrintsizeError" id="PrintsizeErrorCm">
                        <p class="error-text">Frame too big. Maximum sizes are 45cm×10cm, or 30cm×20cm</p>
                    </div>
                    
                    <div data-role="fieldcontain" title="Preset">
                        <label for="select-preset">Or use a preset:</label>
                    
                        <select name="select-preset" id="select-preset" >
                            <option value="custom" selected>Custom</option>
                            <option value="canada" >Passport - Canada (50mm × 70mm)</option>
                            <option value="india" >Passport - India (35mm × 35mm)</option>
                            <option value="uk" >Passport - UK (35mm × 45mm)</option>
                            <option value="us" >Passport - US (2" × 2")</option>
                            <option value="6x4" >Standard - 6"×4"</option>
                            <option value="4x6" >Standard - 4"×6"</option>
                            <option value="7x5" >Standard - 7"×5"</option>
                            <option value="5x7" >Standard - 5"×7"</option>
                            <option value="10x8" >Standard - 10"×8"</option>
                            <option value="8x10" >Standard - 8"×10"</option>
                            <option value="12x8" >Standard - 12"×8"</option>
                            <option value="8x12" >Standard - 8"×12"</option>
                            <option value="18x4" >Panoramic - 18"×4"</option>
                            <option value="4x18" >Supertall - 4"×18"</option>
                        </select>
                    </div>
                </div>
                
                <div class="img-preview" id="img-preview">
                    <img id="bg-img-preview" src="" />
                    <img id="img-img-preview" src="" />
                    <img id="crop-img-preview" src="" />
                    <img id="line-img-preview" src="" />
                    <div class="zoom-in-button"></div>
                    <div class="zoom-out-button"></div>
                </div>
                
                <div class="text-align-right not-sticker-mode">
                    <a id="change-picture-link" href="/edit">change picture</a>
                </div>

                <div data-role="collapsible" data-collapsed="true"  data-content-theme="c" >
                    <h3 title="Advanced control of the generated image">Extra options</h3>
                    
                    <div data-role="fieldcontain" class="not-sticker-mode">    
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
                            
                            <input type="radio" name="radio-crop-fit" id="radio-fill" value="FILL" checked="checked"/>
                            <label for="radio-fill" title="Ensure the picture fills the frame, however, some of the picture may not fit in the frame">
                                Fill
                            </label>
    
                            <input type="radio" name="radio-crop-fit" id="radio-fit" value="FIT" />
                            <label for="radio-fit" title="Ensure the picture fits in the frame, however, some margin may be visible in the frame">
                                Fit
                            </label>
                            
                            <c:if test="${not stickerMode}">
                                <input type="radio" name="radio-crop-fit" id="radio-crop" value="CROP"/>
                                <label for="radio-crop" title="Crop parts of the image that are outside the frame" class="not-sticker-mode">
                                    Crop
                                </label>
    
                                <input type="radio" name="radio-crop-fit" id="radio-tile" value="TILE"/>
                                <label for="radio-tile" title="Tile the image"  class="not-sticker-mode">
                                    Tile
                                </label>
                            </c:if>
                        </fieldset>
                    </div>
                    
                    <div data-role="fieldcontain" class="not-sticker-mode">
                        <fieldset data-role="controlgroup" data-type="horizontal" data-mini="true">
                            <legend>Show guidelines:</legend>
                            
                            <input type="radio" name="radio-guides" id="radio-guides-on" value="guides-on" checked="checked" />
                            <label for="radio-guides-on" title="Guidelines are drawn on top of the image">
                                On
                            </label>
                            
                            <input type="radio" name="radio-guides" id="radio-guides-off" value="guides-off"  />
                            <label for="radio-guides-off" title="No guidelines are drawn">
                                Off
                            </label>
                        </fieldset>
                    </div>
                    
                    <div data-role="fieldcontain" class="not-sticker-mode">
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
                    <div data-role="fieldcontain" title="Image offset">
                        <label for="horizontal-offset">Horizontal offset:</label>
                        <input data-mini="true" type="number" id="horizontal-offset" value="0" step="1" data-highlight="true"/>
                        <label for="vertical-offset">Vertical offset:</label>
                        <input data-mini="true" type="number" id="vertical-offset" value="0" step="1" data-highlight="true"/>
                    </div>
                    <div data-role="fieldcontain" title="Tile margin" class="not-sticker-mode">
                        <label for="tile-margin">Tile margin:</label>
                        <span class="span-slider"><input data-mini="true" type="range" name="slider" id="tile-margin" value="0" step="1" min="0" max="200" data-highlight="true"/></span>
                    </div>
                    <div data-role="fieldcontain" title="Zoom factor">
                        <label for="zoom-factor">Zoom factor:</label>
                        <span class="span-slider"><input data-mini="true" type="range" name="slider" id="zoom-factor" value="1" step="0.1" min="0.1" max="10" data-highlight="true"/></span>
                    </div>
                    <div data-role="fieldcontain" title="Background">
                        <label for="background">Background:</label>
                        <input type="text" class="gray" id="background" value="#dddddd" data-highlight="true"/>
                    </div>
                </div>
                
                <c:choose>
                    <c:when test="${not empty it.basket and it.basket.size gt 0}">
                        <a href="#" id="img-upload" data-role="button" data-theme="b">Add to basket</a>
                    </c:when>
                    <c:otherwise>
                        <div class="text-align-right">
                        <a href="#" id="img-download" data-inline="true" data-mini="true">Download</a>
                        <span id="print-size-text" class="not-sticker-mode"></span>
                        or simply <a href="#" id="img-upload" data-role="button" data-inline="true" data-theme="b">Order prints</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </form>
    
            <div data-role="collapsible" data-collapsed="false" id="debugging" style="display:none;" >
                <h3>Debugging</h3>
                <p id="results">results</p>
                
                <p>View <a href="test.html">test results</a>.</p>
                
                <p>Background:</p>
                <canvas id="bgCanvas" width="100" height="100" >
                    Your browser does not support the canvas element.
                </canvas>
                <p>Image:</p>
                <canvas id="imgCanvas" width="100" height="100" >
                </canvas>
                <p>Cropping mask:</p>
                <canvas id="cropCanvas" width="100" height="100">
                </canvas>
                <p>Guidelines:</p>
                <canvas id="lineCanvas" width="100" height="100">
                </canvas>
                <p>Merged image:</p>
                <canvas id="mergedCanvas" width="100" height="100">
                </canvas>
                
            </div>
        </div>
    </div>
       
    <jsp:include page="/WEB-INF/jsp/parts/page-footer.jsp" />
</div>

<script type="text/javascript">

var bgCanvas = document.getElementById("bgCanvas");
var imgCanvas = document.getElementById("imgCanvas");
var cropCanvas = document.getElementById("cropCanvas");
var lineCanvas = document.getElementById("lineCanvas");
var mergedCanvas = document.getElementById("mergedCanvas");
var bgCtx;
var cropCtx;
var imgCtx;
var lineCtx;
var mergedCtx;

var img = new Image();
var frameSize = "";
var horizontalOffset = 0;
var verticalOffset = 0;
    
$(document).ready(function() {
    
    fileChooser();
    
    if (!isSupportedBrowser()) {
        $.mobile.changePage("/upload/basic");
        return;
    }
    
    document.getElementById('files').addEventListener('change', handleFileSelect, false);
    
    bgCtx = bgCanvas.getContext("2d");
    imgCtx = imgCanvas.getContext("2d");
    cropCtx = cropCanvas.getContext("2d");
    lineCtx = lineCanvas.getContext("2d");
    mergedCtx = mergedCanvas.getContext("2d");
    
    $("#sample-photo-link").click(function(e) {
        e.preventDefault();
        if (panoMode()) {
            loadPano();
        } else {
            loadSample();
        }
    });
    
    $("#change-picture-link").click(function(e) {
        e.preventDefault();
        fileChooser();
    });
    
    $(".zoom-in-button").click(function(e) {
        zoomIn();
    });
    $(".zoom-out-button").click(function(e) {
        zoomOut();
    });
    
    img.src = "images/grey.jpg";
    img.onload = function() {
        queueRenderPreview();
    };

    $("input").change(queueRenderPreview);
    $("input").keyup(queueRenderPreview);
    
    $("#img-upload").click(uploadImage);
    $("#img-download").click(uploadDownloadImage);
    $("#select-preset").change(handlePresetSelect);

    var sURL = window.document.URL.toString();  
    if (sURL.indexOf("showCanvas") > 0) {
        $('#debugging').show();
    }
    
    $("#radio-fill,#radio-fit,#radio-crop,#radio-tile,#select-preset,#frame-width,#frame-height").change(resetOffsets);

    $("#img-img-preview").draggable({ 
        start: function(e, ui) {
            $("div.img-preview").css("width", $("#bg-img-preview").width() + "px");
            horizontalOffset = getHorizontalOffset();
            verticalOffset = getVerticalOffset();
        },
        drag: function(e, ui) {
            var topDelta = ui.position.top - ui.originalPosition.top;
            var leftDelta = ui.position.left - ui.originalPosition.left;
            var dragScale = parseInt($("#imgCanvas").attr("height")) / $("#img-img-preview").height();
            topDelta *= dragScale;
            leftDelta *= dragScale;
            $('#horizontal-offset').val(parseInt(horizontalOffset + leftDelta));
            $('#vertical-offset').val(parseInt(verticalOffset + topDelta));
        },
        stop: function(e, ui) {
            queueRenderPreview();
        }
    });
    
    $(window).resize(function() {
        queueRenderPreview();
        repositionImages();
    });
    init();

});

function stickerMode() {
    return "${stickerMode}" == "true";
}

function panoMode() {
    return "${panoMode}" == "true";
}

function zoomIn() {
    zoom(0.1);
}
function zoomOut() {
    zoom(-0.1);
}
function zoom(delta) {
    var zoomFactor = getZoomFactor();
    zoomFactor += delta;
    $('#zoom-factor').val(zoomFactor.toFixed(1));
    $('#zoom-factor').slider('refresh');
    queueRenderPreview();
}

function pan(xDelta, yDelta) {
    $('#horizontal-offset').val(getHorizontalOffset() + xDelta);
    $('#vertical-offset').val(getVerticalOffset() + yDelta);
}

function resetOffsets() {
    $('#horizontal-offset').val(0);
    $('#vertical-offset').val(0);
    $('#zoom-factor').val(1);
    $('#zoom-factor').slider('refresh');
}

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

function loadPano() {
    loadFileUrl("images/pano.jpg");
}

function loadFile(file) {
    var reader = new FileReader();
    reader.onload = function (event) {
        loadFileUrl(event.target.result);
    };
    reader.readAsDataURL(file);
}

function loadFileUrl(url) {
    img.src = url;
    fileChosen();
    resetOffsets();
}

function drawImage(settings) {
    if (getZooming() == 'TILE') {
        drawTiledImage(settings);
    } else {
        drawImageIOSFix(imgCtx, img, settings.sourceX, settings.sourceY, settings.sourceWidth, settings.sourceHeight, settings.destinationX, settings.destinationY, settings.destinationWidth * getZoomFactor(), settings.destinationHeight * getZoomFactor());
    }
}

function drawTiledImage(settings) {
    var tempFrameY = settings.tileMargin;
    while ((tempFrameY + (settings.frameHeightPx - 1)) < settings.canvasHeight) {
        var tempFrameX = settings.tileMargin;
        while ((tempFrameX + (settings.frameWidthPx - 1)) < settings.canvasWidth) {
            drawImageIOSFix(imgCtx, img, settings.sourceX, settings.sourceY, settings.sourceWidth, settings.sourceHeight, tempFrameX, tempFrameY, settings.destinationWidth, settings.destinationHeight);
            tempFrameX += (settings.frameWidthPx - 1) + settings.tileMargin;
        }
        tempFrameY += (settings.frameHeightPx - 1) + settings.tileMargin;
    }
}

function drawGuidelines(settings) {
    if (getZooming() == 'TILE') {
        drawTiledGuidelines(settings);
    } else {
        // top
        drawHorizontalLine(settings.frameY, settings);
        // bottom
        drawHorizontalLine(settings.frameY + settings.frameHeightPx - 1, settings);
        // left
        drawVerticalLine(settings.frameX, settings);
        // right
        drawVerticalLine(settings.frameX + settings.frameWidthPx - 1, settings);
    }
}

function drawTiledGuidelines(settings) {
    var tempFrameY = settings.tileMargin;
    while ((tempFrameY + (settings.frameHeightPx - 1)) < settings.canvasHeight) {
        drawHorizontalLine(tempFrameY, settings);
        tempFrameY += (settings.frameHeightPx - 1);
        drawHorizontalLine(tempFrameY, settings);
        tempFrameY += settings.tileMargin;
    }

    var tempFrameX = settings.tileMargin;
    while ((tempFrameX + (settings.frameWidthPx - 1)) < settings.canvasWidth) {
        drawVerticalLine(tempFrameX, settings);
        tempFrameX += (settings.frameWidthPx - 1);
        drawVerticalLine(tempFrameX, settings);
        tempFrameX += settings.tileMargin;
    }
}

function drawVerticalLine(x, settings) {
    drawLine(x, 0, x, settings.canvasHeight);
}

function drawHorizontalLine(y, settings) {
    drawLine(0, y, settings.canvasWidth, y);
}

function drawLine(x1, y1, x2, y2) {
    lineCtx.beginPath();
    lineCtx.moveTo(x1 + 0.5, y1 + 0.5);
    lineCtx.lineTo(x2 + 0.5, y2 + 0.5);
    lineCtx.stroke();
}

function drawCropMask(canvasWidth, canvasHeight, x1, y1, windowWidth, windowHeight) {
    cropCtx.fillStyle = $("#background").val();
    cropCtx.fillRect(0, 0, canvasWidth, canvasHeight);
    
    // Cutout rectangle
    cropCtx.clearRect(x1, y1, windowWidth, windowHeight);
}

function drawTiledCropMask(canvasWidth, canvasHeight, settings) {
    cropCtx.fillStyle = $("#background").val();;
    cropCtx.fillRect(0, 0, canvasWidth, canvasHeight);
    
    var tempFrameY = settings.tileMargin;
    while ((tempFrameY + (settings.frameHeightPx - 1)) < settings.canvasHeight) {
        var tempFrameX = settings.tileMargin;
        while ((tempFrameX + (settings.frameWidthPx - 1)) < settings.canvasWidth) {
            // Cutout rectangle
            cropCtx.clearRect(tempFrameX, tempFrameY, settings.destinationWidth, settings.destinationHeight);
            tempFrameX += (settings.frameWidthPx - 1) + settings.tileMargin;
        }
        tempFrameY += (settings.frameHeightPx - 1) + settings.tileMargin;
    }
}

function renderPreview() {
    var jpegData = calculate(dpiRender);
    return jpegData;
}

function renderFull() {
    if (detectVerticalSquash(img) != 1) {
        dpiFull = 215;
    }
    var jpegData = calculate(dpiFull);
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
    var jpegData = calculateImpl(dpi);
    
    // Something has gone wrong, try basic mode...
    if (jpegData.length < 1000) {
	    jpegData = null;
        window.location.href = "/empty-image-error";
    }
    return jpegData;    
}

function calculateImpl(dpi) {
    $.mobile.showPageLoadingMsg();
    updateTextAndControls();
    
    var settings = calculateSettings(dpi);

    $("canvas").attr("width", settings.canvasWidth);
    $("canvas").attr("height", settings.canvasHeight);
    
    // Other browsers clear the canvas when setting width, except IE10 so do it manually
    lineCtx.clearRect(0, 0, settings.canvasWidth, settings.canvasHeight);
      
    bgCtx.fillStyle = $("#background").val();
    bgCtx.fillRect(0, 0, settings.canvasWidth, settings.canvasHeight);
    
    var drawGuides = !$("#radio-guides-off").attr('checked');
    
    if (drawGuides) {
        drawGuidelines(settings);
    }
    drawImage(settings);
    if (getZooming() == 'TILE') {
        drawTiledCropMask(settings.canvasWidth, settings.canvasHeight, settings);
        $("#zoom-factor").attr("min", "1");
    } else {
        drawCropMask(settings.canvasWidth, settings.canvasHeight, settings.frameX, settings.frameY, settings.frameWidthPx, settings.frameHeightPx);
        $("#zoom-factor").attr("min", "0.1");        
    }
    
    if ($("#zoom-factor").attr("min") == getZoomFactor()) {
        $(".zoom-out-button").addClass("zoom-off");
    } else {
        $(".zoom-out-button").removeClass("zoom-off");
    }
    if ($("#zoom-factor").attr("max") == $("#zoom-factor").val()) {
        $(".zoom-in-button").addClass("zoom-off");
    } else {
        $(".zoom-in-button").removeClass("zoom-off");
    }
    
    mergedCtx.drawImage(bgCanvas,0,0);
    mergedCtx.drawImage(imgCanvas,0,0);
    
    if (getZooming() == 'CROP' || getZooming() == 'TILE') {
        if ($("#crop-img-preview").next()[0] == $("#img-img-preview")[0]) {
            // only swap layers and repaint if necessary
            $("#crop-img-preview").before($("#img-img-preview"));
            repositionImages();
        }
        mergedCtx.drawImage(cropCanvas,0,0);
    } else {
        if ($("#img-img-preview").next()[0] == $("#crop-img-preview")[0]) {
            // only swap layers and repaint if necessary
            $("#img-img-preview").before($("#crop-img-preview"));
            repositionImages();
        }
    }
    if (drawGuides) {
        mergedCtx.drawImage(lineCanvas,0,0);
    }
    var mergedJpegData = mergedCanvas.toDataURL('image/jpeg');
    
    $("#bg-img-preview").attr("src", bgCanvas.toDataURL('image/png'));
    
    updateImgPreviewSize();
    $('#bg-img-preview').load(function() {
        updateImgPreviewSize();
    });
    
    $("#img-img-preview").attr("src", imgCanvas.toDataURL('image/png'));
    $("#crop-img-preview").attr("src", cropCanvas.toDataURL('image/png'));
    $("#line-img-preview").attr("src", lineCanvas.toDataURL('image/png'));

    $('#img-img-preview, #crop-img-preview, #line-img-preview').load(repositionImages);
    $.mobile.hidePageLoadingMsg();
    
    return mergedJpegData;
}

function updateImgPreviewSize() {
    $("div.img-preview").css("width", "100%");
    $("div.img-preview").css("height", $("#bg-img-preview").height() + "px");
}

function repositionImages() {
    $("#img-img-preview, #crop-img-preview, #line-img-preview").position({
      my: "left top",
      at: "left top",
      of: "#bg-img-preview"
    });
    repositionZoomControls();
}

function repositionZoomControls() {
    $(".zoom-in-button").position({
        my: "left+10px top+10px",
        at: "left top",
        of: "#img-preview",
        within: "#img-preview"
    });
    $(".zoom-out-button").position({
        my: "left top+10px",
        at: "left bottom",
        of: ".zoom-in-button",
        within: "#img-preview"
    });
}

function calculateSettings(dpi) {
    var dpiFactor = (dpi/100);
    var hOffset = getHorizontalOffset() * dpiFactor;
    var vOffset = getVerticalOffset() * dpiFactor;

    var settings = calculatePrintSize(getFrameWidthInInches(), getFrameHeightInInches(), getOrientation());
    settings.tileMargin = $("#tile-margin").val() * dpiFactor;
    settings = calculateCanvasSize(settings.printWidth, settings.printHeight, dpi, settings);
    settings = calculateFramePixelSize(getFrameWidthInInches(), getFrameHeightInInches(), dpi, settings);
    settings = calculateFrameXY(settings.canvasWidth, settings.canvasHeight, settings.frameWidthPx, settings.frameHeightPx, settings);
    settings = calculateDestination(getZooming(), settings.frameWidthPx, settings.frameHeightPx, settings.frameX, settings.frameY, img.width, img.height, hOffset, vOffset, settings);

    return settings;
}

function uploadImage() {
    $.mobile.showPageLoadingMsg();
    var t=setTimeout("uploadImageImpl()", 50);
}

function uploadImageImpl() {
    
    var settings = calculateSettings(dpiFull);
    var imageDataValue = renderFull();
    
    // show spinner again while uploading
    $.mobile.showPageLoadingMsg();
    
    $.post("/upload",
       { imageData: imageDataValue,
         frameSize: frameSizeString(),
         printWidth: settings.printWidth,
         printHeight: settings.printHeight,
         stickerMode: stickerMode()
       }
    )
    .success(
            function() { 
                window.location.href = "/checkout";
                $.mobile.hidePageLoadingMsg();
            }
    )
    .error(
            function() { 
                window.location.href = "/error?message=Failed+to+upload+image.";
                $.mobile.hidePageLoadingMsg();
            }
    );
}

function uploadDownloadImage() {
    $.mobile.showPageLoadingMsg();
    var t=setTimeout("uploadDownloadImageImpl()", 50);
}

function uploadDownloadImageImpl() {
    
    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", "/upload/download");

    var hiddenField = document.createElement("input");
    hiddenField.setAttribute("type", "hidden");
    hiddenField.setAttribute("name", "imageData");
    hiddenField.setAttribute("value", renderFull());
    
    // continue to show loading message
    $.mobile.showPageLoadingMsg();
    
    form.appendChild(hiddenField);

    document.body.appendChild(form);
    form.submit();
    $.mobile.hidePageLoadingMsg();
}

</script>
</body>
</html>
