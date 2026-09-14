package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamKickConfirmMenuHidesTargetNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/TeamKickConfirmMenu.java");

    @Test
    void kickConfirmUsesHideAwareTargetName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private String resolveTargetName(");
        int end = source.indexOf("private FileConfiguration menus(");
        assertTrue(start >= 0 && end > start, "resolveTargetName should still sit above menus()");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName"),
                "Kick confirm fills {player} from OfflinePlayer.getName() / last-known username. "
                        + "TeamInfoMenu.displayName already uses HideManager.publicName for the same "
                        + "member UUID, so kicking a hidden teammate still shows their real name."
        );
    }
}
