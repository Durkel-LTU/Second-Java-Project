package org.selenide;

import com.codeborne.selenide.*;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

import java.awt.image.BufferedImage;
import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import java.util.Set;
public class LTUWebfunctions {

    private static final Logger logger = LoggerFactory.getLogger(LTUWebfunctions.class);

    public static boolean acceptCookies() {

        logger.info("Finding and accepting all cookies on website");
        try {
            SelenideElement cookiesButton = $(byXpath("//button[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']"));
            if (cookiesButton.exists()) {
                cookiesButton.click();
                logger.info("Successfully accepted cookies on website.");
            }
        } catch (Exception e) {
            logger.info("Failed to accept cookies.");
            return false;
        }
        return true;
    }

    public static boolean openStudentPage() {
        try {
            SelenideElement studentButton = $(byXpath("/html/body/header/div[2]/div[1]/div[1]/div[3]/div/a[1]"));
            if (studentButton.exists()) {
                studentButton.click();
                logger.info("Opened 'Student' page.");
                return true;
            }
        } catch (Exception e) {
            logger.info("Failed to open student page.");
        }
        return false;
    }

    public static boolean kronoxSearch() {
        /*
         * Open "Examination" dropdown and click the "Examination Schedule" menu button.
         */
        try {
            if (Objects.equals(title(), "Update - ltu.se") || Objects.equals(title(), "Aktuellt - ltu.se")) {
                logger.info("Correct website is open");
                if ($(byXpath("//a[contains(text(),'Tentamen')]")).exists()) {
                    $(byXpath("//a[contains(text(),'Tentamen')]")).click();
                    logger.info("Successfully opened the 'Examination' dropdown");
                    if ($(byXpath("//a[contains(text(),'Tentamensschema')]")).exists()) {
                        $(byXpath("//a[contains(text(),'Tentamensschema')]")).click();
                        logger.info("Successfully clicked the 'Examination schedule' button");

                    }
                } else {
                    logger.info("Cannot find the 'Examination' dropdown button.");
                }
            } else {
                logger.info("Failed to open the correct website.");
            }
        } catch (Exception e) {
            logger.info("Failed to log in due to exception: {}", e.getMessage());
            return false;
        }

        // Switches Selenide to the Ladok website, checking if the title of the website exists
        try {
        switchTo().window("KronoX Web");
        SelenideElement searchField = $(byXpath("//*[@id='enkel_sokfalt']"));
            if (searchField.exists()) {
                searchField.setValue("i0015n");
                logger.info("Successfully opened the 'Examination' dropdown");
                SelenideElement searchButton = $(byXpath("//*[@id='enkel_sokknapp']"));
                if (searchButton.exists()) {
                    searchButton.click();
                    logger.info("Searching for i0015n exams");

                }
            } else {
                logger.info("Cannot find the search bar");
            }

        } catch (Exception e) {
            logger.info("Cannot locate Kronox website.");
            return false;
        }
        try {
        sleep(5000);

        // Find all the list items using the specified XPath
        // Locate all PDF links with wildcard
        ElementsCollection listItems = $$x("//a[contains(@href, 'schema.ltu.se') and contains(@href, '/setup/')]");


            listItems.first().click();
            logger.info("Opened first link.");
        } catch (Exception e) {
            logger.info("Cannot find first element");
            return false;
        }

        try {
        switchTo().window("Schema");

        // Take a screenshot of the whole page
        File screenshot = Screenshots.takeScreenShotAsFile();

        // Specify the path and name of the screenshot file
        String destinationDirectoryPath = System.getProperty("user.dir") + File.separator + "target" + File.separator + "screenshots";
        String screenshotPath = destinationDirectoryPath + File.separator + "final_examination.jpg";

        // Convert the screenshot to a JPG file
        File screenshotJpg = new File(screenshotPath);

            // Save the screenshot as a JPG file
            assert screenshot != null;
            BufferedImage screenshotImage = ImageIO.read(screenshot);
            ImageIO.write(screenshotImage, "jpg", screenshotJpg);
            FileUtils.copyFile(screenshot, screenshotJpg);
            logger.info("Image saved at " + screenshotPath);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Image cannot be saved");
            return false;
        }
        try {
        sleep(5000);

        Selenide.closeWindow(); // Close the "Schema" window
        Selenide.switchTo().window("KronoX Web");

        Selenide.closeWindow(); // Close the "KronoX Web" window

       Set<String> remainingWindows = WebDriverRunner.getWebDriver().getWindowHandles();
       switchTo().window(remainingWindows.iterator().next()); // Switch back to the original tab


            Thread.sleep(5000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean loggaIn() {
        try {
            SelenideElement loginButton = $(Selectors.byXpath("//a[contains(text(),'Logga in')]"));
            if (loginButton.exists()) {
                loginButton.click();
                logger.info("Clicked on 'Logga in' button.");
                return true;
            } else {
                logger.info("'Logga in' button not found.");
            }
        } catch (Exception e) {
            logger.info("Failed to click on 'Logga in' button due to exception:  {}", e.getMessage());

        }
        return false;
    }

    public static boolean fillInCredentials(String username, String password) {
        try {
            $("#username").setValue(username);
            $("#password").setValue(password);
            $("[name='submit']").click();
            if (Objects.equals(title(), "Aktuellt - ltu.se") || Objects.equals(title(), "Update - ltu.se")) {
                logger.info("Successfully logged in");
                return true;
            } else {
                throw new LoginException("Failed to log in. Invalid credentials or login page not loaded.");
            }
        } catch (Exception e) {
            logger.error("Failed to log in due to exception: {}", e.getMessage());
            return false;
        }

    }

    public static boolean transcriptDownload() {
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
            return false;
        }

        // Locate the login button
        try {
            SelenideElement ladokLoginButton = $(byXpath("//a[contains(@class, 'btn-ladok-inloggning')]"));
            if (ladokLoginButton.exists()) {
                ladokLoginButton.click();
                logger.info("Clicked on Ladok login button.");
            }
        } catch (Exception e) {
            logger.error("Failed to click on Ladok login button.");
            return false;
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
            return false;
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
            return false;
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
            return false;
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
            return false;
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
            return false;
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
                return false;
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
        return true;
    }
    static boolean syllabusDownload() {
        try {
            sleep(2000);
            SelenideElement utbildningMenu = $$(byXpath("//a[@class='toggleTopMenu']")).get(1);
            if (utbildningMenu.exists()) {
                utbildningMenu.click();
            } else {
                logger.error("Failed to find and click on 'Utbildning'");
            }
        } catch (Exception e) {
            logger.error("Failed to click on 'Utbildning'" );
            return false;
        }


        try {
            sleep(2000);
            SelenideElement courseLink = $(".ltu-search-btn");

            if (courseLink.exists()) {
                courseLink.click();
                logger.info("Successfully clicked on 'Kurskatalog - programstudenter »'");
            } else {
                logger.error("Failed to find and click on 'Kurskatalog - programstudenter »'");
            }
        } catch (Exception e) {
            logger.error("Failed to open course webpage.");
            return false;
        }

        try {
            sleep(2000);
            SelenideElement searchInput = $(byXpath("//input[@id='cludo-search-bar-input']"));
            searchInput.setValue("Test av IT");
            logger.info("Successfully filled in the search term 'Test av IT'");
        } catch (Exception e) {
            logger.error("Failed to fill in the search term 'Test av IT'");
            return false;
        }

        try {
            SelenideElement searchButton = $(byXpath("//button[contains(@class, 'button is-medium is-info') and normalize-space()='Sök']"));
            if (searchButton.exists()) {
                searchButton.click();
                logger.info("Successfully clicked on the search button.");
            }
        } catch (Exception e) {
            logger.error("Failed to click on the search button");
            return false;
        }

        try {
            sleep(2000);
            SelenideElement searchResult = $(byXpath("//a[contains(text(), 'Kursplan')]"));
            if (searchResult.exists()) {
                searchResult.click();
                logger.info("Successfully clicked on the search result 'Test av IT-system'");
            } else {
                logger.error("Failed to find and click on the search result 'Test av IT-system'");
            }
        } catch (Exception e) {
            logger.error("Failed to open the search result");
            return false;
        }

        sleep(2000);
        try {
            SelenideElement syllabusLink = $(byXpath("//a[@class='utbplan-pdf-link']"));
            if (syllabusLink.exists()) {
                syllabusLink.click();
                logger.info("Successfully opened the syllabus page");
            } else {
                logger.error("Failed to open the syllabus page");
            }
        } catch (Exception e) {
            logger.error("Failed to open the syllabus webpage.");
            return false;
        }

        try {
            File pdf = $(".utbplan-pdf-link[href]").download();

            File targetDirectory = new File("target/downloads");
            if (!targetDirectory.exists()) {
                targetDirectory.mkdirs();
            }

            Files.copy(pdf.toPath(), Paths.get(targetDirectory.getAbsolutePath(), pdf.getName()));
            logger.info("Downloading syllabus file");

            return true;
        } catch (Exception e) {
            logger.error("Error occurred while downloading the syllabus: {}", e.getMessage());
            return false;
        }
    }


}






