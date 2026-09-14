package com.bx.ultimateDonutSmp2.listeners;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.SpawnManager;
import com.bx.ultimateDonutSmp2.utils.NightVisionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    private final UltimateDonutSmp2 plugin;

    public PlayerRespawnListener(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String deathWorldName = player.getWorld() == null ? null : player.getWorld().getName();
        boolean respawnRtpAllowed = false;

        boolean duelRespawnHandled = plugin.getDuelManager() != null
                && plugin.getDuelManager().consumeRespawn(player, event);
        boolean ffaRespawnHandled = !duelRespawnHandled
                && plugin.getFfaManager() != null
                && plugin.getFfaManager().consumeRespawn(player, event);

        if (!duelRespawnHandled && !ffaRespawnHandled) {
            Location pearlLocation = plugin.getEnderPearlManager() != null
                    ? plugin.getEnderPearlManager().consumePendingTeleport(player.getUniqueId())
                    : null;
            if (pearlLocation != null) {
                Location finalPearlLocation = pearlLocation.clone();
                event.setRespawnLocation(finalPearlLocation);

                plugin.getSpigotScheduler().runGlobalLater(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    plugin.getSpigotScheduler().teleport(player, finalPearlLocation).thenAccept(success -> {
                        plugin.getSpigotScheduler().runEntity(player, () -> {
                            if (player.isOnline()) {
                                NightVisionUtils.restoreIfEnabled(plugin, player);
                            }
                        });
                    });
                }, 1L);
                return;
            }

            boolean respawnOnBed = plugin.getConfigManager().getConfig().getBoolean("SETTINGS.RESPAWN-ON-BED", false);
            respawnRtpAllowed = !respawnOnBed || !isRespawningAtBedOrAnchor(event, player);
            if (respawnRtpAllowed) {
                Location respawnLocation = plugin.getSpawnManager().resolveCommandDestination(SpawnManager.AreaType.SPAWN);
                if (respawnLocation == null) {
                    respawnLocation = plugin.getSpawnManager().getSpawnLocation();
                }
                if (respawnLocation == null) {
                    respawnLocation = plugin.getSpawnManager().makeSafeDestination(event.getRespawnLocation());
                }
                if (respawnLocation != null) {
                    Location finalRespawnLocation = respawnLocation.clone();
                    event.setRespawnLocation(finalRespawnLocation);

                    plugin.getSpigotScheduler().runGlobalLater(() -> {
                        if (!player.isOnline()) {
                            return;
                        }
                        plugin.getSpigotScheduler().teleport(player, finalRespawnLocation).thenAccept(success -> {
                            plugin.getSpigotScheduler().runEntity(player, () -> {
                                if (player.isOnline()) {
                                    NightVisionUtils.restoreIfEnabled(plugin, player);
                                    startRespawnRtp(player, deathWorldName);
                                }
                            });
                        });
                    }, 1L);
                    return;
                }
            }
        }

        boolean respawnRtpOnFallback = respawnRtpAllowed;
        plugin.getStaffModeManager().handleRespawn(player);
        plugin.getSpigotScheduler().runGlobalLater(() -> {
            if (player.isOnline()) {
                plugin.getSpigotScheduler().runEntity(player, () -> {
                    if (player.isOnline()) {
                        NightVisionUtils.restoreIfEnabled(plugin, player);
                        if (respawnRtpOnFallback) {
                            startRespawnRtp(player, deathWorldName);
                        }
                    }
                });
            }
        }, 2L);
    }

    private void startRespawnRtp(Player player, String deathWorldName) {
        if (plugin.getRespawnRtpManager() == null) {
            return;
        }
        plugin.getRespawnRtpManager().handleRespawn(player, deathWorldName);
    }

    private boolean shouldSnapToRespawnLocation(Location current, Location expected) {
        if (current == null || expected == null || current.getWorld() == null || expected.getWorld() == null) {
            return false;
        }
        if (!current.getWorld().equals(expected.getWorld())) {
            return true;
        }
        return current.distanceSquared(expected) > 0.36D;
    }

    private boolean isRespawningAtBedOrAnchor(PlayerRespawnEvent event, Player player) {
        if (event.isBedSpawn() || event.isAnchorSpawn()) {
            return true;
        }
        Location respawnLoc = event.getRespawnLocation();
        if (respawnLoc == null) {
            return false;
        }
        Location bedLoc = player.getBedSpawnLocation();
        if (bedLoc == null) {
            return false;
        }
        return respawnLoc.getWorld() != null &&
               respawnLoc.getWorld().equals(bedLoc.getWorld()) &&
               respawnLoc.distanceSquared(bedLoc) < 9.0D;
    }
}
