package org.selenide;

import com.codeborne.selenide.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.Objects;
import com.codeborne.selenide.WebDriverRunner;
import static com.codeborne.selenide.Selectors.byXpath;
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

        transcriptDownload(); // Open transcripts download on new tab, download then close it.
        LTUWebfunctions.kronoxSearch(); //Search for Kronox in the search bar and click on the first result.
    }

    /**
     * Downloads the Transcripts from Ladok website.
     */

        public static void transcriptDownload() {
        try {
            // Open "Transcripts" link in a new tab
            SelenideElement transcriptsLink = $x("//a[contains(text(), 'Transcripts') or contains(text(), 'Intyg')]");
            if (transcriptsLink.exists()) {
                transcriptsLink.click();
                // Switches Selenide to the Ladok website, checking if the title of the website exists
                switchTo().window("Studentwebb");
                logger.info("Successfully opened Ladok website in a new tab");
            } else {
                logger.error("Cannot open Ladok website.");
            }
        } catch (Exception e) {
            logger.error("Cannot locate Ladok website.");
        }

        // Locate the login button
        try {
        SelenideElement ladokLoginButton =$(byXpath("//a[contains(@class, 'btn-ladok-inloggning')]"));
            if (ladokLoginButton.exists()) {
                ladokLoginButton.click();
                logger.info("Clicked on Ladok login button.");
            }
        } catch (Exception e) {
            logger.error("Failed to click on Ladok login button.");
        }

        try {
            // Enter the name of the university in the search bar and select the LTU Ladok registry
            SelenideElement searchBar = $x("//*[@id='searchinput']");
            if (searchBar.exists()) {
                searchBar.setValue("Luleå");
                // Wait 5 seconds for results to appear
                sleep(5000);
                SelenideElement institutionLink = $(byXpath("//a[contains(@class, 'institution')]"));
                if (institutionLink.exists()) {
                    institutionLink.click();
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


        int windowWidth = WebDriverRunner.getWebDriver().manage().window().getSize().getWidth();

        System.out.println("Window width is " + windowWidth);

        // Locate the link element
        SelenideElement transcriptsLink = $x("//a[contains(text(), 'Transcripts') or contains(text(), 'Intyg')]");

        try {
            if (windowWidth < 1600) {
                logger.info("Mobile menu button is showing.");
                SelenideElement mobileMenuButton = $x("//button[@role='button']");
                mobileMenuButton.click();
            } else {
                logger.info("Mobile menu not showing");
            }

            if (transcriptsLink.exists()) {
                transcriptsLink.click();
                logger.info("Successfully opened transcripts");
            } else {
                logger.error("Cannot open Transcripts");
            }
        } catch (Exception e) {
            logger.error("Cannot locate Ladok webpage.");
        }

        // We sleep for webpage to fully load, as it loads in browser,
        // and once again the webpages content, which can throw possible errors for future steps.
        sleep(5000);

        // Check for button, both in english and swedish.
        try {
            ElementsCollection firstCreateButtons = $$("button").filter(Condition.text("Skapa intyg"));
            if (firstCreateButtons.size() == 0) {
                firstCreateButtons = $$("button").filter(Condition.text("Create"));
            }
            if (firstCreateButtons.size() > 0) {
                SelenideElement transcriptsButton = firstCreateButtons.first();
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


        // Open the dropdown
        try {
            SelenideElement option = $x("//*[@id=\"intygstyp\"]/option[2]");
            option.click();
            logger.info("Clicked on option in dropdown menu");
        } catch (Exception e) {
            logger.error("Cannot find dropdown menu");
        }

        // Download the transcripts if button is there.
        try {
            ElementsCollection secondCreateButtons = $$("button").filter(Condition.text("Skapa"));
            if (secondCreateButtons.size() == 0) {
                secondCreateButtons = $$("button").filter(Condition.text("Create"));
            }
            if (secondCreateButtons.size() > 0) {
                SelenideElement createTranscriptsButton = secondCreateButtons.first();
                createTranscriptsButton.click();
                logger.info("Downloaded certificate of registration successfully.");
            } else {
                logger.error("Cannot locate button to create certificate of registration.");
            }
        } catch (Exception e) {
            logger.error("Cannot locate dropdown option for registration.");
        }

        // Locate all PDF links with wildcard
        ElementsCollection pdfLinks = $$x("//a[contains(@href, '/intyg/') and contains(@href, '/pdf')]");

        if (pdfLinks.size() == 0) {
            try {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 20000) {
                    pdfLinks = $$x("//a[contains(@href, '/intyg/') and contains(@href, '/pdf')]");

                    if (pdfLinks.size() > 0) {
                        break;
                    }

                    sleep(2000);
                    logger.info("Website not updated, waiting 2 seconds.");
                }
            } catch (Exception e) {
                logger.error("Took too long time");
            }
            // Finds and downloads the first link, which should be latest created transcript.

            try {
                logger.info("Correct window for downloading pdf.");
                if (pdfLinks.size() > 0) {
                    logger.info("Located " + pdfLinks.size() + " links");
                    SelenideElement firstPdfLink = pdfLinks.first();
                    String link = firstPdfLink.getAttribute("href");

                    // Download the file
                    assert link != null;
                    File downloadedFile = download(link);
                    logger.info("Downloading transcripts. File URL: " + downloadedFile);
                } else {
                    logger.error("No PDF links found to download transcripts.");
                }
            } catch (Exception e) {
                logger.error("Error occurred while downloading transcripts: {}", e.getMessage());
            }

            sleep(8000);
            Selenide.closeWindow();
            // Switch back to the default tab
            Selenide.switchTo().window(WebDriverRunner.getWebDriver().getWindowHandles().iterator().next());
        }
    }


}
