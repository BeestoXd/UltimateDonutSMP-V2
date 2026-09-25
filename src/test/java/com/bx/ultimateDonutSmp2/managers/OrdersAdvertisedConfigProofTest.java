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
}
