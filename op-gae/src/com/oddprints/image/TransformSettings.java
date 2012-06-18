package com.oddprints.image;

import com.oddprints.PrintSize;

public class TransformSettings {
    private final PrintSize printSize;
    private final Orientation orientation;
    private final int canvasWidth;
    private final int canvasHeight;
    private final int frameWidthPx;
    private final int frameHeightPx;
    private final int frameX;
    private final int frameY;
    private final int destinationWidth;
    private final int destinationHeight;
    private final int destinationX;
    private final int destinationY;
    private final int sourceWidth;
    private final int sourceHeight;
    private final int sourceX;
    private final int sourceY;

    public enum Orientation {
        AUTO, PORTRAIT, LANDSCAPE;
    }

    public enum Zooming {
        FIT, FILL, CROP;
    }

    private TransformSettings(Builder builder) {
        this.printSize = builder.printSize;
        this.orientation = builder.orientation;
        this.canvasWidth = builder.canvasWidth;
        this.canvasHeight = builder.canvasHeight;
        this.frameWidthPx = builder.frameWidthPx;
        this.frameHeightPx = builder.frameHeightPx;
        this.frameX = builder.frameX;
        this.frameY = builder.frameY;
        this.destinationWidth = builder.destinationWidth;
        this.destinationHeight = builder.destinationHeight;
        this.destinationX = builder.destinationX;
        this.destinationY = builder.destinationY;
        this.sourceWidth = builder.sourceWidth;
        this.sourceHeight = builder.sourceHeight;
        this.sourceX = builder.sourceX;
        this.sourceY = builder.sourceY;
    }

    public int getPrintWidth() {
        switch (orientation) {
        case LANDSCAPE:
            return printSize.getWidth();
        case PORTRAIT:
            return printSize.getHeight();
        case AUTO:
        default:
            throw new RuntimeException("Unexpected orientation");
        }
    }

    public int getPrintHeight() {
        if (orientation == Orientation.LANDSCAPE) {
            return printSize.getHeight();
        } else {
            return printSize.getWidth();
        }
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public int getFrameWidthPx() {
        return frameWidthPx;
    }

    public int getFrameHeightPx() {
        return frameHeightPx;
    }

    public int getFrameX() {
        return frameX;
    }

    public int getFrameY() {
        return frameY;
    }

    public PrintSize getPrintSize() {
        return printSize;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getDestinationWidth() {
        return destinationWidth;
    }

    public int getDestinationHeight() {
        return destinationHeight;
    }

    public int getDestinationX() {
        return destinationX;
    }

    public int getDestinationY() {
        return destinationY;
    }

    public int getSourceWidth() {
        return sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public int getSourceX() {
        return sourceX;
    }

    public int getSourceY() {
        return sourceY;
    }

    public static class Builder {
        private PrintSize printSize;
        private Orientation orientation;
        private int canvasWidth;
        private int canvasHeight;
        private int frameWidthPx;
        private int frameHeightPx;
        private int frameX;
        private int frameY;
        private int destinationWidth;
        private int destinationHeight;
        private int destinationX;
        private int destinationY;
        private int sourceWidth;
        private int sourceHeight;
        private int sourceX;
        private int sourceY;

        public Builder printSize(PrintSize printSize) {
            this.printSize = printSize;
            return this;
        }

        public Builder orientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder canvasWidth(int canvasWidth) {
            this.canvasWidth = canvasWidth;
            return this;
        }

        public Builder canvasHeight(int canvasHeight) {
            this.canvasHeight = canvasHeight;
            return this;
        }

        public Builder frameWidthPx(int frameWidthPx) {
            this.frameWidthPx = frameWidthPx;
            return this;
        }

        public Builder frameHeightPx(int frameHeightPx) {
            this.frameHeightPx = frameHeightPx;
            return this;
        }

        public Builder frameX(int frameX) {
            this.frameX = frameX;
            return this;
        }

        public Builder frameY(int frameY) {
            this.frameY = frameY;
            return this;
        }

        public Builder destinationWidth(int destinationWidth) {
            this.destinationWidth = destinationWidth;
            return this;
        }

        public Builder destinationHeight(int destinationHeight) {
            this.destinationHeight = destinationHeight;
            return this;
        }

        public Builder destinationX(int destinationX) {
            this.destinationX = destinationX;
            return this;
        }

        public Builder destinationY(int destinationY) {
            this.destinationY = destinationY;
            return this;
        }

        public Builder sourceWidth(int sourceWidth) {
            this.sourceWidth = sourceWidth;
            return this;
        }

        public Builder sourceHeight(int sourceHeight) {
            this.sourceHeight = sourceHeight;
            return this;
        }

        public Builder sourceX(int sourceX) {
            this.sourceX = sourceX;
            return this;
        }

        public Builder sourceY(int sourceY) {
            this.sourceY = sourceY;
            return this;
        }

        public TransformSettings build() {
            return new TransformSettings(this);
        }
    }

}
