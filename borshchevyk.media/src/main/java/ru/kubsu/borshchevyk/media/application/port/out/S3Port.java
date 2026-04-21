package ru.kubsu.borshchevyk.media.application.port.out;

import java.io.InputStream;

public interface S3Port {
    void uploadFile(String s3Key, InputStream inputStream, long contentLength, String contentType);
    InputStream downloadFile(String s3Key);
    boolean checkObjectExists(String s3Key);
    void deleteObject(String s3Key);
}
