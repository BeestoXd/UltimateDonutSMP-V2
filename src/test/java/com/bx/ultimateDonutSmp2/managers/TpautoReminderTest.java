package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.models.PlayerData;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TpautoReminderTest {

    @Test
    void deathTurnsTpautoOffAndASecondDeathDoesNothing() {
        PlayerData data = new PlayerData(UUID.randomUUID(), "Steve");
        data.setTpauto(true);

        assertTrue(TPAManager.disableTpauto(data));
        assertFalse(data.isTpauto());
        assertFalse(TPAManager.disableTpauto(data));
        assertFalse(TPAManager.disableTpauto(null));
    }

    @Test
    void reminderWaitsWhileAnotherActionBarIsUsingTheSlot() {
        assertTrue(TPAManager.showTpautoReminder(true, false));
        assertFalse(TPAManager.showTpautoReminder(true, true));
        assertFalse(TPAManager.showTpautoReminder(false, false));
    }

    @Test
    void everyLanguageFileHasTheActionBarLine() throws Exception {
        for (String language : List.of("en_US", "de_DE", "es_ES", "fr_FR", "id_ID", "pt_BR", "ru_RU", "zh_CN")) {
            String text = Files.readString(Path.of("src/main/resources/languages/" + language + ".yml"));
            assertTrue(text.contains("You have tpauto on."), language);
        }
    }
}
