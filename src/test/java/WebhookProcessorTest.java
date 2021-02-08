import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class WebhookProcessorTest {

    /**
     * A function for sending an HTTP post request.
     *
     * @param url destination address
     * @return response as a String
     * @throws IOException
     * @throws InterruptedException
     */
    private static String post(String url) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    @Test
    void dummyTest() {
        assertTrue(true);
    }
}
