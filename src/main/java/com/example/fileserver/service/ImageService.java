package com.example.fileserver.service;

import com.example.fileserver.model.dto.ImageUrlResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImageService {
    byte[] getImage(UUID imageId);

    ImageUrlResponse uploadImage(MultipartFile file);
}
