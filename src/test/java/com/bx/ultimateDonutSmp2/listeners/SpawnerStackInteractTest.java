package com.bx.ultimateDonutSmp2.listeners;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnerStackInteractTest {

    private static final Path LISTENER_SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/listeners/SpawnerInteractListener.java"
    );
    private static final Path MANAGER_SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/SpawnerManager.java"
    );
    private static final File SOUNDS_FILE = new File("src/main/resources/sounds.yml");

    @Test
    void soundsConfigContainsSpawnerStackSound() {
        assertTrue(SOUNDS_FILE.exists(), "sounds.yml must exist");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(SOUNDS_FILE);
        String stackSound = config.getString("SPAWNERS.STACK");
        assertNotNull(stackSound, "SPAWNERS.STACK sound must be configured");
        assertTrue(stackSound.contains("spawner"),
                "SPAWNERS.STACK sound should be a spawner place sound");
    }

    @Test
    void interactListenerPerformsArmSwingAndStackSound() throws Exception {
        String source = Files.readString(LISTENER_SOURCE);
        assertTrue(source.contains("player.swingMainHand()"),
                "SpawnerInteractListener must trigger player.swingMainHand() on successful stack");
        assertTrue(source.contains("playStackSound(player, block)"),
                "SpawnerInteractListener must trigger playStackSound on successful stack");
    }

    @Test
    void spawnerManagerExposesPlayStackSound() throws Exception {
        String source = Files.readString(MANAGER_SOURCE);
        assertTrue(source.contains("public void playStackSound(Location location)"),
                "SpawnerManager must have playStackSound(Location)");
        assertTrue(source.contains("public void playStackSound(Player player, Block block)"),
                "SpawnerManager must have playStackSound(Player, Block)");
        assertTrue(source.contains("public void consumeHeldSpawnerItem(Player player, EquipmentSlot handSlot, int amount)"),
                "SpawnerManager must have consumeHeldSpawnerItem with EquipmentSlot");
    }
}
