package com.bx.ultimateDonutSmp2.dialogs.screens;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FriendsEditDialogLayoutTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/dialogs/screens/FriendsDialog.java");
    private static final Path DIALOG_YML = Path.of(
            "src/main/resources/dialog.yml");

    @Test
    void openDetailsMatchesReferenceEditDesign() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private boolean openDetails(Player player, UUID target)");
        int end = source.indexOf("private boolean openSettings");
        assertTrue(start >= 0 && end > start, "openDetails must exist");

        String method = source.substring(start, end);
        assertFalse(method.contains("screen.exit("), "openDetails must not use bottom pinned exit button");
        assertTrue(method.contains("config.buttonWidth(DETAILS, 300)"), "openDetails button width must default to 300");
        assertTrue(method.contains("VIEW-STATS-TOOLTIP"), "View Stats button must carry tooltip");
        assertTrue(method.contains("PAY-TOOLTIP"), "Pay button must carry tooltip");
        assertTrue(method.contains("TELEPORT-TOOLTIP"), "Teleport button must carry tooltip");
        assertTrue(method.contains("SETTINGS-TOOLTIP"), "Settings button must carry tooltip");
        assertTrue(method.contains("config.integer(DETAILS + \".BACK-WIDTH\", 150)"), "Back button width must be 150");
    }

    @Test
    void openSettingsMatchesReferenceSettingsDesign() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private boolean openSettings(Player player, UUID target)");
        int end = source.indexOf("public boolean handle");
        assertTrue(start >= 0 && end > start, "openSettings must exist");

        String method = source.substring(start, end);
        assertFalse(method.contains("screen.exit("), "openSettings must not use bottom pinned exit button");
        assertTrue(method.contains("config.buttonWidth(SETTINGS, 300)"), "openSettings button width must default to 300");
        assertTrue(method.contains("config.integer(SETTINGS + \".BACK-WIDTH\", width)"), "Back button width must default to width");
    }

    @Test
    void dialogConfigMatchesReferenceDesign() throws Exception {
        String yaml = Files.readString(DIALOG_YML);
        assertTrue(yaml.contains("BUTTON-WIDTH: 300"), "dialog.yml must specify BUTTON-WIDTH: 300");
        assertTrue(yaml.contains("VIEW-STATS-TOOLTIP: \"See %name%'s profile\""));
        assertTrue(yaml.contains("PAY-TOOLTIP: \"Send money to %name%\""));
        assertTrue(yaml.contains("TELEPORT-TOOLTIP: \"Teleport to or bring %name%\""));
        assertTrue(yaml.contains("SETTINGS-TOOLTIP: \"Change settings for %name%\""));
    }
}
