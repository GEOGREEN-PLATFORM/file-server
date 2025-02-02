package com.example.fileserver.service;

import com.jhlabs.image.GaussianFilter;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageEditor {
    public BufferedImage blur(BufferedImage image, float radius) {
        GaussianFilter gaussianFilter = new GaussianFilter(radius);
        return gaussianFilter.filter(image, null);
    }

    public ByteArrayOutputStream zip(BufferedImage file, double quality) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file)
                .size(file.getWidth(), file.getHeight())
                .outputFormat("jpg")
                .outputQuality(quality)  // Сжатие качества (0.0 - 1.0)
                .toOutputStream(outputStream);
        return outputStream;
    }
}
