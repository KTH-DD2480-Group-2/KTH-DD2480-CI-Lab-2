
import org.json.JSONObject;


public class RunnableWebhookProcesser implements Runnable{
    private JSONObject webhookJsonData;

    public RunnableWebhookProcesser(JSONObject webhookJsonData) {
        this.webhookJsonData = webhookJsonData;
    }

    public void run() {
        try {
            WebhookProcesser.handleWebhookEvent(this.webhookJsonData);
        } catch (Exception e) {
        }
        System.out.print("Done handling webhook");
    }
}