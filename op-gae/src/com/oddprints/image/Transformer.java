package com.oddprints.image;

import com.google.appengine.api.images.Image;
import com.oddprints.PrintSize;
import com.oddprints.image.TransformSettings.Builder;
import com.oddprints.image.TransformSettings.Orientation;
import com.oddprints.image.TransformSettings.Zooming;

public class Transformer {

    public TransformSettings calculateSettings(Image image, int dpi,
            double frameWidthInInches, double frameHeightInInches,
            Zooming zooming, Orientation orientation) {
        TransformSettings settings = calculatePrintSize(frameWidthInInches,
                frameHeightInInches, orientation);
        settings = calculateCanvasSize(settings.getPrintWidth(),
                settings.getPrintHeight(), dpi, settings);
        settings = calculateFramePixelSize(frameWidthInInches,
                frameHeightInInches, dpi, settings);
        settings = calculateFrameXY(settings.getCanvasWidth(),
                settings.getCanvasHeight(), settings.getFrameWidthPx(),
                settings.getFrameHeightPx(), settings);
        settings = calculateDestination(zooming, settings.getFrameWidthPx(),
                settings.getFrameHeightPx(), settings.getFrameX(),
                settings.getFrameY(), image.getWidth(), image.getHeight(),
                settings);
        return settings;
    }

    TransformSettings calculatePrintSize(double frameWidthInInches,
            double frameHeightInInches) {
        return calculatePrintSize(frameWidthInInches, frameHeightInInches,
                Orientation.AUTO);
    }

    TransformSettings calculatePrintSize(double frameWidthInInches,
            double frameHeightInInches, Orientation orientation) {

        int printWidth = 0;
        int printHeight = 0;

        if (orientation == Orientation.AUTO) {
            if (frameWidthInInches < frameHeightInInches) {
                orientation = Orientation.PORTRAIT;
            } else {
                orientation = Orientation.LANDSCAPE;
            }
        }

        for (PrintSize size : PrintSize.values()) {
            int w;
            int h;
            if (orientation == Orientation.PORTRAIT) { // swap 'em
                w = size.getHeight();
                h = size.getWidth();
            } else {
                w = size.getWidth();
                h = size.getHeight();
            }
            if (frameWidthInInches <= w && frameHeightInInches <= h) {
                // we done
                printWidth = w;
                printHeight = h;
                break;
            }
        }

        return new Builder()
                .printSize(PrintSize.toPrintSize(printWidth, printHeight))
                .orientation(orientation).build();
    }

    TransformSettings calculateCanvasSize(int printWidth, int printHeight,
            int dpi, TransformSettings settings) {
        int canvasWidth = printWidth * dpi;
        int canvasHeight = printHeight * dpi;

        return new Builder(settings).canvasWidth(canvasWidth)
                .canvasHeight(canvasHeight).build();
    }

    TransformSettings calculateFramePixelSize(double frameWidthInInches,
            double frameHeightInInches, int dpi, TransformSettings settings) {
        int frameWidthPx = (int) Math.floor(frameWidthInInches * dpi);
        int frameHeightPx = (int) Math.floor(frameHeightInInches * dpi);

        return new Builder(settings).frameWidthPx(frameWidthPx)
                .frameHeightPx(frameHeightPx).build();
    }

    TransformSettings calculateFrameXY(int canvasWidth, int canvasHeight,
            int frameWidthPx, int frameHeightPx, TransformSettings settings) {
        int frameX = (int) Math.floor((canvasWidth - frameWidthPx) / 2);
        int frameY = (int) Math.floor((canvasHeight - frameHeightPx) / 2);

        return new Builder(settings).frameX(frameX).frameY(frameY).build();
    }

    TransformSettings forceNewWidth(int width, int imageWidth, int imageHeight) {

        int destinationWidth = width;
        int destinationHeight = (int) Math.floor((double) imageHeight
                / ((double) imageWidth / (double) width));

        return new Builder().destinationHeight(destinationHeight)
                .destinationWidth(destinationWidth).build();
    }

    TransformSettings forceNewHeight(int height, int imageWidth, int imageHeight) {

        int destinationHeight = height;
        int destinationWidth = (int) Math.floor((double) imageWidth
                / ((double) imageHeight / (double) height));

        return new Builder().destinationHeight(destinationHeight)
                .destinationWidth(destinationWidth).build();
    }

    TransformSettings calculateDestination(Zooming zooming, int frameWidthPx,
            int frameHeightPx, int frameX, int frameY, int imageWidth,
            int imageHeight, TransformSettings settings) {

        boolean frameHasWiderRatioThanImage = ((double) imageWidth / imageHeight) < ((double) frameWidthPx / frameHeightPx);

        int sourceX = 0;
        int sourceY = 0;
        int sourceWidth = 0;
        int sourceHeight = 0;
        int destinationX = 0;
        int destinationY = 0;
        int destinationWidth = 0;
        int destinationHeight = 0;

        switch (zooming) {
        case FIT:
            if (frameHasWiderRatioThanImage) {
                destinationWidth = forceNewHeight(frameHeightPx, imageWidth,
                        imageHeight).getDestinationWidth();
                destinationHeight = forceNewHeight(frameHeightPx, imageWidth,
                        imageHeight).getDestinationHeight();
                destinationY = frameY;
                destinationX = (int) (frameX + Math
                        .floor((frameWidthPx - destinationWidth) / 2));
            } else {
                destinationWidth = forceNewWidth(frameWidthPx, imageWidth,
                        imageHeight).getDestinationWidth();
                destinationHeight = forceNewWidth(frameWidthPx, imageWidth,
                        imageHeight).getDestinationHeight();
                destinationY = (int) (frameY + Math
                        .floor((frameHeightPx - destinationHeight) / 2));
                destinationX = frameX;
            }
            sourceX = 0;
            sourceY = 0;
            sourceWidth = imageWidth;
            sourceHeight = imageHeight;
            break;
        case FILL:
            if (frameHasWiderRatioThanImage) {
                destinationWidth = forceNewWidth(frameWidthPx, imageWidth,
                        imageHeight).getDestinationWidth();
                destinationHeight = forceNewWidth(frameWidthPx, imageWidth,
                        imageHeight).getDestinationHeight();
                destinationY = (int) Math.floor(frameY
                        - ((destinationHeight - frameHeightPx) / 2));
                destinationX = frameX;
            } else {
                destinationWidth = forceNewHeight(frameHeightPx, imageWidth,
                        imageHeight).getDestinationWidth();
                destinationHeight = forceNewHeight(frameHeightPx, imageWidth,
                        imageHeight).getDestinationHeight();
                destinationX = (int) Math.floor(frameX
                        - ((destinationWidth - frameWidthPx) / 2));
                destinationY = frameY;
            }
            sourceX = 0;
            sourceY = 0;
            sourceWidth = imageWidth;
            sourceHeight = imageHeight;
            break;
        case CROP:
        case TILE:
            if (frameHasWiderRatioThanImage) {
                sourceWidth = imageWidth;
                sourceHeight = (int) Math
                        .floor((double) (imageWidth * frameHeightPx)
                                / frameWidthPx);
                sourceX = 0;
                sourceY = (int) Math
                        .floor((double) (imageHeight - sourceHeight) / 2);
            } else {
                sourceHeight = imageHeight;
                sourceWidth = (int) Math
                        .floor((double) (imageHeight * frameWidthPx)
                                / frameHeightPx);
                sourceX = (int) Math
                        .floor((double) (imageWidth - sourceWidth) / 2);
                sourceY = 0;
            }
            destinationHeight = frameHeightPx;
            destinationWidth = frameWidthPx;
            destinationX = frameX;
            destinationY = frameY;

            break;
        }

        // prevent negative values
        // TODO: probably better prevented in the first place...
        sourceX = Math.max(sourceX, 0);
        sourceY = Math.max(sourceY, 0);

        return new Builder(settings).sourceX(sourceX).sourceY(sourceY)
                .sourceWidth(sourceWidth).sourceHeight(sourceHeight)
                .destinationX(destinationX).destinationY(destinationY)
                .destinationWidth(destinationWidth)
                .destinationHeight(destinationHeight).build();
    }
}
