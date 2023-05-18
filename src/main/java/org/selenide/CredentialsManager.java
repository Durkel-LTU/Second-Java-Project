package org.selenide;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.logging.Logger;

public class CredentialsManager {
    private static final Logger LOGGER = Logger.getLogger(CredentialsManager.class.getName());
    private final String loginFilePath;

    public CredentialsManager(String loginFilePath) {
        this.loginFilePath = loginFilePath;
    }

    public String[] getLoginCredentials(String url) {
        String domain = extractDomain(url);

        if (domain == null) {
            throw new RuntimeException("Invalid URL: " + url);
        }

        String[] credentials = readLoginCredentialsFromFile(domain);

        if (credentials == null) {
            credentials = readLoginCredentialsFromInput(domain);
        }

        return credentials;
    }

    private String[] readLoginCredentialsFromFile(String domain) {
        if (loginFileExists(domain)) {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Do you want to use saved login credentials?",
                    "Login credentials", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                LOGGER.info("Reading login credentials from file");
                JsonNode credentials = readCredentialsFromFile(domain);
                if (credentials != null) {
                    JsonNode domainCredentials = credentials.get(domain + "Credentials");
                    if (domainCredentials != null) {
                        String username = domainCredentials.get("username") != null ? domainCredentials.get("username").asText() : null;
                        String password = domainCredentials.get("password") != null ? domainCredentials.get("password").asText() : null;
                        return new String[]{username, password};
                    }
                } else {
                    LOGGER.warning("Failed to read login credentials from file");
                }
            }
        }
        return null;
    }

    private String[] readLoginCredentialsFromInput(String domain) {
        LOGGER.info("Reading login credentials from Swing input");
        String username = JOptionPane.showInputDialog(null, "Username:");
        String password = JOptionPane.showInputDialog(null, "Password:");
        int saveChoice = JOptionPane.showConfirmDialog(null, "Do you want to save login credentials?", "Login credentials", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (saveChoice == JOptionPane.YES_OPTION) {
            saveCredentialsToFile(username, password, domain);
        }
        return new String[]{username, password};
    }

    private boolean loginFileExists(String domain) {
        File loginFile = new File(loginFilePath + domain + ".json");
        return loginFile.exists();
    }

    private JsonNode readCredentialsFromFile(String domain) {
        try {
            LOGGER.info("Reading login credentials from file");
            File jsonFile = new File(loginFilePath + domain + ".json");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(jsonFile);
        } catch (IOException e) {
            LOGGER.warning("Failed to read login credentials from file");
            e.printStackTrace();
            return null;
        }
    }

    public void saveCredentialsToFile(String username, String password, String domain) {
        try {
            LOGGER.info("Saving login credentials to file");
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode rootNode = objectMapper.createObjectNode();
            ObjectNode credentialsNode = objectMapper.createObjectNode();
            credentialsNode.put("username", username);
            credentialsNode.put("password", password);
            rootNode.set(domain + "Credentials", credentialsNode);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(loginFilePath + domain + ".json"), rootNode);
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
                String[] parts = domain.split("\\.");
                int lastIndex = parts.length - 1;
                if (lastIndex >= 0) {
                    return parts[lastIndex - 1];
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}