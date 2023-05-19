package org.selenide;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;

public class CredentialsManagerTest {
    @TempDir
    Path tempDir;

    private CredentialsManager manager;

    @BeforeEach
    public void setUp() {
        manager = new CredentialsManager(tempDir.toString());
    }

    @Test
    public void testGetLoginCredentials_ValidUrl_ReturnsCredentials() throws IOException {
        String exampleContent = "{\"exampleCredentials\":{\"username\":\"blaga\",\"password\":\"p1a2s3sword\"}}";
        String filename = "example.json";
        Path filePath = tempDir.resolve(filename);

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write(exampleContent);
        }

        String url = "https://example.com";
        String[] credentials = manager.getLoginCredentials(url);

        Assertions.assertNotNull(credentials);
        Assertions.assertEquals(2, credentials.length);
        Assertions.assertEquals("blaga", credentials[0]);
        Assertions.assertEquals("p1a2s3sword", credentials[1]);

        Files.deleteIfExists(filePath);
    }

    @Test
    public void testGetLoginCredentials_InvalidUrl_ThrowsException() {
        String invalidUrl = "invalid-url";

        Assertions.assertThrows(RuntimeException.class, () -> manager.getLoginCredentials(invalidUrl));
    }
}