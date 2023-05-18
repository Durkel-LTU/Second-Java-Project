package org.selenide;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {

    @Test
    public void mainTest() {
        // Redirect standard error for test
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));

        // Invoke main method
        Main.main(new String[]{});

        // Check if some the expected logs appeared on standard error (as that's where logger.error prints)
        String output = outContent.toString();
        assertTrue(output.contains("Starting Logger"));
        assertTrue(output.contains("Credentials are invalid") || output.contains("Web actions completed successfully"));

        // Reset standard error to original stream after test
        System.setErr(System.err);
    }
}
