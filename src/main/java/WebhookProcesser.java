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



public class WebhookProcesser {

    private WebhookProcesser() {
    }

    /**
     * Handles a webhook request and runs maven tests and build the project
     * depending on the content in the request.
     *
     * @param request The request data.
     */
    public static void handleWebhookEvent(JSONObject json) throws IOException {
        String commitSHA = json.get("after").toString();

        downloadRevision(commitSHA);

        extractZip();

        JsonObject jsonObj = runBuild(commitSHA);

        set_build_result(commitSHA, check_build_succeeded(jsonObj));

    }

    /**
     * Will extract and convert the payload to a json file.
     *
     * @param request the request that contains the payload.
     * @return a JSON object representing the payload.
     * @throws UnsupportedEncodingException
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
    public static JsonObject runBuild(String commitSHA) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe", "/c", "mvn clean install");
        processBuilder.directory(new File("extracted\\KTH-DD2480-CI-Lab-2-" + commitSHA));

        JsonObjectBuilder json = Json.createObjectBuilder();

        try {
            Process process = processBuilder.start();

            //collect test data for JSON log
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
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
            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);

            //save the JSON to file
            String jsonString = json.build().toString();
            try (PrintStream out = new PrintStream(new FileOutputStream("buildlogs/sha=" + commitSHA + ".json"))) {
                out.print(jsonString);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return json.build();
    }

    /**
     * Checks if build succeeded or not
     */
    public static boolean check_build_succeeded(JsonObject json) {
        // Fetch info about errors and failures from JSON
        int fails = json.getInt("Failures");
        int errors = json.getInt("Errors");

        if (fails > 0 || errors > 0) {
            return false;
        }
        return true;
    }

    /**
     * Processes info from commit and sets CI build result status, returns response as a String
     */
    public static String set_build_result(String commitSHA, boolean buildSuccess) {
        String str = "";

        // Set up HTTP Post Request for sending JSON
        try {
            URL url = new URL("https://api.github.com/repos/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/statuses/"
                    + commitSHA + "?access_token=d5add3ff3a3245a54a4b0985cb223639a802f1bd");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/vnd.github.v3+json; charset=UTF-8");
            con.setRequestProperty("Accept-Type", "application/vnd.github.v3+json; charset=UTF-8");
            con.setDoOutput(true);

            byte[] out;

            // If there are failures, set status to failure, else set to success
            if (!buildSuccess) {
                out = "{\"state\":\"failure\"}" .getBytes(StandardCharsets.UTF_8);
            } else {
                out = "{\"state\":\"success\"}" .getBytes(StandardCharsets.UTF_8);
            }

            int length = out.length;

            con.setFixedLengthStreamingMode(length);
            con.connect();
            try(OutputStream os = con.getOutputStream()) {
                os.write(out);
            }

            /*
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                str = response.toString();
            }
             */

            str = Integer.toString(con.getResponseCode());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static boolean commit_status_success_check(String commitSHA) {
        boolean ret = false;
        String str = "";
        try {
            URL url = new URL("https://api.github.com/repos/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/commits/"
                    + commitSHA + "/status");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            Pattern pattern = Pattern.compile("(success|failure|pending|error)");
            Matcher matcher;

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                matcher = pattern.matcher(inputLine);
                if (matcher.find()) {
                    str = matcher.group(1);
                }
            }
            in.close();
            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (str.equals("success")) {
            ret = true;
        }

        return ret;
    }
}
