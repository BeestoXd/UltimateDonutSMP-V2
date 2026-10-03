package com.bx.ultimateDonutSmp2.amethyst;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmethystToolsCountdownTest {

    @Test
    void isHeldSlotIdentifiesMainHandAndOffHand() {
        assertTrue(AmethystToolsTask.isHeldSlot(0, 0), "held slot 0 is held");
        assertTrue(AmethystToolsTask.isHeldSlot(5, 5), "held slot 5 is held");
        assertTrue(AmethystToolsTask.isHeldSlot(40, 0), "slot 40 is off-hand and always treated as held");
        assertTrue(AmethystToolsTask.isHeldSlot(40, 5), "slot 40 is off-hand and always treated as held");

        assertFalse(AmethystToolsTask.isHeldSlot(1, 0), "slot 1 is not held when holding 0");
        assertFalse(AmethystToolsTask.isHeldSlot(9, 5), "slot 9 is not held");
        assertFalse(AmethystToolsTask.isHeldSlot(35, 5), "slot 35 is not held");
    }

    @Test
    void shouldUpdateSlotCountdownRespectsPreventHeldAnimation() {
        int heldSlot = 2;

        // When countdown is disabled entirely, no slot updates
        assertFalse(AmethystToolsTask.shouldUpdateSlotCountdown(heldSlot, heldSlot, false, true));
        assertFalse(AmethystToolsTask.shouldUpdateSlotCountdown(40, heldSlot, false, true));
        assertFalse(AmethystToolsTask.shouldUpdateSlotCountdown(0, heldSlot, false, true));

        // When countdown is enabled and preventHeldAnimation is true:
        // Held slot and offhand MUST NOT update in the periodic timer (prevents tool bobbing in hand)
        assertFalse(AmethystToolsTask.shouldUpdateSlotCountdown(heldSlot, heldSlot, true, true),
                "held slot must not update in periodic timer when preventHeldAnimation is true");
        assertFalse(AmethystToolsTask.shouldUpdateSlotCountdown(40, heldSlot, true, true),
                "offhand slot must not update in periodic timer when preventHeldAnimation is true");

        // Non-held inventory slots DO update in periodic timer
        assertTrue(AmethystToolsTask.shouldUpdateSlotCountdown(0, heldSlot, true, true),
                "non-held slot 0 should update in periodic timer");
        assertTrue(AmethystToolsTask.shouldUpdateSlotCountdown(1, heldSlot, true, true),
                "non-held slot 1 should update in periodic timer");
        assertTrue(AmethystToolsTask.shouldUpdateSlotCountdown(15, heldSlot, true, true),
                "backpack slot 15 should update in periodic timer");

        // When preventHeldAnimation is false (admin opt-out), all slots update
        assertTrue(AmethystToolsTask.shouldUpdateSlotCountdown(heldSlot, heldSlot, true, false),
                "held slot updates when preventHeldAnimation is disabled");
        assertTrue(AmethystToolsTask.shouldUpdateSlotCountdown(40, heldSlot, true, false),
                "offhand slot updates when preventHeldAnimation is disabled");
        assertTrue(AmethystToolsTask.shouldUpdateSlotCountdown(0, heldSlot, true, false));
    }

    @Test
    void countdownConfigurationSectionExistsInAmethystToolsYml() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("src/main/resources/amethyst-tools.yml"));
        ConfigurationSection countdown = config.getConfigurationSection("AMETHYST-TOOLS.COUNTDOWN");
        assertNotNull(countdown, "AMETHYST-TOOLS.COUNTDOWN section must be configured");

        assertTrue(countdown.getBoolean("ENABLED"), "COUNTDOWN.ENABLED defaults to true");
        assertTrue(countdown.getBoolean("PREVENT-HELD-TOOL-ANIMATION"),
                "COUNTDOWN.PREVENT-HELD-TOOL-ANIMATION defaults to true to fix #188 tool bobbing");
    }
}
