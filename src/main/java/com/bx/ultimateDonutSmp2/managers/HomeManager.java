package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.utils.LazyLocation;
import com.bx.ultimateDonutSmp2.utils.PermissionUtils;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.models.Home;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class HomeManager {

    private static final int HOMES_PER_PAGE = 5;
    private static final int MAX_PERMISSION_VALUE = 100;
    private static final String HOMES_PERMISSION_PREFIX = "ultimatedonutsmp2.homes.";
    private static final String HOME_PAGES_PERMISSION_PREFIX = "ultimatedonutsmp2.homes.page.";

    private final UltimateDonutSmp2 plugin;
    /** UUID → list of homes */
    private final Map<UUID, List<Home>> cache = new HashMap<>();

    public HomeManager(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    public void loadHomes(Player player) {
        loadHomes(player, null);
    }

    public void loadHomes(Player player, List<Home> preloaded) {
        if (player == null) {
            return;
        }
        if (preloaded != null) {
            cache.put(player.getUniqueId(), new ArrayList<>(preloaded));
            return;
        }
        List<Home> homes = plugin.getDatabaseManager().loadHomes(player.getUniqueId());
        if (homes != null) {
            cache.put(player.getUniqueId(), homes);
        } else {
            plugin.getLogger().warning("Failed to load homes from database for " + player.getName() + " (" + player.getUniqueId() + "). Preserving existing state.");
        }
    }

    public void unloadHomes(UUID uuid) {
        cache.remove(uuid);
    }

    public void clearAllCaches() {
        cache.clear();
    }

    public List<Home> getHomes(UUID uuid) {
        return cache.getOrDefault(uuid, Collections.emptyList());
    }

    public Home getHome(UUID uuid, String name) {
        for (Home h : getHomes(uuid)) {
            if (h.getName().equalsIgnoreCase(name)) return h;
        }
        return null;
    }

    public int getHomeCount(UUID uuid) {
        return getHomes(uuid).size();
    }

    private FileConfiguration getConfig() {
        return plugin != null && plugin.getConfigManager() != null
                ? plugin.getConfigManager().getConfig()
                : null;
    }

    public boolean isHomePermissionsEnabled() {
        FileConfiguration config = getConfig();
        return config == null || config.getBoolean("SETTINGS.HOME-PERMISSIONS.ENABLED", true);
    }

    public int getDefaultHomes() {
        FileConfiguration config = getConfig();
        return Math.max(1, config == null ? 3 : config.getInt("SETTINGS.HOME-DEFAULT", 3));
    }

    /**
     * Highest home count the player is entitled to by permission, or 0 when no home permission applies.
     */
    public int getPermissionHomes(Player player) {
        if (player == null || !isHomePermissionsEnabled()) {
            return 0;
        }

        int resolved = PermissionUtils.resolveHighestExactNumberedPermission(
                player, HOMES_PERMISSION_PREFIX, MAX_PERMISSION_VALUE);
        int pagesByPermission = PermissionUtils.resolveHighestExactNumberedPermission(
                player, HOME_PAGES_PERMISSION_PREFIX, MAX_PERMISSION_VALUE);
        resolved = Math.max(resolved, pagesByPermission * HOMES_PER_PAGE);

        FileConfiguration config = getConfig();
        ConfigurationSection section = config == null
                ? null
                : config.getConfigurationSection("SETTINGS.HOME-PERMISSIONS.PERMISSIONS");
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getValues(true).entrySet()) {
                if (!(entry.getValue() instanceof Number number)) {
                    continue;
                }
                if (!PermissionUtils.hasExact(player, entry.getKey())) {
                    continue;
                }
                resolved = Math.max(resolved, number.intValue());
            }
        }

        int donutHomes = getDonutPlusHomes(player, section);
        resolved = Math.max(resolved, donutHomes);

        return Math.max(0, resolved);
    }

    private int getDonutPlusHomes(Player player, ConfigurationSection section) {
        if (player == null) {
            return 0;
        }

        if (hasExactAny(player,
                "ultimatedonutsmp2.donutplusplusplus",
                "donutplusplusplus",
                "ultimatedonutsmp2.homes.donutplusplusplus",
                "donutplusplusplus.homes",
                "homes.donutplusplusplus",
                "ultimatedonutsmp2.homes.vip++")) {
            return resolveDonutConfiguredHomes(section, 90,
                    "ultimatedonutsmp2.donutplusplusplus",
                    "donutplusplusplus",
                    "ultimatedonutsmp2.homes.donutplusplusplus",
                    "ultimatedonutsmp2.homes.vip++");
        }

        if (hasExactAny(player,
                "ultimatedonutsmp2.donutplusplus",
                "donutplusplus",
                "ultimatedonutsmp2.homes.donutplusplus",
                "donutplusplus.homes",
                "homes.donutplusplus",
                "ultimatedonutsmp2.homes.vip+")) {
            return resolveDonutConfiguredHomes(section, 27,
                    "ultimatedonutsmp2.donutplusplus",
                    "donutplusplus",
                    "ultimatedonutsmp2.homes.donutplusplus",
                    "ultimatedonutsmp2.homes.vip+");
        }

        if (hasExactAny(player,
                "ultimatedonutsmp2.donutplus",
                "donutplus",
                "ultimatedonutsmp2.homes.donutplus",
                "donutplus.homes",
                "homes.donutplus",
                "ultimatedonutsmp2.homes.vip")) {
            return resolveDonutConfiguredHomes(section, 9,
                    "ultimatedonutsmp2.donutplus",
                    "donutplus",
                    "ultimatedonutsmp2.homes.donutplus",
                    "ultimatedonutsmp2.homes.vip");
        }

        return 0;
    }

    private boolean hasExactAny(Player player, String... permissions) {
        if (player == null || permissions == null) {
            return false;
        }
        for (String permission : permissions) {
            if (PermissionUtils.hasExact(player, permission)) {
                return true;
            }
        }
        return false;
    }

    private int resolveDonutConfiguredHomes(ConfigurationSection section, int fallback, String... keys) {
        if (section != null && keys != null) {
            for (String key : keys) {
                if (key != null && section.isInt(key)) {
                    return section.getInt(key);
                }
            }
        }
        return fallback;
    }

    public int getMaxHomes(Player player) {
        int permissionHomes = getPermissionHomes(player);
        return permissionHomes > 0 ? permissionHomes : getDefaultHomes();
    }

    public int getMaxHomePages(Player player) {
        int maxHomes = getMaxHomes(player);
        return Math.max(1, (int) Math.ceil(maxHomes / (double) HOMES_PER_PAGE));
    }

    public boolean canSetHome(Player player) {
        return getHomeCount(player.getUniqueId()) < getMaxHomes(player);
    }

    public boolean setHome(Player player, String name) {
        return setHome(player.getUniqueId(), name, player.getLocation());
    }

    public boolean setHome(UUID uuid, String name, Location location) {
        if (location == null) return false;
        String worldName = location.getWorld() != null ? location.getWorld().getName() : null;
        if (location instanceof LazyLocation lazy) {
            worldName = lazy.getWorldName();
        }
        if (worldName == null || worldName.isBlank()) return false;

        Location targetLocation = new LazyLocation(
                worldName,
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );

        List<Home> homes = cache.computeIfAbsent(uuid, k -> new ArrayList<>());
        // Update existing
        for (Home h : homes) {
            if (h.getName().equalsIgnoreCase(name)) {
                h.setLocation(targetLocation);
                plugin.getDatabaseManager().saveHome(h);
                return true;
            }
        }
        // Create new
        Player player = plugin.getServer().getPlayer(uuid);
        if (player != null && homes.size() >= getMaxHomes(player)) return false;
        Home home = new Home(uuid, name, targetLocation);
        homes.add(home);
        plugin.getDatabaseManager().saveHome(home);
        return true;
    }

    public boolean deleteHome(UUID uuid, String name) {
        List<Home> homes = cache.get(uuid);
        if (homes == null) return false;
        boolean removed = homes.removeIf(h -> h.getName().equalsIgnoreCase(name));
        if (removed) plugin.getDatabaseManager().deleteHome(uuid, name);
        return removed;
    }

    public boolean renameHome(UUID uuid, String oldName, String newName) {
        // Check newName not already taken
        if (getHome(uuid, newName) != null) return false;
        Home home = getHome(uuid, oldName);
        if (home == null) return false;
        plugin.getDatabaseManager().deleteHome(uuid, oldName);
        home.setName(newName);
        plugin.getDatabaseManager().saveHome(home);
        return true;
    }

    /** Public so the dialog screens validate names without duplicating the rule. */
    public boolean isValidHomeName(String input) {
        return input != null && !input.isBlank() && !input.contains(" ");
    }
}
