package com.bx.ultimateDonutSmp2.tasks;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;

/**
 * Drives the arena once a second: respawn countdowns, the boundary check, and the reset schedule.
 * A second is fine for all three - the countdown is spoken in whole seconds and the boundary has
 * its own padding so a fast player cannot slip past between two checks.
 */
public class PvpArenaTask implements Runnable {

    private final UltimateDonutSmp2 plugin;

    private PvpArenaTask(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    public static void start(UltimateDonutSmp2 plugin) {
        plugin.getSpigotScheduler().runGlobalTimer(new PvpArenaTask(plugin), 20L, 20L);
    }

    @Override
    public void run() {
        if (plugin.getPvpManager() == null || !plugin.getPvpManager().isEnabled()) {
            return;
        }

        plugin.getPvpManager().tick();
        if (plugin.getPvpMatchManager() != null) {
            plugin.getPvpMatchManager().tick();
        }
    }
}
