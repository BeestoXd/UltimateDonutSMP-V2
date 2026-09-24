package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureCommandMapProofTest {

    @Test
    void pvpCommandBelongsToThePvpArenaFeatureGroup() {
        FeatureManager.Feature[] features = FeatureManager.featuresForCommand("pvp");
        assertTrue(
                Arrays.asList(features).contains(FeatureManager.Feature.PVP_ARENA),
                "Wiki lists PVP_ARENA as a command feature group and UltimateDonutSmp2 wraps /pvp "
                        + "with FeatureCommandExecutor(PVP_ARENA). featuresForCommand must include it "
                        + "so UNREGISTER and tab suppression apply."
        );

        assertTrue(
                Arrays.asList(FeatureManager.featuresForCommand("PVP")).contains(FeatureManager.Feature.PVP_ARENA),
                "featuresForCommand must match case-insensitively"
        );
        assertTrue(
                Arrays.asList(FeatureManager.featuresForCommand(" pvp ")).contains(FeatureManager.Feature.PVP_ARENA),
                "featuresForCommand must trim surrounding whitespace"
        );
    }

    @Test
    void rtpQueueAliasBelongsToTheRtpFeatureGroup() {
        FeatureManager.Feature[] rtpqFeatures = FeatureManager.featuresForCommand("rtpq");
        assertTrue(
                Arrays.asList(rtpqFeatures).contains(FeatureManager.Feature.RTP),
                "plugin.yml registers /rtpq; setExecutor wraps it with Feature.RTP"
        );

        FeatureManager.Feature[] rtpqueueFeatures = FeatureManager.featuresForCommand("rtpqueue");
        assertTrue(
                Arrays.asList(rtpqueueFeatures).contains(FeatureManager.Feature.RTP),
                "plugin.yml declares rtpqueue alias for /rtpq; featuresForCommand must map it to Feature.RTP"
        );
    }

    @Test
    void controlsForRtpAndFfaRemainBound() {
        assertTrue(Arrays.asList(FeatureManager.featuresForCommand("rtp")).contains(FeatureManager.Feature.RTP));
        assertTrue(Arrays.asList(FeatureManager.featuresForCommand("ffa")).contains(FeatureManager.Feature.FFA));
        assertTrue(Arrays.asList(FeatureManager.featuresForCommand("ffastats")).contains(FeatureManager.Feature.FFA));
        assertTrue(Arrays.asList(FeatureManager.featuresForCommand("ffaarena")).contains(FeatureManager.Feature.FFA));
    }

    @Test
    void commandFeatureEnabledReflectsFeatureToggleState(@TempDir Path tempDir) throws Exception {
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
        YamlConfiguration mainConfig = new YamlConfiguration();
        mainConfig.set("FEATURES.PVP_ARENA.ENABLED", true);
        mainConfig.set("FEATURES.RTP.ENABLED", true);

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

        assertTrue(featureManager.isCommandFeatureEnabled("pvp"));
        assertTrue(featureManager.isCommandFeatureEnabled("rtpq"));

        // Disable PVP_ARENA
        featureManager.setEnabled(FeatureManager.Feature.PVP_ARENA, false);
        assertFalse(
                featureManager.isCommandFeatureEnabled("pvp"),
                "isCommandFeatureEnabled(\"pvp\") must return false when PVP_ARENA feature is disabled"
        );

        // Disable RTP
        featureManager.setEnabled(FeatureManager.Feature.RTP, false);
        assertFalse(
                featureManager.isCommandFeatureEnabled("rtpq"),
                "isCommandFeatureEnabled(\"rtpq\") must return false when RTP feature is disabled"
        );
    }

    private static void setField(Class<?> clazz, Object target, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
