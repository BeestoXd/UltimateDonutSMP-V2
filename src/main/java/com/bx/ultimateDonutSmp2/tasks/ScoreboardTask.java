package com.bx.ultimateDonutSmp2.tasks;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.OptimizationManager;

/**
 * Updates all player scoreboards on the configured interval (default 20 ticks).
 */
public class ScoreboardTask implements Runnable {

    private final UltimateDonutSmp2 plugin;

    public ScoreboardTask(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getOptimizationManager() != null
                && !plugin.getOptimizationManager().shouldRun(OptimizationManager.OptimizedTask.SCOREBOARD)) {
            return;
        }
        plugin.getScoreboardManager().updateAll();
    }

    public static void start(UltimateDonutSmp2 plugin) {
        if (!plugin.getScoreboardManager().isRuntimeSupported()) {
            return;
        }
        long interval = plugin.getScoreboardManager().getUpdateIntervalTicks();
        plugin.getSpigotScheduler().runGlobalTimer(new ScoreboardTask(plugin), interval, interval);
    }
}
