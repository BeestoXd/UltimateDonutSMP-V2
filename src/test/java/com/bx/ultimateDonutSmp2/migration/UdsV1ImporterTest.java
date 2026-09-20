package com.bx.ultimateDonutSmp2.migration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UdsV1ImporterTest {

    @TempDir
    Path tempDir;

    @Test
    void remapsV1PermissionPrefixWithoutDoublingV2() {
        assertEquals("ultimatedonutsmp2.homes.10", UdsV1Importer.remapPermissionText("ultimatedonutsmp.homes.10"));
        assertEquals("ultimatedonutsmp2.admin.reload", UdsV1Importer.remapPermissionText("ultimatedonutsmp2.admin.reload"));
        assertEquals("give ultimatedonutsmp2.donutplus", UdsV1Importer.remapPermissionText("give ultimatedonutsmp.donutplus"));
    }

    @Test
    void defaultFolderIsSiblingUltimateDonutSmp() {
        Path v2 = Path.of("plugins", "UltimateDonutSmp2");
        assertEquals(Path.of("plugins", "UltimateDonutSmp"), UdsV1Importer.defaultV1Folder(v2));
    }

    @Test
    void sqlitePathReadsDatabaseYmlAndFallsBack() throws Exception {
        Path v1 = tempDir.resolve("UltimateDonutSmp");
        Files.createDirectories(v1.resolve("data"));
        Files.writeString(v1.resolve("database.yml"), """
                DATABASE:
                  TYPE: SQLITE
                  SQLITE:
                    FILE: data/custom.db
                """);
        Files.writeString(v1.resolve("data").resolve("custom.db"), "x");
        assertEquals(v1.resolve("data").resolve("custom.db").normalize(),
                UdsV1Importer.resolveSqliteFile(v1).normalize());

        Path mysqlOnly = tempDir.resolve("mysql-v1");
        Files.createDirectories(mysqlOnly);
        Files.writeString(mysqlOnly.resolve("database.yml"), """
                DATABASE:
                  TYPE: MYSQL
                """);
        assertNull(UdsV1Importer.resolveSqliteFile(mysqlOnly));
    }

    @Test
    void overlayCopiesMatchingKeysAddsExtraCratesAndSkipsUnknownLeaves() {
        YamlConfiguration source = new YamlConfiguration();
        source.set("SHARDS.AMOUNT", 4);
        source.set("SHARDS.CUBOIDS.REGIONS.nether.ENABLED", true);
        source.set("CRATES.mythic.ENABLED", true);
        source.set("ONLY-IN-V1", "nope");
        source.set("HOME-DEFAULT", 9);
        source.set("FLY-SYSTEM.PLAYER-FLY-PERMISSION", "ultimatedonutsmp.player.fly");

        YamlConfiguration target = new YamlConfiguration();
        target.set("SHARDS.AMOUNT", 1);
        target.set("SHARDS.CUBOIDS.REGIONS.spawn.ENABLED", false);
        target.set("CRATES.common.ENABLED", true);
        target.set("HOME-DEFAULT", 3);
        target.set("FLY-SYSTEM.PLAYER-FLY-PERMISSION", "ultimatedonutsmp2.player.fly");
        target.set("ONLY-IN-V2", "keep");

        int changed = UdsV1Importer.overlayMatchingKeys(source, target);
        assertTrue(changed >= 4);
        assertEquals(4, target.getInt("SHARDS.AMOUNT"));
        assertEquals(9, target.getInt("HOME-DEFAULT"));
        assertTrue(target.getBoolean("SHARDS.CUBOIDS.REGIONS.nether.ENABLED"));
        assertTrue(target.getBoolean("CRATES.mythic.ENABLED"));
        assertTrue(target.getBoolean("CRATES.common.ENABLED"));
        assertEquals("keep", target.getString("ONLY-IN-V2"));
        assertFalse(target.isSet("ONLY-IN-V1"));
        assertEquals("ultimatedonutsmp2.player.fly", target.getString("FLY-SYSTEM.PLAYER-FLY-PERMISSION"));
    }

    @Test
    void sqliteInsertIgnoresExistingPrimaryKeys() throws Exception {
        Path sourceDb = tempDir.resolve("v1.db");
        Path destDb = tempDir.resolve("v2.db");
        try (Connection source = DriverManager.getConnection("jdbc:sqlite:" + sourceDb);
             Statement statement = source.createStatement()) {
            statement.execute("CREATE TABLE homes (player_uuid TEXT, home_name TEXT, world TEXT, PRIMARY KEY (player_uuid, home_name))");
            statement.execute("INSERT INTO homes VALUES ('aaa', 'home', 'world')");
            statement.execute("INSERT INTO homes VALUES ('bbb', 'base', 'world')");
        }
        try (Connection dest = DriverManager.getConnection("jdbc:sqlite:" + destDb);
             Statement statement = dest.createStatement()) {
            statement.execute("CREATE TABLE homes (player_uuid TEXT, home_name TEXT, world TEXT, icon TEXT DEFAULT '', PRIMARY KEY (player_uuid, home_name))");
            statement.execute("INSERT INTO homes (player_uuid, home_name, world) VALUES ('aaa', 'home', 'spawn')");
        }

        try (Connection dest = DriverManager.getConnection("jdbc:sqlite:" + destDb)) {
            UdsV1Importer.ImportResult result = UdsV1Importer.importSqlite(sourceDb, dest);
            assertEquals(1, result.tables());
            assertEquals(1, result.rows());
            try (Statement statement = dest.createStatement();
                 ResultSet kept = statement.executeQuery("SELECT player_uuid, world FROM homes ORDER BY player_uuid")) {
                assertTrue(kept.next());
                assertEquals("aaa", kept.getString(1));
                assertEquals("spawn", kept.getString(2));
                assertTrue(kept.next());
                assertEquals("bbb", kept.getString(1));
                assertEquals("world", kept.getString(2));
            }
        }
    }

    @Test
    void yamlImportBacksUpAndWritesMatchingKeys() throws Exception {
        Path v1 = tempDir.resolve("v1");
        Path v2 = tempDir.resolve("v2");
        Path backup = tempDir.resolve("backup");
        Files.createDirectories(v1);
        Files.createDirectories(v2);
        Files.writeString(v1.resolve("config.yml"), """
                HOME-DEFAULT: 12
                ONLY-V1: true
                """);
        Files.writeString(v2.resolve("config.yml"), """
                HOME-DEFAULT: 3
                ONLY-V2: true
                """);
        Files.writeString(v1.resolve("database.yml"), "DATABASE:\n  TYPE: SQLITE\n");
        Files.writeString(v2.resolve("database.yml"), "DATABASE:\n  TYPE: SQLITE\n");

        UdsV1Importer.ImportResult result = UdsV1Importer.importYaml(v1, v2, backup);
        assertEquals(1, result.yamlFiles());
        YamlConfiguration written = YamlConfiguration.loadConfiguration(v2.resolve("config.yml").toFile());
        assertEquals(12, written.getInt("HOME-DEFAULT"));
        assertTrue(written.getBoolean("ONLY-V2"));
        assertFalse(written.isSet("ONLY-V1"));
        YamlConfiguration backed = YamlConfiguration.loadConfiguration(backup.resolve("config.yml").toFile());
        assertEquals(3, backed.getInt("HOME-DEFAULT"));
        YamlConfiguration database = YamlConfiguration.loadConfiguration(v2.resolve("database.yml").toFile());
        assertEquals("SQLITE", database.getString("DATABASE.TYPE"));
    }

    @Test
    void remapValueWalksLists() {
        Object remapped = UdsV1Importer.remapValue(List.of("ultimatedonutsmp.homes.5", "plain"));
        assertEquals(List.of("ultimatedonutsmp2.homes.5", "plain"), remapped);
    }
}
