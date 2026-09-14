package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PvpMatchHistoryMenuHidesOpponentNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/PvpMatchHistoryMenu.java");

    @Test
    void matchHistoryUsesHideAwareFighterNames() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private ItemStack createMatchItem(");
        int end = source.indexOf("private void buildPageButtons(");
        assertTrue(start >= 0 && end > start, "createMatchItem should still sit above buildPageButtons");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName"),
                "Match history lore calls PvpMatchManager.resolveName, which returns "
                        + "Player.getName() / last-known username. /pvp history <player> is public and "
                        + "the scoreboard already uses HideManager.publicName, so a hidden fighter's "
                        + "real name still appears on the history GUI."
        );
    }
}
