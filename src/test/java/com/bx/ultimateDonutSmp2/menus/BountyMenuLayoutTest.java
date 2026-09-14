package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.dialogs.DialogActions;
import com.bx.ultimateDonutSmp2.models.Bounty;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards {@code BOUNTIES-MENU} against drifting away from {@code Design/Bounty}.
 */
class BountyMenuLayoutTest {

    private static YamlConfiguration menus;

    @BeforeAll
    static void load() throws Exception {
        menus = new YamlConfiguration();
        menus.load(Path.of("src/main/resources/menus.yml").toFile());
    }

    @Test
    void titleAndSizeMatchDesign() {
        assertEquals("&8Bounties (Page {page})", menus.getString("BOUNTIES-MENU.TITLE"));
        assertEquals(54, menus.getInt("BOUNTIES-MENU.SIZE"));
        assertEquals(45, menus.getInt("BOUNTIES-MENU.MAX-ITEMS-PER-PAGE"));
        assertEquals(45, BountyMenu.MAX_ITEMS_PER_PAGE);
    }

    @Test
    void bountyHeadButtonMatchesDesign() {
        assertEquals("PLAYER_HEAD", menus.getString("BOUNTIES-MENU.BOUNTY-BUTTON.MATERIAL"));
        assertEquals("&#00fc88{player}", menus.getString("BOUNTIES-MENU.BOUNTY-BUTTON.NAME"));
        assertEquals(List.of("&fBounty: &7${price}"), menus.getStringList("BOUNTIES-MENU.BOUNTY-BUTTON.LORE"));
    }

    @Test
    void controlRowMatchesScreenshots1To5() {
        // Slot 45: Previous page
        assertEquals(45, menus.getInt("BOUNTIES-MENU.PREVIOUS-PAGE-BUTTON.SLOT"));
        assertEquals("ARROW", menus.getString("BOUNTIES-MENU.PREVIOUS-PAGE-BUTTON.MATERIAL"));
        assertEquals("&fPrevious page", menus.getString("BOUNTIES-MENU.PREVIOUS-PAGE-BUTTON.NAME"));
        assertEquals(List.of("&o&7Click to view previous page"), menus.getStringList("BOUNTIES-MENU.PREVIOUS-PAGE-BUTTON.LORE"));

        // Slot 48: Sort (Hopper) matching 1.png & 2.png
        assertEquals(48, menus.getInt("BOUNTIES-MENU.SORT-BUTTON.SLOT"));
        assertEquals("HOPPER", menus.getString("BOUNTIES-MENU.SORT-BUTTON.MATERIAL"));
        assertEquals("&fSort", menus.getString("BOUNTIES-MENU.SORT-BUTTON.NAME"));
        assertEquals(List.of("&fClick to sort (Amount)"), menus.getStringList("BOUNTIES-MENU.SORT-BUTTON.LORE-AMOUNT"));
        assertEquals(List.of("&fClick to sort (Recently Set)"), menus.getStringList("BOUNTIES-MENU.SORT-BUTTON.LORE-RECENT"));

        // Slot 49: Refresh / Bounties (Skeleton Skull) matching 3.png
        assertEquals(49, menus.getInt("BOUNTIES-MENU.REFRESH-BUTTON.SLOT"));
        assertEquals("SKELETON_SKULL", menus.getString("BOUNTIES-MENU.REFRESH-BUTTON.MATERIAL"));
        assertEquals("&fBounties", menus.getString("BOUNTIES-MENU.REFRESH-BUTTON.NAME"));
        assertEquals(
                List.of(
                        "&fClick to refresh",
                        "",
                        "&7Set a bounty using this:",
                        "&7/bounty add (player) (amount)"
                ),
                menus.getStringList("BOUNTIES-MENU.REFRESH-BUTTON.LORE")
        );

        // Slot 50: Search (Oak Sign) matching 4.png
        assertEquals(50, menus.getInt("BOUNTIES-MENU.SEARCH-BUTTON.SLOT"));
        assertEquals("OAK_SIGN", menus.getString("BOUNTIES-MENU.SEARCH-BUTTON.MATERIAL"));
        assertEquals("&fSearch", menus.getString("BOUNTIES-MENU.SEARCH-BUTTON.NAME"));
        assertEquals(List.of("&7Click to search"), menus.getStringList("BOUNTIES-MENU.SEARCH-BUTTON.LORE"));

        // Slot 53: Next page (Arrow) matching 5.png
        assertEquals(53, menus.getInt("BOUNTIES-MENU.NEXT-PAGE-BUTTON.SLOT"));
        assertEquals("ARROW", menus.getString("BOUNTIES-MENU.NEXT-PAGE-BUTTON.MATERIAL"));
        assertEquals("&fNext page", menus.getString("BOUNTIES-MENU.NEXT-PAGE-BUTTON.NAME"));
        assertEquals(List.of("&o&7Click to view next page"), menus.getStringList("BOUNTIES-MENU.NEXT-PAGE-BUTTON.LORE"));
    }

    @Test
    void controlRowLeavesGutterSlotsEmpty() {
        int prev = menus.getInt("BOUNTIES-MENU.PREVIOUS-PAGE-BUTTON.SLOT");
        int sort = menus.getInt("BOUNTIES-MENU.SORT-BUTTON.SLOT");
        int refresh = menus.getInt("BOUNTIES-MENU.REFRESH-BUTTON.SLOT");
        int search = menus.getInt("BOUNTIES-MENU.SEARCH-BUTTON.SLOT");
        int next = menus.getInt("BOUNTIES-MENU.NEXT-PAGE-BUTTON.SLOT");

        List<Integer> used = List.of(prev, sort, refresh, search, next);
        assertEquals(5, used.stream().distinct().count(), "5 distinct bottom controls");
        assertEquals(List.of(45, 48, 49, 50, 53), used);

        assertFalse(used.contains(46), "slot 46 stays empty");
        assertFalse(used.contains(47), "slot 47 stays empty");
        assertFalse(used.contains(51), "slot 51 stays empty");
        assertFalse(used.contains(52), "slot 52 stays empty");
    }

    @Test
    void javaDefaultsMatchYamlControlSlots() {
        assertEquals(menus.getInt("BOUNTIES-MENU.PREVIOUS-PAGE-BUTTON.SLOT"), BountyMenu.PREVIOUS_PAGE_SLOT);
        assertEquals(menus.getInt("BOUNTIES-MENU.SORT-BUTTON.SLOT"), BountyMenu.SORT_SLOT);
        assertEquals(menus.getInt("BOUNTIES-MENU.REFRESH-BUTTON.SLOT"), BountyMenu.REFRESH_SLOT);
        assertEquals(menus.getInt("BOUNTIES-MENU.SEARCH-BUTTON.SLOT"), BountyMenu.SEARCH_SLOT);
        assertEquals(menus.getInt("BOUNTIES-MENU.NEXT-PAGE-BUTTON.SLOT"), BountyMenu.NEXT_PAGE_SLOT);
    }

    @Test
    void bountyModelTracksTimestamp() {
        long before = System.currentTimeMillis();
        Bounty bounty = new Bounty(UUID.randomUUID(), 1000D, UUID.randomUUID());
        long after = System.currentTimeMillis();

        assertTrue(bounty.getTimestamp() >= before && bounty.getTimestamp() <= after);
        bounty.addAmount(500D);
        assertEquals(1500D, bounty.getAmount());
    }

    @Test
    void dialogActionsAreDefined() {
        assertEquals("bty_srch_go", DialogActions.BOUNTY_SEARCH_GO);
        assertEquals("bty_srch_can", DialogActions.BOUNTY_SEARCH_CAN);
    }

    @Test
    void eightLanguageParityForBountiesMenu() throws Exception {
        String[] languages = {"en_US", "id_ID", "de_DE", "es_ES", "fr_FR", "pt_BR", "ru_RU", "zh_CN"};
        for (String lang : languages) {
            File file = Path.of("src/main/resources/languages/" + lang + ".yml").toFile();
            assertTrue(file.exists(), lang + ".yml should exist");

            YamlConfiguration langYaml = new YamlConfiguration();
            langYaml.load(file);

            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.TITLE"), lang + " missing MENUS.BOUNTIES-MENU.TITLE");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.BOUNTY-BUTTON.NAME"), lang + " missing BOUNTY-BUTTON.NAME");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.BOUNTY-BUTTON.LORE"), lang + " missing BOUNTY-BUTTON.LORE");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.PREVIOUS-PAGE-BUTTON.NAME"), lang + " missing PREVIOUS-PAGE-BUTTON.NAME");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.PREVIOUS-PAGE-BUTTON.LORE"), lang + " missing PREVIOUS-PAGE-BUTTON.LORE");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.SORT-BUTTON.NAME"), lang + " missing SORT-BUTTON.NAME");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.SORT-BUTTON.LORE-AMOUNT"), lang + " missing SORT-BUTTON.LORE-AMOUNT");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.SORT-BUTTON.LORE-RECENT"), lang + " missing SORT-BUTTON.LORE-RECENT");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.REFRESH-BUTTON.NAME"), lang + " missing REFRESH-BUTTON.NAME");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.REFRESH-BUTTON.LORE"), lang + " missing REFRESH-BUTTON.LORE");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.SEARCH-BUTTON.NAME"), lang + " missing SEARCH-BUTTON.NAME");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.SEARCH-BUTTON.LORE"), lang + " missing SEARCH-BUTTON.LORE");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.NEXT-PAGE-BUTTON.NAME"), lang + " missing NEXT-PAGE-BUTTON.NAME");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.NEXT-PAGE-BUTTON.LORE"), lang + " missing NEXT-PAGE-BUTTON.LORE");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.DIALOG.SEARCH.TITLE"), lang + " missing DIALOG.SEARCH.TITLE");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.DIALOG.SEARCH.INPUT"), lang + " missing DIALOG.SEARCH.INPUT");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.DIALOG.SEARCH.CANCEL"), lang + " missing DIALOG.SEARCH.CANCEL");
            assertTrue(langYaml.contains("MENUS.BOUNTIES-MENU.DIALOG.SEARCH.SUBMIT"), lang + " missing DIALOG.SEARCH.SUBMIT");
        }
    }

    @Test
    void nextPageArrowOnlyAppearsWhenInventoryFilledWithNextPageItems() {
        int maxItems = 45;

        // When menu is empty or partially filled, next page arrow must NOT appear
        assertFalse(BountyMenu.hasNextPage(0, 0, maxItems), "0 bounties: next page arrow must not appear");
        assertFalse(BountyMenu.hasNextPage(0, 1, maxItems), "1 bounty: next page arrow must not appear");
        assertFalse(BountyMenu.hasNextPage(0, 20, maxItems), "20 bounties: next page arrow must not appear");
        assertFalse(BountyMenu.hasNextPage(0, 44, maxItems), "44 bounties: next page arrow must not appear");

        // Exactly 45 items fill page 0 completely, but there are no remaining items for page 1
        assertFalse(BountyMenu.hasNextPage(0, 45, maxItems), "45 bounties: no next page items, next arrow must not appear");

        // 46 items fill page 0 completely AND there is 1 item for page 1 -> next page arrow appears!
        assertTrue(BountyMenu.hasNextPage(0, 46, maxItems), "46 bounties: page 0 is full and has next page items");
        assertTrue(BountyMenu.hasNextPage(0, 90, maxItems), "90 bounties: page 0 is full and has next page items");

        // Page 0 has no previous page
        assertFalse(BountyMenu.hasPreviousPage(0), "page 0 has no previous page");

        // Page 1 has previous page
        assertTrue(BountyMenu.hasPreviousPage(1), "page 1 has previous page");

        // On page 1: 46 total bounties -> only 1 bounty on page 1, no page 2
        assertFalse(BountyMenu.hasNextPage(1, 46, maxItems), "page 1 with 1 item has no next page");

        // On page 1: 91 total bounties -> page 1 is full (45 items) and has 1 item for page 2 -> next page arrow appears
        assertTrue(BountyMenu.hasNextPage(1, 91, maxItems), "page 1 full with item on page 2 has next page");
    }
}
