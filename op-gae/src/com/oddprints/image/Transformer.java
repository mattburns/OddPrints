package com.oddprints.image;

import com.oddprints.PrintSize;
import com.oddprints.image.TransformSettings.Builder;
import com.oddprints.image.TransformSettings.Orientation;
import com.oddprints.image.TransformSettings.Zooming;

public class Transformer {

    TransformSettings calculatePrintSize(double frameWidthInInches,
            double frameHeightInInches) {
        return calculatePrintSize(frameWidthInInches, frameHeightInInches,
                Orientation.AUTO);
    }

    TransformSettings calculatePrintSize(double frameWidthInInches,
            double frameHeightInInches, Orientation orientation) {

        if (orientation == Orientation.AUTO) {
            if (frameWidthInInches < frameHeightInInches) {
                orientation = Orientation.PORTRAIT;
            } else {
                orientation = Orientation.LANDSCAPE;
            }
        }

        int printWidth;
        int printHeight;

        if (orientation == Orientation.PORTRAIT) {
            printWidth = 4;
            printHeight = 6;
            if (frameWidthInInches > printWidth
                    || frameHeightInInches > printHeight) {
                printWidth = 5;
                printHeight = 7;
            }
            if (frameWidthInInches > printWidth
                    || frameHeightInInches > printHeight) {
                printWidth = 8;
                printHeight = 10;
            }
            if (frameWidthInInches > printWidth
                    || frameHeightInInches > printHeight) {
                printWidth = 8;
                printHeight = 12;
            }
        } else {
            printWidth = 6;
            printHeight = 4;
            if (frameWidthInInches > printWidth
                    || frameHeightInInches > printHeight) {
                printWidth = 7;
                printHeight = 5;
            }
            if (frameWidthInInches > printWidth
                    || frameHeightInInches > printHeight) {
                printWidth = 10;
                printHeight = 8;
            }
            if (frameWidthInInches > printWidth
                    || frameHeightInInches > printHeight) {
                printWidth = 12;
                printHeight = 8;
            }
        }

        return new Builder()
                .printSize(PrintSize.toPrintSize(printWidth, printHeight))
                .orientation(orientation).build();
    }

    TransformSettings calculateCanvasSize(int printWidth, int printHeight,
            int dpi) {
        int canvasWidth = printWidth * dpi;
        int canvasHeight = printHeight * dpi;

        return new Builder().canvasWidth(canvasWidth)
                .canvasHeight(canvasHeight).build();
    }

    TransformSettings calculateFramePixelSize(int frameWidthInInches,
            int frameHeightInInches, int dpi) {
        int frameWidthPx = (int) Math.floor(frameWidthInInches * dpi);
        int frameHeightPx = (int) Math.floor(frameHeightInInches * dpi);

        return new Builder().frameWidthPx(frameWidthPx)
                .frameHeightPx(frameHeightPx).build();
    }

    TransformSettings calculateFrameXY(int canvasWidth, int canvasHeight,
            int frameWidthPx, int frameHeightPx) {
        int frameX = (int) Math.floor((canvasWidth - frameWidthPx) / 2);
        int frameY = (int) Math.floor((canvasHeight - frameHeightPx) / 2);

        return new Builder().frameX(frameX).frameY(frameY).build();
    }

    TransformSettings forceNewWidth(int width, int imageWidth, int imageHeight) {

        int destinationWidth = width;
        int destinationHeight = (int) Math.floor(imageHeight
                / (imageWidth / width));

        return new Builder().destinationHeight(destinationHeight)
                .destinationWidth(destinationWidth).build();
    }

    TransformSettings forceNewHeight(int height, int imageWidth, int imageHeight) {

        int destinationHeight = height;
        int destinationWidth = (int) Math.floor(imageWidth
                / (imageHeight / height));

        return new Builder().destinationHeight(destinationHeight)
                .destinationWidth(destinationWidth).build();
    }

    TransformSettings calculateDestination(Zooming zooming, int frameWidthPx,
            int frameHeightPx, int frameX, int frameY, int imageWidth,
            int imageHeight) {

        boolean frameHasWiderRatioThanImage = (imageWidth / imageHeight) < (frameWidthPx / frameHeightPx);

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
            if (frameHasWiderRatioThanImage) {
                sourceWidth = imageWidth;
                sourceHeight = (int) Math.floor((imageWidth * frameHeightPx)
                        / frameWidthPx);
                sourceX = 0;
                sourceY = (int) Math.floor((imageHeight - sourceHeight) / 2);
            } else {
                sourceHeight = imageHeight;
                sourceWidth = (int) Math.floor((imageHeight * frameWidthPx)
                        / frameHeightPx);
                sourceX = (int) Math.floor((imageWidth - sourceWidth) / 2);
                sourceY = 0;
            }
            destinationHeight = frameHeightPx;
            destinationWidth = frameWidthPx;
            destinationX = frameX;
            destinationY = frameY;

            break;
        }

        return new Builder().sourceX(sourceX).sourceY(sourceY)
                .sourceWidth(sourceWidth).sourceHeight(sourceHeight)
                .destinationX(destinationX).destinationY(destinationY)
                .destinationWidth(destinationWidth)
                .destinationHeight(destinationHeight).build();
    }
}
