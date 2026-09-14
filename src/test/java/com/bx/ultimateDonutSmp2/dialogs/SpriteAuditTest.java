package com.bx.ultimateDonutSmp2.dialogs;

import com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpriteAuditTest {

    /**
     * Every item an icon picker can offer has to draw something. A blank path leaves the button as
     * bare text next to neighbours that all carry an icon, which reads as a broken row.
     */
    @Test
    public void everySelectableItemResolvesToASprite() throws Exception {
        Set<String> shipped = new HashSet<>(Files.readAllLines(
                Path.of("src/main/resources/minecraft_textures.txt")));
        shipped.removeIf(String::isBlank);
        assertFalse(shipped.isEmpty(), "the shipped texture list is empty");

        List<String> blank = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (Material mat : Material.values()) {
            if (mat.isLegacy() || mat.name().startsWith("LEGACY_") || QuickBuyItemDialog.isAir(mat)) {
                continue;
            }
            if (!QuickBuyItemDialog.isItem(mat)) {
                continue;
            }
            // The tag is what a button label carries, and it is either an atlas sprite or a head
            // wearing a skin texture. Both name a file, so both can be checked the same way.
            String tag = QuickBuyItemDialog.getSpriteTag(mat);
            if (tag.isBlank()) {
                blank.add(mat.name());
                continue;
            }
            String path = tag.substring(tag.indexOf(':') + 1, tag.length() - 1);
            if (!shipped.contains(path)) {
                missing.add(mat.name() + " -> " + path);
            }
        }
        assertTrue(blank.isEmpty(), "these items would render with no icon: " + blank);
        assertTrue(missing.isEmpty(), "these items point at a texture that does not exist: " + missing);
    }

    @Test
    public void testAllSelectableMaterialsHaveValidSpritesOrCleanFallback() throws Exception {
        File textureFile = new File("C:/Users/BeestoXd/.gemini/antigravity/brain/89287970-773f-46d0-91c3-fe5da252e477/scratch/textures.txt");
        if (!textureFile.exists()) {
            return;
        }

        Set<String> textures = new HashSet<>(Files.readAllLines(textureFile.toPath()));

        List<String> failed = new ArrayList<>();
        int checked = 0;
        int emptyCount = 0;

        for (Material mat : Material.values()) {
            if (mat.isLegacy() || mat.name().startsWith("LEGACY_") || QuickBuyItemDialog.isAir(mat)) {
                continue;
            }
            if (!QuickBuyItemDialog.isItem(mat)) {
                continue;
            }

            checked++;
            String path = QuickBuyItemDialog.getSpritePath(mat);
            if (path == null || path.isBlank()) {
                emptyCount++;
                continue;
            }

            if (!textures.contains(path)) {
                failed.add(mat.name() + " -> " + path);
            }
        }

        System.out.println("Audited " + checked + " items: " + (checked - emptyCount - failed.size()) + " valid sprites, "
                + emptyCount + " empty text-only fallbacks, " + failed.size() + " failed.");

        if (!failed.isEmpty()) {
            System.err.println("FAILED ITEMS (" + failed.size() + "):");
            for (String f : failed) {
                System.err.println("  " + f);
            }
        }

        assertTrue(failed.isEmpty(), "Found " + failed.size() + " materials with broken texture paths: " + failed);
    }
}
