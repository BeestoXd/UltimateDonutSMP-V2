package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseManagerActivityStatsProofTest {

    @Test
    void emptyWindowMustNotPaintOlderSalesOrPurchasesIntoBuckets() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE sell_history (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      player_uuid TEXT,
                      item_name TEXT,
                      amount INTEGER,
                      price REAL,
                      timestamp INTEGER
                    )
                    """);

            statement.execute("""
                    CREATE TABLE player_logs (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      player_uuid TEXT,
                      player_name TEXT,
                      category TEXT,
                      log_type TEXT,
                      details TEXT,
                      timestamp INTEGER
                    )
                    """);

            long now = System.currentTimeMillis();
            long twoDaysAgo = now - (48L * 3600L * 1000L);

            // Insert a sale and purchase that happened 2 days ago
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO sell_history (player_uuid, item_name, amount, price, timestamp) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, "DIAMOND");
                ps.setInt(3, 10);
                ps.setDouble(4, 500.0);
                ps.setLong(5, twoDaysAgo);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO player_logs (player_uuid, player_name, category, log_type, details, timestamp) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, "Player1");
                ps.setString(3, "SHOP");
                ps.setString(4, "SHOP_BUY");
                ps.setString(5, "Bought 10 DIAMOND");
                ps.setLong(6, twoDaysAgo);
                ps.executeUpdate();
            }

            DatabaseManager manager = new DatabaseManager(null);
            Field connectionField = DatabaseManager.class.getDeclaredField("connection");
            connectionField.setAccessible(true);
            connectionField.set(manager, connection);

            // Query minute activity stats for the last 60 minutes (6 buckets of 10 min each)
            List<DatabaseManager.HourlyActivityEntry> minuteStats = manager.getMinuteActivityStats(6);
            int totalSales = minuteStats.stream().mapToInt(DatabaseManager.HourlyActivityEntry::salesCount).sum();
            int totalPurchases = minuteStats.stream().mapToInt(DatabaseManager.HourlyActivityEntry::purchaseCount).sum();

            assertEquals(0, totalSales, "Sales older than the requested window must not be painted into empty buckets");
            assertEquals(0, totalPurchases, "Purchases older than the requested window must not be painted into empty buckets");

            // Query hourly activity stats for the last 24 hours (24 buckets)
            List<DatabaseManager.HourlyActivityEntry> hourlyStats = manager.getHourlyActivityStats(24);
            int hourlySales = hourlyStats.stream().mapToInt(DatabaseManager.HourlyActivityEntry::salesCount).sum();
            int hourlyPurchases = hourlyStats.stream().mapToInt(DatabaseManager.HourlyActivityEntry::purchaseCount).sum();

            assertEquals(0, hourlySales, "Sales older than 24 hours must not be painted into 24h buckets");
            assertEquals(0, hourlyPurchases, "Purchases older than 24 hours must not be painted into 24h buckets");
        }
    }

    @Test
    void salesAndPurchasesWithinWindowAreCountedAccurately() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE sell_history (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      player_uuid TEXT,
                      item_name TEXT,
                      amount INTEGER,
                      price REAL,
                      timestamp INTEGER
                    )
                    """);

            statement.execute("""
                    CREATE TABLE player_logs (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      player_uuid TEXT,
                      player_name TEXT,
                      category TEXT,
                      log_type TEXT,
                      details TEXT,
                      timestamp INTEGER
                    )
                    """);

            long now = System.currentTimeMillis();
            long fiveMinutesAgo = now - (5L * 60L * 1000L);

            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO sell_history (player_uuid, item_name, amount, price, timestamp) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, "EMERALD");
                ps.setInt(3, 5);
                ps.setDouble(4, 100.0);
                ps.setLong(5, fiveMinutesAgo);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO player_logs (player_uuid, player_name, category, log_type, details, timestamp) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, "Player2");
                ps.setString(3, "SHOP");
                ps.setString(4, "SHOP_BUY");
                ps.setString(5, "Bought 5 EMERALD");
                ps.setLong(6, fiveMinutesAgo);
                ps.executeUpdate();
            }

            DatabaseManager manager = new DatabaseManager(null);
            Field connectionField = DatabaseManager.class.getDeclaredField("connection");
            connectionField.setAccessible(true);
            connectionField.set(manager, connection);

            List<DatabaseManager.HourlyActivityEntry> minuteStats = manager.getMinuteActivityStats(6);
            int totalSales = minuteStats.stream().mapToInt(DatabaseManager.HourlyActivityEntry::salesCount).sum();
            int totalPurchases = minuteStats.stream().mapToInt(DatabaseManager.HourlyActivityEntry::purchaseCount).sum();

            assertEquals(1, totalSales);
            assertEquals(1, totalPurchases);
        }
    }
}
