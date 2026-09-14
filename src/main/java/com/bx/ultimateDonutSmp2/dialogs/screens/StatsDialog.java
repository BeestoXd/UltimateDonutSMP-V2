package com.bx.ultimateDonutSmp2.dialogs.screens;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.dialogs.DialogActions;
import com.bx.ultimateDonutSmp2.dialogs.DialogConfig;
import com.bx.ultimateDonutSmp2.dialogs.DialogFactory;
import com.bx.ultimateDonutSmp2.dialogs.DialogScreen;
import com.bx.ultimateDonutSmp2.dialogs.DialogSession;
import com.bx.ultimateDonutSmp2.dialogs.DialogSupport;
import com.bx.ultimateDonutSmp2.dialogs.DialogText;
import com.bx.ultimateDonutSmp2.managers.CurrencyManager;
import com.bx.ultimateDonutSmp2.managers.FeatureManager;
import com.bx.ultimateDonutSmp2.managers.ProfileViewerManager;
import com.bx.ultimateDonutSmp2.menus.ProfileViewerMenu;
import com.bx.ultimateDonutSmp2.menus.StatsMenu;
import com.bx.ultimateDonutSmp2.models.FollowEntry;
import com.bx.ultimateDonutSmp2.models.PlayerData;
import com.bx.ultimateDonutSmp2.utils.NumberUtils;
import io.papermc.paper.dialog.DialogResponseView;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Player stats: pick someone from a Donut-style shortlist, then the four-line sheet.
 *
 * <p>The list is rebuilt every open so recommendations and incoming follow requests stay
 * current. Players typed into Add stay pinned on the session so they do not vanish on the
 * next open. A player who is not online has to be read out of the database, so that read is
 * pushed off the main thread and the sheet opens when it comes back.
 */
public final class StatsDialog extends DialogScreen {

    private static final String LIST = "STATS_SEARCH_DIALOG";
    private static final String ADD = "ADD_STATS_PLAYER_DIALOG";
    private static final String SHEET = "PLAYER_STATS_DIALOG";

    private enum Kind {
        YOU, PINNED, REQUEST, FOLLOWING, RECOMMENDED
    }

    private record Entry(UUID uuid, String name, Kind kind, long requestAt) {
    }

    public StatsDialog(UltimateDonutSmp2 plugin, DialogConfig config, DialogSession.Store sessions) {
        super(plugin, config, sessions);
    }

    public boolean open(Player player) {
        session(player).setOverlayReturnAction(null);
        Map<String, String> tokens = DialogConfig.tokens("player", player.getName());
        int width = config.buttonWidth(LIST, 100);
        DialogFactory.Screen screen = screen(player)
                .title(config.string(LIST + ".TITLE", "Player Stats", tokens))
                .externalTitle(config.string(LIST + ".EXTERNAL-TITLE", null, tokens))
                .afterAction(config.string(LIST + ".AFTER-ACTION", "NONE"))
                .columns(config.columns(LIST, 2))
                .text(config.string(LIST + ".DESCRIPTION", "Click a player to view their stats", tokens));

        List<Entry> entries = buildList(player);
        if (entries.isEmpty()) {
            screen.text(config.string(LIST + ".EMPTY-TEXT", "&7Nobody on your list yet."));
        }
        for (Entry entry : entries) {
            Component head = DialogPlayerHeads.component(entry.uuid(), entry.name());
            Component text = DialogText.of(entry.name(), player);
            Component label = head == null || head.equals(Component.empty())
                    ? text
                    : head.append(Component.space()).append(text);
            screen.button(label, tooltipOf(player, entry), width, DialogActions.STATS_VIEW + entry.uuid());
        }
        int addWidth = config.integer(LIST + ".ADD-BUTTON-WIDTH", 200);
        screen.button(
                config.string(LIST + ".ADD-BUTTON-LABEL", "+ Add Player to List"),
                null,
                addWidth,
                DialogActions.STATS_ADD_PROMPT
        );
        String backLabel = config.string(LIST + ".BACK-LABEL", "");
        if (backLabel != null && !backLabel.isBlank()) {
            screen.exit(
                    backLabel,
                    config.string(LIST + ".BACK-ACTION", DialogActions.NAMESPACE + ':' + DialogActions.MAIN_MENU),
                    addWidth
            );
        }
        return show(player, screen.build());
    }

    /** Opens straight onto one player's sheet, used by {@code /stats <name>}. */
    public boolean openFor(Player player, String targetName) {
        session(player).setOverlayReturnAction(null);
        if (targetName == null || targetName.isBlank()) {
            return open(player);
        }
        OfflinePlayer target = resolvePlayer(targetName);
        if (target == null || target.getUniqueId() == null) {
            return false;
        }
        session(player).rememberStats(target.getUniqueId(),
                target.getName() == null ? targetName : target.getName());
        openSheet(player, target.getUniqueId());
        return true;
    }

    private boolean openAdd(Player player) {
        Map<String, String> tokens = DialogConfig.tokens("player", player.getName());
        return show(player, screen(player)
                .title(config.string(ADD + ".TITLE", "Add a Player", tokens))
                .externalTitle(config.string(ADD + ".EXTERNAL-TITLE", null, tokens))
                .afterAction(config.string(ADD + ".AFTER-ACTION", "NONE"))
                .columns(config.columns(ADD, 1))
                .text(config.string(ADD + ".DESCRIPTION", "Type a name to add to your stats list", tokens))
                .inputs(config.inputs(ADD, tokens))
                .buttons(config.buttonsOf(ADD, tokens))
                .build());
    }

    private void openSheet(Player player, UUID target) {
        loadData(player, target, data -> {
            if (data == null) {
                message(player, plugin.getConfigManager()
                        .getMessageOrDefault("STATS.PLAYER-NOT-FOUND", "&cThat player has no stats yet."));
                open(player);
                return;
            }
            String name = data.getUsername() == null ? displayName(target, target.toString()) : data.getUsername();
            session(player).rememberStats(target, name);
            session(player).setStatsTarget(target.toString());
            Map<String, String> tokens = statTokens(name, data, target);

            DialogFactory.Screen screen = screen(player)
                    .title(config.string(SHEET + ".TITLE", "%player%", tokens))
                    .externalTitle(config.string(SHEET + ".EXTERNAL-TITLE", null, tokens))
                    .afterAction(config.string(SHEET + ".AFTER-ACTION", "NONE"))
                    .columns(config.columns(SHEET, 1));
            Component headComponent = DialogPlayerHeads.component(target, name);
            if (headComponent != null && !headComponent.equals(Component.empty())) {
                screen.text(headComponent);
            } else {
                ItemStack head = DialogHeads.item(plugin, target, name);
                if (head != null) {
                    screen.item(head);
                } else {
                    screen.item(online(player, target)
                            ? config.string(SHEET + ".ONLINE-ITEM", "LIME_CONCRETE")
                            : config.string(SHEET + ".OFFLINE-ITEM", "GRAY_CONCRETE"));
                }
            }
            for (String line : config.stringList(SHEET + ".STATS-FORMAT")) {
                screen.text(DialogConfig.apply(line, tokens));
            }
            screen.buttons(withOverlayBack(player, config.buttonsOf(SHEET, tokens), DialogActions.STATS_MENU));
            show(player, screen.build());
        });
    }

    private Map<String, String> statTokens(String name, PlayerData data, UUID target) {
        CurrencyManager currency = plugin.getCurrencyManager();
        return DialogConfig.tokens(
                "player", name,
                "player_lower", lower(name),
                "player_uuid", target == null ? "" : target.toString(),
                "money", currency.formatCompactAmount(CurrencyManager.CurrencyType.MONEY, data.getMoney()),
                "shards", currency.formatCompactAmount(CurrencyManager.CurrencyType.SHARDS, data.getShards()),
                "kills", String.valueOf(data.getKills()),
                "deaths", String.valueOf(data.getDeaths()),
                "playtime", NumberUtils.formatTimeScoreboard(data.getTotalPlaytimeSeconds()),
                "blocks_placed", NumberUtils.formatNice(data.getBlocksPlaced()),
                "blocks_broken", NumberUtils.formatNice(data.getBlocksBroken()),
                "mobs_killed", NumberUtils.formatNice(data.getMobsKilled()),
                "sell_earned", currency.formatCompactAmount(CurrencyManager.CurrencyType.MONEY, data.getMoneyMade()),
                "kill_streak", String.valueOf(data.getKillStreak()),
                "highest_kill_streak", String.valueOf(data.getHighestKillStreak())
        );
    }

    @Override
    public boolean handle(Player player, String action, DialogResponseView response) {
        if (DialogActions.STATS_MENU.equals(action)) {
            open(player);
            return true;
        }
        if (DialogActions.STATS_ADD_PROMPT.equals(action)) {
            openAdd(player);
            return true;
        }
        if (DialogActions.STATS_ADD_EXECUTE.equals(action)) {
            addFromInput(player, response);
            return true;
        }

        String full = DialogActions.argument(action, DialogActions.STATS_VIEW_FULL);
        if (full != null) {
            click(player);
            UUID target = resolveFullProfileTarget(player, full);
            if (target == null) {
                open(player);
                return true;
            }
            openFullProfile(player, target);
            return true;
        }

        String view = DialogActions.argument(action, DialogActions.STATS_VIEW);
        if (view != null) {
            click(player);
            UUID target = parseUuid(view);
            if (target != null) {
                openSheet(player, target);
            } else {
                open(player);
            }
            return true;
        }
        return false;
    }

    /**
     * Staff and operators get the profile-viewer chest. Everyone else gets the public
     * {@code STATS-MENU} sheet from {@code Design/Dialog API/Stats/View Full Profile}.
     */
    private void openFullProfile(Player player, UUID target) {
        String name = displayName(target, target.toString());
        session(player).setStatsTarget(target.toString());
        boolean staff = canOpenStaffFullProfile(player);
        DialogSupport.close(player);
        if (plugin.getMenuNavigationTracker() != null) {
            plugin.getMenuNavigationTracker().clear(player);
        }
        plugin.getSpigotScheduler().runEntityLater(player, () -> {
            if (!player.isOnline()) {
                return;
            }
            if (staff && openStaffProfile(player, target)) {
                return;
            }
            new StatsMenu(plugin, target, name).open(player);
        }, 1L);
    }

    private boolean canOpenStaffFullProfile(Player player) {
        if (!config.bool(SHEET + ".OPEN-STAFF-PROFILE", false)) {
            return false;
        }
        ProfileViewerManager viewer = plugin.getProfileViewerManager();
        boolean hasStaffPermission = viewer != null && viewer.canView(player);
        return shouldOpenStaffFullProfile(player != null && player.isOp(), hasStaffPermission)
                && plugin.getFeatureManager() != null
                && plugin.getFeatureManager().isEnabled(FeatureManager.Feature.PROFILE_VIEWER);
    }

    /** Operators and anyone with the staff profile-viewer node get the staff chest. */
    static boolean shouldOpenStaffFullProfile(boolean operator, boolean hasStaffProfilePermission) {
        return operator || hasStaffProfilePermission;
    }

    private boolean openStaffProfile(Player player, UUID target) {
        ProfileViewerManager viewer = plugin.getProfileViewerManager();
        if (viewer == null || viewer.resolveProfile(target).isEmpty()) {
            return false;
        }
        new ProfileViewerMenu(plugin, target).open(player);
        return true;
    }

    /** Accepts a uuid from the button, a typed name from older configs, or the last opened sheet. */
    private UUID resolveFullProfileTarget(Player player, String argument) {
        UUID uuid = parseUuid(argument);
        if (uuid != null) {
            return uuid;
        }
        OfflinePlayer resolved = resolvePlayer(argument);
        if (resolved != null && resolved.getUniqueId() != null) {
            return resolved.getUniqueId();
        }
        return parseUuid(session(player).getStatsTarget());
    }

    private void addFromInput(Player player, DialogResponseView response) {
        String name = input(response, "player_name");
        if (name == null) {
            openAdd(player);
            return;
        }
        OfflinePlayer resolved = resolvePlayer(name);
        if (resolved == null || resolved.getUniqueId() == null) {
            message(player, plugin.getConfigManager()
                    .getMessageOrDefault("STATS.PLAYER-NOT-FOUND", "&cThat player has no stats yet."));
            openAdd(player);
            return;
        }
        session(player).rememberStats(resolved.getUniqueId(),
                resolved.getName() == null ? name : resolved.getName());
        open(player);
    }

    /** Reads cached data straight away, and anything else off the main thread. */
    private void loadData(Player viewer, UUID uuid, Consumer<PlayerData> callback) {
        PlayerData cached = plugin.getPlayerDataManager().get(uuid);
        if (cached != null) {
            callback.accept(cached);
            return;
        }
        plugin.getSpigotScheduler().runAsync(() -> {
            PlayerData loaded = plugin.getDatabaseManager().loadPlayer(uuid);
            plugin.getSpigotScheduler().runEntity(viewer, () -> {
                if (viewer.isOnline()) {
                    callback.accept(loaded);
                }
            });
        });
    }

    /**
     * Self, then anyone the player pinned, then incoming follow requests, then people they
     * follow, then other online players as recommendations — cut to {@code LIST-SIZE}.
     */
    private List<Entry> buildList(Player player) {
        int limit = Math.max(1, config.integer(LIST + ".LIST-SIZE", 5));
        List<Entry> out = new ArrayList<>();
        Set<UUID> seen = new LinkedHashSet<>();
        UUID self = player.getUniqueId();

        add(out, seen, new Entry(self, publicName(player, self, player.getName()), Kind.YOU, 0L), limit);

        for (DialogSession.Shortcut pinned : session(player).getStatsList()) {
            if (pinned.uuid().equals(self)) {
                continue;
            }
            add(out, seen, new Entry(pinned.uuid(), publicName(player, pinned.uuid(), pinned.name()),
                    Kind.PINNED, 0L), limit);
        }

        if (plugin.getFriendsManager() != null) {
            for (FollowEntry follower : plugin.getFriendsManager().getFollowers(self)) {
                if (plugin.getFriendsManager().isFollowing(self, follower.followerUuid())) {
                    continue;
                }
                add(out, seen, new Entry(
                        follower.followerUuid(),
                        publicName(player, follower.followerUuid(),
                                displayName(follower.followerUuid(), follower.followerUuid().toString())),
                        Kind.REQUEST,
                        follower.createdAt()
                ), limit);
            }
            for (FollowEntry following : plugin.getFriendsManager().getFollowing(self)) {
                add(out, seen, new Entry(
                        following.followedUuid(),
                        publicName(player, following.followedUuid(), following.followedNameSnapshot()),
                        Kind.FOLLOWING,
                        0L
                ), limit);
            }
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId().equals(self) || hiddenFrom(player, online)) {
                continue;
            }
            add(out, seen, new Entry(online.getUniqueId(), publicName(player, online.getUniqueId(),
                    online.getName()), Kind.RECOMMENDED, 0L), limit);
        }
        return out;
    }

    private static void add(List<Entry> out, Set<UUID> seen, Entry entry, int limit) {
        if (out.size() >= limit || entry.uuid() == null || !seen.add(entry.uuid())) {
            return;
        }
        out.add(entry);
    }

    private Component tooltipOf(Player viewer, Entry entry) {
        String raw = switch (entry.kind()) {
            case YOU -> config.string(LIST + ".YOU-TOOLTIP", "You");
            case RECOMMENDED -> config.string(LIST + ".RECOMMENDED-TOOLTIP", "Recommended");
            case REQUEST -> DialogConfig.apply(
                    config.string(LIST + ".REQUEST-TOOLTIP", "Sent you a request %time% ago"),
                    DialogConfig.tokens("time", compactAgo(System.currentTimeMillis() - entry.requestAt()))
            );
            case PINNED, FOLLOWING -> null;
        };
        return raw == null || raw.isBlank() ? null : DialogText.of(raw, viewer);
    }

    private String publicName(Player viewer, UUID uuid, String fallback) {
        if (plugin.getHideManager() == null) {
            return fallback == null ? uuid.toString() : fallback;
        }
        Player online = Bukkit.getPlayer(uuid);
        if (online != null) {
            return plugin.getHideManager().visibleName(viewer, online);
        }
        return plugin.getHideManager().publicName(uuid, fallback);
    }

    private boolean hiddenFrom(Player viewer, Player target) {
        if (plugin.getHideManager() == null) {
            return false;
        }
        return plugin.getHideManager().isHidden(target.getUniqueId())
                && !plugin.getHideManager().canSeeRealIdentity(viewer);
    }

    private boolean online(Player viewer, UUID target) {
        Player online = Bukkit.getPlayer(target);
        return online != null && online.isOnline() && !hiddenFrom(viewer, online);
    }

    static String compactAgo(long millisAgo) {
        long seconds = Math.max(0L, millisAgo / 1000L);
        if (seconds < 60L) {
            return seconds + "s";
        }
        long minutes = seconds / 60L;
        if (minutes < 60L) {
            return minutes + "m";
        }
        long hours = minutes / 60L;
        if (hours < 24L) {
            return hours + "h";
        }
        return (hours / 24L) + "d";
    }
}
