package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SellMenuTest {

    @Test
    void theSellButtonSlotAlsoBoundsTheSellableGrid() {
        assertEquals(53, SellMenu.clampSellButtonSlot(53));
        assertEquals(
                45,
                SellMenu.clampSellButtonSlot(45),
                "moving the button up must shrink the grid rather than leave a hole in it"
        );
    }

    @Test
    void anOutOfRangeSlotFallsBackToTheLastSlot() {
        assertEquals(53, SellMenu.clampSellButtonSlot(54), "slot 54 is past the end of a 54-slot menu");
        assertEquals(53, SellMenu.clampSellButtonSlot(-1));
        assertEquals(
                53,
                SellMenu.clampSellButtonSlot(0),
                "slot 0 would leave no sellable slots at all"
        );
    }

    @Test
    void shippedMenusYmlNoLongerCarriesTheRemovedSellModes() throws Exception {
        YamlConfiguration menus = new YamlConfiguration();
        menus.load(Path.of("src/main/resources/menus.yml").toFile());

        assertNull(menus.get("SELL-MENU.MODE"), "sell modes were removed; confirming is the only behaviour");
        assertNull(menus.get("SELL-MENU.AUTO-SELL"), "AUTO-SELL only ever applied to the removed instant mode");
        assertTrue(menus.contains("SELL-MENU.SELL-BUTTON"), "the confirm button is what actually sells now");
    }

    @Test
    void shippedSellButtonShowsTheRunningTotal() throws Exception {
        YamlConfiguration menus = new YamlConfiguration();
        menus.load(Path.of("src/main/resources/menus.yml").toFile());

        String title = menus.getString("SELL-MENU.SELL-BUTTON.TITLE", "");
        assertTrue(
                title.contains("{worth}"),
                "the player has to see the price before agreeing to it, so the label carries the total"
        );
        assertEquals(53, menus.getInt("SELL-MENU.SELL-BUTTON.SLOT"));
    }
}
