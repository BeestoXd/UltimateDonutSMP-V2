package com.bx.ultimateDonutSmp2.storage;

import com.bx.ultimateDonutSmp2.models.QuickBuyEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

class ShopPreferenceEmptyQuickBuyDoesNotDeadlockTest {

    @TempDir
    Path tempDir;

    @Test
    void emptyQuickBuyClearsSlotWithoutNestingOnTheWorker() throws Exception {
        Path database = tempDir.resolve("shop.db");
        ShopPreferenceRepository repository = new ShopPreferenceRepository(
                () -> open(database),
                sql -> sql,
                false,
                Logger.getLogger("ShopPreferenceEmptyQuickBuyDoesNotDeadlockTest")
        );
        UUID playerId = UUID.randomUUID();
        try {
            repository.initialize().join();
            repository.setQuickBuy(playerId, QuickBuyEntry.empty(0))
                    .orTimeout(2, TimeUnit.SECONDS)
                    .join();
        } finally {
            repository.shutdown();
        }
    }

    private Connection open(Path database) throws java.sql.SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + database.toAbsolutePath());
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA journal_mode=WAL");
            statement.execute("PRAGMA busy_timeout=10000");
        }
        return connection;
    }
}
