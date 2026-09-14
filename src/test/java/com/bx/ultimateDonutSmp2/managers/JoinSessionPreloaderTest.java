package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.models.Home;
import com.bx.ultimateDonutSmp2.models.IgnoreEntry;
import com.bx.ultimateDonutSmp2.models.PlayerData;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JoinSessionPreloaderTest {

    @Test
    void consumeRemovesTheSnapshotOnce() {
        JoinSessionPreloader preloader = new JoinSessionPreloader(null);
        UUID uuid = UUID.randomUUID();
        JoinSessionPreloader.Snapshot snapshot = emptySnapshot(new PlayerData(uuid, "Joiner"));

        preloader.store(uuid, snapshot);

        assertTrue(preloader.isPending(uuid));
        assertSame(snapshot, preloader.peek(uuid));
        assertSame(snapshot, preloader.consume(uuid));
        assertFalse(preloader.isPending(uuid));
        assertNull(preloader.peek(uuid));
        assertNull(preloader.consume(uuid));
    }

    @Test
    void aFailedLoginDiscardsThePendingRow() {
        JoinSessionPreloader preloader = new JoinSessionPreloader(null);
        UUID uuid = UUID.randomUUID();
        preloader.store(uuid, emptySnapshot(null));

        preloader.discard(uuid);

        assertFalse(preloader.isPending(uuid));
        assertNull(preloader.consume(uuid));
    }

    @Test
    void aMissingPlayerRowMeansANewPlayerWithoutAnotherDatabaseRead() {
        JoinSessionPreloader.Snapshot snapshot = emptySnapshot(null);
        assertNull(snapshot.playerData());
    }

    @Test
    void homesForCacheCopiesSoJoinCannotMutateTheSnapshot() {
        UUID uuid = UUID.randomUUID();
        Home home = new Home(uuid, "base", new Location(null, 1, 2, 3));
        List<Home> homes = new ArrayList<>();
        homes.add(home);
        JoinSessionPreloader.Snapshot snapshot = new JoinSessionPreloader.Snapshot(
                null,
                homes,
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                null,
                null,
                null
        );

        List<Home> cached = snapshot.homesForCache();
        cached.clear();

        assertEquals(1, snapshot.homes().size());
        assertNotSame(snapshot.homes(), cached);
    }

    @Test
    void ignoreEntriesStayOnTheSnapshotForJoinToApply() {
        UUID owner = UUID.randomUUID();
        UUID ignored = UUID.randomUUID();
        IgnoreEntry entry = new IgnoreEntry(owner, ignored, "Muted", 1L);
        JoinSessionPreloader.Snapshot snapshot = new JoinSessionPreloader.Snapshot(
                null,
                List.of(),
                List.of(entry),
                List.of(),
                List.of(),
                null,
                null,
                null,
                null,
                null
        );

        assertEquals(1, snapshot.ignores().size());
        assertEquals(ignored, snapshot.ignores().get(0).ignoredUuid());
    }

    private static JoinSessionPreloader.Snapshot emptySnapshot(PlayerData playerData) {
        return new JoinSessionPreloader.Snapshot(
                playerData,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                null,
                null,
                null
        );
    }
}
