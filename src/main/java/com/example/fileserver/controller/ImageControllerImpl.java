package com.example.fileserver.controller;

import com.example.fileserver.model.dto.ImageUrlResponse;
import com.example.fileserver.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class ImageControllerImpl implements ImageController {
    private final ImageService imageService;

    @Override
    public ResponseEntity<ImageUrlResponse> uploadImage(MultipartFile file) {
        return ResponseEntity.ok(imageService.uploadImage(file));
    }

    @Override
    public ResponseEntity<byte[]> downloadImage(UUID imageId) {
        var imageBytes = imageService.getImage(imageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageBytes.length);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
