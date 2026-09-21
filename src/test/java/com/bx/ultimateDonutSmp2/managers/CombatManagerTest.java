package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.models.PlayerData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatManagerTest {

    private static final long TAG_APPLIED = 1_000L;
    private static final long TWENTY_SECOND_EXPIRY = TAG_APPLIED + 20_000L;

    @Test
    void twentySecondTagOpensOnTwentyRatherThanNineteen() {
        assertEquals(20L, CombatManager.remainingSeconds(TWENTY_SECOND_EXPIRY, TAG_APPLIED + 50L));
        assertEquals(20L, CombatManager.remainingSeconds(TWENTY_SECOND_EXPIRY, TAG_APPLIED));
    }

    @Test
    void everyLaterFrameDropsByExactlyOne() {
        assertEquals(19L, CombatManager.remainingSeconds(TWENTY_SECOND_EXPIRY, TAG_APPLIED + 1_050L));
        assertEquals(18L, CombatManager.remainingSeconds(TWENTY_SECOND_EXPIRY, TAG_APPLIED + 2_050L));
        assertEquals(1L, CombatManager.remainingSeconds(TWENTY_SECOND_EXPIRY, TAG_APPLIED + 19_050L));
    }

    @Test
    void aWholeSecondLeftReadsAsThatSecondAndNotTheOneAbove() {
        assertEquals(19L, CombatManager.remainingSeconds(TWENTY_SECOND_EXPIRY, TAG_APPLIED + 1_000L));
        assertEquals(1L, CombatManager.remainingSeconds(TWENTY_SECOND_EXPIRY, TAG_APPLIED + 19_000L));
    }

    @Test
    void aTaggedPlayerIsNeverShownZero() {
        assertEquals(1L, CombatManager.remainingSeconds(TWENTY_SECOND_EXPIRY, TWENTY_SECOND_EXPIRY - 1L));
    }

    @Test
    void anExpiredTagReadsZero() {
        assertEquals(0L, CombatManager.remainingSeconds(TWENTY_SECOND_EXPIRY, TWENTY_SECOND_EXPIRY));
        assertEquals(0L, CombatManager.remainingSeconds(TWENTY_SECOND_EXPIRY, TWENTY_SECOND_EXPIRY + 5_000L));
    }

    @Test
    void theCountdownIsDrawnUntilThePlayerTurnsItOff() {
        PlayerData data = new PlayerData(UUID.randomUUID(), "Tester");
        assertTrue(CombatManager.showsCombatTimer(data));

        data.setCombatTimerEnabled(false);
        assertFalse(CombatManager.showsCombatTimer(data));
    }

    @Test
    void aPlayerWithNoProfileKeepsSeeingTheCountdown() {
        assertTrue(CombatManager.showsCombatTimer(null));
    }

    @Test
    void namespacedFormOfEveryBundledBlockedCommandIsBlockedToo() {
        List<String> blocked = bundledBlockCommands();
        assertTrue(CombatManager.isBlockedCommand("/tpa", blocked), "control");
        assertTrue(
                CombatManager.isBlockedCommand("/ultimatedonutsmp2:tpa", blocked),
                "CombatListener forwards the namespaced token; /ultimatedonutsmp2:tpa must still hit /tpa"
        );
        assertTrue(CombatManager.isBlockedCommand("/UltimateDonutSMP2:Spawn", blocked));
        assertTrue(CombatManager.isBlockedCommand("/ultimatedonutsmp2:afk", blocked));
        assertTrue(CombatManager.isBlockedCommand("/ultimatedonutsmp2:rtp", blocked));
        assertTrue(CombatManager.isBlockedCommand("/ultimatedonutsmp2:homes", blocked));
        assertTrue(CombatManager.isBlockedCommand("/ultimatedonutsmp2:tpa Steve", blocked));
        assertFalse(CombatManager.isBlockedCommand("/tpaaccept", blocked));
        assertFalse(CombatManager.isBlockedCommand("/msg Steve", blocked));
        assertFalse(CombatManager.isBlockedCommand("/home", blocked));
    }

    @Test
    void aListedSubcommandStaysNarrowerThanItsParent() {
        List<String> blocked = List.of("/tpa accept");
        assertFalse(CombatManager.isBlockedCommand("/tpa", blocked));
        assertFalse(CombatManager.isBlockedCommand("/ultimatedonutsmp2:tpa", blocked));
        assertTrue(CombatManager.isBlockedCommand("/tpa accept", blocked));
        assertTrue(CombatManager.isBlockedCommand("/ultimatedonutsmp2:tpa accept extra", blocked));
    }

    private static List<String> bundledBlockCommands() {
        var stream = CombatManagerTest.class.getClassLoader().getResourceAsStream("config.yml");
        assertNotNull(stream);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );
        return config.getStringList("COMBAT-MANAGER.BLOCK-COMMANDS");
    }
}
