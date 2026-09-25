package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.models.SpawnerInstance;
import com.bx.ultimateDonutSmp2.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SpawnerMainMenu extends SpawnerStorageMenu {

    public SpawnerMainMenu(UltimateDonutSmp2 plugin, long spawnerId) {
        super(plugin, spawnerId, 1, plugin != null && plugin.getSpawnerManager() != null ? plugin.getSpawnerManager().getMainMenuSize() : 54);
    }

    @Override
    protected String resolveTitle(SpawnerInstance instance, int totalPages) {
        return plugin.getSpawnerManager().getMainMenuTitle(instance);
    }

    @Override
    protected int resolveSize() {
        return plugin.getSpawnerManager().getMainMenuSize();
    }

    @Override
    protected Material resolveFillerMaterial() {
        String filler = plugin.getSpawnerManager().getMainMenuFillerMaterial();
        if (filler == null || filler.isBlank() || filler.equalsIgnoreCase("AIR")) {
            return Material.AIR;
        }
        Material mat = Material.matchMaterial(filler);
        return mat != null ? mat : Material.AIR;
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
