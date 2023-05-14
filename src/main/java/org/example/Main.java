package org.example;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

import java.util.Objects;

import static com.codeborne.selenide.Selenide.*;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Starting Logger");
        String targetURL ="https://ltu.se";
        /*
         * Getting username and password
         */
        GUI login = new GUI();
        String[] credentials = login.getLoginCredentials(targetURL);
        logger.info("Fetched credentials");
        /*
         * Open website LTU.se
         */
        try {
            // Set the browser configuration
            Configuration.browser = "chrome";

            // For automation, chrome browser wont shot if this is true.
            Configuration.headless = false;

            open(targetURL);

            if(Objects.equals(title(), "Luleå tekniska universitet, LTU")) {
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
        /*
         * Find and click on "Logga in" button to go to the login page
         */
        try {
            if ($(By.xpath("//a[contains(text(),'Logga in')]")).exists()) {
                $(By.xpath("//a[contains(text(),'Logga in')]")).click();
                logger.info("Clicked on 'Logga in' button.");
            }
        } catch (Exception e) {
            logger.error("Failed to click on 'Logga in' button.");
        }
        /*
         * Fill in the login form with credentials
         */
        try {
            $(By.id("username")).setValue(credentials[0]);
            $(By.id("password")).setValue(credentials[1]);
            $(By.name("submit")).click();
            if(Objects.equals(title(), "Aktuellt - ltu.se")) {
                logger.info("Successfully logged in");
            } else {
                throw new LoginException("Failed to log in. Invalid credentials or login page not loaded.");
            }
        } catch (Exception e) {
            logger.error("Failed to log in due to exception: {}", e.getMessage());
        }

    }

}
