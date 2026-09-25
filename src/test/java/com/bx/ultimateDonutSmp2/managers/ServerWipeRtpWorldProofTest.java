package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ServerWipeRtpWorldProofTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/ServerWipeManager.java");

    @Test
    void validateConfiguredWorldsMustNotRequireRtpWorldGate() throws Exception {
        String source = Files.readString(SOURCE);
        assertFalse(
                source.contains("isConfiguredRtpWorld"),
                "Staff-and-Security.md says RESET-WORLDS is the list to regenerate with no RTP gate"
        );
    }
}
