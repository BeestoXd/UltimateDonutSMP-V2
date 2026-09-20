package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Config-auction-house.yml.md tables browse, my-listings and claims chest sizes as live.
 * Those keys have to reach the inventories players actually open.
 */
class AuctionAdvertisedGuiSizeTest {

    private static final Path AUCTION_YAML = Path.of("src/main/resources/auction-house.yml");
    private static final Path WIKI = Path.of("docs/wiki/Config-auction-house.yml.md");
    private static final Path MANAGER = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/AuctionHouseManager.java");
    private static final Path BROWSE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/AuctionHouseBrowseMenu.java");
    private static final Path MY_LISTINGS = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/AuctionHouseMyListingsMenu.java");
    private static final Path PLAYER_ITEMS = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/PlayerAuctionGui.java");
    private static final Path CLAIMS = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/menus/AuctionHouseClaimsMenu.java");
    private static final Path COMMAND = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/commands/AuctionHouseCommand.java");

    @Test
    void bundledYamlShipsTheAdvertisedChestLayoutKeys() throws Exception {
        YamlConfiguration auction = new YamlConfiguration();
        auction.load(AUCTION_YAML.toFile());
        assertEquals(54, auction.getInt("GUI.BROWSE.SIZE"));
        assertEquals(54, auction.getInt("GUI.MY_LISTINGS.SIZE"));
        assertEquals(54, auction.getInt("GUI.CLAIMS.SIZE"));
        assertEquals(45, auction.getInt("GUI.CLAIMS.ITEMS_PER_PAGE"));
    }

    @Test
    void wikiTablesThoseKeysAsLiveChestLayout() throws Exception {
        String wiki = Files.readString(WIKI, StandardCharsets.UTF_8);
        assertTrue(wiki.contains("`GUI.BROWSE.SIZE`"), wiki);
        assertTrue(wiki.contains("`GUI.MY_LISTINGS.SIZE`"), wiki);
        assertTrue(wiki.contains("`GUI.CLAIMS.SIZE`"), wiki);
        assertTrue(wiki.contains("`GUI.CLAIMS.ITEMS_PER_PAGE`"), wiki);
    }

    @Test
    void browseSizeMustReadYaml() throws Exception {
        String source = Files.readString(MANAGER, StandardCharsets.UTF_8);
        int method = source.indexOf("public int getBrowseSize()");
        int next = source.indexOf("public int getBrowseItemsPerPage()", method);
        assertTrue(method >= 0 && next > method, "getBrowseSize should still sit above getBrowseItemsPerPage");
        String body = source.substring(method, next);
        assertTrue(body.contains("GUI.BROWSE.SIZE"), body);
        assertFalse(body.contains("return 54;"), body);
    }

    @Test
    void browseMenuMustPassGetBrowseSizeIntoTheChest() throws Exception {
        String source = Files.readString(BROWSE, StandardCharsets.UTF_8);
        assertTrue(source.contains("getBrowseSize()"), source);
        assertFalse(source.contains(", 54);"), source);
    }

    @Test
    void claimsAndMyListingsSizeGettersMustHaveCallers() throws Exception {
        String myListings = Files.readString(MY_LISTINGS, StandardCharsets.UTF_8);
        String playerItems = Files.readString(PLAYER_ITEMS, StandardCharsets.UTF_8);
        String claims = Files.readString(CLAIMS, StandardCharsets.UTF_8);
        String command = Files.readString(COMMAND, StandardCharsets.UTF_8);

        assertTrue(myListings.contains("getMyListingsSize()"), myListings);
        assertTrue(playerItems.contains("getMyListingsSize()"), playerItems);
        assertTrue(claims.contains("getClaimsSize()"), claims);
        assertTrue(claims.contains("getClaimsItemsPerPage()"), claims);
        assertFalse(claims.contains("AuctionYourItemsMenu"), claims);
        assertTrue(command.contains("new AuctionHouseClaimsMenu"), command);
    }
}
