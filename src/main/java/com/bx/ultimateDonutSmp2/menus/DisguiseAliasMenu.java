package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.HideManager;
import com.bx.ultimateDonutSmp2.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisguiseAliasMenu extends BaseMenu {

    private static final int DEFAULT_SIZE = 54;

    private final int page;
    private final Map<Integer, String> aliasesBySlot = new HashMap<>();

    public DisguiseAliasMenu(UltimateDonutSmp2 plugin, int page) {
        super(plugin, title(plugin, page), getMenuSize(plugin));
        this.page = Math.max(0, page);
    }

    public static int getMenuSize(UltimateDonutSmp2 plugin) {
        if (plugin == null || plugin.getConfigManager() == null || plugin.getConfigManager().getHide() == null) {
            return DEFAULT_SIZE;
        }
        int configured = plugin.getConfigManager().getHide().getInt("GUI.ALIASES.SIZE", DEFAULT_SIZE);
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
        aliasesBySlot.clear();
        List<HideManager.AliasOption> aliases = new ArrayList<>(plugin.getHideManager().aliases().values());
        int pageSize = pageSize();
        int start = page * pageSize;
        int end = Math.min(aliases.size(), start + pageSize);
        for (int index = start; index < end; index++) {
            HideManager.AliasOption option = aliases.get(index);
            int slot = index - start;
            HideManager.HeadTexture texture = plugin.getHideManager().cachedHeadTexture(option.skinUsername());
            set(slot, createAliasHead(option, texture));
            aliasesBySlot.put(slot, option.key());
            if (texture == null) {
                refreshHeadAsync(player, slot, option);
            }
        }
        renderNavigation(aliases.size());
    }

    private ItemStack createAliasHead(HideManager.AliasOption option, HideManager.HeadTexture texture) {
        return ItemUtils.createPlayerHead(
                Bukkit.getOfflinePlayer(option.skinUsername()),
                texture == null ? null : texture.value(),
                "&e" + option.name(),
                List.of(
                        "&7Key: &f" + option.key(),
                        "&7Preview skin: &f" + option.skinUsername(),
                        "",
                        "&eClick to select this name."
                )
        );
    }

    private void refreshHeadAsync(Player player, int slot, HideManager.AliasOption option) {
        plugin.getHideManager().resolveHeadTextureAsync(option.skinUsername()).thenAccept(texture -> {
            if (texture == null || !texture.isValid()) {
                return;
            }
            plugin.getSpigotScheduler().runEntity(player, () -> {
                if (!player.isOnline()
                        || player.getOpenInventory().getTopInventory().getHolder() != this
                        || !option.key().equals(aliasesBySlot.get(slot))) {
                    return;
                }
                set(slot, createAliasHead(option, texture));
            });
        });
    }

    @Override
    public void handleClick(int slot, Player player, ClickType clickType) {
        String aliasKey = aliasesBySlot.get(slot);
        if (aliasKey != null) {
            new DisguiseSkinMenu(plugin, aliasKey, 0).open(player);
            return;
        }
        int size = inventory.getSize();
        int navStart = size - 9;
        if (slot == navStart && page > 0) {
            playPageTurn(player);
            new DisguiseAliasMenu(plugin, page - 1).open(player);
        } else if (slot == (size - 1) && (page + 1) * pageSize() < plugin.getHideManager().aliases().size()) {
            playPageTurn(player);
            new DisguiseAliasMenu(plugin, page + 1).open(player);
        } else if (slot == (navStart + 4)) {
            new HideMenu(plugin).open(player);
        }
    }

    private void renderNavigation(int total) {
        int size = inventory.getSize();
        int navStart = size - 9;
        fillNavigation(navStart);
        if (page > 0) {
            set(navStart, ItemUtils.createItem(Material.ARROW, "&bPrevious page", List.of()));
        }
        set(navStart + 4, ItemUtils.createItem(Material.BARRIER, "&cBack", List.of()));
        if ((page + 1) * pageSize() < total) {
            set(size - 1, ItemUtils.createItem(Material.ARROW, "&bNext page", List.of()));
        }
    }

    private void fillNavigation(int navStart) {
        for (int slot = navStart; slot < inventory.getSize(); slot++) {
            set(slot, ItemUtils.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", List.of()));
        }
    }

    private static String title(UltimateDonutSmp2 plugin, int page) {
        int pageSize = Math.max(1, getMenuSize(plugin) - 9);
        int total = Math.max(1, plugin.getHideManager().aliases().size());
        int pages = Math.max(1, (int) Math.ceil(total / (double) pageSize));
        return plugin.getConfigManager().getHide()
                .getString("GUI.ALIASES.TITLE", "&8Select a name - {page}/{pages}")
                .replace("{page}", String.valueOf(Math.min(page + 1, pages)))
                .replace("{pages}", String.valueOf(pages));
    }
}
