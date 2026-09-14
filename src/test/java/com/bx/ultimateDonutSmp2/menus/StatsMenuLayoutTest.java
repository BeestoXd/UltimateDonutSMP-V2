package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Guards {@code STATS-MENU} against drifting away from
 * {@code Design/Dialog API/Stats/View Full Profile}.
 */
class StatsMenuLayoutTest {

    private static YamlConfiguration menus;

    @BeforeAll
    static void loadConfig() throws Exception {
        menus = new YamlConfiguration();
        menus.load(Path.of("src/main/resources/menus.yml").toFile());
    }

    @Test
    void fullProfileChestMatchesTheReferenceLayout() {
        assertEquals("&8{username} Stats", menus.getString("STATS-MENU.TITLE"));
        assertEquals(36, menus.getInt("STATS-MENU.SIZE"));
        assertEquals("RED_STAINED_GLASS_PANE", menus.getString("STATS-MENU.CLOSE-BUTTON.MATERIAL"));
        assertEquals(27, menus.getInt("STATS-MENU.CLOSE-BUTTON.SLOT"));

        Map<String, ExpectedButton> expected = Map.ofEntries(
                Map.entry("MONEY", new ExpectedButton("EMERALD", 10)),
                Map.entry("SHARDS", new ExpectedButton("AMETHYST_SHARD", 11)),
                Map.entry("KILLS", new ExpectedButton("DIAMOND_SWORD", 12)),
                Map.entry("DEATHS", new ExpectedButton("SKELETON_SKULL", 13)),
                Map.entry("PLAYTIME", new ExpectedButton("CLOCK", 14)),
                Map.entry("BLOCKS_PLACED", new ExpectedButton("STONE", 15)),
                Map.entry("BLOCKS_BROKEN", new ExpectedButton("COBBLESTONE", 16)),
                Map.entry("MOBS_KILLED", new ExpectedButton("ZOMBIE_HEAD", 19))
        );

        Map<String, String> expectedDisplayNames = Map.of(
                "MONEY", "&#00FC88Money",
                "SHARDS", "&#00FC88Shards",
                "KILLS", "&#00FC88Kills",
                "DEATHS", "&#00FC88Deaths",
                "PLAYTIME", "&#00FC88Playtime",
                "BLOCKS_PLACED", "&#00FC88Blocks Placed",
                "BLOCKS_BROKEN", "&#00FC88Blocks Broken",
                "MOBS_KILLED", "&#00FC88Mobs Killed"
        );

        ConfigurationSection buttons = menus.getConfigurationSection("STATS-MENU.BUTTONS");
        assertNotNull(buttons);
        assertEquals(8, buttons.getKeys(false).size(), "the public stats chest only shows the eight reference icons");
        for (Map.Entry<String, ExpectedButton> entry : expected.entrySet()) {
            ConfigurationSection button = buttons.getConfigurationSection(entry.getKey());
            assertNotNull(button, entry.getKey());
            assertEquals(entry.getValue().material(), button.getString("MATERIAL"), entry.getKey());
            assertEquals(entry.getValue().slot(), button.getInt("SLOT"), entry.getKey());
            assertEquals(expectedDisplayNames.get(entry.getKey()), button.getString("DISPLAY-NAME"), entry.getKey());
        }

        assertEquals("&cBack", menus.getString("STATS-MENU.CLOSE-BUTTON.DISPLAY-NAME"));
        assertEquals(java.util.List.of("&fClick to return"), menus.getStringList("STATS-MENU.CLOSE-BUTTON.LORE"));
    }

    @Test
    void configuredTitlePreservesPlayerNameCasing() {
        assertEquals("&8BeestoXd Stats", StatsMenu.configuredTitle(null, "BeestoXd"));
        assertEquals("&8Player123 Stats", StatsMenu.configuredTitle(null, "Player123"));
    }

    private record ExpectedButton(String material, int slot) {
    }
}
