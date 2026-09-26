package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorthMenuAdvertisedKeysProofTest {

    private static final Path MENUS_YAML = Path.of("src/main/resources/menus.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-menus.yml.md");
    private static final Path MENU_SOURCE = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/WorthMenu.java");
    private static final Path MANAGER_SOURCE = Path.of("src/main/java/com/bx/ultimateDonutSmp2/managers/WorthManager.java");

    @Test
    void bundledYamlShipsTheAdvertisedKeys() throws Exception {
        YamlConfiguration menus = new YamlConfiguration();
        menus.load(MENUS_YAML.toFile());

        assertEquals("&8Item Prices", menus.getString("WORTH-MENU.TITLE"));
        assertEquals("&7Worth: &a${price}", menus.getString("WORTH-MENU.FORMAT"));
        assertEquals("&aSort", menus.getString("WORTH-MENU.SORT-BUTTON.TITLE"));
        assertEquals("CAULDRON", menus.getString("WORTH-MENU.SORT-BUTTON.MATERIAL"));
    }

    @Test
    void worthMenuSectionMustHaveJavaReaders() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(
                wiki.contains("`WORTH-MENU.TITLE`")
                        && wiki.contains("`WORTH-MENU.FORMAT`")
                        && wiki.contains("`WORTH-MENU.SORT-BUTTON.TITLE`")
                        && wiki.contains("`WORTH-MENU.SORT-BUTTON.MATERIAL`"),
                "Config-menus.yml.md tables WORTH-MENU keys as live layout"
        );

        String menuSource = Files.readString(MENU_SOURCE, StandardCharsets.UTF_8);
        String managerSource = Files.readString(MANAGER_SOURCE, StandardCharsets.UTF_8);
        String combinedSource = menuSource + "\n" + managerSource;

        assertTrue(
                combinedSource.contains("WORTH-MENU.TITLE"),
                "Config-menus.yml.md tables WORTH-MENU.TITLE as a live chest title"
        );
        assertTrue(
                combinedSource.contains("WORTH-MENU.FORMAT"),
                "Config-menus.yml.md tables WORTH-MENU.FORMAT as live format"
        );
        assertTrue(
                combinedSource.contains("WORTH-MENU.SORT-BUTTON.TITLE"),
                "Config-menus.yml.md tables WORTH-MENU.SORT-BUTTON.TITLE as live sort title"
        );
        assertTrue(
                combinedSource.contains("WORTH-MENU.SORT-BUTTON.MATERIAL"),
                "Config-menus.yml.md tables WORTH-MENU.SORT-BUTTON.MATERIAL as live sort material"
        );
    }
}
