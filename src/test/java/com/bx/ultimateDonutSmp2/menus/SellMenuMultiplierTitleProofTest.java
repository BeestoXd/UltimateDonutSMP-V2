package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;

class SellMenuMultiplierTitleProofTest {

    @Test
    void multiplierTitleMustNotBeAdvertisedOrPresentInBundledYaml() throws Exception {
        YamlConfiguration menus = new YamlConfiguration();
        menus.load(new File("src/main/resources/menus.yml"));
        assertFalse(
                menus.contains("SELL-MENU.MULTIPLIER-TITLE"),
                "menus.yml must not contain unread SELL-MENU.MULTIPLIER-TITLE"
        );

        String wiki = Files.readString(Path.of("docs/wiki/Config-menus.yml.md"));
        assertFalse(
                wiki.contains("SELL-MENU.MULTIPLIER-TITLE"),
                "Config-menus.yml.md must not table unread SELL-MENU.MULTIPLIER-TITLE"
        );
    }
}
