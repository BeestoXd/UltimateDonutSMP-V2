package com.bx.ultimateDonutSmp2.dialogs.screens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomesDialogTest {

    private static final int[] TIERS = {3, 9, 27, 90};

    @Test
    void slotsUnderTierOneRequireDonutPlus() {
        assertEquals("Buy Donut&#00A4FC+ &7for more home slots",
                HomesDialog.defaultLockedTooltipForSlot(3, TIERS));
        assertEquals("Buy Donut&#00A4FC+ &7for more home slots",
                HomesDialog.defaultLockedTooltipForSlot(8, TIERS));
    }

    @Test
    void slotsUnderTierTwoRequireDonutPlusPlus() {
        assertEquals("Buy Donut&#00A4FC++ &7for more home slots",
                HomesDialog.defaultLockedTooltipForSlot(9, TIERS));
        assertEquals("Buy Donut&#00A4FC++ &7for more home slots",
                HomesDialog.defaultLockedTooltipForSlot(26, TIERS));
    }

    @Test
    void slotsUnderTierThreeRequireDonutPlusPlusPlus() {
        assertEquals("Buy Donut&#00A4FC+++ &7for more home slots",
                HomesDialog.defaultLockedTooltipForSlot(27, TIERS));
        assertEquals("Buy Donut&#00A4FC+++ &7for more home slots",
                HomesDialog.defaultLockedTooltipForSlot(89, TIERS));
    }
}
