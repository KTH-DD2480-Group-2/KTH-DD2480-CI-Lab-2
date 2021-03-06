import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.json.JSONObject;

import net.lingala.zip4j.core.ZipFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class processes a webhook request sent after a commit to the GitHub repository.
 * Contains methods used to download builds, extract files, run tests and set commit status.
 *
 * @author  Adam Jonsson
 * @author  Hovig Manjikian
 * @author  Isak Vilhelmsson
 * @author  Tony Le
 * @version 1.0
 * @since   1.0
 *
 */
public class WebhookProcesser {

    private WebhookProcesser() {
    }


    /**
     * Handles a webhook request and runs maven tests and build the project
     * depending on the content in the request.
     *
     * @param json JSON object containing push data.
     * @throws IOException
     */
    public static void handleWebhookEvent(JSONObject json) throws IOException {
        String commitSHA = json.get("after").toString();

        setCommitStatus("pending", commitSHA);
        
        downloadRevision(commitSHA);
        
        extractZip();

        setCommitStatus("in_progress", commitSHA);

        JsonObject buildResultAsJSON = runBuild(commitSHA);

        if (isBuildSuccess(buildResultAsJSON)) {
            setCommitStatus("success", commitSHA);
        }
        else {
            setCommitStatus("failure", commitSHA);
        }
    }

    /**
     * Will extract and convert the payload to a json file.
     *
     * @param   request the request that contains the payload.
     * @return  a JSON object representing the payload.
     * @throws  UnsupportedEncodingException
     */
    public static JSONObject payloadToJSON(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("utf-8");
        StringBuilder builder = new StringBuilder();
        String aux = "";
        request.setCharacterEncoding("utf-8");

        while ((aux = request.getReader().readLine()) != null) {
            builder.append(aux);
        }

        String payloadAsString = builder.toString();
        return new JSONObject(payloadAsString);
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
    public static void extractZip() {
        try {
            ZipFile zipFile = new ZipFile("revision.zip");
            zipFile.extractAll("extracted");
        } catch (net.lingala.zip4j.exception.ZipException e){
            System.out.println(e.toString());
        }
    }


    /**
     * Builds and runs the tests and saves the results in JSON format.
     * Used after the contents of the zip has been extracted.
     *
     * @param   commitSHA String containing the hash of the particular commit to run.
     * @return  JSON object containing the test results of the build.
     * @throws  IOException
     */
    public static JsonObject runBuild(String commitSHA) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe", "/c", "mvn clean install");
        processBuilder.directory(new File("extracted\\KTH-DD2480-CI-Lab-2-" + commitSHA));
        JsonObject buildResult = Json.createObjectBuilder().build();
        try {
            Process process = processBuilder.start();
            buildResult =  getBuildResultAsJson(process, commitSHA);
            saveJsonAsFile(buildResult, commitSHA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildResult;
    }


    /**
     * Parses JSON containing build result in a more intuitive way.
     *
     * @param process Process object which the build runs on.
     * @return JSON object containing the test results of the build.
     */
    private static JsonObject getBuildResultAsJson(Process process, String commitSHA) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        JsonObject jsonResult = Json.createObjectBuilder().build();
        try {
         //collect test data for JSON log
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("Tests run")){
                    JsonObjectBuilder array = Json.createObjectBuilder();
                    List<String> ints = new ArrayList<String>();
                    Matcher matcher = Pattern.compile("[0-9]")
                            .matcher(line);
                    while (matcher.find()) {
                        ints.add(matcher.group());
                    }
                    array.add("Tests run", ints.get(0));
                    array.add("Failures", ints.get(1));
                    array.add("Errors", ints.get(2));
                    array.add("Skipped", ints.get(3));
                    json.add("statistics", array);
                }
                else if (line.contains("BUILD"))
                    json.add("result", line.substring(7,line.length()));
                else if (line.contains("Total time")){
                    json.add("time", line.substring(20,line.length()));
                    System.out.println(line);
                }
                else if(line.contains("Finished at"))
                    json.add("endTime", line.substring(20,line.length()));
            }
            json.add("commitSHA", commitSHA);
            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
            jsonResult = json.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return jsonResult;
    }


    /**
     * Takes a JSON object containing build data and saves it to a file named after the commit hash.
     *
     * @param json JSON object containing the test results of a build.
     * @param commitSHA string containing the hash of the tested commit, used for naming.
     */
    private static void saveJsonAsFile(JsonObject json, String commitSHA) {
        try {
            String jsonString = json.toString();
            try (PrintStream out = new PrintStream(new FileOutputStream("buildlogs/sha=" + commitSHA + ".json"))) {
                out.print(jsonString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes a JSON object and fetches the result of the build.
     *
     * @param   buildResult a JSON object that contains the results of the build.
     * @return  true if commit build managed to build without failures or errors, otherwise returns false.
     */
    public static boolean isBuildSuccess(JsonObject buildResult) {
        // Fetch info about errors and failures from JSON
        var jsonStatistics = buildResult.getJsonObject("statistics");
        int fails = Integer.parseInt(jsonStatistics.getString("Failures"));
        int errors = Integer.parseInt(jsonStatistics.getString("Errors"));
        if (fails > 0 || errors > 0) {
            return false;
        }
        return true;
    }

    /**
     * Sends a JSON POST Request to GitHub's API to change the status of a commit.
     *
     * @param status a String that must be either "success", "failure", "pending", or "error" to be valid.
     * @param commitSHA a String that is the hash of the commit to be status-changed.
     */
    private static void setCommitStatus(String status, String commitSHA) {
        // Set up HTTP Post Request for sending JSON
        try {
            String repoAccessToken = System.getenv("KTH_DD2480_CI_TOKEN");
            URL url = new URL("https://api.github.com/repos/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/statuses/"
                    + commitSHA + "?access_token=" + repoAccessToken);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/vnd.github.v3+json; charset=UTF-8");
            con.setRequestProperty("Accept-Type", "application/vnd.github.v3+json; charset=UTF-8");
            con.setDoOutput(true);

            byte[] out;
            out = ("{\"state\":\""+ status +"\", \"context\": \"KTH-DD2480-CI\"}" ).getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            con.setFixedLengthStreamingMode(length);
            con.connect();
            try(OutputStream os = con.getOutputStream()) {
                os.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
