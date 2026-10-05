package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.models.SellCategory;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuickBuySellArbitrageFloorTest {

    private ShopManager shopManager;
    private WorthManager worthManager;

    @BeforeEach
    void setup() throws Exception {
        YamlConfiguration worthConfig = new YamlConfiguration();
        worthConfig.set("TYPE.ORES.NETHERITE_BLOCK", 225_000.0);

        YamlConfiguration menus = new YamlConfiguration();
        menus.set("PROGRESS-MENU.ENABLED", true);
        menus.set("PROGRESS-MENU.LEVEL", List.of(
                25_000L, 150_000L, 500_000L, 1_000_000L, 5_000_000L, 25_000_000L
        ));

        Constructor<Object> objectConstructor = Object.class.getConstructor();
        sun.reflect.ReflectionFactory reflectionFactory = sun.reflect.ReflectionFactory.getReflectionFactory();
        Constructor<?> pluginConstructor = reflectionFactory.newConstructorForSerialization(UltimateDonutSmp2.class, objectConstructor);
        UltimateDonutSmp2 plugin = (UltimateDonutSmp2) pluginConstructor.newInstance();

        Field descField = JavaPlugin.class.getDeclaredField("description");
        descField.setAccessible(true);
        descField.set(plugin, new PluginDescriptionFile("UltimateDonutSmp2", "1.0", "com.bx.ultimateDonutSmp2.UltimateDonutSmp2"));

        ConfigManager configManager = new ConfigManager(plugin);
        setField(configManager, "worth", worthConfig);
        setField(configManager, "menus", menus);
        setField(plugin, "configManager", configManager);

        worthManager = new WorthManager(plugin);
        setField(plugin, "worthManager", worthManager);

        Constructor<?> shopManagerConstructor = reflectionFactory.newConstructorForSerialization(ShopManager.class, objectConstructor);
        shopManager = (ShopManager) shopManagerConstructor.newInstance();
        setField(shopManager, "plugin", plugin);
    }

    @Test
    void minimumQuickBuyUnitPriceUsesBaseWorthWithoutBuyer() {
        double floor = shopManager.minimumQuickBuyUnitPrice(null, new ItemStack(Material.NETHERITE_BLOCK));
        assertEquals(225_000.0, floor, 0.001);
    }

    @Test
    void oreSellMultiplierAtIssueReportLevelRaisesBuyFloorToSellPayout() {
        Map<SellCategory, Double> progress = Map.of(SellCategory.ORES, 30_000_000.0);
        double multiplier = shopManager.getCurrentSellMultiplier(progress, SellCategory.ORES);
        assertEquals(1.6, multiplier, 0.001);

        double worth = worthManager.resolveWorth(new ItemStack(Material.NETHERITE_BLOCK)).unitWorth();
        assertEquals(360_000.0, worth * multiplier, 0.001);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
