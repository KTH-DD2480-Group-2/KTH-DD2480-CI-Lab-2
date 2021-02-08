import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.json.JSONObject;

public class WebhookProcesser {

    private WebhookProcesser() {}

    /**
     * Handles a webhook request and runs maven tests and build the project
     * depending on the content in the request.
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
        System.out.println(builder.toString());
        System.out.println(json.get("zen").toString());
        // Here you do all the continuous integration tasks.
        // For example:
        // 1st clone your repository
        // 2nd compile the code
    }
}
