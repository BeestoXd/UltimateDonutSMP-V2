package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.AuctionHouseManager;
import com.bx.ultimateDonutSmp2.models.AuctionListing;
import org.bukkit.entity.Player;

public final class AuctionHouseListingMenu extends BaseMenu {

    private final long listingId;

    public AuctionHouseListingMenu(
            UltimateDonutSmp2 plugin,
            long listingId,
            boolean ignoredBackToMyListings,
            int ignoredOriginPage,
            AuctionHouseManager.AuctionSort ignoredSort
    ) {
        super(plugin, AuctionHouseMenuSupport.configText(
                plugin,
                "GUI.LISTING.TITLE",
                "&8Auction #{id}",
                "{id}", String.valueOf(listingId)
        ), 27);
        this.listingId = listingId;
    }

    @Override
    public void build(Player player) {
    }

    @Override
    public void open(Player player) {
        AuctionListing listing = plugin.getAuctionHouseManager().getListing(listingId);
        if (listing == null || listing.sellerUuid().equals(player.getUniqueId())) {
            new AuctionYourItemsMenu(plugin).open(player);
            return;
        }
        new ConfirmPurchaseGui(
                plugin,
                listing,
                plugin.getAuctionHouseManager().session(player.getUniqueId()).request()
        ).open(player);
    }
}
