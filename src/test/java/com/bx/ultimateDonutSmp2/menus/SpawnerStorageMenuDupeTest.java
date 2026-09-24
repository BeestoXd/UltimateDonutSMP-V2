package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnerStorageMenuDupeTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/SpawnerStorageMenu.java");

    @Test
    void bottomInventoryShiftClicksAreCancelled() throws Exception {
        String source = Files.readString(SOURCE);
        int bottomSection = source.indexOf("// 1. Click in player's bottom inventory");
        assertTrue(bottomSection >= 0, "Menu must handle player bottom inventory clicks");
        int nextSection = source.indexOf("// 2. Click in control row / bottom bar", bottomSection);
        assertTrue(nextSection > bottomSection);

        String bottomCode = source.substring(bottomSection, nextSection);
        assertTrue(bottomCode.contains("event.setCancelled(true);"),
                "Shift-clicking from bottom inventory into spawner must be cancelled");
        assertFalse(bottomCode.contains("applyStorageMeta("),
                "Bottom inventory shift-clicks must never insert items into spawner storage slots");
        assertFalse(bottomCode.contains("instance.setSlotLoot("),
                "Bottom inventory shift-clicks must never save loot into spawner instance");
    }

    @Test
    void cursorDepositsIntoStorageSlotsAreBlocked() throws Exception {
        String source = Files.readString(SOURCE);
        int storageSection = source.indexOf("// 3. Click in storage content slots");
        assertTrue(storageSection >= 0, "Menu must handle storage content slot clicks");
        int dragSection = source.indexOf("public void handleInventoryDrag(", storageSection);
        assertTrue(dragSection > storageSection);

        String storageCode = source.substring(storageSection, dragSection);
        assertTrue(storageCode.contains("cursorItem != null && !cursorItem.getType().isAir()"),
                "Must check if cursor is holding an item");
        assertFalse(storageCode.contains("cursorItem.setAmount("),
                "Cursor items must never be consumed or deposited into spawner storage slots");
    }

    @Test
    void hotbarDropAndNumberKeysAreBlockedOnStorageSlots() throws Exception {
        String source = Files.readString(SOURCE);
        int storageSection = source.indexOf("// 3. Click in storage content slots");
        assertTrue(storageSection >= 0);
        int dragSection = source.indexOf("public void handleInventoryDrag(", storageSection);

        String storageCode = source.substring(storageSection, dragSection);
        assertTrue(storageCode.contains("ClickType.DROP"), "Drop key must be blocked on storage slots");
        assertTrue(storageCode.contains("ClickType.CONTROL_DROP"), "Control+Drop key must be blocked on storage slots");
        assertTrue(storageCode.contains("ClickType.NUMBER_KEY"), "Number keys must be blocked on storage slots");
        assertTrue(storageCode.contains("ClickType.SWAP_OFFHAND"), "Offhand swap must be blocked on storage slots");
    }

    @Test
    void droppingPageLootForcesImmediateRefresh() throws Exception {
        String source = Files.readString(SOURCE);
        int controlSection = source.indexOf("// 2. Click in control row / bottom bar");
        assertTrue(controlSection >= 0);
        int storageSection = source.indexOf("// 3. Click in storage content slots", controlSection);

        String controlCode = source.substring(controlSection, storageSection);
        int dropIndex = controlCode.indexOf("rawSlot == dropSlot");
        assertTrue(dropIndex >= 0);
        String dropBranch = controlCode.substring(dropIndex, controlCode.indexOf("else if", dropIndex));

        assertTrue(dropBranch.contains("forceRefresh(player);"),
                "Dropping page loot must force-refresh the inventory so items are cleared despite interaction cooldown");
        assertFalse(dropBranch.contains("refresh(player);"),
                "Dropping page loot must not use standard refresh which is skipped within 2s of click");
    }

    @Test
    void dragEventsIntoTopInventoryAreCancelled() throws Exception {
        String source = Files.readString(SOURCE);
        int dragSection = source.indexOf("public void handleInventoryDrag(");
        assertTrue(dragSection >= 0);

        String dragCode = source.substring(dragSection);
        assertTrue(dragCode.contains("rawSlot < plugin.getSpawnerManager().getStorageSize()"),
                "Any drag into spawner inventory must be cancelled");
        assertFalse(dragCode.contains("rawSlot >= lastRow"),
                "Drag cancellation must not be restricted to the control row");
    }
}
