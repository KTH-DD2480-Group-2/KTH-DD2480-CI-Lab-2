import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONObject;

public class WebhookProcesser {

    private WebhookProcesser() {
    }

    /**
     * Handles a webhook request and runs maven tests and build the project
     * depending on the content in the request.
     *
     * @param request The request data
     */
    public static void handleWebhookEvent(HttpServletRequest request) throws IOException {
        StringBuilder builder = new StringBuilder();
        String aux = "";
        request.setCharacterEncoding("utf-8");

        while ((aux = request.getReader().readLine()) != null) {
            builder.append(aux);
        }
        JSONObject json = new JSONObject(builder.toString());

        String commitSHA = json.get("after").toString();
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
