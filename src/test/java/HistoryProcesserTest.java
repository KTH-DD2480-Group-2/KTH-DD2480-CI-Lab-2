import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

public class HistoryProcesserTest {
    /**
     * Creates test files which HistoryProcesser can compile.
     */
    @BeforeAll
    public static void init() throws IOException {
        String path = "../../../buildslogs/";
        int numFiles = 3;
        for (int i = 0; i < numFiles; i++) {
            try {
                PrintStream out = new PrintStream(new FileOutputStream("buildlogs/testfile" + i + ".json"));
                out.print("{ test: 'true', number: " + i + " }");
            } catch (IOException e) {
                System.err.println("An error occurred while testing the HistoryProcesser.");
                e.printStackTrace();
            }

        }
    }

    @Test
    void firstTest() {
        String result = "{ builds: [{ test: 'true', number: 0 }, { test: 'true', number: 1 }, { test: 'true', number: 2 }] }";
        String test = HistoryProcesser.getAllBuilds();
        assertEquals(result, test);

    }

    @AfterAll
    public static void cleanUp() {
        // Delete all testfiles
    }
}
