package com.bx.ultimateDonutSmp2.menus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurchaseShopMenuLoreTest {

    @Test
    void shardBuyPriceDoesNotAppendXShards() {
        String collapsed = PurchaseShopMenu.collapseRedundantShardSuffix("&fBUY PRICE: &5${price}X &lShards");
        assertEquals("&fBUY PRICE: &5{price_formatted}", collapsed);

        String resolved = PurchaseShopMenu.replacePricePlaceholders(
                collapsed,
                "1.5K",
                "★ 1.5K Shards"
        );
        assertEquals("&fBUY PRICE: &5★ 1.5K Shards", resolved);
        assertFalse(resolved.contains("ShardsX"));
        assertFalse(resolved.contains("X Shards"));
    }

    @Test
    void confirmationKeepsShardShopPickaxeLoreOnce() throws Exception {
        YamlConfiguration shop = new YamlConfiguration();
        shop.load(Path.of("src/main/resources/shop.yml").toFile());
        List<String> shopLore = shop.getStringList("SHARD-MENU.SHARD-PICKAXE-SILK-ITEM.LORE");

        List<String> lore = PurchaseShopMenu.previewLore(
                shopLore,
                "&fBUY PRICE: &#A303F9★ 1.5K Shards",
                false,
                1,
                1,
                1,
                "Shards"
        );

        assertEquals(shopLore, lore);
        assertEquals(1, lore.stream().filter(line -> line.contains("Silk Touch")).count());
        assertEquals(1, lore.stream().filter(line -> line.contains("Efficiency V")).count());
        assertFalse(lore.stream().anyMatch(line -> line.contains("BUY PRICE")));
        assertFalse(lore.stream().anyMatch(line -> line.contains("Quantity:")));
        assertFalse(lore.stream().anyMatch(line -> line.contains("ShardsX")));
    }

    @Test
    void confirmationKeepsHastePotionShopLore() throws Exception {
        YamlConfiguration shop = new YamlConfiguration();
        shop.load(Path.of("src/main/resources/shop.yml").toFile());
        List<String> shopLore = shop.getStringList("SHARD-MENU.SHARD-HASTE-POTION-ITEM.LORE");

        List<String> lore = PurchaseShopMenu.previewLore(
                shopLore,
                "&fBUY PRICE: &#A303F9★ 250 Shards",
                false,
                1,
                1,
                1,
                "Shards"
        );

        assertEquals(List.of("&cSelf Destructs in 24h", "&#A303F9250 Shards"), lore);
        assertFalse(lore.stream().anyMatch(line -> line.contains("BUY PRICE")));
        assertFalse(lore.stream().anyMatch(line -> line.contains("No Effects")));
    }

    @Test
    void adjustableMoneyItemsStillGetBuyPriceAndQuantity() {
        List<String> lore = PurchaseShopMenu.previewLore(
                List.of("&7A useful item"),
                "&fBUY PRICE: &a$10",
                true,
                2,
                1,
                64,
                "dollars"
        );
        assertEquals(List.of(
                "&7A useful item",
                "",
                "&fBUY PRICE: &a$10",
                "&7Quantity: &f2",
                "&7Allowed: &f1&7 - &f64",
                "&7Currency: &fdollars"
        ), lore);
    }

    @Test
    void bundledShardPriceLineUsesFormattedPlaceholder() throws Exception {
        YamlConfiguration menus = new YamlConfiguration();
        menus.load(Path.of("src/main/resources/menus.yml").toFile());
        String shardLine = menus.getString("PURCHASE-SHOP-MENU.BUTTONS.MAIN.LORE.SHARD");
        assertTrue(shardLine.contains("{price_formatted}"), shardLine);
        assertFalse(shardLine.contains("${price}X"), shardLine);
    }

    @Test
    void successfulPurchaseReturnsToShardShopNotConfirmation() {
        assertEquals("SHARD-MENU", PurchaseShopMenu.originAfterPurchase("SHARD-MENU", false));
        assertEquals("SHARD-MENU", PurchaseShopMenu.originAfterPurchase("shard-menu", false));
        assertEquals("FAVORITES", PurchaseShopMenu.originAfterPurchase("SHARD-MENU", true));
        assertEquals("MONEY-MENU", PurchaseShopMenu.originAfterPurchase("MONEY-MENU", false));
        assertFalse("PURCHASE-SHOP-MENU".equalsIgnoreCase(
                PurchaseShopMenu.originAfterPurchase("SHARD-MENU", false)
        ));
    }
}
