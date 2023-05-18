package org.selenide;


import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GUI {
    private static final Logger LOGGER = Logger.getLogger(GUI.class.getName());
    private static final String LOGIN_FILE_PATH = determineLoginFilePath();
    private final CredentialsManager credentialsManager;

    static {
        try {
            setupLogFile();
        } catch (IOException e) {
            throw new RuntimeException("Error setting up log file", e);
        }
    }

    public GUI() {
        this.credentialsManager = new CredentialsManager(LOGIN_FILE_PATH);
    }

    public String[] getLoginCredentials(String url) {
        String[] credentials = credentialsManager.getLoginCredentials(url);

        if (credentials == null) {
            credentials = promptForCredentials();
            saveCredentialsIfNeeded(credentials, url);
        }

        return credentials;
    }

    private String[] promptForCredentials() {
        String username = JOptionPane.showInputDialog(null, "Username:");
        String password = JOptionPane.showInputDialog(null, "Password:");
        return new String[]{username, password};
    }

    private void saveCredentialsIfNeeded(String[] credentials, String url) {
        int saveChoice = JOptionPane.showConfirmDialog(null, "Do you want to save login credentials?", "Login credentials", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (saveChoice == JOptionPane.YES_OPTION) {
            credentialsManager.saveCredentialsToFile(credentials[0], credentials[1], credentialsManager.extractDomain(url));
        }
    }

    private static void setupLogFile() throws IOException {
        FileHandler fileHandler = new FileHandler("GUI.log", true);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        LOGGER.addHandler(fileHandler);
    }

    private static String determineLoginFilePath() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return "C:" + File.separator + "temp" + File.separator;
        } else if (os.contains("linux")) {
            return File.separator + "tmp" + File.separator;
        } else if (os.contains("mac")) {
            return "/Library/Caches/";
        } else {
            throw new RuntimeException("Unsupported OS: " + os);
        }
    }

    public static void displayMove(String destinationFilePath, String fileName) {
        JOptionPane.showMessageDialog(null, "Image file moved to: " + destinationFilePath + "\nScreenshot saved as " + fileName);
    }

}
