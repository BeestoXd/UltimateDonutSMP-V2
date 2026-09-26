package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TablistAdvertisedKeysProofTest {

    @Test
    void luckpermsPriorityAndIconHeadSkinMustHaveJavaReaders() throws IOException {
        File tablistManagerFile = new File("src/main/java/com/bx/ultimateDonutSmp2/managers/TablistManager.java");
        assertTrue(tablistManagerFile.exists(), "TablistManager.java must exist");

        String javaSource = Files.readString(tablistManagerFile.toPath());
        assertTrue(
                javaSource.contains("TABLIST.LUCKPERMS-PRIORITY") && javaSource.contains("TABLIST.ICON-HEAD-SKIN"),
                "Config-config.yml.md tables LUCKPERMS-PRIORITY and ICON-HEAD-SKIN as live TABLIST keys"
        );
    }

    @Test
    void tablistManagerExposesAdvertisedKeyDefaults() throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

        Constructor<?> pluginConstructor = reflectionFactory
                .newConstructorForSerialization(UltimateDonutSmp2.class, objectConstructor);
        UltimateDonutSmp2 plugin = (UltimateDonutSmp2) pluginConstructor.newInstance();

        YamlConfiguration mainConfig = new YamlConfiguration();
        mainConfig.load(new File("src/main/resources/config.yml"));

        ConfigManager configManager = new ConfigManager(plugin);
        Field configField = ConfigManager.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(configManager, mainConfig);

        Field configManagerField = UltimateDonutSmp2.class.getDeclaredField("configManager");
        configManagerField.setAccessible(true);
        configManagerField.set(plugin, configManager);

        TablistManager manager = new TablistManager(plugin);

        assertTrue(manager.isLuckpermsPriority());
        assertEquals("<head:%player_name%>", manager.getIconHeadSkin());

        Method usesConfiguredSkinHeadMethod = TablistManager.class.getDeclaredMethod("usesConfiguredSkinHead", String.class);
        usesConfiguredSkinHeadMethod.setAccessible(true);
        assertTrue((Boolean) usesConfiguredSkinHeadMethod.invoke(manager, "<icon_head_skin>&fPlayer"));
    }
}
