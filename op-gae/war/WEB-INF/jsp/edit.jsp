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
                    <label for="frame-width">Width:</label>
                    <span class="span-slider"><input type="range" name="slider" id="frame-width" value="4" step="0.1" min="0.1" max="10" data-highlight="true"/></span>
                </div>
                <div data-role="fieldcontain" title="Height of picture frame">
                    <label for="frame-height">Height:</label>
                    <span class="span-slider"><input type="range" name="slider" id="frame-height" value="2" step="0.1" min="0.1" max="8" data-highlight="true"/></span>
                </div>
                
                <div data-role="fieldcontain" title="Preset">
                    <label for="select-preset">Or use a preset:</label>
                
                    <select name="select-preset" id="select-preset" >
                        <option value="custom" selected>Custom</option>
                        <option value="canada" >Passport - Canada (50mm × 70mm)</option>
                        <option value="india" >Passport - India (35mm × 35mm)</option>
                        <option value="uk" >Passport - UK (35mm × 45mm)</option>
                        <option value="us" >Passport - US (2" × 2")</option>
                    </select>
                </div>
                
                <div class="img-preview" id="img-preview">
                    <img id="bg-img-preview" src="" />
                    <img id="img-img-preview" src="" />
                    <img id="crop-img-preview" src="" />
                    <img id="line-img-preview" src="" />
                    <div class="zoom-in-button"></div>
                    <div class="zoom-out-button"></div>
                </div>
                
                <div class="text-align-right">
                    <a id="change-picture-link" href="/edit">change picture</a>
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
                            
                            <input type="radio" name="radio-crop-fit" id="radio-fill" value="FILL" checked="checked"/>
                            <label for="radio-fill" title="Ensure the picture fills the frame, however, some of the picture may not fit in the frame">
                                Fill
                            </label>
    
                            <input type="radio" name="radio-crop-fit" id="radio-fit" value="FIT" />
                            <label for="radio-fit" title="Ensure the picture fits in the frame, however, some margin may be visible in the frame">
                                Fit
                            </label>
                                                    
                            <input type="radio" name="radio-crop-fit" id="radio-crop" value="CROP" />
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
                    <div data-role="fieldcontain" title="Image offset">
                        <label for="horizontal-offset">Horizontal offset:</label>
                        <span class="span-slider"><input data-mini="true" type="range" name="slider" id="horizontal-offset" value="0" step="1" min="-300" max="300" data-highlight="true"/></span>
                        <label for="vertical-offset">Vertical offset:</label>
                        <span class="span-slider"><input data-mini="true" type="range" name="slider" id="vertical-offset" value="0" step="1" min="-200" max="200" data-highlight="true"/></span>
                    </div>
                    <div data-role="fieldcontain" title="Zoom factor">
                        <label for="zoom-factor">Zoom factor:</label>
                        <span class="span-slider"><input data-mini="true" type="range" name="slider" id="zoom-factor" value="1" step="0.1" min="0.1" max="10" data-highlight="true"/></span>
                    </div>
                </div>
                
                <c:choose>
                    <c:when test="${not empty it.basket and it.basket.size gt 0}">
                        <a href="#" id="img-upload" data-role="button" data-theme="b">Add to basket</a>
                    </c:when>
                    <c:otherwise>
                        <h2 id="print-size-text"></h2>
                        <a href="#" id="img-download" data-role="button" data-theme="b">Download</a>
                        Or just <a href="#" id="img-upload" data-theme="b">order prints from us</a>.
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
var tileMargin = 10;
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
        loadSample();
    });
    
    $("#change-picture-link").click(function(e) {
        e.preventDefault();
        fileChooser();
    });
    
    $(".zoom-in-button").click(function(e) {
        var zoomFactor = getZoomFactor();
        zoomFactor += 0.1;
        $('#zoom-factor').val(zoomFactor.toFixed(1));
        $('#zoom-factor').slider('refresh');
        queueRenderPreview();
    });
    $(".zoom-out-button").click(function(e) {
        var zoomFactor = getZoomFactor();
        zoomFactor -= 0.1;
        $('#zoom-factor').val(zoomFactor.toFixed(1));
        $('#zoom-factor').slider('refresh');
        queueRenderPreview();
    });
    
    img.src = "images/grey.jpg";
    img.onload = function() {
        queueRenderPreview();
    };

    $("input").click(queueRenderPreview);
    $("input, .span-slider").change(queueRenderPreview);
    $("input").keyup(queueRenderPreview);
    $("#radio-cm, #radio-inches").change(renderPreview);
    
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
            horizontalOffset = parseInt($('#horizontal-offset').val());
            verticalOffset = parseInt($('#vertical-offset').val());
        },
        drag: function(e, ui) {
            var topDelta = ui.position.top - ui.originalPosition.top;
            var leftDelta = ui.position.left - ui.originalPosition.left;
            var dragScale = parseInt($("#imgCanvas").attr("height")) / $("#img-img-preview").height();
            topDelta *= dragScale;
            leftDelta *= dragScale;
            $('#horizontal-offset').val(horizontalOffset + leftDelta);
            $('#vertical-offset').val(verticalOffset + topDelta);
        },
        stop: function(e, ui) {
            var topDelta = ui.position.top - ui.originalPosition.top;
            var leftDelta = ui.position.left - ui.originalPosition.left;
            
            var dragScale = parseInt($("#imgCanvas").attr("height")) / $("#img-img-preview").height();
            topDelta *= dragScale;
            leftDelta *= dragScale;
            
            horizontalOffset += leftDelta;
            verticalOffset += topDelta;
            
            $('#horizontal-offset').val(horizontalOffset);
            $('#vertical-offset').val(verticalOffset);
            $('#horizontal-offset, #vertical-offset').slider('refresh');
        }
    });
    
    $(window).resize(function() {
        queueRenderPreview();
        repositionImages();
    });
    
});

function resetOffsets() {
    $('#horizontal-offset').val(0);
    $('#vertical-offset').val(0);
    $('#zoom-factor').val(1);
    $('#horizontal-offset, #vertical-offset, #zoom-factor').slider('refresh');
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
        imgCtx.drawImage(img, settings.sourceX, settings.sourceY, settings.sourceWidth, settings.sourceHeight, settings.destinationX, settings.destinationY, settings.destinationWidth * getZoomFactor(), settings.destinationHeight * getZoomFactor());
    }
}

function drawTiledImage(settings) {
    var tempFrameY = settings.tileMargin;
    while ((tempFrameY + (settings.frameHeightPx - 1)) < settings.canvasHeight) {
        var tempFrameX = settings.tileMargin;
        while ((tempFrameX + (settings.frameWidthPx - 1)) < settings.canvasWidth) {
            imgCtx.drawImage(img, settings.sourceX, settings.sourceY, settings.sourceWidth, settings.sourceHeight, tempFrameX, tempFrameY, settings.destinationWidth, settings.destinationHeight);
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
    lineCtx.moveTo(x1 + 0.5, y1 + 0.5);
    lineCtx.lineTo(x2 + 0.5, y2 + 0.5);
    lineCtx.stroke();
}

function drawCropMask(canvasWidth, canvasHeight, x1, y1, windowWidth, windowHeight) {
    cropCtx.fillStyle="#dddddd";
    cropCtx.fillRect(0, 0, canvasWidth, canvasHeight);
    
    // Cutout rectangle
    cropCtx.clearRect(x1, y1, windowWidth, windowHeight);
}

function drawTiledCropMask(canvasWidth, canvasHeight, settings) {
    cropCtx.fillStyle="#dddddd";
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
    var jpegData = calculate(dpiFull);
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
    updateTextAndControls();
    
    var settings = calculateSettings(dpi);
    $("canvas").attr("width", settings.canvasWidth);
    $("canvas").attr("height", settings.canvasHeight);
      
    bgCtx.fillStyle="#dddddd";
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
    
    $("#horizontal-offset").attr("max", $("#bg-img-preview").width() * getZoomFactor());
    $("#horizontal-offset").attr("min", -$("#bg-img-preview").width() * getZoomFactor());
    $("#vertical-offset").attr("max", $("#bg-img-preview").height() * getZoomFactor());
    $("#vertical-offset").attr("min", -$("#bg-img-preview").height() * getZoomFactor());
    
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
    settings.tileMargin = tileMargin * dpiFactor;
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
    
    $.post("/upload",
       { imageData: renderFull(),
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
                window.location.href = "/error?message=Failed+to+upload+image.";
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

    form.appendChild(hiddenField);

    document.body.appendChild(form);
    form.submit();
    $.mobile.hidePageLoadingMsg();
}

</script>
</body>
</html>
