package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickBuyTransactionsMenuHidesCounterpartyNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/QuickBuyTransactionsMenu.java");

    @Test
    void transactionLoreUsesHideAwareCounterpartyNames() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public void build(Player player)");
        int end = source.indexOf("public void handleClick(");
        assertTrue(start >= 0 && end > start, "build should still sit above handleClick");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName"),
                "Sold-row lore uses OfflinePlayer.getName() for the buyer and bought-row lore uses "
                        + "listing.sellerName(). Auction listing lore already uses HideManager.publicName "
                        + "for the same seller UUID, so a hidden buyer's or seller's real name still "
                        + "appears on the transactions GUI."
        );
    }
}
