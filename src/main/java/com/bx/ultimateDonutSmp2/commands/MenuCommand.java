package com.bx.ultimateDonutSmp2.commands;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.dialogs.DialogManager;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Opens the root dialog.
 *
 * <p>Unlike the other dialog-backed commands this one has no chest menu behind it — the grid
 * only exists as a dialog — so a server that cannot render dialogs gets a plain explanation
 * instead of a broken command.
 */
public class MenuCommand implements CommandExecutor {

    private final UltimateDonutSmp2 plugin;

    public MenuCommand(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Player only.");
            return true;
        }

        DialogManager dialogs = plugin.getDialogManager();
        if (dialogs != null && dialogs.openMain(player)) {
            return true;
        }

        player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessageOrDefault(
                "MENU.UNAVAILABLE",
                "&cThis menu needs a Paper server running 1.21.6 or newer."
        ), player));
        return true;
    }
}
