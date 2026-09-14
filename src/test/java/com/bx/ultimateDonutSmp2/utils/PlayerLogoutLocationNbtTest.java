package com.bx.ultimateDonutSmp2.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerLogoutLocationNbtTest {

    @Test
    void gzipPlayerDatRoundTripsPosAndDimension() throws Exception {
        byte[] payload = PlayerLogoutLocationNbt.writePlayerDat(
                "minecraft:overworld", -152.65137964471575D, 87.0D, 8.709520138185752D);

        PlayerLogoutLocationNbt.LogoutLocation location = PlayerLogoutLocationNbt.read(payload);

        assertEquals("minecraft:overworld", location.dimension());
        assertEquals(-152.65137964471575D, location.x(), 0.0000001D);
        assertEquals(87.0D, location.y(), 0.0000001D);
        assertEquals(8.709520138185752D, location.z(), 0.0000001D);
        assertEquals(-10, location.chunkX());
        assertEquals(0, location.chunkZ());
    }

    @Test
    void candidateFilesLookInMinecraft26AndLegacyPlayerdataFolders(@TempDir Path worldFolder) {
        UUID uuid = UUID.fromString("e49f4766-a7fc-35fb-80cf-9ddcd1ac0296");
        List<Path> files = PlayerLogoutLocationNbt.candidateFiles(worldFolder, uuid);
        List<String> relative = files.stream()
                .map(path -> worldFolder.relativize(path).toString().replace('\\', '/'))
                .toList();

        assertEquals(4, files.size());
        assertEquals("players/data/" + uuid + ".dat", relative.get(0));
        assertEquals("playerdata/" + uuid + ".dat", relative.get(1));
        assertTrue(relative.get(2).contains("dimensions/minecraft/overworld/players/data/"));
        assertTrue(relative.get(3).contains("dimensions/minecraft/overworld/playerdata/"));
    }

    @Test
    void missingOrEmptyPayloadIsIgnored() throws Exception {
        assertNull(PlayerLogoutLocationNbt.read(null));
        assertNull(PlayerLogoutLocationNbt.read(new byte[0]));
        assertNull(PlayerLogoutLocationNbt.readFile(Path.of("does-not-exist.dat")));
        Path empty = Files.createTempFile("empty-player", ".dat");
        Files.write(empty, new byte[]{0x00});
        assertNull(PlayerLogoutLocationNbt.readFile(empty));
    }

    @Test
    void listPlayerDataReadsMinecraft26PlayersDataFolder(@TempDir Path worldFolder) throws Exception {
        Path folder = worldFolder.resolve("players").resolve("data");
        Files.createDirectories(folder);
        UUID uuid = UUID.fromString("e49f4766-a7fc-35fb-80cf-9ddcd1ac0296");
        Path file = folder.resolve(uuid + ".dat");
        Files.write(file, PlayerLogoutLocationNbt.writePlayerDat("minecraft:overworld", 350.9D, 65.0D, 1975.9D));

        List<PlayerLogoutLocationNbt.DatedFile> listed = PlayerLogoutLocationNbt.listPlayerData(folder);
        assertEquals(1, listed.size());
        PlayerLogoutLocationNbt.LogoutLocation location = PlayerLogoutLocationNbt.readFile(listed.getFirst().path());
        assertEquals(350.9D, location.x(), 0.0001D);
        assertEquals(1975.9D, location.z(), 0.0001D);

        List<Path> candidates = PlayerLogoutLocationNbt.candidateFiles(worldFolder, uuid);
        assertEquals(location.x(), PlayerLogoutLocationNbt.readFile(candidates.getFirst()).x(), 0.0001D);
    }

    @Test
    void playerDataFoldersWalkUpFromAPaper26DimensionDirectory(@TempDir Path worldFolder) throws Exception {
        Files.writeString(worldFolder.resolve("level.dat"), "x");
        UUID uuid = UUID.fromString("e49f4766-a7fc-35fb-80cf-9ddcd1ac0296");
        Path dataFile = worldFolder.resolve("players").resolve("data").resolve(uuid + ".dat");
        Files.createDirectories(dataFile.getParent());
        Files.write(dataFile, PlayerLogoutLocationNbt.writePlayerDat("minecraft:overworld", 315.4D, 69.0D, 1678.5D));
        Path dimensionFolder = worldFolder.resolve("dimensions").resolve("minecraft").resolve("overworld");
        Files.createDirectories(dimensionFolder);

        assertEquals(worldFolder.toAbsolutePath().normalize(),
                PlayerLogoutLocationNbt.findLevelRoot(dimensionFolder));
        PlayerLogoutLocationNbt.LogoutLocation location = null;
        for (Path folder : PlayerLogoutLocationNbt.playerDataFoldersForWorld(dimensionFolder)) {
            location = PlayerLogoutLocationNbt.readFile(folder.resolve(uuid + ".dat"));
            if (location != null) {
                break;
            }
        }
        assertNotNull(location);
        assertEquals(315.4D, location.x(), 0.0001D);
        assertEquals(1678.5D, location.z(), 0.0001D);
    }

    @Test
    void newestUniquePlayerDataKeepsTheLatestCopyOfEachUuid() {
        UUID older = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID newer = UUID.fromString("e49f4766-a7fc-35fb-80cf-9ddcd1ac0296");
        Path stale = Path.of("world", "playerdata", newer + ".dat");
        Path latest = Path.of("other", "playerdata", newer + ".dat");
        Path other = Path.of("world", "playerdata", older + ".dat");
        List<Path> newest = PlayerLogoutLocationNbt.newestUniquePlayerData(List.of(
                new PlayerLogoutLocationNbt.DatedFile(stale, 10L),
                new PlayerLogoutLocationNbt.DatedFile(latest, 50L),
                new PlayerLogoutLocationNbt.DatedFile(other, 40L),
                new PlayerLogoutLocationNbt.DatedFile(Path.of("world", "playerdata", "not-a-uuid.dat"), 99L)
        ), 4);

        assertEquals(List.of(latest, other), newest);
    }
}
