package com.bx.ultimateDonutSmp2.utils;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentUtilsHidesRestrictedTargetNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/utils/PaymentUtils.java");

    @Test
    void paymentRefusalChatUsesHideAwareTargetName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("only accepts payments from friends");
        assertTrue(start >= 0, "friends-only refusal copy should still be in PaymentUtils");

        String around = source.substring(Math.max(0, start - 120), start);
        assertTrue(
                around.contains("publicName"),
                "Pay/shard refusal chat concatenates target.getName() when the target only accepts "
                        + "friends. The success path in the same methods already uses "
                        + "HideManager.publicName, so a hidden player's real name still appears on /pay."
        );
    }
}
