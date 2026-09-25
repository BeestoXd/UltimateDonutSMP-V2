package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdersAdvertisedConfigProofTest {

    @Test
    void deliveryModeDirectMustBeALiveEnumValue() {
        boolean hasDirect = Arrays.stream(OrdersManager.DeliveryMode.values())
                .anyMatch(mode -> mode.name().equals("DIRECT"));
        assertTrue(
                hasDirect,
                "Economy-and-Marketplaces.md and Config-orders.yml.md say DELIVERY.MODE is DEPOSIT_GUI or DIRECT"
        );
        assertEquals(OrdersManager.DeliveryMode.DIRECT, OrdersManager.DeliveryMode.fromConfig("DIRECT"));
        assertEquals(OrdersManager.DeliveryMode.DIRECT, OrdersManager.DeliveryMode.fromConfig("direct"));
    }

    @Test
    void donutStyleMustNotBeAdvertisedOrPresentInBundledYaml() throws Exception {
        org.bukkit.configuration.file.YamlConfiguration config = new org.bukkit.configuration.file.YamlConfiguration();
        config.load(java.nio.file.Path.of("src/main/resources/orders.yml").toFile());
        org.junit.jupiter.api.Assertions.assertFalse(config.contains("DONUT-STYLE"), "orders.yml must not contain unread DONUT-STYLE");

        String wiki = java.nio.file.Files.readString(java.nio.file.Path.of("docs/wiki/Config-orders.yml.md"));
        org.junit.jupiter.api.Assertions.assertFalse(wiki.contains("DONUT-STYLE"), "Config-orders.yml.md must not table unread DONUT-STYLE");
    }
}
