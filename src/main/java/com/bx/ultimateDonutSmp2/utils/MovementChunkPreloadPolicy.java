package com.bx.ultimateDonutSmp2.utils;

/**
 * Which chunks to start loading when a player walks, runs, or flies into a new chunk.
 *
 * <p>Join warmup only covers spawn (and recent logout squares). The first time someone
 * leaves that area, Paper streams view-distance on the tick thread and tab ping spikes.
 * This policy aims the async loader a couple of chunks ahead of the movement so those
 * region files are already in memory. It never asks for new terrain.</p>
 */
public final class MovementChunkPreloadPolicy {

    public static final int DEFAULT_AHEAD_CHUNKS = 2;
    public static final int DEFAULT_RADIUS = 2;
    public static final int DEFAULT_MAX_CHUNKS_PER_MOVE = 10;
    static final int MIN_AHEAD = 1;
    static final int MAX_AHEAD = 4;
    static final int MIN_RADIUS = 1;
    static final int MAX_RADIUS = 3;
    static final int MIN_MAX_CHUNKS = 4;
    static final int MAX_MAX_CHUNKS = 16;

    private MovementChunkPreloadPolicy() {
    }

    public static int aheadChunks(int configured) {
        return clamp(configured, MIN_AHEAD, MAX_AHEAD, DEFAULT_AHEAD_CHUNKS);
    }

    public static int radius(int configured) {
        return clamp(configured, MIN_RADIUS, MAX_RADIUS, DEFAULT_RADIUS);
    }

    public static int maxChunksPerMove(int configured) {
        return clamp(configured, MIN_MAX_CHUNKS, MAX_MAX_CHUNKS, DEFAULT_MAX_CHUNKS_PER_MOVE);
    }

    /**
     * The chunk to centre the preload square on: a few chunks further along the same
     * axis the player just crossed.
     */
    public static int[] focusChunk(int fromChunkX, int fromChunkZ, int toChunkX, int toChunkZ, int ahead) {
        int dx = Integer.compare(toChunkX, fromChunkX);
        int dz = Integer.compare(toChunkZ, fromChunkZ);
        int distance = Math.max(MIN_AHEAD, ahead);
        return new int[]{toChunkX + dx * distance, toChunkZ + dz * distance};
    }

    private static int clamp(int configured, int min, int max, int fallback) {
        if (configured <= 0) {
            return fallback;
        }
        return Math.max(min, Math.min(max, configured));
    }
}
