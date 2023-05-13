import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import org.openqa.selenium.WebDriver;
import java.time.Duration;

public class TestCase {

    @BeforeEach
    public void setUp() {
        String os = System.getProperty("os.name").toLowerCase();
        // Defaults to the windows chrome driver if OS not found.
        String chromeDriverPath = "windows/chromedriver.exe";
        if(os.contains("win")) {
            chromeDriverPath = "windows/chromedriver.exe";
        } else if (os.contains("linux")) {
            chromeDriverPath = "chromedriver_linux64/chromedriver";
        } else if (os.contains("mac")) {
            chromeDriverPath = "mac/chromedriver";
        } else {
            System.out.println("Unsupported OS: " + os);
        }


        //LOGGER.info("Starting Chrome driver");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
    }

    @Test
    public void userCanSearchKeywordWithGoogle() {
        open("https://www.google.com");

        // Acceptera cookies
        $(byId("L2AGLb")).click();

        // Kontrollera att sökningen har gått genom
        $("[name='q']").setValue("selenide").pressEnter();
        $("html").shouldHave(text("selenide.org"));
    }
}