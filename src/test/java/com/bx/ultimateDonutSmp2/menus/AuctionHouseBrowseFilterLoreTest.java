package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.managers.AuctionHouseManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards browse Filter lore against {@code Design/Auction/Main Gui or Menu/1.png}.
 * YAML only stores the "Click to change" prefix; the option list is appended in Java
 * so a configured LORE block cannot wipe Lowest / Highest / Recently Listed.
 */
class AuctionHouseBrowseFilterLoreTest {

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
                AuctionHouseManager.AuctionSort.PRICE_LOWEST,
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
                AuctionHouseManager.AuctionSort.PRICE_HIGHEST,
                List.of("&o&7Click to change")
        ).get(3));
        assertEquals("&f▪ Recently Listed", AuctionHouseMenuSupport.browseFilterLore(
                AuctionHouseManager.AuctionSort.NEWEST,
                List.of("&o&7Click to change")
        ).get(4));
    }

    @Test
    void configuredPrefixIsKeptWhenOptionsAreAppended() {
        List<String> lore = AuctionHouseMenuSupport.browseFilterLore(
                AuctionHouseManager.AuctionSort.PRICE_LOWEST,
                List.of("&o&7Click to change")
        );
        assertTrue(lore.get(0).contains("Click to change"));
        assertEquals(5, lore.size());
    }
}
