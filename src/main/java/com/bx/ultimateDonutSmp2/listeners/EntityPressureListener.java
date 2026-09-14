package com.bx.ultimateDonutSmp2.listeners;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.utils.EntityPressurePolicy;
import com.bx.ultimateDonutSmp2.utils.EntityPressurePolicy.Bucket;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Trims bats, animals and monsters when a chunk's entities are actually loaded. {@link ChunkLoadEvent}
 * fires before stored entities exist, so trimming there does nothing and a join still spends seconds
 * ticking every bat and sheep in the newly streamed chunks.
 */
public class EntityPressureListener implements Listener {

    private static final long TRIM_LOG_INTERVAL_MILLIS = 5000L;
    private static final long STARTUP_TRIM_DELAY_TICKS = 40L;

    private final UltimateDonutSmp2 plugin;
    private final AtomicInteger trimmedSinceLog = new AtomicInteger();
    private volatile long lastTrimLogMillis;

    private volatile FileConfiguration cachedConfig;
    private volatile boolean enabled = true;
    private volatile int ambientCap = 2;
    private volatile int animalCap = 24;
    private volatile int monsterCap = 24;

    public EntityPressureListener(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
        plugin.getSpigotScheduler().runGlobalLater(this::trimAllLoadedChunks, STARTUP_TRIM_DELAY_TICKS);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        refreshSettingsIfNeeded();
        if (!enabled) {
            return;
        }
        trimChunk(event.getChunk());
        trimLoadedEntities(event.getChunk(), event.getEntities());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        refreshSettingsIfNeeded();
        if (!enabled) {
            return;
        }
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Location anchor = new Location(world, (chunkX << 4) + 8, world.getMinHeight(), (chunkZ << 4) + 8);
        plugin.getSpigotScheduler().runRegionLater(anchor, () -> {
            if (!world.isChunkLoaded(chunkX, chunkZ)) {
                return;
            }
            trimChunk(world.getChunkAt(chunkX, chunkZ));
        }, 1L);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        refreshSettingsIfNeeded();
        if (!enabled) {
            return;
        }
        if (isProtectedSpawnReason(event.getSpawnReason())) {
            return;
        }
        Entity entity = event.getEntity();
        Bucket bucket = EntityPressurePolicy.bucket(entity);
        if (bucket == Bucket.SKIP) {
            return;
        }
        int cap = EntityPressurePolicy.capFor(bucket, ambientCap, animalCap, monsterCap);
        if (cap == Integer.MAX_VALUE) {
            return;
        }
        Chunk chunk = entity.getLocation().getChunk();
        int current = countBucket(chunk, bucket);
        if (current >= cap) {
            event.setCancelled(true);
        }
    }

    private void trimAllLoadedChunks() {
        refreshSettingsIfNeeded();
        if (!enabled || Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                Location anchor = new Location(world, (chunk.getX() << 4) + 8, world.getMinHeight(), (chunk.getZ() << 4) + 8);
                plugin.getSpigotScheduler().runRegion(anchor, () -> {
                    if (chunk.isLoaded()) {
                        trimChunk(chunk);
                    }
                });
            }
        }
    }

    private void trimLoadedEntities(Chunk chunk, Collection<Entity> loaded) {
        if (loaded == null || loaded.isEmpty()) {
            return;
        }
        trimEntities(chunk, loaded);
    }

    private void trimChunk(Chunk chunk) {
        if (chunk == null || !chunk.isLoaded()) {
            return;
        }
        Entity[] entities = chunk.getEntities();
        if (entities.length == 0) {
            return;
        }
        trimEntities(chunk, List.of(entities));
    }

    private void trimEntities(Chunk chunk, Collection<Entity> entities) {
        if (chunk == null || entities == null || entities.isEmpty()) {
            return;
        }
        int minCap = Math.min(ambientCap, Math.min(animalCap, monsterCap));
        if (entities.size() <= minCap) {
            return;
        }

        Map<Bucket, List<Entity>> grouped = new EnumMap<>(Bucket.class);
        for (Entity entity : entities) {
            Bucket bucket = EntityPressurePolicy.bucket(entity);
            if (bucket == Bucket.SKIP) {
                continue;
            }
            grouped.computeIfAbsent(bucket, ignored -> new ArrayList<>()).add(entity);
        }

        int removed = 0;
        for (Map.Entry<Bucket, List<Entity>> entry : grouped.entrySet()) {
            int cap = EntityPressurePolicy.capFor(entry.getKey(), ambientCap, animalCap, monsterCap);
            int excess = EntityPressurePolicy.excessCount(entry.getValue().size(), cap);
            for (int i = 0; i < excess; i++) {
                entry.getValue().get(i).remove();
                removed++;
            }
        }
        recordTrim(removed);
    }

    private void recordTrim(int removed) {
        if (removed <= 0) {
            return;
        }
        int pending = trimmedSinceLog.addAndGet(removed);
        long now = System.currentTimeMillis();
        if (now - lastTrimLogMillis < TRIM_LOG_INTERVAL_MILLIS) {
            return;
        }
        lastTrimLogMillis = now;
        int reported = trimmedSinceLog.getAndSet(0);
        if (reported <= 0) {
            reported = pending;
        }
        plugin.getLogger().info("Removed " + reported + " extra mobs from loaded chunks (entity pressure).");
    }

    private static boolean isProtectedSpawnReason(CreatureSpawnEvent.SpawnReason reason) {
        if (reason == null) {
            return false;
        }
        return switch (reason) {
            case SPAWNER, TRIAL_SPAWNER, SPAWNER_EGG, CUSTOM, COMMAND -> true;
            default -> false;
        };
    }

    private int countBucket(Chunk chunk, Bucket bucket) {
        int count = 0;
        for (Entity entity : chunk.getEntities()) {
            if (EntityPressurePolicy.bucket(entity) == bucket) {
                count++;
            }
        }
        return count;
    }

    private void refreshSettingsIfNeeded() {
        FileConfiguration current = plugin.getConfigManager().getConfig();
        if (current == cachedConfig || current == null) {
            return;
        }
        enabled = current.getBoolean("ENTITY-PRESSURE.ENABLED", true);
        ambientCap = Math.max(0, current.getInt("ENTITY-PRESSURE.MAX-AMBIENT-PER-CHUNK", 2));
        animalCap = Math.max(0, current.getInt("ENTITY-PRESSURE.MAX-ANIMALS-PER-CHUNK", 24));
        monsterCap = Math.max(0, current.getInt("ENTITY-PRESSURE.MAX-MONSTERS-PER-CHUNK", 24));
        cachedConfig = current;
    }
}
