package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CrateManagerHideClaimBroadcastTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/CrateManager.java");

    @Test
    void claimBroadcastUsesHideManagerPublicName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private String buildClaimBroadcast");
        int end = source.indexOf("private boolean shouldBroadcastClaim");
        assertTrue(start >= 0 && end > start, "buildClaimBroadcast should still sit above shouldBroadcastClaim");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName") || method.contains("HideManager"),
                "Crate claim broadcasts go to every online player. Using player.getName() "
                        + "leaks a scrambled player's real username. AuctionHouseManager and FfaManager "
                        + "already use HideManager.publicName for the same kind of announcement."
        );
    }
}
