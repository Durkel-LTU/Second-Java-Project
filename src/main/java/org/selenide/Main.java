package org.selenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import static com.codeborne.selenide.Selenide.*;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Starting Logger");
        String targetURL = "https://ltu.se";
        GUI login = new GUI();
        String[] credentials = login.getLoginCredentials(targetURL);
        logger.info("Fetched credentials");

        try {
            BrowserConfig.setConfig();
            open(targetURL);

            if (Objects.equals(title(), "Luleå tekniska universitet, LTU")) {
                logger.info("Successfully opened webpage.");
            } else {
                logger.error("Failed to open the webpage.");
            }

        } catch (Exception e) {
            logger.error("Failed to open website");
        }

        LTUWebfunctions.AcceptCookies(); //Find "Accept cookies" button and click it.
        boolean result = LTUWebfunctions.OpenStudentPage();
        if (result) {
            logger.info("Student page opened successfully.");
        } else {
            logger.error("Failed to open student page.");
        }

        //Find and click on "Logga in" button to go to the login page

        if (LTUWebfunctions.loggaIn()) {
            logger.info("Clicked on 'Logga in' button.");
        } else {
            logger.error("Failed to click on 'Logga in' button.");
        }

        //Fill in the login form with credentials
        boolean loginSuccessful = LTUWebfunctions.fillInCredentials(credentials[0], credentials[1]);
        if (loginSuccessful) {
            logger.info("Successfully logged in");
        } else {
            logger.error("Failed to log in");
        }

        boolean downloadSuccessful = LTUWebfunctions.transcriptDownload();
        if (downloadSuccessful) {
            logger.info("Successfully downloaded transcript.");
        } else {
            logger.error("Failed to download transcript.");
        }
        LTUWebfunctions.kronoxSearch(); //Search for Kronox in the search bar and click on the first result.
    }
}
