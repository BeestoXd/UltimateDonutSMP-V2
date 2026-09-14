package com.bx.ultimateDonutSmp2.dialogs;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bridges Paper's custom-click event onto {@link DialogManager}.
 *
 * <p>Only registered when {@link DialogSupport#isAvailable()}, so the Paper event class is never
 * named on a server that does not ship it.
 */
public final class DialogListener implements Listener {

    private final UltimateDonutSmp2 plugin;
    private final DialogManager manager;

    public DialogListener(UltimateDonutSmp2 plugin, DialogManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCustomClick(PlayerCustomClickEvent event) {
        if (!(event.getCommonConnection() instanceof PlayerGameConnection connection)) {
            return;
        }
        Player player = connection.getPlayer();
        if (player == null) {
            return;
        }
        // The event object is only valid for the duration of the call, so read what we need out
        // of it before hopping onto the player's own region — which is where the click has to be
        // handled on Folia, since it touches player data and opens the next screen.
        Key identifier = event.getIdentifier();
        DialogResponseView response = event.getDialogResponseView();
        plugin.getSpigotScheduler().runEntity(player, () -> {
            if (player.isOnline()) {
                manager.handle(player, identifier, response);
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        manager.forget(event.getPlayer().getUniqueId());
    }
}
