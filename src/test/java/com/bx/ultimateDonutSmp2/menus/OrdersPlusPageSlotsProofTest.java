package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdersPlusPageSlotsProofTest {

    private static final Path ORDERS_MANAGER = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/OrdersManager.java");
    private static final Path MY_ORDERS_MENU = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/OrdersMyOrdersMenu.java");

    @Test
    void plusPageSlotsMustHaveAJavaReader() throws Exception {
        String managerSource = Files.readString(ORDERS_MANAGER);
        assertTrue(
                managerSource.contains("GUI.MY_ORDERS.PLUS_PAGE_SLOTS"),
                "Config-orders.yml.md tables PLUS_PAGE_SLOTS as the Donut++ page-2 lock fill"
        );

        String menuSource = Files.readString(MY_ORDERS_MENU);
        assertTrue(
                menuSource.contains("getMyOrdersPlusPageSlots"),
                "OrdersMyOrdersMenu must use getMyOrdersPlusPageSlots to fill locked slots on page 2"
        );
    }
}
