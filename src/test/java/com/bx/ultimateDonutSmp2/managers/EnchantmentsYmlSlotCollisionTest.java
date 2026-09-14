package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnchantmentsYmlSlotCollisionTest {

    private static final Path YML = Path.of("src/main/resources/enchantments.yml");
    private static final Path MENU = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/EnchantSelectMenu.java");
    private static final Set<String> CATEGORIES = Set.of(
            "helmet", "chestplate", "leggings", "boots", "elytra",
            "bow", "crossbow", "fishing_rod", "shovel", "pickaxe", "axe", "hoe", "shield", "sword", "trident"
    );

    @Test
    void shippedBootsPageOneDoesNotShareSlotsBetweenSwiftSneakAndProjectileProtection() throws Exception {
        String menu = Files.readString(MENU);
        assertTrue(menu.contains("set(opt.slot, ItemUtils.createItem(bookMat, displayName, lore));"));
        assertTrue(menu.contains("if (opt.page == page && opt.slot == slot)"));
        assertTrue(menu.contains("toggle(opt, player);"));

        List<String> collisions = new ArrayList<>();
        String category = null;
        String entry = null;
        Integer slot = null;
        Integer page = null;
        Map<String, String> occupants = new LinkedHashMap<>();

        for (String raw : Files.readAllLines(YML)) {
            String line = raw.stripTrailing();
            if (line.matches("^[a-z_]+:$") && CATEGORIES.contains(line.substring(0, line.length() - 1))) {
                flush(occupants, collisions, category, entry, slot, page);
                category = line.substring(0, line.length() - 1);
                entry = null;
                slot = null;
                page = null;
                occupants.clear();
                continue;
            }
            if (category == null) {
                continue;
            }
            if (line.matches("^  [A-Za-z0-9_]+:$")) {
                flush(occupants, collisions, category, entry, slot, page);
                entry = line.substring(2, line.length() - 1);
                slot = null;
                page = null;
                continue;
            }
            if (line.matches("^    slot: \\d+$")) {
                slot = Integer.parseInt(line.substring("    slot: ".length()));
            } else if (line.matches("^    page: \\d+$")) {
                page = Integer.parseInt(line.substring("    page: ".length()));
            }
        }
        flush(occupants, collisions, category, entry, slot, page);

        assertFalse(
                collisions.stream().anyMatch(row -> row.contains("boots page 1 slot 38")),
                String.join("\n", collisions)
        );
    }

    private static void flush(
            Map<String, String> occupants,
            List<String> collisions,
            String category,
            String entry,
            Integer slot,
            Integer page
    ) {
        if (category == null || entry == null || slot == null || page == null) {
            return;
        }
        String cell = category + " page " + page + " slot " + slot;
        String previous = occupants.putIfAbsent(cell, entry);
        if (previous != null) {
            collisions.add(cell + ": " + previous + " and " + entry);
        }
    }
}
