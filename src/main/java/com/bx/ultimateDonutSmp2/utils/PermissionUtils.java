package com.bx.ultimateDonutSmp2.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.Permissible;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class PermissionUtils {

    private static final Charset WINDOWS_1252 = Charset.forName("Windows-1252");

    private PermissionUtils() {
    }

    public static boolean isTemporaryPlayer(Permissible permissible) {
        if (permissible == null) {
            return false;
        }
        return permissible.getClass().getName().contains("TemporaryPlayer");
    }

    public static boolean has(Permissible permissible, String permission) {
        if (permissible == null || permission == null || permission.isBlank() || isTemporaryPlayer(permissible)) {
            return false;
        }

        try {
            String normalized = normalizePermissionNode(permission);
            if (permissible.hasPermission(permission)) {
                return true;
            }
            if (!normalized.equals(permission) && permissible.hasPermission(normalized)) {
                return true;
            }

            return hasEffectivePermissionAlias(permissible, normalized);
        } catch (UnsupportedOperationException ignored) {
            return false;
        }
    }

    public static boolean hasOrUnset(Permissible permissible, String permission) {
        return permission == null || permission.isBlank() || has(permissible, permission);
    }

    public static boolean hasAny(Permissible permissible, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        for (String permission : permissions) {
            if (has(permissible, permission)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasExact(Permissible permissible, String permission) {
        if (permissible == null || permission == null || permission.isBlank() || isTemporaryPlayer(permissible)) {
            return false;
        }

        try {
            String normalized = normalizePermissionNode(permission);
            boolean matchedTrue = false;
            for (PermissionAttachmentInfo info : permissible.getEffectivePermissions()) {
                String normalizedGranted = normalizePermissionNode(info.getPermission());
                if (!normalizedGranted.equals(normalized)) {
                    continue;
                }
                if (!info.getValue()) {
                    return false;
                }
                matchedTrue = true;
            }
            if (matchedTrue) {
                return true;
            }

            if (permissible instanceof Player player) {
                if (isPluginEnabled("LuckPerms")) {
                    Boolean luckPerms = LuckPermsResolver.resolveExact(player, normalized);
                    if (luckPerms != null) {
                        return luckPerms;
                    }
                }

                if (isPluginEnabled("Vault")) {
                    Boolean vault = VaultResolver.resolveExact(player, normalized);
                    if (vault != null) {
                        return vault;
                    }
                }

                if (isSafeBukkitExact(player, permission, normalized)) {
                    return true;
                }
            }

            return false;
        } catch (UnsupportedOperationException ignored) {
            return false;
        }
    }

    public static int resolveHighestExactNumberedPermission(Permissible permissible, String prefix, int maxValue) {
        if (permissible == null || prefix == null || prefix.isBlank() || maxValue < 1) {
            return 0;
        }

        String normalizedPrefix = normalizePermissionNode(prefix);
        for (int value = maxValue; value >= 1; value--) {
            if (hasExact(permissible, normalizedPrefix + value)) {
                return value;
            }
        }
        return 0;
    }

    public static String normalizePermissionNode(String permission) {
        if (permission == null || permission.isBlank()) {
            return "";
        }

        String value = normalizeRawPermissionNode(permission.trim().toLowerCase(Locale.ROOT));
        String repaired = normalizeRawPermissionNode(repairMojibake(permission.trim()).toLowerCase(Locale.ROOT));
        if (scoreNormalizedPermission(repaired) < scoreNormalizedPermission(value)) {
            return repaired;
        }
        return value;
    }

    private static String normalizeRawPermissionNode(String value) {
        StringBuilder normalized = new StringBuilder(value.length());
        value.codePoints().forEach(codePoint -> normalized.appendCodePoint(normalizeCodePoint(codePoint)));
        return normalized.toString();
    }

    private static String repairMojibake(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        try {
            return new String(value.getBytes(WINDOWS_1252), StandardCharsets.UTF_8);
        } catch (RuntimeException ignored) {
            return value;
        }
    }

    private static int scoreNormalizedPermission(String value) {
        if (value == null || value.isBlank()) {
            return Integer.MAX_VALUE;
        }

        int score = 0;
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if ((character >= 'a' && character <= 'z')
                    || (character >= '0' && character <= '9')
                    || character == '.'
                    || character == '-'
                    || character == '_'
                    || character == '*') {
                continue;
            }
            score++;
        }
        return score;
    }

    private static boolean hasEffectivePermissionAlias(Permissible permissible, String normalizedPermission) {
        boolean matchedTrue = false;
        for (PermissionAttachmentInfo info : permissible.getEffectivePermissions()) {
            String normalizedGranted = normalizePermissionNode(info.getPermission());
            if (!matches(normalizedGranted, normalizedPermission)) {
                continue;
            }
            if (!info.getValue()) {
                return false;
            }
            matchedTrue = true;
        }
        return matchedTrue;
    }

    private static boolean matches(String granted, String requested) {
        if (granted.equals(requested) || granted.equals("*")) {
            return true;
        }
        if (!granted.endsWith(".*")) {
            return false;
        }
        String prefix = granted.substring(0, granted.length() - 1);
        return requested.startsWith(prefix);
    }

    private static int normalizeCodePoint(int codePoint) {
        return switch (codePoint) {
            case '\u1D00', '\u0430' -> 'a';
            case '\u0299' -> 'b';
            case '\u1D04', '\u0441' -> 'c';
            case '\u1D05' -> 'd';
            case '\u1D07', '\u0435' -> 'e';
            case '\u0493', '\uA730' -> 'f';
            case '\u0262', '\u0261' -> 'g';
            case '\u029C' -> 'h';
            case '\u026A', '\u0456' -> 'i';
            case '\u1D0A', '\u0458' -> 'j';
            case '\u1D0B' -> 'k';
            case '\u029F' -> 'l';
            case '\u1D0D' -> 'm';
            case '\u0274' -> 'n';
            case '\u1D0F', '\u043E' -> 'o';
            case '\u1D18', '\u0440' -> 'p';
            case '\u01EB' -> 'q';
            case '\u0280' -> 'r';
            case '\u0455' -> 's';
            case '\u1D1B' -> 't';
            case '\u1D1C' -> 'u';
            case '\u1D20' -> 'v';
            case '\u1D21' -> 'w';
            case '\u0445' -> 'x';
            case '\u028F', '\u0443' -> 'y';
            case '\u1D22' -> 'z';
            case '\u00A0', '\u2007', '\u202F' -> ' ';
            default -> codePoint;
        };
    }

    private static boolean isPluginEnabled(String pluginName) {
        try {
            return Bukkit.getServer() != null
                    && Bukkit.getPluginManager() != null
                    && Bukkit.getPluginManager().isPluginEnabled(pluginName);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isSafeBukkitExact(Player player, String permission, String normalized) {
        try {
            boolean isOp = false;
            try {
                isOp = player.isOp();
            } catch (Throwable ignored) {
            }
            if (isOp) {
                return false;
            }

            String probe = UUID.randomUUID().toString().replace("-", "");
            if (player.hasPermission("ultimatedonutsmp2.probe." + probe)
                    || player.hasPermission("ultimatedonutsmp2.homes.probe." + probe)
                    || player.hasPermission("probe." + probe)) {
                return false;
            }

            return player.hasPermission(permission) || (!normalized.equals(permission) && player.hasPermission(normalized));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static final class LuckPermsResolver {
        static Boolean resolveExact(Player player, String normalizedPermission) {
            try {
                net.luckperms.api.LuckPerms lp = net.luckperms.api.LuckPermsProvider.get();
                if (lp == null) {
                    return null;
                }

                List<net.luckperms.api.model.user.User> users = new ArrayList<>();
                try {
                    net.luckperms.api.model.user.User primary = lp.getPlayerAdapter(Player.class).getUser(player);
                    if (primary != null) {
                        users.add(primary);
                    }
                } catch (Throwable ignored) {
                }

                try {
                    net.luckperms.api.model.user.User byUuid = lp.getUserManager().getUser(player.getUniqueId());
                    if (byUuid != null && !users.contains(byUuid)) {
                        users.add(byUuid);
                    }
                } catch (Throwable ignored) {
                }

                try {
                    if (Bukkit.getPluginManager().isPluginEnabled("floodgate")
                            && org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                        org.geysermc.floodgate.api.player.FloodgatePlayer fp =
                                org.geysermc.floodgate.api.FloodgateApi.getInstance().getPlayer(player.getUniqueId());
                        if (fp != null) {
                            UUID javaUuid = fp.getJavaUniqueId();
                            if (javaUuid != null) {
                                net.luckperms.api.model.user.User u = lp.getUserManager().getUser(javaUuid);
                                if (u != null && !users.contains(u)) {
                                    users.add(u);
                                }
                            }
                            UUID correctUuid = fp.getCorrectUniqueId();
                            if (correctUuid != null && !correctUuid.equals(javaUuid)) {
                                net.luckperms.api.model.user.User u = lp.getUserManager().getUser(correctUuid);
                                if (u != null && !users.contains(u)) {
                                    users.add(u);
                                }
                            }
                            String username = fp.getUsername();
                            if (username != null && !username.isBlank()) {
                                net.luckperms.api.model.user.User u = lp.getUserManager().getUser(username);
                                if (u != null && !users.contains(u)) {
                                    users.add(u);
                                }
                            }
                            String correctUsername = fp.getCorrectUsername();
                            if (correctUsername != null && !correctUsername.isBlank() && !correctUsername.equalsIgnoreCase(username)) {
                                net.luckperms.api.model.user.User u = lp.getUserManager().getUser(correctUsername);
                                if (u != null && !users.contains(u)) {
                                    users.add(u);
                                }
                            }
                        }
                    }
                } catch (Throwable ignored) {
                }

                String name = player.getName();
                if (name != null && (name.startsWith(".") || name.startsWith("*")) && name.length() > 1) {
                    try {
                        net.luckperms.api.model.user.User u = lp.getUserManager().getUser(name.substring(1));
                        if (u != null && !users.contains(u)) {
                            users.add(u);
                        }
                    } catch (Throwable ignored) {
                    }
                }

                if (name != null && !name.isBlank()) {
                    try {
                        net.luckperms.api.model.user.User u = lp.getUserManager().getUser(name);
                        if (u != null && !users.contains(u)) {
                            users.add(u);
                        }
                    } catch (Throwable ignored) {
                    }
                }

                for (net.luckperms.api.model.user.User user : users) {
                    if (user == null) {
                        continue;
                    }

                    for (net.luckperms.api.node.Node node : user.getNodes()) {
                        if (node.hasExpired()) {
                            continue;
                        }
                        String nodeKey = PermissionUtils.normalizePermissionNode(node.getKey());
                        if (nodeKey.equals(normalizedPermission)) {
                            return node.getValue();
                        }
                        if (node instanceof net.luckperms.api.node.types.InheritanceNode in) {
                            String grp = PermissionUtils.normalizePermissionNode(in.getGroupName());
                            if (normalizedPermission.equals("group." + grp) || normalizedPermission.equals(grp)) {
                                return node.getValue();
                            }
                        }
                        if (nodeKey.startsWith("group.")) {
                            String grp = nodeKey.substring(6);
                            if (normalizedPermission.equals("group." + grp) || normalizedPermission.equals(grp)) {
                                return node.getValue();
                            }
                        }
                    }

                    String primary = user.getPrimaryGroup();
                    if (primary != null) {
                        String grp = PermissionUtils.normalizePermissionNode(primary);
                        if (normalizedPermission.equals("group." + grp) || normalizedPermission.equals(grp)) {
                            return true;
                        }
                    }

                    try {
                        Map<String, Boolean> permMap = user.getCachedData().getPermissionData().getPermissionMap();
                        if (permMap != null) {
                            for (Map.Entry<String, Boolean> entry : permMap.entrySet()) {
                                String key = PermissionUtils.normalizePermissionNode(entry.getKey());
                                if (key.equals(normalizedPermission)) {
                                    return entry.getValue();
                                }
                                if (key.startsWith("group.")) {
                                    String grp = key.substring(6);
                                    if (normalizedPermission.equals("group." + grp) || normalizedPermission.equals(grp)) {
                                        return entry.getValue();
                                    }
                                }
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                }

                return null;
            } catch (Throwable ignored) {
                return null;
            }
        }
    }

    private static final class VaultResolver {
        static Boolean resolveExact(Player player, String normalizedPermission) {
            try {
                org.bukkit.plugin.RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> rsp =
                        Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
                if (rsp == null || rsp.getProvider() == null) {
                    return null;
                }

                net.milkbowl.vault.permission.Permission vault = rsp.getProvider();
                String groupName = normalizedPermission.startsWith("group.")
                        ? normalizedPermission.substring(6)
                        : (isCommonGroupName(normalizedPermission) ? normalizedPermission : null);

                if (groupName != null && !groupName.isBlank()) {
                    if (vault.playerInGroup(player, groupName)) {
                        return true;
                    }

                    String name = player.getName();
                    if (name != null && (name.startsWith(".") || name.startsWith("*")) && name.length() > 1) {
                        if (vault.playerInGroup((String) null, name.substring(1), groupName)) {
                            return true;
                        }
                    }

                    try {
                        if (Bukkit.getPluginManager().isPluginEnabled("floodgate")
                                && org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                            org.geysermc.floodgate.api.player.FloodgatePlayer fp =
                                    org.geysermc.floodgate.api.FloodgateApi.getInstance().getPlayer(player.getUniqueId());
                            if (fp != null && fp.getUsername() != null && !fp.getUsername().isBlank()) {
                                if (vault.playerInGroup((String) null, fp.getUsername(), groupName)) {
                                    return true;
                                }
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                }

                return null;
            } catch (Throwable ignored) {
                return null;
            }
        }

        private static boolean isCommonGroupName(String name) {
            return name.equals("donutplus")
                    || name.equals("donut+")
                    || name.equals("donutplusplus")
                    || name.equals("donut++")
                    || name.equals("donutplusplusplus")
                    || name.equals("donut+++")
                    || name.equals("vip")
                    || name.equals("vip+")
                    || name.equals("vip++");
        }
    }
}
