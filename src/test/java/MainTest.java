import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class MainTest {

    @BeforeAll
    public static void setUp() {
        // Konfigurera Selenide här om det behövs för testerna
    }

    @Test
    public void testLoginAndTranscriptDownload() {
        String targetURL = "https://ltu.se";

        // Öppna webbsidan
        open(targetURL);

        // Kontrollera att webbsidan öppnas korrekt
        $("title").shouldHave(text("Luleå tekniska universitet, LTU"));

        // Acceptera kakor
        if ($("#CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll").exists()) {
            $("#CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll").click();
        }

        // Klicka på "Student" -knappen
        if ($("div.header-navigation-wrapper div a:contains(Student)").exists()) {
            $("div.header-navigation-wrapper div a:contains(Student)").click();
        }

        // Klicka på "Logga in" -knappen
        if ($("a:contains(Logga in)").exists()) {
            $("a:contains(Logga in)").click();
        }

        // Logga in med användarnamn och lösenord
        $(By.id("username")).setValue("username");
        $(By.id("password")).setValue("password");
        $(By.name("submit")).click();

        // Utför tester för att kontrollera att inloggningen lyckades och att intyg kan laddas ned
        // ...

        // Avsluta och rensa eventuell användarinformation
    }

    @Test
    public void testKronoxSearch() {
        // Skriv tester för att söka efter tentamenstider i Kronox och skapa en skärmdump
        // ...
    }
}
