package com.example.file_server.service;

import com.example.file_server.model.dto.ImageUrlResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImageService {
    byte[] getImage(UUID imageId);

    ImageUrlResponse uploadImage(MultipartFile file);
}
