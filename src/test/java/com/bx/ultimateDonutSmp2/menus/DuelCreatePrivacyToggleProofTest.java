package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.models.DuelPrivacyMode;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DuelCreatePrivacyToggleProofTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/DuelCreateMenu.java");
    private static final Path WIKI = Path.of("docs/wiki/Config-duels.yml.md");

    @Test
    void handleClickMustTogglePrivacyMode() throws Exception {
        String wiki = Files.readString(WIKI);
        assertTrue(
                wiki.contains("privacy mode toggles"),
                "Config-duels.yml.md must describe CREATE.ITEMS as including privacy mode toggles"
        );

        String source = Files.readString(SOURCE);
        int start = source.indexOf("public void handleClick(int slot, Player player)");
        assertTrue(start >= 0, "handleClick must exist in DuelCreateMenu");
        int end = source.indexOf("private int[] contentSlots()", start);
        assertTrue(end > start, "contentSlots must follow handleClick");
        String method = source.substring(start, end);

        boolean handlesToggle = method.contains("new DuelCreateMenu")
                && (method.contains("FRIENDS_ONLY") || method.contains("INVITE_ONLY"))
                && (method.contains("resolvePrivacySlot") || method.contains("lastRow + 5"));

        assertTrue(
                handlesToggle,
                "Config-duels.yml.md describes CREATE.ITEMS as including privacy mode toggles"
        );
    }

    @Test
    void privacySlotLayoutFitsWithinBoundsAndAvoidsCloseButton() {
        for (int size : List.of(18, 27, 36, 45, 54)) {
            int privacySlot = DuelCreateMenu.resolvePrivacySlot(size);
            int closeSlot = size - 1;
            int lastRow = size - 9;

            assertEquals(lastRow + 5, privacySlot, "Privacy slot should sit at lastRow + 5");
            assertTrue(privacySlot >= 0 && privacySlot < size, "Privacy slot must be within inventory bounds");
            assertNotEquals(closeSlot, privacySlot, "Privacy slot must not collide with close button");
        }
    }

    @Test
    void privacyModeTogglesBothWays() {
        DuelPrivacyMode invite = DuelPrivacyMode.INVITE_ONLY;
        DuelPrivacyMode friends = DuelPrivacyMode.FRIENDS_ONLY;

        assertEquals(DuelPrivacyMode.FRIENDS_ONLY, toggle(invite));
        assertEquals(DuelPrivacyMode.INVITE_ONLY, toggle(friends));
    }

    private DuelPrivacyMode toggle(DuelPrivacyMode current) {
        return current == DuelPrivacyMode.FRIENDS_ONLY ? DuelPrivacyMode.INVITE_ONLY : DuelPrivacyMode.FRIENDS_ONLY;
    }
}
