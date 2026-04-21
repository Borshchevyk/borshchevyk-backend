package ru.kubsu.borshchevyk.media.infrastructure.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.media.application.port.out.S3Port;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Adapter implements S3Port {

    @Value("${app.s3.endpoint}")
    private String endpoint;

    @Value("${app.s3.bucket}")
    private String bucket;

    @Value("${app.s3.access-key}")
    private String accessKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private String getUrl(String key) {
        String baseUrl = endpoint.endsWith("/") ? endpoint : endpoint + "/";
        return baseUrl + bucket + "/" + key;
    }

    @Override
    public void uploadFile(String s3Key, InputStream inputStream, long contentLength, String contentType) {
        try {
            HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofInputStream(() -> inputStream);
            HttpRequest.BodyPublisher lengthAwarePublisher = new HttpRequest.BodyPublisher() {
                @Override
                public long contentLength() {
                    return contentLength;
                }
                @Override
                public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
                    publisher.subscribe(subscriber);
                }
            };

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getUrl(s3Key)))
                    .header("Authorization", "Bearer " + accessKey)
                    .header("Content-Type", contentType)
                    .PUT(lengthAwarePublisher)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("Failed to upload to S3. Status: {}, Body: {}", response.statusCode(), response.body());
                throw new RuntimeException("S3 Upload failed with status " + response.statusCode());
            }

            log.info("Successfully uploaded file {} to S3", s3Key);
        } catch (Exception e) {
            log.error("Exception during S3 upload for key {}", s3Key, e);
            throw new RuntimeException("Exception during S3 upload", e);
        }
    }

    @Override
    public InputStream downloadFile(String s3Key) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getUrl(s3Key)))
                    .header("Authorization", "Bearer " + accessKey)
                    .GET()
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("Failed to download from S3. Status: {}", response.statusCode());
                throw new RuntimeException("S3 Download failed with status " + response.statusCode());
            }

            return response.body();
        } catch (Exception e) {
            log.error("Exception during S3 download for key {}", s3Key, e);
            throw new RuntimeException("Exception during S3 download", e);
        }
    }

    @Override
    public boolean checkObjectExists(String s3Key) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getUrl(s3Key)))
                    .header("Authorization", "Bearer " + accessKey)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            log.error("Exception checking S3 object existence for key {}", s3Key, e);
            return false;
        }
    }

    @Override
    public void deleteObject(String s3Key) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getUrl(s3Key)))
                    .header("Authorization", "Bearer " + accessKey)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("Failed to delete from S3. Status: {}, Body: {}", response.statusCode(), response.body());
            } else {
                log.info("Successfully deleted file {} from S3", s3Key);
            }
        } catch (Exception e) {
            log.error("Exception during S3 deletion for key {}", s3Key, e);
        }
    }
}
