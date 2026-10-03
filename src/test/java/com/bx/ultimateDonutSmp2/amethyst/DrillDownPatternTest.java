package com.bx.ultimateDonutSmp2.amethyst;

import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DrillDownPatternTest {

    @Test
    void lookingDownStaysAFlatSquareByDefault() {
        List<int[]> offsets = AmethystToolsListener.aoeOffsets(BlockFace.DOWN, 1, false, 0f);

        assertEquals(9, offsets.size());
        assertTrue(offsets.stream().allMatch(offset -> offset[1] == 0));
    }

    @Test
    void columnDownBreaksThreeLayersOfASingleRow() {
        List<int[]> offsets = AmethystToolsListener.aoeOffsets(BlockFace.DOWN, 1, true, 0f);

        assertEquals(9, offsets.size());
        assertEquals(3, offsets.stream().filter(offset -> offset[1] == 0).count());
        assertEquals(3, offsets.stream().filter(offset -> offset[1] == -2).count());
        assertTrue(offsets.stream().allMatch(offset -> offset[2] == 0));
        assertTrue(offsets.stream().anyMatch(offset -> offset[0] == 1 && offset[1] == -1 && offset[2] == 0));
    }

    @Test
    void columnRowFollowsYawWhenLookingDown() {
        List<int[]> alongX = AmethystToolsListener.aoeOffsets(BlockFace.DOWN, 1, true, 0f);
        List<int[]> alongZ = AmethystToolsListener.aoeOffsets(BlockFace.DOWN, 1, true, 90f);

        assertTrue(alongX.stream().allMatch(offset -> offset[2] == 0));
        assertTrue(alongZ.stream().allMatch(offset -> offset[0] == 0));
    }

    @Test
    void columnModeLeavesSidewaysAndUpwardStrikesAsASquare() {
        assertEquals(9, AmethystToolsListener.aoeOffsets(BlockFace.NORTH, 1, true, 0f).size());
        List<int[]> up = AmethystToolsListener.aoeOffsets(BlockFace.UP, 1, true, 0f);
        assertEquals(9, up.size());
        assertTrue(up.stream().allMatch(offset -> offset[1] == 0));
    }

    @Test
    void aWiderRadiusDigsADeeperRowStack() {
        List<int[]> offsets = AmethystToolsListener.aoeOffsets(BlockFace.DOWN, 2, true, 0f);

        assertEquals(25, offsets.size());
        assertEquals(5, offsets.stream().filter(offset -> offset[1] == -4).count());
        assertTrue(offsets.stream().anyMatch(offset -> offset[0] == 2 && offset[1] == -4 && offset[2] == 0));
    }

    @Test
    void columnDownOnlyMatchesThatWord() {
        assertTrue(AmethystToolsListener.columnDown("COLUMN"));
        assertTrue(AmethystToolsListener.columnDown(" column "));
        assertFalse(AmethystToolsListener.columnDown("FACE"));
        assertFalse(AmethystToolsListener.columnDown(null));
        assertFalse(AmethystToolsListener.columnDown(""));
    }

    @Test
    void bundledDrillShipsFaceSoExistingServersKeepTheSquare() throws Exception {
        String tools = Files.readString(Path.of("src/main/resources/amethyst-tools.yml"));
        int drill = tools.indexOf("  DRILL:");
        int chopper = tools.indexOf("  CHOPPER:");

        assertTrue(drill >= 0 && chopper > drill);
        String drillSection = tools.substring(drill, chopper);
        assertTrue(drillSection.contains("DOWN: FACE"));
        assertFalse(tools.substring(chopper).contains("\n    DOWN:"));
    }
}
