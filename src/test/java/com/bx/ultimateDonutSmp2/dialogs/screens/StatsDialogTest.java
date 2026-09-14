package com.bx.ultimateDonutSmp2.dialogs.screens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatsDialogTest {

    @Test
    void compactAgoUsesTheLargestWholeUnit() {
        assertEquals("0s", StatsDialog.compactAgo(0));
        assertEquals("45s", StatsDialog.compactAgo(45_000L));
        assertEquals("10m", StatsDialog.compactAgo(10 * 60_000L));
        assertEquals("10h", StatsDialog.compactAgo(10 * 3_600_000L));
        assertEquals("2d", StatsDialog.compactAgo(2 * 86_400_000L));
    }

    @Test
    void viewFullProfileSendsStaffToTheProfileViewerAndMembersToThePublicSheet() {
        assertTrue(StatsDialog.shouldOpenStaffFullProfile(true, false));
        assertTrue(StatsDialog.shouldOpenStaffFullProfile(false, true));
        assertTrue(StatsDialog.shouldOpenStaffFullProfile(true, true));
        assertFalse(StatsDialog.shouldOpenStaffFullProfile(false, false));
    }

    @Test
    void playtimeDropsSecondsWhenHoursOrDaysArePresent() {
        // 2h 54m 33s -> 2h 54m (matching stats_player.png and View Full Profile/5.png)
        assertEquals("2h 54m", com.bx.ultimateDonutSmp2.utils.NumberUtils.formatTimeScoreboard(2 * 3600 + 54 * 60 + 33));
        assertEquals("2h 44m", com.bx.ultimateDonutSmp2.utils.NumberUtils.formatTimeScoreboard(2 * 3600 + 44 * 60 + 33));
        assertEquals("45s", com.bx.ultimateDonutSmp2.utils.NumberUtils.formatTimeScoreboard(45));
    }
}
