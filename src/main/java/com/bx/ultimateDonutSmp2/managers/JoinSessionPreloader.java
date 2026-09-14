package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.models.FollowEntry;
import com.bx.ultimateDonutSmp2.models.HideState;
import com.bx.ultimateDonutSmp2.models.Home;
import com.bx.ultimateDonutSmp2.models.IgnoreEntry;
import com.bx.ultimateDonutSmp2.models.PlayerData;
import com.bx.ultimateDonutSmp2.models.PunishmentRecord;
import com.bx.ultimateDonutSmp2.models.PunishmentType;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Loads everything a join needs before the player reaches the main thread.
 *
 * <p>Paper keeps {@code AsyncPlayerPreLoginEvent} off the tick loop and waits for it to finish
 * before {@code PlayerJoinEvent}. That is the window where SQLite and punishment history belong.
 * Join then only applies the snapshot, so other players do not see a ping spike while one person
 * is logging in after a restart.</p>
 */
public final class JoinSessionPreloader {

    public record Snapshot(
            PlayerData playerData,
            List<Home> homes,
            List<IgnoreEntry> ignores,
            List<FollowEntry> following,
            List<FollowEntry> followers,
            HideState hideState,
            Location maintenanceLocation,
            PunishmentRecord blacklist,
            PunishmentRecord ban,
            PunishmentRecord voiceMute
    ) {
        public Snapshot {
            homes = homes == null ? null : List.copyOf(homes);
            ignores = ignores == null ? List.of() : List.copyOf(ignores);
            following = following == null ? List.of() : List.copyOf(following);
            followers = followers == null ? List.of() : List.copyOf(followers);
        }

        public List<Home> homesForCache() {
            return homes == null ? null : new ArrayList<>(homes);
        }
    }

    private final UltimateDonutSmp2 plugin;
    private final ConcurrentHashMap<UUID, Snapshot> pending = new ConcurrentHashMap<>();

    public JoinSessionPreloader(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    public Snapshot preload(UUID uuid, String name) {
        if (uuid == null) {
            return null;
        }

        try {
            Snapshot snapshot = loadSnapshot(uuid, name);
            pending.put(uuid, snapshot);
            return snapshot;
        } catch (RuntimeException exception) {
            if (plugin != null) {
                plugin.getLogger().log(Level.WARNING, "Failed to preload join data for " + uuid, exception);
            }
            pending.remove(uuid);
            return null;
        }
    }

    public Snapshot peek(UUID uuid) {
        return uuid == null ? null : pending.get(uuid);
    }

    public Snapshot consume(UUID uuid) {
        return uuid == null ? null : pending.remove(uuid);
    }

    public void discard(UUID uuid) {
        if (uuid != null) {
            pending.remove(uuid);
        }
    }

    public void store(UUID uuid, Snapshot snapshot) {
        if (uuid != null && snapshot != null) {
            pending.put(uuid, snapshot);
        }
    }

    public void clear() {
        pending.clear();
    }

    public boolean isPending(UUID uuid) {
        return uuid != null && pending.containsKey(uuid);
    }

    private Snapshot loadSnapshot(UUID uuid, String name) {
        DatabaseManager database = plugin.getDatabaseManager();
        PlayerData playerData = database == null ? null : database.loadPlayer(uuid);
        List<Home> homes = database == null ? null : database.loadHomes(uuid);
        List<IgnoreEntry> ignores = database == null ? List.of() : database.loadIgnoredPlayers(uuid);
        List<FollowEntry> following = database == null ? List.of() : database.loadFollowsByFollower(uuid);
        List<FollowEntry> followers = database == null ? List.of() : database.loadFollowsByFollowed(uuid);
        HideState hideState = database == null ? null : database.loadHideState(uuid);
        Location maintenanceLocation = loadMaintenanceLocation(database, uuid);

        PunishmentManager punishments = plugin.getPunishmentManager();
        PunishmentRecord blacklist = activePunishment(punishments, uuid, name, PunishmentType.BLACKLIST);
        PunishmentRecord ban = activePunishment(punishments, uuid, name, PunishmentType.BAN);
        PunishmentRecord voiceMute = activePunishment(punishments, uuid, name, PunishmentType.VOICE_MUTE);

        return new Snapshot(
                playerData,
                homes,
                ignores,
                following,
                followers,
                hideState,
                maintenanceLocation,
                blacklist,
                ban,
                voiceMute
        );
    }

    private Location loadMaintenanceLocation(DatabaseManager database, UUID uuid) {
        if (database == null || plugin.getConfigManager() == null || plugin.getConfigManager().getNetwork() == null) {
            return null;
        }
        String localServerId = plugin.getConfigManager().getNetwork()
                .getString("NETWORK.LOCAL_SERVER_ID", "local");
        return database.getMaintenanceLocation(uuid, localServerId);
    }

    private static PunishmentRecord activePunishment(
            PunishmentManager punishments,
            UUID uuid,
            String name,
            PunishmentType type
    ) {
        if (punishments == null || type == null) {
            return null;
        }
        return punishments.getActiveRecord(uuid, name, type).orElse(null);
    }
}
