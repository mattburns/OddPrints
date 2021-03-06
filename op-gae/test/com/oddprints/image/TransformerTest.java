package com.oddprints.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.appengine.api.images.Image;
import com.oddprints.image.TransformSettings.Orientation;
import com.oddprints.image.TransformSettings.Zooming;

public class TransformerTest {

    TransformSettings EMPTY_SETTINGS = new TransformSettings.Builder().build();

    @Test
    public void can_calculate_minimum_print_size() {
        Transformer transformer = new Transformer();

        TransformSettings settings = transformer.calculatePrintSize(1, 1);
        assertEquals(6, settings.getPrintWidth());
        assertEquals(4, settings.getPrintHeight());

        settings = transformer.calculatePrintSize(6, 4);
        assertEquals(6, settings.getPrintWidth());
        assertEquals(4, settings.getPrintHeight());

        settings = transformer.calculatePrintSize(6.1, 4);
        assertEquals(7, settings.getPrintWidth());
        assertEquals(5, settings.getPrintHeight());

        settings = transformer.calculatePrintSize(6, 4.1);
        assertEquals(7, settings.getPrintWidth());
        assertEquals(5, settings.getPrintHeight());

        settings = transformer.calculatePrintSize(7.1, 5);
        assertEquals(10, settings.getPrintWidth());
        assertEquals(8, settings.getPrintHeight());
    }

    @Test
    public void can_calculate_auto_portrait() {
        Transformer transformer = new Transformer();

        TransformSettings settings = transformer.calculatePrintSize(6, 4);
        assertEquals(6, settings.getPrintWidth());
        assertEquals(4, settings.getPrintHeight());

        settings = transformer.calculatePrintSize(4, 6);
        assertEquals(4, settings.getPrintWidth());
        assertEquals(6, settings.getPrintHeight());

        settings = transformer.calculatePrintSize(10, 2);
        assertEquals(10, settings.getPrintWidth());
        assertEquals(8, settings.getPrintHeight());

        // same behaviour with args
        settings = transformer.calculatePrintSize(4, 6, Orientation.AUTO);
        assertEquals(4, settings.getPrintWidth());
        assertEquals(6, settings.getPrintHeight());

        settings = transformer.calculatePrintSize(6, 4, Orientation.LANDSCAPE);
        assertEquals(6, settings.getPrintWidth());
        assertEquals(4, settings.getPrintHeight());

        settings = transformer.calculatePrintSize(4, 6, Orientation.LANDSCAPE);
        assertEquals(10, settings.getPrintWidth());
        assertEquals(8, settings.getPrintHeight());
    }

    @Test
    public void can_calculate_force_portrait() {
        Transformer transformer = new Transformer();

        TransformSettings settings = transformer.calculatePrintSize(4, 6,
                Orientation.PORTRAIT);
        assertEquals(4, settings.getPrintWidth());
        assertEquals(6, settings.getPrintHeight());

        settings = transformer.calculatePrintSize(6, 4, Orientation.PORTRAIT);
        assertEquals(8, settings.getPrintWidth());
        assertEquals(10, settings.getPrintHeight());
    }

    @Test
    public void can_calculate_canvas_size() {
        Transformer transformer = new Transformer();

        TransformSettings settings = transformer.calculateCanvasSize(6, 4, 150,
                EMPTY_SETTINGS);
        assertEquals(900, settings.getCanvasWidth());
        assertEquals(600, settings.getCanvasHeight());

        settings = transformer.calculateCanvasSize(6, 4, 300, EMPTY_SETTINGS);
        assertEquals(1800, settings.getCanvasWidth());
        assertEquals(1200, settings.getCanvasHeight());

        settings = transformer.calculateCanvasSize(7, 5, 150, EMPTY_SETTINGS);
        assertEquals(1050, settings.getCanvasWidth());
        assertEquals(750, settings.getCanvasHeight());
    }

    @Test
    public void can_calculate_frame_pixel_size() {
        Transformer transformer = new Transformer();

        TransformSettings settings = transformer.calculateFramePixelSize(1, 1,
                150, EMPTY_SETTINGS);
        assertEquals(150, settings.getFrameWidthPx());
        assertEquals(150, settings.getFrameHeightPx());

        settings = transformer.calculateFramePixelSize(6, 4, 150,
                EMPTY_SETTINGS);
        assertEquals(900, settings.getFrameWidthPx());
        assertEquals(600, settings.getFrameHeightPx());

        settings = transformer.calculateFramePixelSize(6, 4, 300,
                EMPTY_SETTINGS);
        assertEquals(1800, settings.getFrameWidthPx());
        assertEquals(1200, settings.getFrameHeightPx());
    }

    @Test
    public void can_calculate_frame_position_on_canvas() {
        Transformer transformer = new Transformer();

        TransformSettings settings = transformer.calculateFrameXY(6, 4, 6, 4,
                EMPTY_SETTINGS);
        assertEquals(0, settings.getFrameX());
        assertEquals(0, settings.getFrameY());

        settings = transformer.calculateFrameXY(3, 3, 1, 1, EMPTY_SETTINGS);
        assertEquals(1, settings.getFrameX());
        assertEquals(1, settings.getFrameY());

        settings = transformer.calculateFrameXY(4, 3, 2, 1, EMPTY_SETTINGS);
        assertEquals(1, settings.getFrameX());
        assertEquals(1, settings.getFrameY());

        settings = transformer.calculateFrameXY(3, 3, 2, 2, EMPTY_SETTINGS);
        assertEquals("if not exact fit, should snap left and up by half pixel",
                0, settings.getFrameX());
        assertEquals("if not exact fit, should snap left and up by half pixel",
                0, settings.getFrameY());
    }

    @Test
    public void can_calculate_image_position_on_canvas() {
        Transformer transformer = new Transformer();

        TransformSettings settings = transformer.calculateDestination(
                Zooming.FIT, 4, 4, 2, 2, 4, 8, EMPTY_SETTINGS);
        assertEquals(2, settings.getDestinationWidth());
        assertEquals(4, settings.getDestinationHeight());
        assertEquals(3, settings.getDestinationX());
        assertEquals(2, settings.getDestinationY());
        assertEquals(4, settings.getSourceWidth());
        assertEquals(8, settings.getSourceHeight());
        assertEquals(0, settings.getSourceX());
        assertEquals(0, settings.getSourceY());

        settings = transformer.calculateDestination(Zooming.FIT, 4, 4, 2, 2, 8,
                4, EMPTY_SETTINGS);
        assertEquals(4, settings.getDestinationWidth());
        assertEquals(2, settings.getDestinationHeight());
        assertEquals(2, settings.getDestinationX());
        assertEquals(3, settings.getDestinationY());
        assertEquals(8, settings.getSourceWidth());
        assertEquals(4, settings.getSourceHeight());
        assertEquals(0, settings.getSourceX());
        assertEquals(0, settings.getSourceY());

        settings = transformer.calculateDestination(Zooming.FILL, 4, 4, 2, 2,
                4, 8, EMPTY_SETTINGS);
        assertEquals(4, settings.getDestinationWidth());
        assertEquals(8, settings.getDestinationHeight());
        assertEquals(2, settings.getDestinationX());
        assertEquals(0, settings.getDestinationY());
        assertEquals(4, settings.getSourceWidth());
        assertEquals(8, settings.getSourceHeight());
        assertEquals(0, settings.getSourceX());
        assertEquals(0, settings.getSourceY());

        settings = transformer.calculateDestination(Zooming.FILL, 4, 4, 2, 2,
                8, 4, EMPTY_SETTINGS);
        assertEquals(8, settings.getDestinationWidth());
        assertEquals(4, settings.getDestinationHeight());
        assertEquals(0, settings.getDestinationX());
        assertEquals(2, settings.getDestinationY());
        assertEquals(8, settings.getSourceWidth());
        assertEquals(4, settings.getSourceHeight());
        assertEquals(0, settings.getSourceX());
        assertEquals(0, settings.getSourceY());

        settings = transformer.calculateDestination(Zooming.FILL, 4, 4, 3, 3,
                8, 4, EMPTY_SETTINGS);
        assertEquals(8, settings.getDestinationWidth());
        assertEquals(4, settings.getDestinationHeight());
        assertEquals(1, settings.getDestinationX());
        assertEquals(3, settings.getDestinationY());
        assertEquals(8, settings.getSourceWidth());
        assertEquals(4, settings.getSourceHeight());
        assertEquals(0, settings.getSourceX());
        assertEquals(0, settings.getSourceY());

        settings = transformer.calculateDestination(Zooming.FILL, 3, 2, 2, 1,
                6, 4, EMPTY_SETTINGS);
        assertEquals(3, settings.getDestinationWidth());
        assertEquals(2, settings.getDestinationHeight());
        assertEquals(2, settings.getDestinationX());
        assertEquals(1, settings.getDestinationY());
        assertEquals(6, settings.getSourceWidth());
        assertEquals(4, settings.getSourceHeight());
        assertEquals(0, settings.getSourceX());
        assertEquals(0, settings.getSourceY());

        settings = transformer.calculateDestination(Zooming.CROP, 4, 4, 2, 2,
                4, 8, EMPTY_SETTINGS);
        assertEquals(4, settings.getDestinationWidth());
        assertEquals(4, settings.getDestinationHeight());
        assertEquals(2, settings.getDestinationX());
        assertEquals(2, settings.getDestinationY());
        assertEquals(4, settings.getSourceWidth());
        assertEquals(4, settings.getSourceHeight());
        assertEquals(0, settings.getSourceX());
        assertEquals(2, settings.getSourceY());

        settings = transformer.calculateDestination(Zooming.CROP, 4, 4, 2, 2,
                8, 4, EMPTY_SETTINGS);
        assertEquals(4, settings.getDestinationWidth());
        assertEquals(4, settings.getDestinationHeight());
        assertEquals(2, settings.getDestinationX());
        assertEquals(2, settings.getDestinationY());
        assertEquals(4, settings.getSourceWidth());
        assertEquals(4, settings.getSourceHeight());
        assertEquals(2, settings.getSourceX());
        assertEquals(0, settings.getSourceY());

        settings = transformer.calculateDestination(Zooming.CROP, 4, 4, 3, 3,
                8, 4, EMPTY_SETTINGS);
        assertEquals(4, settings.getDestinationWidth());
        assertEquals(4, settings.getDestinationHeight());
        assertEquals(3, settings.getDestinationX());
        assertEquals(3, settings.getDestinationY());
        assertEquals(4, settings.getSourceWidth());
        assertEquals(4, settings.getSourceHeight());
        assertEquals(2, settings.getSourceX());
        assertEquals(0, settings.getSourceY());

        settings = transformer.calculateDestination(Zooming.CROP, 3, 2, 2, 1,
                6, 4, EMPTY_SETTINGS);
        assertEquals(3, settings.getDestinationWidth());
        assertEquals(2, settings.getDestinationHeight());
        assertEquals(2, settings.getDestinationX());
        assertEquals(1, settings.getDestinationY());
        assertEquals(6, settings.getSourceWidth());
        assertEquals(4, settings.getSourceHeight());
        assertEquals(0, settings.getSourceX());
        assertEquals(0, settings.getSourceY());
    }

    @Test
    public void image_shrinking_algorithm() {
        Transformer transformer = new Transformer();

        TransformSettings settings = transformer.forceNewHeight(3, 4, 6);
        assertEquals(2, settings.getDestinationWidth());
        assertEquals(3, settings.getDestinationHeight());

        settings = transformer.forceNewWidth(3, 6, 4);
        assertEquals(3, settings.getDestinationWidth());
        assertEquals(2, settings.getDestinationHeight());
    }

    @Test
    public void image_shrinking_algorithm_should_not_throw_div_by_zero_exception() {
        Transformer transformer = new Transformer();
        transformer.forceNewHeight(600, 830, 554);
        transformer.forceNewWidth(600, 554, 830);
    }

    @Test
    public void end_to_end_test() {
        Image image = Mockito.mock(Image.class);
        Mockito.when(image.getWidth()).thenReturn(2000);
        Mockito.when(image.getHeight()).thenReturn(1000);
        Transformer transformer = new Transformer();
        TransformSettings settings = transformer.calculateSettings(image, 300,
                2, 1, Zooming.FILL, Orientation.AUTO);
        assertEquals(300, settings.getFrameHeightPx());
        assertEquals(600, settings.getFrameWidthPx());
    }

    @Test
    public void source_coordinates_are_correct() {
        Image image = Mockito.mock(Image.class);
        Mockito.when(image.getWidth()).thenReturn(830);
        Mockito.when(image.getHeight()).thenReturn(554);
        Transformer transformer = new Transformer();
        TransformSettings settings = transformer.calculateSettings(image, 100,
                5, 3, Zooming.CROP, Orientation.AUTO);
        assertEquals(0, settings.getSourceX());
        assertEquals(28, settings.getSourceY());

        assertEquals(830, settings.getSourceWidth());
        assertEquals(498, settings.getSourceHeight());
    }

    @Test
    public void source_coordinates_are_positive() {
        Image image = Mockito.mock(Image.class);
        Mockito.when(image.getWidth()).thenReturn(2000);
        Mockito.when(image.getHeight()).thenReturn(1000);
        Transformer transformer = new Transformer();
        TransformSettings settings = transformer.calculateSettings(image, 300,
                5, 3, Zooming.CROP, Orientation.AUTO);
        assertTrue(settings.getSourceX() >= 0);
        assertTrue(settings.getSourceY() >= 0);
    }

}
