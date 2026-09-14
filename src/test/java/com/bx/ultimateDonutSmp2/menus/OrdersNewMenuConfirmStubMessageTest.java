package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;

class OrdersNewMenuConfirmStubMessageTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/OrdersNewMenu.java");

    @Test
    void confirmBusyAndCooldownDoNotSendStubEllipsis() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("if (slot == 16) {");
        int end = source.indexOf("private String resolveFailureMessage(");
        assertFalse(start < 0 || end <= start, "confirm click should still sit above resolveFailureMessage");

        String confirm = source.substring(start, end);
        assertFalse(
                confirm.contains("\"&c...\""),
                "Confirming a new order while beginAction fails or the click cooldown is active "
                        + "sends ColorUtils.toComponent(\"&c...\"). OrdersCollectMenu already tells the "
                        + "player 'Orders is still processing' / 'Slow down for a moment' on those same "
                        + "guards, so the new-order confirm path still shows a stub instead of a real reason."
        );
    }
}
