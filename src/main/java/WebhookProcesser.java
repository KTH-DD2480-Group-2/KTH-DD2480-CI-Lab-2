import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.net.URL;

import org.json.JSONObject;

public class WebhookProcesser {

    private WebhookProcesser() {
    }

    /**
     * Handles a webhook request and runs maven tests and build the project
     * depending on the content in the request.
     *
     * @param request The request data.
     */
    public static void handleWebhookEvent(HttpServletRequest request) throws IOException {

        JSONObject json = payloadToJSON(request);

        String commitSHA = json.get("after").toString();

        downloadRevision(commitSHA);
    }

    /**
     * Will extract and convert the payload to a json file.
     *
     * @param request the request that contains the payload.
     * @return a JSON object representing the payload.
     * @throws UnsupportedEncodingException
     */
    private static JSONObject payloadToJSON(HttpServletRequest request) throws IOException {
        StringBuilder builder = new StringBuilder();
        String aux = "";
        request.setCharacterEncoding("utf-8");

        while ((aux = request.getReader().readLine()) != null) {
            builder.append(aux);
        }

        return new JSONObject(builder.toString());
    }

    /**
     * Downloads a revision in a single zip file.
     *
     * @param commitSHA the SHA-code of the revision.
     */
    private static void downloadRevision(String commitSHA) {
        String revisionLink = "https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/archive/" + commitSHA + ".zip";
        try (
                BufferedInputStream inputStream = new BufferedInputStream(new URL(revisionLink).openStream());
                FileOutputStream fileOS = new FileOutputStream("revision.zip")
        ) {
            byte data[] = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
