package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.CurrencyManager;
import com.bx.ultimateDonutSmp2.managers.SpawnerManager;
import com.bx.ultimateDonutSmp2.models.SpawnerInstance;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import com.bx.ultimateDonutSmp2.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class SpawnerSellConfirmMenu extends BaseMenu {

    private final long spawnerId;
    private final int returnPage;

    public SpawnerSellConfirmMenu(UltimateDonutSmp2 plugin, long spawnerId, int returnPage) {
        super(plugin, "Confirm Sell", 27);
        this.spawnerId = spawnerId;
        this.returnPage = Math.max(1, returnPage);
    }

    @Override
    public void build(Player player) {
        SpawnerInstance instance = plugin.getSpawnerManager().getSpawner(spawnerId);
        if (instance == null) {
            inventory = Bukkit.createInventory(this, 27, ColorUtils.toComponent("&8Spawner Missing"));
            clear();
            set(13, ItemUtils.createItem(Material.BARRIER, "&cSpawner Not Found"));
            return;
        }

        FileConfiguration config = plugin.getConfigManager().getMenus();
        String titleStr = config.getString("SPAWNER-MENUS.SELL-CONFIRM-MENU.TITLE", "Confirm Sell");
        int menuSize = plugin.getSpawnerManager().normalizeSize(config.getInt("SPAWNER-MENUS.SELL-CONFIRM-MENU.SIZE", 27));
        inventory = Bukkit.createInventory(this, menuSize, ColorUtils.toComponent(titleStr));

        clear();
        // Keep non-button slots as empty AIR, matching reference design in confirm_sell.png

        SpawnerManager.SpawnerSellPreview preview = plugin.getSpawnerManager().calculateLootSellPreview(player, instance);
        String formattedPrice = plugin.getCurrencyManager().formatCompactAmount(CurrencyManager.CurrencyType.MONEY, preview.totalPayout());

        // 1. Cancel Button (Slot 11) - Red Stained Glass Pane
        int cancelSlot = config.getInt("SPAWNER-MENUS.SELL-CONFIRM-MENU.CANCEL-BUTTON.SLOT", 11);
        String cancelMatName = config.getString("SPAWNER-MENUS.SELL-CONFIRM-MENU.CANCEL-BUTTON.MATERIAL", "RED_STAINED_GLASS_PANE");
        Material cancelMat = Material.matchMaterial(cancelMatName);
        if (cancelMat == null) cancelMat = Material.RED_STAINED_GLASS_PANE;
        String cancelTitle = config.getString("SPAWNER-MENUS.SELL-CONFIRM-MENU.CANCEL-BUTTON.TITLE", "&cCancel");

        List<String> cancelLore = config.getStringList("SPAWNER-MENUS.SELL-CONFIRM-MENU.CANCEL-BUTTON.LORE");
        if (cancelLore.isEmpty()) {
            cancelLore = List.of("&7Click to cancel");
        }
        set(cancelSlot, ItemUtils.createItem(cancelMat, cancelTitle, cancelLore));

        // 2. Info / Mob Head Icon (Slot 13)
        int infoSlot = config.getInt("SPAWNER-MENUS.SELL-CONFIRM-MENU.INFO-ICON.SLOT", 13);
        set(infoSlot, SpawnerStorageMenu.createSpawnerMobHead(plugin, instance));

        // 3. Confirm Button (Slot 15) - Lime Stained Glass Pane
        int confirmSlot = config.getInt("SPAWNER-MENUS.SELL-CONFIRM-MENU.CONFIRM-BUTTON.SLOT", 15);
        String confirmMatName = config.getString("SPAWNER-MENUS.SELL-CONFIRM-MENU.CONFIRM-BUTTON.MATERIAL", "LIME_STAINED_GLASS_PANE");
        Material confirmMat = Material.matchMaterial(confirmMatName);
        if (confirmMat == null) confirmMat = Material.LIME_STAINED_GLASS_PANE;

        String confirmTitleFormat = config.getString("SPAWNER-MENUS.SELL-CONFIRM-MENU.CONFIRM-BUTTON.TITLE", "&a$ &f{price}");
        String confirmTitle = confirmTitleFormat.replace("{price}", formattedPrice);

        List<String> rawConfirmLore = config.getStringList("SPAWNER-MENUS.SELL-CONFIRM-MENU.CONFIRM-BUTTON.LORE");
        List<String> confirmLore = new ArrayList<>();
        if (rawConfirmLore.isEmpty()) {
            confirmLore.add("&7Click to sell items");
        } else {
            for (String line : rawConfirmLore) {
                confirmLore.add(line.replace("{price}", formattedPrice));
            }
        }
        set(confirmSlot, ItemUtils.createItem(confirmMat, confirmTitle, confirmLore));
    }

    @Override
    public void handleClick(int slot, Player player, ClickType clickType) {
        SpawnerInstance instance = plugin.getSpawnerManager().getSpawner(spawnerId);
        if (instance == null) {
            player.closeInventory();
            return;
        }

        FileConfiguration config = plugin.getConfigManager().getMenus();
        int cancelSlot = config.getInt("SPAWNER-MENUS.SELL-CONFIRM-MENU.CANCEL-BUTTON.SLOT", 11);
        int confirmSlot = config.getInt("SPAWNER-MENUS.SELL-CONFIRM-MENU.CONFIRM-BUTTON.SLOT", 15);

        if (slot == cancelSlot) {
            plugin.getSpawnerManager().playSellCancelSound(player);
            new SpawnerStorageMenu(plugin, spawnerId, returnPage).open(player);
            return;
        }

        if (slot == confirmSlot) {
            var sellResult = plugin.getSpawnerManager().sellAllLoot(player, instance);
            if (plugin.getSpawnerManager().isXpEnabled() && instance.getStoredXp() > 0) {
                plugin.getSpawnerManager().collectXp(player, instance);
            }
            player.sendMessage(ColorUtils.toComponent(sellResult.message()));
            new SpawnerStorageMenu(plugin, spawnerId, returnPage).open(player);
            return;
        }
    }
}
