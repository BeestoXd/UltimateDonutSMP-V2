package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.models.PlayerData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerVisibilityManagerTest {

    @Test
    void joinDoesNotHideAnyoneWhenTheSettingIsOff() {
        PlayerData data = new PlayerData(UUID.randomUUID(), "Viewer");
        assertFalse(PlayerVisibilityManager.shouldHideAllPlayers(null));
        assertFalse(PlayerVisibilityManager.shouldHideAllPlayers(data));
    }

    @Test
    void joinHidesPlayersOnlyWhenTheViewerAskedToHideEveryone() {
        PlayerData data = new PlayerData(UUID.randomUUID(), "Viewer");
        data.setHideAllPlayersEnabled(true);
        assertTrue(PlayerVisibilityManager.shouldHideAllPlayers(data));
    }
}
