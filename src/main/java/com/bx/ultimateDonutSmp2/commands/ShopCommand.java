package com.bx.ultimateDonutSmp2.commands;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.menus.QuickBuyMenu;
import com.bx.ultimateDonutSmp2.menus.QuickBuySounds;
import com.bx.ultimateDonutSmp2.menus.ShardShopMenu;
import com.bx.ultimateDonutSmp2.menus.ShopMenu;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import com.bx.ultimateDonutSmp2.utils.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private final UltimateDonutSmp2 plugin;

    public ShopCommand(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            return reload(sender);
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtils.colorize(text(
                    "SHOP-GUI.MESSAGES.PLAYER-ONLY",
                    "&cOnly players can use this command."
            )));
            return true;
        }

        if (command.getName().equalsIgnoreCase("shardshop")) {
            ShardShopMenu.openShop(plugin, player);
            return true;
        }

        if (args.length == 0) {
            boolean quickBuyEnabled = plugin.getConfigManager().getShop().getBoolean("QUICK-BUY.ENABLED", true);
            if (quickBuyEnabled) {
                QuickBuySounds.play(player, plugin, QuickBuySounds.OPEN);
                new QuickBuyMenu(plugin).open(player);
            } else {
                new ShopMenu(plugin).open(player);
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("legacy")) {
            new ShopMenu(plugin).open(player);
            return true;
        }

        player.sendMessage(ColorUtils.toComponent(text(
                "SHOP-GUI.MESSAGES.USAGE",
                "&cUsage: /shop [reload]"
        )));
        return true;
    }

    private boolean reload(CommandSender sender) {
        if (!PermissionUtils.has(sender, "ultimatedonutsmp2.admin.shop")) {
            sender.sendMessage(ColorUtils.colorize(text(
                    "SHOP-GUI.MESSAGES.NO-RELOAD-PERMISSION",
                    "&cYou do not have permission to reload shop settings."
            )));
            return true;
        }

        plugin.getConfigManager().reloadShop();
        plugin.getConfigManager().reloadMenus();
        plugin.getConfigManager().reloadSounds();
        plugin.getShopManager().reload();
        sender.sendMessage(ColorUtils.colorize(text(
                "SHOP-GUI.MESSAGES.RELOADED",
                "&aShop configuration reloaded."
        )));
        return true;
    }

    private String text(String path, String fallback) {
        return plugin.getConfigManager().getShop().getString(path, fallback);
    }
}
