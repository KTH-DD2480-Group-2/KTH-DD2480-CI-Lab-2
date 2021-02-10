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
    @BeforeAll
    public static void init() {
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
        assertEquals("CI server working!", post("http://127.0.0.1:8080"));
    }

    /**
     * Tests with an invalid request
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void main_InvalidInput_HandlingInvalidRequests() throws IOException, InterruptedException {
        assertEquals("404. Page not found", post("http://127.0.0.1:8080/xyz"));
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

    /**
     * Test the ability of the server to download a specific copy of the repo.
     */
    @Test
    void main_ValidInput_DownloadZip(){
        WebhookProcesser.downloadRevision("44ccb7345a39b21e67effa10101e9e61157b6526");
        File file = new File("revision.zip");
        assertTrue(file.exists());
    }

    /**
     * Test the ability of the server to download and extract a specific copy of the repo.
     */
    @Test
    void main_ValidInput_ExtractZip(){
        WebhookProcesser.downloadRevision("44ccb7345a39b21e67effa10101e9e61157b6526");
        WebhookProcesser.extractZip();
        File file = new File("extracted/KTH-DD2480-CI-Lab-2-44ccb7345a39b21e67effa10101e9e61157b6526");
        assertTrue(file.exists());
    }

    /**
     * Test the ability of the server to download, extract and run tests on a specific copy of the repo.
     */
    @Test
    void main_ValidInput_RunTests(){
        WebhookProcesser.downloadRevision("44ccb7345a39b21e67effa10101e9e61157b6526");
        WebhookProcesser.extractZip();
        try {
            WebhookProcesser.runTests("44ccb7345a39b21e67effa10101e9e61157b6526");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File("buildlogs/sha=44ccb7345a39b21e67effa10101e9e61157b6526.txt");
        assertTrue(file.exists());
    }






}
