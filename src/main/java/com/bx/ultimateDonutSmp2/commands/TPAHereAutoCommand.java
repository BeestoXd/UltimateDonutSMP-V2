package com.bx.ultimateDonutSmp2.commands;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.models.PlayerData;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAHereAutoCommand implements CommandExecutor {

    private final UltimateDonutSmp2 plugin;

    public TPAHereAutoCommand(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Player only."); return true; }
        PlayerData data = plugin.getPlayerDataManager().get(player);
        if (data == null) return true;

        data.setAutoTpaHereEnabled(!data.isAutoTpaHereEnabled());
        if (data.isAutoTpaHereEnabled()) {
            plugin.getTPAManager().processQueuedAutoRequests(player.getUniqueId());
        }

        String msg = data.isAutoTpaHereEnabled()
                ? plugin.getConfigManager().getMessage("TPAHEREAUTO.ENABLED")
                : plugin.getConfigManager().getMessage("TPAHEREAUTO.DISABLED");
        player.sendMessage(ColorUtils.toComponent(msg));
        return true;
    }
}
