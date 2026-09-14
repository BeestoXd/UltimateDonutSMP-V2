package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DuelCreateMenuHidesTargetNameTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/DuelCreateMenu.java");

    @Test
    void createMenuTargetLinesUseHideAwareName() throws Exception {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("public void build(Player player)");
        int end = source.indexOf("public void handleClick(");
        assertTrue(start >= 0 && end > start, "build should still sit above handleClick");

        String method = source.substring(start, end);
        assertTrue(
                method.contains("publicName"),
                "DuelCreateMenu.build labels the target with target.getName() in lore and the skull. "
                        + "DuelManager.getCreateTitle already uses publicName for the same window title, "
                        + "so a hidden opponent's real name still appears inside the create GUI."
        );
    }
}
