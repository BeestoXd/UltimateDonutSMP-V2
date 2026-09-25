package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.HideManager;
import com.bx.ultimateDonutSmp2.models.HideState;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import com.bx.ultimateDonutSmp2.utils.ItemUtils;
import com.bx.ultimateDonutSmp2.utils.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HideListMenu extends BaseMenu {

    private static final int DEFAULT_SIZE = 54;

    private final int page;
    private final Map<Integer, UUID> targets = new HashMap<>();

    public HideListMenu(UltimateDonutSmp2 plugin, int page) {
        super(plugin, title(plugin, page), getMenuSize(plugin));
        this.page = Math.max(0, page);
    }

    public static int getMenuSize(UltimateDonutSmp2 plugin) {
        if (plugin == null || plugin.getConfigManager() == null || plugin.getConfigManager().getHide() == null) {
            return DEFAULT_SIZE;
        }
        int configured = plugin.getConfigManager().getHide().getInt("GUI.LIST.SIZE", DEFAULT_SIZE);
        return normalizeSize(configured);
    }

    private static int normalizeSize(int configured) {
        int size = Math.max(9, Math.min(54, configured));
        return size - (size % 9);
    }

    private int pageSize() {
        return Math.max(1, (inventory != null ? inventory.getSize() : getMenuSize(plugin)) - 9);
    }

    @Override
    public void build(Player player) {
        clear();
        targets.clear();
        if (!PermissionUtils.has(player, HideManager.ADMIN_PERMISSION)) {
            player.closeInventory();
            return;
        }
        List<HideState> states = plugin.getHideManager().getStates().stream().toList();
        int pageSize = pageSize();
        int start = page * pageSize;
        int end = Math.min(states.size(), start + pageSize);
        for (int index = start; index < end; index++) {
            HideState state = states.get(index);
            Player online = Bukkit.getPlayer(state.playerUuid());
            int slot = index - start;
            set(slot, ItemUtils.createPlayerHead(
                    Bukkit.getOfflinePlayer(state.playerUuid()),
                    "&b" + state.alias(),
                    List.of(
                            "&7Real name: &f" + state.realNameSnapshot(),
                            "&7Mode: &f" + state.mode().name(),
                            "&7Skin: &f" + (state.skinUsername().isBlank() ? "Original" : state.skinUsername()),
                            "&7Status: " + (online == null ? "&cOffline" : "&aOnline"),
                            "",
                            online == null ? "&7Left-click unavailable." : "&aLeft-click to teleport.",
                            "&cRight-click to remove."
                    )
            ));
            targets.put(slot, state.playerUuid());
        }
        renderNavigation(states.size());
    }

    @Override
    public void handleClick(int slot, Player player, ClickType clickType) {
        UUID targetUuid = targets.get(slot);
        if (targetUuid != null) {
            HideState state = plugin.getHideManager().getState(targetUuid);
            if (state == null) {
                build(player);
                return;
            }
            if (clickType.isRightClick()) {
                plugin.getHideManager().remove(targetUuid);
                player.sendMessage(ColorUtils.toComponent(plugin.getHideManager().message(
                        "ADMIN-REMOVED",
                        "&aSuccessfully removed hide from &f{player}&a.",
                        "{player}", state.realNameSnapshot()
                ), player));
                Player target = Bukkit.getPlayer(targetUuid);
                if (target != null) {
                    target.sendMessage(ColorUtils.toComponent(plugin.getHideManager().message(
                            "REMOVED-BY-ADMIN",
                            "&cYour hide state has been removed by an administrator."
                    ), target));
                }
                build(player);
                return;
            }
            Player target = Bukkit.getPlayer(targetUuid);
            if (target != null) {
                player.closeInventory();
                plugin.getSpigotScheduler().teleport(player, target.getLocation());
            }
            return;
        }
        int size = inventory.getSize();
        int navStart = size - 9;
        if (slot == navStart && page > 0) {
            playPageTurn(player);
            new HideListMenu(plugin, page - 1).open(player);
        } else if (slot == (size - 1) && (page + 1) * pageSize() < plugin.getHideManager().getStates().size()) {
            playPageTurn(player);
            new HideListMenu(plugin, page + 1).open(player);
        } else if (slot == (navStart + 4)) {
            new HideMenu(plugin).open(player);
        }
    }

    private void renderNavigation(int total) {
        int size = inventory.getSize();
        int navStart = size - 9;
        for (int slot = navStart; slot < size; slot++) {
            set(slot, ItemUtils.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", List.of()));
        }
        if (page > 0) {
            set(navStart, ItemUtils.createItem(Material.ARROW, "&bPrevious page", List.of()));
        }
        set(navStart + 4, ItemUtils.createItem(Material.BARRIER, "&cBack", List.of()));
        if ((page + 1) * pageSize() < total) {
            set(size - 1, ItemUtils.createItem(Material.ARROW, "&bNext page", List.of()));
        }
    }

    private static String title(UltimateDonutSmp2 plugin, int page) {
        int pageSize = Math.max(1, getMenuSize(plugin) - 9);
        int total = Math.max(1, plugin.getHideManager().getStates().size());
        int pages = Math.max(1, (int) Math.ceil(total / (double) pageSize));
        return plugin.getConfigManager().getHide()
                .getString("GUI.LIST.TITLE", "&8Hidden players - {page}/{pages}")
                .replace("{page}", String.valueOf(Math.min(page + 1, pages)))
                .replace("{pages}", String.valueOf(pages));
    }
}
