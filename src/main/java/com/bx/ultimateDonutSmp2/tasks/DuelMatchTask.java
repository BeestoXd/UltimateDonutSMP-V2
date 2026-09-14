package com.bx.ultimateDonutSmp2.tasks;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;

public class DuelMatchTask implements Runnable {

    private final UltimateDonutSmp2 plugin;

    private DuelMatchTask(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    public static void start(UltimateDonutSmp2 plugin) {
        plugin.getSpigotScheduler().runGlobalTimer(new DuelMatchTask(plugin), 1L, 1L);
    }

    @Override
    public void run() {
        if (plugin.getDuelManager() != null && plugin.getDuelManager().isEnabled()) {
            plugin.getDuelManager().tick();
        }
    }
}
