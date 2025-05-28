package com.example.file_server.service.impl;


import com.example.file_server.configuration.MinioProperties;
import com.example.file_server.exception.ObjectNotFoundException;
import com.example.file_server.model.dto.ImageUrlResponse;
import com.example.file_server.service.ImageEditor;
import com.example.file_server.service.ImageService;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {
    @Value("${image.preview.quality}")
    private double previewQuality;
    @Value("${image.preview.blur}")
    private float previewBlur;
    @Value("${image.full.quality}")
    private double fullQuality;

    private final ImageEditor imageEditor;
    private final MinioProperties properties;
    private final MinioClient minioClient;

    @Override
    public ImageUrlResponse uploadImage(MultipartFile file) {
        try {
            var previewImageId = savePreviewImage(file);
            var fullImageId = saveFullImage(file);
            return ImageUrlResponse.builder().previewImageId(previewImageId).fullImageId(fullImageId).build();
        } catch (Exception e) {
            log.debug(e.getMessage());
            throw new RuntimeException("Ошибка при сохранении изображения. Попробуйте позже");
        }
    }

    private UUID savePreviewImage(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        var bucketName = properties.getImageBucket();
        var imageId = UUID.randomUUID();
        checkAndCreateBucket(bucketName);
        var blurImage = imageEditor.blur(ImageIO.read(file.getInputStream()), previewBlur);
        var zipImage = imageEditor.zip(blurImage, previewQuality);
        saveImage(bucketName, zipImage, imageId.toString(), file.getContentType());
        return imageId;
    }

    private UUID saveFullImage(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String bucketName = properties.getImageBucket();
        var imageId = UUID.randomUUID();
        checkAndCreateBucket(bucketName);
        var zipImage = imageEditor.zip(ImageIO.read(file.getInputStream()), fullQuality);
        saveImage(bucketName, zipImage, imageId.toString(), file.getContentType());
        return imageId;
    }

    private void saveImage(String bucketName, ByteArrayOutputStream outputStream, String imageId, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .stream(inputStream, outputStream.size(), -1)
                        .object(imageId)
                        .contentType(contentType)
                        .build()
        );
    }

    private void checkAndCreateBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build());
        if (!bucketExists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(createBucketPolicyConfig(bucketName))
                            .build());
        }
    }

    private String createBucketPolicyConfig(String bucketName) {
        return """
                {
                  "Statement" : [ {
                    "Action" : "s3:GetObject",
                    "Effect" : "Allow",
                    "Principal" : "*",
                    "Resource" : "arn:aws:s3:::%s/*"
                  } ],
                  "Version" : "2012-10-17"
                }
                """.formatted(bucketName);
    }


    @Override
    public byte[] getImage(UUID objectName) {
        try (InputStream stream = minioClient
                .getObject(GetObjectArgs.builder()
                        .bucket(properties.getImageBucket())
                        .object(String.valueOf(objectName))
                        .build())) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new ObjectNotFoundException("Файл не найден");
        }
    }
}