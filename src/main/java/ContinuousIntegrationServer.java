import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.Request;

/**
 * Skeleton of a ContinuousIntegrationServer which acts as webhook
 * See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler {
    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        StartServer(8080);
    }

    public static void StartServer(int portNumber) throws Exception {
        Server server = new Server(portNumber); // Starts the server on port 8080
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        switch (target) {
            case "/" -> response.getWriter().print("CI server working!");
            case "/api/webhook-processer" -> {
                response.getWriter().print("Handling webhook event");
                WebhookProcesser.handleWebhookEvent(request);
            }
            default -> response.getWriter().print("404. Page not found");
        }
    }
}