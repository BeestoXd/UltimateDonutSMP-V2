package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards {@code YOUR-ITEMS} against drifting away from {@code Design/Auction/Your Item Menu}.
 */
class AuctionYourItemsMenuLayoutTest {

    private static YamlConfiguration shop;

    @BeforeAll
    static void load() throws Exception {
        shop = new YamlConfiguration();
        shop.load(Path.of("src/main/resources/shop.yml").toFile());
    }

    @Test
    void titleAndSizeMatchTheReferenceChest() {
        assertEquals("Auction -> Your Items", shop.getString("YOUR-ITEMS.TITLE"));
        assertEquals(54, shop.getInt("YOUR-ITEMS.SIZE"));
        assertEquals(18, shop.getInt("YOUR-ITEMS.UNLOCKED-SLOTS"));
        assertEquals(45, shop.getInt("YOUR-ITEMS.DONUT-PLUS-SLOTS"));
        assertEquals(45, shop.getInt("YOUR-ITEMS.PLUS-PAGE-SLOTS"));
        assertEquals(45, AuctionYourItemsMenu.GRID_SLOTS);
        assertEquals(18, AuctionYourItemsMenu.DEFAULT_UNLOCKED_SLOTS);
        assertEquals(45, AuctionYourItemsMenu.DEFAULT_DONUT_PLUS_SLOTS);
        assertEquals(45, AuctionYourItemsMenu.DEFAULT_PLUS_PAGE_SLOTS);
    }

    @Test
    void emptyListSlotsMatchScreenshot7() {
        assertEquals("GRAY_STAINED_GLASS_PANE", shop.getString("YOUR-ITEMS.EMPTY-SLOT.MATERIAL"));
        assertEquals("&fList", shop.getString("YOUR-ITEMS.EMPTY-SLOT.NAME"));
        assertEquals(List.of("&o&7Click to sell an item"), shop.getStringList("YOUR-ITEMS.EMPTY-SLOT.LORE"));
        assertEquals("GRAY_STAINED_GLASS_PANE", shop.getString("YOUR-ITEMS.SELL-BUTTON.MATERIAL"));
        assertEquals("&fList", shop.getString("YOUR-ITEMS.SELL-BUTTON.NAME"));
        assertEquals(0, shop.getInt("YOUR-ITEMS.SELL-BUTTON.SLOT"));
    }

    @Test
    void lockedSlotsMatchScreenshot8() {
        assertEquals("RED_STAINED_GLASS_PANE", shop.getString("YOUR-ITEMS.LOCKED-SLOT.MATERIAL"));
        assertEquals("&cLocked", shop.getString("YOUR-ITEMS.LOCKED-SLOT.NAME"));
        assertEquals(
                List.of("&fBuy Donut&b+&f for more auction slots"),
                shop.getStringList("YOUR-ITEMS.LOCKED-SLOT.LORE")
        );
        assertEquals("RED_STAINED_GLASS_PANE", shop.getString("YOUR-ITEMS.LOCKED-PLUS-SLOT.MATERIAL"));
        assertEquals("&cLocked", shop.getString("YOUR-ITEMS.LOCKED-PLUS-SLOT.NAME"));
        assertEquals(
                List.of("&fBuy Donut&b++&f for even more auction slots"),
                shop.getStringList("YOUR-ITEMS.LOCKED-PLUS-SLOT.LORE")
        );
        assertTrue(shop.getStringList("YOUR-ITEMS.LOCKED-SLOT.LORE").get(0).contains("Donut&b+"));
        assertFalse(shop.getStringList("YOUR-ITEMS.LOCKED-SLOT.LORE").get(0).contains("&bDonut"));
        assertTrue(shop.getStringList("YOUR-ITEMS.LOCKED-PLUS-SLOT.LORE").get(0).contains("Donut&b++"));
        assertFalse(shop.getStringList("YOUR-ITEMS.LOCKED-PLUS-SLOT.LORE").get(0).contains("&bDonut"));
    }

    @Test
    void controlRowMatchesScreenshots1To6() {
        assertButton("FILTER", 47, "HOPPER", "&fFilter", List.of("&o&7Click to change"));
        assertButton("QUICK-BUY", 48, "ENDER_CHEST", "&fQuick Buy", List.of("&o&7Click to view"));
        assertButton("AUCTION", 49, "ANVIL", "&fAuction", List.of("&o&7Click to view"));
        assertButton("SEARCH", 50, "OAK_SIGN", "&fSearch", List.of("&o&7Click to search"));
        assertButton("TRANSACTIONS", 51, "WRITABLE_BOOK", "&fTransactions",
                List.of("&o&7Click to view your transactions"));
        assertButton("NEXT", 53, "ARROW", "&fNext page", List.of("&o&7Click to view next page"));
        assertButton("PREV", 45, "ARROW", "&fPrevious page", List.of("&o&7Click to view previous page"));
    }

    @Test
    void controlRowLeavesGutterSlotsEmpty() {
        assertFalse(shop.contains("YOUR-ITEMS.BUTTONS.BACK"), "design uses Next page at 53, not Back");
        assertEquals(27, shop.getInt("YOUR-ITEMS.DONUT-PLUS-SLOTS") - shop.getInt("YOUR-ITEMS.UNLOCKED-SLOTS"),
                "page 1 keeps 27 Donut+ locks when slots 18-44 are locked");
        assertEquals(45, shop.getInt("YOUR-ITEMS.PLUS-PAGE-SLOTS"),
                "page 2 fills all five content rows with Donut++ locks");
        assertEquals(45, shop.getInt("YOUR-ITEMS.BUTTONS.PREV.SLOT"));
        assertEquals(47, shop.getInt("YOUR-ITEMS.BUTTONS.FILTER.SLOT"));
        assertEquals(48, shop.getInt("YOUR-ITEMS.BUTTONS.QUICK-BUY.SLOT"));
        assertEquals(49, shop.getInt("YOUR-ITEMS.BUTTONS.AUCTION.SLOT"));
        assertEquals(50, shop.getInt("YOUR-ITEMS.BUTTONS.SEARCH.SLOT"));
        assertEquals(51, shop.getInt("YOUR-ITEMS.BUTTONS.TRANSACTIONS.SLOT"));
        assertEquals(53, shop.getInt("YOUR-ITEMS.BUTTONS.NEXT.SLOT"));

        List<Integer> used = List.of(45, 47, 48, 49, 50, 51, 53);
        assertTrue(used.stream().distinct().count() == used.size());
        assertFalse(used.contains(46), "slot 46 stays empty");
        assertFalse(used.contains(52), "slot 52 stays empty");
    }

    @Test
    void filterOptionsMatchScreenshot1() {
        AuctionYourItemsMenu.YourItemsFilter[] filters = AuctionYourItemsMenu.YourItemsFilter.values();
        assertEquals(4, filters.length);
        assertEquals("Default", filters[0].getDisplayName());
        assertEquals("Lowest Price", filters[1].getDisplayName());
        assertEquals("Highest Price", filters[2].getDisplayName());
        assertEquals("Recently Listed", filters[3].getDisplayName());
        assertEquals(AuctionYourItemsMenu.YourItemsFilter.LOWEST_PRICE, filters[0].next());
        assertEquals(AuctionYourItemsMenu.YourItemsFilter.DEFAULT, filters[3].next());
    }

    @Test
    void javaDefaultsMatchTheYamlControlSlots() {
        assertEquals(shop.getInt("YOUR-ITEMS.BUTTONS.PREV.SLOT"), AuctionYourItemsMenu.PREV_SLOT);
        assertEquals(shop.getInt("YOUR-ITEMS.BUTTONS.FILTER.SLOT"), AuctionYourItemsMenu.FILTER_SLOT);
        assertEquals(shop.getInt("YOUR-ITEMS.BUTTONS.QUICK-BUY.SLOT"), AuctionYourItemsMenu.QUICK_BUY_SLOT);
        assertEquals(shop.getInt("YOUR-ITEMS.BUTTONS.AUCTION.SLOT"), AuctionYourItemsMenu.AUCTION_SLOT);
        assertEquals(shop.getInt("YOUR-ITEMS.BUTTONS.SEARCH.SLOT"), AuctionYourItemsMenu.SEARCH_SLOT);
        assertEquals(shop.getInt("YOUR-ITEMS.BUTTONS.TRANSACTIONS.SLOT"), AuctionYourItemsMenu.TRANSACTIONS_SLOT);
        assertEquals(shop.getInt("YOUR-ITEMS.BUTTONS.NEXT.SLOT"), AuctionYourItemsMenu.NEXT_SLOT);
    }

    private static void assertButton(String key, int slot, String material, String name, List<String> lore) {
        String path = "YOUR-ITEMS.BUTTONS." + key;
        assertEquals(slot, shop.getInt(path + ".SLOT"), key);
        assertEquals(material, shop.getString(path + ".MATERIAL"), key);
        assertEquals(name, shop.getString(path + ".NAME"), key);
        assertEquals(lore, shop.getStringList(path + ".LORE"), key);
    }
}
