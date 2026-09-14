package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RtpQueueRequeueRestartsMatchTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/RTPQueueManager.java");

    @Test
    void failedMatchRequeueStartsAnotherMatchWhenThePartyStillFillsTheQueue() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private void requeue(");
        int end = source.indexOf("private void rejoin(");
        assertTrue(start >= 0 && end > start, "requeue should still sit above rejoin");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("startMatchIfReady"),
                "When a match search fails, requeue puts the online party back in waiting but never "
                        + "calls startMatchIfReady. A full party (MATCH-SIZE 2 with both players still "
                        + "online) then sits at 2/2 until somebody else joins."
        );
    }
}
