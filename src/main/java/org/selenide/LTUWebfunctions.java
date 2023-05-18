package org.selenide;

import com.codeborne.selenide.*;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public class LTUWebfunctions {


    private static final java.util.logging.Logger logger = Logger.getLogger(CredentialsManager.class.getName());


    public static void AcceptCookies() {

        logger.info("Finding and accept all cookies on website");
        try {
            SelenideElement cookiesButton = $(byXpath("//button[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']"));
            if (cookiesButton.exists()) {
                cookiesButton.click();
                logger.info("Successfully accepted cookies on website.");
            }
        } catch (Exception e) {
            logger.info("Failed to accept cookies.");
        }
    }

    public static void OpenStudentPage() {
        try {
            SelenideElement studentButton = $(byXpath("/html/body/header/div[2]/div[1]/div[1]/div[3]/div/a[1]"));
            if (studentButton.exists()) {
                studentButton.click();
                logger.info("Opened 'Student' page.");
            }
        } catch (Exception e) {
            logger.info("Failed to open student page.");
        }
    }

    public static void kronoxSearch() {
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
            logger.info("Failed to log in due to exception: {}");
        }


        // Switches Selenide to the Ladok website, checking if the title of the website exists
        switchTo().window("KronoX Web");
        try {
            if ($(byXpath("//*[@id='enkel_sokfalt']")).exists()) {
                $(byXpath("//*[@id='enkel_sokfalt']")).setValue("i0015n");
                logger.info("Successfully opened the 'Examination' dropdown");
                if ($(byXpath("//*[@id='enkel_sokknapp']")).exists()) {
                    $(byXpath("//*[@id='enkel_sokknapp']")).click();
                    logger.info("Searching for i0015n exams");

                }
            } else {
                logger.info("Cannot find the search bar");
            }

        } catch (Exception e) {
            logger.info("Cannot locate Kronox website.");
        }

        sleep(5000);

        // Find all the list items using the specified XPath
        // Locate all PDF links with wildcard
        ElementsCollection listItems = $$x("//a[contains(@href, 'schema.ltu.se') and contains(@href, '/setup/')]");

        try {
            listItems.first().click();
            logger.info("Opened first link.");
        } catch (Exception e) {
            logger.info("Cannot find first element");
        }

        //SelenideElement targetWindow = (SelenideElement) Selenide.switchTo().window("Schema");

        switchTo().window("Schema");
        // Take a screenshot of the whole page
        File screenshot = Screenshots.takeScreenShotAsFile();

        // Specify the path and name of the screenshot file
        String destinationDirectoryPath = System.getProperty("user.dir") + File.separator + "target" + File.separator + "screenshots";
        String screenshotPath = destinationDirectoryPath + File.separator + "final_examination.jpg";

        // Convert the screenshot to a JPG file
        File screenshotJpg = new File(screenshotPath);
        try {
            // Save the screenshot as a JPG file
            assert screenshot != null;
            BufferedImage screenshotImage = ImageIO.read(screenshot);
            ImageIO.write(screenshotImage, "jpg", screenshotJpg);
            FileUtils.copyFile(screenshot, screenshotJpg);
            logger.info("Image saved at " + screenshotPath);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Image cannot be saved");
        }

        sleep(5000);
        Selenide.closeWindow();


        // Switch back to the default tab
        Selenide.switchTo().window(WebDriverRunner.getWebDriver().getWindowHandles().iterator().next());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

