import static org.junit.jupiter.api.Assertions.*;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import java.io.File;
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
     * A function for sending an HTTP post request that contain a valid and real payload.
     *
     * @param url destination address
     * @return response as a String
     * @throws IOException
     * @throws InterruptedException
     */
    private static void postWithPayload(String url, String payloadFile) throws IOException, InterruptedException {
        File file = new File(payloadFile);
        FileEntity entity = new FileEntity(file, ContentType.create("text/plain", "UTF-8"));

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        CloseableHttpResponse response2 = httpclient.execute(httpPost);
        response2.close();
        httpclient.close();
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

    /**
     * Starts the server in a separate thread.
     */
    @BeforeEach
    public void init() {
        RunnableServer server = new RunnableServer();
        Thread t = new Thread(server);
        t.start();
    }


    /**
     * Deletes downloaded files.
     */
    @AfterEach
    public void fin() {
        File file = new File("revision.zip");

        if (file.delete()) {
            System.out.println("File deleted successfully");
        } else {
            System.out.println("Failed to delete the file");
        }
    }

    /**
     * Tests weather the server is up and responding.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void main_ValidInput_CheckServerStatus() throws IOException, InterruptedException {
        assertEquals("CI server working!\n", post("http://127.0.0.1:8080"));
        assertNotEquals("CI server working!", post("http://127.0.0.1:8080"));
    }

    /**
     * Tests with an invalid request
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void main_InvalidInput_HandlingInvalidRequests() throws IOException, InterruptedException {
        assertEquals("404. Page not found\n", post("http://127.0.0.1:8080/xyz"));
    }

    /**
     * Test webhook handling with valid payload.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void main_ValidInput_HandlingValidWebhook() throws IOException, InterruptedException {
        postWithPayload("http://127.0.0.1:8080/api/webhook-processer","src/test/java/ValidGitPayload.txt");
        File file = new File("revision.zip");
        assertTrue(file.exists());
        assertTrue(file.delete());
    }

    /**
     * Test webhook handling with invalid payload.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void main_InvalidInput_HandlingValidWebhook() throws IOException, InterruptedException {
        postWithPayload("http://127.0.0.1:8080/api/webhook-processer","src/test/java/InvalidGitPayload.txt");
        File file = new File("revision.zip");
        assertFalse(file.exists());
        assertFalse(file.delete());
    }
}
