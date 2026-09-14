package com.bx.ultimateDonutSmp2.utils;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Locale;

/**
 * Caps join warmup so boot and the first login do not load a view-distance square.
 *
 * <p>Paper logs {@code Loading 0 persistent chunks} when Multiverse leaves
 * {@code keep-spawn-in-memory: false}. A tiny already-generated logout square is loaded
 * during login; Paper streams the rest of view-distance after the player is in the world.</p>
 */
public final class JoinWarmupPolicy {

    public static final String ROOT = "JOIN-WARMUP";

    static final int DEFAULT_SPAWN_RADIUS = 10;
    static final int MIN_SPAWN_RADIUS = 2;
    static final int MAX_SPAWN_RADIUS = 10;
    static final int DEFAULT_CHUNKS_PER_TICK = 1;
    static final int MIN_CHUNKS_PER_TICK = 1;
    static final int MAX_CHUNKS_PER_TICK = 2;
    static final int DEFAULT_LOGOUT_RADIUS = 2;
    static final int MIN_LOGOUT_RADIUS = 1;
    static final int MAX_LOGOUT_RADIUS = 3;
    static final int DEFAULT_LOGOUT_WAIT_RADIUS = 2;
    static final int MIN_LOGOUT_WAIT_RADIUS = 1;
    static final int MAX_LOGOUT_WAIT_RADIUS = 2;
    static final int DEFAULT_STARTUP_LOGOUT_ANCHORS = 4;
    static final int MIN_STARTUP_LOGOUT_ANCHORS = 0;
    static final int MAX_STARTUP_LOGOUT_ANCHORS = 8;
    static final long DEFAULT_LOGOUT_WAIT_MILLIS = 1500L;
    static final long MIN_LOGOUT_WAIT_MILLIS = 250L;
    static final long MAX_LOGOUT_WAIT_MILLIS = 2500L;
    static final long DEFAULT_RELEASE_TICKETS_AFTER_EMPTY_TICKS = 200L;
    static final long MIN_RELEASE_TICKETS_AFTER_EMPTY_TICKS = 20L;
    static final long MAX_RELEASE_TICKETS_AFTER_EMPTY_TICKS = 1200L;

    private JoinWarmupPolicy() {
    }

    public static boolean enabled(ConfigurationSection config) {
        return config == null || config.getBoolean(ROOT + ".ENABLED", true);
    }

    public static boolean keepSpawnInMemory(ConfigurationSection config) {
        return config != null && config.getBoolean(ROOT + ".KEEP-SPAWN-IN-MEMORY", false);
    }

    public static boolean warmEntityApi(ConfigurationSection config) {
        return config == null || config.getBoolean(ROOT + ".WARM-ENTITY-API", true);
    }

    public static boolean preloadLogoutChunks(ConfigurationSection config) {
        return config == null || config.getBoolean(ROOT + ".PRELOAD-LOGOUT-CHUNKS", true);
    }

    /**
     * Keep deserialized warmup chunks with plugin tickets until a player is in the world.
     * Without this, Paper unloads the square during the idle gap after {@code Done}.
     */
    public static boolean pinLoadedChunks(ConfigurationSection config) {
        return config == null || config.getBoolean(ROOT + ".PIN-LOADED-CHUNKS", true);
    }

    public static long releaseTicketsAfterEmptyTicks(ConfigurationSection config) {
        long value = config == null
                ? DEFAULT_RELEASE_TICKETS_AFTER_EMPTY_TICKS
                : config.getLong(ROOT + ".RELEASE-TICKETS-AFTER-EMPTY-TICKS",
                DEFAULT_RELEASE_TICKETS_AFTER_EMPTY_TICKS);
        return Math.max(MIN_RELEASE_TICKETS_AFTER_EMPTY_TICKS,
                Math.min(MAX_RELEASE_TICKETS_AFTER_EMPTY_TICKS, value));
    }

    /**
     * Cap a configured radius by the world's view-distance. Never expand a small login
     * square up to view-distance: that loads hundreds of mob chunks and drops TPS.
     */
    public static int viewDistanceRadius(int worldViewDistance, int configuredRadius) {
        int configured = clamp(configuredRadius, MIN_LOGOUT_RADIUS, MAX_SPAWN_RADIUS);
        if (worldViewDistance <= 0) {
            return configured;
        }
        return clamp(Math.min(worldViewDistance, configured), MIN_LOGOUT_RADIUS, MAX_SPAWN_RADIUS);
    }

    public static int spawnChunkRadius(ConfigurationSection config) {
        int value = config == null
                ? DEFAULT_SPAWN_RADIUS
                : config.getInt(ROOT + ".SPAWN-CHUNK-RADIUS", DEFAULT_SPAWN_RADIUS);
        return clamp(value, MIN_SPAWN_RADIUS, MAX_SPAWN_RADIUS);
    }

    public static int chunksPerTick(ConfigurationSection config) {
        int value = config == null
                ? DEFAULT_CHUNKS_PER_TICK
                : config.getInt(ROOT + ".CHUNKS-PER-TICK", DEFAULT_CHUNKS_PER_TICK);
        return clamp(value, MIN_CHUNKS_PER_TICK, MAX_CHUNKS_PER_TICK);
    }

    public static int logoutChunkRadius(ConfigurationSection config) {
        int value = config == null
                ? DEFAULT_LOGOUT_RADIUS
                : config.getInt(ROOT + ".LOGOUT-CHUNK-RADIUS", DEFAULT_LOGOUT_RADIUS);
        return clamp(value, MIN_LOGOUT_RADIUS, MAX_LOGOUT_RADIUS);
    }

    /**
     * Radius that {@code AsyncPlayerPreLoginEvent} waits for. Kept smaller than the full
     * logout radius so the login thread is not held for a whole view-distance square.
     */
    public static int logoutWaitRadius(ConfigurationSection config) {
        int full = logoutChunkRadius(config);
        int value = config == null
                ? DEFAULT_LOGOUT_WAIT_RADIUS
                : config.getInt(ROOT + ".LOGOUT-WAIT-RADIUS", DEFAULT_LOGOUT_WAIT_RADIUS);
        return Math.min(full, clamp(value, MIN_LOGOUT_WAIT_RADIUS, MAX_LOGOUT_WAIT_RADIUS));
    }

    public static long logoutWaitMillis(ConfigurationSection config) {
        long value = config == null
                ? DEFAULT_LOGOUT_WAIT_MILLIS
                : config.getLong(ROOT + ".LOGOUT-WAIT-MILLIS", DEFAULT_LOGOUT_WAIT_MILLIS);
        return Math.max(MIN_LOGOUT_WAIT_MILLIS, Math.min(MAX_LOGOUT_WAIT_MILLIS, value));
    }

    /**
     * How many recently modified {@code player.dat} files to treat as extra warmup centres.
     * World spawn is often hundreds of blocks from where people actually log out.
     */
    public static int startupLogoutAnchors(ConfigurationSection config) {
        int value = config == null
                ? DEFAULT_STARTUP_LOGOUT_ANCHORS
                : config.getInt(ROOT + ".STARTUP-LOGOUT-ANCHORS", DEFAULT_STARTUP_LOGOUT_ANCHORS);
        return clamp(value, MIN_STARTUP_LOGOUT_ANCHORS, MAX_STARTUP_LOGOUT_ANCHORS);
    }

    public static boolean isOverworldDimension(String dimension) {
        if (dimension == null || dimension.isBlank()) {
            return true;
        }
        String key = dimension.toLowerCase(Locale.ROOT);
        return "minecraft:overworld".equals(key) || "overworld".equals(key);
    }

    /**
     * Overworld survival maps keep spawn loaded. Generated duel/FFA/lobby worlds stay cold
     * because those folders are created on demand and must not pin spawn chunks.
     */
    public static boolean shouldKeepSpawnLoaded(String worldName, String environmentName) {
        if (environmentName == null || !"NORMAL".equalsIgnoreCase(environmentName)) {
            return false;
        }
        if (worldName == null || worldName.isBlank()) {
            return false;
        }
        String key = worldName.toLowerCase(Locale.ROOT);
        return !key.contains("duel")
                && !key.contains("ffa")
                && !key.equals("lobby")
                && !key.contains("arena");
    }

    public static String chunkKey(String worldName, int chunkX, int chunkZ) {
        return worldName + ":" + chunkX + ":" + chunkZ;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
