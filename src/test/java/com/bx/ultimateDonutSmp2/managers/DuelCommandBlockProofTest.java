package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DuelCommandBlockProofTest {

    private static final Path DUELS_YML = Path.of("src/main/resources/duels.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-duels.yml.md");

    @Test
    void bundledCommandListMustBeTheKeyDuelManagerReads() throws Exception {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(DUELS_YML.toFile());
        List<String> bundledCommands = config.getStringList("COMMAND_BLOCK.COMMANDS");
        assertEquals(List.of("/duel", "/draw", "/leave", "/queue"), bundledCommands);
        assertTrue(config.getStringList("COMMAND_BLOCK.ALLOWLIST").isEmpty());

        DuelManager manager = createDuelManager(config);
        Method method = DuelManager.class.getDeclaredMethod("commandPatterns", String.class, List.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> patterns = (List<String>) method.invoke(manager, "COMMAND_BLOCK.ALLOWLIST", List.of());

        assertEquals(
                List.of("/duel", "/draw", "/leave", "/queue"),
                patterns,
                "DuelManager.commandPatterns reads COMMAND_BLOCK.ALLOWLIST / BLOCKLIST, so COMMANDS is dead"
        );
    }

    @Test
    void isCommandAllowedUsesBundledCommandsAndBlocksCreate() throws Exception {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(DUELS_YML.toFile());
        DuelManager manager = createDuelManager(config);

        assertTrue(manager.isCommandAllowedDuringMatch("/duel"));
        assertTrue(manager.isCommandAllowedDuringMatch("/draw"));
        assertTrue(manager.isCommandAllowedDuringMatch("/leave"));
        assertTrue(manager.isCommandAllowedDuringMatch("/queue"));
        assertFalse(manager.isCommandAllowedDuringMatch("/create"), "/create is not in bundled COMMANDS and must not be hardcoded");
        assertFalse(manager.isCommandAllowedDuringMatch("/tpa"));
    }

    @Test
    void isCommandAllowedReflectsCustomizedCommandsList() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.set("COMMAND_BLOCK.ENABLED", true);
        config.set("COMMAND_BLOCK.MODE", "ALLOWLIST");
        config.set("COMMAND_BLOCK.COMMANDS", List.of("/duel", "/msg"));

        DuelManager manager = createDuelManager(config);

        assertTrue(manager.isCommandAllowedDuringMatch("/duel"));
        assertTrue(manager.isCommandAllowedDuringMatch("/msg"));
        assertFalse(manager.isCommandAllowedDuringMatch("/leave"));
        assertFalse(manager.isCommandAllowedDuringMatch("/queue"));
        assertFalse(manager.isCommandAllowedDuringMatch("/create"));
    }

    @Test
    void isCommandAllowedSupportsBlocklistModeWithCommandsList() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.set("COMMAND_BLOCK.ENABLED", true);
        config.set("COMMAND_BLOCK.MODE", "BLOCKLIST");
        config.set("COMMAND_BLOCK.COMMANDS", List.of("/tpa", "/spawn"));

        DuelManager manager = createDuelManager(config);

        assertFalse(manager.isCommandAllowedDuringMatch("/tpa"));
        assertFalse(manager.isCommandAllowedDuringMatch("/spawn"));
        assertTrue(manager.isCommandAllowedDuringMatch("/duel"));
        assertTrue(manager.isCommandAllowedDuringMatch("/home"));
    }

    @Test
    void wikiAndBundledConfigAgreeOnCommandBlockCommands() throws Exception {
        String wiki = Files.readString(WIKI);
        assertTrue(
                wiki.contains("COMMAND_BLOCK.COMMANDS"),
                "Config-duels.yml.md must document COMMAND_BLOCK.COMMANDS"
        );

        YamlConfiguration config = YamlConfiguration.loadConfiguration(DUELS_YML.toFile());
        assertTrue(
                config.contains("COMMAND_BLOCK.COMMANDS"),
                "duels.yml must define COMMAND_BLOCK.COMMANDS"
        );
    }

    @Test
    void namespacedLeaveMustMatchAllowlistTheSameWayFreezeDoes() throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        Constructor<?> constructor = ReflectionFactory.getReflectionFactory()
                .newConstructorForSerialization(DuelManager.class, objectConstructor);
        DuelManager manager = (DuelManager) constructor.newInstance();

        assertTrue(
                manager.isCommandAllowedDuringMatch("/leave"),
                "control: plain /leave must be allowed during a match"
        );
        assertTrue(
                manager.isCommandAllowedDuringMatch("/ultimatedonutsmp2:leave"),
                "isCommandAllowedDuringMatch must treat /ultimatedonutsmp2:leave as /leave"
        );
        assertTrue(manager.isCommandAllowedDuringMatch("/UltimateDonutSMP2:Leave"));
        assertTrue(manager.isCommandAllowedDuringMatch("/ultimatedonutsmp2:duel"));
        assertTrue(manager.isCommandAllowedDuringMatch("/ultimatedonutsmp2:duel player"));
        assertTrue(manager.isCommandAllowedDuringMatch("/ultimatedonutsmp2:draw"));
        assertTrue(manager.isCommandAllowedDuringMatch("/ultimatedonutsmp2:queue"));

        assertFalse(manager.isCommandAllowedDuringMatch("/leaves"));
        assertFalse(manager.isCommandAllowedDuringMatch("/ultimatedonutsmp2:leaves"));
        assertFalse(manager.isCommandAllowedDuringMatch("/tpa"));
        assertFalse(manager.isCommandAllowedDuringMatch("/ultimatedonutsmp2:tpa"));
        assertFalse(manager.isCommandAllowedDuringMatch("/spawn"));
    }

    @Test
    void normalizeCommandPatternStripsPluginNamespace() {
        assertEquals("/leave", DuelManager.normalizeCommandPattern("/ultimatedonutsmp2:leave"));
        assertEquals("/leave", DuelManager.normalizeCommandPattern("/UltimateDonutSMP2:Leave"));
        assertEquals("/leave", DuelManager.normalizeCommandPattern("ultimatedonutsmp2:leave"));
        assertEquals("/leave", DuelManager.normalizeCommandPattern("/leave"));
        assertEquals("/leave", DuelManager.normalizeCommandPattern("leave"));
        assertEquals("/duel player", DuelManager.normalizeCommandPattern("/ultimatedonutsmp2:duel player"));
    }

    @Test
    void blocklistModeBlocksNamespacedVariantsToo() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("COMMAND_BLOCK.ENABLED", true);
        config.set("COMMAND_BLOCK.MODE", "BLOCKLIST");
        config.set("COMMAND_BLOCK.BLOCKLIST", List.of("/tpa", "/spawn", "/rtp"));

        assertFalse(DuelManager.isCommandAllowedDuringMatch("/tpa", config));
        assertFalse(DuelManager.isCommandAllowedDuringMatch("/ultimatedonutsmp2:tpa", config));
        assertFalse(DuelManager.isCommandAllowedDuringMatch("/UltimateDonutSMP2:Spawn", config));
        assertTrue(DuelManager.isCommandAllowedDuringMatch("/msg Steve", config));
    }

    @Test
    void bundledDuelsYmlAllowlistContainsExpectedDefaults() {
        var stream = DuelCommandBlockProofTest.class.getClassLoader().getResourceAsStream("duels.yml");
        assertNotNull(stream, "duels.yml resource must be present");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );
        assertTrue(config.getBoolean("COMMAND_BLOCK.ENABLED", true));
    }

    private DuelManager createDuelManager(YamlConfiguration duelConfig) throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor<?> newConstructor = reflectionFactory.newConstructorForSerialization(UltimateDonutSmp2.class, objectConstructor);
        UltimateDonutSmp2 plugin = (UltimateDonutSmp2) newConstructor.newInstance();

        ConfigManager configManager = new ConfigManager(plugin);
        Field duelsField = ConfigManager.class.getDeclaredField("duels");
        duelsField.setAccessible(true);
        duelsField.set(configManager, duelConfig);

        Field cmField = UltimateDonutSmp2.class.getDeclaredField("configManager");
        cmField.setAccessible(true);
        cmField.set(plugin, configManager);

        Field descField = JavaPlugin.class.getDeclaredField("description");
        descField.setAccessible(true);
        PluginDescriptionFile pdf = new PluginDescriptionFile("UltimateDonutSmp2", "1.0", "com.bx.ultimateDonutSmp2.UltimateDonutSmp2");
        descField.set(plugin, pdf);

        Constructor<?> duelManagerConstructor = reflectionFactory.newConstructorForSerialization(DuelManager.class, objectConstructor);
        DuelManager manager = (DuelManager) duelManagerConstructor.newInstance();

        Field pluginField = DuelManager.class.getDeclaredField("plugin");
        pluginField.setAccessible(true);
        pluginField.set(manager, plugin);

        return manager;
    }
}
