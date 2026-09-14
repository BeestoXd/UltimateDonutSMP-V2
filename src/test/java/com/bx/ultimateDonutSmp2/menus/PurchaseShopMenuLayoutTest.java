package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurchaseShopMenuLayoutTest {

    private static YamlConfiguration menus;

    @BeforeAll
    static void load() throws Exception {
        menus = new YamlConfiguration();
        menus.load(Path.of("src/main/resources/menus.yml").toFile());
    }

    @Test
    void cancelAndConfirmSitBesideThePreviewWithOneSlotGap() {
        int preview = menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.MAIN.SLOT");
        int cancel = menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.CANCEL.SLOT");
        int confirm = menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.CONFIRM.SLOT");

        assertEquals(13, preview);
        assertEquals(11, cancel, "red cancel is one empty slot left of the preview");
        assertEquals(15, confirm, "green confirm is one empty slot right of the preview");
        assertEquals(preview / 9, cancel / 9, "cancel must share the preview row");
        assertEquals(preview / 9, confirm / 9, "confirm must share the preview row");
        assertEquals(2, preview - cancel, "exactly one empty slot between cancel and preview");
        assertEquals(2, confirm - preview, "exactly one empty slot between preview and confirm");
        assertEquals("RED_STAINED_GLASS_PANE", menus.getString("PURCHASE-SHOP-MENU.BUTTONS.CANCEL.MATERIAL"));
        assertEquals("LIME_STAINED_GLASS_PANE", menus.getString("PURCHASE-SHOP-MENU.BUTTONS.CONFIRM.MATERIAL"));
        assertTrue(cancel % 9 < confirm % 9, "cancel must sit left of confirm");
    }

    @Test
    void confirmButtonsDoNotCollideWithQuantityOrPreview() {
        Set<Integer> used = new HashSet<>();
        used.add(menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.MAIN.SLOT"));
        used.add(menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.CANCEL.SLOT"));
        used.add(menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.CONFIRM.SLOT"));
        used.add(menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.QUANTITY_ADJUST.ADD.ADD_1.SLOT"));
        used.add(menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.QUANTITY_ADJUST.ADD.ADD_10.SLOT"));
        used.add(menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.QUANTITY_ADJUST.ADD.SET_64.SLOT"));
        used.add(menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.QUANTITY_ADJUST.REMOVE.REMOVE_1.SLOT"));
        used.add(menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.QUANTITY_ADJUST.REMOVE.REMOVE_10.SLOT"));
        used.add(menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.QUANTITY_ADJUST.REMOVE.REMOVE_64.SLOT"));
        assertEquals(9, used.size(), "preview, cancel, confirm, and quantity keys must use unique slots");

        int previewRow = menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.MAIN.SLOT") / 9;
        assertTrue(menus.getInt("PURCHASE-SHOP-MENU.BUTTONS.QUANTITY_ADJUST.ADD.ADD_1.SLOT") / 9 != previewRow,
                "quantity keys must not sit on the preview row");
    }
}
