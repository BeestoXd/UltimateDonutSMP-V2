package com.bx.ultimateDonutSmp2.listeners;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaintenanceLoginGateTest {

    @Test
    void maintenanceWithNowhereToSendThemRefusesTheLogin() {
        assertTrue(PlayerJoinQuitListener.deniesMaintenanceLogin(true, false, false));
    }

    @Test
    void everyoneElseIsStillLetThrough() {
        assertFalse(PlayerJoinQuitListener.deniesMaintenanceLogin(false, false, false));
        assertFalse(PlayerJoinQuitListener.deniesMaintenanceLogin(true, true, false));
        assertFalse(PlayerJoinQuitListener.deniesMaintenanceLogin(true, false, true));
    }

    @Test
    void staleJoinInvulnerabilityIsClearedUnlessAModeNeedsIt() {
        assertFalse(PlayerJoinQuitListener.shouldKeepJoinInvulnerability(false, false, false, false));
        assertTrue(PlayerJoinQuitListener.shouldKeepJoinInvulnerability(true, false, false, false));
        assertTrue(PlayerJoinQuitListener.shouldKeepJoinInvulnerability(false, true, false, false));
        assertTrue(PlayerJoinQuitListener.shouldKeepJoinInvulnerability(false, false, true, false));
        assertTrue(PlayerJoinQuitListener.shouldKeepJoinInvulnerability(false, false, false, true));
    }
}
