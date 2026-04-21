package ru.kubsu.borshchevyk.media.infrastructure.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.media.application.port.out.S3Port;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Adapter implements S3Port {

    private final S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucket;

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
            throw new RuntimeException("S3 Upload failed", e);
        } catch (Exception e) {
            log.error("Unexpected error during S3 upload for key {}", s3Key, e);
            throw new RuntimeException("Exception during S3 upload", e);
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
            throw new RuntimeException("S3 Download failed", e);
        } catch (Exception e) {
            log.error("Unexpected error during S3 download for key {}", s3Key, e);
            throw new RuntimeException("Exception during S3 download", e);
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
