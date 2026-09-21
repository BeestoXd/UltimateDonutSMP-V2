package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.managers.AuctionHouseManager.AuctionSort;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards browse Filter lore against {@code Design/Auction/Main Gui or Menu/1.png}
 * when SORTING.ALLOWED is the three design sorts. The option list is appended in
 * Java from that allowed list, so a configured LORE block cannot wipe the rows.
 */
class AuctionHouseBrowseFilterLoreTest {

    private static final List<AuctionSort> DESIGN_SORTS = List.of(
            AuctionSort.PRICE_LOWEST,
            AuctionSort.PRICE_HIGHEST,
            AuctionSort.NEWEST
    );

    @Test
    void yamlKeepsOnlyTheClickToChangePrefix() throws Exception {
        YamlConfiguration auction = new YamlConfiguration();
        auction.load(Path.of("src/main/resources/auction-house.yml").toFile());
        assertEquals("&fFilter", auction.getString("GUI.BROWSE.CONTROLS.FILTER.NAME"));
        assertEquals(List.of("&o&7Click to change"), auction.getStringList("GUI.BROWSE.CONTROLS.FILTER.LORE"));
        assertFalse(auction.getStringList("GUI.BROWSE.CONTROLS.FILTER.LORE").stream()
                .anyMatch(line -> line.contains("Lowest Price")));
    }

    @Test
    void lowestPriceIsSelectedByDefault() {
        List<String> lore = AuctionHouseMenuSupport.browseFilterLore(
                AuctionSort.PRICE_LOWEST,
                DESIGN_SORTS,
                List.of("&o&7Click to change")
        );
        assertEquals(
                List.of(
                        "&o&7Click to change",
                        "",
                        "&f▪ Lowest Price",
                        "&8▪ Highest Price",
                        "&8▪ Recently Listed"
                ),
                lore
        );
    }

    @Test
    void highestAndRecentlyListedHighlightTheActiveLine() {
        assertEquals("&f▪ Highest Price", AuctionHouseMenuSupport.browseFilterLore(
                AuctionSort.PRICE_HIGHEST,
                DESIGN_SORTS,
                List.of("&o&7Click to change")
        ).get(3));
        assertEquals("&f▪ Recently Listed", AuctionHouseMenuSupport.browseFilterLore(
                AuctionSort.NEWEST,
                DESIGN_SORTS,
                List.of("&o&7Click to change")
        ).get(4));
    }

    @Test
    void configuredPrefixIsKeptWhenOptionsAreAppended() {
        List<String> lore = AuctionHouseMenuSupport.browseFilterLore(
                AuctionSort.PRICE_LOWEST,
                DESIGN_SORTS,
                List.of("&o&7Click to change")
        );
        assertTrue(lore.get(0).contains("Click to change"));
        assertEquals(5, lore.size());
    }

    @Test
    void bundledAllowedSortsIncludeExpiringSoonAndOldest() throws Exception {
        List<AuctionSort> allowed = bundledAllowedSorts();
        List<String> lore = AuctionHouseMenuSupport.browseFilterLore(
                AuctionSort.EXPIRING_SOON,
                allowed,
                List.of("&o&7Click to change")
        );
        assertEquals(
                List.of(
                        "&o&7Click to change",
                        "",
                        "&8▪ Lowest Price",
                        "&8▪ Highest Price",
                        "&8▪ Recently Listed",
                        "&f▪ Expiring Soon",
                        "&8▪ Oldest"
                ),
                lore
        );
    }

    @Test
    void filterCyclesEverySortListedInAuctionHouseYaml() throws Exception {
        List<AuctionSort> allowed = bundledAllowedSorts();
        assertTrue(allowed.contains(AuctionSort.EXPIRING_SOON));
        assertTrue(allowed.contains(AuctionSort.OLDEST));

        AuctionSort cursor = allowed.get(0);
        List<AuctionSort> seen = new ArrayList<>();
        for (int i = 0; i < allowed.size(); i++) {
            seen.add(cursor);
            cursor = AuctionHouseBrowseMenu.nextAllowedSort(cursor, allowed, AuctionSort.PRICE_LOWEST);
        }
        assertEquals(allowed, seen);
        assertEquals(allowed.get(0), cursor);
    }

    @Test
    void trimmedAllowedListKeepsTheThreeDesignSorts() {
        assertEquals(
                AuctionSort.PRICE_HIGHEST,
                AuctionHouseBrowseMenu.nextAllowedSort(AuctionSort.PRICE_LOWEST, DESIGN_SORTS, AuctionSort.PRICE_LOWEST)
        );
        assertEquals(
                AuctionSort.NEWEST,
                AuctionHouseBrowseMenu.nextAllowedSort(AuctionSort.PRICE_HIGHEST, DESIGN_SORTS, AuctionSort.PRICE_LOWEST)
        );
        assertEquals(
                AuctionSort.PRICE_LOWEST,
                AuctionHouseBrowseMenu.nextAllowedSort(AuctionSort.NEWEST, DESIGN_SORTS, AuctionSort.PRICE_LOWEST)
        );
    }

    private static List<AuctionSort> bundledAllowedSorts() throws Exception {
        YamlConfiguration auction = new YamlConfiguration();
        auction.load(Path.of("src/main/resources/auction-house.yml").toFile());
        return auction.getStringList("SORTING.ALLOWED").stream()
                .map(AuctionSort::fromConfig)
                .toList();
    }
}
