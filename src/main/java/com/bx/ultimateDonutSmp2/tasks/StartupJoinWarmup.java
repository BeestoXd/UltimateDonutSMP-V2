package com.bx.ultimateDonutSmp2.tasks;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.utils.JoinWarmupPolicy;
import com.bx.ultimateDonutSmp2.utils.PaperChunkLoader;
import com.bx.ultimateDonutSmp2.utils.PaperChunkTickets;
import com.bx.ultimateDonutSmp2.utils.PlayerLogoutLocationNbt;
import com.bx.ultimateDonutSmp2.utils.RtpChunkPreloadPolicy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * After boot, spawn chunks are often not in memory (Multiverse {@code keep-spawn-in-memory: false}
 * plus Paper {@code Loading 0 persistent chunks}). Loading terrain at startup ticks every mob in
 * those chunks and drops TPS while the server is still empty. Boot does not load chunks. A small
 * logout square is loaded during {@code AsyncPlayerPreLoginEvent}, a few chunks per tick. Paper
 * streams the rest of view-distance after the player is in the world.
 */
public final class StartupJoinWarmup {

    private final UltimateDonutSmp2 plugin;
    private final List<BukkitTask> loadTasks = new CopyOnWriteArrayList<>();
    private BukkitTask releaseTicketsTask;
    private volatile boolean entityApiWarmed;

    public StartupJoinWarmup(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    public void start() {
        FileConfiguration config = config();
        if (!JoinWarmupPolicy.enabled(config)) {
            return;
        }

        int logoutCount = collectRecentLogoutAnchors(config).size();
        if (logoutCount > 0) {
            plugin.getLogger().info("Join warmup: found " + logoutCount
                    + " recent logout location(s); chunks load at login, not at boot.");
        }
        plugin.getLogger().info("Join warmup ready (no chunk preload at boot).");
    }

    public void shutdown() {
        for (BukkitTask task : loadTasks) {
            task.cancel();
        }
        loadTasks.clear();
        cancelTicketRelease();
        releaseTicketsNow();
    }

    /**
     * Called from {@code AsyncPlayerPreLoginEvent} after the player is allowed in. Starts loading
     * the logout square off the tick thread, pins those chunks so they cannot unload, and waits
     * a short time for the inner radius so {@code placeNewPlayer} does not open those region files
     * itself.
     */
    public void preloadLogoutChunks(UUID uuid) {
        FileConfiguration config = config();
        if (!JoinWarmupPolicy.enabled(config) || !JoinWarmupPolicy.preloadLogoutChunks(config) || uuid == null) {
            return;
        }
        keepTickets();

        PlayerLogoutLocationNbt.LogoutLocation logout = findLogoutLocation(uuid);
        if (logout == null) {
            return;
        }
        World world = resolveWorld(logout.dimension());
        if (world == null) {
            return;
        }

        Location anchor = new Location(world, logout.x(), logout.y(), logout.z());
        int configured = JoinWarmupPolicy.logoutChunkRadius(config);
        int radius = JoinWarmupPolicy.viewDistanceRadius(worldViewDistance(world), configured);
        int waitRadius = Math.min(radius, JoinWarmupPolicy.logoutWaitRadius(config));
        boolean pin = JoinWarmupPolicy.pinLoadedChunks(config);
        List<ChunkTarget> waitTargets = uniqueChunks(List.of(anchor), waitRadius, pin);
        int perTick = JoinWarmupPolicy.chunksPerTick(config);
        if (Bukkit.isPrimaryThread()) {
            loadTargetsPaced(waitTargets, perTick);
            return;
        }
        try {
            loadTargetsPaced(waitTargets, perTick)
                    .get(JoinWarmupPolicy.logoutWaitMillis(config), TimeUnit.MILLISECONDS);
        } catch (Exception ignored) {
        }
    }

    public void keepTickets() {
        cancelTicketRelease();
    }

    public void releaseTicketsIfServerEmpty() {
        FileConfiguration config = config();
        if (!JoinWarmupPolicy.pinLoadedChunks(config)) {
            return;
        }
        cancelTicketRelease();
        long delay = JoinWarmupPolicy.releaseTicketsAfterEmptyTicks(config);
        releaseTicketsTask = plugin.getSpigotScheduler().runGlobalLater(() -> {
            releaseTicketsTask = null;
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                return;
            }
            releaseTicketsNow();
        }, delay);
    }

    private List<Location> collectRecentLogoutAnchors(FileConfiguration config) {
        int limit = JoinWarmupPolicy.startupLogoutAnchors(config);
        if (limit <= 0) {
            return List.of();
        }
        List<PlayerLogoutLocationNbt.DatedFile> files = new ArrayList<>();
        Set<Path> folders = new LinkedHashSet<>();
        for (World world : Bukkit.getWorlds()) {
            Path folder = world.getWorldFolder() == null ? null : world.getWorldFolder().toPath();
            folders.addAll(PlayerLogoutLocationNbt.playerDataFoldersForWorld(folder));
        }
        Path container = Bukkit.getWorldContainer() == null ? null : Bukkit.getWorldContainer().toPath();
        folders.addAll(PlayerLogoutLocationNbt.playerDataFoldersForWorld(container));
        for (Path playerData : folders) {
            files.addAll(PlayerLogoutLocationNbt.listPlayerData(playerData));
        }
        List<Location> logoutAnchors = new ArrayList<>();
        for (Path dat : PlayerLogoutLocationNbt.newestUniquePlayerData(files, limit)) {
            PlayerLogoutLocationNbt.LogoutLocation logout = PlayerLogoutLocationNbt.readFile(dat);
            if (logout == null || !JoinWarmupPolicy.isOverworldDimension(logout.dimension())) {
                continue;
            }
            World world = resolveWorld(logout.dimension());
            if (world == null || !JoinWarmupPolicy.shouldKeepSpawnLoaded(
                    world.getName(), world.getEnvironment().name())) {
                continue;
            }
            logoutAnchors.add(new Location(world, logout.x(), logout.y(), logout.z()));
        }
        return logoutAnchors;
    }

    private CompletableFuture<Void> loadTargetsPaced(List<ChunkTarget> targets, int perTick) {
        if (targets == null || targets.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        int pace = Math.max(1, perTick);
        if (targets.size() <= pace) {
            return loadTargets(targets);
        }
        CompletableFuture<Void> done = new CompletableFuture<>();
        AtomicInteger nextIndex = new AtomicInteger();
        AtomicInteger pending = new AtomicInteger();
        BukkitTask[] holder = new BukkitTask[1];
        holder[0] = plugin.getSpigotScheduler().runGlobalTimer(() -> {
            if (done.isDone()) {
                return;
            }
            int scheduled = 0;
            while (scheduled < pace) {
                int index = nextIndex.getAndIncrement();
                if (index >= targets.size()) {
                    break;
                }
                scheduled++;
                pending.incrementAndGet();
                loadTarget(targets.get(index)).whenComplete((chunk, throwable) -> pending.decrementAndGet());
            }
            if (nextIndex.get() >= targets.size() && pending.get() <= 0) {
                BukkitTask running = holder[0];
                if (running != null) {
                    running.cancel();
                    loadTasks.remove(running);
                }
                done.complete(null);
            }
        }, 1L, 1L);
        if (holder[0] != null) {
            loadTasks.add(holder[0]);
        }
        return done;
    }

    private void warmEntityApi(World world, int chunkX, int chunkZ) {
        if (entityApiWarmed || world == null || !JoinWarmupPolicy.warmEntityApi(config())) {
            return;
        }
        if (!world.isChunkLoaded(chunkX, chunkZ)) {
            return;
        }
        try {
            Location loc = new Location(
                    world,
                    (chunkX << 4) + 8.5D,
                    world.getMinHeight() + 1.0D,
                    (chunkZ << 4) + 8.5D
            );
            ArmorStand stand = world.spawn(loc, ArmorStand.class, entity -> {
                entity.setInvisible(true);
                entity.setMarker(true);
                entity.setPersistent(false);
                entity.setGravity(false);
                entity.setInvulnerable(true);
            });
            try {
                stand.isInvulnerable();
                stand.getNearbyEntities(4.0D, 4.0D, 4.0D);
                entityApiWarmed = true;
            } finally {
                stand.remove();
            }
        } catch (RuntimeException exception) {
            plugin.getLogger().log(Level.WARNING, "Join warmup: could not warm entity API", exception);
        }
    }

    private CompletableFuture<Void> loadTargets(List<ChunkTarget> targets) {
        if (targets == null || targets.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        CompletableFuture<?>[] futures = new CompletableFuture<?>[targets.size()];
        for (int i = 0; i < targets.size(); i++) {
            futures[i] = loadTarget(targets.get(i));
        }
        return CompletableFuture.allOf(futures);
    }

    private CompletableFuture<Chunk> loadTarget(ChunkTarget target) {
        return PaperChunkLoader.getChunkAtAsync(
                plugin.getSpigotScheduler(), target.world(), target.x(), target.z()
        ).whenComplete((chunk, throwable) -> {
            if (chunk == null) {
                return;
            }
            if (target.pin()) {
                pinChunk(target.world(), target.x(), target.z());
            }
            if (plugin.getSpigotScheduler() != null) {
                plugin.getSpigotScheduler().runRegion(target.world(), target.x(), target.z(), () ->
                        warmEntityApi(target.world(), target.x(), target.z()));
            }
        });
    }

    private void pinChunk(World world, int chunkX, int chunkZ) {
        plugin.getSpigotScheduler().runRegion(world, chunkX, chunkZ, () ->
                PaperChunkTickets.add(world, chunkX, chunkZ, plugin));
    }

    private void cancelTicketRelease() {
        if (releaseTicketsTask != null) {
            releaseTicketsTask.cancel();
            releaseTicketsTask = null;
        }
    }

    private void releaseTicketsNow() {
        Plugin owner = plugin;
        for (World world : Bukkit.getWorlds()) {
            PaperChunkTickets.removeAll(world, owner);
        }
    }

    private static int worldViewDistance(World world) {
        if (world == null) {
            return 0;
        }
        try {
            return world.getViewDistance();
        } catch (RuntimeException ignored) {
            return 0;
        }
    }

    private PlayerLogoutLocationNbt.LogoutLocation findLogoutLocation(UUID uuid) {
        Set<Path> folders = new LinkedHashSet<>();
        for (World world : Bukkit.getWorlds()) {
            Path folder = world.getWorldFolder() == null ? null : world.getWorldFolder().toPath();
            folders.addAll(PlayerLogoutLocationNbt.playerDataFoldersForWorld(folder));
        }
        Path container = Bukkit.getWorldContainer() == null ? null : Bukkit.getWorldContainer().toPath();
        folders.addAll(PlayerLogoutLocationNbt.playerDataFoldersForWorld(container));
        String fileName = uuid + ".dat";
        for (Path folder : folders) {
            PlayerLogoutLocationNbt.LogoutLocation location = PlayerLogoutLocationNbt.readFile(folder.resolve(fileName));
            if (location != null) {
                return location;
            }
        }
        return null;
    }

    private World resolveWorld(String dimension) {
        if (dimension == null || dimension.isBlank()) {
            return overworld();
        }
        for (World world : Bukkit.getWorlds()) {
            NamespacedKey key = world.getKey();
            if (key != null && key.toString().equalsIgnoreCase(dimension)) {
                return world;
            }
            if (world.getName().equalsIgnoreCase(dimension)) {
                return world;
            }
            if (("minecraft:" + world.getName()).equalsIgnoreCase(dimension)) {
                return world;
            }
        }
        if ("minecraft:overworld".equalsIgnoreCase(dimension)) {
            return overworld();
        }
        plugin.getLogger().log(Level.FINE, "Join warmup: unknown logout dimension {0}", dimension);
        return overworld();
    }

    private static World overworld() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL
                    && JoinWarmupPolicy.shouldKeepSpawnLoaded(world.getName(), world.getEnvironment().name())) {
                return world;
            }
        }
        List<World> worlds = Bukkit.getWorlds();
        return worlds.isEmpty() ? null : worlds.get(0);
    }

    private static List<ChunkTarget> uniqueChunks(List<Location> anchors, int radius, boolean pin) {
        Set<String> seen = new LinkedHashSet<>();
        List<ChunkTarget> targets = new ArrayList<>();
        if (anchors == null) {
            return targets;
        }
        for (Location anchor : anchors) {
            if (anchor == null || anchor.getWorld() == null) {
                continue;
            }
            World world = anchor.getWorld();
            int centerX = anchor.getBlockX() >> 4;
            int centerZ = anchor.getBlockZ() >> 4;
            for (int[] chunk : RtpChunkPreloadPolicy.chunkOrder(centerX, centerZ, radius)) {
                String key = JoinWarmupPolicy.chunkKey(world.getName(), chunk[0], chunk[1]);
                if (seen.add(key)) {
                    targets.add(new ChunkTarget(world, chunk[0], chunk[1], pin));
                }
            }
        }
        return targets;
    }

    private FileConfiguration config() {
        return plugin.getConfigManager() == null ? null : plugin.getConfigManager().getConfig();
    }

    private record ChunkTarget(World world, int x, int z, boolean pin) {
    }
}
