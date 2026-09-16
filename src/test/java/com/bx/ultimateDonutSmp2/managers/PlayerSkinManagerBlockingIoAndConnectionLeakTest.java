package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerSkinManagerBlockingIoAndConnectionLeakTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/PlayerSkinManager.java");

    @Test
    void scheduleSaveDoesNotRunBlockingDiskIoOnGlobalTickThread() throws Exception {
        String source = Files.readString(SOURCE);
        int scheduleSaveIndex = source.indexOf("private synchronized void scheduleSave()");
        assertTrue(scheduleSaveIndex >= 0, "scheduleSave should exist in PlayerSkinManager");
        int methodEnd = source.indexOf("DEBOUNCE_SAVE_TICKS);", scheduleSaveIndex);
        String scheduleSaveBody = source.substring(scheduleSaveIndex, methodEnd + 30);

        assertFalse(
                scheduleSaveBody.contains("runGlobalLater"),
                "scheduleSave schedules saveCache() which performs blocking disk I/O (FileWriter and Files.move). "
                        + "Running saveCache() on runGlobalLater blocks the global server tick thread; "
                        + "it must use runAsyncLater instead."
        );
    }

    @Test
    void mojangHttpConnectionsAreClosedInFinallyBlock() throws Exception {
        String source = Files.readString(SOURCE);
        int fetchMethodIndex = source.indexOf("private SkinTexture fetchMojangSessionTexture(");
        assertTrue(fetchMethodIndex >= 0, "fetchMojangSessionTexture should exist");
        int fetchMethodEnd = source.indexOf("return null;\n    }", fetchMethodIndex);
        String fetchBody = source.substring(fetchMethodIndex, fetchMethodEnd);

        assertTrue(
                fetchBody.contains(".disconnect()"),
                "fetchMojangSessionTexture opens HttpURLConnection without calling disconnect() in a finally block"
        );
    }
}
