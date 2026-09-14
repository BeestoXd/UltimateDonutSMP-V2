package com.bx.ultimateDonutSmp2.commands;

import com.bx.ultimateDonutSmp2.amethyst.AmethystToolType;
import com.bx.ultimateDonutSmp2.amethyst.AmethystToolsManager;
import com.bx.ultimateDonutSmp2.amethyst.AmethystToolsManager.ShardToolVariant;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmethystToolCommandGiveTest {

    @Test
    void modernShardVariantsResolveCorrectly() {
        assertEquals(ShardToolVariant.PICKAXE_SILK, AmethystToolsManager.resolveVariant("pickaxe-silk"));
        assertEquals(ShardToolVariant.PICKAXE_FORTUNE, AmethystToolsManager.resolveVariant("pickaxe-fortune"));
        assertEquals(ShardToolVariant.AXE, AmethystToolsManager.resolveVariant("axe"));
        assertEquals(ShardToolVariant.SELL_AXE, AmethystToolsManager.resolveVariant("sell-axe"));
        assertEquals(ShardToolVariant.SHOVEL_SILK, AmethystToolsManager.resolveVariant("shovel-silk"));
        assertEquals(ShardToolVariant.SHOVEL_FORTUNE, AmethystToolsManager.resolveVariant("shovel-fortune"));
        assertEquals(ShardToolVariant.HASTE_POTION, AmethystToolsManager.resolveVariant("haste-potion"));
    }

    @Test
    void legacyAliasesResolveToAppropriateVariants() {
        assertEquals(ShardToolVariant.PICKAXE_FORTUNE, AmethystToolsManager.resolveVariant("drill"));
        assertEquals(ShardToolVariant.AXE, AmethystToolsManager.resolveVariant("chopper"));
        assertEquals(ShardToolVariant.SHOVEL_FORTUNE, AmethystToolsManager.resolveVariant("shovel"));
        assertEquals(ShardToolVariant.BUCKET, AmethystToolsManager.resolveVariant("bucket"));
        assertEquals(ShardToolVariant.SHARD_BOOSTER, AmethystToolsManager.resolveVariant("shard-booster"));

        // Additional aliases
        assertEquals(ShardToolVariant.PICKAXE_SILK, AmethystToolsManager.resolveVariant("drill-silk"));
        assertEquals(ShardToolVariant.PICKAXE_FORTUNE, AmethystToolsManager.resolveVariant("drill-fortune"));
        assertEquals(ShardToolVariant.AXE, AmethystToolsManager.resolveVariant("tree-chopper"));
        assertEquals(ShardToolVariant.SELL_AXE, AmethystToolsManager.resolveVariant("sellaxe"));
        assertEquals(ShardToolVariant.HASTE_POTION, AmethystToolsManager.resolveVariant("potion"));
    }

    @Test
    void invalidInputReturnsNull() {
        assertNull(AmethystToolsManager.resolveVariant(null));
        assertNull(AmethystToolsManager.resolveVariant(""));
        assertNull(AmethystToolsManager.resolveVariant("   "));
        assertNull(AmethystToolsManager.resolveVariant("unknown-tool"));
    }

    @Test
    void variantResolutionWorksUnderTurkishLocale() {
        Locale previous = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        try {
            assertEquals(ShardToolVariant.PICKAXE_SILK, AmethystToolsManager.resolveVariant("PICKAXE-SILK"));
            assertEquals(ShardToolVariant.PICKAXE_FORTUNE, AmethystToolsManager.resolveVariant("PICKAXE-FORTUNE"));
            assertEquals(ShardToolVariant.PICKAXE_FORTUNE, AmethystToolsManager.resolveVariant("DRILL"));
            assertEquals(ShardToolVariant.AXE, AmethystToolsManager.resolveVariant("AXE"));
            assertEquals(ShardToolVariant.SELL_AXE, AmethystToolsManager.resolveVariant("SELL-AXE"));
            assertEquals(ShardToolVariant.SHOVEL_SILK, AmethystToolsManager.resolveVariant("SHOVEL-SILK"));
            assertEquals(ShardToolVariant.SHOVEL_FORTUNE, AmethystToolsManager.resolveVariant("SHOVEL-FORTUNE"));
            assertEquals(ShardToolVariant.HASTE_POTION, AmethystToolsManager.resolveVariant("HASTE-POTION"));
            assertEquals(ShardToolVariant.BUCKET, AmethystToolsManager.resolveVariant("BUCKET"));
            assertEquals(ShardToolVariant.SHARD_BOOSTER, AmethystToolsManager.resolveVariant("SHARD-BOOSTER"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    void tabCompletionsContainModernAndLegacyTypes() throws Exception {
        Field completionsField = AmethystToolCommand.class.getDeclaredField("TYPE_COMPLETIONS");
        completionsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> completions = (List<String>) completionsField.get(null);

        assertTrue(completions.contains("pickaxe-silk"));
        assertTrue(completions.contains("pickaxe-fortune"));
        assertTrue(completions.contains("axe"));
        assertTrue(completions.contains("sell-axe"));
        assertTrue(completions.contains("shovel-silk"));
        assertTrue(completions.contains("shovel-fortune"));
        assertTrue(completions.contains("haste-potion"));
        assertTrue(completions.contains("drill"));
        assertTrue(completions.contains("chopper"));
        assertTrue(completions.contains("shovel"));
        assertTrue(completions.contains("bucket"));
        assertTrue(completions.contains("shard-booster"));

        // Ensure each tab completion resolves to a valid variant
        for (String completion : completions) {
            assertNotNull(AmethystToolsManager.resolveVariant(completion),
                    "Tab completion '" + completion + "' must resolve to a valid ShardToolVariant");
        }
    }

    @Test
    void variantDefaultsMatchModernShardEquipment() {
        // Pickaxes
        assertEquals(Material.NETHERITE_PICKAXE, ShardToolVariant.PICKAXE_SILK.getDefaultMaterial());
        assertTrue(ShardToolVariant.PICKAXE_SILK.getDefaultEnchantments().contains("SILK_TOUCH:1"));
        assertEquals(Material.NETHERITE_PICKAXE, ShardToolVariant.PICKAXE_FORTUNE.getDefaultMaterial());
        assertTrue(ShardToolVariant.PICKAXE_FORTUNE.getDefaultEnchantments().contains("FORTUNE:3"));

        // Axes
        assertEquals(Material.NETHERITE_AXE, ShardToolVariant.AXE.getDefaultMaterial());
        assertEquals(Material.NETHERITE_AXE, ShardToolVariant.SELL_AXE.getDefaultMaterial());

        // Shovels
        assertEquals(Material.NETHERITE_SHOVEL, ShardToolVariant.SHOVEL_SILK.getDefaultMaterial());
        assertTrue(ShardToolVariant.SHOVEL_SILK.getDefaultEnchantments().contains("SILK_TOUCH:1"));
        assertEquals(Material.NETHERITE_SHOVEL, ShardToolVariant.SHOVEL_FORTUNE.getDefaultMaterial());
        assertTrue(ShardToolVariant.SHOVEL_FORTUNE.getDefaultEnchantments().contains("FORTUNE:3"));

        // Haste Potion
        assertEquals(Material.POTION, ShardToolVariant.HASTE_POTION.getDefaultMaterial());
        assertTrue(ShardToolVariant.HASTE_POTION.getDefaultPotionEffects().contains("haste:1:9600"));
        assertEquals("A303F9", ShardToolVariant.HASTE_POTION.getDefaultPotionColor());

        // All timed variants have 86400 duration (24h)
        assertEquals(86400L, ShardToolVariant.PICKAXE_SILK.getDefaultDuration());
        assertEquals(86400L, ShardToolVariant.PICKAXE_FORTUNE.getDefaultDuration());
        assertEquals(86400L, ShardToolVariant.AXE.getDefaultDuration());
        assertEquals(86400L, ShardToolVariant.SELL_AXE.getDefaultDuration());
        assertEquals(86400L, ShardToolVariant.SHOVEL_SILK.getDefaultDuration());
        assertEquals(86400L, ShardToolVariant.SHOVEL_FORTUNE.getDefaultDuration());
        assertEquals(86400L, ShardToolVariant.HASTE_POTION.getDefaultDuration());
    }

    @Test
    void shopKeysMatchShopYmlConfiguration() {
        YamlConfiguration shop = YamlConfiguration.loadConfiguration(new File("src/main/resources/shop.yml"));

        for (ShardToolVariant variant : ShardToolVariant.values()) {
            if (variant.getShopKey() != null) {
                String path = "SHARD-MENU." + variant.getShopKey();
                assertTrue(shop.isConfigurationSection(path),
                        "Expected shop section for " + variant + " at " + path);
            }
        }
    }
}
