package ru.kubsu.borshchevyk.media.application.port.out;

import java.time.Duration;
import java.io.InputStream;

public interface S3Port {
    String generatePresignedPutUrl(String s3Key, String contentType, Duration expiration);
    String generatePresignedGetUrl(String s3Key, Duration expiration);
    boolean checkObjectExists(String s3Key);
    void deleteObject(String s3Key);
    void uploadFile(String s3Key, InputStream inputStream, long size, String contentType);
    InputStream downloadFile(String s3Key);
}
