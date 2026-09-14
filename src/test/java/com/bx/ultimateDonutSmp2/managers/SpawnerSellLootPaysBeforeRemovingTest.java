package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnerSellLootPaysBeforeRemovingTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/SpawnerManager.java");

    @Test
    void sellAllLootDepositsBeforeEmptyingStoredLoot() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public SellLootResult sellAllLoot(");
        int end = source.indexOf("public ActionResult collectXp(");
        assertTrue(start >= 0 && end > start, "sellAllLoot should still sit above collectXp");

        String method = source.substring(start, end);
        int remove = method.indexOf("removeStoredLoot");
        int deposit = method.indexOf("deposit(");
        assertTrue(remove >= 0 && deposit >= 0, "sellAllLoot should still remove loot and deposit money");
        assertTrue(
                deposit < remove,
                "sellAllLoot removes stored loot, queues sell history, then deposits. Shop sell "
                        + "deposits first and only then removes items. If the deposit fails the spawner "
                        + "loot is already gone and the method returns without putting it back."
        );
    }
}
