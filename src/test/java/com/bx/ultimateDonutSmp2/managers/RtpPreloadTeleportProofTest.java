package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RtpPreloadTeleportProofTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/RTPManager.java");

    @Test
    void preloadTeleportChunksMustHaveACallSite() throws Exception {
        String source = Files.readString(SOURCE);
        int declIndex = source.indexOf("CompletableFuture<Void> preloadTeleportChunks(Location destination)");
        assertTrue(declIndex >= 0, "preloadTeleportChunks must exist in RTPManager");

        int firstCall = source.indexOf("preloadTeleportChunks(");
        int secondCall = source.indexOf("preloadTeleportChunks(", firstCall + 1);

        assertTrue(
                secondCall >= 0,
                "Config-rtp.yml.md says PRELOAD-TELEPORT-CHUNKS loads chunks before the teleport lands"
        );
    }

    @Test
    void queuePreparedRtpTeleportMustAwaitPreload() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private void queuePreparedRtpTeleport(");
        int end = source.indexOf("private void sendFoundActionBar(");
        assertTrue(start >= 0 && end > start, "queuePreparedRtpTeleport should sit above sendFoundActionBar");

        String method = source.substring(start, end);
        boolean callsPreload = method.contains("preloadTeleportChunks(");
        boolean awaitsPreload = method.contains("preloadFuture.whenComplete(")
                || method.contains("whenComplete(");

        assertTrue(
                callsPreload && awaitsPreload,
                "finishSearch queues TeleportManager immediately; PRELOAD-TELEPORT-CHUNKS never runs"
        );
    }
}
