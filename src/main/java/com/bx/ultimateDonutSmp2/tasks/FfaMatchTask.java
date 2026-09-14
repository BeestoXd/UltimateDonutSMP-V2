package com.bx.ultimateDonutSmp2.tasks;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;

public class FfaMatchTask implements Runnable {

    private final UltimateDonutSmp2 plugin;

    private FfaMatchTask(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    public static void start(UltimateDonutSmp2 plugin) {
        plugin.getSpigotScheduler().runGlobalTimer(new FfaMatchTask(plugin), 1L, 1L);
    }

    @Override
    public void run() {
        if (plugin.getFfaManager() != null && plugin.getFfaManager().isEnabled()) {
            plugin.getFfaManager().tick();
        }
    }
}
