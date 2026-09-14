package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.ShopManager;
import com.bx.ultimateDonutSmp2.models.PlayerData;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import com.bx.ultimateDonutSmp2.utils.ItemUtils;
import com.bx.ultimateDonutSmp2.utils.NumberUtils;
import com.bx.ultimateDonutSmp2.utils.SoundUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dedicated /shardshop chest matching {@code Design/Shards Shop}.
 * Empty slots stay empty; the bottom row only shows shard balance and Ranks.
 */
public final class ShardShopMenu extends BaseMenu {

    public static final String MENU_SECTION = "SHARD-MENU";

    private final Map<Integer, ShopManager.ShopItem> slotItems = new HashMap<>();
    private int balanceSlot = 49;
    private int ranksSlot = 50;

    public ShardShopMenu(UltimateDonutSmp2 plugin) {
        super(
                plugin,
                plugin.getConfigManager().getShop().getString(MENU_SECTION + ".TITLE", "&8Shard Shop"),
                clampSize(plugin.getConfigManager().getShop().getInt(MENU_SECTION + ".SIZE", 54))
        );
    }

    public static void openShop(UltimateDonutSmp2 plugin, Player player) {
        new ShardShopMenu(plugin).open(player);
    }

    @Override
    public void build(Player player) {
        clear();
        slotItems.clear();

        ConfigurationSection menu = plugin.getConfigManager().getShop().getConfigurationSection(MENU_SECTION);
        ConfigurationSection buttons = menu == null ? null : menu.getConfigurationSection("BUTTONS");
        balanceSlot = slot(buttons, "BALANCE", 49);
        ranksSlot = slot(buttons, "RANKS", 50);

        for (ShopManager.ShopItem item : plugin.getShopManager().loadMenuItems(MENU_SECTION)) {
            if (item.slot() < 0 || item.slot() >= inventory.getSize()) {
                continue;
            }
            set(item.slot(), createDisplay(item));
            slotItems.put(item.slot(), item);
        }

        set(balanceSlot, createBalanceButton(player, buttons));
        set(ranksSlot, createRanksButton(buttons));
    }

    @Override
    public void handleClick(int slot, Player player, ClickType clickType) {
        if (slot == ranksSlot) {
            click(player);
            openRanks(player);
            return;
        }
        if (slot == balanceSlot) {
            click(player);
            return;
        }

        ShopManager.ShopItem item = slotItems.get(slot);
        if (item == null) {
            return;
        }

        click(player);
        new PurchaseShopMenu(plugin, item, MENU_SECTION, 0).open(player);
    }

    private ItemStack createDisplay(ShopManager.ShopItem item) {
        ConfigurationSection section = itemSection(item.key());
        boolean vanillaName = section != null && section.getBoolean("VANILLA-NAME", false);
        boolean hideEnchants = section != null && section.getBoolean("HIDE-ENCHANTS", false);
        boolean potion = item.material() == Material.POTION
                || item.material() == Material.SPLASH_POTION
                || item.material() == Material.LINGERING_POTION;

        ItemStack stack = plugin.getShopManager().createCustomItem(item);
        if (stack == null) {
            stack = new ItemStack(item.material());
            if (item.enchantments() != null && !item.enchantments().isEmpty()) {
                ItemUtils.addEnchantments(stack, item.enchantments());
            }
            if (item.glint() != null) {
                ItemUtils.setGlint(stack, item.glint());
            }
        }
        ItemUtils.applyConfiguredPotion(stack, section);

        List<String> lore = item.lore() == null ? List.of() : item.lore();
        if (vanillaName) {
            stack = ItemUtils.withLore(stack, lore);
        } else {
            stack = ItemUtils.withDisplay(stack, item.displayName(), lore);
        }

        ItemUtils.hideMenuNoise(stack, !potion);
        if (hideEnchants) {
            ItemUtils.hideEnchantments(stack);
        }
        return stack;
    }

    private ItemStack createBalanceButton(Player player, ConfigurationSection buttons) {
        ConfigurationSection section = buttons == null ? null : buttons.getConfigurationSection("BALANCE");
        Material material = ItemUtils.parseMaterial(
                section == null ? "AMETHYST_SHARD" : section.getString("MATERIAL", "AMETHYST_SHARD")
        );
        String name = section == null ? "&fShard Shop" : section.getString("NAME", "&fShard Shop");
        List<String> lore = section == null
                ? List.of("&fYou have &#A303F9{shards} Shards", "", "&fVisit our website to purchase shards")
                : section.getStringList("LORE");

        PlayerData data = plugin.getPlayerDataManager() == null ? null : plugin.getPlayerDataManager().get(player);
        long shards = data == null ? 0L : data.getShards();
        String amount = NumberUtils.formatNice(shards);
        lore = lore.stream().map(line -> line.replace("{shards}", amount)).toList();

        ItemStack item = ItemUtils.createItem(material, name, lore);
        ItemUtils.hideMenuNoise(item, true);
        return item;
    }

    private ItemStack createRanksButton(ConfigurationSection buttons) {
        ConfigurationSection section = buttons == null ? null : buttons.getConfigurationSection("RANKS");
        String name = section == null ? "&fRanks" : section.getString("NAME", "&fRanks");
        List<String> lore = section == null
                ? List.of("&fClick to view")
                : section.getStringList("LORE");
        Material material = ItemUtils.parseMaterial(
                section == null ? "PLAYER_HEAD" : section.getString("MATERIAL", "PLAYER_HEAD")
        );
        String texture = section == null ? "" : section.getString("HEAD-TEXTURE", "");

        ItemStack item;
        if (material == Material.PLAYER_HEAD && texture != null && !texture.isBlank()) {
            item = ItemUtils.createHeadFromSkinUrl(texture, name, lore);
        } else {
            // An unowned player head is Minecraft's default Steve, matching Design/Shards Shop.
            item = ItemUtils.createItem(material, name, lore);
        }
        ItemUtils.hideMenuNoise(item, true);
        return item;
    }

    private void openRanks(Player player) {
        if (!plugin.getConfigManager().isCommandEnabled("RANKS")) {
            player.sendMessage(ColorUtils.toComponent("&cRanks command is currently disabled."));
            return;
        }
        RanksMenu menu = new RanksMenu(plugin);
        if (!menu.hasValidButtons()) {
            player.sendMessage(ColorUtils.toComponent("&cThe ranks menu has no usable buttons configured."));
            return;
        }
        menu.open(player);
    }

    private ConfigurationSection itemSection(String key) {
        return plugin.getConfigManager().getShop().getConfigurationSection(MENU_SECTION + "." + key);
    }

    private static int slot(ConfigurationSection buttons, String key, int fallback) {
        if (buttons == null) {
            return fallback;
        }
        return buttons.getInt(key + ".SLOT", fallback);
    }

    private static int clampSize(int configuredSize) {
        if (configuredSize < 9) {
            return 54;
        }
        if (configuredSize > 54) {
            return 54;
        }
        return configuredSize - (configuredSize % 9);
    }

    private void click(Player player) {
        SoundUtils.play(player, plugin.getConfigManager().getSound("MENUS.BUTTON-CLICK"));
    }

    @Override
    public boolean isSameScreen(BaseMenu other) {
        return other instanceof ShardShopMenu;
    }
}
