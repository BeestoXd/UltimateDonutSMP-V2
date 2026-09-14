package com.bx.ultimateDonutSmp2.dialogs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DialogSupportTest {

    @Test
    void thePlayersOwnScreenIsNotClosedBeforeADialog() {
        assertFalse(DialogSupport.shouldCloseOpenInventory("CRAFTING"));
        assertFalse(DialogSupport.shouldCloseOpenInventory("CREATIVE"));
        assertFalse(DialogSupport.shouldCloseOpenInventory((String) null));
        assertFalse(DialogSupport.shouldCloseOpenInventory(""));
    }

    @Test
    void aRealContainerIsClosedSoItDoesNotSitUnderTheDialog() {
        assertTrue(DialogSupport.shouldCloseOpenInventory("CHEST"));
        assertTrue(DialogSupport.shouldCloseOpenInventory("HOPPER"));
        assertTrue(DialogSupport.shouldCloseOpenInventory("ENDER_CHEST"));
    }

    @Test
    void closingAChestForADialogIsNotTreatedAsPlayerEsc() {
        assertFalse(DialogSupport.isClosingInventoryForDialog(null));
        assertFalse(DialogSupport.isClosingInventoryForDialog(java.util.UUID.randomUUID()));
    }
}
