package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BountyManagerHideDisplayNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/BountyManager.java");

    @Test
    void getDisplayNameDoesNotBypassHideForOfflinePlayers() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public String getDisplayName");
        int end = source.indexOf("public PlacementResult placeBounty");
        assertTrue(start >= 0 && end > start, "getDisplayName should still sit above placeBounty");

        String method = source.substring(start, end);
        assertFalse(
                method.contains("getOfflinePlayer"),
                "Bounty broadcasts use getDisplayName. Returning Bukkit.getOfflinePlayer(uuid).getName() "
                        + "for anyone not currently online skips HideManager, so a scrambled player who logged "
                        + "out appears under their real username. HideManager.publicName(uuid) already applies "
                        + "the alias for both online and offline players."
        );
    }
}
