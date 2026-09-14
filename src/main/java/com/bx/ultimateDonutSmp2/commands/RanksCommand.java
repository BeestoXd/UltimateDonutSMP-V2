package com.bx.ultimateDonutSmp2.commands;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.menus.RanksMenu;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RanksCommand implements CommandExecutor {

    private final UltimateDonutSmp2 plugin;

    public RanksCommand(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Player only."); return true; }
        if (!plugin.getConfigManager().isCommandEnabled("RANKS")) {
            player.sendMessage(ColorUtils.toComponent("&cRanks command is currently disabled."));
            return true;
        }

        RanksMenu menu = new RanksMenu(plugin);
        if (!menu.hasValidButtons()) {
            player.sendMessage(ColorUtils.toComponent("&cThe ranks menu has no usable buttons configured."));
            return true;
        }

        menu.open(player);
        return true;
    }
}
