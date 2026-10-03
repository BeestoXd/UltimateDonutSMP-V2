package com.bx.ultimateDonutSmp2.amethyst;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.FeatureManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AmethystToolsTask implements Runnable {

    private final UltimateDonutSmp2 plugin;
    private final AmethystToolsManager manager;

    private AmethystToolsTask(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
        this.manager = plugin.getAmethystToolsManager();
    }

    public static void start(UltimateDonutSmp2 plugin) {
        plugin.getSpigotScheduler().runGlobalTimer(new AmethystToolsTask(plugin), 20L, 20L); // every second
    }

    @Override
    public void run() {
        if (!plugin.getFeatureManager().isEnabled(FeatureManager.Feature.AMETHYST_TOOLS)) {
            return;
        }
        plugin.getSpigotScheduler().forEachOnlinePlayer(this::checkInventory);
    }

    static boolean isHeldSlot(int slot, int heldSlot) {
        return slot == heldSlot || slot == 40;
    }

    static boolean shouldUpdateSlotCountdown(int slot, int heldSlot, boolean countdownEnabled, boolean preventHeldAnimation) {
        if (!countdownEnabled) {
            return false;
        }
        if (preventHeldAnimation && isHeldSlot(slot, heldSlot)) {
            return false;
        }
        return true;
    }

    private void checkInventory(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        int heldSlot = player.getInventory().getHeldItemSlot();
        boolean countdownEnabled = manager.isCountdownEnabled();
        boolean preventHeldAnimation = manager.isPreventHeldToolAnimationEnabled();

        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
                continue;
            }

            if (!manager.isAmethystTool(item)) {
                continue;
            }

            if (manager.sanitizeInventorySlot(player, slot, true)) {
                continue;
            }

            if (countdownEnabled && !isHeldSlot(slot, heldSlot)) {
                if (manager.updateLoreCountdown(item)) {
                    player.getInventory().setItem(slot, item);
                }
            }
        }

        if (!countdownEnabled) {
            return;
        }

        if (manager.isVisualSyncSuppressed(player.getUniqueId())) {
            return;
        }

        if (!preventHeldAnimation) {
            syncHeldCountdown(player);
            syncOffHandCountdown(player);
        }
        syncCursorCountdown(player);
    }

    private void syncHeldCountdown(Player player) {
        int heldSlot = player.getInventory().getHeldItemSlot();
        if (manager.sanitizeInventorySlot(player, heldSlot, true)) {
            return;
        }

        ItemStack held = player.getInventory().getItem(heldSlot);
        if (manager.isAmethystTool(held) && manager.updateLoreCountdown(held)) {
            player.getInventory().setItem(heldSlot, held);
        }
    }

    private void syncOffHandCountdown(Player player) {
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (!manager.isAmethystTool(offHand)) {
            return;
        }

        if (manager.sanitizeInventorySlot(player, 40, true)) {
            return;
        }

        offHand = player.getInventory().getItemInOffHand();
        if (manager.isAmethystTool(offHand) && manager.updateLoreCountdown(offHand)) {
            player.getInventory().setItemInOffHand(offHand);
        }
    }

    private void syncCursorCountdown(Player player) {
        if (manager.sanitizeCursorItem(player, true)) {
            return;
        }

        ItemStack cursor = player.getItemOnCursor();
        if (manager.isAmethystTool(cursor) && manager.updateLoreCountdown(cursor)) {
            player.setItemOnCursor(cursor);
        }
    }
}
