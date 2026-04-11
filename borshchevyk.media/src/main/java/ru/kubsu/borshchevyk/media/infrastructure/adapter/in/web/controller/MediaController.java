package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Endpoints for handling media files")
public class MediaController {

    private final S3Presigner s3Presigner;

    @Value("${app.s3.bucket}")
    private String bucket;

    @Operation(summary = "Get pre-signed URL for upload", description = "Generates a secure temporary link for the client to directly upload a file to S3.")
    @GetMapping("/upload-url")
    public UploadUrlResponse getUploadUrl(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam String contentType,
            @RequestParam String extension) {
        
        log.info("Generating upload URL for user {} with content type {}", userId, contentType);

        String objectKey = "attachments/" + userId.toString() + "/" + UUID.randomUUID().toString() + "." + extension;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return new UploadUrlResponse(
                presignedRequest.url().toString(),
                objectKey
        );
    }

    public record UploadUrlResponse(String uploadUrl, String objectKey) {}
}
