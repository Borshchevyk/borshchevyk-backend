import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

public class TestUpload {
    public static void main(String[] args) throws Exception {
        String accessKey = "NWJiMWY1NmMtMjk2NS00YjQ2LTk5MGYtYjllMWI5YjRmMWM5.b9e803e7379612641fec37bef1e745bf";
        String url = "https://s3.cloud.ru/borshchevyk-media/test-upload.txt";
        String content = "Hello World";

        HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(content);
        HttpRequest.BodyPublisher lengthAwarePublisher = new HttpRequest.BodyPublisher() {
            @Override
            public long contentLength() {
                return content.length();
            }
            @Override
            public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
                publisher.subscribe(subscriber);
            }
        };

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessKey)
                .header("Content-Type", "text/plain")
                .PUT(lengthAwarePublisher)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());
    }
}
