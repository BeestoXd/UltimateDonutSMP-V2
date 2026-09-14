package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuctionBrowsePurchaseHidesSellerNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/AuctionHouseBrowseMenu.java");

    @Test
    void purchaseSuccessMessageUsesHideAwareSellerName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private void purchase(");
        int end = source.indexOf("private void open(Player player, AuctionBrowseRequest next");
        assertTrue(start >= 0 && end > start, "purchase should still sit above open");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName"),
                "The auction browse purchase-success message fills {seller} with listing.sellerName(). "
                        + "The listing lore in AuctionHouseMenuSupport already uses HideManager.publicName "
                        + "for the same seller, so a hidden seller's real name still appears in chat after a buy."
        );
    }
}
