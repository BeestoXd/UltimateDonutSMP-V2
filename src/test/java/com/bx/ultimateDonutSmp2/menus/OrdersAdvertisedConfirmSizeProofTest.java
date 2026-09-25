package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdersAdvertisedConfirmSizeProofTest {

    private static final Path DELIVER_MENU = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/OrdersDeliverConfirmMenu.java");
    private static final Path CANCEL_MENU = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/OrdersDeleteConfirmMenu.java");
    private static final Path ORDERS_MANAGER = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/OrdersManager.java");
    private static final Path ORDERS_YAML = Path.of(
            "src/main/resources/orders.yml");

    @Test
    void deliverConfirmAndCancelOrderMustUseYamlSize() throws Exception {
        String deliverSource = Files.readString(DELIVER_MENU);
        assertTrue(
                deliverSource.contains("getDeliverSize"),
                "Config-orders.yml.md lists SIZE as required on every GUI entry including DELIVER_CONFIRM"
        );
        assertFalse(
                deliverSource.contains("OrdersMenuSupport.text(plugin, \"ORDERS.GUI.CONFIRM.TITLE\", \"&8Orders -> Confirm delivery\"),\n                27"),
                "OrdersDeliverConfirmMenu must not pass hardcoded 27 to BaseMenu"
        );

        String cancelSource = Files.readString(CANCEL_MENU);
        assertTrue(
                cancelSource.contains("getCancelOrderSize"),
                "Config-orders.yml.md lists SIZE as required on every GUI entry including CANCEL_ORDER"
        );
        assertFalse(
                cancelSource.contains("super(plugin, OrdersMenuSupport.text(plugin, \"ORDERS.GUI.DELETE.TITLE\", \"Orders -> Cancel Order\"), 27);"),
                "OrdersDeleteConfirmMenu must not pass hardcoded 27 to BaseMenu"
        );

        String managerSource = Files.readString(ORDERS_MANAGER);
        assertTrue(managerSource.contains("GUI.DELIVER_CONFIRM.SIZE"), "OrdersManager must read GUI.DELIVER_CONFIRM.SIZE");
        assertTrue(managerSource.contains("GUI.CANCEL_ORDER.SIZE"), "OrdersManager must read GUI.CANCEL_ORDER.SIZE");

        YamlConfiguration config = new YamlConfiguration();
        config.load(ORDERS_YAML.toFile());
        assertEquals(27, config.getInt("GUI.DELIVER_CONFIRM.SIZE"));
        assertEquals(27, config.getInt("GUI.CANCEL_ORDER.SIZE"));
    }
}
