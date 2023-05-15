package org.selenide;


import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GUI {
    private static final Logger LOGGER = Logger.getLogger(GUI.class.getName());
    private static final String LOGIN_FILE_PATH = determineLoginFilePath();

    private LoginManager loginManager;

    static {
        try {
            FileHandler fileHandler = new FileHandler("GUI.log", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            throw new RuntimeException("Error setting up log file", e);
        }
    }

    public GUI() {
        this.loginManager = new LoginManager(LOGIN_FILE_PATH);
    }

    public String[] getLoginCredentials(String url) {
        String[] credentials = loginManager.getLoginCredentials(url);

        if (credentials == null) {
            // Prompt for credentials using GUI components if needed

            String username = JOptionPane.showInputDialog(null, "Username:");
            String password = JOptionPane.showInputDialog(null, "Password:");
            credentials = new String[]{username, password};

            int saveChoice = JOptionPane.showConfirmDialog(null, "Do you want to save login credentials?", "Login credentials", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (saveChoice == JOptionPane.YES_OPTION) {

                loginManager.saveCredentialsToFile(username, password, loginManager.extractDomain(url));
            }
        }

        return credentials;
    }


    private static String determineLoginFilePath() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return "C:" + File.separator + "temp" + File.separator ;
        } else if (os.contains("linux")) {
            return File.separator + "tmp" + File.separator ;
        } else if (os.contains("mac")) {
            return "~/Library/Caches/";
        } else {
            throw new RuntimeException("Unsupported OS: " + os);
        }
    }
}
