package com.bx.ultimateDonutSmp2.listeners;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PortalListener implements Listener {

    private final UltimateDonutSmp2 plugin;

    public PortalListener(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getFeatureManager().isEnabled(com.bx.ultimateDonutSmp2.managers.FeatureManager.Feature.PORTALS)) {
            return;
        }
        Location to = event.getTo();
        if (to == null) {
            return;
        }

        boolean teleported = event instanceof PlayerTeleportEvent;
        if (!teleported
                && event.getFrom().getBlockX() == to.getBlockX()
                && event.getFrom().getBlockY() == to.getBlockY()
                && event.getFrom().getBlockZ() == to.getBlockZ()) {
            return;
        }

        plugin.getPortalManager().handlePlayerMovement(event.getPlayer(), event.getFrom(), to, teleported);
    }
}
