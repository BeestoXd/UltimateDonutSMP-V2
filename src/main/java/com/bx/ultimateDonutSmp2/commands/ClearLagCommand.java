package com.bx.ultimateDonutSmp2.commands;

import com.bx.ultimateDonutSmp2.utils.PermissionUtils;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClearLagCommand implements CommandExecutor {

    private final UltimateDonutSmp2 plugin;

    public ClearLagCommand(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!PermissionUtils.has(sender, "ultimatedonutsmp2.admin.clearlag")) {
            sender.sendMessage(ColorUtils.toComponent("&cNo permission."));
            return true;
        }
        plugin.getClearLagManager().clearEntities();
        return true;
    }
}
