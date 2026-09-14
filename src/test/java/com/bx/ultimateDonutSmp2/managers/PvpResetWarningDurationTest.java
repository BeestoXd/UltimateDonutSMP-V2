package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PvpResetWarningDurationTest {

    @Test
    void resetWarningTemplateTreatsDAsMinutes() throws Exception {
        String source = java.nio.file.Files.readString(java.nio.file.Path.of(
                "src/main/java/com/bx/ultimateDonutSmp2/managers/PvpManager.java"));
        int tickStart = source.indexOf("private void tickReset(");
        int durationStart = source.indexOf("// ── Duration helpers");
        assertTrue(tickStart >= 0 && durationStart > tickStart, "tickReset should still sit above Duration helpers");
        String tickReset = source.substring(tickStart, durationStart);
        assertTrue(
                tickReset.contains("formatDuration(Math.max(0L, nextResetAt - now), \"{M}m {S}s\")"),
                "tickReset formats the warning with {M}m {S}s");

        assertEquals(
                "1m 30s",
                PvpManager.formatDuration(90_000L, "{M}m {S}s"),
                "formatDuration documents {M} as unpadded minutes and {S} as seconds."
        );
    }
}
