package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfirmPurchaseHidesSellerNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/ConfirmPurchaseGui.java");

    @Test
    void confirmPurchaseSuccessMessageUsesHideAwareSellerName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public void handleClick(");
        int end = source.indexOf("private void reopen(");
        assertTrue(start >= 0 && end > start, "handleClick should still sit above reopen");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName"),
                "Confirm-purchase success fills {seller} with listing.sellerName(). "
                        + "The confirm GUI's listing item already uses HideManager.publicName via "
                        + "createListingDisplay, so a hidden seller's real name still appears in chat after confirm."
        );
    }
}
