package com.bx.ultimateDonutSmp2.listeners;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import com.bx.ultimateDonutSmp2.utils.PermissionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerMoveListener implements Listener {

    private final UltimateDonutSmp2 plugin;

    public PlayerMoveListener(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getFrom().getX() != event.getTo().getX()
                || event.getFrom().getY() != event.getTo().getY()
                || event.getFrom().getZ() != event.getTo().getZ()
                || event.getFrom().getYaw() != event.getTo().getYaw()
                || event.getFrom().getPitch() != event.getTo().getPitch()) {
            plugin.getAFKManager().recordMovement(player.getUniqueId());
        }

        // Only care about block-level movement for flight checks and cuboids
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()) {
            return;
        }

        // Check flight restrictions for player-fly
        if (player.getAllowFlight()
                && player.getGameMode() != org.bukkit.GameMode.CREATIVE
                && player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {

            if (plugin.getConfigManager().getConfig().getBoolean("FLY-SYSTEM.AUTO-DISABLE-OUTSIDE", true)) {
                if (!PermissionUtils.has(player, "ultimatedonutsmp2.staff.fly")) {
                    String playerFlyPerm = plugin.getConfigManager().getConfig()
                            .getString("FLY-SYSTEM.PLAYER-FLY-PERMISSION", "ultimatedonutsmp2.player.fly");

                    if (PermissionUtils.has(player, playerFlyPerm)) {
                        boolean inSpawn = plugin.getAFKManager().isInSpawnCuboid(player);
                        boolean inCuboid = plugin.getCuboidManager().isInAnyCuboid(player);

                        boolean inCombat = plugin.getCombatManager() != null && plugin.getCombatManager().isInCombat(player.getUniqueId());

                        if ((!inSpawn && !inCuboid) || inCombat) {
                            player.setFlying(false);
                            player.setAllowFlight(false);
                            String msg = plugin.getConfigManager().getMessageOrDefault(
                                    "FLY.PLAYER_DISABLED",
                                    "&c✗ &7Flight deactivated because you left the allowed area or entered combat."
                            );
                            player.sendMessage(ColorUtils.toComponent(msg));
                        }
                    } else {
                        // Player doesn't have the player flight permission, disable flight
                        player.setFlying(false);
                        player.setAllowFlight(false);
                    }
                }
            }
        }

        // Reset AFK timer
        plugin.getAFKManager().recordMovement(player.getUniqueId());
        plugin.getShardManager().recordMovement(
                player.getUniqueId(),
                event.getFrom(),
                event.getTo(),
                event instanceof PlayerTeleportEvent
        );

        int fromChunkX = event.getFrom().getBlockX() >> 4;
        int fromChunkZ = event.getFrom().getBlockZ() >> 4;
        int toChunkX = event.getTo().getBlockX() >> 4;
        int toChunkZ = event.getTo().getBlockZ() >> 4;
        if ((fromChunkX != toChunkX || fromChunkZ != toChunkZ)
                && plugin.getMovementChunkWarmup() != null
                && event.getTo().getWorld() != null) {
            plugin.getMovementChunkWarmup().preloadAhead(
                    event.getTo().getWorld(), fromChunkX, fromChunkZ, toChunkX, toChunkZ);
        }

        // Check pending teleport
        if (plugin.getTeleportManager().hasPending(player.getUniqueId())) {
            plugin.getTeleportManager().checkMovement(player);
        }
    }
}
