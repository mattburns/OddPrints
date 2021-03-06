var cmToInches = 0.393700787;
// if you change these, consider tileMargin...
var dpiRender = 100;
// this is overridden in edit-basic due to max image constraints
// it is also overridden if the device exhibits the iOS squash bug
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
    
    restoreSettings();
    
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
    if (isNaN(width) || width < 0.1) {
        if (width < 0.1 && $("#frame-width").val() != "0.") {
            $("#frame-width").val(0.1);
        }
        width = 0.1;
    }
    return width;
}

function getFrameHeight() {
    var height = parseFloat($("#frame-height").val());
    if (isNaN(height) || height < 0.1) {
        if (height < 0.1 && $("#frame-height").val() != "0.") {
            $("#frame-height").val(0.1);
        }
        height = 0.1;
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

function printsizeAvailable(frameWidthInInches, frameHeightInInches, orientation) {
    
    var settings = calculatePrintSize(frameWidthInInches, frameHeightInInches, orientation);
    
    if (!settings.printWidth) {
        if (getFrameUnits() === "cm") {
            $("#PrintsizeErrorInches").hide();
            $("#PrintsizeErrorCm").show();
        } else {
            $("#PrintsizeErrorInches").show();
            $("#PrintsizeErrorCm").hide();
        }
        $("#img-preview").hide();
        return false;
    } else {
        $("#PrintsizeErrorInches").hide();
        $("#PrintsizeErrorCm").hide();
        $("#img-preview").show();
        return true;
    }
}

var renderDelay = 100;
var renderTimeoutId = 0;

// if there are multiple call in quick succession, only that last call does the real work
function queueRenderPreview() {
    $.mobile.showPageLoadingMsg();
    $("#error-loading-preview").hide();
    updateFrameHeader();    
    clearTimeout(renderTimeoutId);
    
    renderTimeoutId = window.setTimeout(
        function() {
            saveSettings();
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
    printsizeAvailable(getFrameWidthInInches(), getFrameHeightInInches(), getOrientation());
}

function isSupportedBrowser() {
    return !!window.FileReader && Modernizr.canvas && isFileInputSupported() && canRenderLargeCanvasesToJpeg();
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
    queueRenderPreview();
}

function standardPrintDefaults() {
    $('#radio-inches').attr('checked', true);
    $('#radio-fill').attr('checked', true);
    $('#radio-orient-auto').attr('checked', true);
    $('#radio-guides-off').attr('checked', true);
    $("input[type='radio']").checkboxradio("refresh");
    $('#tile-margin').val("0");
}

function saveSettings() {
    if (typeof (window.localStorage) != "undefined") {
        var form = $("#settings-form");
        var settings = {};
        $(":input", form).each(function(index, value){
            if ($(value).attr("type") == "radio" && !$(value).prop("checked")) {
                // Radio not checked? Don't save
            } else {
                settings[value.id] = value.value;
            }
        });
        var data = JSON.stringify(settings);
        localStorage.setItem("settings", data);
    }
}

function restoreSettings() {
    if (typeof (window.localStorage) != "undefined") {
        var settings = localStorage.getItem("settings");
        if (settings) {
            settings = JSON.parse(settings);
            
            var form = $("#settings-form");
            $(":input", form).each(function(index, value){
                if ($(value).attr("type") == "radio") {
                    if (settings[value.id]) {
                        $(value).prop('checked', true);
                    }
                } else {
                    $(value).val(settings[value.id]);
                }
            });
            $('select').selectmenu('refresh');
            $('input[type=radio]').checkboxradio("refresh");
        }
    }
}

/**
 * Detecting vertical squash in loaded image.
 * Fixes a bug which squash image vertically while drawing into canvas for some images.
 * This is a bug in iOS6 devices. This function from https://github.com/stomita/ios-imagefile-megapixel
 * 
 */
function detectVerticalSquash(img) {
    var iw = img.naturalWidth, ih = img.naturalHeight;
    var canvas = document.createElement('canvas');
    canvas.width = 1;
    canvas.height = ih;
    var ctx = canvas.getContext('2d');
    ctx.drawImage(img, 0, 0);
    var data = ctx.getImageData(0, 0, 1, ih).data;
    // search image edge pixel position in case it is squashed vertically.
    var sy = 0;
    var ey = ih;
    var py = ih;
    while (py > sy) {
        var alpha = data[(py - 1) * 4 + 3];
        if (alpha === 0) {
            ey = py;
        } else {
            sy = py;
        }
        py = (ey + sy) >> 1;
    }
    var ratio = (py / ih);
    return (ratio===0)?1:ratio;
}

function canRenderLargeCanvasesToJpeg() {
    var canvas = document.createElement('canvas');
    canvas.width = 2500;
    canvas.height = 2500;
    return canvas.toDataURL('image/jpeg').length > 10;
}

/**
 * A replacement for context.drawImage
 * (args are for source and destination).
 */
function drawImageIOSFix(ctx, img, sx, sy, sw, sh, dx, dy, dw, dh) {
    var vertSquashRatio = detectVerticalSquash(img);
    ctx.drawImage(img, sx, sy, sw, sh, dx, dy, dw, dh / vertSquashRatio);
}
