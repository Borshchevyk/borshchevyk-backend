package ru.kubsu.borshchevyk.media.infrastructure.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.media.application.port.out.S3Port;
import ru.kubsu.borshchevyk.media.domain.exception.StorageException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;

/**
 * Adapter for AWS S3 implementing the S3Port.
 * Handles interactions with object storage.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3Adapter implements S3Port {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${app.s3.bucket}")
    private String bucket;

    @Value("${app.s3.tenant-id}")
    private String tenantId;

    private String getFullBucketName() {
        return bucket; // The user provided bucket-d6b96b as part of the endpoint in text, but usually it's bucket-name.tenant-id or similar in Cloud.ru.
        // However, the user said "API Endpoint https://s3.cloud.ru/bucket-d6b96b" and "bucket-d6b96b".
        // Let's assume the bucket name is just bucket-d6b96b as provided.
    }

    @Override
    public String generatePresignedPutUrl(String s3Key, String contentType, Duration expiration) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
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
    public void uploadFile(String s3Key, InputStream inputStream, long contentLength, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
            log.info("Successfully uploaded file {} to S3 bucket {}", s3Key, bucket);
        } catch (S3Exception e) {
            log.error("S3 SDK error during upload for key {}: {}", s3Key, e.awsErrorDetails().errorMessage(), e);
            throw new StorageException("S3 Upload failed", e);
        } catch (Exception e) {
            log.error("Unexpected error during S3 upload for key {}", s3Key, e);
            throw new StorageException("Exception during S3 upload", e);
        }
    }

    @Override
    public InputStream downloadFile(String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();

            return s3Client.getObject(getObjectRequest);
        } catch (S3Exception e) {
            log.error("S3 SDK error during download for key {}: {}", s3Key, e.awsErrorDetails().errorMessage(), e);
            throw new StorageException("S3 Download failed", e);
        } catch (Exception e) {
            log.error("Unexpected error during S3 download for key {}", s3Key, e);
            throw new StorageException("Exception during S3 download", e);
        }
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
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                // S3 headObject returns 404 if the object does not exist. This is expected.
                return false;
            }
            log.error("S3 SDK error checking existence for key {}: {}", s3Key, e.awsErrorDetails().errorMessage(), e);
            return false;
        }
    }

    @Override
    public void deleteObject(String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted file {} from S3 bucket {}", s3Key, bucket);
        } catch (S3Exception e) {
            log.error("S3 SDK error during deletion for key {}: {}", s3Key, e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during S3 deletion for key {}", s3Key, e);
        }
    }
}
