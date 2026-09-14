package com.bx.ultimateDonutSmp2.utils;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.CurrencyManager;
import com.bx.ultimateDonutSmp2.models.EconomyReason;
import com.bx.ultimateDonutSmp2.models.PlayerData;
import org.bukkit.entity.Player;

public final class PaymentUtils {

    private PaymentUtils() {
    }

    public static boolean transferMoney(UltimateDonutSmp2 plugin, Player sender, String targetName, double amount) {
        if (targetName.equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("BALANCE.PAY.CANT-PAY-SELF")));
            return false;
        }
        if (amount <= 0) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("BALANCE.PAY.MUST-BE-POSITIVE")));
            return false;
        }

        Player target = plugin.getHideManager().findOnlinePlayer(sender, targetName);
        if (target == null) {
            sender.sendMessage(ColorUtils.toComponent("&cPlayer not online."));
            return false;
        }

        if (target.getUniqueId().equals(sender.getUniqueId())) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("BALANCE.PAY.CANT-PAY-SELF")));
            return false;
        }
        PlayerData senderData = plugin.getPlayerDataManager().get(sender);
        PlayerData targetData = plugin.getPlayerDataManager().get(target);
        if (targetData == null) {
            sender.sendMessage(ColorUtils.toComponent("&cTarget profile not found."));
            return false;
        }
        if (targetData.getPaymentsChoice() == com.bx.ultimateDonutSmp2.models.ThreeChoice.OFF) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("BALANCE.PAY.TARGET-DISABLED-PAYMENTS")));
            return false;
        }
        if (targetData.getPaymentsChoice() == com.bx.ultimateDonutSmp2.models.ThreeChoice.FRIENDS_FOLLOWED) {
            boolean isFriendOrFollowed = plugin.getFriendsManager() != null && plugin.getFriendsManager().isFollowing(target.getUniqueId(), sender.getUniqueId());
            if (!isFriendOrFollowed) {
                sender.sendMessage(ColorUtils.toComponent("&c" + plugin.getHideManager().publicName(target) + " only accepts payments from friends/followed."));
                return false;
            }
        }
        if (plugin.getFriendsManager() != null && plugin.getFriendsManager().isPaymentBlocked(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ColorUtils.toComponent("&c" + plugin.getHideManager().publicName(target) + " has disabled payments from you."));
            return false;
        }
        if (senderData == null) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("BALANCE.PAY.NOT-ENOUGH-MONEY")));
            return false;
        }
        if (!plugin.getEconomyManager().has(sender, amount)) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("BALANCE.PAY.NOT-ENOUGH-MONEY")));
            return false;
        }

        var transferResult = plugin.getEconomyManager().transfer(sender, target, amount, EconomyReason.PLAYER_PAY);
        if (!transferResult.success()) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("BALANCE.PAY.TRANSACTION-ERROR")));
            return false;
        }

        sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage(
                "BALANCE.PAY.SUCCESS-SENDER",
                "{player}", plugin.getHideManager().publicName(target),
                "{amount}", compactMoneyAmount(plugin, amount),
                "{amount_full}", fullMoneyAmount(plugin, amount),
                "{money}", plugin.getCurrencyManager().formatMoneyCompact(amount),
                "{money_full}", fullMoney(plugin, amount))));
        SoundUtils.play(plugin, sender, plugin.getConfigManager().getSound("PAY.SUCCESS"), PlayerSettingUtils.SoundChannel.NOTIFICATION);
        if (targetData.isPayAlertsEnabled()) {
            target.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage(
                    "BALANCE.PAY.SUCCESS-RECEIVER",
                    "{player}", plugin.getHideManager().publicName(sender),
                    "{amount}", compactMoneyAmount(plugin, amount),
                    "{amount_full}", fullMoneyAmount(plugin, amount),
                    "{money}", plugin.getCurrencyManager().formatMoneyCompact(amount),
                    "{money_full}", fullMoney(plugin, amount))));
            SoundUtils.play(plugin, target, plugin.getConfigManager().getSound("PAY.SUCCESS"), PlayerSettingUtils.SoundChannel.NOTIFICATION);
        }
        return true;
    }

    public static boolean transferShards(UltimateDonutSmp2 plugin, Player sender, String targetName, long amount) {
        if (targetName.equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("SHARD_PAY.CANT-PAY-SELF")));
            return false;
        }
        if (amount <= 0) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("SHARD_PAY.MUST-BE-POSITIVE")));
            return false;
        }

        Player target = plugin.getHideManager().findOnlinePlayer(sender, targetName);
        if (target == null) {
            sender.sendMessage(ColorUtils.toComponent("&cPlayer not online."));
            return false;
        }

        if (target.getUniqueId().equals(sender.getUniqueId())) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("SHARD_PAY.CANT-PAY-SELF")));
            return false;
        }
        PlayerData senderData = plugin.getPlayerDataManager().get(sender);
        PlayerData targetData = plugin.getPlayerDataManager().get(target);
        if (targetData == null) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("SHARD_PAY.TARGET-PROFILE-NOT-FOUND")));
            return false;
        }
        if (targetData.getPaymentsChoice() == com.bx.ultimateDonutSmp2.models.ThreeChoice.OFF) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("SHARD_PAY.TARGET-DISABLED-PAYMENTS")));
            return false;
        }
        if (targetData.getPaymentsChoice() == com.bx.ultimateDonutSmp2.models.ThreeChoice.FRIENDS_FOLLOWED) {
            boolean isFriendOrFollowed = plugin.getFriendsManager() != null && plugin.getFriendsManager().isFollowing(target.getUniqueId(), sender.getUniqueId());
            if (!isFriendOrFollowed) {
                sender.sendMessage(ColorUtils.toComponent("&c" + plugin.getHideManager().publicName(target) + " only accepts payments from friends/followed."));
                return false;
            }
        }
        if (plugin.getFriendsManager() != null && plugin.getFriendsManager().isPaymentBlocked(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ColorUtils.toComponent("&c" + plugin.getHideManager().publicName(target) + " has disabled payments from you."));
            return false;
        }
        if (senderData == null || !senderData.hasShards(amount)) {
            sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage("SHARD_PAY.NOT-ENOUGH-SHARDS")));
            return false;
        }

        senderData.removeShards(amount);
        targetData.addShards(amount);

        sender.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage(
                "SHARD_PAY.SUCCESS-SENDER",
                "{player}", plugin.getHideManager().publicName(target),
                "{amount}", String.valueOf(amount),
                "{shards}", plugin.getCurrencyManager().formatShards(amount))));
        SoundUtils.play(plugin, sender, plugin.getConfigManager().getSound("PAY.SUCCESS"), PlayerSettingUtils.SoundChannel.NOTIFICATION);
        if (targetData.isPayAlertsEnabled()) {
            target.sendMessage(ColorUtils.toComponent(plugin.getConfigManager().getMessage(
                    "SHARD_PAY.SUCCESS-RECEIVER",
                    "{player}", plugin.getHideManager().publicName(sender),
                    "{amount}", String.valueOf(amount),
                    "{shards}", plugin.getCurrencyManager().formatShards(amount))));
            SoundUtils.play(plugin, target, plugin.getConfigManager().getSound("PAY.SUCCESS"), PlayerSettingUtils.SoundChannel.NOTIFICATION);
        }
        return true;
    }

    private static String compactMoneyAmount(UltimateDonutSmp2 plugin, double amount) {
        return plugin.getCurrencyManager().formatCompactAmount(CurrencyManager.CurrencyType.MONEY, amount);
    }

    private static String fullMoneyAmount(UltimateDonutSmp2 plugin, double amount) {
        return plugin.getCurrencyManager().formatAmount(CurrencyManager.CurrencyType.MONEY, amount);
    }

    private static String fullMoney(UltimateDonutSmp2 plugin, double amount) {
        return plugin.getCurrencyManager().format(CurrencyManager.CurrencyType.MONEY, amount, false);
    }
}
