var cmToInches = 0.393700787;
var dpiRender = 100;
var dpiFull = 300;

function calculatePrintSize(frameWidthInInches, frameHeightInInches, orientation) {
	var settings = new Object();
	
	if (!orientation || orientation === "AUTO") {
		if (frameWidthInInches < frameHeightInInches) {
			orientation = "PORTRAIT";
		} else {
			orientation = "LANDSCAPE";
		}
	}
	
	if (orientation === "PORTRAIT") {
		settings.printWidth = 4;
		settings.printHeight = 6;
		if (frameWidthInInches > settings.printWidth || frameHeightInInches > settings.printHeight) {
			settings.printWidth = 5;
			settings.printHeight = 7;
		}
		if (frameWidthInInches > settings.printWidth || frameHeightInInches > settings.printHeight) {
			settings.printWidth = 8;
			settings.printHeight = 10;
		}
		if (frameWidthInInches > settings.printWidth || frameHeightInInches > settings.printHeight) {
			settings.printWidth = 8;
			settings.printHeight = 12;
		}
	} else {
		settings.printWidth = 6;
		settings.printHeight = 4;
		if (frameWidthInInches > settings.printWidth || frameHeightInInches > settings.printHeight) {
			settings.printWidth = 7;
			settings.printHeight = 5;
		}
		if (frameWidthInInches > settings.printWidth || frameHeightInInches > settings.printHeight) {
			settings.printWidth = 10;
			settings.printHeight = 8;
		}
		if (frameWidthInInches > settings.printWidth || frameHeightInInches > settings.printHeight) {
			settings.printWidth = 12;
			settings.printHeight = 8;
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

function calculateDestination(zooming, frameWidthPx, frameHeightPx, frameX, frameY, imageWidth, imageHeight, settings) {
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
			break;
		case ('FILL') :
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
			break;
		case ('CROP') :
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
    return frameWidth + (isInches? '"' : '') + 'x' + frameHeight + (isInches? '"' : 'cm');
}

function getFrameUnits() {
    return $('input:radio[name=radio-frame-units]:checked').val();
}

function updateFrameHeader() {
    $("#frame-size-text").html('Enter your frame size (' + getFrameUnits() + ')');
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

function restrictSliders() {
    var maxFrameWidth = 10;
    var maxFrameHeight = 8;
    if (isImageCurrentlyPortrait()) {
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
    $("#print-size-text").html("Print at " + settings.printWidth + "\"x" + settings.printHeight + "\"");
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