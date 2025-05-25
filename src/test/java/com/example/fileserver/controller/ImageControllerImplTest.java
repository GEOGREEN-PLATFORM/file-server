package com.example.fileserver.controller;

import com.example.fileserver.controller.impl.ImageControllerImpl;
import com.example.fileserver.model.dto.ImageUrlResponse;
import com.example.fileserver.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ImageControllerImplTest {
    @Mock
    private ImageService imageService;
    @InjectMocks
    private ImageControllerImpl controller;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void uploadImage_ShouldReturnUrlResponse() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[]{1, 2, 3}
        );
        UUID previewId = UUID.randomUUID();
        UUID fullId = UUID.randomUUID();
        ImageUrlResponse expectedResponse = ImageUrlResponse.builder()
                .previewImageId(previewId)
                .fullImageId(fullId)
                .build();

        when(imageService.uploadImage(file)).thenReturn(expectedResponse);

        ResponseEntity<ImageUrlResponse> responseEntity = controller.uploadImage(file);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(previewId, responseEntity.getBody().getPreviewImageId());
        assertEquals(fullId, responseEntity.getBody().getFullImageId());
    }

    @Test
    public void downloadImage_ShouldReturnBytesWithHeaders() {
        UUID imageId = UUID.randomUUID();
        byte[] imageData = new byte[]{10, 20, 30};

        when(imageService.getImage(imageId)).thenReturn(imageData);

        ResponseEntity<byte[]> responseEntity = controller.downloadImage(imageId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertArrayEquals(imageData, responseEntity.getBody());

        HttpHeaders headers = responseEntity.getHeaders();
        assertEquals(MediaType.IMAGE_JPEG, headers.getContentType());
        assertEquals(imageData.length, headers.getContentLength());
    }

    @Test
    public void downloadImage_WhenServiceThrows_ShouldPropagateException() {
        UUID imageId = UUID.randomUUID();
        when(imageService.getImage(imageId)).thenThrow(new RuntimeException("Not found"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.downloadImage(imageId));
        assertEquals("Not found", ex.getMessage());
    }
}
