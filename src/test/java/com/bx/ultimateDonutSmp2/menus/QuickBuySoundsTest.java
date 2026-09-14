package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.managers.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickBuySoundsTest {

    @Test
    void soundsConfigDefinesEveryQuickBuyActionSound() {
        var stream = getClass().getClassLoader().getResourceAsStream("sounds.yml");
        assertNotNull(stream, "sounds.yml must exist in resources");
        YamlConfiguration sounds = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );

        QuickBuySounds.DEFAULTS.forEach((path, value) -> {
            String sound = sounds.getString(path);
            assertNotNull(sound, path + " must be defined");
            assertFalse(sound.isBlank(), path + " must not be blank");
            assertEquals(value, sound, path);
        });
    }

    @Test
    void configManagerProvidesQuickBuySoundFallbacks() {
        ConfigManager manager = new ConfigManager(null);
        QuickBuySounds.DEFAULTS.forEach((path, value) ->
                assertEquals(value, manager.getSound(path), path));
    }

    @Test
    void everyQuickBuySoundIsWiredInQuickBuyCode() throws IOException {
        Path srcRoot = Path.of("src", "main", "java");
        assertTrue(Files.isDirectory(srcRoot), "main java sources must exist");

        String sources;
        try (Stream<Path> walk = Files.walk(srcRoot)) {
            sources = walk
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> {
                        String name = path.getFileName().toString();
                        return name.equals("ShopCommand.java")
                                || name.equals("QuickBuyItemDialog.java")
                                || name.equals("AuctionHouseBrowseMenu.java")
                                || name.equals("AuctionYourItemsMenu.java")
                                || (name.startsWith("QuickBuy") && !name.equals("QuickBuySounds.java")
                                && !name.equals("QuickBuyEntry.java"));
                    })
                    .map(path -> {
                        try {
                            return Files.readString(path, StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.joining("\n"));
        }

        QuickBuySounds.DEFAULTS.keySet().forEach(path -> {
            String constant = path.substring("QUICK_BUY.".length()).replace('-', '_');
            boolean referenced = sources.contains("QuickBuySounds." + constant)
                    || sources.contains("\"" + path + "\"")
                    || (path.equals(QuickBuySounds.BUY_SUCCESS) && sources.contains("playPurchase"))
                    || (path.equals(QuickBuySounds.FAIL) && sources.contains("QuickBuySounds.fail"))
                    || (path.equals(QuickBuySounds.CLICK) && sources.contains("QuickBuySounds.click"))
                    || (path.equals(QuickBuySounds.PAGE_TURN) && sources.contains("QuickBuySounds.pageTurn"));
            assertTrue(referenced, path + " must be played from Quick Buy code");
        });
    }
}
