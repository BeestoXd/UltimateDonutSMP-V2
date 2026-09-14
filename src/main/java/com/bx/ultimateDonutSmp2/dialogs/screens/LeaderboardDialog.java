package com.bx.ultimateDonutSmp2.dialogs.screens;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.dialogs.DialogActions;
import com.bx.ultimateDonutSmp2.dialogs.DialogConfig;
import com.bx.ultimateDonutSmp2.dialogs.DialogFactory;
import com.bx.ultimateDonutSmp2.dialogs.DialogScreen;
import com.bx.ultimateDonutSmp2.dialogs.DialogSession;
import com.bx.ultimateDonutSmp2.dialogs.DialogText;
import com.bx.ultimateDonutSmp2.managers.LeaderboardManager;
import io.papermc.paper.dialog.DialogResponseView;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Leaderboards: a category grid, a top-ten view, and an expanded view.
 *
 * <p>The two views share one renderer and differ only in how many rows they ask for and which
 * button row the config gives them, so a server owner can retitle either without touching code.
 */
public final class LeaderboardDialog extends DialogScreen {

    private static final String MENU = "LEADERBOARD_MENU_DIALOG";
    private static final String DETAIL = "LEADERBOARD_DETAIL_DIALOG";

    public LeaderboardDialog(UltimateDonutSmp2 plugin, DialogConfig config, DialogSession.Store sessions) {
        super(plugin, config, sessions);
    }

    public boolean open(Player player) {
        Map<String, String> tokens = DialogConfig.tokens("player", player.getName());
        return show(player, screen(player)
                .title(config.string(MENU + ".TITLE", "Leaderboards", tokens))
                .externalTitle(config.string(MENU + ".EXTERNAL-TITLE", null, tokens))
                .columns(config.columns(MENU, 2))
                .item(config.string(MENU + ".ITEM", "GOLD_INGOT"))
                .text(config.string(MENU + ".TEXT", null, tokens))
                .buttons(config.buttonsOf(MENU, tokens))
                .build());
    }

    private boolean openDetail(Player player, LeaderboardManager.LeaderboardType type, boolean full) {
        int limit = Math.max(1, config.integer(DETAIL + (full ? ".FULL-LIMIT" : ".SHORT-LIMIT"), full ? 100 : 10));
        String category = plugin.getLeaderboardManager().getDisplayName(type);
        Map<String, String> tokens = DialogConfig.tokens(
                "category", category,
                "category_lower", DialogActions.sanitise(type.getConfigKey()),
                "limit", String.valueOf(limit),
                "player", player.getName()
        );

        DialogFactory.Screen screen = screen(player)
                .title(config.string(DETAIL + ".TITLE", "Top %limit% - %category%", tokens))
                .externalTitle(config.string(DETAIL + ".EXTERNAL-TITLE", null, tokens))
                .columns(config.columns(DETAIL, 2));

        List<LeaderboardManager.LeaderboardEntry> entries =
                plugin.getLeaderboardManager().getEntries(type, 0, limit);
        if (entries.isEmpty()) {
            screen.text(config.string(DETAIL + ".NO-DATA", "&cNo data available."));
        } else {
            String format = config.string(DETAIL + ".ENTRY-FORMAT", "&e#%rank% &f%player% &8- &f%value%");
            for (LeaderboardManager.LeaderboardEntry entry : entries) {
                screen.text(renderEntry(player, format, entry, type));
            }
        }

        int width = config.buttonWidth(DETAIL, 150);
        screen.buttons(config.buttons(DETAIL + (full ? ".BUTTONS-100" : ".BUTTONS-10"), tokens, width));
        return show(player, screen.build());
    }

    /**
     * Builds one row. {@code %skin%} becomes the player's head, which only exists as an object
     * component, so the line is assembled around it instead of going through the string path.
     */
    private Component renderEntry(
            Player viewer,
            String format,
            LeaderboardManager.LeaderboardEntry entry,
            LeaderboardManager.LeaderboardType type
    ) {
        String name = entry.playerData().getUsername();
        if (name == null || name.isBlank()) {
            name = displayName(entry.playerData().getUuid(), "?");
        }
        String rankColor = switch (entry.position()) {
            case 1 -> "&#FCFC00";
            case 2 -> "&#FCFCFC";
            case 3 -> "&#FCA800";
            default -> "&#AAAAAA";
        };
        Map<String, String> tokens = DialogConfig.tokens(
                "rank", String.valueOf(entry.position()),
                "rank_color", rankColor,
                "player", name,
                "value", plugin.getLeaderboardManager().formatValue(type, entry.playerData())
        );
        String line = DialogConfig.apply(format, tokens);

        int skin = line.indexOf("%skin%");
        if (skin < 0) {
            return DialogText.of(line, viewer);
        }
        // Rank colour lives on the prefix. Appending the head there tints the face, which is why
        // #1 turned solid yellow while #2 (white) still looked like a real skin.
        Component head = DialogPlayerHeads.component(entry.playerData().getUuid(), name);
        return DialogPlayerHeads.insertBetween(
                DialogText.of(line.substring(0, skin), viewer),
                head,
                DialogText.of(line.substring(skin + "%skin%".length()), viewer));
    }

    @Override
    public boolean handle(Player player, String action, DialogResponseView response) {
        if (DialogActions.LEADERBOARDS_MENU.equals(action)) {
            open(player);
            return true;
        }

        String full = DialogActions.argument(action, DialogActions.LEADERBOARD_FULL);
        if (full != null) {
            openTyped(player, full, true);
            return true;
        }

        String category = DialogActions.argument(action, DialogActions.LEADERBOARD_CATEGORY);
        if (category != null) {
            openTyped(player, category, false);
            return true;
        }
        return false;
    }

    private void openTyped(Player player, String rawType, boolean full) {
        plugin.getLeaderboardManager()
                .parseType(rawType.toLowerCase(Locale.ROOT))
                .ifPresentOrElse(type -> openDetail(player, type, full), () -> open(player));
    }
}
