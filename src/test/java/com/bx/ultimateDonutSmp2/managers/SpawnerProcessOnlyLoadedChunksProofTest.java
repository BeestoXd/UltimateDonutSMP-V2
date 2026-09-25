package com.bx.ultimateDonutSmp2.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnerProcessOnlyLoadedChunksProofTest {

    private static final Path SPAWNERS_YAML = Path.of("src/main/resources/spawners.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-spawners.yml.md");
    private static final Path MANAGER = Path.of("src/main/java/com/bx/ultimateDonutSmp2/managers/SpawnerManager.java");

    @Test
    void bundledYamlShipsProcessOnlyLoadedChunks() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.load(SPAWNERS_YAML.toFile());
        assertTrue(config.contains("SETTINGS.PROCESS_ONLY_LOADED_CHUNKS"), "spawners.yml must contain SETTINGS.PROCESS_ONLY_LOADED_CHUNKS");
        assertEquals(true, config.getBoolean("SETTINGS.PROCESS_ONLY_LOADED_CHUNKS"));
    }

    @Test
    void wikiTablesProcessOnlyLoadedChunks() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(wiki.contains("`SETTINGS.PROCESS_ONLY_LOADED_CHUNKS`"), "Config-spawners.yml.md must list SETTINGS.PROCESS_ONLY_LOADED_CHUNKS");
    }

    @Test
    void processOnlyLoadedChunksMustBeConsulted() throws Exception {
        String managerSource = Files.readString(MANAGER, StandardCharsets.UTF_8);
        assertTrue(managerSource.contains("isProcessOnlyLoadedChunks"),
                "SpawnerManager must have isProcessOnlyLoadedChunks getter");
        assertTrue(managerSource.contains("processOnlyLoadedChunks && !world.isChunkLoaded"),
                "Config-spawners.yml.md says PROCESS_ONLY_LOADED_CHUNKS gates generation; field is assigned then never read");
    }
}
