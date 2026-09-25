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

class InsertItemAdvertisedSlotsProofTest {

    private static final Path SHOP_YAML = Path.of("src/main/resources/shop.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-shop.yml.md");
    private static final Path MENU_SOURCE = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/QuickBuyInsertItemMenu.java");

    @Test
    void bundledYamlShipsTheAdvertisedButtonSlots() throws Exception {
        YamlConfiguration shop = new YamlConfiguration();
        shop.load(SHOP_YAML.toFile());
        assertEquals(0, shop.getInt("INSERT-ITEM.CANCEL-BUTTON.SLOT"));
        assertEquals(4, shop.getInt("INSERT-ITEM.CONFIRM-BUTTON.SLOT"));
    }

    @Test
    void wikiTablesTheKeysAsLiveHopperCells() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(wiki.contains("`INSERT-ITEM.CANCEL-BUTTON.SLOT`"),
                "Config-shop.yml.md tables INSERT-ITEM.CANCEL-BUTTON.SLOT as the hopper cancel cell");
        assertTrue(wiki.contains("`INSERT-ITEM.CONFIRM-BUTTON.SLOT`"),
                "Config-shop.yml.md tables INSERT-ITEM.CONFIRM-BUTTON.SLOT as the hopper confirm cell");
    }

    @Test
    void insertItemClicksMustUseConfiguredButtonSlots() throws Exception {
        String source = Files.readString(MENU_SOURCE, StandardCharsets.UTF_8);
        assertTrue(
                source.contains("getCancelSlot()") || (source.contains("INSERT-ITEM.CANCEL-BUTTON.SLOT") && source.indexOf("INSERT-ITEM.CANCEL-BUTTON.SLOT") != source.lastIndexOf("INSERT-ITEM.CANCEL-BUTTON.SLOT")),
                "QuickBuyInsertItemMenu click handling must use the configured cancel button slot"
        );
        assertTrue(
                source.contains("getConfirmSlot()") || (source.contains("INSERT-ITEM.CONFIRM-BUTTON.SLOT") && source.indexOf("INSERT-ITEM.CONFIRM-BUTTON.SLOT") != source.lastIndexOf("INSERT-ITEM.CONFIRM-BUTTON.SLOT")),
                "QuickBuyInsertItemMenu click handling must use the configured confirm button slot"
        );
        assertFalse(
                source.contains("if (rawSlot == CANCEL_SLOT)"),
                "QuickBuyInsertItemMenu click handling must not compare rawSlot against hardcoded CANCEL_SLOT"
        );
        assertFalse(
                source.contains("if (rawSlot == CONFIRM_SLOT)"),
                "QuickBuyInsertItemMenu click handling must not compare rawSlot against hardcoded CONFIRM_SLOT"
        );
    }

    @Test
    void getButtonSlotsAppliesConfiguredValues() throws Exception {
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        ConfigManager configManager = new ConfigManager(plugin);
        YamlConfiguration shop = new YamlConfiguration();
        shop.set("INSERT-ITEM.CANCEL-BUTTON.SLOT", 1);
        shop.set("INSERT-ITEM.CONFIRM-BUTTON.SLOT", 3);
        set(ConfigManager.class, configManager, "shop", shop);
        set(UltimateDonutSmp2.class, plugin, "configManager", configManager);

        QuickBuyInsertItemMenu menu = allocate(QuickBuyInsertItemMenu.class);
        set(BaseMenu.class, menu, "plugin", plugin);

        assertEquals(1, menu.getCancelSlot());
        assertEquals(3, menu.getConfirmSlot());
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
