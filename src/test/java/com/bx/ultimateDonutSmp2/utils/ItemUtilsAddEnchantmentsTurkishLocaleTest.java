package com.bx.ultimateDonutSmp2.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ItemUtilsAddEnchantmentsTurkishLocaleTest {

    @Test
    void addEnchantmentsWithUppercaseInfinityDoesNotThrowUnderTurkishLocale() throws Exception {
        Locale previous = Locale.getDefault();
        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        Server previousServer = (Server) serverField.get(null);

        try {
            ItemMeta dummyMeta = (ItemMeta) Proxy.newProxyInstance(
                    ItemMeta.class.getClassLoader(),
                    new Class<?>[]{ItemMeta.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("clone")) {
                            return proxy;
                        }
                        if (method.getReturnType().equals(boolean.class)) {
                            return false;
                        }
                        return null;
                    }
            );

            ItemFactory dummyFactory = (ItemFactory) Proxy.newProxyInstance(
                    ItemFactory.class.getClassLoader(),
                    new Class<?>[]{ItemFactory.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("getItemMeta")) {
                            return dummyMeta;
                        }
                        if (method.getName().equals("isApplicable")) {
                            return true;
                        }
                        if (method.getName().equals("asMetaFor")) {
                            return dummyMeta;
                        }
                        return null;
                    }
            );

            Server dummyServer = (Server) Proxy.newProxyInstance(
                    Server.class.getClassLoader(),
                    new Class<?>[]{Server.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("getItemFactory")) {
                            return dummyFactory;
                        }
                        if (method.getName().equals("getRegistry")) {
                            Class<?> registryClass = Class.forName("org.bukkit.Registry");
                            return Proxy.newProxyInstance(
                                    registryClass.getClassLoader(),
                                    new Class<?>[]{registryClass},
                                    (rProxy, rMethod, rArgs) -> null
                            );
                        }
                        return null;
                    }
            );

            serverField.set(null, dummyServer);
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            ItemStack item = new ItemStack(Material.BOW);
            assertDoesNotThrow(
                    () -> ItemUtils.addEnchantments(item, List.of("INFINITY:1")),
                    "addEnchantments uses toLowerCase() with default locale, causing invalid NamespacedKey on Turkish JVM"
            );
        } finally {
            serverField.set(null, previousServer);
            Locale.setDefault(previous);
        }
    }
}
