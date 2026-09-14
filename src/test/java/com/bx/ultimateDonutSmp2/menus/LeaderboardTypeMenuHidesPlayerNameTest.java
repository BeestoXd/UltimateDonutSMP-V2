package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LeaderboardTypeMenuHidesPlayerNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/LeaderboardTypeMenu.java");

    @Test
    void leaderboardEntriesUseHideAwarePlayerName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private ItemStack createEntryItem(");
        int end = source.indexOf("private ItemStack createPlayerRankItem(");
        assertTrue(start >= 0 && end > start, "createEntryItem should still sit above createPlayerRankItem");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName"),
                "Leaderboard entries fill {player} with PlayerData.getUsername(). Scoreboard and "
                        + "auction listing lore already use HideManager.publicName, so a hidden player's "
                        + "real name still appears on the public leaderboard GUI."
        );
    }
}
