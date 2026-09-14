package com.bx.ultimateDonutSmp2.utils;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

/**
 * Bukkit plugin chunk tickets so a warmup square stays deserialized until a player
 * is actually in it. {@code World#setKeepSpawnInMemory(true)} is not enough here:
 * Multiverse sets {@code keep-spawn-in-memory: false}, and Paper then logs
 * {@code Loading 0 persistent chunks}. Async {@code getChunkAtAsync} without a ticket
 * lets those chunks unload again before the first login.
 */
public final class PaperChunkTickets {

    private PaperChunkTickets() {
    }

    public static boolean add(World world, int chunkX, int chunkZ, Plugin plugin) {
        if (world == null || plugin == null) {
            return false;
        }
        try {
            return world.addPluginChunkTicket(chunkX, chunkZ, plugin);
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    public static void removeAll(World world, Plugin plugin) {
        if (world == null || plugin == null) {
            return;
        }
        try {
            world.removePluginChunkTickets(plugin);
        } catch (RuntimeException ignored) {
        }
    }
}
