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
 * Config-shop.yml.md tables YOUR-ITEMS.SIZE, YOUR-ITEMS.PLUS-PAGE-SLOTS, and YOUR-ITEMS.SELL-BUTTON.SLOT
 * as live layout. Those keys must be read and applied.
 */
class ShopYourItemsAdvertisedProofTest {

    private static final Path SHOP_YAML = Path.of("src/main/resources/shop.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-shop.yml.md");
    private static final Path YOUR_ITEMS_MENU = Path.of("src/main/java/com/bx/ultimateDonutSmp2/menus/AuctionYourItemsMenu.java");

    @Test
    void bundledYamlShipsTheAdvertisedChestLayoutKeys() throws Exception {
        YamlConfiguration shop = new YamlConfiguration();
        shop.load(SHOP_YAML.toFile());
        assertEquals(54, shop.getInt("YOUR-ITEMS.SIZE"));
        assertEquals(45, shop.getInt("YOUR-ITEMS.PLUS-PAGE-SLOTS"));
        assertEquals(0, shop.getInt("YOUR-ITEMS.SELL-BUTTON.SLOT"));
    }

    @Test
    void wikiTablesThoseKeysAsLiveChestLayout() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(wiki.contains("`YOUR-ITEMS.SIZE`"), "Config-shop.yml.md tables YOUR-ITEMS.SIZE as the Your Items chest size");
        assertTrue(wiki.contains("`YOUR-ITEMS.PLUS-PAGE-SLOTS`"), "Config-shop.yml.md tables YOUR-ITEMS.PLUS-PAGE-SLOTS as the page-2 capacity");
        assertTrue(wiki.contains("`YOUR-ITEMS.SELL-BUTTON.SLOT`"), "Config-shop.yml.md tables YOUR-ITEMS.SELL-BUTTON.SLOT as the sell button slot");
    }

    @Test
    void sizePlusPageAndSellButtonSlotMustHaveJavaReaders() throws Exception {
        String source = Files.readString(YOUR_ITEMS_MENU, StandardCharsets.UTF_8);
        assertTrue(source.contains("YOUR-ITEMS.SIZE"), "Config-shop.yml.md tables YOUR-ITEMS.SIZE as the Your Items chest size");
        assertTrue(source.contains("YOUR-ITEMS.PLUS-PAGE-SLOTS"), "Config-shop.yml.md tables YOUR-ITEMS.PLUS-PAGE-SLOTS as the page-2 capacity");
        assertTrue(source.contains("YOUR-ITEMS.SELL-BUTTON.SLOT"), "Config-shop.yml.md tables YOUR-ITEMS.SELL-BUTTON.SLOT as the sell button slot");
        assertFalse(source.contains("plusPageSlots = GRID_SLOTS;"), "AuctionYourItemsMenu must not hardcode plusPageSlots to GRID_SLOTS");
        assertFalse(source.contains("super(plugin, plugin.getConfigManager().getShop().getString(\"YOUR-ITEMS.TITLE\", \"Auction -> Your Items\"), 54);"),
                "AuctionYourItemsMenu must not pass hardcoded 54 to BaseMenu");
    }

    @Test
    void getMenuSizeAppliesConfiguredValues() throws Exception {
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        ConfigManager configManager = new ConfigManager(plugin);
        YamlConfiguration shop = new YamlConfiguration();
        shop.set("YOUR-ITEMS.SIZE", 27);
        set(ConfigManager.class, configManager, "shop", shop);
        set(UltimateDonutSmp2.class, plugin, "configManager", configManager);

        assertEquals(27, AuctionYourItemsMenu.getMenuSize(plugin));
    }

    @Test
    void getPlusPageSlotsAppliesConfiguredValues() throws Exception {
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        ConfigManager configManager = new ConfigManager(plugin);
        YamlConfiguration shop = new YamlConfiguration();
        shop.set("YOUR-ITEMS.PLUS-PAGE-SLOTS", 18);
        set(ConfigManager.class, configManager, "shop", shop);
        set(UltimateDonutSmp2.class, plugin, "configManager", configManager);

        assertEquals(18, AuctionYourItemsMenu.getPlusPageSlots(plugin));
    }

    @Test
    void getSellButtonSlotAppliesConfiguredValues() throws Exception {
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        ConfigManager configManager = new ConfigManager(plugin);
        YamlConfiguration shop = new YamlConfiguration();
        shop.set("YOUR-ITEMS.SELL-BUTTON.SLOT", 3);
        set(ConfigManager.class, configManager, "shop", shop);
        set(UltimateDonutSmp2.class, plugin, "configManager", configManager);

        assertEquals(3, AuctionYourItemsMenu.getSellButtonSlot(plugin));
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
