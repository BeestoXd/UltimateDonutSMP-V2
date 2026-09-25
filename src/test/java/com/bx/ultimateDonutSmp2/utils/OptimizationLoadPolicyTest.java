package com.bx.ultimateDonutSmp2.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptimizationLoadPolicyTest {

    @Test
    void joinSpikeWithLowTpsIsCritical() {
        assertEquals(
                OptimizationLoadPolicy.State.CRITICAL,
                OptimizationLoadPolicy.classify(15.29D, 88.53D, 16.0D, 18.5D, 45.0D)
        );
    }

    @Test
    void isolatedMsptSpikeWhileTpsIsHealthyIsOnlyAWarning() {
        assertEquals(
                OptimizationLoadPolicy.State.WARN,
                OptimizationLoadPolicy.classify(19.32D, 73.77D, 16.0D, 18.5D, 45.0D)
        );
    }

    @Test
    void healthySampleStaysNormal() {
        assertEquals(
                OptimizationLoadPolicy.State.NORMAL,
                OptimizationLoadPolicy.classify(20.0D, 22.60D, 16.0D, 18.5D, 45.0D)
        );
    }

    @Test
    void firstMinuteAfterDoneIsStartupGrace() {
        assertTrue(OptimizationLoadPolicy.inStartupGrace(10_000L, 60_000L));
        assertFalse(OptimizationLoadPolicy.inStartupGrace(60_000L, 60_000L));
        assertFalse(OptimizationLoadPolicy.inStartupGrace(5_000L, 0L));
    }

    @Test
    void criticalMsptThresholdIsNotInConfigOrDocumentation() throws Exception {
        org.bukkit.configuration.file.YamlConfiguration config =
                org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(new java.io.File("src/main/resources/config.yml"));
        assertFalse(config.contains("OPTIMIZATION.MSPT-CRITICAL-THRESHOLD"),
                "config.yml must not contain OPTIMIZATION.MSPT-CRITICAL-THRESHOLD");

        String wiki = java.nio.file.Files.readString(java.nio.file.Path.of("docs/wiki/Config-config.yml.md"), java.nio.charset.StandardCharsets.UTF_8);
        assertFalse(wiki.contains("OPTIMIZATION.MSPT-CRITICAL-THRESHOLD"),
                "Config-config.yml.md must not document OPTIMIZATION.MSPT-CRITICAL-THRESHOLD");
    }
}
