package com.example.fileserver.service;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageEditorTest {
    private final ImageEditor imageEditor = new ImageEditor();

    @Test
    public void testBlurWithZeroRadiusReturnsOriginalImage() {
        BufferedImage image = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        image.setRGB(1, 1, Color.RED.getRGB());

        BufferedImage result = imageEditor.blur(image, 0f);

        assertNotNull(result, "Resulting image should not be null");
        assertEquals(
                image.getRGB(1, 1),
                result.getRGB(1, 1),
                "Blurring with radius 0 should leave pixels unchanged"
        );
    }

    @Test
    public void testBlurWithPositiveRadiusChangesPixels() {
        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                image.setRGB(x, y, Color.BLACK.getRGB());
            }
        }
        image.setRGB(2, 2, Color.WHITE.getRGB());

        BufferedImage result = imageEditor.blur(image, 2f);

        assertNotEquals(
                Color.WHITE.getRGB(),
                result.getRGB(2, 2),
                "Blurring with positive radius should mix pixel colors"
        );
    }

    @Test
    public void testZipProducesNonEmptyOutput() throws IOException {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = imageEditor.zip(image, 0.5);

        assertNotNull(outputStream, "Output stream should not be null");
        byte[] bytes = outputStream.toByteArray();
        assertTrue(bytes.length > 0, "Compressed image should contain data");

        assertEquals((byte) 0xFF, bytes[0], "JPEG should start with 0xFF");
        assertEquals((byte) 0xD8, bytes[1], "JPEG should start with 0xD8");
    }
}