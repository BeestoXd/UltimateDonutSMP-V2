package com.bx.ultimateDonutSmp2.commands;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.dialogs.DialogSession;
import com.bx.ultimateDonutSmp2.utils.PermissionUtils;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import com.bx.ultimateDonutSmp2.models.FollowEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FriendsCommand implements CommandExecutor {

    private static final String PERMISSION = "ultimatedonutsmp2.friends";
    private static final String COMMAND_PERMISSION = "ultimatedonutsmp2.command.friends";
    private static final String FRIEND_COMMAND_PERMISSION = "ultimatedonutsmp2.command.friend";
    private static final String ADMIN_PERMISSION = "donutfriends.admin";

    private final UltimateDonutSmp2 plugin;

    public FriendsCommand(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission(ADMIN_PERMISSION)) {
                    plugin.getConfigManager().reload();
                    sender.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getMessage("FRIENDS.RELOAD_SUCCESS")));
                } else {
                    sender.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getMessage("FRIENDS.NO_PERMISSION")));
                }
                return true;
            }
            sender.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getMessage("FRIENDS.PLAYER_ONLY")));
            return true;
        }

        if (!PermissionUtils.has(player, PERMISSION)
                && !PermissionUtils.has(player, COMMAND_PERMISSION)
                && !PermissionUtils.has(player, FRIEND_COMMAND_PERMISSION)) {
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.NO_PERMISSION"), player));
            return true;
        }

        if (args.length == 0) {
            openFriendsUi(player, DialogSession.FriendFilter.ALL, null);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "reload" -> {
                if (PermissionUtils.has(player, ADMIN_PERMISSION)) {
                    plugin.getConfigManager().reload();
                    player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.RELOAD_SUCCESS"), player));
                } else {
                    player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.NO_PERMISSION"), player));
                }
            }
            case "list" -> openFriendsUi(player, DialogSession.FriendFilter.ALL, null);
            case "following" -> openFriendsUi(player, DialogSession.FriendFilter.FOLLOWING, null);
            case "followers" -> openFriendsUi(player, DialogSession.FriendFilter.FOLLOWERS, null);
            case "friends" -> openFriendsUi(player, DialogSession.FriendFilter.FRIENDS, null);
            case "add", "follow" -> {
                if (args.length < 2) {
                    player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.USAGE_FOLLOW"), player));
                    return true;
                }
                handleFollow(player, args[1]);
            }
            case "remove", "unfollow" -> {
                if (args.length < 2) {
                    player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.USAGE_REMOVE"), player));
                    return true;
                }
                handleUnfollow(player, args[1]);
            }
            case "search" -> {
                if (args.length < 2) {
                    player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.USAGE_SEARCH"), player));
                    return true;
                }
                openFriendsUi(player, DialogSession.FriendFilter.ALL, args[1]);
            }
            default -> {
                player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.UNKNOWN_SUBCOMMAND"), player));
            }
        }

        return true;
    }

    private void openFriendsUi(Player player, DialogSession.FriendFilter filter, String search) {
        if (plugin.getDialogManager() != null
                && plugin.getDialogManager().openFriends(player, filter, search)) {
            return;
        }
        player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessageOrDefault(
                "FRIENDS.UNAVAILABLE",
                "&cFriends needs a Paper server running 1.21.6 or newer."
        ), player));
    }

    private void handleFollow(Player player, String targetName) {
        ResolvedTarget target = resolveTarget(player, targetName);
        if (target == null) {
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.PLAYER_NOT_FOUND"), player));
            return;
        }

        if (player.getUniqueId().equals(target.uuid())) {
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.CANNOT_FOLLOW_SELF"), player));
            return;
        }

        if (plugin.getFriendsManager().isFollowing(player.getUniqueId(), target.uuid())) {
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.ALREADY_FOLLOWING", "{player}", target.name()), player));
            return;
        }

        boolean success = plugin.getFriendsManager().followPlayer(player, target.uuid(), target.name());
        if (success) {
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.FOLLOW_SUCCESS", "{player}", target.name()), player));
        } else {
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.FOLLOW_FAILURE"), player));
        }
    }

    private void handleUnfollow(Player player, String targetName) {
        ResolvedTarget target = resolveTarget(player, targetName);
        if (target == null) {
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.REMOVE_NOT_FOUND"), player));
            return;
        }

        if (!plugin.getFriendsManager().isFollowing(player.getUniqueId(), target.uuid())) {
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.NOT_FOLLOWING", "{player}", target.name()), player));
            return;
        }

        boolean success = plugin.getFriendsManager().unfollowPlayer(player, target.uuid());
        if (success) {
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.UNFOLLOW_SUCCESS", "{player}", target.name()), player));
        } else {
            player.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("FRIENDS.UNFOLLOW_FAILURE"), player));
        }
    }

    private ResolvedTarget resolveTarget(Player player, String input) {
        if (input == null || input.isBlank()) return null;

        Player online = plugin.getHideManager().findOnlinePlayer(player, input);
        if (online != null) {
            return new ResolvedTarget(online.getUniqueId(), plugin.getHideManager().plainPublicName(online));
        }

        for (FollowEntry entry : plugin.getFriendsManager().getFollowing(player.getUniqueId())) {
            if (entry.followedNameSnapshot().equalsIgnoreCase(input) || entry.followedUuid().toString().equalsIgnoreCase(input)) {
                return new ResolvedTarget(entry.followedUuid(), entry.followedNameSnapshot());
            }
        }

        UUID uuid = plugin.getHideManager().findKnownPlayerUuid(player, input);
        if (uuid == null) return null;

        String name = plugin.getDatabaseManager().getLastKnownUsername(uuid);
        String fallback = name == null || name.isBlank() ? input : name;
        return new ResolvedTarget(uuid, plugin.getHideManager().plainPublicName(uuid, fallback));
    }

    private record ResolvedTarget(UUID uuid, String name) {}
}
