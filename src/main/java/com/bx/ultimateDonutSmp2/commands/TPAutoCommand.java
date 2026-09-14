package com.bx.ultimateDonutSmp2.commands;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.models.PlayerData;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAutoCommand implements CommandExecutor {

    private final UltimateDonutSmp2 plugin;

    public TPAutoCommand(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Player only."); return true; }
        PlayerData data = plugin.getPlayerDataManager().get(player);
        if (data == null) return true;
        data.setTpauto(!data.isTpauto());
        if (data.isTpauto()) {
            plugin.getTPAManager().processQueuedAutoRequests(player.getUniqueId());
        }
        String msg = data.isTpauto()
                ? plugin.getConfigManager().getMessage("TPAUTO.ENABLED")
                : plugin.getConfigManager().getMessage("TPAUTO.DISABLED");
        player.sendMessage(ColorUtils.toComponent(msg));
        return true;
    }
}
