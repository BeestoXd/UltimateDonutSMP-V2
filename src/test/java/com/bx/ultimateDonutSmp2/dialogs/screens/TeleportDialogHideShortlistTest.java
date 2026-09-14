package com.bx.ultimateDonutSmp2.dialogs.screens;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TeleportDialogHideShortlistTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/dialogs/screens/TeleportDialog.java");

    @Test
    void seedShortlistDoesNotListHiddenOnlinePlayersByRealName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private void seedShortlist");
        assertTrue(start >= 0, "seedShortlist should still exist");

        String method = source.substring(start);
        assertTrue(method.contains("getOnlinePlayers"), "seedShortlist still walks online players");
        assertTrue(
                method.contains("isHidden")
                        || method.contains("hiddenFrom")
                        || method.contains("HideManager")
                        || method.contains("canSeeRealIdentity")
                        || method.contains("visibleName")
                        || method.contains("onlineNames"),
                "Online-player seeding must go through HideManager so scrambled players "
                        + "do not appear by real name. /tpa tab complete already uses "
                        + "HideManager.onlineNames, and FriendsDialog/StatsDialog skip hidden online players."
        );
    }
}
