package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdersBrowseOwnerHideProofTest {

    private static final Path SUPPORT_SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/OrdersMenuSupport.java");
    private static final Path BROWSE_SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/OrdersBrowseMenu.java");

    @Test
    void orderDisplayMustUseHideAwareOwnerName() throws Exception {
        String source = Files.readString(SUPPORT_SOURCE);
        int start = source.indexOf("static ItemStack createOrderDisplay(");
        int end = source.indexOf("static ItemStack createClaimDisplay(");
        assertTrue(start >= 0 && end > start, "createOrderDisplay should exist in OrdersMenuSupport");

        String displayMethod = source.substring(start, end);
        assertTrue(
                displayMethod.contains("publicName"),
                "ORDERS.GUI.ORDER_ITEM lore fills {owner} with order.ownerName(); OrdersDeliverConfirmMenu already uses HideManager.publicName for the same owner"
        );
    }

    @Test
    void visibleOrdersMustUseHideAwareOwnerSearch() throws Exception {
        String source = Files.readString(BROWSE_SOURCE);
        int start = source.indexOf("private List<Order> visibleOrders(");
        int end = source.indexOf("private boolean hasNextPage(");
        assertTrue(start >= 0 && end > start, "visibleOrders should exist in OrdersBrowseMenu");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName"),
                "OrdersBrowseMenu.visibleOrders() must search public hide-aware name rather than order.ownerName()"
        );
    }
}
