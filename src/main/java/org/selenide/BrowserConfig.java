package org.selenide;

import com.codeborne.selenide.Configuration;

public class BrowserConfig {
    public static void setConfig() {
        Configuration.browser = "chrome";
        Configuration.browserSize = "1080x1080";
        Configuration.headless = false;
    }
}