package com.bx.ultimateDonutSmp2.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeleportHomeRtpCooldownProofTest {

    @Test
    void homeAndRtpMustHonourConfiguredWarmup() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("TELEPORT-COOLDOWN.HOME", 10);
        config.set("TELEPORT-COOLDOWN.RTP", 7);

        assertEquals(10, TeleportManager.warmupSeconds(config, "HOME"),
                "Config-config.yml.md tables TELEPORT-COOLDOWN.HOME as the home stand-still warmup");
        assertEquals(7, TeleportManager.warmupSeconds(config, "RTP"),
                "Config-config.yml.md tables TELEPORT-COOLDOWN.RTP as the rtp stand-still warmup");
    }
}
