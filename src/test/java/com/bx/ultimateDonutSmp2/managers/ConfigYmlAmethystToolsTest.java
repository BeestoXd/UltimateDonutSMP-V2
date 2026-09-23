package com.bx.ultimateDonutSmp2.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigYmlAmethystToolsTest {

    @Test
    void configYmlDoesNotContainAmethystToolsSection() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("src/main/resources/config.yml"));
        assertFalse(config.contains("AMETHYST-TOOLS"),
                "config.yml must not contain AMETHYST-TOOLS; amethyst-tools.yml is the dedicated config file");
    }

    @Test
    void configConfigWikiDoesNotDocumentAmethystTools() throws IOException {
        Path wikiPage = Path.of("docs", "wiki", "Config-config.yml.md");
        String content = Files.readString(wikiPage, StandardCharsets.UTF_8);
        assertFalse(content.contains("AMETHYST-TOOLS"),
                "docs/wiki/Config-config.yml.md must not table AMETHYST-TOOLS; it belongs in Config-amethyst-tools.yml.md");
    }

    @Test
    void amethystToolsFileMaintainsAmethystToolsSection() {
        YamlConfiguration amethystTools = YamlConfiguration.loadConfiguration(new File("src/main/resources/amethyst-tools.yml"));
        assertTrue(amethystTools.contains("AMETHYST-TOOLS"),
                "amethyst-tools.yml must be the home of AMETHYST-TOOLS configuration");
        assertNotNull(amethystTools.getConfigurationSection("AMETHYST-TOOLS.DRILL"),
                "amethyst-tools.yml must configure DRILL under AMETHYST-TOOLS");
    }
}
