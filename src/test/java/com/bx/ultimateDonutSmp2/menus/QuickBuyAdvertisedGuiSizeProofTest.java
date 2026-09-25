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
 * Config-shop.yml.md tables the main Quick Buy chest size as live.
 * That key must be read and applied to the inventory.
 */
class QuickBuyAdvertisedGuiSizeProofTest {

    private static final Path SHOP_YAML = Path.of("src/main/resources/shop.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-shop.yml.md");
    private static final Path QUICK_BUY_MENU = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/QuickBuyMenu.java");

    @Test
    void bundledYamlShipsTheAdvertisedChestLayoutKeys() throws Exception {
        YamlConfiguration shop = new YamlConfiguration();
        shop.load(SHOP_YAML.toFile());
        assertEquals(54, shop.getInt("QUICK-BUY.SIZE"));
    }

    @Test
    void wikiTablesThoseKeysAsLiveChestLayout() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(wiki.contains("`QUICK-BUY.SIZE`"), "Config-shop.yml.md tables QUICK-BUY.SIZE as the quick buy chest size");
    }

    @Test
    void quickBuyMenuMustReadConfiguredSize() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(wiki.contains("`QUICK-BUY.SIZE`"), "Config-shop.yml.md tables QUICK-BUY.SIZE as a live chest size");

        String source = Files.readString(QUICK_BUY_MENU, StandardCharsets.UTF_8);
        assertTrue(source.contains("QUICK-BUY.SIZE"), "QuickBuyMenu must read QUICK-BUY.SIZE from shop.yml");
        assertFalse(source.contains("super(plugin, plugin.getConfigManager().getShop().getString(\"QUICK-BUY.TITLE\", \"&8Quick Buy\"), 54);"),
                "QuickBuyMenu must not pass hardcoded 54 to BaseMenu");
    }

    @Test
    void getMenuSizeAppliesConfiguredValues() throws Exception {
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        ConfigManager configManager = new ConfigManager(plugin);
        YamlConfiguration shop = new YamlConfiguration();
        shop.set("QUICK-BUY.SIZE", 27);
        set(ConfigManager.class, configManager, "shop", shop);
        set(UltimateDonutSmp2.class, plugin, "configManager", configManager);

        assertEquals(27, QuickBuyMenu.getMenuSize(plugin));
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
