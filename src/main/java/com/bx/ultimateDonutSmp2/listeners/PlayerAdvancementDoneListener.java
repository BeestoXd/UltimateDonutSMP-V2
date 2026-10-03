package com.bx.ultimateDonutSmp2.listeners;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.models.PlayerData;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class PlayerAdvancementDoneListener implements Listener {

    private static final MethodHandle ADVANCEMENT_SET;

    static {
        MethodHandle advSet = null;
        try {
            Method m = PlayerAdvancementDoneEvent.class.getMethod("message");
            Class<?> componentClass = m.getReturnType();
            if (componentClass != null && !"java.lang.String".equals(componentClass.getName())) {
                advSet = MethodHandles.lookup().unreflect(
                        PlayerAdvancementDoneEvent.class.getMethod("message", componentClass));
            }
        } catch (Throwable ignored) {
        }
        ADVANCEMENT_SET = advSet;
    }

    private final UltimateDonutSmp2 plugin;

    public PlayerAdvancementDoneListener(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
        disableVanillaAnnouncements();
    }

    private void disableVanillaAnnouncements() {
        for (World world : Bukkit.getWorlds()) {
            setAnnounceAdvancements(world, false);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        setAnnounceAdvancements(event.getWorld(), false);
    }

    @SuppressWarnings("unchecked")
    private void setAnnounceAdvancements(World world, boolean value) {
        if (world == null) {
            return;
        }
        try {
            GameRule<Boolean> rule = (GameRule<Boolean>) GameRule.getByName("announceAdvancements");
            if (rule != null) {
                world.setGameRule(rule, value);
            }
        } catch (Throwable ignored) {
        }
    }

    public static void clearAdvancementMessage(PlayerAdvancementDoneEvent event) {
        if (ADVANCEMENT_SET != null && event != null) {
            try {
                ADVANCEMENT_SET.invokeWithArguments(event, (Object) null);
            } catch (Throwable ignored) {
            }
        }
    }

    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        clearAdvancementMessage(event);

        Advancement adv = event.getAdvancement();
        if (adv.getDisplay() == null) {
            return;
        }

        Player player = event.getPlayer();
        String title = adv.getDisplay().getTitle();
        if (title == null || title.isEmpty()) {
            return;
        }

        String titleColor = "&a";
        String frameText = "made the advancement";
        if (adv.getDisplay().getType() != null) {
            switch (adv.getDisplay().getType()) {
                case GOAL -> {
                    titleColor = "&6";
                    frameText = "reached the goal";
                }
                case CHALLENGE -> {
                    titleColor = "&5";
                    frameText = "completed the challenge";
                }
            }
        }

        String displayName = plugin.getHideManager() != null 
                ? plugin.getHideManager().publicName(player) 
                : player.getDisplayName();
        
        String announcement = displayName + " &7has " + frameText + " " + titleColor + "[" + title + "]";
        final String finalAnnouncement = ColorUtils.colorize(announcement);

        plugin.getSpigotScheduler().forEachOnlinePlayer(p -> {
            if (shouldReceiveAdvancement(p)) {
                p.sendMessage(ColorUtils.toComponent(finalAnnouncement));
            }
        });
    }

    private boolean shouldReceiveAdvancement(Player receiver) {
        PlayerData receiverData = plugin.getPlayerDataManager().get(receiver);
        if (receiverData == null) {
            return true;
        }
        return receiverData.isAdvancementMessagesEnabled();
    }
}
