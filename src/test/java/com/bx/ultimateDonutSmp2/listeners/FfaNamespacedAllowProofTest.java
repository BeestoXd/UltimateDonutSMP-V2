package com.bx.ultimateDonutSmp2.listeners;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FfaNamespacedAllowProofTest {

    @Test
    void plainLeaveIsAllowed() {
        assertTrue(FfaListener.isAllowedFfaCommand("/leave"));
        assertTrue(FfaListener.isAllowedFfaCommand("/ffa"));
        assertTrue(FfaListener.isAllowedFfaCommand("/ffastats"));
    }

    @Test
    void namespacedLeaveMustBeAllowedToo() {
        assertTrue(
                FfaListener.isAllowedFfaCommand("/ultimatedonutsmp2:leave"),
                "FfaListener.onCommand compares the raw token to /leave and would cancel /ultimatedonutsmp2:leave"
        );
        assertTrue(FfaListener.isAllowedFfaCommand("/UltimateDonutSMP2:Leave"));
        assertTrue(FfaListener.isAllowedFfaCommand("/ultimatedonutsmp2:ffa"));
        assertTrue(FfaListener.isAllowedFfaCommand("/ultimatedonutsmp2:ffastats"));
        assertTrue(FfaListener.isAllowedFfaCommand("/ultimatedonutsmp2:ffa join"));
    }

    @Test
    void blockedCommandsRemainBlocked() {
        assertFalse(FfaListener.isAllowedFfaCommand("/spawn"));
        assertFalse(FfaListener.isAllowedFfaCommand("/ultimatedonutsmp2:spawn"));
        assertFalse(FfaListener.isAllowedFfaCommand("/tpa Steve"));
        assertFalse(FfaListener.isAllowedFfaCommand("/ultimatedonutsmp2:tpa Steve"));
        assertFalse(FfaListener.isAllowedFfaCommand("/leaveextra"));
        assertFalse(FfaListener.isAllowedFfaCommand(null));
        assertFalse(FfaListener.isAllowedFfaCommand(""));
    }
}
