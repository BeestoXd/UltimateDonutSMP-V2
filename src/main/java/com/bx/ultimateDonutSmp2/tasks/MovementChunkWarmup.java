package com.bx.ultimateDonutSmp2.tasks;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.utils.JoinWarmupPolicy;
import com.bx.ultimateDonutSmp2.utils.MovementChunkPreloadPolicy;
import com.bx.ultimateDonutSmp2.utils.PaperChunkLoader;
import com.bx.ultimateDonutSmp2.utils.RtpChunkPreloadPolicy;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Starts loading already-generated chunks ahead of a moving player so walk/run/fly does
 * not pay region IO on the tick that they enter the square.
 */
public final class MovementChunkWarmup {

    private final UltimateDonutSmp2 plugin;

    public MovementChunkWarmup(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    public void preloadAhead(World world, int fromChunkX, int fromChunkZ, int toChunkX, int toChunkZ) {
        if (world == null || plugin.getConfigManager() == null) {
            return;
        }
        FileConfiguration config = plugin.getConfigManager().getConfig();
        if (!JoinWarmupPolicy.enabled(config)) {
            return;
        }

        int ahead = MovementChunkPreloadPolicy.aheadChunks(
                config.getInt(JoinWarmupPolicy.ROOT + ".MOVEMENT-AHEAD-CHUNKS",
                        MovementChunkPreloadPolicy.DEFAULT_AHEAD_CHUNKS));
        int radius = MovementChunkPreloadPolicy.radius(
                config.getInt(JoinWarmupPolicy.ROOT + ".MOVEMENT-AHEAD-RADIUS",
                        MovementChunkPreloadPolicy.DEFAULT_RADIUS));
        int maxChunks = MovementChunkPreloadPolicy.maxChunksPerMove(
                config.getInt(JoinWarmupPolicy.ROOT + ".MOVEMENT-AHEAD-MAX-CHUNKS",
                        MovementChunkPreloadPolicy.DEFAULT_MAX_CHUNKS_PER_MOVE));

        int[] focus = MovementChunkPreloadPolicy.focusChunk(
                fromChunkX, fromChunkZ, toChunkX, toChunkZ, ahead);
        int scheduled = 0;
        for (int[] chunk : RtpChunkPreloadPolicy.chunkOrder(focus[0], focus[1], radius)) {
            if (scheduled >= maxChunks) {
                return;
            }
            if (world.isChunkLoaded(chunk[0], chunk[1])) {
                continue;
            }
            scheduled++;
            PaperChunkLoader.getChunkAtAsync(plugin.getSpigotScheduler(), world, chunk[0], chunk[1]);
        }
    }
}
