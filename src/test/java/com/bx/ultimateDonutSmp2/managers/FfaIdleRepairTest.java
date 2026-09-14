package com.bx.ultimateDonutSmp2.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FfaIdleRepairTest {

    @Test
    void backgroundRepairNeverScansArenasOnATimer() {
        assertFalse(FfaManager.shouldScanArenaForBackgroundRepair(false, false));
        assertFalse(FfaManager.shouldScanArenaForBackgroundRepair(true, false));
        assertFalse(FfaManager.shouldScanArenaForBackgroundRepair(false, true));
        assertFalse(FfaManager.shouldScanArenaForBackgroundRepair(true, true));
    }

    @Test
    void backgroundRepairWaitsThirtySecondsBetweenPasses() {
        assertEquals(20L * 30L, FfaManager.AUTO_REPAIR_INTERVAL_TICKS);
    }

    @Test
    void rollbackPaddingIsCappedSoAPointArenaCannotScanHundredsOfThousandsOfBlocks() {
        assertEquals(8, FfaManager.clampRollbackHorizontalPadding(true, 8));
        assertEquals(16, FfaManager.clampRollbackHorizontalPadding(true, 48));
        assertEquals(16, FfaManager.clampRollbackHorizontalPadding(false, 48));
        assertEquals(6, FfaManager.clampRollbackVerticalPadding(true, 6));
        assertEquals(8, FfaManager.clampRollbackVerticalPadding(true, 20));
    }

    @Test
    void bundledVanillaDuelPoolDoesNotGenerateChunksEveryTick() {
        YamlConfiguration duels = YamlConfiguration.loadConfiguration(new File("src/main/resources/duels.yml"));
        assertEquals(20, duels.getInt("MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.PREPARE_INTERVAL_TICKS"));
    }
}
