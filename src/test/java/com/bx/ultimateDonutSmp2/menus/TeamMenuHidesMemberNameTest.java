package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamMenuHidesMemberNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/TeamMenu.java");

    @Test
    void teamRosterUsesHideAwareMemberName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private ItemStack createMemberItem(");
        int end = source.indexOf("private void renderSearchButton(");
        assertTrue(start >= 0 && end > start, "createMemberItem should still sit above renderSearchButton");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName"),
                "Team roster heads use OfflinePlayer.getName(). TeamInfoMenu.displayName already "
                        + "uses HideManager.publicName for the same member UUID on the public /team info "
                        + "GUI, so a hidden teammate's real name still appears on /team."
        );
    }
}
