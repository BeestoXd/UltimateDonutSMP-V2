package com.bx.ultimateDonutSmp2.amethyst;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmethystToolAppearanceTest {

    @Test
    void purchasedPickaxeKeepsShopLoreMinusPrice() throws Exception {
        YamlConfiguration shop = new YamlConfiguration();
        shop.load(Path.of("src/main/resources/shop.yml").toFile());

        List<String> shopLore = shop.getStringList("SHARD-MENU.SHARD-PICKAXE-SILK-ITEM.LORE");
        AmethystToolAppearance appearance = AmethystToolAppearance.fromShop(
                shop.getString("SHARD-MENU.SHARD-PICKAXE-SILK-ITEM.DISPLAY-NAME"),
                shopLore,
                shop.getStringList("SHARD-MENU.SHARD-PICKAXE-SILK-ITEM.ENCHANTMENTS")
        );

        assertEquals("&#A303F9Shard Pickaxe", appearance.name());
        assertEquals(List.of(
                "&fBreaks 9 Blocks at Once",
                "&7Silk Touch",
                "&7Efficiency V",
                "&7Unbreaking III",
                "&7Mending",
                "&cSelf Destruct: {time}"
        ), appearance.loreTemplate());
        assertFalse(appearance.loreTemplate().stream().anyMatch(line -> line.contains("Shards")));
        assertFalse(appearance.loreTemplate().stream().anyMatch(line -> line.contains("Amethyst Drill")));
        assertEquals(List.of("EFFICIENCY:5", "SILK_TOUCH:1", "UNBREAKING:3", "MENDING:1"), appearance.enchantments());

        List<String> owned = AmethystToolAppearance.resolveLore(appearance.loreTemplate(), 86399L);
        assertEquals(shopLore.subList(0, 5), owned.subList(0, 5));
        assertEquals("&cSelf Destruct: 23h 59m 59s", owned.get(5));
        assertFalse(owned.stream().anyMatch(line -> line.contains("Shards")));
    }

    @Test
    void otherTimedShardToolsKeepTheirShopIdentity() throws Exception {
        YamlConfiguration shop = new YamlConfiguration();
        shop.load(Path.of("src/main/resources/shop.yml").toFile());

        assertEquals(
                List.of(
                        "&fBreaks Full Tree",
                        "&7Efficiency V",
                        "&7Unbreaking III",
                        "&7Mending",
                        "&cSelf Destruct: {time}"
                ),
                AmethystToolAppearance.ownedLoreTemplate(shop.getStringList("SHARD-MENU.SHARD-AXE-ITEM.LORE"))
        );
        assertEquals(
                List.of(
                        "&fSell Full Chests",
                        "&7Efficiency V",
                        "&7Unbreaking III",
                        "&7Mending",
                        "&cSelf Destruct: {time}"
                ),
                AmethystToolAppearance.ownedLoreTemplate(shop.getStringList("SHARD-MENU.SHARD-SELL-AXE-ITEM.LORE"))
        );
        assertEquals(
                List.of(
                        "&fBreaks 9 Blocks at Once",
                        "&7Fortune III",
                        "&7Efficiency V",
                        "&7Unbreaking III",
                        "&7Mending",
                        "&cSelf Destruct: {time}"
                ),
                AmethystToolAppearance.ownedLoreTemplate(
                        shop.getStringList("SHARD-MENU.SHARD-PICKAXE-FORTUNE-ITEM.LORE")
                )
        );
    }

    @Test
    void liveCountdownStaysOnTheSelfDestructLine() {
        List<String> resolved = AmethystToolAppearance.resolveLore(
                List.of("&fBreaks 9 Blocks at Once", "&cSelf Destruct: {time}"),
                86399L
        );
        assertEquals("&fBreaks 9 Blocks at Once", resolved.get(0));
        assertTrue(resolved.get(1).startsWith("&cSelf Destruct: "));
        assertFalse(resolved.get(1).contains("per strike"));
        assertFalse(resolved.get(1).contains("Powered by amethyst"));
    }

    @Test
    void loreTemplateRoundTripsThroughPersistentStorage() {
        List<String> template = List.of("&fBreaks 9 Blocks at Once", "", "&cSelf Destruct: {time}");
        assertEquals(template, AmethystToolAppearance.decodeTemplate(AmethystToolAppearance.encodeTemplate(template)));
    }

    @Test
    void purchasedHastePotionDropsTheShopPrice() throws Exception {
        YamlConfiguration shop = new YamlConfiguration();
        shop.load(Path.of("src/main/resources/shop.yml").toFile());
        List<String> owned = AmethystToolAppearance.stripPriceLines(
                shop.getStringList("SHARD-MENU.SHARD-HASTE-POTION-ITEM.LORE")
        );
        assertEquals(List.of("&cSelf Destructs in 24h"), owned);
        assertEquals(List.of("&cSelf Destructs in {time}"), AmethystToolAppearance.ownedLoreTemplate(
                shop.getStringList("SHARD-MENU.SHARD-HASTE-POTION-ITEM.LORE")
        ));
        assertFalse(owned.stream().anyMatch(line -> line.contains("Shards")));
        assertFalse(owned.stream().anyMatch(line -> line.contains("250")));
    }
}
