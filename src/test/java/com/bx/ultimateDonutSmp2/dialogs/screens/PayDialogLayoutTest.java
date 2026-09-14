package com.bx.ultimateDonutSmp2.dialogs.screens;

import com.bx.ultimateDonutSmp2.managers.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PayDialogLayoutTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/dialogs/screens/PayDialog.java");

    @Test
    void openAmountsMatchesReferenceMainDesign() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private boolean openAmounts");
        int end = source.indexOf("private boolean openCustom");
        assertTrue(start >= 0 && end > start, "openAmounts must exist");

        String method = source.substring(start, end);
        // Inline player head component must be used
        assertTrue(method.contains("DialogPlayerHeads.component"), "Inline player head must be present");
        // No floating 3D item entity
        assertFalse(method.contains(".item("), "Floating item entity must not be added");
        // No pinned bottom exit button; back button is in the 2-column grid alongside custom
        assertFalse(method.contains("screen.exit("), "exit() pinned bottom button must not be used");
        assertTrue(method.contains("hasBackButton"), "Back button in grid must be tracked");
    }

    @Test
    void openCustomMatchesReferenceCustomDesign() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private boolean openCustom");
        int end = source.indexOf("private boolean openConfirm");
        assertTrue(start >= 0 && end > start, "openCustom must exist");

        String method = source.substring(start, end);
        // Stacked 1-column layout
        assertTrue(method.contains(".columns(config.columns(CUSTOM, 1))"), "Custom screen must default to 1 column");
    }

    @Test
    void openAddMatchesReferenceAddDesign() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private boolean openAdd");
        int end = source.indexOf("private boolean openAmounts");
        assertTrue(start >= 0 && end > start, "openAdd must exist");

        String method = source.substring(start, end);
        // Stacked 1-column layout
        assertTrue(method.contains(".columns(config.columns(ADD, 1))"), "Add screen must default to 1 column");
    }

    @Test
    void openConfirmMatchesCleanMultiActionLayout() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private boolean openConfirm");
        int end = source.indexOf("private boolean execute");
        assertTrue(start >= 0 && end > start, "openConfirm must exist");

        String method = source.substring(start, end);
        // Inline player head component must be used
        assertTrue(method.contains("DialogPlayerHeads.component"), "Inline player head must be present");
        // No floating 3D item entity
        assertFalse(method.contains(".item("), "Floating item entity must not be added");
        // Must not call buildConfirmation which puts buttons at the bottom of the screen
        assertFalse(method.contains("buildConfirmation"), "buildConfirmation must not be used");
        // Uses standard multiAction build()
        assertTrue(method.contains("screen.build()"), "Confirmation screen must use multiAction screen.build()");
        assertTrue(method.contains("\"raw_amount\""), "Confirmation screen must provide raw_amount token");
    }

    @Test
    void executeClosesDialogAndTransfers() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private boolean execute");
        int end = source.indexOf("public boolean handle");
        assertTrue(start >= 0 && end > start, "execute must exist");

        String method = source.substring(start, end);
        assertTrue(method.contains("DialogSupport.close(player)"), "execute must close dialog");
        assertTrue(method.contains("PaymentUtils.transferMoney"), "execute must transfer money");
    }

    @Test
    void withTargetAndAmountParsesCompactNumbers() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("private void withTargetAndAmount");
        int end = source.indexOf("private interface TargetAmountStep");
        assertTrue(start >= 0 && end > start, "withTargetAndAmount must exist");

        String method = source.substring(start, end);
        assertTrue(method.contains("NumberUtils.parse"), "withTargetAndAmount must use NumberUtils.parse");
        assertFalse(method.contains("Double.parseDouble"), "withTargetAndAmount must not use Double.parseDouble");
    }

    @Test
    void paymentUtilsPlaysPaySuccessSound() throws Exception {
        String source = Files.readString(Path.of("src/main/java/com/bx/ultimateDonutSmp2/utils/PaymentUtils.java"));
        assertTrue(source.contains("PAY.SUCCESS"), "PaymentUtils must play PAY.SUCCESS sound");
    }

    @Test
    void soundsConfigAndConfigManagerProvidePaySuccessSound() {
        var stream = getClass().getClassLoader().getResourceAsStream("sounds.yml");
        assertNotNull(stream, "sounds.yml must exist in resources");
        YamlConfiguration sounds = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );
        String configured = sounds.getString("PAY.SUCCESS");
        assertEquals("minecraft:entity.player.levelup|1.0|1.2", configured);

        ConfigManager manager = new ConfigManager(null);
        assertEquals("minecraft:entity.player.levelup|1.0|1.2", manager.getSound("PAY.SUCCESS"));
    }
}
