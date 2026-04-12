package ru.kubsu.borshchevyk.media.application.port.out;

import java.time.Duration;

public interface S3Port {
    String generatePresignedPutUrl(String s3Key, String contentType, Duration expiration);
    boolean checkObjectExists(String s3Key);
}
