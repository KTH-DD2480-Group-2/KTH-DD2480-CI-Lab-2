import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HistoryProcesser {
    private String builds;
    /**
     * Handles the request history API call by compiling all JSON-objects in buildlogs
     * into single JSON object of the form {builds: []}
     *
     */
    public HistoryProcesser(String buildLogsPath) throws IOException {
        JSONObject jsonBuilds = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String path = buildLogsPath;
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                if (!file.getName().equals("dummy.txt")) {
                    FileInputStream input = new FileInputStream(file);
                    byte[] data = new byte[(int) file.length()];
                    input.read(data);
                    input.close();
                    String str = new String(data, "UTF-8");
                    jsonArray.put(new JSONObject(str));
                }
            }
            jsonBuilds.put("builds", jsonArray);
            builds = jsonBuilds.toString();
        } else {
            System.err.println("Error: directory not found");
        }
    }

    /**
     *
     * @return The single history JSON-object as a string
     */
    public String getAllBuilds() {
        return builds;
    }
}
