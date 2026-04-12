package ru.kubsu.borshchevyk.media.infrastructure.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.media.application.port.out.S3Port;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Adapter implements S3Port {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucket;

    @Override
    public String generatePresignedPutUrl(String s3Key, String contentType, Duration expiration) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

    @Override
    public String generatePresignedGetUrl(String s3Key, Duration expiration) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    @Override
    public boolean checkObjectExists(String s3Key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            log.warn("S3 object not found: {}", s3Key);
            return false;
        } catch (Exception e) {
            log.error("Error checking S3 object existence for key {}: {}", s3Key, e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteObject(String s3Key) {
        try {
            software.amazon.awssdk.services.s3.model.DeleteObjectRequest deleteObjectRequest = software.amazon.awssdk.services.s3.model.DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Deleted S3 object with key {}", s3Key);
        } catch (Exception e) {
            log.error("Error deleting S3 object with key {}: {}", s3Key, e.getMessage());
        }
    }
}
