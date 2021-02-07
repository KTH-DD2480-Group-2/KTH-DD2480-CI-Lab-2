import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ContinuousIntegrationServerTest {

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

    /**
     * A class for making the StartServer function executable on a separate
     * thread during tests.
     */
    private static class RunnableServer implements Runnable {
        @Override
        public void run() {
            try {
                ContinuousIntegrationServer.StartServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @BeforeEach
    public void init() {
        RunnableServer server = new RunnableServer();
        Thread t = new Thread(server);
        t.start();
    }

    @Test
    void main_ValidInput() throws IOException, InterruptedException {
        assertEquals("CI server working!\n", post("http://127.0.0.1:8080"));
        assertNotEquals("CI server working!", post("http://127.0.0.1:8080"));
        assertEquals("Handling webhook event\n", post("http://127.0.0.1:8080/api/webhook-processer"));
        assertNotEquals("Handling webhook vent\n", post("http://127.0.0.1:8080/api/webhook-processer"));
        assertEquals("404. Page not found\n", post("http://127.0.0.1:8080/xyz"));
    }
}
