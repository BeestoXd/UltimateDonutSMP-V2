package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TpaConfirmAdvertisedButtonsProofTest {

    private static final Path MENUS_YAML = Path.of("src/main/resources/menus.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-menus.yml.md");
    private static final Path MENU_SOURCE = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/TpaConfirmMenu.java");

    @Test
    void bundledYamlShipsTheAdvertisedButtons() throws Exception {
        YamlConfiguration menus = new YamlConfiguration();
        menus.load(MENUS_YAML.toFile());

        assertEquals("&cCancel", menus.getString("TPA-CONFIRM-MENU.BUTTONS.CANCEL.NAME"));
        assertEquals("RED_STAINED_GLASS_PANE", menus.getString("TPA-CONFIRM-MENU.BUTTONS.CANCEL.MATERIAL"));
        assertEquals("&aConfirm", menus.getString("TPA-CONFIRM-MENU.BUTTONS.CONFIRM.NAME"));
        assertEquals("LIME_STAINED_GLASS_PANE", menus.getString("TPA-CONFIRM-MENU.BUTTONS.CONFIRM.MATERIAL"));
        assertEquals("&#00FC00Player", menus.getString("TPA-CONFIRM-MENU.BUTTONS.PLAYER.NAME"));
        assertEquals("PLAYER_HEAD", menus.getString("TPA-CONFIRM-MENU.BUTTONS.PLAYER.MATERIAL"));
        assertEquals("&#6BF18DLocation", menus.getString("TPA-CONFIRM-MENU.BUTTONS.LOCATION.NAME"));
        assertEquals("GRASS_BLOCK", menus.getString("TPA-CONFIRM-MENU.BUTTONS.LOCATION.MATERIAL"));
        assertEquals("&#6BF18DRegion", menus.getString("TPA-CONFIRM-MENU.BUTTONS.REGION.NAME"));
        assertEquals("FEATHER", menus.getString("TPA-CONFIRM-MENU.BUTTONS.REGION.MATERIAL"));
    }

    @Test
    void tpaConfirmButtonsMustHaveJavaReaders() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(
                wiki.contains("TPA-CONFIRM-MENU.BUTTONS"),
                "Config-menus.yml.md tables TPA-CONFIRM-MENU.BUTTONS as live layout"
        );

        String source = Files.readString(MENU_SOURCE, StandardCharsets.UTF_8);
        assertTrue(
                source.contains("TPA-CONFIRM-MENU.BUTTONS.CANCEL"),
                "TpaConfirmMenu must read TPA-CONFIRM-MENU.BUTTONS.CANCEL"
        );
        assertTrue(
                source.contains("TPA-CONFIRM-MENU.BUTTONS.CONFIRM"),
                "TpaConfirmMenu must read TPA-CONFIRM-MENU.BUTTONS.CONFIRM"
        );
        assertTrue(
                source.contains("TPA-CONFIRM-MENU.BUTTONS.PLAYER"),
                "TpaConfirmMenu must read TPA-CONFIRM-MENU.BUTTONS.PLAYER"
        );
        assertTrue(
                source.contains("TPA-CONFIRM-MENU.BUTTONS.LOCATION"),
                "TpaConfirmMenu must read TPA-CONFIRM-MENU.BUTTONS.LOCATION"
        );
        assertTrue(
                source.contains("TPA-CONFIRM-MENU.BUTTONS.REGION"),
                "TpaConfirmMenu must read TPA-CONFIRM-MENU.BUTTONS.REGION"
        );

        assertFalse(
                source.contains("ItemUtils.createItem(Material.RED_STAINED_GLASS_PANE, \"&cCancel\""),
                "TpaConfirmMenu must not hardcode cancel button item"
        );
        assertFalse(
                source.contains("ItemUtils.createItem(Material.LIME_STAINED_GLASS_PANE, \"&aConfirm\""),
                "TpaConfirmMenu must not hardcode confirm button item"
        );
    }
}
