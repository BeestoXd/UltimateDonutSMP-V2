package com.bx.ultimateDonutSmp2.dialogs.screens;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.dialogs.DialogActions;
import com.bx.ultimateDonutSmp2.dialogs.DialogConfig;
import com.bx.ultimateDonutSmp2.dialogs.DialogFactory;
import com.bx.ultimateDonutSmp2.dialogs.DialogScreen;
import com.bx.ultimateDonutSmp2.dialogs.DialogSession;
import com.bx.ultimateDonutSmp2.dialogs.DialogSupport;
import com.bx.ultimateDonutSmp2.managers.CurrencyManager;
import com.bx.ultimateDonutSmp2.models.FollowEntry;
import com.bx.ultimateDonutSmp2.models.PlayerData;
import com.bx.ultimateDonutSmp2.utils.NumberUtils;
import com.bx.ultimateDonutSmp2.utils.PaymentUtils;
import io.papermc.paper.dialog.DialogResponseView;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The pay flow: pick a player, pick an amount, confirm.
 *
 * <p>The shortlist starts as the people the player follows so it is useful before they have
 * added anyone, and grows as they pay names typed into the prompt. The payment itself still
 * goes through {@link PaymentUtils}, so every check the chat command applies applies here too.
 */
public final class PayDialog extends DialogScreen {

    private static final String LIST = "PAY_DIALOG";
    private static final String ADD = "ADD_PAY_PLAYER_DIALOG";
    private static final String AMOUNTS = "PAY_AMOUNTS_DIALOG";
    private static final String CUSTOM = "PAY_CUSTOM_DIALOG";
    private static final String CONFIRM = "PAY_CONFIRM_DIALOG";

    public PayDialog(UltimateDonutSmp2 plugin, DialogConfig config, DialogSession.Store sessions) {
        super(plugin, config, sessions);
    }

    public boolean open(Player player) {
        DialogSession session = session(player);
        session.setOverlayReturnAction(null);
        seedShortlist(player, session);

        Map<String, String> tokens = DialogConfig.tokens("player", player.getName());
        int width = config.buttonWidth(LIST, 150);
        DialogFactory.Screen screen = screen(player)
                .title(config.string(LIST + ".TITLE", "Pay", tokens))
                .externalTitle(config.string(LIST + ".EXTERNAL-TITLE", null, tokens))
                .columns(config.columns(LIST, 2))
                .text(config.string(LIST + ".DESCRIPTION", "Click a player to pay", tokens));

        List<DialogSession.Shortcut> shortcuts = session.getPayList();
        if (shortcuts.isEmpty()) {
            screen.text(config.string(LIST + ".EMPTY-TEXT", "&7Nobody on your list yet."));
        }
        for (DialogSession.Shortcut shortcut : shortcuts) {
            Component head = DialogPlayerHeads.component(shortcut.uuid(), shortcut.name());
            Component label = head == null || head.equals(Component.empty())
                    ? Component.text(shortcut.name())
                    : head.append(Component.space()).append(Component.text(shortcut.name()));
            screen.button(label, null, width, DialogActions.PAY_TARGET + shortcut.uuid());
        }
        screen.button(
                config.string(LIST + ".ADD-BUTTON-LABEL", "&7+ Add Player to List"),
                null,
                config.integer(LIST + ".ADD-BUTTON-WIDTH", 150),
                DialogActions.PAY_ADD_PROMPT
        );
        String backLabel = config.string(LIST + ".BACK-LABEL", null);
        if (backLabel != null && !backLabel.isBlank()) {
            screen.exit(
                    backLabel,
                    config.string(LIST + ".BACK-ACTION", DialogActions.NAMESPACE + ':' + DialogActions.MAIN_MENU),
                    config.integer(LIST + ".ADD-BUTTON-WIDTH", 150)
            );
        }
        return show(player, screen.build());
    }

    /** Jumps straight into the flow, used by {@code /pay <name> [amount]}. */
    public boolean openFor(Player player, UUID target, String targetName, Double amount) {
        session(player).setOverlayReturnAction(null);
        if (target == null) {
            return open(player);
        }
        session(player).rememberPay(target, targetName);
        if (amount == null) {
            return openAmounts(player, target);
        }
        return openConfirm(player, target, amount);
    }

    private boolean openAdd(Player player) {
        Map<String, String> tokens = DialogConfig.tokens("player", player.getName());
        return show(player, screen(player)
                .title(config.string(ADD + ".TITLE", "Add a Player", tokens))
                .externalTitle(config.string(ADD + ".EXTERNAL-TITLE", null, tokens))
                .columns(config.columns(ADD, 1))
                .text(config.string(ADD + ".DESCRIPTION", null, tokens))
                .inputs(config.inputs(ADD, tokens))
                .buttons(config.buttonsOf(ADD, tokens))
                .build());
    }

    private boolean openAmounts(Player player, UUID target) {
        String name = displayName(target, target.toString());
        Map<String, String> tokens = DialogConfig.tokens(
                "target", name,
                "target_uuid", target.toString(),
                "player", player.getName()
        );
        int width = config.buttonWidth(AMOUNTS, 150);
        DialogFactory.Screen screen = screen(player)
                .title(config.string(AMOUNTS + ".TITLE", "Pay %target%", tokens))
                .externalTitle(config.string(AMOUNTS + ".EXTERNAL-TITLE", null, tokens))
                .columns(config.columns(AMOUNTS, 2));

        Component head = DialogPlayerHeads.component(target, name);
        Component headAndName = head == null || head.equals(Component.empty())
                ? Component.text(name)
                : DialogPlayerHeads.insertBetween(null, head, Component.text(" " + name));
        screen.text(headAndName);

        String description = config.string(AMOUNTS + ".DESCRIPTION", "Choose an amount to pay", tokens);
        if (description != null && !description.isBlank()) {
            screen.text(description);
        }

        boolean hasBackButton = false;
        for (Map<?, ?> entry : plugin.getConfigManager().getDialog().getMapList(AMOUNTS + ".AMOUNTS")) {
            Object label = entry.get("LABEL");
            if (label == null) {
                continue;
            }
            Object rawAmount = entry.get("AMOUNT");
            if (rawAmount instanceof Number number) {
                screen.button(String.valueOf(label), null, width,
                        DialogActions.PAY_AMOUNT + target + "_" + keySafeAmount(number.doubleValue()));
            } else {
                Object action = entry.get("ACTION");
                if ("back".equalsIgnoreCase(String.valueOf(action))) {
                    hasBackButton = true;
                    screen.button(String.valueOf(label), null, width,
                            backAction(player, config.string(AMOUNTS + ".BACK-ACTION",
                                    DialogActions.NAMESPACE + ':' + DialogActions.PAY_MENU, tokens)));
                } else {
                    // Anything without an AMOUNT is the custom-entry button.
                    screen.button(String.valueOf(label), null, width, DialogActions.PAY_CUSTOM + target);
                }
            }
        }

        if (!hasBackButton) {
            String backLabel = config.string(AMOUNTS + ".BACK-LABEL", "&7Back");
            if (backLabel != null && !backLabel.isBlank()) {
                screen.button(
                        backLabel,
                        null,
                        width,
                        backAction(player, config.string(AMOUNTS + ".BACK-ACTION",
                                DialogActions.NAMESPACE + ':' + DialogActions.PAY_MENU, tokens))
                );
            }
        }

        return show(player, screen.build());
    }

    private boolean openCustom(Player player, UUID target) {
        Map<String, String> tokens = DialogConfig.tokens(
                "target", displayName(target, target.toString()),
                "target_uuid", target.toString()
        );
        return show(player, screen(player)
                .title(config.string(CUSTOM + ".TITLE", "Pay %target%", tokens))
                .externalTitle(config.string(CUSTOM + ".EXTERNAL-TITLE", null, tokens))
                .columns(config.columns(CUSTOM, 1))
                .text(config.string(CUSTOM + ".DESCRIPTION", null, tokens))
                .inputs(config.inputs(CUSTOM, tokens))
                .buttons(config.buttonsOf(CUSTOM, tokens))
                .build());
    }

    private boolean openConfirm(Player player, UUID target, double amount) {
        PlayerData data = plugin.getPlayerDataManager().get(player);
        if (data != null && !data.isPayConfirmMenuEnabled()) {
            return execute(player, target, amount);
        }

        String name = displayName(target, target.toString());
        String safeAmount = keySafeAmount(amount);
        Map<String, String> tokens = DialogConfig.tokens(
                "target", name,
                "target_uuid", target.toString(),
                "raw_amount", safeAmount,
                "amount", plugin.getCurrencyManager()
                        .formatCompactAmount(CurrencyManager.CurrencyType.MONEY, amount)
        );
        int width = config.buttonWidth(CONFIRM, 150);
        String amountAction = DialogActions.PAY_EXECUTE + target + "_" + safeAmount;

        DialogFactory.Screen screen = screen(player)
                .title(config.string(CONFIRM + ".TITLE", "Are you sure you want to pay %target%?", tokens))
                .externalTitle(config.string(CONFIRM + ".EXTERNAL-TITLE", null, tokens))
                .columns(config.columns(CONFIRM, 2));

        Component head = DialogPlayerHeads.component(target, name);
        Component headAndName = head == null || head.equals(Component.empty())
                ? Component.text(name)
                : DialogPlayerHeads.insertBetween(null, head, Component.text(" " + name));
        screen.text(headAndName);

        screen.text(config.string(CONFIRM + ".AMOUNT-LABEL", "&#00FC00$ &f%amount%", tokens));

        String yesAction = config.string(CONFIRM + ".YES-ACTION",
                DialogActions.NAMESPACE + ':' + amountAction, tokens);
        String noAction = config.string(CONFIRM + ".NO-ACTION",
                DialogActions.NAMESPACE + ':' + DialogActions.PAY_TARGET + target, tokens);

        screen.button(config.string(CONFIRM + ".YES-LABEL", "&#00FC00Yes", tokens), null, width, yesAction);
        screen.button(config.string(CONFIRM + ".NO-LABEL", "&cNo", tokens), null, width, noAction);
        return show(player, screen.build());
    }

    private boolean execute(Player player, UUID target, double amount) {
        String name = displayName(target, null);
        if (name == null) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(target);
            if (offline.getName() != null) {
                name = offline.getName();
            }
        }
        if (name == null) {
            message(player, plugin.getConfigManager().getMessage("BALANCE.PAY.PLAYER-NOT-FOUND"));
            return open(player);
        }
        session(player).rememberPay(target, name);
        DialogSupport.close(player);
        return PaymentUtils.transferMoney(plugin, player, name, amount);
    }

    @Override
    public boolean handle(Player player, String action, DialogResponseView response) {
        if (DialogActions.PAY_MENU.equals(action)) {
            open(player);
            return true;
        }
        if (DialogActions.PAY_ADD_PROMPT.equals(action)) {
            openAdd(player);
            return true;
        }
        if (DialogActions.PAY_ADD_EXECUTE.equals(action)) {
            addFromInput(player, response);
            return true;
        }

        String customContinue = DialogActions.argument(action, DialogActions.PAY_CUSTOM_CONTINUE);
        if (customContinue != null) {
            continueCustom(player, parseUuid(customContinue), response);
            return true;
        }

        String custom = DialogActions.argument(action, DialogActions.PAY_CUSTOM);
        if (custom != null) {
            UUID target = parseUuid(custom);
            if (target != null) {
                openCustom(player, target);
            } else {
                open(player);
            }
            return true;
        }

        String amountArgument = DialogActions.argument(action, DialogActions.PAY_AMOUNT);
        if (amountArgument != null) {
            withTargetAndAmount(player, amountArgument, this::openConfirm);
            return true;
        }

        String executeArgument = DialogActions.argument(action, DialogActions.PAY_EXECUTE);
        if (executeArgument != null) {
            click(player);
            withTargetAndAmount(player, executeArgument, this::execute);
            return true;
        }

        String target = DialogActions.argument(action, DialogActions.PAY_TARGET);
        if (target != null) {
            UUID uuid = parseUuid(target);
            if (uuid != null) {
                openAmounts(player, uuid);
            } else {
                open(player);
            }
            return true;
        }
        return false;
    }

    private void addFromInput(Player player, DialogResponseView response) {
        String name = input(response, "player_name");
        if (name == null) {
            openAdd(player);
            return;
        }
        if (name.equalsIgnoreCase(player.getName())) {
            message(player, plugin.getConfigManager().getMessage("BALANCE.PAY.CANT-PAY-SELF"));
            openAdd(player);
            return;
        }
        OfflinePlayer resolved = resolvePlayer(name);
        if (resolved == null || resolved.getUniqueId() == null) {
            message(player, plugin.getConfigManager().getMessage("BALANCE.PAY.PLAYER-NOT-FOUND"));
            openAdd(player);
            return;
        }
        session(player).rememberPay(resolved.getUniqueId(), resolved.getName() == null ? name : resolved.getName());
        openAmounts(player, resolved.getUniqueId());
    }

    private void continueCustom(Player player, UUID target, DialogResponseView response) {
        if (target == null) {
            open(player);
            return;
        }
        String raw = input(response, "amount");
        double amount;
        try {
            amount = NumberUtils.parse(raw == null ? "" : raw);
        } catch (NumberFormatException ignored) {
            message(player, plugin.getConfigManager().getMessage("BALANCE.PAY.INVALID-AMOUNT"));
            openCustom(player, target);
            return;
        }
        if (amount <= 0) {
            message(player, plugin.getConfigManager().getMessage("BALANCE.PAY.MUST-BE-POSITIVE"));
            openCustom(player, target);
            return;
        }
        openConfirm(player, target, amount);
    }

    /** Splits {@code <uuid>_<amount>} and hands both halves to the given step. */
    private void withTargetAndAmount(Player player, String argument, TargetAmountStep step) {
        int separator = argument.indexOf('_');
        if (separator < 0) {
            open(player);
            return;
        }
        UUID target = parseUuid(argument.substring(0, separator));
        if (target == null) {
            open(player);
            return;
        }
        double amount;
        try {
            amount = NumberUtils.parse(argument.substring(separator + 1));
        } catch (NumberFormatException ignored) {
            openAmounts(player, target);
            return;
        }
        if (amount <= 0) {
            openAmounts(player, target);
            return;
        }
        step.run(player, target, amount);
    }

    @FunctionalInterface
    private interface TargetAmountStep {
        boolean run(Player player, UUID target, double amount);
    }

    /** Fills an empty shortlist with the people this player follows. */
    private void seedShortlist(Player player, DialogSession session) {
        if (!session.getPayList().isEmpty() || plugin.getFriendsManager() == null) {
            return;
        }
        for (FollowEntry entry : plugin.getFriendsManager().getFollowing(player.getUniqueId())) {
            session.rememberPay(entry.followedUuid(), entry.followedNameSnapshot());
        }
    }

    /** A head for the dialog banner, or null when the target has never joined. */
    private org.bukkit.inventory.ItemStack headOf(UUID uuid) {
        return DialogHeads.item(plugin, uuid, displayName(uuid, null));
    }

    /** Renders an amount so it survives being embedded in a resource-location value. */
    private static String keySafeAmount(double amount) {
        if (amount == Math.rint(amount) && !Double.isInfinite(amount)) {
            return Long.toString((long) amount);
        }
        return Double.toString(amount);
    }
}
