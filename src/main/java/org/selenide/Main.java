package org.selenide;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

import java.util.Objects;

import static com.codeborne.selenide.Condition.text;
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

            Configuration.browserSize = "1920x1080";

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
            if(Objects.equals(title(), "Aktuellt - ltu.se") || Objects.equals(title(), "Update - ltu.se")) {
                logger.info("Successfully logged in");
            } else {
                throw new LoginException("Failed to log in. Invalid credentials or login page not loaded.");
            }
        } catch (Exception e) {
            logger.error("Failed to log in due to exception: {}", e.getMessage());
        }

        // Open transcripts download on new tab, download then close it.
        transcriptDownload();

        /*
         * Open "Examination" dropdown and click the "Examination Schedule" menu button.
         */
        try {
            if(Objects.equals(title(), "Update - ltu.se") || Objects.equals(title(), "Aktuellt - ltu.se")) {
                logger.info("Correct website is open");
                if ($(By.xpath("//a[contains(text(),'Tentamen')]")).exists()) {
                    $(By.xpath("//a[contains(text(),'Tentamen')]")).click();
                    logger.info("Successfully opened the 'Examination' dropdown");
                    if ($(By.xpath("//a[contains(text(),'Tentamensschema')]")).exists()) {
                        $(By.xpath("//a[contains(text(),'Tentamensschema')]")).click();
                        logger.info("Successfully clicked the 'Examination' button");

                    }
                } else {
                    logger.error("Cannot find the 'Examination' dropdown button.");
                }
            } else {
                logger.error("Failed to open the correct website.");
            }
        } catch (Exception e) {
            logger.error("Failed to log in due to exception: {}", e.getMessage());
        }
    }

    /**
     * Downloads the Transcripts from Ladok website.
     */
    public static void transcriptDownload() {

        try {
            if ($(By.xpath("//a[contains(text(),'Intyg')]")).exists()) {
                $(By.xpath("//a[contains(text(),'Intyg')]")).click();
                logger.info("Successfully opened Ladok website on a new tab");
                // Switches Selenide to the Ladok website, checking if the title of the website exists
                switchTo().window("Studentwebb");
            } else {
                logger.error("Cannot open Ladok website.");
            }
        } catch (Exception e) {
            logger.error("Cannot locate Ladok website.");
        }

        // Locate the span element using XPath
        SelenideElement spanElement = $x("/html/body/ladok-root/div/main/div/ladok-inloggning/div/div/div/div/div/div/div/ladok-student/div[1]/a/div/div[2]/span[2]");
        try {
            if (spanElement.shouldHave(text("Access through your institution")).exists()) {
                spanElement.shouldHave(text("Access through your institution")).click();
                logger.info("Successfully continued in the Ladok website");
            } else {
                logger.error("Cannot open Ladok website.");
            }
        } catch (Exception e) {
            logger.error("Cannot locate Ladok website.");
        }

        try {
            SelenideElement searchBar = $x("//*[@id='searchinput']");
            if (searchBar.exists()) {
                searchBar.setValue("Luleå");
                // Wait 5 seconds for results to appear
                sleep(5000);
                if ($(By.xpath("//a[contains(@class, 'institution')]")).exists()) {
                    $(By.xpath("//a[contains(@class, 'institution')]")).click();
                    logger.info("Successfully opened LTU Ladok registry");
                } else {
                    logger.error("Cannot locate Luleå on search list.");
                }
            } else {
                logger.error("Cannot find Search bar.");
            }
        } catch (Exception e) {
            logger.error("Cannot locate search input on Ladok webpage.");
        }

        sleep(15000);

        // Locate the link element
        SelenideElement transcriptsLink = $x("//a[contains(text(), 'Transcripts') or contains(text(), 'Intyg')]");

        try {
            if (transcriptsLink.exists()) {
                transcriptsLink.click();
                logger.info("Successfully opened transcripts");
            } else {
                logger.error("Cannot open Transcripts");
            }
        } catch (Exception e) {
            logger.error("Cannot locate ladok webpage.");
        }

        // We sleep for webpage to fully load, as it loads in browser,
        // and once again the webpages content, which can throw possible errors for future steps.
        sleep(5000);

        // Check for button, both in english and swedish.
        SelenideElement transcriptsButton = $x("//button[contains(text(), 'Create') or contains(text(), 'Skapa Intyg')]");
        try {
            if (transcriptsButton.exists()) {
                transcriptsButton.click();
                logger.info("Successfully created transcript.");
            } else {
                logger.error("Cannot create transcripts");
            }
        } catch (Exception e) {
            logger.error("Error creating the transcripts.");
        }

        /*
         * Opens dropdown and selects the correct option, then downloads the document.
         */
        // Locate the dropdown element
        SelenideElement dropdown = $x("//*[@id='intygstyp']");
        SelenideElement createTranscriptsButton = $x("/html/body/ladok-root/div/main/div/ladok-skapa-intyg/ladok-card/div/div/ladok-card-body/div[3]/div/form/div[3]/div/ladok-skapa-intyg-knapprad/div/button[1]/span");
        // Open the dropdown
        dropdown.click();

        try {
            // Select the option with the value "1: Object", which is registration.
            SelenideElement option = $("option[value='1: Object']");
            option.click();

            // Download the transcripts if button is there.
            if (createTranscriptsButton.exists()) {
                createTranscriptsButton.click();
                logger.info("Downloaded certificate of registration successfully.");
            } else {
                logger.error("Cannot locate button to download certificate of registration.");
            }
        } catch (Exception e) {
            logger.error("Cannot locate dropdown option for registration.");
        }

        // Locate all PDF links with wildcard
        ElementsCollection pdfLinks = $$x("//a[contains(@href, '/intyg/') and contains(@href, '/pdf')]");

        // Finds and downloads the first link, which should be latest created transcript.
        try {
            if (pdfLinks.size() > 0) {
                SelenideElement firstPdfLink = pdfLinks.first();
                firstPdfLink.download();

                logger.info("Clicked on pdf link: " + firstPdfLink);
            } else {
                System.out.println("No PDF links found.");
            }
        } catch (Exception e) {
            logger.error("Cannot download transcript");
        }
    }

    /**
     * Searches for the exam time and creates a screenshot of the webpage.
     */
    public static void kronoxSearch() {

    }
}
