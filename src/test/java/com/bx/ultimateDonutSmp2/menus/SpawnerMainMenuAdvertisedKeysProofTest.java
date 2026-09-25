package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnerMainMenuAdvertisedKeysProofTest {

    private static final Path MENUS_YAML = Path.of("src/main/resources/menus.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-menus.yml.md");
    private static final Path MANAGER = Path.of("src/main/java/com/bx/ultimateDonutSmp2/managers/SpawnerManager.java");
    private static final Path MAIN_MENU = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/SpawnerMainMenu.java");

    @Test
    void bundledYamlShipsTheAdvertisedKeys() throws Exception {
        YamlConfiguration menus = new YamlConfiguration();
        menus.load(MENUS_YAML.toFile());
        assertEquals("{type} Spawners", menus.getString("SPAWNER-MENUS.MAIN-MENU.TITLE"));
        assertEquals(54, menus.getInt("SPAWNER-MENUS.MAIN-MENU.SIZE"));
        assertEquals("AIR", menus.getString("SPAWNER-MENUS.MAIN-MENU.FILLER-MATERIAL"));
    }

    @Test
    void wikiTablesThoseKeysAsLiveMenuLayout() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(wiki.contains("`MAIN-MENU`"), "Config-menus.yml.md lists MAIN-MENU");
        assertTrue(wiki.contains("`SPAWNER-MENUS`"), "Config-menus.yml.md lists SPAWNER-MENUS");
    }

    @Test
    void mainMenuTitleAndSizeMustHaveCallers() throws Exception {
        String managerSource = Files.readString(MANAGER, StandardCharsets.UTF_8);
        assertTrue(managerSource.contains("SPAWNER-MENUS.MAIN-MENU.FILLER-MATERIAL"),
                "SpawnerManager must read SPAWNER-MENUS.MAIN-MENU.FILLER-MATERIAL");
        assertTrue(managerSource.contains("getMainMenuFillerMaterial"),
                "SpawnerManager must have getMainMenuFillerMaterial getter");

        String menuSource = Files.readString(MAIN_MENU, StandardCharsets.UTF_8);
        assertTrue(menuSource.contains("getMainMenuTitle"),
                "Config-menus.yml.md tables SPAWNER-MENUS.MAIN-MENU.TITLE as a live chest title");
        assertTrue(menuSource.contains("getMainMenuSize"),
                "Config-menus.yml.md tables SPAWNER-MENUS.MAIN-MENU.SIZE as a live chest size");
        assertTrue(menuSource.contains("getMainMenuFillerMaterial"),
                "SpawnerMainMenu must use getMainMenuFillerMaterial to configure filler");
    }
}
