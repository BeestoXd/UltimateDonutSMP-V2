package com.bx.ultimateDonutSmp2.commands;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.SpawnManager;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import com.bx.ultimateDonutSmp2.utils.PermissionUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private static final String PERMISSION = "ultimatedonutsmp2.command.setspawn";
    private static final String SETUP_PERMISSION = "ultimatedonutsmp2.admin.setup";

    private final UltimateDonutSmp2 plugin;

    public SetSpawnCommand(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can save spawn locations.");
            return true;
        }

        if (!PermissionUtils.has(player, PERMISSION) && !PermissionUtils.has(player, SETUP_PERMISSION)) {
            player.sendMessage(ColorUtils.toComponent("&cYou do not have permission to set spawn."));
            return true;
        }

        Location location = player.getLocation();
        SpawnManager.SetupLocationResult result = plugin.getSpawnManager().setSpawnLocation(location);
        if (!result.success()) {
            player.sendMessage(ColorUtils.toComponent("&cSpawn location could not be saved: &f" + result.message()));
            return true;
        }

        player.sendMessage(ColorUtils.toComponent("&aSpawn location saved. &7(World: &f" + (location.getWorld() == null ? "unknown" : location.getWorld().getName())
                + "&7, X: &f" + String.format("%.1f", location.getX())
                + "&7, Y: &f" + String.format("%.1f", location.getY())
                + "&7, Z: &f" + String.format("%.1f", location.getZ()) + "&7)"));
        return true;
    }
}
