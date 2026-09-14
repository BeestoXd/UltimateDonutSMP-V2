package com.bx.ultimateDonutSmp2.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SellSoundsTest {

    @Test
    void soundsConfigDefinesSellSuccessSound() {
        var stream = getClass().getClassLoader().getResourceAsStream("sounds.yml");
        assertNotNull(stream, "sounds.yml must exist in resources");
        YamlConfiguration sounds = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );

        String sound = sounds.getString("SELL.SUCCESS");
        assertEquals("minecraft:entity.experience_orb.pickup|1.0|1.0", sound,
                "SELL.SUCCESS should be experience_orb pickup at natural 1.0 volume and 1.0 pitch");
    }

    @Test
    void configManagerProvidesSellSoundFallbacks() {
        ConfigManager manager = new ConfigManager(null);
        assertEquals("minecraft:entity.experience_orb.pickup|1.0|1.0", manager.getSound("SELL.SUCCESS"));
        assertEquals("minecraft:entity.experience_orb.pickup|1.0|1.0", manager.getSound("SHOP.SELL-SUCCESS"));
        assertEquals("minecraft:entity.player.levelup|1.0|2.0", manager.getSound("SELL.LEVEL-UP"));
    }
}
