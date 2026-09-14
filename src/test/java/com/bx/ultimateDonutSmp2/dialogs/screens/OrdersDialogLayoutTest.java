package com.bx.ultimateDonutSmp2.dialogs.screens;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdersDialogLayoutTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/dialogs/screens/OrdersDialog.java");

    @Test
    void openChooseItemMatchesReferenceDesign() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public boolean openChooseItem(Player player, String query)");
        int end = source.indexOf("public boolean openAmount");
        assertTrue(start >= 0 && end > start, "openChooseItem must exist");

        String method = source.substring(start, end);
        assertTrue(method.contains("CHOOSE_INPUT_WIDTH"), "Search input width must use CHOOSE_INPUT_WIDTH");
        assertTrue(method.contains("CHOOSE_SEARCH_WIDTH"), "Search button width must use CHOOSE_SEARCH_WIDTH");
        assertTrue(method.contains("CHOOSE_ITEM_WIDTH"), "Item button width must use CHOOSE_ITEM_WIDTH");
        assertTrue(method.contains("CHOOSE_CANCEL_WIDTH"), "Cancel button width must use CHOOSE_CANCEL_WIDTH");
        assertTrue(method.contains("\"&cCancel\""), "Cancel button must be red &cCancel");
        assertTrue(source.contains("CHOOSE_INPUT_WIDTH = 220"), "Search input width must be 220");
        assertTrue(source.contains("CHOOSE_SEARCH_WIDTH = 100"), "Search button width must be 100");
        assertTrue(source.contains("CHOOSE_ITEM_WIDTH = 130"), "Item button width must be 130");
        assertTrue(source.contains("CHOOSE_CANCEL_WIDTH = 100"), "Cancel button width must be 100");
    }

    @Test
    void openAmountMatchesReferenceDesign() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public boolean openAmount");
        int end = source.indexOf("public boolean openPrice");
        assertTrue(start >= 0 && end > start, "openAmount must exist");

        String method = source.substring(start, end);
        assertFalse(method.contains(".exit("), "openAmount must not use bottom pinned exit button");
        assertTrue(method.contains(".columns(2)"), "openAmount must use 2 columns");
        assertTrue(method.contains("\"&cCancel\""), "Cancel button must be red &cCancel");
        assertTrue(method.contains("\"&aNext\""), "Next button must be green");

        int cancelIdx = method.indexOf("ORD_AMT_CAN");
        int nextIdx = method.indexOf("ORD_AMT_NEXT");
        assertTrue(cancelIdx >= 0 && nextIdx > cancelIdx, "Cancel button must come before Next button in grid");
    }

    @Test
    void openPriceMatchesReferenceDesign() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public boolean openPrice");
        int end = source.indexOf("public boolean openReview");
        assertTrue(start >= 0 && end > start, "openPrice must exist");

        String method = source.substring(start, end);
        assertFalse(method.contains(".exit("), "openPrice must not use bottom pinned exit button");
        assertTrue(method.contains(".columns(2)"), "openPrice must use 2 columns");

        int invalidIdx = method.indexOf("INVALID");
        int itemIdx = method.indexOf(".item(");
        assertTrue(invalidIdx >= 0 && itemIdx > invalidIdx, "Invalid price text must precede item icon");

        assertTrue(method.contains("\"&7Amount: &f{amount}\""), "Amount label must have &7 prefix and &f value");
        assertTrue(method.contains("\"&7Minimum: &a$ &f{min}\""), "Minimum label must have &7 prefix, &a$ and &f value");
        assertTrue(method.contains("\"&cCancel\""), "Cancel button must be red &cCancel");
        assertTrue(method.contains("\"&aReview Order\""), "Review Order button must be green");

        int cancelIdx = method.indexOf("ORD_PRICE_CAN");
        int nextIdx = method.indexOf("ORD_PRICE_GO");
        assertTrue(cancelIdx >= 0 && nextIdx > cancelIdx, "Cancel button must come before Review Order in grid");
    }

    @Test
    void openReviewMatchesReferenceDesign() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public boolean openReview");
        int end = source.indexOf("public boolean handle");
        assertTrue(start >= 0 && end > start, "openReview must exist");

        String method = source.substring(start, end);
        assertFalse(method.contains(".exit("), "openReview must not use bottom pinned exit button");
        assertTrue(method.contains(".columns(2)"), "openReview must use 2 columns");

        assertTrue(method.contains("\"&7Item: &f{item}\""), "Item label must have &7 prefix and &f value");
        assertTrue(method.contains("\"&7Amount: &f{amount}\""), "Amount label must have &7 prefix and &f value");
        assertTrue(method.contains("\"&7Price: &a$ &f{price} each\""), "Price label must have &7 prefix, &a$ and &f value");
        assertTrue(method.contains("\"&7Total: &a$ &f{total}\""), "Total label must have &7 prefix, &a$ and &f value");

        assertTrue(method.contains("REVIEW_BUTTON_WIDTH"), "Review edit buttons must use REVIEW_BUTTON_WIDTH");
        assertTrue(method.contains("REVIEW_CREATE_WIDTH"), "Create Order button must use REVIEW_CREATE_WIDTH");
        assertTrue(source.contains("REVIEW_BUTTON_WIDTH = 130"), "Review edit buttons width must be 130");
        assertTrue(source.contains("REVIEW_CREATE_WIDTH = 150"), "Create Order button width must be 150");

        assertTrue(method.contains("\"&cCancel\""), "Cancel button must be red &cCancel");
        assertTrue(method.contains("\"&aCreate Order\""), "Create Order button must be green");
    }

    @Test
    void sanitizePriceLineAddsCurrencySymbolToLegacyStrings() {
        org.junit.jupiter.api.Assertions.assertEquals(
                "&7Minimum: &a$ &f10",
                OrdersDialog.sanitizePriceLine("Minimum: 10", "Minimum:", "10", "$", "&a$")
        );
        org.junit.jupiter.api.Assertions.assertEquals(
                "&7Price: &a$ &f10 each",
                OrdersDialog.sanitizePriceLine("Price: 10 each", "Price:", "10 each", "$", "&a$")
        );
        org.junit.jupiter.api.Assertions.assertEquals(
                "&7Price: &a$ &f10 &feach",
                OrdersDialog.sanitizePriceLine("Price: 10 &feach", "Price:", "10 &feach", "$", "&a$")
        );
        org.junit.jupiter.api.Assertions.assertEquals(
                "&7Total: &a$ &f100",
                OrdersDialog.sanitizePriceLine("Total: 100", "Total:", "100", "$", "&a$")
        );
        // Preserves already formatted strings with $
        org.junit.jupiter.api.Assertions.assertEquals(
                "&7Minimum: &a$ &f10",
                OrdersDialog.sanitizePriceLine("&7Minimum: &a$ &f10", "Minimum:", "10", "$", "&a$")
        );
    }
}
