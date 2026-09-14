package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.managers.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrdersSoundsTest {

    private static final Map<String, String> EXPECTED = Map.ofEntries(
            Map.entry("ORDERS.CLICK", "minecraft:ui.button.click|1.0|1.0"),
            Map.entry("ORDERS.PAGE-TURN", "minecraft:item.book.page_turn|1.0|1.0"),
            Map.entry("ORDERS.OPEN", "minecraft:block.chest.open|0.8|1.15"),
            Map.entry("ORDERS.SEARCH", "minecraft:entity.villager.work_cartographer|1.0|1.2"),
            Map.entry("ORDERS.SUCCESS", "minecraft:entity.experience_orb.pickup|1.0|1.2"),
            Map.entry("ORDERS.FAIL", "minecraft:entity.villager.no|1.0|1.0"),
            Map.entry("ORDERS.CREATE", "minecraft:entity.player.levelup|0.85|1.35"),
            Map.entry("ORDERS.CANCEL", "minecraft:entity.item.break|1.0|0.85"),
            Map.entry("ORDERS.REFUND", "minecraft:entity.experience_orb.pickup|1.0|1.45"),
            Map.entry("ORDERS.DELIVER", "minecraft:entity.villager.yes|1.0|1.2"),
            Map.entry("ORDERS.DELIVERED", "minecraft:entity.player.levelup|0.9|1.25"),
            Map.entry("ORDERS.COLLECT", "minecraft:entity.item.pickup|1.0|1.15"),
            Map.entry("ORDERS.DROP", "minecraft:entity.item.pickup|1.0|0.8"),
            Map.entry("ORDERS.EXPIRE", "minecraft:entity.item.break|0.9|0.7")
    );

    @Test
    void soundsConfigDefinesEveryOrdersActionSound() {
        var stream = getClass().getClassLoader().getResourceAsStream("sounds.yml");
        assertNotNull(stream, "sounds.yml must exist in resources");
        YamlConfiguration sounds = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );

        EXPECTED.forEach((path, value) -> {
            String sound = sounds.getString(path);
            assertNotNull(sound, path + " must be defined");
            assertFalse(sound.isBlank(), path + " must not be blank");
            assertEquals(value, sound, path);
        });
    }

    @Test
    void configManagerProvidesOrdersSoundFallbacks() {
        ConfigManager manager = new ConfigManager(null);
        EXPECTED.forEach((path, value) -> assertEquals(value, manager.getSound(path), path));
    }
}
