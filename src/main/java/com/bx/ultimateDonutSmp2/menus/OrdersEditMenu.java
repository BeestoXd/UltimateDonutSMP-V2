package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.OrdersManager;
import com.bx.ultimateDonutSmp2.models.Order;
import com.bx.ultimateDonutSmp2.models.OrderBatchClaimResult;
import com.bx.ultimateDonutSmp2.models.OrderCollectionClaim;
import com.bx.ultimateDonutSmp2.models.OrderSort;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import com.bx.ultimateDonutSmp2.utils.PlayerSettingUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class OrdersEditMenu extends BaseMenu {

    private final long orderId;
    private final boolean backToMyOrders;
    private final int originPage;
    private final OrderSort sortMode;
    private final String categoryFilter;

    public OrdersEditMenu(
            UltimateDonutSmp2 plugin,
            long orderId,
            boolean backToMyOrders,
            int originPage,
            OrderSort sortMode,
            String categoryFilter
    ) {
        super(plugin, plugin.getOrdersManager().getEditOrderTitle(orderId), plugin.getOrdersManager().getEditOrderSize());
        this.orderId = orderId;
        this.backToMyOrders = backToMyOrders;
        this.originPage = Math.max(1, originPage);
        this.sortMode = sortMode == null ? plugin.getOrdersManager().getDefaultSort() : sortMode;
        this.categoryFilter = categoryFilter == null ? "ALL" : categoryFilter;
    }

    @Override
    public void build(Player player) {
        clear();

        OrdersManager manager = plugin.getOrdersManager();
        Order order = manager.getOrder(orderId);
        if (order == null) {
            return;
        }

        int itemSlot = OrdersMenuSupport.slot(plugin, "GUI.EDIT_ORDER.BUTTONS.ITEM.SLOT", 10);
        int cancelSlot = OrdersMenuSupport.slot(plugin, "GUI.EDIT_ORDER.BUTTONS.CANCEL.SLOT", 13);
        int collectSlot = OrdersMenuSupport.slot(plugin, "GUI.EDIT_ORDER.BUTTONS.COLLECT.SLOT", 15);

        set(itemSlot, OrdersMenuSupport.createOrderDisplay(plugin, manager, order, true));
        set(cancelSlot, OrdersMenuSupport.button(
                plugin, "GUI.EDIT_ORDER.BUTTONS.CANCEL", "ORDERS.GUI.EDIT_ORDER.CANCEL",
                Material.RED_TERRACOTTA, "&fCancel", List.of("&7&oClick to cancel this order")
        ));
        set(collectSlot, OrdersMenuSupport.button(
                plugin, "GUI.EDIT_ORDER.BUTTONS.COLLECT", "ORDERS.GUI.EDIT_ORDER.COLLECT",
                Material.CHEST, "&fCollect", List.of("&7&oClick to collect items")
        ));
    }

    @Override
    public void handleClick(int slot, Player player, ClickType clickType) {
        Order order = plugin.getOrdersManager().getOrder(orderId);
        if (order == null) {
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessageOrDefault(
                    "ORDERS.ORDER_NOT_FOUND",
                    "&cThat order no longer exists."
            )));
            return;
        }

        int cancelSlot = OrdersMenuSupport.slot(plugin, "GUI.EDIT_ORDER.BUTTONS.CANCEL.SLOT", 13);
        int collectSlot = OrdersMenuSupport.slot(plugin, "GUI.EDIT_ORDER.BUTTONS.COLLECT.SLOT", 15);

        if (slot == collectSlot) {
            OrdersMenuSupport.click(player, plugin);
            collect(player);
            return;
        }
        if (slot == cancelSlot && order.active() && order.ownerUuid().equals(player.getUniqueId())) {
            OrdersMenuSupport.click(player, plugin);
            new OrdersDeleteConfirmMenu(
                    plugin, order.id(), backToMyOrders, originPage, sortMode, categoryFilter
            ).open(player);
        }
    }

    private void collect(Player player) {
        OrdersManager manager = plugin.getOrdersManager();
        String empty = plugin.getConfigManager().getMessageOrDefault(
                "ORDERS.NO_ITEMS_TO_COLLECT",
                "&cYou have no items to collect"
        );
        if (!manager.isClaimsEnabled()) {
            PlayerSettingUtils.sendActionBar(plugin, player, empty);
            OrdersMenuSupport.play(player, plugin, "ORDERS.FAIL");
            return;
        }
        List<Long> itemClaimIds = manager.getUnclaimedClaims(player.getUniqueId(), orderId).stream()
                .filter(OrderCollectionClaim::itemClaim)
                .map(OrderCollectionClaim::id)
                .toList();
        if (itemClaimIds.isEmpty()) {
            PlayerSettingUtils.sendActionBar(plugin, player, empty);
            OrdersMenuSupport.play(player, plugin, "ORDERS.FAIL");
            return;
        }
        if (!manager.beginAction(player.getUniqueId())) {
            return;
        }
        try {
            OrderBatchClaimResult result = manager.claimBatch(player, itemClaimIds, false);
            if (result.itemClaims() <= 0) {
                PlayerSettingUtils.sendActionBar(plugin, player, empty);
                OrdersMenuSupport.play(player, plugin, "ORDERS.FAIL");
                return;
            }
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessageOrDefault(
                    "ORDERS.BATCH_COLLECTED",
                    "&aCollected {claims} claims ({items} items, {refund} refund). &c{failed} failed.",
                    "{claims}", String.valueOf(result.itemClaims()),
                    "{items}", String.valueOf(result.itemAmount()),
                    "{refund}", plugin.getCurrencyManager().formatMoney(0D),
                    "{failed}", String.valueOf(result.failedClaims())
            )));
            OrdersMenuSupport.play(player, plugin, "ORDERS.COLLECT");
            openAfterCollect(player);
        } finally {
            manager.endAction(player.getUniqueId());
        }
    }

    private void openAfterCollect(Player player) {
        if (plugin.getOrdersManager().hasUnclaimedItemClaims(player.getUniqueId(), orderId)) {
            new OrdersEditMenu(plugin, orderId, backToMyOrders, originPage, sortMode, categoryFilter).open(player);
            return;
        }
        new OrdersMyOrdersMenu(plugin, 1, sortMode).open(player);
    }
}
