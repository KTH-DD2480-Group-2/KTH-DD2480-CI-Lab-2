import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class HistoryProcesser {
    private static String builds;

    public HistoryProcesser() throws IOException {
        JSONObject jsonBuilds = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String path = "../../../buildlogs";
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                if (!file.getName().equals("dummy.txt")) {
                    // Do something with child
                    FileReader fileText = new FileReader(file);
                    // Add json file to array
                    jsonArray.put(new JSONObject(fileText.read()));
                }
            }
            jsonBuilds.put("builds", jsonArray);
            builds = jsonBuilds.toString();
        } else {
            System.err.println("Error: directory not found");
        }
    }

    public static String getAllBuilds() {
        return builds;
    }
}
