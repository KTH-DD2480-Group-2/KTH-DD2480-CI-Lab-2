
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.json.JSONObject;

import org.eclipse.jetty.server.Handler;
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

        ContextHandler frontendHandler = createFrontendHandler(server);
        ContinuousIntegrationServer backendHandler = new ContinuousIntegrationServer();
        HandlerList handlers = new HandlerList();
	    handlers.setHandlers(new Handler[] { frontendHandler, backendHandler});

	    server.setHandler(handlers);
        server.start();
        server.join();
    }

    private static ContextHandler createFrontendHandler(Server server) {
        ContextHandler ctx = new ContextHandler("/dashboard");
        ResourceHandler resourceHandler = new ResourceHandler();
	    
	    resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
	    resourceHandler.setResourceBase("src/main/webapp/ci-frontend/build");
        ctx.setHandler(resourceHandler);
        return ctx;
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
                // WebhookProcesser.handleWebhookEvent(request);
                // // Need to start the webhook in a thread as the github webhook has a short timeout
                JSONObject webhookJsonData = WebhookProcesser.payloadToJSON(request);
                RunnableWebhookProcesser runnableWebhookProcesser = new RunnableWebhookProcesser(webhookJsonData);
                Thread webhookProcesserThread = new Thread(runnableWebhookProcesser);
                webhookProcesserThread.start();

            }
            default -> response.getWriter().print("404. Page not found");
        }
    }
}