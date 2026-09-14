package com.bx.ultimateDonutSmp2.dialogs.screens;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.dialogs.DialogActions;
import com.bx.ultimateDonutSmp2.dialogs.DialogConfig;
import com.bx.ultimateDonutSmp2.dialogs.DialogFactory;
import com.bx.ultimateDonutSmp2.dialogs.DialogScreen;
import com.bx.ultimateDonutSmp2.dialogs.DialogSession;
import com.bx.ultimateDonutSmp2.models.FollowEntry;
import io.papermc.paper.dialog.DialogResponseView;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The teleport request dialogs: player list, add player prompt, and teleport direction choice.
 */
public final class TeleportDialog extends DialogScreen {

    private static final String LIST = "TELEPORT_DIALOG";
    private static final String ADD = "ADD_TELEPORT_PLAYER_DIALOG";
    private static final String CONFIRM = "TELEPORT_CONFIRM_DIALOG";

    public TeleportDialog(UltimateDonutSmp2 plugin, DialogConfig config, DialogSession.Store sessions) {
        super(plugin, config, sessions);
    }

    public boolean open(Player player) {
        DialogSession session = session(player);
        session.setOverlayReturnAction(null);
        seedShortlist(player, session);

        Map<String, String> tokens = DialogConfig.tokens("player", player.getName());
        int width = config.buttonWidth(LIST, 150);
        DialogFactory.Screen screen = screen(player)
                .title(config.string(LIST + ".TITLE", "Teleport", tokens))
                .externalTitle(config.string(LIST + ".EXTERNAL-TITLE", null, tokens))
                .columns(config.columns(LIST, 2))
                .text(config.string(LIST + ".DESCRIPTION", "Click a player to teleport", tokens));

        List<DialogSession.Shortcut> shortcuts = session.getTeleportList();
        if (shortcuts.isEmpty()) {
            screen.text(config.string(LIST + ".EMPTY-TEXT", "&7Nobody on your list yet."));
        }
        for (DialogSession.Shortcut shortcut : shortcuts) {
            Component head = DialogPlayerHeads.component(shortcut.uuid(), shortcut.name());
            Component label = head == null || head.equals(Component.empty())
                    ? Component.text(shortcut.name())
                    : head.append(Component.space()).append(Component.text(shortcut.name()));
            screen.button(label, null, width, DialogActions.TPA_TARGET + shortcut.uuid());
        }
        screen.button(
                config.string(LIST + ".ADD-BUTTON-LABEL", "&7+ Add Player to List"),
                config.string(LIST + ".ADD-BUTTON-TOOLTIP", "Type a player name to add"),
                config.integer(LIST + ".ADD-BUTTON-WIDTH", 200),
                DialogActions.TPA_ADD_PROMPT
        );
        String backLabel = config.string(LIST + ".BACK-LABEL", null);
        if (backLabel != null && !backLabel.isBlank()) {
            screen.exit(
                    backLabel,
                    config.string(LIST + ".BACK-ACTION", DialogActions.NAMESPACE + ':' + DialogActions.MAIN_MENU),
                    config.integer(LIST + ".ADD-BUTTON-WIDTH", 200)
            );
        }
        return show(player, screen.build());
    }

    private boolean openAdd(Player player) {
        Map<String, String> tokens = DialogConfig.tokens("player", player.getName());
        return show(player, screen(player)
                .title(config.string(ADD + ".TITLE", "Add a Player", tokens))
                .externalTitle(config.string(ADD + ".EXTERNAL-TITLE", null, tokens))
                .columns(config.columns(ADD, 2))
                .text(config.string(ADD + ".DESCRIPTION", "Type a name to add to your teleport list", tokens))
                .inputs(config.inputs(ADD, tokens))
                .buttons(config.buttonsOf(ADD, tokens))
                .build());
    }

    private boolean openTarget(Player player, UUID target, String targetName) {
        String name = targetName != null ? targetName : displayName(target, target.toString());
        Map<String, String> tokens = DialogConfig.tokens("target", name, "player", player.getName());
        int width = config.buttonWidth(CONFIRM, 150);

        DialogFactory.Screen screen = screen(player)
                .title(config.string(CONFIRM + ".TITLE", "Teleport with %target%?", tokens))
                .externalTitle(config.string(CONFIRM + ".EXTERNAL-TITLE", null, tokens))
                .columns(config.columns(CONFIRM, 1));

        Component head = DialogPlayerHeads.component(target, name);
        Component header = head == null || head.equals(Component.empty())
                ? Component.text(name)
                : head.append(Component.space()).append(Component.text(name));
        screen.text(header);

        screen.button(
                config.string(CONFIRM + ".TELEPORT-TO-LABEL", "Teleport to"),
                null,
                width,
                DialogActions.TPA_TO + name
        );
        screen.button(
                config.string(CONFIRM + ".TELEPORT-HERE-LABEL", "Teleport here"),
                null,
                width,
                DialogActions.TPA_HERE + name
        );
        screen.button(
                config.string(CONFIRM + ".BACK-LABEL", "Back"),
                null,
                width,
                backActionId(player, DialogActions.TPA)
        );
        return show(player, screen.build());
    }

    @Override
    public boolean handle(Player player, String action, DialogResponseView response) {
        if (DialogActions.TPA.equals(action)) {
            open(player);
            return true;
        }
        if (DialogActions.TPA_ADD_PROMPT.equals(action)) {
            openAdd(player);
            return true;
        }
        if (DialogActions.TPA_ADD_EXECUTE.equals(action)) {
            String name = input(response, "player_name");
            if (name == null || name.isBlank()) {
                open(player);
                return true;
            }
            OfflinePlayer resolved = resolvePlayer(name);
            UUID targetUuid = resolved != null && resolved.getUniqueId() != null
                    ? resolved.getUniqueId()
                    : UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
            String resolvedName = resolved != null && resolved.getName() != null ? resolved.getName() : name;
            session(player).rememberTeleport(targetUuid, resolvedName);
            openTarget(player, targetUuid, resolvedName);
            return true;
        }
        String targetArg = DialogActions.argument(action, DialogActions.TPA_TARGET);
        if (targetArg != null) {
            UUID target = parseUuid(targetArg);
            if (target != null) {
                openTarget(player, target, displayName(target, target.toString()));
                return true;
            }
        }
        String toArg = DialogActions.argument(action, DialogActions.TPA_TO);
        if (toArg != null) {
            click(player);
            runCommand(player, "tpa " + toArg);
            return true;
        }
        String hereArg = DialogActions.argument(action, DialogActions.TPA_HERE);
        if (hereArg != null) {
            click(player);
            runCommand(player, "tpahere " + hereArg);
            return true;
        }
        if (DialogActions.TPA_EXECUTE.equals(action)) {
            String name = input(response, "player_name");
            if (name == null) {
                open(player);
                return true;
            }
            click(player);
            String direction = input(response, "direction");
            runCommand(player, ("tpahere".equalsIgnoreCase(direction) ? "tpahere " : "tpa ") + name);
            return true;
        }
        return false;
    }

    private void seedShortlist(Player player, DialogSession session) {
        if (!session.getTeleportList().isEmpty()) {
            return;
        }
        if (plugin.getFriendsManager() != null) {
            for (FollowEntry entry : plugin.getFriendsManager().getFollowing(player.getUniqueId())) {
                session.rememberTeleport(entry.followedUuid(), entry.followedNameSnapshot());
            }
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.getUniqueId().equals(player.getUniqueId()) && !hiddenFrom(player, online)) {
                String name = plugin.getHideManager() != null
                        ? plugin.getHideManager().visibleName(player, online)
                        : online.getName();
                session.rememberTeleport(online.getUniqueId(), name);
            }
        }
    }

    private boolean hiddenFrom(Player viewer, Player target) {
        if (target == null || plugin.getHideManager() == null) {
            return false;
        }
        return plugin.getHideManager().isHidden(target.getUniqueId())
                && !plugin.getHideManager().canSeeRealIdentity(viewer);
    }
}
