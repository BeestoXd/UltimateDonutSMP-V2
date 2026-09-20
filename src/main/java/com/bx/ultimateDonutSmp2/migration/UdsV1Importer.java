package com.bx.ultimateDonutSmp2.migration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Copies compatible UltimateDonutSMP (v1) sqlite rows and matching yaml into a v2 data folder.
 */
public final class UdsV1Importer {

    public static final String DEFAULT_FOLDER_NAME = "UltimateDonutSmp";
    public static final String SQLITE_FALLBACK = "data/data.db";

    private static final Pattern V1_PERMISSION_PREFIX = Pattern.compile("ultimatedonutsmp(?!2)\\.");
    private static final Set<String> SKIP_YAML_NAMES = Set.of("database.yml", "plugin.yml");
    private static final Set<String> SKIP_TABLES = Set.of("sqlite_sequence", "sqlite_stat1");
    private static final List<String> EXTRA_SECTION_ROOTS = List.of(
            "CRATES",
            "SHARDS.CUBOIDS.REGIONS"
    );

    private UdsV1Importer() {
    }

    public static Path defaultV1Folder(Path v2DataFolder) {
        Path parent = v2DataFolder == null ? null : v2DataFolder.getParent();
        if (parent == null) {
            return Path.of(DEFAULT_FOLDER_NAME);
        }
        return parent.resolve(DEFAULT_FOLDER_NAME);
    }

    public static Path resolveV1Folder(Path v2DataFolder, String rawPath) {
        if (rawPath != null && !rawPath.isBlank()) {
            return Path.of(rawPath.trim()).toAbsolutePath().normalize();
        }
        return defaultV1Folder(v2DataFolder).toAbsolutePath().normalize();
    }

    public static Path resolveSqliteFile(Path v1Folder) {
        if (v1Folder == null) {
            return null;
        }
        Path databaseYml = v1Folder.resolve("database.yml");
        if (Files.isRegularFile(databaseYml)) {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(databaseYml.toFile());
            String type = configuration.getString("DATABASE.TYPE", "SQLITE");
            if (type != null && !type.equalsIgnoreCase("SQLITE")) {
                return null;
            }
            String configured = configuration.getString("DATABASE.SQLITE.FILE", SQLITE_FALLBACK);
            Path file = Path.of(configured == null || configured.isBlank() ? SQLITE_FALLBACK : configured);
            return file.isAbsolute() ? file : v1Folder.resolve(file).normalize();
        }
        Path nested = v1Folder.resolve(SQLITE_FALLBACK);
        if (Files.isRegularFile(nested)) {
            return nested;
        }
        Path legacy = v1Folder.resolve("data.db");
        if (Files.isRegularFile(legacy)) {
            return legacy;
        }
        return nested;
    }

    public static String remapPermissionText(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return V1_PERMISSION_PREFIX.matcher(value).replaceAll("ultimatedonutsmp2.");
    }

    public static Object remapValue(Object value) {
        if (value instanceof String text) {
            return remapPermissionText(text);
        }
        if (value instanceof List<?> list) {
            List<Object> remapped = new ArrayList<>(list.size());
            for (Object entry : list) {
                remapped.add(remapValue(entry));
            }
            return remapped;
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> remapped = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                remapped.put(String.valueOf(entry.getKey()), remapValue(entry.getValue()));
            }
            return remapped;
        }
        if (value instanceof ConfigurationSection section) {
            Map<String, Object> remapped = new LinkedHashMap<>();
            for (String key : section.getKeys(false)) {
                remapped.put(key, remapValue(section.get(key)));
            }
            return remapped;
        }
        return value;
    }

    public static int overlayMatchingKeys(FileConfiguration source, FileConfiguration target) {
        if (source == null || target == null) {
            return 0;
        }
        return overlaySection(source, target, "");
    }

    public static ImportResult importSqlite(Path sourceDb, Connection destination) throws SQLException {
        if (sourceDb == null || !Files.isRegularFile(sourceDb) || destination == null) {
            return ImportResult.empty();
        }
        int tables = 0;
        int rows = 0;
        List<String> notes = new ArrayList<>();
        try (Connection source = DriverManager.getConnection("jdbc:sqlite:" + sourceDb.toAbsolutePath())) {
            Set<String> destTables = tableNames(destination);
            Set<String> sourceTables = tableNames(source);
            for (String table : sourceTables) {
                if (SKIP_TABLES.contains(table.toLowerCase(Locale.ROOT)) || !containsIgnoreCase(destTables, table)) {
                    continue;
                }
                String destTable = matchIgnoreCase(destTables, table);
                List<String> columns = intersectingColumns(source, destination, table, destTable);
                if (columns.isEmpty()) {
                    notes.add("skipped " + table + " (no shared columns)");
                    continue;
                }
                int copied = copyTable(source, destination, table, destTable, columns);
                if (copied > 0) {
                    tables++;
                    rows += copied;
                }
            }
        }
        return new ImportResult(tables, rows, 0, 0, notes);
    }

    public static ImportResult importYaml(Path v1Folder, Path v2Folder, Path backupFolder) throws IOException {
        if (v1Folder == null || v2Folder == null || !Files.isDirectory(v1Folder) || !Files.isDirectory(v2Folder)) {
            return ImportResult.empty();
        }
        int files = 0;
        int keys = 0;
        List<String> notes = new ArrayList<>();
        for (Path targetFile : yamlTargets(v2Folder)) {
            Path relative = v2Folder.relativize(targetFile);
            Path sourceFile = v1Folder.resolve(relative);
            if (!Files.isRegularFile(sourceFile)) {
                continue;
            }
            YamlConfiguration source = YamlConfiguration.loadConfiguration(sourceFile.toFile());
            YamlConfiguration target = YamlConfiguration.loadConfiguration(targetFile.toFile());
            int changed = overlayMatchingKeys(source, target);
            if (changed <= 0) {
                continue;
            }
            if (backupFolder != null) {
                Path backup = backupFolder.resolve(relative);
                Files.createDirectories(backup.getParent());
                Files.copy(targetFile, backup, StandardCopyOption.REPLACE_EXISTING);
            }
            target.save(targetFile.toFile());
            files++;
            keys += changed;
        }
        if (files == 0) {
            notes.add("no matching yaml keys to copy");
        }
        return new ImportResult(0, 0, files, keys, notes);
    }

    static boolean allowsMissingPath(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }
        for (String root : EXTRA_SECTION_ROOTS) {
            if (path.equalsIgnoreCase(root) || path.toUpperCase(Locale.ROOT).startsWith(root + ".")) {
                return true;
            }
        }
        return false;
    }

    private static int overlaySection(ConfigurationSection source, ConfigurationSection target, String path) {
        int changed = 0;
        for (String key : source.getKeys(false)) {
            String child = path.isEmpty() ? key : path + "." + key;
            Object sourceValue = source.get(key);
            if (sourceValue instanceof ConfigurationSection sourceSection) {
                if (target.isConfigurationSection(key)) {
                    changed += overlaySection(sourceSection, target.getConfigurationSection(key), child);
                } else if (!target.isSet(key) && allowsMissingPath(child)) {
                    ConfigurationSection created = target.createSection(key);
                    changed += overlaySection(sourceSection, created, child);
                }
                continue;
            }
            if (target.isSet(key) || allowsMissingPath(child)) {
                Object remapped = remapValue(sourceValue);
                if (!Objects.equals(target.get(key), remapped)) {
                    target.set(key, remapped);
                    changed++;
                }
            }
        }
        return changed;
    }

    private static List<Path> yamlTargets(Path v2Folder) throws IOException {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> stream = Files.list(v2Folder)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".yml"))
                    .filter(path -> !SKIP_YAML_NAMES.contains(path.getFileName().toString().toLowerCase(Locale.ROOT)))
                    .forEach(files::add);
        }
        Path languages = v2Folder.resolve("languages");
        if (Files.isDirectory(languages)) {
            try (Stream<Path> stream = Files.list(languages)) {
                stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".yml"))
                        .forEach(files::add);
            }
        }
        files.sort(Path::compareTo);
        return files;
    }

    private static Set<String> tableNames(Connection connection) throws SQLException {
        Set<String> names = new LinkedHashSet<>();
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getTables(connection.getCatalog(), connection.getSchema(), "%", new String[]{"TABLE"})) {
            while (resultSet.next()) {
                String name = resultSet.getString("TABLE_NAME");
                if (name != null && !name.startsWith("sqlite_")) {
                    names.add(name);
                }
            }
        }
        return names;
    }

    private static List<String> intersectingColumns(
            Connection source,
            Connection destination,
            String sourceTable,
            String destTable
    ) throws SQLException {
        Set<String> sourceColumns = columnNames(source, sourceTable);
        Set<String> destColumns = columnNames(destination, destTable);
        List<String> shared = new ArrayList<>();
        for (String column : destColumns) {
            String match = matchIgnoreCase(sourceColumns, column);
            if (match != null) {
                shared.add(match);
            }
        }
        return shared;
    }

    private static Set<String> columnNames(Connection connection, String table) throws SQLException {
        Set<String> names = new LinkedHashSet<>();
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(connection.getCatalog(), connection.getSchema(), table, "%")) {
            while (resultSet.next()) {
                names.add(resultSet.getString("COLUMN_NAME"));
            }
        }
        if (!names.isEmpty()) {
            return names;
        }
        // SQLite sometimes wants the physical table name exactly as created.
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + quote(table, false) + ")")) {
            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
            }
        } catch (SQLException ignored) {
            return names;
        }
        return names;
    }

    private static int copyTable(
            Connection source,
            Connection destination,
            String sourceTable,
            String destTable,
            List<String> columns
    ) throws SQLException {
        boolean mysql = isMysql(destination);
        String columnSql = columns.stream().map(column -> quote(column, mysql)).collect(Collectors.joining(", "));
        String placeholders = columns.stream().map(column -> "?").collect(Collectors.joining(", "));
        String selectSql = "SELECT " + columns.stream().map(column -> quote(column, false)).collect(Collectors.joining(", "))
                + " FROM " + quote(sourceTable, false);
        String insertSql = (mysql ? "INSERT IGNORE INTO " : "INSERT OR IGNORE INTO ")
                + quote(destTable, mysql) + " (" + columnSql + ") VALUES (" + placeholders + ")";
        int copied = 0;
        try (PreparedStatement select = source.prepareStatement(selectSql);
             ResultSet resultSet = select.executeQuery();
             PreparedStatement insert = destination.prepareStatement(insertSql)) {
            while (resultSet.next()) {
                for (int index = 0; index < columns.size(); index++) {
                    insert.setObject(index + 1, resultSet.getObject(index + 1));
                }
                copied += insert.executeUpdate();
            }
        }
        return copied;
    }

    private static boolean isMysql(Connection connection) throws SQLException {
        String product = connection.getMetaData().getDatabaseProductName();
        return product != null && product.toLowerCase(Locale.ROOT).contains("mysql");
    }

    private static String quote(String identifier, boolean mysql) {
        if (identifier == null) {
            return mysql ? "``" : "\"\"";
        }
        if (mysql) {
            return "`" + identifier.replace("`", "``") + "`";
        }
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    private static boolean containsIgnoreCase(Set<String> values, String candidate) {
        return matchIgnoreCase(values, candidate) != null;
    }

    private static String matchIgnoreCase(Set<String> values, String candidate) {
        if (candidate == null) {
            return null;
        }
        for (String value : values) {
            if (value.equalsIgnoreCase(candidate)) {
                return value;
            }
        }
        return null;
    }

    public record ImportResult(int tables, int rows, int yamlFiles, int yamlKeys, List<String> notes) {
        public ImportResult {
            notes = notes == null ? List.of() : List.copyOf(notes);
        }

        public static ImportResult empty() {
            return new ImportResult(0, 0, 0, 0, List.of());
        }

        public ImportResult plus(ImportResult other) {
            if (other == null) {
                return this;
            }
            List<String> merged = new ArrayList<>(notes);
            merged.addAll(other.notes);
            return new ImportResult(
                    tables + other.tables,
                    rows + other.rows,
                    yamlFiles + other.yamlFiles,
                    yamlKeys + other.yamlKeys,
                    merged
            );
        }
    }
}
