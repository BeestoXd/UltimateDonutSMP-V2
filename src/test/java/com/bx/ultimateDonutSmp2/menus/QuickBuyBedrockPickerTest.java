package com.bx.ultimateDonutSmp2.menus;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickBuyBedrockPickerTest {

    @Test
    void emptySlotDoesNotOpenTheMaterialDialogForBedrock() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/bx/ultimateDonutSmp2/menus/QuickBuyMenu.java"));
        int start = source.indexOf("private void openItemPicker(");
        int end = source.indexOf("private void click(", start);
        assertTrue(start >= 0 && end > start);
        String method = source.substring(start, end);
        int bedrock = method.indexOf("BedrockPlayers.isBedrock");
        int dialog = method.indexOf("openSelection");
        assertTrue(bedrock >= 0 && dialog > bedrock,
                "Bedrock has to leave this method before the Choose Item dialog is built");
        assertTrue(method.contains("QuickBuyItemSelectMenu"),
                "Without a Floodgate form, Bedrock still needs the paged chest catalogue");
    }

    @Test
    void chestCatalogueDoesNotContinueIntoDialogsForBedrock() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/bx/ultimateDonutSmp2/menus/QuickBuyItemSelectMenu.java"));
        assertTrue(source.contains("!BedrockPlayers.isBedrock(player)"),
                "Search and enchant clicks on the chest catalogue must not open a dialog for Bedrock");
    }
}
