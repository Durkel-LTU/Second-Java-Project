package org.selenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import static com.codeborne.selenide.Selenide.*;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String TITLE_EXPECTED = "Luleå tekniska universitet, LTU";
    private static final String SITE_URL = "https://ltu.se";

    public static void main(String[] args) {


        System.setProperty("logback.configurationFile", "jetbrains://idea/navigate/reference?project=Second-Java-Project&path=logback.xml");

        logger.info("Starting Logger");

        String[] credentials = getCredentials();
        if (credentials == null) {
            logger.error("Credentials are invalid.");
            return;
        }

        try {
            initializeBrowser();
            performWebActions(credentials);
            logger.info("Web actions completed successfully.");
        } catch (Exception e) {
            logger.error("Error during web actions:", e);
        }
    }

    private static String[] getCredentials() {
        GUI gui = new GUI();
        return gui.getLoginCredentials(SITE_URL);
    }

    private static void initializeBrowser() throws Exception {
        BrowserConfig.setConfig();
        open(SITE_URL);

        if (!Objects.equals(title(), TITLE_EXPECTED)) {
            throw new Exception("Failed to open the webpage.");
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
