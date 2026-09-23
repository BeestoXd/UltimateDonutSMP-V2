package com.bx.ultimateDonutSmp2.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrateConfirmCancelSlotProofTest {

    private static final Path CRATES = Path.of("src/main/resources/crates.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-crates.yml.md");
    private static final Path MANAGER = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/CrateManager.java");

    @Test
    void confirmMenuMustReadAdvertisedCancelButtonSlot() throws Exception {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(CRATES.toFile());
        assertTrue(
                config.contains("SETTINGS.CONFIRM-MENU.CANCEL-BUTTON.SLOT"),
                "crates.yml must define SETTINGS.CONFIRM-MENU.CANCEL-BUTTON.SLOT"
        );
        assertEquals(11, config.getInt("SETTINGS.CONFIRM-MENU.CANCEL-BUTTON.SLOT"));

        String wiki = Files.readString(WIKI);
        assertTrue(
                wiki.contains("SETTINGS.CONFIRM-MENU.CANCEL-BUTTON.SLOT"),
                "Config-crates.yml.md tables SETTINGS.CONFIRM-MENU.CANCEL-BUTTON.SLOT as live"
        );

        String managerSource = Files.readString(MANAGER);
        int methodStart = managerSource.indexOf("private ConfirmMenuSettings loadConfirmMenuSettings()");
        assertTrue(methodStart >= 0, "loadConfirmMenuSettings should exist in CrateManager");
        int methodEnd = managerSource.indexOf("private GachaSettings loadGachaDefaults()", methodStart);
        assertTrue(methodEnd > methodStart, "loadGachaDefaults should follow loadConfirmMenuSettings");
        String methodBody = managerSource.substring(methodStart, methodEnd);

        assertTrue(
                methodBody.contains("CANCEL-BUTTON.SLOT"),
                "loadConfirmMenuSettings must read CANCEL-BUTTON.SLOT as advertised in crates.yml and the wiki"
        );
    }

    @Test
    void confirmMenuCancelSlotFallbackLogic() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("SETTINGS.CONFIRM-MENU.CANCEL-BUTTON.SLOT", 18);
        var section = config.getConfigurationSection("SETTINGS.CONFIRM-MENU");
        int cancelSlot = section.getInt("CANCEL-BUTTON.SLOT", section.getInt("CANCEL-SLOT", 11));
        assertEquals(18, cancelSlot);

        YamlConfiguration fallbackConfig = new YamlConfiguration();
        fallbackConfig.set("SETTINGS.CONFIRM-MENU.CANCEL-SLOT", 20);
        var fallbackSection = fallbackConfig.getConfigurationSection("SETTINGS.CONFIRM-MENU");
        int fallbackCancelSlot = fallbackSection.getInt("CANCEL-BUTTON.SLOT", fallbackSection.getInt("CANCEL-SLOT", 11));
        assertEquals(20, fallbackCancelSlot);

        YamlConfiguration emptyConfig = new YamlConfiguration();
        emptyConfig.createSection("SETTINGS.CONFIRM-MENU");
        var emptySection = emptyConfig.getConfigurationSection("SETTINGS.CONFIRM-MENU");
        int defaultCancelSlot = emptySection.getInt("CANCEL-BUTTON.SLOT", emptySection.getInt("CANCEL-SLOT", 11));
        assertEquals(11, defaultCancelSlot);
    }
}
