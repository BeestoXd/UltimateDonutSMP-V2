package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.managers.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MenuPageTurnSoundTest {

    @Test
    void soundsConfigDefinesBookPageTurnSound() {
        var stream = getClass().getClassLoader().getResourceAsStream("sounds.yml");
        assertNotNull(stream, "sounds.yml must exist in resources");
        YamlConfiguration sounds = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );

        String sound = sounds.getString("MENUS.PAGE-TURN");
        assertEquals("minecraft:item.book.page_turn|1.0|1.0", sound,
                "MENUS.PAGE-TURN should be item.book.page_turn at natural 1.0 volume and 1.0 pitch");
    }

    @Test
    void configManagerProvidesPageTurnFallback() {
        ConfigManager manager = new ConfigManager(null);
        // Even without sounds file loaded, fallback handles MENUS.PAGE-TURN and PAGE-TURN
        assertEquals("minecraft:item.book.page_turn|1.0|1.0", manager.getSound("MENUS.PAGE-TURN"));
        assertEquals("minecraft:item.book.page_turn|1.0|1.0", manager.getSound("PAGE-TURN"));
        assertEquals("minecraft:item.book.page_turn|1.0|1.0", manager.getSound("ORDERS.PAGE-TURN"));
    }
}
