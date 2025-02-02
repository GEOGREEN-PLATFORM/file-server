package com.example.fileserver.controller;

import com.example.fileserver.model.dto.ImageUrlResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequestMapping("/file/image")
public interface ImageController {
    @PostMapping("/upload")
    ResponseEntity<ImageUrlResponse> uploadImage(@RequestPart MultipartFile file);

    @GetMapping("/download/{imageId}")
    ResponseEntity<byte[]> downloadImage(@PathVariable("imageId") UUID imageId);
}
