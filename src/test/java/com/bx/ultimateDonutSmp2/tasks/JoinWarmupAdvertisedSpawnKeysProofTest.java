package com.bx.ultimateDonutSmp2.tasks;

import com.bx.ultimateDonutSmp2.utils.JoinWarmupPolicy;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JoinWarmupAdvertisedSpawnKeysProofTest {

    private static final Path CONFIG_YAML = Path.of("src/main/resources/config.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-config.yml.md");
    private static final Path WARMUP_SOURCE = Path.of("src/main/java/com/bx/ultimateDonutSmp2/tasks/StartupJoinWarmup.java");

    @Test
    void bundledYamlShipsTheAdvertisedSpawnKeys() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.load(CONFIG_YAML.toFile());
        assertFalse(config.getBoolean("JOIN-WARMUP.KEEP-SPAWN-IN-MEMORY"));
        assertEquals(10, config.getInt("JOIN-WARMUP.SPAWN-CHUNK-RADIUS"));
    }

    @Test
    void wikiTablesTheKeysAsLiveOptions() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(wiki.contains("`JOIN-WARMUP.KEEP-SPAWN-IN-MEMORY`"),
                "Config-config.yml.md tables JOIN-WARMUP KEEP-SPAWN-IN-MEMORY as a live option");
        assertTrue(wiki.contains("`JOIN-WARMUP.SPAWN-CHUNK-RADIUS`"),
                "Config-config.yml.md tables JOIN-WARMUP SPAWN-CHUNK-RADIUS as a live option");
    }

    @Test
    void keepSpawnInMemoryAndSpawnChunkRadiusMustHaveCallers() throws Exception {
        String source = Files.readString(WARMUP_SOURCE, StandardCharsets.UTF_8);
        assertTrue(source.contains("keepSpawnInMemory"),
                "StartupJoinWarmup must call JoinWarmupPolicy.keepSpawnInMemory");
        assertTrue(source.contains("spawnChunkRadius"),
                "StartupJoinWarmup must call JoinWarmupPolicy.spawnChunkRadius");
    }

    @Test
    void policyReturnsConfiguredValues() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("JOIN-WARMUP.KEEP-SPAWN-IN-MEMORY", true);
        config.set("JOIN-WARMUP.SPAWN-CHUNK-RADIUS", 6);

        assertTrue(JoinWarmupPolicy.keepSpawnInMemory(config));
        assertEquals(6, JoinWarmupPolicy.spawnChunkRadius(config));
    }
}
