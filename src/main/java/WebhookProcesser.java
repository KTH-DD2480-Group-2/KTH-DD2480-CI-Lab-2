import javax.servlet.http.HttpServletRequest;

public class WebhookProcesser {

    private WebhookProcesser() {}

    /**
     * Handles a webhook request and runs maven tests and build the project
     * depending on the content in the request.
     * @param request The request data
     */
    public static void handleWebhookEvent(HttpServletRequest request) {
        // Here you do all the continuous integration tasks.
        // For example:
        // 1st clone your repository
        // 2nd compile the code
    }
}
