package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Guards Your Orders against drifting away from
 * {@code Design/Orders/Your Orders} and {@code Design/Orders/Your Orders/Next Page}.
 */
class OrdersMyOrdersMenuLayoutTest {

    private static YamlConfiguration orders;

    @BeforeAll
    static void load() throws Exception {
        orders = new YamlConfiguration();
        orders.load(Path.of("src/main/resources/orders.yml").toFile());
    }

    @Test
    void page1KeepsEighteenNewOrderSlotsAndTwentySevenLocks() {
        assertEquals("Orders -> Your Orders", orders.getString("GUI.MY_ORDERS.TITLE"));
        assertEquals(54, orders.getInt("GUI.MY_ORDERS.SIZE"));
        assertEquals(45, orders.getInt("GUI.MY_ORDERS.ITEMS_PER_PAGE"));
        assertEquals(18, orders.getInt("GUI.MY_ORDERS.VISIBLE_SLOTS"));
        assertEquals(45, OrdersMyOrdersMenu.GRID_SLOTS);
        assertEquals(
                27,
                orders.getInt("GUI.MY_ORDERS.ITEMS_PER_PAGE") - orders.getInt("GUI.MY_ORDERS.VISIBLE_SLOTS"),
                "page 1 fills rows 1-2 with order slots and leaves rows 3-5 locked"
        );
    }

    @Test
    void nextPageFillsAllFiveContentRowsWithDonutPlusPlusLocks() {
        assertEquals(45, orders.getInt("GUI.MY_ORDERS.PLUS_PAGE_SLOTS"));
        assertEquals(45, OrdersMyOrdersMenu.DEFAULT_PLUS_PAGE_SLOTS);
        assertEquals(
                OrdersMyOrdersMenu.GRID_SLOTS,
                orders.getInt("GUI.MY_ORDERS.PLUS_PAGE_SLOTS"),
                "page 2 fills slots 0-44; a 36-slot fill leaves row 5 empty"
        );
    }

    @Test
    void nextPageControlRowMatchesTheDesign() {
        assertEquals(45, orders.getInt("GUI.MAIN.BUTTONS.PREV.SLOT"));
        assertEquals(47, orders.getInt("GUI.MAIN.BUTTONS.FILTER.SLOT"));
        assertEquals(48, orders.getInt("GUI.MAIN.BUTTONS.SHARD_SHOP.SLOT"));
        assertEquals(49, orders.getInt("GUI.MAIN.BUTTONS.INFO.SLOT"));
        assertEquals(51, orders.getInt("GUI.MAIN.BUTTONS.MY_ORDERS.SLOT"));
        assertEquals(53, orders.getInt("GUI.MAIN.BUTTONS.NEXT.SLOT"));
        assertEquals(OrdersMenuSupport.PREV_SLOT, orders.getInt("GUI.MAIN.BUTTONS.PREV.SLOT"));
        assertEquals(OrdersMenuSupport.NEXT_SLOT, orders.getInt("GUI.MAIN.BUTTONS.NEXT.SLOT"));
    }
}
