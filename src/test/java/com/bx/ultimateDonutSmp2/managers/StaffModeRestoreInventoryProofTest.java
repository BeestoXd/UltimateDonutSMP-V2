package com.bx.ultimateDonutSmp2.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaffModeRestoreInventoryProofTest {

    private static final Path STAFF_MODE_YAML = Path.of("src/main/resources/staff-mode.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-staff-mode.yml.md");
    private static final Path MANAGER = Path.of("src/main/java/com/bx/ultimateDonutSmp2/managers/StaffModeManager.java");

    @Test
    void bundledYamlShipsRestoreInventoryOnDisable() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.load(STAFF_MODE_YAML.toFile());
        assertTrue(config.contains("STAFF-MODE.RESTORE-INVENTORY-ON-DISABLE"),
                "staff-mode.yml must contain STAFF-MODE.RESTORE-INVENTORY-ON-DISABLE");
        assertEquals(true, config.getBoolean("STAFF-MODE.RESTORE-INVENTORY-ON-DISABLE"));
    }

    @Test
    void wikiTablesRestoreInventoryOnDisable() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(wiki.contains("`STAFF-MODE.RESTORE-INVENTORY-ON-DISABLE`"),
                "Config-staff-mode.yml.md must list STAFF-MODE.RESTORE-INVENTORY-ON-DISABLE");
    }

    @Test
    void restoreInventoryOnDisableMustBeConsulted() throws Exception {
        String managerSource = Files.readString(MANAGER, StandardCharsets.UTF_8);
        assertTrue(managerSource.contains("shouldRestoreInventoryOnDisable()"),
                "Config-staff-mode.yml.md says RESTORE-INVENTORY-ON-DISABLE gates disable restore; getter is never called");
        // Verify it is called in disableSilently or disable logic rather than only being defined
        long occurrences = managerSource.lines()
                .filter(line -> line.contains("shouldRestoreInventoryOnDisable()"))
                .count();
        assertTrue(occurrences >= 2,
                "shouldRestoreInventoryOnDisable() must have at least one caller besides its definition");
    }
}
