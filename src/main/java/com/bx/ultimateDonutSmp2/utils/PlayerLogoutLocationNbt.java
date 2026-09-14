package com.bx.ultimateDonutSmp2.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Reads the last logout {@code Pos} and {@code Dimension} from a vanilla player.dat file.
 *
 * <p>Join happens on the tick thread before {@code PlayerJoinEvent}, so the only way to start
 * loading those chunks in time is {@code AsyncPlayerPreLoginEvent}. Bukkit does not expose the
 * logout location there; the player data NBT does.</p>
 *
 * <p>Minecraft 26.1 moved {@code playerdata/} to {@code players/data/}. Older worlds still
 * use {@code playerdata/}, so both layouts are searched.</p>
 */
public final class PlayerLogoutLocationNbt {

    public record LogoutLocation(String dimension, double x, double y, double z) {
        public int chunkX() {
            return ((int) Math.floor(x)) >> 4;
        }

        public int chunkZ() {
            return ((int) Math.floor(z)) >> 4;
        }
    }

    public record DatedFile(Path path, long modifiedMillis) {
    }

    private static final byte TAG_END = 0;
    private static final byte TAG_BYTE = 1;
    private static final byte TAG_SHORT = 2;
    private static final byte TAG_INT = 3;
    private static final byte TAG_LONG = 4;
    private static final byte TAG_FLOAT = 5;
    private static final byte TAG_DOUBLE = 6;
    private static final byte TAG_BYTE_ARRAY = 7;
    private static final byte TAG_STRING = 8;
    private static final byte TAG_LIST = 9;
    private static final byte TAG_COMPOUND = 10;
    private static final byte TAG_INT_ARRAY = 11;
    private static final byte TAG_LONG_ARRAY = 12;

    private PlayerLogoutLocationNbt() {
    }

    public static List<Path> candidateFiles(Path worldFolder, UUID uuid) {
        if (worldFolder == null || uuid == null) {
            return List.of();
        }
        String fileName = uuid + ".dat";
        List<Path> paths = new ArrayList<>();
        for (Path folder : playerDataFoldersForWorld(worldFolder)) {
            paths.add(folder.resolve(fileName));
        }
        return paths;
    }

    public static boolean isPlayerDat(Path path) {
        if (path == null || path.getFileName() == null) {
            return false;
        }
        String name = path.getFileName().toString();
        return name.endsWith(".dat") && !name.endsWith(".dat_old");
    }

    public static UUID uuidFromPlayerDat(Path path) {
        if (!isPlayerDat(path)) {
            return null;
        }
        String name = path.getFileName().toString();
        try {
            return UUID.fromString(name.substring(0, name.length() - 4));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    /**
     * Newest unique {@code player.dat} paths. Older copies of the same UUID are dropped so a
     * player who appears in more than one world folder only warms one square.
     */
    public static List<Path> newestUniquePlayerData(List<DatedFile> files, int limit) {
        if (files == null || limit <= 0) {
            return List.of();
        }
        List<DatedFile> ordered = new ArrayList<>();
        for (DatedFile file : files) {
            if (file != null && isPlayerDat(file.path())) {
                ordered.add(file);
            }
        }
        ordered.sort(Comparator.comparingLong(DatedFile::modifiedMillis).reversed());
        Set<UUID> seen = new LinkedHashSet<>();
        List<Path> newest = new ArrayList<>();
        for (DatedFile file : ordered) {
            UUID uuid = uuidFromPlayerDat(file.path());
            if (uuid == null || !seen.add(uuid)) {
                continue;
            }
            newest.add(file.path());
            if (newest.size() >= limit) {
                break;
            }
        }
        return newest;
    }

    public static List<DatedFile> listPlayerData(Path folder) {
        if (folder == null || !Files.isDirectory(folder)) {
            return List.of();
        }
        List<DatedFile> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path path : stream) {
                if (!Files.isRegularFile(path) || !isPlayerDat(path)) {
                    continue;
                }
                long modified = 0L;
                try {
                    modified = Files.getLastModifiedTime(path).toMillis();
                } catch (IOException ignored) {
                }
                files.add(new DatedFile(path, modified));
            }
        } catch (IOException ignored) {
            return List.of();
        }
        return files;
    }

    public static List<Path> listNewestPlayerData(Path folder, int limit) {
        return newestUniquePlayerData(listPlayerData(folder), limit);
    }

    public static List<Path> playerDataFolders(Path worldFolder) {
        if (worldFolder == null) {
            return List.of();
        }
        Path overworldDimension = worldFolder.resolve("dimensions").resolve("minecraft").resolve("overworld");
        return List.of(
                worldFolder.resolve("players").resolve("data"),
                worldFolder.resolve("playerdata"),
                overworldDimension.resolve("players").resolve("data"),
                overworldDimension.resolve("playerdata")
        );
    }

    /**
     * Paper 26 {@code World#getWorldFolder()} is often the dimension directory
     * ({@code world/dimensions/minecraft/overworld}), while {@code players/data} lives next to
     * {@code level.dat}. Walk up until that level root and search both.
     */
    public static List<Path> playerDataFoldersForWorld(Path worldFolder) {
        LinkedHashSet<Path> folders = new LinkedHashSet<>();
        if (worldFolder == null) {
            return List.of();
        }
        folders.addAll(playerDataFolders(worldFolder));
        Path levelRoot = findLevelRoot(worldFolder);
        Path start = worldFolder.toAbsolutePath().normalize();
        if (levelRoot != null && !levelRoot.equals(start)) {
            folders.addAll(playerDataFolders(levelRoot));
        }
        return List.copyOf(folders);
    }

    public static Path findLevelRoot(Path start) {
        Path current = start == null ? null : start.toAbsolutePath().normalize();
        for (int depth = 0; current != null && depth < 6; depth++) {
            if (Files.isRegularFile(current.resolve("level.dat"))) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }

    public static LogoutLocation readFile(Path file) {
        if (file == null || !Files.isRegularFile(file)) {
            return null;
        }
        try {
            return read(Files.readAllBytes(file));
        } catch (IOException ignored) {
            return null;
        }
    }

    public static LogoutLocation read(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return null;
        }
        try {
            return readUncompressed(maybeGunzip(payload));
        } catch (IOException ignored) {
            return null;
        }
    }

    static byte[] writePlayerDat(String dimension, double x, double y, double z) throws IOException {
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(raw)) {
            out.writeByte(TAG_COMPOUND);
            out.writeUTF("");
            out.writeByte(TAG_LIST);
            out.writeUTF("Pos");
            out.writeByte(TAG_DOUBLE);
            out.writeInt(3);
            out.writeDouble(x);
            out.writeDouble(y);
            out.writeDouble(z);
            out.writeByte(TAG_STRING);
            out.writeUTF("Dimension");
            out.writeUTF(dimension);
            out.writeByte(TAG_END);
        }
        ByteArrayOutputStream gzipped = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(gzipped)) {
            gzip.write(raw.toByteArray());
        }
        return gzipped.toByteArray();
    }

    private static LogoutLocation readUncompressed(byte[] payload) throws IOException {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload))) {
            byte type = in.readByte();
            if (type != TAG_COMPOUND) {
                return null;
            }
            in.readUTF();
            return readCompound(in);
        }
    }

    private static LogoutLocation readCompound(DataInput in) throws IOException {
        Double x = null;
        Double y = null;
        Double z = null;
        String dimension = null;
        while (true) {
            byte type = in.readByte();
            if (type == TAG_END) {
                break;
            }
            String name = in.readUTF();
            if (type == TAG_LIST && "Pos".equals(name)) {
                double[] pos = readDoubleList(in);
                if (pos != null && pos.length >= 3) {
                    x = pos[0];
                    y = pos[1];
                    z = pos[2];
                }
            } else if (type == TAG_STRING && "Dimension".equals(name)) {
                dimension = in.readUTF();
            } else {
                skip(in, type);
            }
        }
        if (x == null || y == null || z == null) {
            return null;
        }
        return new LogoutLocation(dimension == null || dimension.isBlank() ? "minecraft:overworld" : dimension, x, y, z);
    }

    private static double[] readDoubleList(DataInput in) throws IOException {
        byte listType = in.readByte();
        int count = in.readInt();
        if (count < 0 || count > 1024) {
            return null;
        }
        if (listType != TAG_DOUBLE) {
            for (int i = 0; i < count; i++) {
                skip(in, listType);
            }
            return null;
        }
        double[] values = new double[count];
        for (int i = 0; i < count; i++) {
            values[i] = in.readDouble();
        }
        return values;
    }

    private static void skip(DataInput in, byte type) throws IOException {
        switch (type) {
            case TAG_BYTE -> in.skipBytes(1);
            case TAG_SHORT -> in.skipBytes(2);
            case TAG_INT, TAG_FLOAT -> in.skipBytes(4);
            case TAG_LONG, TAG_DOUBLE -> in.skipBytes(8);
            case TAG_BYTE_ARRAY -> {
                int length = in.readInt();
                in.skipBytes(Math.max(0, length));
            }
            case TAG_STRING -> in.readUTF();
            case TAG_LIST -> {
                byte listType = in.readByte();
                int count = in.readInt();
                for (int i = 0; i < count; i++) {
                    skip(in, listType);
                }
            }
            case TAG_COMPOUND -> {
                while (true) {
                    byte child = in.readByte();
                    if (child == TAG_END) {
                        break;
                    }
                    in.readUTF();
                    skip(in, child);
                }
            }
            case TAG_INT_ARRAY -> {
                int length = in.readInt();
                in.skipBytes(Math.max(0, length) * 4);
            }
            case TAG_LONG_ARRAY -> {
                int length = in.readInt();
                in.skipBytes(Math.max(0, length) * 8);
            }
            default -> {
            }
        }
    }

    private static byte[] maybeGunzip(byte[] payload) throws IOException {
        if (payload.length >= 2 && (payload[0] == (byte) 0x1f) && (payload[1] == (byte) 0x8b)) {
            try (InputStream gzip = new GZIPInputStream(new ByteArrayInputStream(payload))) {
                return gzip.readAllBytes();
            }
        }
        return payload;
    }
}
