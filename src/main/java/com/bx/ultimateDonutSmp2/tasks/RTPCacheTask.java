package com.bx.ultimateDonutSmp2.tasks;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;

public class RTPCacheTask implements Runnable {

    private final UltimateDonutSmp2 plugin;

    public RTPCacheTask(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (org.bukkit.Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }
        if (plugin.getRtpManager() == null || !plugin.getRtpManager().isEnabled()) {
            return;
        }
        plugin.getRtpManager().refillPreCacheAllWorlds();
    }

    public static void start(UltimateDonutSmp2 plugin) {
        // 2 seconds after enable, then every 5 seconds. Long enough to miss the join TPS spike,
        // short enough that the RTP button can hit a cached far spot instead of searching live.
        plugin.getSpigotScheduler().runGlobalTimer(new RTPCacheTask(plugin), 40L, 100L);
    }
}
