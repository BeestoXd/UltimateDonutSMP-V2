package com.bx.ultimateDonutSmp2.listeners;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.models.SpawnerInstance;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class SpawnerInteractListener implements Listener {

    private final UltimateDonutSmp2 plugin;

    public SpawnerInteractListener(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!plugin.getSpawnerManager().isEnabled()) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        SpawnerInstance instance = plugin.getSpawnerManager().getSpawner(block);
        if (instance == null) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();
        EquipmentSlot handUsed = EquipmentSlot.HAND;
        if (!plugin.getSpawnerManager().isSpawnerItem(held)) {
            ItemStack offHand = player.getInventory().getItemInOffHand();
            if (plugin.getSpawnerManager().isSpawnerItem(offHand) && (held == null || held.getType().isAir())) {
                held = offHand;
                handUsed = EquipmentSlot.OFF_HAND;
            }
        }
        event.setCancelled(true);

        if (plugin.getSpawnStashManager() != null && plugin.getSpawnStashManager().isActiveBlock(block)) {
            plugin.getSpawnStashManager().triggerBlockAlert(player, block, "open");
            if (!plugin.getSpawnerManager().canOpen(player, instance)) {
                player.sendMessage(ColorUtils.toComponent("&cYou do not have access to that spawner."));
                return;
            }

            plugin.getSpawnerManager().openStorage(player, instance, 1);
            return;
        }

        if (plugin.getSpawnerManager().isSpawnerItem(held)) {
            var result = plugin.getSpawnerManager().stackSpawner(player, block, held);
            player.sendMessage(ColorUtils.toComponent(result.message()));
            if (result.success()) {
                int consumed = result.consumedAmount() > 0 ? result.consumedAmount() : (player.isSneaking() ? held.getAmount() : 1);
                plugin.getSpawnerManager().consumeHeldSpawnerItem(player, handUsed, consumed);
                player.updateInventory();
                if (handUsed == EquipmentSlot.OFF_HAND) {
                    player.swingOffHand();
                } else {
                    player.swingMainHand();
                }
                plugin.getSpawnerManager().playStackSound(player, block);
            }
            return;
        }

        if (!plugin.getSpawnerManager().canOpen(player, instance)) {
            player.sendMessage(ColorUtils.toComponent("&cYou do not have access to that spawner."));
            return;
        }

        plugin.getSpawnerManager().openMainMenu(player, instance);
    }
}
