package com.bx.ultimateDonutSmp2.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeleportManagerWarmupTest {

    @Test
    void tpaAndTpahereWaitFiveSecondsInTheShippedConfig() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("src/main/resources/config.yml"));

        assertEquals(5, TeleportManager.warmupSeconds(config, "TPA"));
        assertEquals(5, TeleportManager.warmupSeconds(config, "tpahere"));
        assertEquals(0, TeleportManager.warmupSeconds(config, "RTP"));
        assertEquals(0, TeleportManager.warmupSeconds(config, "HOME"));
        assertEquals(0, TeleportManager.warmupSeconds(config, "home"));
    }

    @Test
    void tpahereFallsBackToTheTpaWarmupWhenItsOwnKeyIsMissing() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("TELEPORT-COOLDOWN.TPA", 5);

        assertEquals(5, TeleportManager.warmupSeconds(config, "TPAHERE"));
    }

    @Test
    void rtpStaysInstantEvenWhenAWarmupIsConfigured() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("TELEPORT-COOLDOWN.RTP", 5);

        assertEquals(0, TeleportManager.warmupSeconds(config, "RTP"));
    }

    @Test
    void homeStaysInstantEvenWhenAWarmupIsConfigured() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("TELEPORT-COOLDOWN.HOME", 5);

        assertEquals(0, TeleportManager.warmupSeconds(config, "HOME"));
        assertEquals(0, TeleportManager.warmupSeconds(config, "home"));
        assertEquals(0, TeleportManager.warmupSeconds(null, "HOME"));
    }

    @Test
    void walkingCancelsTheWarmupLookingDoesNot() {
        assertFalse(TeleportManager.movedEnoughToCancel(0, 0, 0.4, 0.4));
        assertTrue(TeleportManager.movedEnoughToCancel(0, 0, 0.6, 0));
        assertTrue(TeleportManager.movedEnoughToCancel(0, 0, 0, -0.6));
    }
}
