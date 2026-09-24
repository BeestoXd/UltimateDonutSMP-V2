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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FfaAdvertisedConfigProofTest {

    private static final Path FFA_YML = Path.of("src/main/resources/ffa.yml");
    private static final Path CONFIG_WIKI = Path.of("docs/wiki/Config-ffa.yml.md");
    private static final Path DUELS_WIKI = Path.of("docs/wiki/Duels-and-FFA.md");
    private static final Path SOURCE = Path.of("src/main/java/com/bx/ultimateDonutSmp2/managers/FfaManager.java");

    @Test
    void survivalRewardsKeyMustBeRead() throws Exception {
        String wiki = Files.readString(DUELS_WIKI);
        assertTrue(
                wiki.contains("GIVE_SURVIVAL_REWARDS"),
                "Duels-and-FFA.md must mention GIVE_SURVIVAL_REWARDS"
        );

        String source = Files.readString(SOURCE);
        boolean honoursKey = source.contains("RULES.GIVE_SURVIVAL_REWARDS")
                && source.contains("shouldGiveSurvivalRewards")
                && source.contains("giveSurvivalRewards");

        assertTrue(
                honoursKey,
                "Duels-and-FFA.md says turning RULES.GIVE_SURVIVAL_REWARDS on pays money/shards"
        );

        YamlConfiguration config = new YamlConfiguration();
        config.set("RULES.GIVE_SURVIVAL_REWARDS", true);
        FfaManager manager = createFfaManager(config);
        assertTrue(manager.shouldGiveSurvivalRewards());

        config.set("RULES.GIVE_SURVIVAL_REWARDS", false);
        assertFalse(manager.shouldGiveSurvivalRewards());
    }

    @Test
    void playerStateTogglesMustBeRead() throws Exception {
        String wiki = Files.readString(CONFIG_WIKI);
        assertTrue(
                wiki.contains("PLAYER_STATE.RESTORE_INVENTORY"),
                "Config-ffa.yml.md must document PLAYER_STATE.RESTORE_INVENTORY"
        );

        String source = Files.readString(SOURCE);
        boolean honoursToggles = source.contains("PLAYER_STATE.RESTORE_INVENTORY")
                && source.contains("PLAYER_STATE.RESTORE_HEALTH")
                && source.contains("PLAYER_STATE.RESTORE_EFFECTS")
                && source.contains("shouldRestoreInventory")
                && source.contains("shouldRestoreHealth")
                && source.contains("shouldRestoreEffects");

        assertTrue(
                honoursToggles,
                "Config-ffa.yml.md documents PLAYER_STATE.RESTORE_INVENTORY as a live exit toggle"
        );

        YamlConfiguration config = new YamlConfiguration();
        config.set("PLAYER_STATE.RESTORE_INVENTORY", false);
        config.set("PLAYER_STATE.RESTORE_HEALTH", false);
        config.set("PLAYER_STATE.RESTORE_EFFECTS", false);
        FfaManager manager = createFfaManager(config);

        assertFalse(manager.shouldRestoreInventory());
        assertFalse(manager.shouldRestoreHealth());
        assertFalse(manager.shouldRestoreEffects());

        config.set("PLAYER_STATE.RESTORE_INVENTORY", true);
        config.set("PLAYER_STATE.RESTORE_HEALTH", true);
        config.set("PLAYER_STATE.RESTORE_EFFECTS", true);

        assertTrue(manager.shouldRestoreInventory());
        assertTrue(manager.shouldRestoreHealth());
        assertTrue(manager.shouldRestoreEffects());
    }

    @Test
    void rollbackCleanupFlagsMustBeRead() throws Exception {
        String wiki = Files.readString(CONFIG_WIKI);
        assertTrue(
                wiki.contains("ROLLBACK.CLEANUP_PROJECTILES") && wiki.contains("ROLLBACK.CLEANUP_DROPS"),
                "Config-ffa.yml.md must document ROLLBACK flags"
        );

        String source = Files.readString(SOURCE);
        boolean honoursCleanup = source.contains("ROLLBACK.CLEANUP_PROJECTILES")
                && source.contains("ROLLBACK.CLEANUP_DROPS")
                && source.contains("ROLLBACK.CLEANUP_FIRE_AND_FLUIDS")
                && source.contains("shouldCleanupProjectiles")
                && source.contains("shouldCleanupDrops")
                && source.contains("shouldCleanupFireAndFluids");

        assertTrue(
                honoursCleanup,
                "Config-ffa.yml.md documents ROLLBACK.CLEANUP_PROJECTILES / CLEANUP_DROPS as toggles"
        );

        YamlConfiguration config = new YamlConfiguration();
        config.set("ROLLBACK.CLEANUP_PROJECTILES", false);
        config.set("ROLLBACK.CLEANUP_DROPS", false);
        config.set("ROLLBACK.CLEANUP_FIRE_AND_FLUIDS", false);
        FfaManager manager = createFfaManager(config);

        assertFalse(manager.shouldCleanupProjectiles());
        assertFalse(manager.shouldCleanupDrops());
        assertFalse(manager.shouldCleanupFireAndFluids());

        config.set("ROLLBACK.CLEANUP_PROJECTILES", true);
        config.set("ROLLBACK.CLEANUP_DROPS", true);
        config.set("ROLLBACK.CLEANUP_FIRE_AND_FLUIDS", true);

        assertTrue(manager.shouldCleanupProjectiles());
        assertTrue(manager.shouldCleanupDrops());
        assertTrue(manager.shouldCleanupFireAndFluids());
    }

    @Test
    void resultTitlesMustBeShown() throws Exception {
        String source = Files.readString(SOURCE);
        int finishMatchIdx = source.indexOf("void finishMatch");
        int helperIdx = source.indexOf("void storeTransitionTitle");
        assertTrue(finishMatchIdx >= 0 && helperIdx > finishMatchIdx, "finishMatch and storeTransitionTitle helper must exist");

        int callIdx = source.indexOf("storeTransitionTitle", finishMatchIdx);
        assertTrue(
                callIdx > 0 && callIdx < helperIdx,
                "finishMatch must call storeTransitionTitle before the helper definition, otherwise RESULT-TITLES in ffa.yml never appears"
        );

        assertTrue(
                source.contains(".toLowerCase() + \".title\"") || source.contains(".title\""),
                "formatResultTitle must support lowercase title key shipped in ffa.yml"
        );
        assertTrue(
                source.contains(".toLowerCase() + \".subtitle\"") || source.contains(".subtitle\""),
                "formatResultSubtitle must support lowercase subtitle key shipped in ffa.yml"
        );
    }

    @Test
    void bundledFfaYmlContainsAdvertisedKeys() {
        var stream = FfaAdvertisedConfigProofTest.class.getClassLoader().getResourceAsStream("ffa.yml");
        assertNotNull(stream, "ffa.yml resource must be present");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );
        assertTrue(config.contains("RULES.GIVE_SURVIVAL_REWARDS"));
        assertTrue(config.contains("PLAYER_STATE.RESTORE_INVENTORY"));
        assertTrue(config.contains("PLAYER_STATE.RESTORE_HEALTH"));
        assertTrue(config.contains("PLAYER_STATE.RESTORE_EFFECTS"));
        assertTrue(config.contains("ROLLBACK.CLEANUP_PROJECTILES"));
        assertTrue(config.contains("ROLLBACK.CLEANUP_DROPS"));
        assertTrue(config.contains("ROLLBACK.CLEANUP_FIRE_AND_FLUIDS"));
        assertTrue(config.contains("RESULT-TITLES.victory.title"));
        assertTrue(config.contains("RESULT-TITLES.defeat.title"));
    }

    private FfaManager createFfaManager(YamlConfiguration ffaConfig) throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor<?> pluginConstructor = reflectionFactory.newConstructorForSerialization(UltimateDonutSmp2.class, objectConstructor);
        UltimateDonutSmp2 plugin = (UltimateDonutSmp2) pluginConstructor.newInstance();

        ConfigManager configManager = new ConfigManager(plugin);
        Field ffaField = ConfigManager.class.getDeclaredField("ffa");
        ffaField.setAccessible(true);
        ffaField.set(configManager, ffaConfig);

        Field cmField = UltimateDonutSmp2.class.getDeclaredField("configManager");
        cmField.setAccessible(true);
        cmField.set(plugin, configManager);

        Field descField = JavaPlugin.class.getDeclaredField("description");
        descField.setAccessible(true);
        PluginDescriptionFile pdf = new PluginDescriptionFile("UltimateDonutSmp2", "1.0", "com.bx.ultimateDonutSmp2.UltimateDonutSmp2");
        descField.set(plugin, pdf);

        Constructor<?> ffaManagerConstructor = reflectionFactory.newConstructorForSerialization(FfaManager.class, objectConstructor);
        FfaManager manager = (FfaManager) ffaManagerConstructor.newInstance();

        Field pluginField = FfaManager.class.getDeclaredField("plugin");
        pluginField.setAccessible(true);
        pluginField.set(manager, plugin);

        return manager;
    }
}
