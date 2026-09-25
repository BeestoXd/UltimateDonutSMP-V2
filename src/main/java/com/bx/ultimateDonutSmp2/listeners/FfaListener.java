package com.bx.ultimateDonutSmp2.listeners;

import com.bx.ultimateDonutSmp2.utils.PermissionUtils;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;

import com.bx.ultimateDonutSmp2.utils.CommandLabelUtils;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class FfaListener implements Listener {

    private static final Set<String> ALLOWED_FFA_COMMANDS = Set.of("/ffa", "/leave", "/ffastats");

    private final UltimateDonutSmp2 plugin;

    public FfaListener(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker = resolveAttacker(event);

        if (event.getEntity() instanceof Player victim) {
            UUID victimUuid = victim.getUniqueId();
            if (attacker != null
                    && plugin.getFfaManager().isActiveOpponentPair(attacker.getUniqueId(), victimUuid)) {
                event.setCancelled(false);
                plugin.getFfaManager().refreshCombatTag(attacker, victim);
                return;
            }

            if (attacker != null && plugin.getFfaManager().tryStartCombatPair(attacker, victim)) {
                event.setCancelled(false);
                plugin.getFfaManager().refreshCombatTag(attacker, victim);
                return;
            }

            boolean attackerProtected = attacker != null
                    && (plugin.getFfaManager().isInSession(attacker.getUniqueId())
                    || plugin.getFfaManager().isCombatLocked(attacker.getUniqueId()));
            boolean victimProtected = plugin.getFfaManager().isInSession(victimUuid)
                    || plugin.getFfaManager().isCombatLocked(victimUuid);
            if (attackerProtected || victimProtected) {
                event.setCancelled(true);
            }
            return;
        }

        boolean attackerProtected = attacker != null
                && (plugin.getFfaManager().isInSession(attacker.getUniqueId())
                || plugin.getFfaManager().isCombatLocked(attacker.getUniqueId()));
        if (attackerProtected) {
            event.setCancelled(true);
            return;
        }

        if (attacker != null
                && plugin.getFfaManager().isInMatch(attacker.getUniqueId())
                && !plugin.getFfaManager().canModifyArena(attacker)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL
                && plugin.getFfaManager().hasArenaSetting(uuid, com.bx.ultimateDonutSmp2.managers.FfaManager.ArenaSetting.NO_FALL_DAMAGE)) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getFfaManager().isInQueue(uuid) || plugin.getFfaManager().isTransitioning(uuid)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        if (!plugin.getFfaManager().hasArenaSetting(uuid, com.bx.ultimateDonutSmp2.managers.FfaManager.ArenaSetting.NO_HUNGER)) {
            return;
        }

        event.setCancelled(true);
        if (player.getFoodLevel() < 20) {
            player.setFoodLevel(20);
        }
        if (player.getSaturation() < 20F) {
            player.setSaturation(20F);
        }
        player.setExhaustion(0F);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (plugin.getFfaManager().isInQueue(uuid)
                || plugin.getFfaManager().isTransitioning(uuid)
                || (plugin.getFfaManager().isInMatch(uuid) && !plugin.getFfaManager().canModifyArena(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (plugin.getFfaManager().isInQueue(uuid)
                || plugin.getFfaManager().isTransitioning(uuid)
                || (plugin.getFfaManager().isInMatch(uuid) && !plugin.getFfaManager().canModifyArena(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (plugin.getFfaManager().isInQueue(uuid)
                || plugin.getFfaManager().isTransitioning(uuid)
                || (plugin.getFfaManager().isInMatch(uuid) && !plugin.getFfaManager().canModifyArena(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (plugin.getFfaManager().isInQueue(uuid)
                || plugin.getFfaManager().isTransitioning(uuid)
                || (plugin.getFfaManager().isInMatch(uuid) && !plugin.getFfaManager().canModifyArena(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (plugin.getFfaManager().isInQueue(uuid)
                || plugin.getFfaManager().isInMatch(uuid)
                || plugin.getFfaManager().isTransitioning(uuid)) {
            event.setCancelled(true);
            plugin.getSpigotScheduler().runEntity(player, () -> {
                if (player.isOnline()) {
                    player.updateInventory();
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        if (plugin.getFfaManager().isInQueue(uuid)
                || plugin.getFfaManager().isInMatch(uuid)
                || plugin.getFfaManager().isTransitioning(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.getFfaManager().shouldBlockCommands()) {
            return;
        }

        if (PermissionUtils.has(event.getPlayer(), "ultimatedonutsmp2.admin.ffa")) {
            return;
        }

        UUID uuid = event.getPlayer().getUniqueId();
        if (!plugin.getFfaManager().isInQueue(uuid)
                && !plugin.getFfaManager().isInMatch(uuid)
                && !plugin.getFfaManager().isTransitioning(uuid)) {
            return;
        }

        if (isAllowedFfaCommand(event.getMessage())) {
            return;
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage(ColorUtils.toComponent("&cYou cannot use that command during FFA."));
    }

    static boolean isAllowedFfaCommand(String raw) {
        if (raw == null) {
            return false;
        }
        String normalized = withoutPluginNamespace(raw);
        for (String allowed : ALLOWED_FFA_COMMANDS) {
            if (normalized.equals(allowed) || normalized.startsWith(allowed + " ")) {
                return true;
            }
        }
        return false;
    }

    private static String withoutPluginNamespace(String raw) {
        if (raw == null) {
            return "";
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        int space = value.indexOf(' ');
        String token = space < 0 ? value : value.substring(0, space);
        String rest = space < 0 ? "" : value.substring(space);

        boolean slash = token.startsWith("/");
        String body = slash ? token.substring(1) : token;
        if (body.isEmpty()) {
            return value;
        }
        String label = CommandLabelUtils.normalizeLabel(body);
        if (label.isEmpty()) {
            return value;
        }
        return (slash ? "/" : "") + label + rest;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        if (plugin.getFfaManager() != null) {
            plugin.getFfaManager().refreshArenaAvailability();
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (plugin.getFfaManager() != null) {
            plugin.getFfaManager().refreshArenaAvailability();
        }
    }

    private Player resolveAttacker(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            return player;
        }

        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {
            return player;
        }

        return null;
    }

}
