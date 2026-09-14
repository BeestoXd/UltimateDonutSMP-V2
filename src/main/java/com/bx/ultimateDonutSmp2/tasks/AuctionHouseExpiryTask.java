package com.bx.ultimateDonutSmp2.tasks;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;

public class AuctionHouseExpiryTask implements Runnable {

    private final UltimateDonutSmp2 plugin;

    private AuctionHouseExpiryTask(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    public static void start(UltimateDonutSmp2 plugin) {
        long configuredSeconds = plugin.getConfigManager().getAuctionHouse()
                .getLong("SETTINGS.EXPIRE_CHECK_SECONDS", 30L);
        long periodTicks = Math.max(20L, configuredSeconds * 20L);
        plugin.getSpigotScheduler().runAsyncTimer(new AuctionHouseExpiryTask(plugin), periodTicks, periodTicks);
    }

    @Override
    public void run() {
        if (plugin.getAuctionHouseManager() != null && plugin.getAuctionHouseManager().isEnabled()) {
            plugin.getAuctionHouseManager().expireListings().exceptionally(throwable -> {
                plugin.getLogger().warning("Auction House expiry scan failed: " + throwable.getMessage());
                return 0;
            });
        }
    }
}
