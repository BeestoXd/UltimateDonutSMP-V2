package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PvpLeaderboardMenuHidesPlayerNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/PvpLeaderboardMenu.java");

    @Test
    void pvpLeaderboardLinesUseHideAwarePlayerName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private List<String> buildLore(");
        int end = source.indexOf("public void handleClick(");
        assertTrue(start >= 0 && end > start, "buildLore should still sit above handleClick");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName"),
                "PvP leaderboard lore fills {player} with entry.name(), which PvpManager.getTop "
                        + "loads from getLastKnownUsername. The economy leaderboard GUI and scoreboard "
                        + "already use HideManager.publicName, so a hidden player's real name still "
                        + "appears on /pvp leaderboard."
        );
    }
}
