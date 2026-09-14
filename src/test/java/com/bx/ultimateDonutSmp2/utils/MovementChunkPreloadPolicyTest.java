package com.bx.ultimateDonutSmp2.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MovementChunkPreloadPolicyTest {

    @Test
    void movementPreloadAimsAheadOfTheChunkThePlayerJustEntered() {
        assertArrayEquals(new int[]{3, 5}, MovementChunkPreloadPolicy.focusChunk(0, 5, 1, 5, 2));
        assertArrayEquals(new int[]{4, 1}, MovementChunkPreloadPolicy.focusChunk(4, 4, 4, 3, 2));
        assertEquals(2, MovementChunkPreloadPolicy.aheadChunks(0));
        assertEquals(4, MovementChunkPreloadPolicy.aheadChunks(99));
        assertEquals(10, MovementChunkPreloadPolicy.maxChunksPerMove(0));
    }
}
