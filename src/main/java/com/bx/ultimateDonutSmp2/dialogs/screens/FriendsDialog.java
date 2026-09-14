package com.bx.ultimateDonutSmp2.dialogs.screens;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.dialogs.DialogActions;
import com.bx.ultimateDonutSmp2.dialogs.DialogConfig;
import com.bx.ultimateDonutSmp2.dialogs.DialogFactory;
import com.bx.ultimateDonutSmp2.dialogs.DialogScreen;
import com.bx.ultimateDonutSmp2.dialogs.DialogSession;
import com.bx.ultimateDonutSmp2.dialogs.DialogText;
import com.bx.ultimateDonutSmp2.models.FollowEntry;
import io.papermc.paper.dialog.DialogResponseView;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Friends, matching {@code Design/Dialog API/Friends}: a three-column list, then the
 * per-player edit stack, settings toggles, and the Search / Follow name prompts.
 *
 * <p>"Friend" is a mutual follow. Filter, Search and {@code + Follow} sit in the same grid as
 * the names so the empty state is {@code Filter | Search | No friends or following} with
 * {@code + Follow} on the next row, exactly as the reference screenshots.
 *
 * <p>{@code + Follow} opens a name search, then a picker of matches. Follow only happens after
 * the player clicks a result.
 */
public final class FriendsDialog extends DialogScreen {

    private static final String MENU = "FRIENDS_MENU_DIALOG";
    private static final String SEARCH = "FRIENDS_SEARCH_DIALOG";
    private static final String FOLLOW_PROMPT = "FOLLOW_PROMPT_DIALOG";
    private static final String FOLLOW_RESULTS = "FOLLOW_SEARCH_RESULTS_DIALOG";
    private static final String DETAILS = "FRIEND_DETAILS_DIALOG";
    private static final String SETTINGS = "FRIEND_SETTINGS_DIALOG";

    private record FriendRow(UUID uuid, String name, boolean following, boolean follower) {
        boolean isFriend() {
            return following && follower;
        }
    }

    public FriendsDialog(UltimateDonutSmp2 plugin, DialogConfig config, DialogSession.Store sessions) {
        super(plugin, config, sessions);
    }

    public boolean open(Player player) {
        return showList(player);
    }

    public boolean open(Player player, DialogSession.FriendFilter filter, String search) {
        DialogSession session = session(player);
        session.setFriendFilter(filter);
        session.setFriendSearch(search);
        return showList(player);
    }

    private boolean showList(Player player) {
        DialogSession session = session(player);
        session.setOverlayReturnAction(null);
        UUID viewer = player.getUniqueId();
        List<FriendRow> entries = collect(player, session.getFriendFilter(), session.getFriendSearch());

        Map<String, String> tokens = DialogConfig.tokens(
                "player", player.getName(),
                "friends", String.valueOf(countFriends(viewer)),
                "following", String.valueOf(plugin.getFriendsManager().getFollowing(viewer).size()),
                "followers", String.valueOf(plugin.getFriendsManager().getFollowers(viewer).size())
        );
        String title = applyBraces(config.string(MENU + ".TITLE",
                "Friends {friends} friends / {following} following"), tokens);
        int cellWidth = config.integer(MENU + ".FRIEND-WIDTH", config.buttonWidth(MENU, 100));

        DialogFactory.Screen screen = screen(player)
                .title(title)
                .externalTitle(applyBraces(config.string(MENU + ".EXTERNAL-TITLE", title), tokens))
                .afterAction(config.string(MENU + ".AFTER-ACTION", "NONE"))
                .columns(config.columns(MENU, 3));

        screen.button(
                config.string(MENU + ".FILTER-LABEL", "Filter"),
                filterTooltip(session),
                config.integer(MENU + ".FILTER-WIDTH", cellWidth),
                DialogActions.FRIENDS_FILTER
        );
        screen.button(
                config.string(MENU + ".SEARCH-LABEL", "Search"),
                searchTooltip(session),
                config.integer(MENU + ".SEARCH-WIDTH", cellWidth),
                DialogActions.FRIENDS_SEARCH_PROMPT
        );

        if (entries.isEmpty()) {
            screen.inertButton(emptyLabel(session), null, cellWidth);
        } else {
            for (FriendRow entry : entries) {
                screen.button(
                        playerLine(player, entry.uuid(), entry.name()),
                        tooltipComponent(player, relationshipTooltip(entry)),
                        cellWidth,
                        DialogActions.FRIENDS_VIEW + entry.uuid()
                );
            }
        }

        screen.button(
                config.string(MENU + ".FOLLOW-LABEL", "&#00FC00+ Follow"),
                config.string(MENU + ".FOLLOW-TOOLTIP", "&7Search for a player to follow"),
                config.integer(MENU + ".FOLLOW-WIDTH", 200),
                DialogActions.FRIENDS_FOLLOW_PROMPT
        );

        String backLabel = config.string(MENU + ".BACK-LABEL", "");
        if (backLabel != null && !backLabel.isBlank()) {
            screen.exit(
                    backLabel,
                    config.string(MENU + ".BACK-ACTION", DialogActions.NAMESPACE + ':' + DialogActions.MAIN_MENU),
                    config.integer(MENU + ".FOLLOW-WIDTH", 200)
            );
        }
        return show(player, screen.build());
    }

    private boolean openPrompt(Player player, String path, String defaultTitle) {
        Map<String, String> tokens = DialogConfig.tokens("player", player.getName());
        return show(player, screen(player)
                .title(config.string(path + ".TITLE", defaultTitle, tokens))
                .externalTitle(config.string(path + ".EXTERNAL-TITLE", null, tokens))
                .afterAction(config.string(path + ".AFTER-ACTION", "NONE"))
                .columns(config.columns(path, 1))
                .text(config.string(path + ".DESCRIPTION", null, tokens))
                .inputs(config.inputs(path, tokens))
                .buttons(config.buttonsOf(path, tokens))
                .build());
    }

    private boolean openDetails(Player player, UUID target) {
        session(player).setOverlayReturnAction(DialogActions.FRIENDS_VIEW + target);
        String name = publicName(player, target, target.toString());
        boolean following = plugin.getFriendsManager().isFollowing(player.getUniqueId(), target);
        boolean follower = plugin.getFriendsManager().isFollower(player.getUniqueId(), target);
        Map<String, String> tokens = DialogConfig.tokens(
                "name", name,
                "target", name,
                "target_uuid", target.toString()
        );
        String titleKey;
        String defaultTitle;
        if (following && follower) {
            titleKey = "TITLE-FRIENDS";
            defaultTitle = "%name% Friends";
        } else if (following) {
            titleKey = "TITLE-FOLLOWING";
            defaultTitle = "%name% Following";
        } else {
            titleKey = "TITLE-FOLLOWERS";
            defaultTitle = "%name% Followers";
        }
        String title = config.string(DETAILS + "." + titleKey, defaultTitle, tokens);
        int width = config.buttonWidth(DETAILS, 300);

        DialogFactory.Screen screen = screen(player)
                .title(title)
                .externalTitle(title)
                .afterAction(config.string(DETAILS + ".AFTER-ACTION", "NONE"))
                .columns(config.columns(DETAILS, 1))
                .text(playerLine(player, target, name));

        screen.button(
                config.string(DETAILS + ".VIEW-STATS-LABEL", "View Stats"),
                config.string(DETAILS + ".VIEW-STATS-TOOLTIP", "See %name%'s profile", tokens),
                width,
                DialogActions.STATS_VIEW + target
        );
        screen.button(
                config.string(DETAILS + ".PAY-LABEL", "Pay"),
                config.string(DETAILS + ".PAY-TOOLTIP", "Send money to %name%", tokens),
                width,
                DialogActions.PAY_TARGET + target
        );
        screen.button(
                config.string(DETAILS + ".TELEPORT-LABEL", "Teleport"),
                config.string(DETAILS + ".TELEPORT-TOOLTIP", "Teleport to or bring %name%", tokens),
                width,
                DialogActions.TPA_TARGET + target
        );

        if (following) {
            screen.button(
                    config.string(DETAILS + ".SETTINGS-LABEL", "Settings"),
                    config.string(DETAILS + ".SETTINGS-TOOLTIP", "Change settings for %name%", tokens),
                    width,
                    DialogActions.FRIENDS_SETTINGS + target
            );
            screen.button(
                    config.string(DETAILS + ".UNFOLLOW-LABEL", "&cUnfollow"),
                    null,
                    width,
                    DialogActions.FRIENDS_UNFOLLOW + target
            );
        } else {
            screen.button(
                    config.string(DETAILS + ".FOLLOW-LABEL", "&#00FC00Follow"),
                    null,
                    width,
                    DialogActions.FRIENDS_FOLLOW_ADD + target
            );
        }
        String backLabel = config.string(DETAILS + ".BACK-LABEL", "Back");
        if (backLabel != null && !backLabel.isBlank()) {
            screen.button(
                    backLabel,
                    null,
                    config.integer(DETAILS + ".BACK-WIDTH", 150),
                    DialogActions.NAMESPACE + ':' + DialogActions.FRIENDS
            );
        }
        return show(player, screen.build());
    }

    private boolean openSettings(Player player, UUID target) {
        FollowEntry entry = plugin.getFriendsManager().getFollowEntry(player.getUniqueId(), target);
        if (entry == null) {
            return openDetails(player, target);
        }
        String name = publicName(player, target, target.toString());
        int width = config.buttonWidth(SETTINGS, 300);
        String on = config.string(SETTINGS + ".STATE-TRUE", "&aON");
        String off = config.string(SETTINGS + ".STATE-FALSE", "&cOFF");

        DialogFactory.Screen screen = screen(player)
                .title(config.string(SETTINGS + ".TITLE", "%name% — Settings",
                        DialogConfig.tokens("name", name)))
                .externalTitle(config.string(SETTINGS + ".EXTERNAL-TITLE", null,
                        DialogConfig.tokens("name", name)))
                .afterAction(config.string(SETTINGS + ".AFTER-ACTION", "NONE"))
                .columns(config.columns(SETTINGS, 1))
                .text(playerLine(player, target, name));

        for (FriendSetting setting : FriendSetting.DISPLAY_ORDER) {
            String label = config.string(SETTINGS + '.' + setting.labelKey(), setting.name(),
                    DialogConfig.tokens("state", setting.read(entry) ? on : off, "name", name));
            screen.button(label, null, width,
                    DialogActions.FRIENDS_SETTING_TOGGLE + setting.id() + "_" + target);
        }
        String backLabel = config.string(SETTINGS + ".BACK-LABEL", "Back");
        if (backLabel != null && !backLabel.isBlank()) {
            screen.button(
                    backLabel,
                    null,
                    config.integer(SETTINGS + ".BACK-WIDTH", width),
                    DialogActions.NAMESPACE + ':' + DialogActions.FRIENDS_VIEW + target
            );
        }
        return show(player, screen.build());
    }

    @Override
    public boolean handle(Player player, String action, DialogResponseView response) {
        if (DialogActions.FRIENDS.equals(action)) {
            open(player);
            return true;
        }
        if (DialogActions.FRIENDS_FILTER.equals(action)) {
            session(player).cycleFriendFilter();
            open(player);
            return true;
        }
        if (DialogActions.FRIENDS_SEARCH_PROMPT.equals(action)) {
            openPrompt(player, SEARCH, "Search Friends");
            return true;
        }
        if (DialogActions.FRIENDS_FOLLOW_PROMPT.equals(action)) {
            openPrompt(player, FOLLOW_PROMPT, "Follow Player");
            return true;
        }
        if (DialogActions.FRIENDS_SEARCH_EXECUTE.equals(action)) {
            session(player).setFriendSearch(typedName(response));
            open(player);
            return true;
        }
        if (DialogActions.FRIENDS_FOLLOW_EXECUTE.equals(action)) {
            String query = typedName(response);
            if (query == null) {
                openPrompt(player, FOLLOW_PROMPT, "Follow Player");
                return true;
            }
            List<DialogSession.Shortcut> results = searchAnyone(player, query);
            session(player).setSearchResults(results);
            openFollowResults(player, results);
            return true;
        }

        String toggle = DialogActions.argument(action, DialogActions.FRIENDS_SETTING_TOGGLE);
        if (toggle != null) {
            toggleFriendSetting(player, toggle);
            return true;
        }

        String follow = DialogActions.argument(action, DialogActions.FRIENDS_FOLLOW_ADD);
        if (follow != null) {
            followTarget(player, parseUuid(follow));
            return true;
        }

        String unfollow = DialogActions.argument(action, DialogActions.FRIENDS_UNFOLLOW);
        if (unfollow != null) {
            unfollowTarget(player, parseUuid(unfollow));
            return true;
        }

        String settings = DialogActions.argument(action, DialogActions.FRIENDS_SETTINGS);
        if (settings != null) {
            UUID target = parseUuid(settings);
            if (target != null) {
                openSettings(player, target);
            } else {
                open(player);
            }
            return true;
        }

        String view = DialogActions.argument(action, DialogActions.FRIENDS_VIEW);
        if (view != null) {
            UUID target = parseUuid(view);
            if (target != null) {
                openDetails(player, target);
            } else {
                open(player);
            }
            return true;
        }
        return false;
    }

    /**
     * + Follow never follows from the typed name alone: it opens a picker of matches so the
     * player chooses who to follow, matching {@code Design/Dialog API/Friends}.
     */
    private boolean openFollowResults(Player player, List<DialogSession.Shortcut> results) {
        Map<String, String> tokens = DialogConfig.tokens("player", player.getName());
        int width = config.buttonWidth(FOLLOW_RESULTS, 100);
        DialogFactory.Screen screen = screen(player)
                .title(config.string(FOLLOW_RESULTS + ".TITLE", "Follow Results", tokens))
                .externalTitle(config.string(FOLLOW_RESULTS + ".EXTERNAL-TITLE", null, tokens))
                .afterAction(config.string(FOLLOW_RESULTS + ".AFTER-ACTION", "NONE"))
                .columns(config.columns(FOLLOW_RESULTS, 2));

        if (results.isEmpty()) {
            screen.text(config.string(FOLLOW_RESULTS + ".NO-RESULTS", "&7No players matched that name."));
        }
        for (DialogSession.Shortcut result : results) {
            screen.button(
                    playerLine(player, result.uuid(), result.name()),
                    null,
                    width,
                    DialogActions.FRIENDS_FOLLOW_ADD + result.uuid()
            );
        }
        screen.exit(
                config.string(FOLLOW_RESULTS + ".BACK-LABEL", "Back"),
                config.string(FOLLOW_RESULTS + ".BACK-ACTION",
                        DialogActions.NAMESPACE + ':' + DialogActions.FRIENDS_FOLLOW_PROMPT),
                config.integer(FOLLOW_RESULTS + ".BACK-WIDTH", width)
        );
        return show(player, screen.build());
    }

    /** Online name matches first; if none, an exact known-account lookup so offline players still appear. */
    private List<DialogSession.Shortcut> searchAnyone(Player player, String query) {
        List<DialogSession.Shortcut> results = new ArrayList<>();
        if (query == null) {
            return results;
        }
        String needle = query.toLowerCase(Locale.ROOT);
        Set<UUID> seen = new LinkedHashSet<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId().equals(player.getUniqueId()) || hiddenFrom(player, online)) {
                continue;
            }
            String name = publicName(player, online.getUniqueId(), online.getName());
            if (name.toLowerCase(Locale.ROOT).contains(needle)
                    || online.getName().toLowerCase(Locale.ROOT).contains(needle)) {
                if (seen.add(online.getUniqueId())) {
                    results.add(new DialogSession.Shortcut(online.getUniqueId(), name));
                }
            }
        }
        if (results.isEmpty()) {
            OfflinePlayer resolved = resolvePlayer(query);
            if (resolved != null && resolved.getUniqueId() != null
                    && !resolved.getUniqueId().equals(player.getUniqueId())
                    && seen.add(resolved.getUniqueId())) {
                results.add(new DialogSession.Shortcut(
                        resolved.getUniqueId(),
                        publicName(player, resolved.getUniqueId(),
                                resolved.getName() == null ? query : resolved.getName())
                ));
            }
        }
        return results;
    }

    private boolean hiddenFrom(Player viewer, Player target) {
        if (plugin.getHideManager() == null) {
            return false;
        }
        return plugin.getHideManager().isHidden(target.getUniqueId())
                && !plugin.getHideManager().canSeeRealIdentity(viewer);
    }

    private void followTarget(Player player, UUID target) {
        if (target == null) {
            open(player);
            return;
        }
        String name = publicName(player, target, target.toString());
        if (plugin.getFriendsManager().isFollowing(player.getUniqueId(), target)) {
            openDetails(player, target);
            return;
        }
        boolean success = plugin.getFriendsManager().followPlayer(player, target, name);
        if (success) {
            message(player, plugin.getConfigManager().getMessage("FRIENDS.FOLLOW_SUCCESS", "{player}", name));
        } else {
            message(player, plugin.getConfigManager().getMessage("FRIENDS.FOLLOW_FAILURE"));
        }
        openDetails(player, target);
    }

    private void unfollowTarget(Player player, UUID target) {
        if (target == null) {
            open(player);
            return;
        }
        String name = publicName(player, target, target.toString());
        boolean success = plugin.getFriendsManager().unfollowPlayer(player, target);
        if (success) {
            message(player, plugin.getConfigManager().getMessage("FRIENDS.UNFOLLOW_SUCCESS", "{player}", name));
        } else {
            message(player, plugin.getConfigManager().getMessage("FRIENDS.UNFOLLOW_FAILURE"));
        }
        open(player);
    }

    /** Splits {@code <setting>_<uuid>} and flips that one flag on the follow entry. */
    private void toggleFriendSetting(Player player, String argument) {
        int separator = argument.lastIndexOf('_');
        if (separator < 0) {
            open(player);
            return;
        }
        UUID target = parseUuid(argument.substring(separator + 1));
        FriendSetting setting = FriendSetting.byId(argument.substring(0, separator));
        if (target == null || setting == null) {
            open(player);
            return;
        }
        FollowEntry entry = plugin.getFriendsManager().getFollowEntry(player.getUniqueId(), target);
        if (entry == null) {
            openDetails(player, target);
            return;
        }
        boolean[] values = FriendSetting.readAll(entry);
        values[setting.ordinal()] = !values[setting.ordinal()];
        plugin.getFriendsManager().updateFollowSettings(
                player.getUniqueId(), target,
                values[FriendSetting.TRANSACTIONS.ordinal()],
                values[FriendSetting.MESSAGES.ordinal()],
                values[FriendSetting.PAYMENTS.ordinal()],
                values[FriendSetting.ACTIVITY.ordinal()],
                values[FriendSetting.AUTO_TPA.ordinal()],
                values[FriendSetting.TELEPORT_REQUESTS.ordinal()]
        );
        openSettings(player, target);
    }

    private List<FriendRow> collect(Player viewer, DialogSession.FriendFilter filter, String search) {
        UUID viewerId = viewer.getUniqueId();
        Map<UUID, FriendRow> entries = new LinkedHashMap<>();

        for (FollowEntry entry : plugin.getFriendsManager().getFollowing(viewerId)) {
            UUID uuid = entry.followedUuid();
            entries.put(uuid, new FriendRow(
                    uuid,
                    publicName(viewer, uuid, entry.followedNameSnapshot()),
                    true,
                    plugin.getFriendsManager().isFollower(viewerId, uuid)
            ));
        }
        for (FollowEntry entry : plugin.getFriendsManager().getFollowers(viewerId)) {
            UUID uuid = entry.followerUuid();
            FriendRow existing = entries.get(uuid);
            if (existing != null) {
                entries.put(uuid, new FriendRow(uuid, existing.name(), true, true));
            } else {
                entries.put(uuid, new FriendRow(
                        uuid,
                        publicName(viewer, uuid, null),
                        false,
                        true
                ));
            }
        }

        String needle = search == null ? "" : search.toLowerCase(Locale.ROOT);
        List<FriendRow> list = new ArrayList<>();
        for (FriendRow row : entries.values()) {
            switch (filter) {
                case FRIENDS -> {
                    if (!row.isFriend()) {
                        continue;
                    }
                }
                case FOLLOWING -> {
                    if (!row.following()) {
                        continue;
                    }
                }
                case FOLLOWERS -> {
                    if (!row.follower()) {
                        continue;
                    }
                }
                default -> {
                }
            }
            if (!needle.isEmpty() && !row.name().toLowerCase(Locale.ROOT).contains(needle)) {
                continue;
            }
            list.add(row);
        }
        list.sort((left, right) -> {
            boolean leftOnline = Bukkit.getPlayer(left.uuid()) != null;
            boolean rightOnline = Bukkit.getPlayer(right.uuid()) != null;
            if (leftOnline != rightOnline) {
                return leftOnline ? -1 : 1;
            }
            return left.name().compareToIgnoreCase(right.name());
        });
        return list;
    }

    private int countFriends(UUID viewer) {
        int count = 0;
        for (FollowEntry entry : plugin.getFriendsManager().getFollowing(viewer)) {
            if (plugin.getFriendsManager().isFriend(viewer, entry.followedUuid())) {
                count++;
            }
        }
        return count;
    }

    private String nameOf(UUID uuid, String snapshot) {
        String resolved = displayName(uuid, snapshot);
        return resolved == null || resolved.isBlank() ? uuid.toString() : resolved;
    }

    private String publicName(Player viewer, UUID uuid, String snapshot) {
        String fallback = nameOf(uuid, snapshot);
        if (plugin.getHideManager() == null) {
            return fallback;
        }
        Player online = Bukkit.getPlayer(uuid);
        if (online != null) {
            return plugin.getHideManager().visibleName(viewer, online);
        }
        return plugin.getHideManager().publicName(uuid, fallback);
    }

    private String emptyLabel(DialogSession session) {
        if (session.getFriendSearch() != null) {
            return config.string(MENU + ".EMPTY-SEARCH", "&7No players matched that name.");
        }
        return config.string(MENU + ".EMPTY-" + session.getFriendFilter().name(),
                "&7No friends or following");
    }

    private String searchTooltip(DialogSession session) {
        String query = session.getFriendSearch();
        if (query == null) {
            return config.string(MENU + ".SEARCH-TOOLTIP", "&7Click to search");
        }
        return DialogConfig.apply(
                config.string(MENU + ".SEARCH-ACTIVE-TOOLTIP", "&7Currently: &f%query%\n&7Click to search"),
                DialogConfig.tokens("query", query)
        );
    }

    private String filterTooltip(DialogSession session) {
        String template = config.string(MENU + ".FILTER-TOOLTIP-TEMPLATE", null);
        if (template == null) {
            return null;
        }
        String active = config.string(MENU + ".ACTIVE-FORMAT", "&f- %label%");
        String inactive = config.string(MENU + ".INACTIVE-FORMAT", "&7%label%");
        Map<String, String> tokens = new LinkedHashMap<>();
        for (DialogSession.FriendFilter filter : DialogSession.FriendFilter.values()) {
            String label = config.string(MENU + ".FILTER-LABELS." + filter.name(), filter.name());
            String format = filter == session.getFriendFilter() ? active : inactive;
            tokens.put(filter.name().toLowerCase(Locale.ROOT),
                    format.replace("%label%", label));
        }
        return DialogConfig.apply(template, tokens);
    }

    private String relationshipTooltip(FriendRow row) {
        String path = MENU + ".RELATIONSHIP-TOOLTIP.";
        String relation;
        if (row.isFriend()) {
            relation = config.string(path + "FRIENDS", "&fFriends");
        } else if (row.following()) {
            relation = config.string(path + "FOLLOWING", "&fFollowing");
        } else if (row.follower()) {
            relation = config.string(path + "FOLLOWERS", "&fFollows you");
        } else {
            relation = "";
        }
        boolean online = Bukkit.getPlayer(row.uuid()) != null;
        String status = online
                ? config.string(path + "ONLINE", "&#00FC00Online")
                : config.string(path + "OFFLINE", "&7Offline");
        String hint = row.following()
                ? config.string(path + "CLICK-TO-EDIT", "&7Click to edit")
                : config.string(path + "CLICK-TO-FOLLOW", "&7Click to follow");
        return (relation.isEmpty() ? "" : relation + "\n") + status + "\n" + hint;
    }

    private Component playerLine(Player viewer, UUID uuid, String name) {
        Component head = DialogPlayerHeads.component(uuid, name);
        Component text = DialogText.of(name, viewer);
        if (head == null || head.equals(Component.empty())) {
            return text;
        }
        return head.append(Component.space()).append(text);
    }

    private Component tooltipComponent(Player player, String tooltip) {
        return DialogText.tooltip(tooltip, player);
    }

    private String typedName(DialogResponseView response) {
        String name = input(response, "player_name");
        return name != null ? name : input(response, "query");
    }

    /** Braces instead of percent signs, matching the counters in the reference config. */
    private static String applyBraces(String text, Map<String, String> tokens) {
        if (text == null) {
            return null;
        }
        String result = text;
        for (Map.Entry<String, String> token : tokens.entrySet()) {
            result = result.replace('{' + token.getKey() + '}', token.getValue());
        }
        return result;
    }

    /**
     * The six per-friend switches, in the order {@code updateFollowSettings} expects them.
     * {@link #DISPLAY_ORDER} is the on-screen stack from the reference.
     */
    private enum FriendSetting {
        TRANSACTIONS("transactions", "TRANSACTIONS-LABEL"),
        MESSAGES("messages", "MESSAGE-LABEL"),
        PAYMENTS("payments", "PAY-LABEL"),
        ACTIVITY("activity", "ACTIVITY-LABEL"),
        AUTO_TPA("autotpa", "AUTO-TPA-LABEL"),
        TELEPORT_REQUESTS("tpa", "TPA-LABEL");

        static final FriendSetting[] DISPLAY_ORDER = {
                ACTIVITY, TRANSACTIONS, MESSAGES, TELEPORT_REQUESTS, AUTO_TPA, PAYMENTS
        };

        private final String id;
        private final String labelKey;

        FriendSetting(String id, String labelKey) {
            this.id = id;
            this.labelKey = labelKey;
        }

        String id() {
            return id;
        }

        String labelKey() {
            return labelKey;
        }

        boolean read(FollowEntry entry) {
            return readAll(entry)[ordinal()];
        }

        static boolean[] readAll(FollowEntry entry) {
            return new boolean[]{
                    entry.transactionsEnabled(),
                    entry.messagesEnabled(),
                    entry.paymentsEnabled(),
                    entry.activityEnabled(),
                    entry.tpaAutoAcceptEnabled(),
                    entry.teleportRequestsEnabled()
            };
        }

        static FriendSetting byId(String id) {
            for (FriendSetting setting : values()) {
                if (setting.id.equals(id)) {
                    return setting;
                }
            }
            return null;
        }
    }
}
