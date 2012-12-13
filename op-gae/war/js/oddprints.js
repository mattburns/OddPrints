var cmToInches = 0.393700787;
// if you change these, consider tileMargin...
var dpiRender = 100;
// this is overridden in edit-basic due to max image constraints
var dpiFull = 300;

var availableSizes = [{w:6, h:4},
                      {w:7, h:5},
                      {w:10, h:8},
                      {w:12, h:8},
                      {w:18, h:4}];

function init() {
    $("#background").miniColors({
        change: function(hex, rgba) {
            queueRenderPreview();
        }
    });
    
    if (stickerMode()) {
        availableSizes = [{w:4, h:2}];
        $(".not-sticker-mode").hide();
        $('#frame-width').val("4");
        $('#frame-height').val("2");
        $('#radio-iches').attr('checked', true);
        $('#radio-fill').attr('checked', true);
        $('#radio-orient-landscape').attr('checked', true);
        $('#radio-guides-off').attr('checked', true);
        $("input[type='radio']").checkboxradio("refresh");
        $('#tile-margin').val("0");
    }
    if (panoMode()) {
        $("#select-preset").val('18x4').change();
    }
}

function calculatePrintSize(frameWidthInInches, frameHeightInInches, orientation) {
    var settings = new Object();
    
    if (!orientation || orientation === "AUTO") {
        if (frameWidthInInches < frameHeightInInches) {
            orientation = "PORTRAIT";
        } else {
            orientation = "LANDSCAPE";
        }
    }

    for (var i = 0 ; i < availableSizes.length ; i++) {
        var w = availableSizes[i].w;
        var h = availableSizes[i].h;
        if (orientation === "PORTRAIT") { // swap 'em
            w = availableSizes[i].h;
            h = availableSizes[i].w;
        }
        if (frameWidthInInches <= w && frameHeightInInches <= h) {
            // we done
            settings.printWidth = w;
            settings.printHeight = h;
            break;
        }
    }

    return settings;
}

function calculateCanvasSize(printWidth, printHeight, dpi, settings) {
    if (!settings) {
        settings = new Object();
    }
    
    settings.canvasWidth = printWidth * dpi;
    settings.canvasHeight = printHeight * dpi;
    
    return settings;
}

function calculateFramePixelSize(frameWidthInInches, frameHeightInInches, dpi, settings) {
    if (!settings) {
        settings = new Object();
    }
    
    settings.frameWidthPx = Math.floor(frameWidthInInches * dpi);
    settings.frameHeightPx = Math.floor(frameHeightInInches * dpi);
    
    return settings;
}

function calculateFrameXY(canvasWidth, canvasHeight, frameWidthPx, frameHeightPx, settings) {
    if (!settings) {
        settings = new Object();
    }
    
    settings.frameX = Math.floor((canvasWidth - frameWidthPx) / 2);
    settings.frameY = Math.floor((canvasHeight - frameHeightPx) / 2);
    
    return settings;
}

function forceNewWidth(width, imageWidth, imageHeight, settings) {
    if (!settings) {
        settings = new Object();
    }
    
    settings.destinationWidth = width;
    settings.destinationHeight = Math.floor(imageHeight / (imageWidth/width));
    
    return settings;
}

function forceNewHeight(height, imageWidth, imageHeight, settings) {
    if (!settings) {
        settings = new Object();
    }
    
    settings.destinationHeight = height;
    settings.destinationWidth = Math.floor(imageWidth / (imageHeight/height));
    
    return settings;
}

function calculateDestination(zooming, frameWidthPx, frameHeightPx, frameX, frameY, imageWidth, imageHeight, horizontalOffset, verticalOffset, settings) {
    if (!settings) {
        settings = new Object();
    }
    
    var frameHasWiderRatioThanImage = (imageWidth/imageHeight) < (frameWidthPx/frameHeightPx);
        
    switch (zooming) {
        case ('FIT') :
            if (frameHasWiderRatioThanImage) {
                settings = forceNewHeight(frameHeightPx, imageWidth, imageHeight, settings);
                settings.destinationY = frameY;
                settings.destinationX = frameX + Math.floor((frameWidthPx - settings.destinationWidth) / 2);
            } else {
                settings = forceNewWidth(frameWidthPx, imageWidth, imageHeight, settings);
                settings.destinationY = frameY + Math.floor((frameHeightPx - settings.destinationHeight) / 2);
                settings.destinationX = frameX;
            }
            settings.sourceX = 0;
            settings.sourceY = 0;
            settings.sourceWidth = imageWidth;
            settings.sourceHeight = imageHeight;
            
            settings.destinationX += horizontalOffset;
            settings.destinationY += verticalOffset;
            
            break;
        case ('FILL') :
        case ('CROP') :
            if (frameHasWiderRatioThanImage) {
                settings = forceNewWidth(frameWidthPx, imageWidth, imageHeight, settings);
                settings.destinationY = Math.floor(frameY - ((settings.destinationHeight - frameHeightPx) / 2));
                settings.destinationX = frameX;
            } else {
                settings = forceNewHeight(frameHeightPx, imageWidth, imageHeight, settings);
                settings.destinationX = Math.floor(frameX - ((settings.destinationWidth - frameWidthPx) / 2));
                settings.destinationY = frameY;
            }
            settings.sourceX = 0;
            settings.sourceY = 0;
            settings.sourceWidth = imageWidth;
            settings.sourceHeight = imageHeight;
            
            settings.destinationX += horizontalOffset;
            settings.destinationY += verticalOffset;
            
            break;
        case ('TILE') :
            if (frameHasWiderRatioThanImage) {
                settings.sourceWidth = imageWidth;
                settings.sourceHeight = Math.floor((imageWidth * frameHeightPx) / frameWidthPx);
                settings.sourceX = 0;
                settings.sourceY = Math.floor((imageHeight - settings.sourceHeight) / 2);
            } else {
                settings.sourceHeight = imageHeight;
                settings.sourceWidth = Math.floor((imageHeight * frameWidthPx) / frameHeightPx);
                settings.sourceX = Math.floor((imageWidth - settings.sourceWidth) / 2);
                settings.sourceY = 0;
            }
            settings.destinationHeight = frameHeightPx;
            settings.destinationWidth = frameWidthPx;
            settings.destinationX = frameX;
            settings.destinationY = frameY;
                        
            var scaleRatio = (settings.sourceWidth / settings.destinationWidth) / getZoomFactor();
            settings.sourceX -= horizontalOffset * scaleRatio;
            settings.sourceY -= verticalOffset * scaleRatio;

            settings.sourceWidth = settings.sourceWidth / getZoomFactor();
            settings.sourceHeight = settings.sourceHeight / getZoomFactor();
            
            if (settings.sourceX < 0) {
                var excess = settings.sourceX / scaleRatio;
                $('#horizontal-offset').val(horizontalOffset + excess);
                settings.destinationX -= excess;
                settings.sourceX = 0;
                repositionImages();
            }
            if (settings.sourceY < 0) {
                var excess = settings.sourceY / scaleRatio;
                $('#vertical-offset').val(verticalOffset + excess);
                settings.destinationY -= excess;
                settings.sourceY = 0;
                repositionImages();
            }
            var extraX = imageWidth - settings.sourceWidth;
            if (settings.sourceX > extraX) {
                var excess = (settings.sourceX - extraX) / scaleRatio;
                $('#horizontal-offset').val(horizontalOffset + excess);
                settings.destinationX -= excess;
                settings.sourceX = extraX;
                repositionImages();
            }
            var extraY = imageHeight - settings.sourceHeight;
            if (settings.sourceY > extraY) {
                var excess = (settings.sourceY - extraY) / scaleRatio;
                $('#vertical-offset').val(verticalOffset + excess);
                settings.destinationY -= excess;
                settings.sourceY = extraY;
                repositionImages();
            }

            break;
    }
    
    return settings;
}

function getZooming() {
    return $('input:radio[name=radio-crop-fit]:checked').val();
}

function getOrientation() {
    return $('input:radio[name=radio-orient]:checked').val();
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
    return frameWidth + (isInches? '"' : '') + '×' + frameHeight + (isInches? '"' : 'cm');
}

function getFrameUnits() {
    return $('input:radio[name=radio-frame-units]:checked').val();
}

function updateFrameHeader() {
    $("#frame-size-text").html('Enter your frame size (' + getFrameUnits() + ')');
}

function getHorizontalOffset() {
    return parseFloat($("#horizontal-offset").val());
}

function getVerticalOffset() {
    return parseFloat($("#vertical-offset").val());
}

function getZoomFactor() {
    return parseFloat($("#zoom-factor").val()) || 1;
}

function getFrameWidth() {
    var width = parseFloat($("#frame-width").val());
    if (isNaN(width) || width <= 0) {
        width = 0.1;
        $("#frame-width").val(width);
    }
    return width;
}

function getFrameHeight() {
    var height = parseFloat($("#frame-height").val());
    if (isNaN(height) || height <= 0) {
        height = 0.1;
        $("#frame-height").val(height);
    }
    return height;
}

function getFrameWidthString() {
    return getFrameWidth().toFixed(1);
}

function getFrameHeightString() {
    return getFrameHeight().toFixed(1);
}

function getFrameWidthInInches() {
    var frameWidthInInches = getFrameWidth();
    if (getFrameUnits() === "cm") {
        frameWidthInInches = toInches(frameWidthInInches);
    }
    return frameWidthInInches;

}

function getFrameHeightInInches() {
    var frameHeightInInches = getFrameHeight();
    if (getFrameUnits() === "cm") {
        frameHeightInInches = toInches(frameHeightInInches);
    }
    return frameHeightInInches;
}

function isImageCurrentlyPortrait() {
    var img = $("#img-preview");
    var width = img.clientWidth;
    var height = img.clientHeight;
}

function checkPrintsizeAvailable(frameWidthInInches, frameHeightInInches, orientation) {
    
    var settings = calculatePrintSize(frameWidthInInches, frameHeightInInches, orientation);
    
    if (!settings.printWidth) {
        if (getFrameUnits() === "cm") {
            $("#PrintsizeErrorCm").show();
        } else {
            $("#PrintsizeErrorInches").show();
        }
    } else {
        $("#PrintsizeErrorInches").hide();
        $("#PrintsizeErrorCm").hide();
    }
}

function restrictSliders() {
    var max = 0;

    for (var i = 0 ; i < availableSizes.length ; i++) {
        max = Math.max(max, availableSizes[i].h);
        max = Math.max(max, availableSizes[i].w);
    }
    
    var visibleMax = max;
    
    if (getFrameUnits() === "cm") {
        visibleMax = toCm(max);
    }
    
    $("#frame-width").attr("max", visibleMax);
    $("#frame-height").attr("max", visibleMax);
    
    if (getFrameWidth() > visibleMax) {
        $("#frame-width").val(visibleMax);
    }
    if (getFrameHeight() > visibleMax) {
        $("#frame-height").val(visibleMax);
    }
}

var renderDelay = 100;
var renderTimeoutId = 0;

// if there are multiple call in quick succession, only that last call does the real work
function queueRenderPreview() {
    $.mobile.showPageLoadingMsg();
    $("#error-loading-preview").hide();
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

function updateTextAndControls() {
    updateFrameHeader();
    var settings = calculatePrintSize(getFrameWidthInInches(), getFrameHeightInInches(), getOrientation());
    if (settings.printWidth) {
        $("#print-size-text").html(" and print yourself at <span class='output-size'>" + settings.printWidth + "\"×" + settings.printHeight + "\"</span>");
    }
    checkPrintsizeAvailable(getFrameWidthInInches(), getFrameHeightInInches(), getOrientation());
    restrictSliders();
}

function isSupportedBrowser() {
    return !!window.FileReader && Modernizr.canvas && isFileInputSupported();
}

function isFileInputSupported() {
    var el = document.createElement("input");
    el.setAttribute("type", "file");
    return !el.disabled;
}

function handlePresetSelect(evt) {
    switch ($("#select-preset option:selected").val()) {
        case ('canada') :
            $('#frame-width').val("5");
            $('#frame-height').val("7");
            $('#radio-cm').attr('checked', true);
            $('#radio-tile').attr('checked', true);
            $('#radio-orient-landscape').attr('checked', true);
            $('#radio-guides-off').attr('checked', true);
            $("input[type='radio']").checkboxradio("refresh");
            $('#tile-margin').val("50");
            break;
        case ('india') :
            $('#frame-width').val("3.5");
            $('#frame-height').val("3.5");
            $('#radio-cm').attr('checked', true);
            $('#radio-tile').attr('checked', true);
            $('#radio-orient-landscape').attr('checked', true);
            $('#radio-guides-off').attr('checked', true);
            $("input[type='radio']").checkboxradio("refresh");
            $('#tile-margin').val("11");
            break;
        case ('uk') :
            $('#frame-width').val("3.5");
            $('#frame-height').val("4.5");
            $('#radio-cm').attr('checked', true);
            $('#radio-tile').attr('checked', true);
            $('#radio-orient-landscape').attr('checked', true);
            $('#radio-guides-off').attr('checked', true);
            $("input[type='radio']").checkboxradio("refresh");
            $('#tile-margin').val("11");
            break;
        case ('us') :
            $('#frame-width').val("2");
            $('#frame-height').val("2");
            $('#radio-inches').attr('checked', true);
            $('#radio-tile').attr('checked', true);
            $('#radio-orient-landscape').attr('checked', true);
            $('#radio-guides-off').attr('checked', true);
            $("input[type='radio']").checkboxradio("refresh");
            $('#tile-margin').val("65");
            break;
        case ('custom') :
            $('#frame-width').val("4");
            $('#frame-height').val("2");
            $('#radio-inches').attr('checked', true);
            $('#radio-fill').attr('checked', true);
            $('#radio-orient-auto').attr('checked', true);
            $('#radio-guides-on').attr('checked', true);
            $("input[type='radio']").checkboxradio("refresh");
            $('#tile-margin').val("0");
            break;
        default :
            // handle options of form: "6x4"
            var wh = $("#select-preset option:selected").val().split("x");
            $('#frame-width').val(wh[0]);
            $('#frame-height').val(wh[1]);
            standardPrintDefaults();
            break;
    }
    $("#frame-width, #frame-height").slider("refresh"); // this will also trigger render
}

function standardPrintDefaults() {
    $('#radio-inches').attr('checked', true);
    $('#radio-fill').attr('checked', true);
    $('#radio-orient-auto').attr('checked', true);
    $('#radio-guides-off').attr('checked', true);
    $("input[type='radio']").checkboxradio("refresh");
    $('#tile-margin').val("0");
}
