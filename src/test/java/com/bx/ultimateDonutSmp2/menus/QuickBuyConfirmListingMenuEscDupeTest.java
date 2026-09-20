package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickBuyConfirmListingMenuEscDupeTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/QuickBuyConfirmListingMenu.java");

    @Test
    void confirmListingIsNotAnEscBackTarget() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public boolean isEscBackTarget()");
        assertTrue(start >= 0, "Confirm listing must opt out of ESC-back so a listed item cannot be confirmed again");
        int end = source.indexOf("public void handleClick(", start);
        assertTrue(end > start, "isEscBackTarget should sit above handleClick");
        String method = source.substring(start, end);
        assertTrue(method.contains("return false"), "Confirm listing has to stay out of ESC history");
        assertFalse(method.contains("return true"), "Confirm listing must not remain an ESC-back parent");
    }

    @Test
    void handleClickIgnoresClicksAfterTheListingIsConsumed() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public void handleClick(");
        int end = source.indexOf("public void onClose(");
        assertTrue(start >= 0 && end > start, "handleClick should still sit above onClose");
        String method = source.substring(start, end);
        int guard = method.indexOf("if (confirmed)");
        int cancel = method.indexOf("slot == cancelSlot");
        int confirm = method.indexOf("slot == confirmSlot");
        assertTrue(guard >= 0, "A second confirm/cancel after listing restores or lists the escrowed stack again");
        assertTrue(guard < cancel && guard < confirm, "The consumed guard has to run before cancel and confirm");
        assertTrue(method.contains("return;"), "Consumed clicks must no-op rather than restoreEscrow or createListingFromItem");
    }
}
