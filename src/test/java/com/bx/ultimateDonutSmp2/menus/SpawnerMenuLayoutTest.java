package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpawnerMenuLayoutTest {

    @Test
    void spawnerStorageMenuMatchesDesignLayout() throws Exception {
        YamlConfiguration menus = load("menus.yml");

        assertEquals("{type} Spawners", menus.getString("SPAWNER-MENUS.STORAGE-MENU.TITLE"));
        assertEquals(54, menus.getInt("SPAWNER-MENUS.STORAGE-MENU.SIZE"));
        assertEquals(45, menus.getInt("SPAWNER-MENUS.STORAGE-MENU.ITEMS-PER-PAGE"));

        assertEquals("&f{material}", menus.getString("SPAWNER-MENUS.STORAGE-MENU.ITEM-META.TITLE"));
        assertEquals(List.of("&a$ &f{price}"), menus.getStringList("SPAWNER-MENUS.STORAGE-MENU.ITEM-META.LORE"));

        // Slot 48: Gold Ingot - Sell All
        assertEquals(48, menus.getInt("SPAWNER-MENUS.STORAGE-MENU.SELL-ALL-BUTTON.SLOT"));
        assertEquals("GOLD_INGOT", menus.getString("SPAWNER-MENUS.STORAGE-MENU.SELL-ALL-BUTTON.MATERIAL"));
        assertEquals("&fSell All", menus.getString("SPAWNER-MENUS.STORAGE-MENU.SELL-ALL-BUTTON.TITLE"));
        assertEquals(List.of("&7&oClick to sell all loot"), menus.getStringList("SPAWNER-MENUS.STORAGE-MENU.SELL-ALL-BUTTON.LORE"));

        // Slot 49: Mob Head
        assertEquals(49, menus.getInt("SPAWNER-MENUS.STORAGE-MENU.MOB-HEAD-BUTTON.SLOT"));
        assertEquals("&a{amount} {type} Spawner", menus.getString("SPAWNER-MENUS.STORAGE-MENU.MOB-HEAD-BUTTON.TITLE"));

        // Slot 50: Dropper - Drop Loot
        assertEquals(50, menus.getInt("SPAWNER-MENUS.STORAGE-MENU.DROP-LOOT-BUTTON.SLOT"));
        assertEquals("DROPPER", menus.getString("SPAWNER-MENUS.STORAGE-MENU.DROP-LOOT-BUTTON.MATERIAL"));
        assertEquals("&fDrop Loot", menus.getString("SPAWNER-MENUS.STORAGE-MENU.DROP-LOOT-BUTTON.TITLE"));
        assertEquals(List.of("&7&oClick to drop all loot on the page"), menus.getStringList("SPAWNER-MENUS.STORAGE-MENU.DROP-LOOT-BUTTON.LORE"));

        // Slot 53: Next Page Arrow
        assertEquals(53, menus.getInt("SPAWNER-MENUS.STORAGE-MENU.NEXT-PAGE-BUTTON.SLOT"));
        assertEquals("ARROW", menus.getString("SPAWNER-MENUS.STORAGE-MENU.NEXT-PAGE-BUTTON.MATERIAL"));
        assertEquals("&fNext page", menus.getString("SPAWNER-MENUS.STORAGE-MENU.NEXT-PAGE-BUTTON.TITLE"));
        assertEquals(List.of("&7&oClick to view next page"), menus.getStringList("SPAWNER-MENUS.STORAGE-MENU.NEXT-PAGE-BUTTON.LORE"));

        // Slot 45: Previous Page Arrow
        assertEquals(45, menus.getInt("SPAWNER-MENUS.STORAGE-MENU.PREVIOUS-PAGE-BUTTON.SLOT"));
        assertEquals("ARROW", menus.getString("SPAWNER-MENUS.STORAGE-MENU.PREVIOUS-PAGE-BUTTON.MATERIAL"));
        assertEquals("&fPrevious page", menus.getString("SPAWNER-MENUS.STORAGE-MENU.PREVIOUS-PAGE-BUTTON.TITLE"));
        assertEquals(List.of("&7&oClick to view previous page"), menus.getStringList("SPAWNER-MENUS.STORAGE-MENU.PREVIOUS-PAGE-BUTTON.LORE"));
    }

    @Test
    void spawnerSellConfirmMenuMatchesDesignLayout() throws Exception {
        YamlConfiguration menus = load("menus.yml");

        assertEquals("Confirm Sell", menus.getString("SPAWNER-MENUS.SELL-CONFIRM-MENU.TITLE"));
        assertEquals(27, menus.getInt("SPAWNER-MENUS.SELL-CONFIRM-MENU.SIZE"));

        // Slot 11: Cancel
        assertEquals(11, menus.getInt("SPAWNER-MENUS.SELL-CONFIRM-MENU.CANCEL-BUTTON.SLOT"));
        assertEquals("RED_STAINED_GLASS_PANE", menus.getString("SPAWNER-MENUS.SELL-CONFIRM-MENU.CANCEL-BUTTON.MATERIAL"));
        assertEquals("&cCancel", menus.getString("SPAWNER-MENUS.SELL-CONFIRM-MENU.CANCEL-BUTTON.TITLE"));
        assertEquals(List.of("&7Click to cancel"), menus.getStringList("SPAWNER-MENUS.SELL-CONFIRM-MENU.CANCEL-BUTTON.LORE"));

        // Slot 13: Info icon
        assertEquals(13, menus.getInt("SPAWNER-MENUS.SELL-CONFIRM-MENU.INFO-ICON.SLOT"));

        // Slot 15: Confirm
        assertEquals(15, menus.getInt("SPAWNER-MENUS.SELL-CONFIRM-MENU.CONFIRM-BUTTON.SLOT"));
        assertEquals("LIME_STAINED_GLASS_PANE", menus.getString("SPAWNER-MENUS.SELL-CONFIRM-MENU.CONFIRM-BUTTON.MATERIAL"));
        assertEquals("&a$ &f{price}", menus.getString("SPAWNER-MENUS.SELL-CONFIRM-MENU.CONFIRM-BUTTON.TITLE"));
        assertEquals(List.of("&7Click to sell items"), menus.getStringList("SPAWNER-MENUS.SELL-CONFIRM-MENU.CONFIRM-BUTTON.LORE"));
    }

    @Test
    void allMonstersAndMobsUseGenuineMobHeadsAndNeverPlayerHeadByDefault() {
        // Vanilla Head Mobs have exact Material
        assertEquals(org.bukkit.Material.SKELETON_SKULL, com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("SKELETON"));
        assertEquals(org.bukkit.Material.ZOMBIE_HEAD, com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("ZOMBIE"));
        assertEquals(org.bukkit.Material.CREEPER_HEAD, com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("CREEPER"));
        assertEquals(org.bukkit.Material.PIGLIN_HEAD, com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("PIGLIN"));
        assertEquals(org.bukkit.Material.PIGLIN_HEAD, com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("ZOMBIFIED_PIGLIN"));
        assertEquals(org.bukkit.Material.WITHER_SKELETON_SKULL, com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("WITHER_SKELETON"));
        assertEquals(org.bukkit.Material.DRAGON_HEAD, com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("ENDER_DRAGON"));
        assertEquals(org.bukkit.Material.DRAGON_HEAD, com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("DRAGON"));

        // Mobs without vanilla head return null for exact vanilla head
        org.junit.jupiter.api.Assertions.assertNull(com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("PIG"));
        org.junit.jupiter.api.Assertions.assertNull(com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("COW"));
        org.junit.jupiter.api.Assertions.assertNull(com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("SPIDER"));
        org.junit.jupiter.api.Assertions.assertNull(com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("BLAZE"));
        org.junit.jupiter.api.Assertions.assertNull(com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveExactVanillaMobHead("IRON_GOLEM"));

        // resolveMobHeadMaterial never returns PLAYER_HEAD for any mob
        List<String> mobTypes = List.of(
                "SKELETON", "ZOMBIE", "CREEPER", "PIGLIN", "WITHER_SKELETON", "BLAZE",
                "IRON_GOLEM", "SPIDER", "COW", "PIG", "ENDERMAN", "GHAST", "SLIME",
                "MAGMA_CUBE", "SHEEP", "CHICKEN", "UNKNOWN_MOB", ""
        );
        for (String mob : mobTypes) {
            org.bukkit.Material headMat = com.bx.ultimateDonutSmp2.utils.ItemUtils.resolveMobHeadMaterial(mob);
            org.junit.jupiter.api.Assertions.assertNotEquals(
                    org.bukkit.Material.PLAYER_HEAD,
                    headMat,
                    "Mob " + mob + " must not use PLAYER_HEAD"
            );
        }
    }

    @Test
    void spawnersConfigDefinesCustomHeadTexturesForNonVanillaMobs() throws Exception {
        YamlConfiguration spawners = load("spawners.yml");
        String pigTexture = spawners.getString("TYPES.PIG.HEAD_TEXTURE");
        org.junit.jupiter.api.Assertions.assertNotNull(pigTexture);
        org.junit.jupiter.api.Assertions.assertTrue(pigTexture.startsWith("https://textures.minecraft.net/texture/"));

        String spiderTexture = spawners.getString("TYPES.SPIDER.HEAD_TEXTURE");
        org.junit.jupiter.api.Assertions.assertNotNull(spiderTexture);
        org.junit.jupiter.api.Assertions.assertTrue(spiderTexture.startsWith("https://textures.minecraft.net/texture/"));

        String blazeTexture = spawners.getString("TYPES.BLAZE.HEAD_TEXTURE");
        org.junit.jupiter.api.Assertions.assertNotNull(blazeTexture);
        org.junit.jupiter.api.Assertions.assertTrue(blazeTexture.startsWith("https://textures.minecraft.net/texture/"));

        String golemTexture = spawners.getString("TYPES.IRON_GOLEM.HEAD_TEXTURE");
        org.junit.jupiter.api.Assertions.assertNotNull(golemTexture);
        org.junit.jupiter.api.Assertions.assertTrue(golemTexture.startsWith("https://textures.minecraft.net/texture/"));
    }

    @Test
    void paginationLogicDeterminesNextAndPreviousPagesCorrectly() {
        com.bx.ultimateDonutSmp2.models.SpawnerInstance instance = new com.bx.ultimateDonutSmp2.models.SpawnerInstance(
                1L, "world", 0, 64, 0, java.util.UUID.randomUUID(), "Player", "IRON_GOLEM", 1L,
                com.bx.ultimateDonutSmp2.models.SpawnerInstance.AccessMode.OWNER_ONLY,
                System.currentTimeMillis(), System.currentTimeMillis(), System.currentTimeMillis()
        );

        // Initially empty: 0 items, page 1 -> no next page, no previous page
        org.junit.jupiter.api.Assertions.assertFalse(com.bx.ultimateDonutSmp2.menus.SpawnerStorageMenu.hasNextPage(instance, 1, 45, 45));
        org.junit.jupiter.api.Assertions.assertFalse(com.bx.ultimateDonutSmp2.menus.SpawnerStorageMenu.hasPreviousPage(1));

        // Add 2 items (like in user screenshot: 8 iron ingots, 1 poppy) -> still no next page
        instance.setSlotLoot(0, org.bukkit.Material.IRON_INGOT, 8);
        instance.setSlotLoot(1, org.bukkit.Material.POPPY, 1);
        org.junit.jupiter.api.Assertions.assertFalse(com.bx.ultimateDonutSmp2.menus.SpawnerStorageMenu.hasNextPage(instance, 1, 45, 45));

        // Fill all 45 slots of page 1 (slots 0..44) -> page 1 is full! hasNextPage must be TRUE
        for (int i = 0; i < 45; i++) {
            instance.setSlotLoot(i, org.bukkit.Material.IRON_INGOT, 64);
        }
        org.junit.jupiter.api.Assertions.assertTrue(com.bx.ultimateDonutSmp2.menus.SpawnerStorageMenu.hasNextPage(instance, 1, 45, 45));

        // On page 2 -> hasPreviousPage must be TRUE
        org.junit.jupiter.api.Assertions.assertTrue(com.bx.ultimateDonutSmp2.menus.SpawnerStorageMenu.hasPreviousPage(2));

        // If an item is removed on page 1 (creating a gap, only 44 items occupied),
        // next page arrow MUST NOT appear because the menu is no longer filled!
        instance.setSlotLoot(45, org.bukkit.Material.IRON_INGOT, 10);
        instance.removeSlotLoot(10); // create a gap on page 1 (44 slots occupied)
        org.junit.jupiter.api.Assertions.assertFalse(
                com.bx.ultimateDonutSmp2.menus.SpawnerStorageMenu.hasNextPage(instance, 1, 45, 45),
                "Next page arrow must NOT appear when inventory menu has empty slots"
        );

        // Once slot 10 is refilled (all 45 slots occupied), next page arrow appears again
        instance.setSlotLoot(10, org.bukkit.Material.IRON_INGOT, 64);
        org.junit.jupiter.api.Assertions.assertTrue(
                com.bx.ultimateDonutSmp2.menus.SpawnerStorageMenu.hasNextPage(instance, 1, 45, 45),
                "Next page arrow MUST appear when inventory menu is completely filled"
        );

        // On page 2, only slot 45 has loot (1 slot occupied out of 45) -> next page arrow must NOT appear
        org.junit.jupiter.api.Assertions.assertFalse(
                com.bx.ultimateDonutSmp2.menus.SpawnerStorageMenu.hasNextPage(instance, 2, 45, 45),
                "Next page arrow must NOT appear on page 2 when page 2 has not filled its inventory menu"
        );
    }

    @Test
    void storageTitleReplacesTypePlaceholder() {
        String template = "{type} Spawners";
        String cleanMob = "Iron Golem";
        String formatted = template.replace("{type}", cleanMob);
        assertEquals("Iron Golem Spawners", formatted);
    }

    @Test
    void mobHeadTitleFormatsTitleCaseAndNotFullUppercase() throws Exception {
        YamlConfiguration menus = load("menus.yml");
        String template = menus.getString("SPAWNER-MENUS.STORAGE-MENU.MOB-HEAD-BUTTON.TITLE");
        assertEquals("&a{amount} {type} Spawner", template);

        // 1 Iron Golem Spawner (true), NOT 1 IRON GOLEM Spawner (false)
        String title1 = SpawnerStorageMenu.formatSpawnerMobHeadTitle(template, 1L, "Iron Golem");
        assertEquals("&a1 Iron Golem Spawner", title1);

        // Multiple: 2 Iron Golem Spawners
        String title2 = SpawnerStorageMenu.formatSpawnerMobHeadTitle(template, 2L, "Iron Golem");
        assertEquals("&a2 Iron Golem Spawners", title2);

        // Pig Spawner
        String titlePig = SpawnerStorageMenu.formatSpawnerMobHeadTitle(template, 1L, "Pig");
        assertEquals("&a1 Pig Spawner", titlePig);

        // Also works with legacy {stack} {mob} Spawners template
        String legacy = "&a{stack} {mob} Spawners";
        assertEquals("&a1 Iron Golem Spawner", SpawnerStorageMenu.formatSpawnerMobHeadTitle(legacy, 1L, "Iron Golem"));
        assertEquals("&a5 Iron Golem Spawners", SpawnerStorageMenu.formatSpawnerMobHeadTitle(legacy, 5L, "Iron Golem"));
    }

    private static YamlConfiguration load(String name) throws Exception {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.options().parseComments(true);
        configuration.load(Path.of("src/main/resources", name).toFile());
        return configuration;
    }
}
