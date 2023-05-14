import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.selenide.GUI;

class GUITest {

    @Test
    public void testExtractDomain() {
        GUI gui = new GUI();

        String url1 = "https://www.ltu.se";
        String expectedDomain1 = "ltu.se";
        String actualDomain1 = gui.extractDomain(url1);
        Assertions.assertEquals(expectedDomain1, actualDomain1);

        String url2 = "http://gp.se";
        String expectedDomain2 = "gp.se";
        String actualDomain2 = gui.extractDomain(url2);
        Assertions.assertEquals(expectedDomain2, actualDomain2);

        String url3 = "http://www.play.svt.se";
        String expectedDomain3 = "play.svt.se";
        String actualDomain3 = gui.extractDomain(url3);
        Assertions.assertEquals(expectedDomain3, actualDomain3);

        String url4 = "invalid url";
        String expectedDomain4 = null;
        String actualDomain4 = gui.extractDomain(url4);
        Assertions.assertEquals(expectedDomain4, actualDomain4);
    }
    @Test
    void testGetLoginCredentials() {
        GUI gui = new GUI();
        String[] credentials = gui.getLoginCredentials("https://slack.com");

        Assertions.assertNotNull(credentials);
        Assertions.assertEquals(2, credentials.length);
        Assertions.assertNotNull(credentials[0]);
        Assertions.assertNotNull(credentials[1]);
    }
}
