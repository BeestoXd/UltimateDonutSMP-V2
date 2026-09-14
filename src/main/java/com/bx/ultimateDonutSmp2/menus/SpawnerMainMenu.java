package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.bukkit.entity.Player;

public class SpawnerMainMenu extends SpawnerStorageMenu {

    public SpawnerMainMenu(UltimateDonutSmp2 plugin, long spawnerId) {
        super(plugin, spawnerId, 1);
    }

    @Override
    public void open(Player player) {
        super.open(player);
        plugin.getSpawnerManager().registerOpenMainMenu(player, this);
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        plugin.getSpawnerManager().unregisterOpenMainMenu(player, this);
    }

    public static double calculateFillPercentage(long currentTotal, long totalCapacity) {
        return SpawnerStorageMenu.calculateFillPercentage(currentTotal, totalCapacity);
    }

    public static String formatFillPercentage(double fillPercentage) {
        return SpawnerStorageMenu.formatFillPercentage(fillPercentage);
    }
}
