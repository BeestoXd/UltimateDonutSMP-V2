package com.bx.ultimateDonutSmp2.utils;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Paper's {@code World#getChunkAtAsync} without compiling against paper-api.
 *
 * <p>{@code gen} is always {@code false} here: join warmup must not create terrain, only read
 * chunks that already exist so the first login is not generating a view-distance square on
 * the tick thread.</p>
 */
public final class PaperChunkLoader {

    private PaperChunkLoader() {
    }

    @SuppressWarnings("unchecked")
    public static CompletableFuture<Chunk> getChunkAtAsync(SpigotScheduler scheduler, World world, int chunkX, int chunkZ) {
        if (world == null) {
            return CompletableFuture.completedFuture(null);
        }
        if (world.isChunkLoaded(chunkX, chunkZ)) {
            return CompletableFuture.completedFuture(world.getChunkAt(chunkX, chunkZ));
        }
        try {
            Method method = world.getClass().getMethod("getChunkAtAsync", int.class, int.class, boolean.class);
            if (CompletableFuture.class.isAssignableFrom(method.getReturnType())) {
                return (CompletableFuture<Chunk>) method.invoke(world, chunkX, chunkZ, false);
            }
        } catch (Exception ignored) {
        }
        try {
            Method method = world.getClass().getMethod(
                    "getChunkAtAsync", int.class, int.class, boolean.class, Consumer.class);
            CompletableFuture<Chunk> future = new CompletableFuture<>();
            method.invoke(world, chunkX, chunkZ, false, (Consumer<Chunk>) future::complete);
            return future;
        } catch (Exception ignored) {
        }
        CompletableFuture<Chunk> future = new CompletableFuture<>();
        if (scheduler == null) {
            future.complete(null);
            return future;
        }
        scheduler.runRegion(world, chunkX, chunkZ, () -> {
            try {
                if (world.loadChunk(chunkX, chunkZ, false)) {
                    future.complete(world.getChunkAt(chunkX, chunkZ));
                } else {
                    future.complete(null);
                }
            } catch (Exception exception) {
                future.completeExceptionally(exception);
            }
        });
        return future;
    }
}
