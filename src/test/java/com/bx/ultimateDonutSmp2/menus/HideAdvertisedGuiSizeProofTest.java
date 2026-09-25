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
 * Config-hide.yml.md tables alias and skin picker chest sizes as live.
 * Those keys must be read and applied to the inventories.
 */
class HideAdvertisedGuiSizeProofTest {

    private static final Path HIDE_YAML = Path.of("src/main/resources/hide.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-hide.yml.md");
    private static final Path ALIAS_MENU = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/DisguiseAliasMenu.java");
    private static final Path SKIN_MENU = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/DisguiseSkinMenu.java");

    @Test
    void bundledYamlShipsTheAdvertisedChestLayoutKeys() throws Exception {
        YamlConfiguration hide = new YamlConfiguration();
        hide.load(HIDE_YAML.toFile());
        assertEquals(54, hide.getInt("GUI.ALIASES.SIZE"));
        assertEquals(54, hide.getInt("GUI.SKINS.SIZE"));
    }

    @Test
    void wikiTablesThoseKeysAsLiveChestLayout() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(wiki.contains("`GUI.ALIASES.SIZE`"), "Config-hide.yml.md tables GUI.ALIASES.SIZE as the alias picker chest size");
        assertTrue(wiki.contains("`GUI.SKINS.SIZE`"), "Config-hide.yml.md tables GUI.SKINS.SIZE as the skin picker chest size");
    }

    @Test
    void aliasAndSkinMenusMustReadConfiguredSize() throws Exception {
        String aliasSource = Files.readString(ALIAS_MENU, StandardCharsets.UTF_8);
        assertTrue(aliasSource.contains("GUI.ALIASES.SIZE"), "DisguiseAliasMenu must read GUI.ALIASES.SIZE from hide.yml");
        assertFalse(aliasSource.contains(", 54);"), "DisguiseAliasMenu must not pass hardcoded 54 to BaseMenu");

        String skinSource = Files.readString(SKIN_MENU, StandardCharsets.UTF_8);
        assertTrue(skinSource.contains("GUI.SKINS.SIZE"), "DisguiseSkinMenu must read GUI.SKINS.SIZE from hide.yml");
        assertFalse(skinSource.contains(", 54);"), "DisguiseSkinMenu must not pass hardcoded 54 to BaseMenu");
    }

    @Test
    void getMenuSizeAppliesConfiguredValues() throws Exception {
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        ConfigManager configManager = new ConfigManager(plugin);
        YamlConfiguration hide = new YamlConfiguration();
        hide.set("GUI.ALIASES.SIZE", 27);
        hide.set("GUI.SKINS.SIZE", 36);
        set(ConfigManager.class, configManager, "hide", hide);
        set(UltimateDonutSmp2.class, plugin, "configManager", configManager);

        assertEquals(27, DisguiseAliasMenu.getMenuSize(plugin));
        assertEquals(36, DisguiseSkinMenu.getMenuSize(plugin));
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
