package com.example.file_server.service;

import com.example.file_server.configuration.MinioProperties;
import com.example.file_server.service.impl.ImageServiceImpl;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyFloat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {
    @Mock
    private ImageEditor imageEditor;
    @Mock
    private MinioProperties properties;
    @Mock
    private MinioClient minioClient;
    @Mock
    private MultipartFile multipartFile;
    @InjectMocks
    private ImageServiceImpl service;

    @BeforeEach
    public void setup() throws Exception {
        when(properties.getImageBucket()).thenReturn("test-bucket");
        byte[] content = "dummy".getBytes(StandardCharsets.UTF_8);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        when(multipartFile.getContentType()).thenReturn("image/png");
        BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream dummyStream = new ByteArrayOutputStream();
        dummyStream.write(0);

        when(imageEditor.blur(any(BufferedImage.class), anyFloat())).thenReturn(dummyImage);
        when(imageEditor.zip(any(BufferedImage.class), anyDouble())).thenReturn(dummyStream);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);
    }

    @Test
    public void uploadImage_runtimeExceptionOnFailure() throws Exception {
        when(imageEditor.zip(any(BufferedImage.class), anyDouble())).thenThrow(new IOException("fail"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.uploadImage(multipartFile));
        assertTrue(ex.getMessage().contains("Ошибка при сохранении изображения"));
    }
}