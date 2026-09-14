package com.bx.ultimateDonutSmp2.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JoinWarmupPolicyTest {

    @Test
    void bundledConfigKeepsSpawnLoadedAndPreloadsLogoutChunks() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(
                new File("src/main/resources/config.yml"));

        assertTrue(JoinWarmupPolicy.enabled(config));
        assertFalse(JoinWarmupPolicy.keepSpawnInMemory(config));
        assertTrue(JoinWarmupPolicy.warmEntityApi(config));
        assertTrue(JoinWarmupPolicy.preloadLogoutChunks(config));
        assertTrue(JoinWarmupPolicy.pinLoadedChunks(config));
        assertEquals(10, JoinWarmupPolicy.spawnChunkRadius(config));
        assertEquals(1, JoinWarmupPolicy.chunksPerTick(config));
        assertEquals(4, JoinWarmupPolicy.startupLogoutAnchors(config));
        assertEquals(2, JoinWarmupPolicy.logoutChunkRadius(config));
        assertEquals(2, JoinWarmupPolicy.logoutWaitRadius(config));
        assertEquals(1500L, JoinWarmupPolicy.logoutWaitMillis(config));
        assertEquals(200L, JoinWarmupPolicy.releaseTicketsAfterEmptyTicks(config));
    }

    @Test
    void missingConfigUsesTheSameDefaultsAsTheBundledFile() {
        assertTrue(JoinWarmupPolicy.enabled(null));
        assertFalse(JoinWarmupPolicy.keepSpawnInMemory(null));
        assertTrue(JoinWarmupPolicy.pinLoadedChunks(null));
        assertEquals(10, JoinWarmupPolicy.spawnChunkRadius(null));
        assertEquals(1, JoinWarmupPolicy.chunksPerTick(null));
        assertEquals(2, JoinWarmupPolicy.logoutChunkRadius(null));
        assertEquals(2, JoinWarmupPolicy.logoutWaitRadius(null));
        assertEquals(1500L, JoinWarmupPolicy.logoutWaitMillis(null));
        assertEquals(4, JoinWarmupPolicy.startupLogoutAnchors(null));
        assertEquals(200L, JoinWarmupPolicy.releaseTicketsAfterEmptyTicks(null));
    }

    @Test
    void radiiStayInsideTheRangeThatWillNotFreezeTheTickThread() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("JOIN-WARMUP.SPAWN-CHUNK-RADIUS", 99);
        config.set("JOIN-WARMUP.CHUNKS-PER-TICK", 0);
        config.set("JOIN-WARMUP.LOGOUT-CHUNK-RADIUS", 1);
        config.set("JOIN-WARMUP.LOGOUT-WAIT-RADIUS", 8);
        config.set("JOIN-WARMUP.LOGOUT-WAIT-MILLIS", 1L);

        assertEquals(10, JoinWarmupPolicy.spawnChunkRadius(config));
        assertEquals(1, JoinWarmupPolicy.chunksPerTick(config));
        config.set("JOIN-WARMUP.STARTUP-LOGOUT-ANCHORS", 99);
        assertEquals(8, JoinWarmupPolicy.startupLogoutAnchors(config));
        assertEquals(1, JoinWarmupPolicy.logoutChunkRadius(config));
        assertEquals(1, JoinWarmupPolicy.logoutWaitRadius(config));
        assertEquals(250L, JoinWarmupPolicy.logoutWaitMillis(config));
        config.set("JOIN-WARMUP.LOGOUT-CHUNK-RADIUS", 99);
        assertEquals(3, JoinWarmupPolicy.logoutChunkRadius(config));
        config.set("JOIN-WARMUP.LOGOUT-WAIT-RADIUS", 99);
        assertEquals(2, JoinWarmupPolicy.logoutWaitRadius(config));
    }

    @Test
    void viewDistanceRadiusNeverExpandsASmallLoginSquare() {
        assertEquals(10, JoinWarmupPolicy.viewDistanceRadius(10, 10));
        assertEquals(8, JoinWarmupPolicy.viewDistanceRadius(10, 8));
        assertEquals(2, JoinWarmupPolicy.viewDistanceRadius(10, 2));
        assertEquals(8, JoinWarmupPolicy.viewDistanceRadius(8, 10));
        assertEquals(10, JoinWarmupPolicy.viewDistanceRadius(0, 10));
        assertEquals(1, JoinWarmupPolicy.viewDistanceRadius(1, 10));
    }

    @Test
    void liveConfigValuesAreClampedSoJoinCannotLoadAViewDistanceSquare() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("JOIN-WARMUP.CHUNKS-PER-TICK", 8);
        config.set("JOIN-WARMUP.LOGOUT-CHUNK-RADIUS", 10);
        config.set("JOIN-WARMUP.LOGOUT-WAIT-RADIUS", 6);
        config.set("JOIN-WARMUP.LOGOUT-WAIT-MILLIS", 10000L);

        assertEquals(2, JoinWarmupPolicy.chunksPerTick(config));
        assertEquals(3, JoinWarmupPolicy.logoutChunkRadius(config));
        assertEquals(2, JoinWarmupPolicy.logoutWaitRadius(config));
        assertEquals(2500L, JoinWarmupPolicy.logoutWaitMillis(config));
    }

    @Test
    void onlyOverworldLogoutPositionsAreStartupAnchors() {
        assertTrue(JoinWarmupPolicy.isOverworldDimension(null));
        assertTrue(JoinWarmupPolicy.isOverworldDimension("minecraft:overworld"));
        assertFalse(JoinWarmupPolicy.isOverworldDimension("minecraft:the_nether"));
        assertFalse(JoinWarmupPolicy.isOverworldDimension("minecraft:the_end"));
    }

    @Test
    void onlyOrdinaryOverworldMapsKeepSpawnLoaded() {
        assertTrue(JoinWarmupPolicy.shouldKeepSpawnLoaded("world", "NORMAL"));
        assertFalse(JoinWarmupPolicy.shouldKeepSpawnLoaded("world_nether", "NETHER"));
        assertFalse(JoinWarmupPolicy.shouldKeepSpawnLoaded("world_the_end", "THE_END"));
        assertFalse(JoinWarmupPolicy.shouldKeepSpawnLoaded("duels", "NORMAL"));
        assertFalse(JoinWarmupPolicy.shouldKeepSpawnLoaded("ffa", "NORMAL"));
        assertFalse(JoinWarmupPolicy.shouldKeepSpawnLoaded("lobby", "NORMAL"));
        assertFalse(JoinWarmupPolicy.shouldKeepSpawnLoaded("arena_1", "NORMAL"));
    }
}
