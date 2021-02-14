import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import java.io.*;

public class HistoryProcesserTest {
    /**
     * Creates test files which HistoryProcesser can compile.
     */

    static String path = "src/test/buildlogs/";

    /**
     * Create some json testfiles
     * @param numFiles the number of testfiles we want to create
     * @throws IOException
     */
    void init_testfiles(int numFiles) throws IOException {
        for (int i = 0; i < numFiles; i++) {
            try {
                PrintStream out = new PrintStream(new FileOutputStream(path + "testfile" + i + ".json"));
                out.print("{ \"test\": true, \"number\": " + i + " }");
                out.close();
            } catch (IOException e) {
                System.err.println("An error occurred while testing the HistoryProcesser.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Tests the HistoryProcesser with valid input
     * @throws IOException
     */
    @Test
    void history_valid_input() throws IOException {
        init_testfiles(3);
        String expected = "{ builds: [{ \"test\": true, \"number\": 0 }, {  \"test\": true, \"number\": 1 }, {  \"test\": true, \"number\": 2 }] }";
        try {
            JSONObject JSONtest = new JSONObject((new HistoryProcesser(path)).getAllBuilds());
            JSONAssert.assertEquals(expected, JSONtest, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the HistoryProcesser with input that should not be equal
     * @throws IOException
     */
    @Test
    void history_invalid_input() throws IOException {
        init_testfiles(3);
        String expected = "{ builds: [{ \"test\": true, \"number\": 1 }, {  \"test\": true, \"number\": 2 }, {  \"test\": true, \"number\": 3 }] }";
        try {
            JSONObject JSONtest = new JSONObject((new HistoryProcesser(path)).getAllBuilds());
            JSONAssert.assertNotEquals(expected, JSONtest, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the HistoryProcesser with empty input
     * @throws IOException
     */
    @Test
    void history_empty_input() {
        String expected = "{ builds: [] }";
        try {
            JSONObject JSONtest = new JSONObject((new HistoryProcesser(path)).getAllBuilds());
            JSONAssert.assertEquals(expected, JSONtest, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cleans all testfiles with filename 'testfileX.json'
     */
    @AfterEach
    public void cleanUp() {
        // Delete all testfiles
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                if (!file.getName().equals("dummy.txt") && file.getName().substring(0,8).equals("testfile")) {
                    file.delete();
                }
            }
        } else {
            System.err.println("Error: directory not found while deleting");
        }
    }
}
