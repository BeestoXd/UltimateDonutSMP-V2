package com.bx.ultimateDonutSmp2.dialogs.screens;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.dialogs.DialogActions;
import com.bx.ultimateDonutSmp2.dialogs.DialogConfig;
import com.bx.ultimateDonutSmp2.dialogs.DialogFactory;
import com.bx.ultimateDonutSmp2.dialogs.DialogScreen;
import com.bx.ultimateDonutSmp2.dialogs.DialogSession;
import com.bx.ultimateDonutSmp2.dialogs.DialogSupport;
import com.bx.ultimateDonutSmp2.menus.OrdersBrowseMenu;
import io.papermc.paper.dialog.DialogResponseView;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/** The root grid every other dialog hangs off. */
public final class MainDialog extends DialogScreen {

    private static final String PATH = "DONUT_SMP_DIALOG";

    public MainDialog(UltimateDonutSmp2 plugin, DialogConfig config, DialogSession.Store sessions) {
        super(plugin, config, sessions);
    }

    public boolean open(Player player) {
        Map<String, String> tokens = DialogConfig.tokens("player", player.getName());
        DialogFactory.Screen screen = screen(player)
                .title(config.string(PATH + ".TITLE", "UltimateDonutSmp V2", tokens))
                .externalTitle(config.string(PATH + ".EXTERNAL-TITLE", null, tokens))
                .canCloseWithEscape(config.bool(PATH + ".CAN-CLOSE-WITH-ESCAPE", true))
                .pause(config.bool(PATH + ".PAUSE", false))
                .afterAction(config.string(PATH + ".AFTER-ACTION", "NONE"))
                .columns(config.columns(PATH, 2))
                .text(config.string(PATH + ".TEXT", null, tokens))
                .item(config.string(PATH + ".ITEM", null))
                .buttons(config.buttonsOf(PATH, tokens));
        return show(player, screen.build());
    }

    public void openOrders(Player player) {
        click(player);
        DialogSupport.close(player);
        if (plugin.getMenuNavigationTracker() != null) {
            plugin.getMenuNavigationTracker().clear(player);
        }
        plugin.getSpigotScheduler().runEntityLater(player, () -> {
            if (!player.isOnline()) {
                return;
            }
            if (plugin.getOrdersBedrockManager() != null && plugin.getOrdersBedrockManager().openMain(player)) {
                if (plugin.getOrdersManager() != null) {
                    plugin.getOrdersManager().playSound(player, "ORDERS.OPEN");
                }
                return;
            }
            if (plugin.getOrdersManager() != null && plugin.getOrdersManager().isEnabled()) {
                plugin.getOrdersManager().playSound(player, "ORDERS.OPEN");
                new OrdersBrowseMenu(plugin, 1, plugin.getOrdersManager().getDefaultSort(), "ALL").open(player);
            } else {
                plugin.getSpigotScheduler().dispatchPlayerCommand(player, "orders");
            }
        }, 1L);
    }

    /**
     * Runs a COMMAND button clicked on the generated pause-screen copy of this grid. That file is
     * static, so the button carries its position and the command is read back out of the config
     * here — which also means editing dialog.yml changes what the ESC button does without
     * regenerating the datapack.
     */
    private void runConfiguredCommand(Player player, String rawIndex) {
        int index;
        try {
            index = Integer.parseInt(rawIndex.trim());
        } catch (NumberFormatException ignored) {
            open(player);
            return;
        }

        List<Map<?, ?>> buttons = plugin.getConfigManager().getDialog().getMapList(PATH + ".BUTTONS");
        if (index < 0 || index >= buttons.size()) {
            open(player);
            return;
        }
        Object command = buttons.get(index).get("COMMAND");
        if (command == null || String.valueOf(command).isBlank()) {
            open(player);
            return;
        }

        String cmdStr = String.valueOf(command).trim();
        String trimmedCmd = cmdStr.startsWith("/") ? cmdStr.substring(1) : cmdStr;
        if (trimmedCmd.equalsIgnoreCase("orders")) {
            openOrders(player);
            return;
        }

        click(player);
        DialogSupport.close(player);
        plugin.getSpigotScheduler().runEntityLater(player, () -> {
            if (player.isOnline()) {
                plugin.getSpigotScheduler().dispatchPlayerCommand(player, trimmedCmd);
            }
        }, 1L);
    }

    @Override
    public boolean handle(Player player, String action, DialogResponseView response) {
        if (DialogActions.MAIN_MENU.equals(action)) {
            open(player);
            return true;
        }
        if (DialogActions.CLOSE.equals(action)) {
            DialogSupport.close(player);
            return true;
        }
        if (DialogActions.ORDERS.equals(action) || DialogActions.ORDERS_MENU.equals(action)) {
            openOrders(player);
            return true;
        }

        String commandIndex = DialogActions.argument(action, DialogActions.MAIN_COMMAND);
        if (commandIndex != null) {
            runConfiguredCommand(player, commandIndex);
            return true;
        }
        return false;
    }
}
