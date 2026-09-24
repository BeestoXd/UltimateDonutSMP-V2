package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sun.reflect.ReflectionFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FakePlayerEnabledCacheProofTest {

    @Test
    void reloadMustHonourEnabledFalseAfterAPriorIsEnabledCall(@TempDir Path tempDir) throws Exception {
        TestFixture fixture = createFixture(tempDir, true, true);
        assertTrue(fixture.fakePlayerManager.isEnabled(),
                "control: fakeplayer must initially be reported as enabled");

        // Simulate admin disabling fakeplayer in staff-mode.yml
        fixture.staffConfig.set("FAKE-PLAYER.ENABLED", false);

        // StaffModeCommand reloads FakePlayerManager via fakePlayerManager.reload()
        fixture.fakePlayerManager.reload();

        assertFalse(
                fixture.fakePlayerManager.isEnabled(),
                "StaffModeCommand reloads FakePlayerManager after staff-mode.yml changes; "
                        + "FAKE-PLAYER.ENABLED false must stick even if isEnabled() ran before reload"
        );
    }

    @Test
    void reloadMustHonourEnabledTrueAfterAPriorIsEnabledCall(@TempDir Path tempDir) throws Exception {
        TestFixture fixture = createFixture(tempDir, false, true);
        assertFalse(fixture.fakePlayerManager.isEnabled(),
                "control: fakeplayer must initially be reported as disabled");

        fixture.staffConfig.set("FAKE-PLAYER.ENABLED", true);
        fixture.fakePlayerManager.reload();

        assertTrue(
                fixture.fakePlayerManager.isEnabled(),
                "Reload must clear cachedEnabled and report true when re-enabled"
        );
    }

    @Test
    void featureToggleMustDropFakePlayerCache(@TempDir Path tempDir) throws Exception {
        TestFixture fixture = createFixture(tempDir, true, true);
        assertTrue(fixture.fakePlayerManager.isEnabled());

        // Disable STAFF_MODE feature via FeatureManager
        fixture.featureManager.setEnabled(FeatureManager.Feature.STAFF_MODE, false);

        assertFalse(
                fixture.fakePlayerManager.isEnabled(),
                "Disabling STAFF_MODE feature must invalidate fakeplayer cache and report disabled"
        );

        // Re-enable STAFF_MODE feature
        fixture.featureManager.setEnabled(FeatureManager.Feature.STAFF_MODE, true);

        assertTrue(
                fixture.fakePlayerManager.isEnabled(),
                "Re-enabling STAFF_MODE feature must invalidate fakeplayer cache and report enabled"
        );
    }

    @Test
    void shutdownMustClearCache(@TempDir Path tempDir) throws Exception {
        TestFixture fixture = createFixture(tempDir, true, true);
        assertTrue(fixture.fakePlayerManager.isEnabled());

        fixture.staffConfig.set("FAKE-PLAYER.ENABLED", false);
        fixture.fakePlayerManager.shutdown();

        assertFalse(
                fixture.fakePlayerManager.isEnabled(),
                "shutdown() must clear cache so isEnabled() does not return stale true"
        );
    }

    private static class TestFixture {
        UltimateDonutSmp2 plugin;
        ConfigManager configManager;
        FeatureManager featureManager;
        FakePlayerManager fakePlayerManager;
        YamlConfiguration staffConfig;
        YamlConfiguration mainConfig;
    }

    private TestFixture createFixture(Path tempDir, boolean fakePlayerEnabled, boolean staffModeFeatureEnabled) throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

        Constructor<?> pluginConstructor = reflectionFactory.newConstructorForSerialization(
                UltimateDonutSmp2.class, objectConstructor
        );
        UltimateDonutSmp2 plugin = (UltimateDonutSmp2) pluginConstructor.newInstance();

        setField(JavaPlugin.class, plugin, "dataFolder", tempDir.toFile());
        setField(JavaPlugin.class, plugin, "description",
                new org.bukkit.plugin.PluginDescriptionFile(
                        "UltimateDonutSmp2", "1.0", "com.bx.ultimateDonutSmp2.UltimateDonutSmp2"));

        ConfigManager configManager = new ConfigManager(plugin);
        YamlConfiguration staffConfig = new YamlConfiguration();
        staffConfig.set("FAKE-PLAYER.ENABLED", fakePlayerEnabled);

        YamlConfiguration mainConfig = new YamlConfiguration();
        mainConfig.set("FEATURES.STAFF_MODE.ENABLED", staffModeFeatureEnabled);

        Field staffModeField = ConfigManager.class.getDeclaredField("staffMode");
        staffModeField.setAccessible(true);
        staffModeField.set(configManager, staffConfig);

        Field configField = ConfigManager.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(configManager, mainConfig);

        Field cmField = UltimateDonutSmp2.class.getDeclaredField("configManager");
        cmField.setAccessible(true);
        cmField.set(plugin, configManager);

        FeatureManager featureManager = new FeatureManager(plugin);
        Field fmField = UltimateDonutSmp2.class.getDeclaredField("featureManager");
        fmField.setAccessible(true);
        fmField.set(plugin, featureManager);

        FakePlayerManager fakePlayerManager = new FakePlayerManager(plugin);
        Field fpmField = UltimateDonutSmp2.class.getDeclaredField("fakePlayerManager");
        fpmField.setAccessible(true);
        fpmField.set(plugin, fakePlayerManager);

        TestFixture fixture = new TestFixture();
        fixture.plugin = plugin;
        fixture.configManager = configManager;
        fixture.featureManager = featureManager;
        fixture.fakePlayerManager = fakePlayerManager;
        fixture.staffConfig = staffConfig;
        fixture.mainConfig = mainConfig;
        return fixture;
    }

    private static void setField(Class<?> clazz, Object target, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
