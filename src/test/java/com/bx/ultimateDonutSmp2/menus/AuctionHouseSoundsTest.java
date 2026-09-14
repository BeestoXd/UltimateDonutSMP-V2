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

class AuctionHouseSoundsTest {

    @Test
    void soundsConfigDefinesEveryAuctionHouseActionSound() {
        var stream = getClass().getClassLoader().getResourceAsStream("sounds.yml");
        assertNotNull(stream, "sounds.yml must exist in resources");
        YamlConfiguration sounds = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );

        AuctionHouseSounds.DEFAULTS.forEach((path, value) -> {
            String sound = sounds.getString(path);
            assertNotNull(sound, path + " must be defined");
            assertFalse(sound.isBlank(), path + " must not be blank");
            assertEquals(value, sound, path);
        });
    }

    @Test
    void configManagerProvidesAuctionHouseSoundFallbacks() {
        ConfigManager manager = new ConfigManager(null);
        AuctionHouseSounds.DEFAULTS.forEach((path, value) ->
                assertEquals(value, manager.getSound(path), path));
    }

    @Test
    void everyAuctionHouseSoundIsWiredInAuctionHouseCode() throws IOException {
        Path srcRoot = Path.of("src", "main", "java");
        assertTrue(Files.isDirectory(srcRoot), "main java sources must exist");

        String sources;
        try (Stream<Path> walk = Files.walk(srcRoot)) {
            sources = walk
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> {
                        String name = path.getFileName().toString();
                        return name.equals("AuctionHouseCommand.java")
                                || name.equals("AuctionHouseBrowseMenu.java")
                                || name.equals("AuctionYourItemsMenu.java")
                                || name.equals("PlayerAuctionGui.java")
                                || name.equals("ConfirmPurchaseGui.java")
                                || name.equals("ShulkerPreviewGui.java")
                                || name.equals("AuctionHouseManager.java")
                                || name.equals("QuickBuyItemDialog.java")
                                || name.equals("SellGui.java")
                                || name.equals("FilterGui.java");
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

        AuctionHouseSounds.DEFAULTS.keySet().forEach(path -> {
            String constant = path.substring("AUCTION_HOUSE.".length()).replace('-', '_');
            boolean referenced = sources.contains("AuctionHouseSounds." + constant)
                    || sources.contains("\"" + path + "\"")
                    || (path.equals(AuctionHouseSounds.FAIL) && sources.contains("AuctionHouseSounds.fail"))
                    || (path.equals(AuctionHouseSounds.CLICK) && sources.contains("AuctionHouseSounds.click"))
                    || (path.equals(AuctionHouseSounds.PAGE_TURN) && sources.contains("AuctionHouseSounds.pageTurn"))
                    || (path.equals(AuctionHouseSounds.SUCCESS) && sources.contains("AuctionHouseSounds.success"));
            assertTrue(referenced, path + " must be played from Auction House code");
        });
    }
}
