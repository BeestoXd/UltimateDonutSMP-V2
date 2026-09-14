package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RtpStartSearchQueueReentryTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/RTPManager.java");

    @Test
    void startSearchDoesNotAdvanceTheWaitingQueueBeforeThisSearchIsRegistered() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private void startSearch(");
        int end = source.indexOf("private void tickSearch(UUID playerId)");
        assertTrue(start >= 0 && end > start, "startSearch should still sit above tickSearch");

        String method = source.substring(start, end);
        assertFalse(
                method.contains("clearSearch("),
                "startSearch begins with clearSearch, and stopSearch always calls processNextInQueue. "
                        + "processNextInQueue is synchronized and reentrant, so it can startSearch the next "
                        + "waiter before this player is in activeSearches. Both searches then run even when "
                        + "PLAYERS-IN-RTP is 1."
        );
    }
}
