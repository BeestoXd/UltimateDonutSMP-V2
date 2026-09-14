package com.bx.ultimateDonutSmp2.dialogs.screens;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SettingsDialogLayoutTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/dialogs/screens/SettingsDialog.java");
    private static final Path DIALOG_YML = Path.of(
            "src/main/resources/dialog.yml");

    @Test
    void openCategoryMatchesReferenceDesignWithInlineBackButton() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private boolean openCategory(Player player, String id)");
        int end = source.indexOf("public boolean handle");
        assertTrue(start >= 0 && end > start, "openCategory must exist");

        String method = source.substring(start, end);
        assertFalse(method.contains("screen.exit("), "openCategory must not use bottom pinned exit button");
        assertTrue(method.contains("config.buttonWidth(PATH, 250)"), "openCategory button width must default to 250");
        assertTrue(method.contains("screen.button("), "openCategory must add Back as an inline button");
    }

    @Test
    void openMatchesReferenceDesignWithoutBottomExit() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public boolean open(Player player)");
        int end = source.indexOf("private boolean openCategory");
        assertTrue(start >= 0 && end > start, "open must exist");

        String method = source.substring(start, end);
        assertFalse(method.contains("screen.exit("), "open must not use bottom pinned exit button");
    }

    @Test
    void dialogConfigSpecifiesCorrectSettingsButtonWidth() throws Exception {
        String yaml = Files.readString(DIALOG_YML);
        int settingsIndex = yaml.indexOf("SETTINGS_DIALOG:");
        int payIndex = yaml.indexOf("PAY_DIALOG:");
        assertTrue(settingsIndex >= 0 && payIndex > settingsIndex, "SETTINGS_DIALOG section must exist");

        String settingsSection = yaml.substring(settingsIndex, payIndex);
        assertTrue(settingsSection.contains("BUTTON-WIDTH: 250"),
                "SETTINGS_DIALOG must specify BUTTON-WIDTH: 250 to match reference design");
    }
}
