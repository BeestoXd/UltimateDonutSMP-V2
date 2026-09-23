package com.bx.ultimateDonutSmp2.utils;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.models.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

public final class PlayerSettingUtils {

    public enum NotificationChannel {
        PUBLIC_CHAT,
        SERVER_BROADCAST,
        AUCTION,
        ORDER,
        TEAM_CHAT
    }

    public enum SoundChannel {
        NOTIFICATION,
        DUEL,
        GAMEPLAY
    }

    private PlayerSettingUtils() {
    }

    public static boolean hotbarMessagesEnabled(UltimateDonutSmp2 plugin, Player player) {
        PlayerData data = plugin.getPlayerDataManager().get(player);
        return data == null || data.isHotbarMessagesEnabled();
    }

    public static boolean notificationEnabled(
            UltimateDonutSmp2 plugin,
            Player player,
            NotificationChannel channel
    ) {
        PlayerData data = plugin.getPlayerDataManager().get(player);
        return notificationEnabled(data, channel);
    }

    public static boolean notificationEnabled(PlayerData data, NotificationChannel channel) {
        if (data == null || channel == null) {
            return true;
        }
        return switch (channel) {
            case PUBLIC_CHAT -> data.isPublicChatEnabled();
            case SERVER_BROADCAST -> data.isServerBroadcastsEnabled();
            case AUCTION -> data.isAuctionNotificationsEnabled();
            case ORDER -> data.isOrderNotificationsEnabled();
            case TEAM_CHAT -> data.isTeamChatVisible();
        };
    }

    public static boolean soundEnabled(UltimateDonutSmp2 plugin, Player player, SoundChannel channel) {
        PlayerData data = plugin.getPlayerDataManager().get(player);
        return soundEnabled(data, channel);
    }

    public static boolean soundEnabled(PlayerData data, SoundChannel channel) {
        if (data == null || channel == null || channel == SoundChannel.GAMEPLAY) {
            return true;
        }
        return switch (channel) {
            case NOTIFICATION -> data.isNotificationSoundsEnabled();
            case DUEL -> data.isDuelMusicEnabled();
            case GAMEPLAY -> true;
        };
    }

    public static boolean rtpCoordinatesEnabled(UltimateDonutSmp2 plugin, Player player) {
        PlayerData data = plugin.getPlayerDataManager().get(player);
        return data == null || data.isRtpCoordinatesEnabled();
    }

    public static boolean quietSpawnEnabled(UltimateDonutSmp2 plugin, Player player) {
        PlayerData data = plugin.getPlayerDataManager().get(player);
        return data != null && data.isQuietSpawnEnabled();
    }

    public static void sendActionBar(UltimateDonutSmp2 plugin, Player player, String text) {
        if (!hotbarMessagesEnabled(plugin, player)) return;
        if (text == null || text.isBlank()) return;
        try {
            java.lang.reflect.Method m = player.getClass().getMethod("sendActionBar", net.kyori.adventure.text.Component.class);
            m.invoke(player, net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                    .legacySection().deserialize(ColorUtils.colorize(text, player)));
            return;
        } catch (Throwable ignored) {
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, ColorUtils.toBaseComponents(text, player));
    }

    public static void sendActionBar(UltimateDonutSmp2 plugin, Player player, BaseComponent... component) {
        if (!hotbarMessagesEnabled(plugin, player)) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }

    public static void clearActionBar(Player player) {
        if (player == null) return;
        try {
            java.lang.reflect.Method m = player.getClass().getMethod("sendActionBar", net.kyori.adventure.text.Component.class);
            m.invoke(player, net.kyori.adventure.text.Component.empty());
            return;
        } catch (Throwable ignored) {
        }
        try {
            if (player.spigot() != null) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, ColorUtils.toBaseComponents(""));
            }
        } catch (Throwable ignored) {
        }
    }
}
