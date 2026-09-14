package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.amethyst.AmethystToolAppearance;
import com.bx.ultimateDonutSmp2.amethyst.AmethystToolType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards {@code SHARD-MENU} against drifting away from {@code Design/Shards Shop}.
 */
class ShardShopConfigurationTest {

    private static YamlConfiguration config;
    private static ConfigurationSection shardMenu;

    private static final Map<String, ExpectedItem> ITEMS = new LinkedHashMap<>();

    static {
        ITEMS.put("SHARD-PICKAXE-SILK-ITEM", item("NETHERITE_PICKAXE", 0, 1500, "DRILL"));
        ITEMS.put("SHARD-PICKAXE-FORTUNE-ITEM", item("NETHERITE_PICKAXE", 1, 1500, "DRILL"));
        ITEMS.put("SHARD-AXE-ITEM", item("NETHERITE_AXE", 2, 1500, "CHOPPER"));
        ITEMS.put("SHARD-SELL-AXE-ITEM", item("NETHERITE_AXE", 3, 1500, "SELL_AXE"));
        ITEMS.put("SHARD-SHOVEL-SILK-ITEM", item("NETHERITE_SHOVEL", 4, 1500, "SHOVEL"));
        ITEMS.put("SHARD-SHOVEL-FORTUNE-ITEM", item("NETHERITE_SHOVEL", 5, 1500, "SHOVEL"));
        ITEMS.put("SHARD-HASTE-POTION-ITEM", item("POTION", 6, 250, "HASTE_POTION"));
        ITEMS.put("NETHERITE-HELMET-ITEM", item("NETHERITE_HELMET", 7, 750, null));
        ITEMS.put("NETHERITE-CHESTPLATE-ITEM", item("NETHERITE_CHESTPLATE", 8, 750, null));
        ITEMS.put("NETHERITE-LEGGINGS-ITEM", item("NETHERITE_LEGGINGS", 9, 750, null));
        ITEMS.put("NETHERITE-BOOTS-ITEM", item("NETHERITE_BOOTS", 10, 750, null));
        ITEMS.put("NETHERITE-SWORD-ITEM", item("NETHERITE_SWORD", 11, 750, null));
        ITEMS.put("NETHERITE-PICKAXE-SILK-ITEM", item("NETHERITE_PICKAXE", 12, 500, null));
        ITEMS.put("NETHERITE-PICKAXE-FORTUNE-ITEM", item("NETHERITE_PICKAXE", 13, 500, null));
        ITEMS.put("NETHERITE-SHOVEL-ITEM", item("NETHERITE_SHOVEL", 14, 400, null));
        ITEMS.put("NETHERITE-AXE-ITEM", item("NETHERITE_AXE", 15, 300, null));
        ITEMS.put("MACE-DENSITY-ITEM", item("MACE", 16, 1000, null));
        ITEMS.put("NETHERITE-SPEAR-ITEM", item("NETHERITE_SPEAR", 17, 750, null));
        ITEMS.put("NETHERITE-HOE-ITEM", item("NETHERITE_HOE", 18, 250, null));
        ITEMS.put("CROSSBOW-PIERCING-ITEM", item("CROSSBOW", 19, 250, null));
        ITEMS.put("BOW-ITEM", item("BOW", 20, 250, null));
    }

    @BeforeAll
    static void load() {
        config = loadShopConfig();
        shardMenu = config.getConfigurationSection("SHARD-MENU");
    }

    @Test
    void chestMatchesTheReferenceLayout() {
        assertNotNull(shardMenu, "SHARD-MENU section must exist in shop.yml");
        assertEquals("&8Shard Shop", shardMenu.getString("TITLE"));
        assertEquals(54, shardMenu.getInt("SIZE"));
        assertEquals("SHARD", shardMenu.getString("CURRENCY"));

        assertEquals(49, shardMenu.getInt("BUTTONS.BALANCE.SLOT"));
        assertEquals("AMETHYST_SHARD", shardMenu.getString("BUTTONS.BALANCE.MATERIAL"));
        assertEquals("&fShard Shop", shardMenu.getString("BUTTONS.BALANCE.NAME"));
        assertEquals(List.of(
                "&fYou have &#A303F9{shards} Shards",
                "",
                "&fVisit our website to purchase shards"
        ), shardMenu.getStringList("BUTTONS.BALANCE.LORE"));

        assertEquals(50, shardMenu.getInt("BUTTONS.RANKS.SLOT"));
        assertEquals("PLAYER_HEAD", shardMenu.getString("BUTTONS.RANKS.MATERIAL"));
        assertTrue(shardMenu.getString("BUTTONS.RANKS.HEAD-TEXTURE", "").isBlank(),
                "Ranks icon must stay the default Steve head");
        assertEquals("&fRanks", shardMenu.getString("BUTTONS.RANKS.NAME"));
        assertEquals(List.of("&fClick to view"), shardMenu.getStringList("BUTTONS.RANKS.LORE"));
    }

    @Test
    void everyDesignItemIsInTheConfiguredSlot() {
        assertNotNull(shardMenu);
        Set<Integer> usedSlots = new HashSet<>();
        usedSlots.add(shardMenu.getInt("BUTTONS.BALANCE.SLOT"));
        usedSlots.add(shardMenu.getInt("BUTTONS.RANKS.SLOT"));

        for (Map.Entry<String, ExpectedItem> entry : ITEMS.entrySet()) {
            ConfigurationSection item = shardMenu.getConfigurationSection(entry.getKey());
            assertNotNull(item, "Item " + entry.getKey() + " must be defined in SHARD-MENU");
            assertEquals("SHARD", item.getString("CURRENCY"), entry.getKey() + " currency");
            assertEquals(entry.getValue().material(), item.getString("MATERIAL"), entry.getKey() + " material");
            assertEquals(entry.getValue().slot(), item.getInt("SLOT"), entry.getKey() + " slot");
            assertEquals(entry.getValue().price(), item.getDouble("PRICE-PER-UNIT"), 0.001, entry.getKey() + " price");
            assertTrue(item.getStringList("LORE").stream().anyMatch(line -> line.contains("Shards")),
                    entry.getKey() + " must show a shard price in lore");
            assertTrue(usedSlots.add(item.getInt("SLOT")), entry.getKey() + " slot must be unique");
            assertNotNull(item.getString("DISPLAY-NAME"), entry.getKey() + " must have DISPLAY-NAME");
            assertFalse(item.getStringList("LORE").isEmpty(), entry.getKey() + " must have LORE");
            assertEquals(1, item.getInt("MIN-QUANTITY"));
            assertEquals(1, item.getInt("MAX-QUANTITY"));
            assertEquals(1, item.getInt("DEFAULT-QUANTITY"));
            assertTrue(item.getBoolean("HIDE-QUANTITY-BUTTONS"));
        }

        assertEquals(21, ITEMS.size(), "Design/Shards Shop has 21 purchasable icons");
        assertFalse(usedSlots.contains(45), "Bottom-row arrows are unused in the reference; slot 45 stays empty");
    }

    @Test
    void shardSpecialToolsGiveAmethystTools() {
        assertNotNull(shardMenu);
        for (Map.Entry<String, ExpectedItem> entry : ITEMS.entrySet()) {
            if (entry.getValue().commandType() == null) {
                continue;
            }
            ConfigurationSection item = shardMenu.getConfigurationSection(entry.getKey());
            assertNotNull(item, entry.getKey());
            assertEquals(entry.getValue().commandType(), item.getString("AMETHYST-TOOL"),
                    entry.getKey() + " must give " + entry.getValue().commandType());
            assertEquals(86400L, item.getLong("AMETHYST-DURATION"), entry.getKey() + " duration must be 24h");
            assertTrue(item.getBoolean("GIVE-ITEM", true), entry.getKey() + " delivers the amethyst tool");
            assertTrue(item.getBoolean("KEEP-DISPLAY"), entry.getKey() + " must keep the shop tooltip after purchase");
            String command = item.getString("COMMAND", "");
            assertTrue(command == null || command.isBlank(), entry.getKey() + " must not also run a give command");
            assertTrue(item.getBoolean("HIDE-ENCHANTS"));
        }
    }

    @Test
    void hastePotionMatchesTheReferenceTooltip() {
        ConfigurationSection potion = shardMenu.getConfigurationSection("SHARD-HASTE-POTION-ITEM");
        assertNotNull(potion);
        assertEquals("&#A303F9Shard Potion of Haste", potion.getString("DISPLAY-NAME"));
        assertEquals(List.of("haste:1:9600"), potion.getStringList("POTION-EFFECTS"));
        assertEquals("A303F9", potion.getString("POTION-COLOR"));
        assertTrue(potion.getStringList("LORE").contains("&cSelf Destructs in 24h"));
        assertEquals(
                List.of("&cSelf Destructs in 24h"),
                AmethystToolAppearance.stripPriceLines(potion.getStringList("LORE"))
        );
        assertTrue(potion.getBoolean("GIVE-ITEM", true));
        assertTrue(potion.getBoolean("KEEP-DISPLAY"));
        assertEquals("HASTE_POTION", potion.getString("AMETHYST-TOOL"));
        assertEquals(86400L, potion.getLong("AMETHYST-DURATION"));
        assertEquals(
                List.of("&cSelf Destructs in {time}"),
                AmethystToolAppearance.ownedLoreTemplate(potion.getStringList("LORE"))
        );
    }

    @Test
    void everySelfDestructShardItemExpiresAfterPurchase() {
        assertNotNull(shardMenu);
        int timed = 0;
        for (String key : shardMenu.getKeys(false)) {
            ConfigurationSection item = shardMenu.getConfigurationSection(key);
            if (item == null || !item.contains("MATERIAL")) {
                continue;
            }
            boolean selfDestruct = item.getStringList("LORE").stream()
                    .anyMatch(line -> line != null && line.toLowerCase(java.util.Locale.US).contains("self destruct"));
            if (!selfDestruct) {
                continue;
            }
            timed++;
            String typeName = item.getString("AMETHYST-TOOL");
            assertNotNull(typeName, key + " has Self Destruct lore but no AMETHYST-TOOL expiry");
            assertNotNull(AmethystToolType.fromString(typeName), key + " AMETHYST-TOOL must be a real type");
            assertEquals(86400L, item.getLong("AMETHYST-DURATION"), key + " must last 24h");
            assertTrue(item.getBoolean("GIVE-ITEM", true), key + " must deliver the timed item");
        }
        assertEquals(7, timed, "six timed tools plus the haste potion");
    }

    @Test
    void timedShardToolsMatchTheReferenceLore() {
        assertEquals(List.of(
                "&fBreaks 9 Blocks at Once",
                "&7Silk Touch",
                "&7Efficiency V",
                "&7Unbreaking III",
                "&7Mending",
                "&cSelf Destruct: 24h",
                "&#A303F91.5K Shards"
        ), shardMenu.getStringList("SHARD-PICKAXE-SILK-ITEM.LORE"));
        assertEquals(List.of(
                "&fBreaks Full Tree",
                "&7Efficiency V",
                "&7Unbreaking III",
                "&7Mending",
                "&cSelf Destruct: 24h",
                "&#A303F91.5K Shards"
        ), shardMenu.getStringList("SHARD-AXE-ITEM.LORE"));
        assertEquals(List.of(
                "&fSell Full Chests",
                "&7Efficiency V",
                "&7Unbreaking III",
                "&7Mending",
                "&cSelf Destruct: 24h",
                "&#A303F91.5K Shards"
        ), shardMenu.getStringList("SHARD-SELL-AXE-ITEM.LORE"));
        assertEquals("&#A303F9Shard Pickaxe", shardMenu.getString("SHARD-PICKAXE-SILK-ITEM.DISPLAY-NAME"));
        assertEquals("&#A303F9Shard Sell Axe", shardMenu.getString("SHARD-SELL-AXE-ITEM.DISPLAY-NAME"));
    }

    @Test
    void netheriteGearUsesVanillaNamesAndOnlyAPriceLine() {
        List<String> gear = List.of(
                "NETHERITE-HELMET-ITEM",
                "NETHERITE-CHESTPLATE-ITEM",
                "NETHERITE-LEGGINGS-ITEM",
                "NETHERITE-BOOTS-ITEM",
                "NETHERITE-SWORD-ITEM",
                "NETHERITE-PICKAXE-SILK-ITEM",
                "NETHERITE-PICKAXE-FORTUNE-ITEM",
                "NETHERITE-SHOVEL-ITEM",
                "NETHERITE-AXE-ITEM",
                "MACE-DENSITY-ITEM",
                "NETHERITE-SPEAR-ITEM",
                "NETHERITE-HOE-ITEM",
                "CROSSBOW-PIERCING-ITEM",
                "BOW-ITEM"
        );
        for (String key : gear) {
            ConfigurationSection item = shardMenu.getConfigurationSection(key);
            assertNotNull(item, key);
            assertTrue(item.getBoolean("VANILLA-NAME"), key + " must keep the vanilla item name");
            assertEquals(1, item.getStringList("LORE").size(), key + " lore is only the purple price");
            assertFalse(item.getStringList("ENCHANTMENTS").isEmpty(), key + " must apply real enchantments");
        }

        assertEquals(List.of("PROTECTION:4", "RESPIRATION:3", "AQUA_AFFINITY:1", "UNBREAKING:3", "MENDING:1"),
                shardMenu.getStringList("NETHERITE-HELMET-ITEM.ENCHANTMENTS"));
        assertEquals(List.of("PROTECTION:4", "UNBREAKING:3", "MENDING:1"),
                shardMenu.getStringList("NETHERITE-CHESTPLATE-ITEM.ENCHANTMENTS"));
        assertEquals(List.of("PROTECTION:4", "SWIFT_SNEAK:3", "UNBREAKING:3", "MENDING:1"),
                shardMenu.getStringList("NETHERITE-LEGGINGS-ITEM.ENCHANTMENTS"));
        assertEquals(List.of(
                "PROTECTION:4", "FEATHER_FALLING:4", "SOUL_SPEED:3", "DEPTH_STRIDER:3", "UNBREAKING:3", "MENDING:1"
        ), shardMenu.getStringList("NETHERITE-BOOTS-ITEM.ENCHANTMENTS"));
        assertEquals(List.of(
                "SHARPNESS:5", "SWEEPING_EDGE:3", "FIRE_ASPECT:2", "KNOCKBACK:2", "LOOTING:3", "UNBREAKING:3", "MENDING:1"
        ), shardMenu.getStringList("NETHERITE-SWORD-ITEM.ENCHANTMENTS"));
        assertEquals(List.of("WIND_BURST:3", "DENSITY:5", "FIRE_ASPECT:2", "UNBREAKING:3", "MENDING:1"),
                shardMenu.getStringList("MACE-DENSITY-ITEM.ENCHANTMENTS"));
        assertEquals(List.of(
                "LUNGE:3", "SHARPNESS:5", "FIRE_ASPECT:2", "KNOCKBACK:2", "LOOTING:3", "UNBREAKING:3", "MENDING:1"
        ), shardMenu.getStringList("NETHERITE-SPEAR-ITEM.ENCHANTMENTS"));
        assertEquals(List.of("PIERCING:4", "QUICK_CHARGE:3", "UNBREAKING:3", "MENDING:1"),
                shardMenu.getStringList("CROSSBOW-PIERCING-ITEM.ENCHANTMENTS"));
        assertEquals(List.of("POWER:5", "FLAME:1", "PUNCH:2", "UNBREAKING:3", "MENDING:1"),
                shardMenu.getStringList("BOW-ITEM.ENCHANTMENTS"));
    }

    private static ExpectedItem item(String material, int slot, double price, String commandType) {
        return new ExpectedItem(material, slot, price, commandType);
    }

    private static YamlConfiguration loadShopConfig() {
        File file = new File("src/main/resources/shop.yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    private record ExpectedItem(String material, int slot, double price, String commandType) {
    }
}
