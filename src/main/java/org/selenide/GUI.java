package org.selenide;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
public class GUI {
    private static final Logger LOGGER = Logger.getLogger(GUI.class.getName());

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

    public String[] getLoginCredentials(String url) {
        String username = null;
        String password = null;
        String domain = extractDomain(url);
        String os = System.getProperty("os.name").toLowerCase();
        String pathname = os.contains("win")
                ? "C:" + File.separator + "temp" + File.separator + domain + ".json"
                : os.contains("linux")
                ? File.separator + "tmp" + File.separator + domain+ ".json"
                : os.contains("mac")
                ? "~/Library/Caches/"+domain+".json"
                : null;

        if (pathname == null) {
            throw new RuntimeException("Unsupported OS: " + os);
        }

        Path parentDir = Path.of(pathname).getParent();
        if (!Files.exists(parentDir)) {
            int option = JOptionPane.showConfirmDialog(null,
                    "The directory " + parentDir + " does not exist. Do you want to create it?",
                    "Directory not found", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                try {
                    Files.createDirectories(parentDir);
                    JOptionPane.showMessageDialog(null,
                            "Directory created successfully.");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                            "Failed to create directory: " + parentDir,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    LOGGER.log(Level.WARNING, "Failed to create directory: " + parentDir, e);
                }
            }
        }

        File loginFile = new File(pathname);
        if (loginFile.exists()) {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Do you want to use saved login credentials?",
                    "Login credentials", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                LOGGER.info("Reading login credentials from file");
                JsonNode credentials = readCredentialsFromFile(pathname);
                if (credentials != null) {
                    username = credentials.get("Credentials").get("username").asText();
                    password = credentials.get("Credentials").get("password").asText();
                } else {
                    LOGGER.warning("Failed to read login credentials from file");
                }
            }
        }

        if (username == null || password == null) {
            LOGGER.info("Reading login credentials from Swing input");
            username = JOptionPane.showInputDialog(null, "Username:");
            password = JOptionPane.showInputDialog(null, "Password:");
            int saveChoice = JOptionPane.showConfirmDialog(null, "Do you want to save login credentials?", "Login credentials", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (saveChoice == JOptionPane.YES_OPTION) {
                saveCredentialsToFile(username, password, pathname);
            }
        }


        return new String[]{username, password};
    }



    private JsonNode readCredentialsFromFile(String pathname) {
        try {
            LOGGER.info("Reading login credentials from file");
            File jsonFile = new
                    File(pathname);
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readTree(jsonFile);
        } catch (IOException e) {
            LOGGER.warning("Failed to read login credentials from file");
            e.printStackTrace();
            return null;
        }
    }

    private void saveCredentialsToFile(String username, String password, String pathname) {
        try {
            LOGGER.info("Saving login credentials to file");
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode rootNode = objectMapper.createObjectNode();
            ObjectNode credentialsNode = objectMapper.createObjectNode();
            credentialsNode.put("username", username);
            credentialsNode.put("password", password);
            rootNode.set("Credentials", credentialsNode);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(pathname), rootNode);
        } catch (IOException e) {
            LOGGER.warning("Failed to save login credentials to file");
            e.printStackTrace();
        }
    }
    public String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            if (domain != null) {
                return domain.startsWith("www.") ? domain.substring(4) : domain;
            }
        } catch (URISyntaxException e) {
            // hantera eventuella fel här
        }
        return null;
    }
}
