package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateManagerConnectionLeakProofTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/UpdateManager.java");

    @Test
    void checkForUpdatesDisconnectsHttpConnectionInFinally() throws Exception {
        String source = Files.readString(SOURCE).replace("\r\n", "\n");
        int methodIndex = source.indexOf("public void checkForUpdates()");
        assertTrue(methodIndex >= 0, "checkForUpdates should exist");
        int methodEnd = source.indexOf("private String cleanVersion", methodIndex);
        String methodBody = source.substring(methodIndex, methodEnd);

        assertTrue(
                methodBody.contains(".disconnect()"),
                "checkForUpdates opens HttpURLConnection without calling disconnect() in a finally block, leaking sockets"
        );
    }
}
