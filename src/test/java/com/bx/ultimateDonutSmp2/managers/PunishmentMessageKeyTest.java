package com.bx.ultimateDonutSmp2.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * languages/ used to ship four MESSAGES.PUNISHMENTS keys that no code read. A banned or blacklisted
 * player is disconnected with PUNISHMENTS.BAN and PUNISHMENTS.BLACKLIST, and a muted player sees
 * PUNISHMENTS.MUTE both when the mute lands and on every blocked chat attempt, so the four were text
 * a server owner could edit all day without changing anything. These keep the shipped files honest
 * about which messages exist.
 */
class PunishmentMessageKeyTest {

    private static final List<String> REMOVED = List.of(
            "BAN-KICK", "BLACKLIST-KICK", "MUTE-RECEIVED", "MUTED-CHAT"
    );

    private static final List<String> BUNDLED_LOCALES = List.of(
            "en_US", "es_ES", "id_ID", "pt_BR", "de_DE", "fr_FR", "ru_RU", "zh_CN"
    );

    private static YamlConfiguration load(String path) {
        return YamlConfiguration.loadConfiguration(new File(path));
    }

    @Test
    void theDeadPunishmentKeysAreNoLongerShipped() {
        for (String locale : BUNDLED_LOCALES) {
            YamlConfiguration language = load("src/main/resources/languages/" + locale + ".yml");
            for (String key : REMOVED) {
                assertFalse(
                        language.contains("MESSAGES.PUNISHMENTS." + key),
                        locale + " still ships the dead key " + key
                );
            }
        }
    }

    @Test
    void theKeysThatActuallyReachPlayersStayPut() {
        YamlConfiguration language = load("src/main/resources/languages/en_US.yml");
        for (String key : List.of("BAN", "BLACKLIST", "MUTE", "KICK", "VOICE-MUTE", "WARN-RECEIVED")) {
            assertTrue(
                    language.contains("MESSAGES.PUNISHMENTS." + key),
                    "PunishmentMessages reads PUNISHMENTS." + key + ", so removing it would blank a real message"
            );
        }
    }
}
