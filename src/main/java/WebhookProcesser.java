import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.net.URL;
import java.util.Map;
import java.util.zip.ZipException;

import org.json.JSONObject;

import net.lingala.zip4j.core.ZipFile;

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
    public static void downloadRevision(String commitSHA) {
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

    /**
     * Extracts the zip produced by downloadRevision(String commitSHA).
     */
    public static void extractZip(){
        try {
            ZipFile zipFile = new ZipFile("revision.zip");
            zipFile.extractAll("extracted");
        } catch (net.lingala.zip4j.exception.ZipException e){
            System.out.println(e.toString());
        }
    }

    /**
     * Runs the tests and save the results in JSON format. Used after the contents of the zip has been extracted.
     */
    public static void runTests() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command("cmd.exe", "/c", "mvn test");
        processBuilder.directory(new File("extracted\\KTH-DD2480-CI-Lab-2-44ccb7345a39b21e67effa10101e9e61157b6526"));

        JsonObjectBuilder json = Json.createObjectBuilder();

        try {

            Process process = processBuilder.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Tests run"))
                    json.add("statistics", line.substring(7,line.length()));
                else if (line.contains("BUILD"))
                    json.add("result", line.substring(7,line.length()));
                else if (line.contains("Total time"))
                    json.add("time", line.substring(7,line.length()));
                else if(line.contains("Finished at"))
                    json.add("endTime", line.substring(7,line.length()));
            }

            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);

            //save the JSON to file
            String jsonString = json.build().toString();
            try (PrintStream out = new PrintStream(new FileOutputStream("buildlogs/" + "44ccb7345a39b21e67effa10101e9e61157b6526" + ".txt"))) {
                out.print(jsonString);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
