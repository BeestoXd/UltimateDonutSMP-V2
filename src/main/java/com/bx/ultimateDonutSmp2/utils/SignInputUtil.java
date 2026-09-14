package com.bx.ultimateDonutSmp2.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class SignInputUtil {

    private static final Set<String> REGISTERED = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, JavaPlugin> PLUGINS = new ConcurrentHashMap<>();
    private static final Map<UUID, Location> SIGN_LOC = new ConcurrentHashMap<>();
    private static final Map<UUID, BlockData> OLD_DATA = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> INPUT_LINE = new ConcurrentHashMap<>();
    private static final Map<UUID, Consumer<String>> CALLBACK = new ConcurrentHashMap<>();
    private static final Map<UUID, org.bukkit.scheduler.BukkitTask> HIDE_TASK = new ConcurrentHashMap<>();
    private static final Map<UUID, String[]> EXPECTED_LINES = new ConcurrentHashMap<>();
    private static final Map<UUID, List<String>> ORIGINAL_LINES = new ConcurrentHashMap<>();

    private static com.bx.ultimateDonutSmp2.utils.SpigotScheduler getScheduler(JavaPlugin plugin) {
        if (plugin instanceof UltimateDonutSmp2 uds) {
            return uds.getSpigotScheduler();
        }
        UltimateDonutSmp2 uds = (UltimateDonutSmp2) Bukkit.getPluginManager().getPlugin("UltimateDonutSmp2");
        if (uds != null) {
            return uds.getSpigotScheduler();
        }
        return null;
    }

    public static final String META_SIGN_INPUT = "donutorder-sign-input";

    private SignInputUtil() {}

    public static void openFromConfig(JavaPlugin plugin, Player player, org.bukkit.configuration.ConfigurationSection config, Consumer<String> callback) {
        List<String> lines = config == null ? null : config.getStringList("lines");
        int inputLine = config == null ? 0 : config.getInt("input-line", 0);
        open(plugin, player, lines, inputLine, callback);
    }

    public static void open(JavaPlugin plugin, Player player, List<String> lines, int inputLine, Consumer<String> callback) {
        cancel(player);
        if (!player.isOnline()) {
            if (callback != null) {
                callback.accept(null);
            }
            return;
        }

        UUID uuid = player.getUniqueId();
        int lineIndex = Math.max(0, Math.min(3, inputLine));

        List<String> list = new ArrayList<>();
        if (lines != null) {
            list.addAll(lines);
        }
        while (list.size() < 4) {
            list.add("");
        }
        if (list.size() > 4) {
            list = list.subList(0, 4);
        }

        if (list.stream().allMatch(s -> s == null || s.isBlank())) {
            list.set(1, "↑↑↑↑↑");
            list.set(2, "Enter Value");
        }

        for (int i = 0; i < list.size(); i++) {
            String line = list.get(i);
            if (line != null && line.contains("^")) {
                list.set(i, line.replace('^', '↑'));
            }
        }

        String[] signLines = new String[4];
        for (int i = 0; i < 4; i++) {
            String cleanLine = list.get(i);
            if (cleanLine == null) {
                cleanLine = "";
            }
            cleanLine = org.bukkit.ChatColor.translateAlternateColorCodes('&', cleanLine);
            signLines[i] = org.bukkit.ChatColor.stripColor(cleanLine);
        }

        Placement placement = findGroundPlacement(player);
        if (placement == null) {
            if (callback != null) {
                callback.accept(null);
            }
            return;
        }

        ensureRegistered(plugin);

        PLUGINS.put(uuid, plugin);
        SIGN_LOC.put(uuid, placement.loc);
        OLD_DATA.put(uuid, placement.oldData);
        INPUT_LINE.put(uuid, lineIndex);
        EXPECTED_LINES.put(uuid, signLines);
        ORIGINAL_LINES.put(uuid, list);
        if (callback != null) {
            CALLBACK.put(uuid, callback);
        }

        player.setMetadata(META_SIGN_INPUT, new FixedMetadataValue(plugin, true));

        var scheduler = getScheduler(plugin);

        // Close inventory immediately so player is ready for sign input
        try {
            player.closeInventory();
        } catch (Throwable ignored) {}

        // Schedule timeout (45 seconds)
        if (scheduler != null) {
            org.bukkit.scheduler.BukkitTask task = scheduler.runEntityLater(player, () -> {
                if (PLUGINS.containsKey(uuid)) {
                    cancel(player);
                    player.sendMessage(org.bukkit.ChatColor.RED + "Sign input timed out.");
                }
            }, 900L);
            HIDE_TASK.put(uuid, task);
        }

        Location loc = placement.loc.clone();
        BlockData oldData = placement.oldData;

        Runnable openAction = () -> {
            Block block = loc.getBlock();
            block.setType(Material.OAK_SIGN, false);

            if (!(block.getState() instanceof Sign sign)) {
                finish(player, null);
                return;
            }

            // Set sign lines (supporting 1.20+ Side API via reflection)
            try {
                Class<?> sideClass = Class.forName("org.bukkit.block.sign.Side");
                java.lang.reflect.Method getSideMethod = sign.getClass().getMethod("getSide", sideClass);
                Object frontSide = sideClass.getEnumConstants()[0]; // FRONT is index 0
                Object sideObject = getSideMethod.invoke(sign, frontSide);
                java.lang.reflect.Method setLineMethod = sideObject.getClass().getMethod("setLine", int.class, String.class);
                for (int i = 0; i < 4; i++) {
                    setLineMethod.invoke(sideObject, i, signLines[i]);
                }
            } catch (Throwable e) {
                // Fallback to pre-1.20
                for (int i = 0; i < 4; i++) {
                    sign.setLine(i, signLines[i]);
                }
            }

            // Set editable, unwaxed and allowed editor UUID
            try {
                java.lang.reflect.Method setEditable = sign.getClass().getMethod("setEditable", boolean.class);
                setEditable.invoke(sign, true);
            } catch (Throwable ignored) {}

            try {
                java.lang.reflect.Method setWaxed = sign.getClass().getMethod("setWaxed", boolean.class);
                setWaxed.invoke(sign, false);
            } catch (Throwable ignored) {}

            try {
                java.lang.reflect.Method setEditor = sign.getClass().getMethod("setAllowedEditorUniqueId", UUID.class);
                setEditor.invoke(sign, uuid);
            } catch (Throwable ignored) {}

            sign.update(true, false);

            // Open sign for player
            player.sendBlockChange(loc, sign.getBlockData());
            try {
                player.sendSignChange(loc, signLines);
            } catch (Throwable ignored) {}

            try {
                Class<?> sideClass = Class.forName("org.bukkit.block.sign.Side");
                Object frontSide = sideClass.getEnumConstants()[0];
                java.lang.reflect.Method openSignMethod = player.getClass().getMethod("openSign", Sign.class, sideClass);
                openSignMethod.invoke(player, sign, frontSide);
            } catch (Throwable t) {
                try {
                    player.openSign(sign);
                } catch (Throwable t2) {
                    finish(player, null);
                }
            }

            // Hide from others immediately
            startHideFromOthers(plugin, player, loc, oldData);
        };

        if (scheduler != null) {
            scheduler.runRegion(loc, openAction);
        } else {
            openAction.run();
        }
    }

    public static void cancel(Player player) {
        finish(player, null);
    }

    private static void finish(Player player, String text) {
        if (player == null) return;
        UUID uuid = player.getUniqueId();

        JavaPlugin plugin = PLUGINS.remove(uuid);
        Location loc = SIGN_LOC.remove(uuid);
        BlockData oldData = OLD_DATA.remove(uuid);
        INPUT_LINE.remove(uuid);
        EXPECTED_LINES.remove(uuid);
        ORIGINAL_LINES.remove(uuid);
        Consumer<String> callback = CALLBACK.remove(uuid);
        org.bukkit.scheduler.BukkitTask task = HIDE_TASK.remove(uuid);

        if (task != null) {
            task.cancel();
        }

        if (player.hasMetadata(META_SIGN_INPUT)) {
            player.removeMetadata(META_SIGN_INPUT, plugin);
        }

        var scheduler = getScheduler(plugin);

        if (loc != null && oldData != null && plugin != null && scheduler != null) {
            // Restore block in the world
            scheduler.runRegion(loc, () -> {
                Block block = loc.getBlock();
                block.setBlockData(oldData, false);
                player.sendBlockChange(loc, oldData);
                sendOriginalToOthers(plugin, player, loc, oldData);
            });
        }

        if (callback != null) {
            if (scheduler != null) {
                scheduler.runEntity(player, () -> callback.accept(text));
            } else {
                callback.accept(text);
            }
        }
    }

    private static Placement findGroundPlacement(Player player) {
        Location loc = player.getLocation().clone().add(0, 2.0, 0);
        int maxHeight = loc.getWorld().getMaxHeight() - 2;
        if (loc.getY() > maxHeight) {
            loc.setY(maxHeight);
        }
        Block block = loc.getBlock();
        return new Placement(block.getLocation(), block.getBlockData());
    }

    private static void startHideFromOthers(JavaPlugin plugin, Player player, Location loc, BlockData oldData) {
        for (Player other : loc.getWorld().getPlayers()) {
            if (!other.getUniqueId().equals(player.getUniqueId()) && other.getLocation().distanceSquared(loc) < 2500) {
                other.sendBlockChange(loc, oldData);
            }
        }
    }

    private static void sendOriginalToOthers(JavaPlugin plugin, Player player, Location loc, BlockData oldData) {
        startHideFromOthers(plugin, player, loc, oldData);
    }

    private static void ensureRegistered(JavaPlugin plugin) {
        String name = plugin.getName();
        if (REGISTERED.add(name)) {
            Bukkit.getPluginManager().registerEvents(new InternalListener(), plugin);
        }
    }

    private record Placement(Location loc, BlockData oldData) {}

    private static final class InternalListener implements Listener {

        @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST)
        public void onSignChange(SignChangeEvent event) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            Location loc = SIGN_LOC.get(uuid);
            if (loc == null || event.getBlock() == null) {
                return;
            }

            // Robust block coordinates match (avoids yaw/pitch/world reference discrepancy)
            if (loc.getWorld() != null && event.getBlock().getWorld() != null) {
                if (!loc.getWorld().getName().equals(event.getBlock().getWorld().getName())) {
                    return;
                }
            }
            if (event.getBlock().getX() != loc.getBlockX()
                    || event.getBlock().getY() != loc.getBlockY()
                    || event.getBlock().getZ() != loc.getBlockZ()) {
                return;
            }

            event.setCancelled(true);
            int lineIdx = INPUT_LINE.getOrDefault(uuid, 0);

            // Read lines from event (support both Adventure Component lines and legacy String)
            String[] submitted = new String[4];
            for (int i = 0; i < 4; i++) {
                String lineText = null;
                try {
                    java.lang.reflect.Method lineMethod = event.getClass().getMethod("line", int.class);
                    Object comp = lineMethod.invoke(event, i);
                    if (comp instanceof net.kyori.adventure.text.Component c) {
                        lineText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(c);
                    }
                } catch (Throwable ignored) {}
                if (lineText == null) {
                    try {
                        lineText = event.getLine(i);
                    } catch (Throwable ignored) {}
                }
                if (lineText == null) {
                    lineText = "";
                }
                lineText = org.bukkit.ChatColor.stripColor(lineText).trim();
                submitted[i] = lineText;
            }

            String[] expected = EXPECTED_LINES.get(uuid);
            String text = null;

            // 1. Check if the designated input line contains valid user text
            if (lineIdx >= 0 && lineIdx < 4) {
                String candidate = submitted[lineIdx];
                String exp = (expected != null && lineIdx < expected.length) ? expected[lineIdx] : null;
                if (!candidate.isBlank() && !isHelperPrompt(candidate, exp)) {
                    text = candidate;
                }
            }

            // 2. If designated line had nothing new, scan all lines for user-entered text
            if (text == null) {
                for (int i = 0; i < 4; i++) {
                    String candidate = submitted[i];
                    if (candidate.isBlank()) continue;
                    String exp = (expected != null && i < expected.length) ? expected[i] : null;
                    if (!isHelperPrompt(candidate, exp)) {
                        text = candidate;
                        break;
                    }
                }
            }

            // 3. Fallback: if designated line has any non-blank text
            if (text == null && lineIdx >= 0 && lineIdx < 4 && !submitted[lineIdx].isBlank()) {
                text = submitted[lineIdx];
            }

            finish(player, text);
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            cancel(event.getPlayer());
        }
    }

    public static boolean isHelperPrompt(String candidate, String expected) {
        if (candidate == null || candidate.isBlank()) {
            return true;
        }
        String trimmed = candidate.trim();
        if (expected != null && trimmed.equalsIgnoreCase(expected.trim())) {
            return true;
        }
        // Match arrow markers like ^^^^^^, ↑↑↑↑↑, vvvvv, etc.
        if (trimmed.matches("^[\\^↑v<>\\-_=~.]{2,}$")) {
            return true;
        }
        if (trimmed.equalsIgnoreCase("Search") || trimmed.equalsIgnoreCase("Enter Value") || trimmed.equalsIgnoreCase("Type price")
                || trimmed.equalsIgnoreCase("Player Name") || trimmed.equalsIgnoreCase("Amount") || trimmed.equalsIgnoreCase("Price")) {
            return true;
        }
        return false;
    }
}
