package com.bx.ultimateDonutSmp2.amethyst;

import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DrillDownPatternTest {

    @Test
    void lookingDownStaysAFlatSquareByDefault() {
        List<int[]> offsets = AmethystToolsListener.aoeOffsets(BlockFace.DOWN, 1, false);

        assertEquals(9, offsets.size());
        assertTrue(offsets.stream().allMatch(offset -> offset[1] == 0));
    }

    @Test
    void columnDownBreaksThreeBlocksAtTheDefaultRadius() {
        List<int[]> offsets = AmethystToolsListener.aoeOffsets(BlockFace.DOWN, 1, true);

        assertEquals(3, offsets.size());
        assertArrayEquals(new int[]{0, 0, 0}, offsets.get(0));
        assertArrayEquals(new int[]{0, -1, 0}, offsets.get(1));
        assertArrayEquals(new int[]{0, -2, 0}, offsets.get(2));
    }

    @Test
    void columnModeLeavesSidewaysAndUpwardStrikesAsASquare() {
        assertEquals(9, AmethystToolsListener.aoeOffsets(BlockFace.NORTH, 1, true).size());
        List<int[]> up = AmethystToolsListener.aoeOffsets(BlockFace.UP, 1, true);
        assertEquals(9, up.size());
        assertTrue(up.stream().allMatch(offset -> offset[1] == 0));
    }

    @Test
    void aWiderRadiusDigsADeeperColumn() {
        List<int[]> offsets = AmethystToolsListener.aoeOffsets(BlockFace.DOWN, 2, true);

        assertEquals(5, offsets.size());
        assertArrayEquals(new int[]{0, -4, 0}, offsets.get(4));
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
