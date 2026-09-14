package com.bx.ultimateDonutSmp2.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemUtilsLoreTest {

    @Test
    void cleanLoreListStripsStringifiedListBrackets() {
        List<String> input = List.of("[&o&7Click to search]");
        List<String> cleaned = ItemUtils.cleanLoreList(input);
        assertEquals(List.of("&o&7Click to search"), cleaned);
    }

    @Test
    void cleanLoreListPreservesRegularLore() {
        List<String> input = List.of("&fSearch", "&o&7Click to search");
        List<String> cleaned = ItemUtils.cleanLoreList(input);
        assertEquals(List.of("&fSearch", "&o&7Click to search"), cleaned);
    }

    @Test
    void readLoreParsesYamlListCorrectly() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("BUTTONS.SEARCH.LORE", List.of("&o&7Click to search"));

        List<String> result = ItemUtils.readLore(config, "BUTTONS.SEARCH.LORE", List.of("fallback"));
        assertEquals(List.of("&o&7Click to search"), result);
    }

    @Test
    void readLoreParsesSingleStringCorrectly() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("BUTTONS.SEARCH.LORE", "&o&7Click to search");

        List<String> result = ItemUtils.readLore(config, "BUTTONS.SEARCH.LORE", List.of("fallback"));
        assertEquals(List.of("&o&7Click to search"), result);
    }
}