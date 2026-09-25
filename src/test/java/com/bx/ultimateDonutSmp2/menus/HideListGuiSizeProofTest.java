package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Config-hide.yml.md tables GUI.LIST.SIZE as the hidden-players chest size.
 * That key must be read and applied to the inventory.
 */
class HideListGuiSizeProofTest {

    private static final Path HIDE_YAML = Path.of("src/main/resources/hide.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-hide.yml.md");
    private static final Path LIST_MENU = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/HideListMenu.java");

    @Test
    void bundledYamlShipsTheAdvertisedChestLayoutKey() throws Exception {
        YamlConfiguration hide = new YamlConfiguration();
        hide.load(HIDE_YAML.toFile());
        assertEquals(54, hide.getInt("GUI.LIST.SIZE"));
    }

    @Test
    void wikiTablesTheKeyAsLiveChestLayout() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(wiki.contains("`GUI.LIST.SIZE`"), "Config-hide.yml.md tables GUI.LIST.SIZE as the hidden-players chest size");
    }

    @Test
    void listMenuMustReadConfiguredSize() throws Exception {
        String listSource = Files.readString(LIST_MENU, StandardCharsets.UTF_8);
        assertTrue(listSource.contains("GUI.LIST.SIZE"), "HideListMenu must read GUI.LIST.SIZE from hide.yml");
        assertFalse(listSource.contains("super(plugin, title(plugin, page), 54);"), "HideListMenu must not pass hardcoded 54 to BaseMenu");
    }

    @Test
    void getMenuSizeAppliesConfiguredValues() throws Exception {
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        ConfigManager configManager = new ConfigManager(plugin);
        YamlConfiguration hide = new YamlConfiguration();
        hide.set("GUI.LIST.SIZE", 27);
        set(ConfigManager.class, configManager, "hide", hide);
        set(UltimateDonutSmp2.class, plugin, "configManager", configManager);

        assertEquals(27, HideListMenu.getMenuSize(plugin));
    }

    @SuppressWarnings("unchecked")
    private static <T> T allocate(Class<T> type) throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        Constructor<?> constructor = ReflectionFactory.getReflectionFactory()
                .newConstructorForSerialization(type, objectConstructor);
        return (T) constructor.newInstance();
    }

    private static void set(Class<?> owner, Object instance, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(instance, value);
    }
}
