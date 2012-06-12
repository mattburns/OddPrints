
function calculatePrintSize(frameWidthInInches, frameHeightInInches, orientation) {
	var settings = new Object();
	
	if (!orientation || orientation === "auto") {
		if (frameWidthInInches < frameHeightInInches) {
			orientation = "portrait";
		} else {
			orientation = "landscape";
		}
	}
	
	if (orientation === "portrait") {
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
		case ('fit') :
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
		case ('fill') :
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
		case ('crop') :
		
			if (frameHasWiderRatioThanImage) {
				settings.sourceWidth = imageWidth;
				settings.sourceHeight = Math.floor(imageWidth * (frameHeightPx/frameWidthPx));
				settings.sourceX = 0;
				settings.sourceY = Math.floor((imageHeight - settings.sourceHeight) / 2);
			} else {
				settings.sourceHeight = imageHeight;
				settings.sourceWidth = Math.floor(imageHeight * (frameWidthPx/frameHeightPx));
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
