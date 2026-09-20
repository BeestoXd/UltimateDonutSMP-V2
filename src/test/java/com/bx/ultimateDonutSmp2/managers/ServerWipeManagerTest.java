package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerWipeManagerTest {

    @Test
    void acceptsOnlyDirectSafeWorldNames() {
        assertTrue(ServerWipeManager.isSafeWorldName("resource_world"));
        assertTrue(ServerWipeManager.isSafeWorldName("resource-nether.2"));
        assertFalse(ServerWipeManager.isSafeWorldName("../world"));
        assertFalse(ServerWipeManager.isSafeWorldName("folder/world"));
        assertFalse(ServerWipeManager.isSafeWorldName("world\\nether"));
    }

    @Test
    void normalizesAndDeduplicatesWorldNames() {
        assertEquals(
                List.of("Resource", "resource_nether"),
                ServerWipeManager.normalizeWorldNames(List.of(" Resource ", "resource", "resource_nether", ""))
        );
    }

    @Test
    void expiresConfirmationTokensUsingConfiguredTtl() {
        assertFalse(ServerWipeManager.isTokenExpired(1_000L, 5_999L, 5_000L));
        assertTrue(ServerWipeManager.isTokenExpired(1_000L, 6_001L, 5_000L));
        assertTrue(ServerWipeManager.isTokenExpired(0L, 1_000L, 5_000L));
    }

    @Test
    void protectedWorldsComeOnlyFromConfig() {
        assertEquals(Set.of(), ServerWipeManager.configuredProtectedWorlds(List.of()));
        assertEquals(Set.of(), ServerWipeManager.configuredProtectedWorlds(List.of(" ", "")));
        assertEquals(
                Set.of("spawn", "lobby"),
                ServerWipeManager.configuredProtectedWorlds(List.of(" spawn ", "lobby", "SPAWN"))
        );

        Set<String> empty = ServerWipeManager.configuredProtectedWorlds(List.of());
        assertFalse(empty.contains("world"));
        assertFalse(empty.contains("world_nether"));
        assertFalse(empty.contains("world_the_end"));
    }

    @Test
    void defersMoveWhenALoadedWorldCannotUnload() {
        assertTrue(ServerWipeManager.shouldDeferWorldMove(true, false));
        assertFalse(ServerWipeManager.shouldDeferWorldMove(true, true));
        assertFalse(ServerWipeManager.shouldDeferWorldMove(false, false));
    }

    @Test
    void movesWorldFolderOnlyWhenTheOriginalIsStillInPlace() {
        assertTrue(ServerWipeManager.shouldMoveWorldFolder(true, false));
        assertFalse(ServerWipeManager.shouldMoveWorldFolder(true, true));
        assertFalse(ServerWipeManager.shouldMoveWorldFolder(false, false));
        assertFalse(ServerWipeManager.shouldMoveWorldFolder(false, true));
    }
}
