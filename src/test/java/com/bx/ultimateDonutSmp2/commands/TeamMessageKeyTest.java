package com.bx.ultimateDonutSmp2.commands;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamMessageKeyTest {

    private static final Pattern KEY = Pattern.compile("getMessage\\(\\s*\"(TEAM\\.[A-Z0-9_-]+)\"");

    private static final Path COMMAND =
            Path.of("src/main/java/com/bx/ultimateDonutSmp2/commands/TeamCommand.java");

    /**
     * ConfigManager.getMessage(path) asks LanguageManager for "MESSAGES." + path. A key missing from
     * languages/en_US.yml reaches the player as "&cmissing language key: MESSAGES.&lt;path&gt;".
     */
    @Test
    void everyTeamMessageKeyTheCommandReadsIsShipped() throws IOException {
        ConfigurationSection language = YamlConfiguration
                .loadConfiguration(new File("src/main/resources/languages/en_US.yml"))
                .getConfigurationSection("MESSAGES");

        Set<String> missing = new TreeSet<>();
        Matcher matcher = KEY.matcher(Files.readString(COMMAND));
        while (matcher.find()) {
            String key = matcher.group(1);
            if (language == null || !language.isString(key)) {
                missing.add(key);
            }
        }

        assertTrue(missing.isEmpty(),
                "languages/en_US.yml is missing keys TeamCommand reads: " + missing);
    }
}
