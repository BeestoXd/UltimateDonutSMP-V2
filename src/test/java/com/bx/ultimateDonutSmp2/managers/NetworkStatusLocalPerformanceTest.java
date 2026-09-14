package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkStatusLocalPerformanceTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/NetworkStatusManager.java");

    @Test
    void localSnapshotPassesThreeTpsSamples() throws Exception {
        String source = Files.readString(SOURCE);
        int createStart = source.indexOf("private ServerStatusSnapshot createLocalSnapshot");
        int formatStart = source.indexOf("private String formatPerformance");
        int normalizeStart = source.indexOf("private double normalizeTps");
        assertTrue(createStart >= 0 && formatStart > createStart && normalizeStart > formatStart,
                "createLocalSnapshot should still sit above formatPerformance");

        String create = source.substring(createStart, formatStart);
        String format = source.substring(formatStart, normalizeStart);
        assertTrue(
                format.contains("length < 3"),
                "formatPerformance still returns N/A unless it receives three TPS samples");
        assertFalse(
                create.contains("new double[]{tps}"),
                "createLocalSnapshot wraps a single OptimizationManager TPS in a 1-element array, "
                        + "then formatPerformance returns N/A unless length >= 3. The local status "
                        + "endpoint and LOCAL SOURCE snapshots therefore always ship performance=N/A."
        );
    }
}
