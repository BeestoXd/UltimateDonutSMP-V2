package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PayConfirmAdvertisedButtonsProofTest {

    private static final Path MENUS_YAML = Path.of("src/main/resources/menus.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-menus.yml.md");
    private static final Path MENU_SOURCE = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/PayConfirmMenu.java");

    @Test
    void bundledYamlShipsTheAdvertisedButtons() throws Exception {
        YamlConfiguration menus = new YamlConfiguration();
        menus.load(MENUS_YAML.toFile());

        assertEquals("&#00FC00Confirm", menus.getString("PAY-CONFIRM-MENU.CONFIRM-BUTTON.TITLE"));
        assertEquals("LIME_STAINED_GLASS_PANE", menus.getString("PAY-CONFIRM-MENU.CONFIRM-BUTTON.MATERIAL"));
        assertEquals("&#FC0000Cancel", menus.getString("PAY-CONFIRM-MENU.CANCEL-BUTTON.TITLE"));
        assertEquals("RED_STAINED_GLASS_PANE", menus.getString("PAY-CONFIRM-MENU.CANCEL-BUTTON.MATERIAL"));
        assertEquals("&#00FC00{player}", menus.getString("PAY-CONFIRM-MENU.PLAYER-BUTTON.TITLE"));
        assertEquals("PLAYER_HEAD", menus.getString("PAY-CONFIRM-MENU.PLAYER-BUTTON.MATERIAL"));
    }

    @Test
    void confirmCancelAndPlayerButtonsMustHaveJavaReaders() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(
                wiki.contains("`PAY-CONFIRM-MENU.CONFIRM-BUTTON.TITLE`")
                        && wiki.contains("`PAY-CONFIRM-MENU.CANCEL-BUTTON.TITLE`")
                        && wiki.contains("`PAY-CONFIRM-MENU.PLAYER-BUTTON.TITLE`"),
                "Config-menus.yml.md tables PAY-CONFIRM-MENU confirm/cancel/player buttons as live layout"
        );

        String source = Files.readString(MENU_SOURCE, StandardCharsets.UTF_8);
        assertTrue(
                source.contains("PAY-CONFIRM-MENU.CONFIRM-BUTTON.TITLE"),
                "PayConfirmMenu must read PAY-CONFIRM-MENU.CONFIRM-BUTTON.TITLE"
        );
        assertTrue(
                source.contains("PAY-CONFIRM-MENU.CONFIRM-BUTTON.MATERIAL"),
                "PayConfirmMenu must read PAY-CONFIRM-MENU.CONFIRM-BUTTON.MATERIAL"
        );
        assertTrue(
                source.contains("PAY-CONFIRM-MENU.CONFIRM-BUTTON.LORE"),
                "PayConfirmMenu must read PAY-CONFIRM-MENU.CONFIRM-BUTTON.LORE"
        );
        assertTrue(
                source.contains("PAY-CONFIRM-MENU.CANCEL-BUTTON.TITLE"),
                "PayConfirmMenu must read PAY-CONFIRM-MENU.CANCEL-BUTTON.TITLE"
        );
        assertTrue(
                source.contains("PAY-CONFIRM-MENU.CANCEL-BUTTON.MATERIAL"),
                "PayConfirmMenu must read PAY-CONFIRM-MENU.CANCEL-BUTTON.MATERIAL"
        );
        assertTrue(
                source.contains("PAY-CONFIRM-MENU.CANCEL-BUTTON.LORE"),
                "PayConfirmMenu must read PAY-CONFIRM-MENU.CANCEL-BUTTON.LORE"
        );
        assertTrue(
                source.contains("PAY-CONFIRM-MENU.PLAYER-BUTTON.TITLE"),
                "PayConfirmMenu must read PAY-CONFIRM-MENU.PLAYER-BUTTON.TITLE"
        );
        assertTrue(
                source.contains("PAY-CONFIRM-MENU.PLAYER-BUTTON.MATERIAL"),
                "PayConfirmMenu must read PAY-CONFIRM-MENU.PLAYER-BUTTON.MATERIAL"
        );

        assertFalse(
                source.contains("ItemUtils.createItem(Material.RED_STAINED_GLASS_PANE, \"&cCancel\""),
                "PayConfirmMenu must not hardcode cancel button item"
        );
        assertFalse(
                source.contains("ItemUtils.createItem(Material.LIME_STAINED_GLASS_PANE, \"&aConfirm\""),
                "PayConfirmMenu must not hardcode confirm button item"
        );
    }
}
