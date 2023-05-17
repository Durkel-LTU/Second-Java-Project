package org.selenide;

//import ch.qos.logback.classic.Logger;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;

//import org.slf4j.LoggerFactory;

public class Webfunctions {

    public static void AcceptCookies() {

        //logger.info("Finding and accept all cookies on website");
        try {
            SelenideElement cookiesButton = $(byXpath("//button[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']"));
            if (cookiesButton.exists()) {
                cookiesButton.click();
                //logger.info("Successfully accepted cookies on website.");
            }
        } catch (Exception e) {
            //logger.error("Failed to accept cookies.");
        }
    }

    public static void OpenStudentPage() {
        try {
            SelenideElement studentButton = $(byXpath("/html/body/header/div[2]/div[1]/div[1]/div[3]/div/a[1]"));
            if (studentButton.exists()) {
                studentButton.click();
                //logger.info("Opened 'Student' page.");
            }
        } catch (Exception e) {
            //logger.error("Failed to open student page.");
        }
    }
}

