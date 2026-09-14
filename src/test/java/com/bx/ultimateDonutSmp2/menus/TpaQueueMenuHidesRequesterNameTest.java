package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TpaQueueMenuHidesRequesterNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/TpaQueueMenu.java");

    @Test
    void queuedTpaItemsUseHideAwareRequesterName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private String resolveName(");
        int end = source.indexOf("private String formatRemaining(");
        assertTrue(start >= 0 && end > start, "resolveName should still sit above formatRemaining");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName"),
                "TPA queue lore fills {player} from Player.getName() / OfflinePlayer.getName(). "
                        + "TeamInfoMenu already uses HideManager.publicName for the same kind of public "
                        + "player label, so a hidden requester's real name still appears on the queue GUI."
        );
    }
}
