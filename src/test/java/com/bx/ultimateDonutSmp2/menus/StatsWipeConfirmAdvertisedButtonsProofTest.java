package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatsWipeConfirmAdvertisedButtonsProofTest {

    private static final Path MENUS_YAML = Path.of("src/main/resources/menus.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-menus.yml.md");
    private static final Path CONFIRM_MENU = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/StatsWipeConfirmMenu.java");
    private static final Path STATS_MENU = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/StatsWipeMenu.java");
    private static final Path TEAM_DISBAND = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/TeamDisbandConfirmMenu.java");
    private static final Path TEAM_KICK = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/TeamKickConfirmMenu.java");
    private static final Path TEAM_EDIT = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/TeamEditMenu.java");
    private static final Path TEAM_MENU = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/TeamMenu.java");

    @Test
    void bundledYamlResolvesAdvertisedUppercaseKeys() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.load(MENUS_YAML.toFile());

        assertEquals("&cCancel", config.getString("STATS-WIPE-CONFIRM-MENU.BUTTONS.CANCEL.DISPLAY-NAME"));
        assertNull(config.getString("STATS-WIPE-CONFIRM-MENU.buttons.cancel.DISPLAY-NAME"));

        assertNotNull(config.getString("STATS-WIPE-MENU.BUTTONS.CLOSE.DISPLAY-NAME"));
        assertNull(config.getString("STATS-WIPE-MENU.buttons.close.DISPLAY-NAME"));

        assertNotNull(config.getString("TEAM-MENUS.TEAM-DISBAND.CANCEL-BUTTON.TITLE"));
        assertNotNull(config.getString("TEAM-MENUS.TEAM-DISBAND.CONFIRM-BUTTON.TITLE"));

        assertNotNull(config.getString("TEAM-MENUS.TEAM-KICK-MEMBER.CANCEL-BUTTON.TITLE"));
        assertNotNull(config.getString("TEAM-MENUS.TEAM-KICK-MEMBER.CONFIRM-BUTTON.TITLE"));

        assertNotNull(config.getString("TEAM-MENUS.TEAM-EDIT-MEMBER.KICK-BUTTON.TITLE"));
        assertNotNull(config.getString("TEAM-MENUS.TEAM-EDIT-MEMBER.BACK-BUTTON.TITLE"));

        assertNotNull(config.getString("TEAM-MENUS.TEAM.HOME-BUTTON.TITLE"));
    }

    @Test
    void confirmMenuBuildMustReadAdvertisedButtonKeys() throws Exception {
        String confirmSource = Files.readString(CONFIRM_MENU, StandardCharsets.UTF_8);
        assertTrue(confirmSource.contains("MENU_PATH + \".BUTTONS.TARGET\""),
                "Config-menus.yml.md tables STATS-WIPE-CONFIRM-MENU.BUTTONS.TARGET as live layout");
        assertTrue(confirmSource.contains("MENU_PATH + \".BUTTONS.CANCEL\""),
                "Config-menus.yml.md tables STATS-WIPE-CONFIRM-MENU.BUTTONS.CANCEL as live layout");
        assertTrue(confirmSource.contains("MENU_PATH + \".BUTTONS.CONFIRM\""),
                "Config-menus.yml.md tables STATS-WIPE-CONFIRM-MENU.BUTTONS.CONFIRM as live layout");
        assertFalse(confirmSource.contains(".buttons.cancel"),
                "StatsWipeConfirmMenu must not read lowercase .buttons.cancel");

        String statsSource = Files.readString(STATS_MENU, StandardCharsets.UTF_8);
        assertTrue(statsSource.contains("MENU_PATH + \".BUTTONS.CLOSE\""),
                "StatsWipeMenu must read STATS-WIPE-MENU.BUTTONS.CLOSE");
        assertFalse(statsSource.contains(".buttons.close"),
                "StatsWipeMenu must not read lowercase .buttons.close");

        String disbandSource = Files.readString(TEAM_DISBAND, StandardCharsets.UTF_8);
        assertTrue(disbandSource.contains("MENU_PATH + \".CANCEL-BUTTON\""),
                "TeamDisbandConfirmMenu must read CANCEL-BUTTON");
        assertTrue(disbandSource.contains("MENU_PATH + \".CONFIRM-BUTTON\""),
                "TeamDisbandConfirmMenu must read CONFIRM-BUTTON");
        assertFalse(disbandSource.contains(".cancel-button"),
                "TeamDisbandConfirmMenu must not read lowercase .cancel-button");

        String kickSource = Files.readString(TEAM_KICK, StandardCharsets.UTF_8);
        assertTrue(kickSource.contains("MENU_PATH + \".CANCEL-BUTTON\""),
                "TeamKickConfirmMenu must read CANCEL-BUTTON");
        assertTrue(kickSource.contains("MENU_PATH + \".CONFIRM-BUTTON\""),
                "TeamKickConfirmMenu must read CONFIRM-BUTTON");
        assertFalse(kickSource.contains(".cancel-button"),
                "TeamKickConfirmMenu must not read lowercase .cancel-button");

        String editSource = Files.readString(TEAM_EDIT, StandardCharsets.UTF_8);
        assertTrue(editSource.contains("MENU_PATH + \".KICK-BUTTON\""),
                "TeamEditMenu must read KICK-BUTTON");
        assertTrue(editSource.contains("MENU_PATH + \".BACK-BUTTON\""),
                "TeamEditMenu must read BACK-BUTTON");
        assertFalse(editSource.contains(".kick-button"),
                "TeamEditMenu must not read lowercase .kick-button");

        String teamSource = Files.readString(TEAM_MENU, StandardCharsets.UTF_8);
        assertTrue(teamSource.contains("MENU_PATH + \".HOME-BUTTON\""),
                "TeamMenu must read HOME-BUTTON");
        assertFalse(teamSource.contains(".home-button"),
                "TeamMenu must not read lowercase .home-button");
    }
}
