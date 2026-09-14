package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdersDeliverConfirmMenuHidesOwnerNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/OrdersDeliverConfirmMenu.java");

    @Test
    void confirmSummaryUsesHideAwareOwnerName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("set(13, OrdersMenuSupport.decorateItem(");
        int end = source.indexOf("set(15, OrdersMenuSupport.button(");
        assertTrue(start >= 0 && end > start, "confirm summary item should still sit at slot 13");

        String summary = source.substring(start, end);
        assertTrue(
                summary.contains("publicName"),
                "ORDERS.GUI.CONFIRM.SUMMARY.NAME is '&f{owner}'S order' and this menu fills {owner} "
                        + "with order.ownerName(). ServerNotificationManager.ownerName(Order) already "
                        + "uses HideManager.publicName for the same owner UUID, so a hidden order "
                        + "owner's real name still appears on the public delivery-confirm GUI."
        );
    }
}
