package org.selenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import static com.codeborne.selenide.Selenide.*;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String TITLE_EXPECTED = "Luleå tekniska universitet, LTU";

    public static void main(String[] args) {
        logger.info("Starting Logger");

        GUI gui = new GUI();
        String[] credentials = gui.getLoginCredentials("https://ltu.se");

        if (credentials == null || credentials.length < 2) {
            logger.error("Credentials are invalid.");
            return;
        }

        try {
            BrowserConfig.setConfig();
            open("https://ltu.se");

            if (!Objects.equals(title(), TITLE_EXPECTED)) {
                throw new Exception("Failed to open the webpage.");
            }

        } catch (Exception e) {
            logger.error("Failed to open website due to: ", e);
            return;
        }

        try {
            performWebActions(credentials);
            logger.info("Web actions completed successfully.");
        } catch (Exception e) {
            logger.error("Error during web actions due to: ", e);
        }
    }

    private static void performWebActions(String[] credentials) throws Exception {
        if (!LTUWebfunctions.acceptCookies()) {
            throw new Exception("Failed to accept cookies.");
        }

        if (!LTUWebfunctions.openStudentPage()) {
            throw new Exception("Failed to open student page.");
        }

        if (!LTUWebfunctions.loggaIn()) {
            throw new Exception("Failed to click on 'Logga in' button.");
        }

        if (!LTUWebfunctions.fillInCredentials(credentials[0], credentials[1])) {
            throw new Exception("Failed to fill in credentials.");
        }

        if (!LTUWebfunctions.transcriptDownload()) {
            throw new Exception("Failed to download transcript.");
        }

        if (!LTUWebfunctions.kronoxSearch()) {
            throw new Exception("Failed to perform Kronox search.");
        }
    }
}