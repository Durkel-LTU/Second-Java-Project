import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.selenide.CredentialsManager;

import java.io.File;



class LoginManagerTest {
    private static final String TEST_LOGIN_FILE_PATH = "test-login.json";
    private CredentialsManager credentialsManager;

    @BeforeEach
    void setUp() {
        // Förbereda testmiljön
        File testLoginFile = new File(TEST_LOGIN_FILE_PATH);
        if (testLoginFile.exists()) {
            testLoginFile.delete();
        }

        // Skapa LoginManager-instans för varje testfall
        credentialsManager = new CredentialsManager(TEST_LOGIN_FILE_PATH);
    }

    @Test
    void testGetLoginCredentials_NoSavedCredentials() {
        // Anropa getLoginCredentials med en URL som inte har sparade inloggningsuppgifter
        String[] credentials = credentialsManager.getLoginCredentials("http://example.com");

        // Kontrollera att inloggningsuppgifterna är null
        Assertions.assertNull(credentials);
    }

    @Test
    void testGetLoginCredentials_SavedCredentialsExist() {
        // Förbereda testmiljön genom att skapa en fil med sparade inloggningsuppgifter
        String testUsername = "testuser";
        String testPassword = "testpassword";
        String testDomain = "svt";
        credentialsManager.saveCredentialsToFile(testUsername, testPassword,testDomain);

        // Anropa getLoginCredentials med en URL som har sparade inloggningsuppgifter
        String[] credentials = credentialsManager.getLoginCredentials("http://example.com");

        // Kontrollera att inloggningsuppgifterna är korrekta
        Assertions.assertNotNull(credentials);
        Assertions.assertEquals(testUsername, credentials[0]);
        Assertions.assertEquals(testPassword, credentials[1]);
    }

    @Test
    void testGetLoginCredentials_InvalidURL() {
        // Anropa getLoginCredentials med en ogiltig URL
        Assertions.assertThrows(RuntimeException.class, () -> {
            credentialsManager.getLoginCredentials("invalidurl");
        });
    }
}
