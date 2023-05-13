package org.example;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.codeborne.selenide.Configuration;
import static com.codeborne.selenide.Selenide.*;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Logger");

        /*
         * Open website LTU.se
         */
        try {
            // Set the browser configuration
            Configuration.browser = "chrome";

            // For automation, chrome browser wont shot if this is true.
            Configuration.headless = false;

            open("https://ltu.se");

            if(title().equals("Luleå tekniska universitet, LTU")) {
                logger.info("Successfully opened webpage.");
            } else {
                logger.error("Failed to open the webpage.");
            }
            // Open a website
        } catch (Exception e) {
            logger.error("Failed to open website");
        }

        /*
         * Finding cookies modal and accepting it
         */
        logger.info("Finding and accept all cookies on website");
        try {
            if ($(By.xpath("//button[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']")).exists()) {
                $(By.xpath("//button[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']")).click();
                logger.info("Successfully accepted cookies on website.");
            }
        } catch (Exception e) {
            logger.error("Failed to accept cookies.");
        }


        /*
         * Find "Student" button and click it to redirect to LTU login.
         */
        try {
            if ($(By.xpath("/html/body/header/div[2]/div[1]/div[1]/div[3]/div/a[1]")).exists()) {
                $(By.xpath("/html/body/header/div[2]/div[1]/div[1]/div[3]/div/a[1]")).click();
                logger.info("Opened 'Student' page.");
            }
        } catch (Exception e) {
            logger.error("Failed to open student page.");
        }
    }

}
