package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.CurrencyManager;
import com.bx.ultimateDonutSmp2.managers.SkinsRestorerSkinLookup;
import com.bx.ultimateDonutSmp2.managers.TablistManager;
import com.bx.ultimateDonutSmp2.models.PlayerData;
import com.bx.ultimateDonutSmp2.utils.ItemUtils;
import com.bx.ultimateDonutSmp2.utils.NumberUtils;
import com.bx.ultimateDonutSmp2.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class StatsMenu extends BaseMenu {

    private static final String MENU_PATH = "STATS-MENU";
    /** The eight icons in Design/Dialog API/Stats/View Full Profile. Extra config keys are ignored. */
    private static final Set<String> REFERENCE_BUTTONS = Set.of(
            "MONEY", "SHARDS", "KILLS", "DEATHS", "PLAYTIME", "BLOCKS_PLACED", "BLOCKS_BROKEN", "MOBS_KILLED"
    );

    private final UUID targetUuid;
    private final String targetName;

    public StatsMenu(UltimateDonutSmp2 plugin, UUID targetUuid, String targetName) {
        super(
                plugin,
                configuredTitle(plugin, targetName),
                configuredSize(plugin)
        );
        this.targetUuid = targetUuid;
        this.targetName = targetName;
    }

    @Override
    public void build(Player player) {
        clear();

        PlayerData data = plugin.getPlayerDataManager().get(targetUuid);
        if (data == null) {
            data = plugin.getDatabaseManager().loadPlayer(targetUuid);
        }

        if (data == null) {
            set(inventory.getSize() / 2, ItemUtils.createItem(
                    Material.BARRIER,
                    "&cPlayer Not Found",
                    List.of("&7This player has no recorded stats.")
            ));
            placeCloseButton();
            return;
        }

        ConfigurationSection buttons = plugin.getConfigManager().getMenus().getConfigurationSection(MENU_PATH + ".BUTTONS");
        if (buttons == null) {
            placeCloseButton();
            return;
        }

        for (String key : buttons.getKeys(false)) {
            if (!REFERENCE_BUTTONS.contains(key.toUpperCase(Locale.ROOT))) {
                continue;
            }
            ConfigurationSection section = buttons.getConfigurationSection(key);
            if (section == null) {
                continue;
            }

            int slot = section.getInt("SLOT", -1);
            if (slot < 0 || slot >= inventory.getSize()) {
                continue;
            }

            Material material = ItemUtils.parseMaterial(section.getString("MATERIAL", "STONE"));
            String rawName = section.getString("DISPLAY-NAME", "&b" + key);
            String displayName = replacePlaceholders(rawName, data);

            String val = resolveStatValue(key, data);

            List<String> rawLore = section.getStringList("LORE");
            List<String> lore = new ArrayList<>();
            for (String line : rawLore) {
                lore.add(replacePlaceholders(line.replace("{value}", val), data));
            }

            ItemStack item = createItem(material, displayName, lore);
            set(slot, item);
        }

        placeCloseButton();
    }

    @Override
    public void handleClick(int slot, Player player) {
        if (slot < 0 || slot >= inventory.getSize()) {
            return;
        }
        SoundUtils.play(player, plugin.getConfigManager().getSound("MENUS.BUTTON-CLICK"));
        if (slot == closeSlot()) {
            close(player);
            plugin.getSpigotScheduler().runEntityLater(player, () -> {
                if (player.isOnline() && plugin.getDialogManager() != null) {
                    plugin.getDialogManager().openStats(player, targetName);
                }
            }, 1L);
        }
    }

    private void placeCloseButton() {
        ConfigurationSection section = plugin.getConfigManager().getMenus()
                .getConfigurationSection(MENU_PATH + ".CLOSE-BUTTON");
        int slot = closeSlot();
        if (slot < 0 || slot >= inventory.getSize()) {
            return;
        }
        Material material = ItemUtils.parseMaterial(
                section != null ? section.getString("MATERIAL", "RED_STAINED_GLASS_PANE") : "RED_STAINED_GLASS_PANE"
        );
        String displayName = section != null
                ? section.getString("DISPLAY-NAME", "&cClose")
                : "&cClose";
        List<String> lore = section != null
                ? ItemUtils.readLore(section, "LORE", List.of("&7Close this menu"))
                : List.of("&7Close this menu");
        set(slot, ItemUtils.createItem(material, displayName, lore));
    }

    private int closeSlot() {
        return plugin.getConfigManager().getMenus().getInt(MENU_PATH + ".CLOSE-BUTTON.SLOT", 27);
    }

    private ItemStack createItem(Material material, String displayName, List<String> lore) {
        if (material != Material.PLAYER_HEAD) {
            return ItemUtils.createItem(material, displayName, lore);
        }
        TablistManager.SkinTexture texture = resolveSkinTexture();
        return ItemUtils.createPlayerHead(
                Bukkit.getOfflinePlayer(targetUuid),
                texture != null ? texture.value() : null,
                displayName,
                lore
        );
    }

    private TablistManager.SkinTexture resolveSkinTexture() {
        TablistManager tablist = plugin.getTablistManager();
        if (tablist != null) {
            TablistManager.SkinTexture cached = tablist.cachedSkinsRestorerTexture(targetUuid);
            if (cached != null && cached.isValid()) {
                return cached;
            }
            cached = tablist.cachedSkinTexture(targetUuid);
            if (cached != null && cached.isValid()) {
                return cached;
            }
        }
        return SkinsRestorerSkinLookup.resolve(targetUuid, targetName);
    }

    private String resolveStatValue(String key, PlayerData data) {
        if (data == null) {
            return "0";
        }

        return switch (key.toUpperCase(Locale.ROOT)) {
            case "MONEY" -> plugin.getCurrencyManager().formatCompactAmount(CurrencyManager.CurrencyType.MONEY, data.getMoney());
            case "SHARDS" -> plugin.getCurrencyManager().formatCompactAmount(CurrencyManager.CurrencyType.SHARDS, data.getShards());
            case "KILLS" -> NumberUtils.format(data.getKills());
            case "DEATHS" -> NumberUtils.format(data.getDeaths());
            case "PLAYTIME" -> NumberUtils.formatTimeScoreboard(data.getTotalPlaytimeSeconds());
            case "BLOCKS_PLACED" -> NumberUtils.format(data.getBlocksPlaced());
            case "BLOCKS_BROKEN" -> NumberUtils.format(data.getBlocksBroken());
            case "MOBS_KILLED" -> NumberUtils.format(data.getMobsKilled());
            case "KILL_STREAK" -> NumberUtils.format(data.getKillStreak());
            case "HIGHEST_KILL_STREAK" -> NumberUtils.format(data.getHighestKillStreak());
            case "MONEY_SPENT" -> plugin.getCurrencyManager().formatCompactAmount(CurrencyManager.CurrencyType.MONEY, data.getMoneySpent());
            case "MONEY_MADE" -> plugin.getCurrencyManager().formatCompactAmount(CurrencyManager.CurrencyType.MONEY, data.getMoneyMade());
            case "TEAM" -> {
                var team = plugin.getTeamManager().getTeam(data.getUuid());
                yield team != null ? team.getName().toUpperCase() : "None";
            }
            default -> "0";
        };
    }

    private String replacePlaceholders(String text, PlayerData data) {
        if (text == null) {
            return "";
        }
        String name = targetName == null ? "" : targetName;
        String upperName = targetName == null ? "" : targetName.toUpperCase(Locale.ROOT);
        String result = plugin.getCurrencyManager().applyStaticPlaceholders(text)
                .replace("{username_upper}", upperName)
                .replace("{username}", name);

        if (data != null) {
            result = result
                    .replace("{kills}", NumberUtils.format(data.getKills()))
                    .replace("{deaths}", NumberUtils.format(data.getDeaths()))
                    .replace("{money}", plugin.getCurrencyManager().formatCompactAmount(CurrencyManager.CurrencyType.MONEY, data.getMoney()))
                    .replace("{shards}", plugin.getCurrencyManager().formatCompactAmount(CurrencyManager.CurrencyType.SHARDS, data.getShards()))
                    .replace("{playtime}", NumberUtils.formatTimeScoreboard(data.getTotalPlaytimeSeconds()))
                    .replace("{blocks_placed}", NumberUtils.format(data.getBlocksPlaced()))
                    .replace("{blocks_broken}", NumberUtils.format(data.getBlocksBroken()))
                    .replace("{mobs_killed}", NumberUtils.format(data.getMobsKilled()))
                    .replace("{kill_streak}", NumberUtils.format(data.getKillStreak()))
                    .replace("{highest_kill_streak}", NumberUtils.format(data.getHighestKillStreak()))
                    .replace("{money_spent}", plugin.getCurrencyManager().formatCompactAmount(CurrencyManager.CurrencyType.MONEY, data.getMoneySpent()))
                    .replace("{money_made}", plugin.getCurrencyManager().formatCompactAmount(CurrencyManager.CurrencyType.MONEY, data.getMoneyMade()));
        }
        return result;
    }

    static String configuredTitle(UltimateDonutSmp2 plugin, String targetName) {
        String template = plugin != null && plugin.getConfigManager() != null && plugin.getConfigManager().getMenus() != null
                ? plugin.getConfigManager().getMenus().getString(MENU_PATH + ".TITLE", "&8{username} Stats")
                : "&8{username} Stats";
        String name = targetName == null ? "" : targetName;
        String upper = targetName == null ? "" : targetName.toUpperCase(Locale.ROOT);
        return template
                .replace("{username_upper}", upper)
                .replace("{username}", name);
    }

    private static int configuredSize(UltimateDonutSmp2 plugin) {
        int size = plugin.getConfigManager().getMenus().getInt(MENU_PATH + ".SIZE", 36);
        return size >= 9 && size <= 54 && size % 9 == 0 ? size : 36;
    }
}
