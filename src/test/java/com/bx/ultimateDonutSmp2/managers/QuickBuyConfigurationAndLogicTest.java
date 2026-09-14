package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.models.QuickBuyEntry;
import com.bx.ultimateDonutSmp2.models.ShopPreference;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickBuyConfigurationAndLogicTest {

    private static final List<String> LOCALES = List.of(
            "en_US", "id_ID", "de_DE", "es_ES", "fr_FR", "pt_BR", "ru_RU", "zh_CN"
    );

    private static final List<String> REQUIRED_QUICK_BUY_KEYS = List.of(
            "QUICK_BUY.TITLE",
            "QUICK_BUY.EMPTY_SLOT_NAME",
            "QUICK_BUY.EMPTY_SLOT_LORE",
            "QUICK_BUY.NOT_ENOUGH_MONEY",
            "QUICK_BUY.NOT_AVAILABLE",
            "QUICK_BUY.INVENTORY_FULL",
            "QUICK_BUY.BOUGHT_SUCCESS",
            "QUICK_BUY.SAVED",
            "QUICK_BUY.INSERT_ITEM_TITLE",
            "QUICK_BUY.NO_ITEM_INSERTED",
            "QUICK_BUY.CONFIRM_LISTING_TITLE",
            "QUICK_BUY.LISTED_SUCCESS",
            "QUICK_BUY.TRANSACTIONS_TITLE",
            "QUICK_BUY.YOUR_ITEMS_TITLE"
    );

    @Test
    void shopConfigHasValidQuickBuySection() throws Exception {
        File file = new File("src/main/resources/shop.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        assertTrue(config.getBoolean("QUICK-BUY.ENABLED"), "Quick Buy should be enabled by default");

        // Verify that shop.yml has DEFAULT-ENTRIES documented in comments with example entries
        String content = java.nio.file.Files.readString(file.toPath());
        assertTrue(content.contains("DEFAULT-ENTRIES:"), "shop.yml should contain commented DEFAULT-ENTRIES key");
        assertTrue(content.contains("TOTEM_OF_UNDYING"), "shop.yml should contain commented example entry");

        // Verify empty default entries in active config (empty grid by default per user requirement)
        ConfigurationSection entries = config.getConfigurationSection("QUICK-BUY.DEFAULT-ENTRIES");
        assertTrue(entries == null || entries.getKeys(false).isEmpty(), "Default entries should be empty by default (commented out)");

        // Verify parsing logic works correctly when an example config section is present
        YamlConfiguration sampleConfig = new YamlConfiguration();
        sampleConfig.loadFromString("""
                QUICK-BUY:
                  DEFAULT-ENTRIES:
                    0:
                      MATERIAL: "TOTEM_OF_UNDYING"
                      AMOUNT: 1
                    1:
                      MATERIAL: "GOLDEN_APPLE"
                      AMOUNT: 64
                """);
        ConfigurationSection sampleEntries = sampleConfig.getConfigurationSection("QUICK-BUY.DEFAULT-ENTRIES");
        assertNotNull(sampleEntries);
        assertEquals(2, sampleEntries.getKeys(false).size());
        for (String slotKey : sampleEntries.getKeys(false)) {
            int slot = Integer.parseInt(slotKey);
            assertTrue(slot >= 0 && slot < 45, "Slot must be within quick buy grid: " + slot);

            String matStr = sampleEntries.getString(slotKey + ".MATERIAL");
            assertNotNull(matStr, "Material must not be null for slot " + slot);
            Material mat = Material.getMaterial(matStr.toUpperCase(Locale.ROOT));
            assertNotNull(mat, "Invalid material configured: " + matStr);
            assertFalse(mat == Material.AIR, "Material must not be air for slot " + slot);

            int amount = sampleEntries.getInt(slotKey + ".AMOUNT");
            assertTrue(amount > 0 && amount <= 64, "Amount must be between 1 and 64 for slot " + slot);
        }
    }

    @Test
    void quickBuyHasFullEightLanguageParity() {
        for (String locale : LOCALES) {
            File langFile = new File("src/main/resources/languages/" + locale + ".yml");
            assertTrue(langFile.exists(), "Language file must exist: " + locale);

            YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
            for (String key : REQUIRED_QUICK_BUY_KEYS) {
                assertTrue(
                        langConfig.contains(key),
                        "Missing key '" + key + "' in locale " + locale
                );
                String val = langConfig.getString(key);
                assertNotNull(val, "Value for '" + key + "' must not be null in " + locale);
                assertFalse(val.isBlank(), "Value for '" + key + "' must not be blank in " + locale);
            }
        }
    }

    @Test
    void quickBuyEntryRecordBehavior() {
        QuickBuyEntry entry = new QuickBuyEntry(5, Material.GOLDEN_APPLE, 32, null, true);
        assertEquals(5, entry.slot());
        assertEquals(Material.GOLDEN_APPLE, entry.material());
        assertEquals(32, entry.buyAmount());
        assertTrue(entry.active());
        assertFalse(entry.isEmpty());

        QuickBuyEntry empty = QuickBuyEntry.empty(5);
        assertEquals(5, empty.slot());
        assertEquals(Material.AIR, empty.material());
        assertTrue(empty.isEmpty());
    }

    @Test
    void shopPreferenceQuickBuyStateTransitions() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        ShopPreference initial = new ShopPreference(uuid, Set.of());
        assertTrue(initial.quickBuyEntries().isEmpty());

        QuickBuyEntry entry = new QuickBuyEntry(0, Material.TOTEM_OF_UNDYING, 1, null, true);
        ShopPreference updated = initial.withQuickBuy(0, entry);
        assertEquals(1, updated.quickBuyEntries().size());
        assertEquals(Material.TOTEM_OF_UNDYING, updated.quickBuyEntries().get(0).material());

        ShopPreference removed = updated.withoutQuickBuy(0);
        assertTrue(removed.quickBuyEntries().isEmpty());
    }

    @Test
    void shopConfigHasAllNewQuickBuySectionsAndNoOldKeys() {
        File file = new File("src/main/resources/shop.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Old shop keys must be removed
        List<String> oldKeys = List.of(
                "CATEGORIES", "END-MENU", "NETHER-MENU", "GEAR-MENU", "FOOD-MENU",
                "EXTRA-1-MENU", "EXTRA-2-MENU", "EXTRA-3-MENU", "BACK-BUTTON"
        );
        for (String oldKey : oldKeys) {
            assertFalse(config.contains(oldKey), "Old shop key should be deleted: " + oldKey);
        }

        // SHOP-GUI must be preserved
        assertTrue(config.contains("SHOP-GUI"), "SHOP-GUI must be preserved");

        // QUICK-BUY must be present with customization keys
        assertTrue(config.contains("QUICK-BUY.TITLE"));
        assertTrue(config.contains("QUICK-BUY.EMPTY-SLOT.MATERIAL"));
        assertTrue(config.contains("QUICK-BUY.ITEM.PRICE-FORMAT"));
        assertTrue(config.contains("QUICK-BUY.BUTTONS.FILTER.SLOT"));
        assertTrue(config.contains("QUICK-BUY.BUTTONS.REFRESH.SLOT"));
        assertTrue(config.contains("QUICK-BUY.BUTTONS.AUCTION.SLOT"));
        assertTrue(config.contains("QUICK-BUY.BUTTONS.SEARCH.SLOT"));
        assertTrue(config.contains("QUICK-BUY.BUTTONS.YOUR-ITEMS.SLOT"));
        assertTrue(config.contains("QUICK-BUY.BUTTONS.EDIT.SLOT"));

        // YOUR-ITEMS must be present
        assertTrue(config.contains("YOUR-ITEMS.TITLE"));
        assertTrue(config.contains("YOUR-ITEMS.SELL-BUTTON.SLOT"));
        assertTrue(config.contains("YOUR-ITEMS.LOCKED-SLOT.MATERIAL"));
        assertTrue(config.contains("YOUR-ITEMS.LOCKED-PLUS-SLOT.MATERIAL"));
        assertTrue(config.contains("YOUR-ITEMS.ACTIVE-ITEM.PRICE-FORMAT"));
        assertTrue(config.contains("YOUR-ITEMS.BUTTONS.FILTER.SLOT"));
        assertTrue(config.contains("YOUR-ITEMS.BUTTONS.NEXT.SLOT"));
        assertTrue(config.contains("YOUR-ITEMS.UNLOCKED-SLOTS"));
        assertTrue(config.contains("YOUR-ITEMS.DONUT-PLUS-SLOTS"));

        // INSERT-ITEM must be present
        assertTrue(config.contains("INSERT-ITEM.TITLE"));
        assertTrue(config.contains("INSERT-ITEM.CANCEL-BUTTON.SLOT"));
        assertTrue(config.contains("INSERT-ITEM.CONFIRM-BUTTON.SLOT"));

        // CONFIRM-LISTING must be present
        assertTrue(config.contains("CONFIRM-LISTING.TITLE"));
        assertTrue(config.contains("CONFIRM-LISTING.CANCEL-BUTTON.SLOT"));
        assertTrue(config.contains("CONFIRM-LISTING.CONFIRM-BUTTON.SLOT"));
        assertTrue(config.contains("CONFIRM-LISTING.PREVIEW-ITEM.NOTICE-LINE-1"));

        // TRANSACTIONS must be present
        assertTrue(config.contains("TRANSACTIONS.TITLE"));
        assertTrue(config.contains("TRANSACTIONS.END-OF-LIST.MATERIAL"));
        assertTrue(config.contains("TRANSACTIONS.BUTTONS.STATS.SLOT"));
        assertTrue(config.contains("TRANSACTIONS.BUTTONS.REFRESH.SLOT"));
        assertTrue(config.contains("TRANSACTIONS.BUTTONS.SEARCH.SLOT"));

        // QUICK-BUY.CHOOSE-ITEM must be present with default MODE: VANILLA
        assertTrue(config.contains("QUICK-BUY.CHOOSE-ITEM.MODE"));
        assertEquals("VANILLA", config.getString("QUICK-BUY.CHOOSE-ITEM.MODE"));
        assertTrue(config.contains("QUICK-BUY.CHOOSE-ITEM.MAX-ITEMS"));
        assertTrue(config.getInt("QUICK-BUY.CHOOSE-ITEM.MAX-ITEMS") > 0);
        assertTrue(config.contains("QUICK-BUY.CHOOSE-ITEM.BLACKLIST"));
        List<String> blacklist = config.getStringList("QUICK-BUY.CHOOSE-ITEM.BLACKLIST");
        assertTrue(blacklist.contains("COMMAND_BLOCK"), "Choose Item blacklist must include command blocks");
        assertTrue(blacklist.contains("BEDROCK"), "Choose Item blacklist must include bedrock");
        assertTrue(blacklist.contains("BARRIER"), "Choose Item blacklist must include barriers");
        for (String builtIn : com.bx.ultimateDonutSmp2.dialogs.screens.ChooseItemBlacklist.BUILT_IN_NAMES) {
            assertTrue(
                    blacklist.contains(builtIn),
                    "Choose Item blacklist must ship built-in survival-unsuitable item: " + builtIn
            );
        }
        assertTrue(blacklist.contains("*_SPAWN_EGG"));
        assertTrue(blacklist.contains("INFESTED_*"));
    }

    @Test
    void spritePathResolvesCorrectlyForItemsAndBlocks() {
        // Items map to item/<name>
        assertEquals("item/apple", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.APPLE));
        assertEquals("item/diamond_sword", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.DIAMOND_SWORD));
        assertEquals("item/arrow", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ARROW));
        assertEquals("item/acacia_boat", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ACACIA_BOAT));

        // Blocks map to block/<name>
        assertEquals("block/stone", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.STONE));
        assertEquals("block/acacia_planks", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ACACIA_PLANKS));
        assertEquals("block/acacia_leaves", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ACACIA_LEAVES));
        assertEquals("block/beacon", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.BEACON));

        // Slabs, stairs, fences, buttons resolve to base plank/stone texture
        assertEquals("block/acacia_planks", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ACACIA_BUTTON));
        assertEquals("block/acacia_planks", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ACACIA_STAIRS));
        assertEquals("block/acacia_planks", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ACACIA_SLAB));
        assertEquals("block/acacia_planks", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ACACIA_FENCE));
        assertEquals("block/andesite", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ANDESITE_STAIRS));
        assertEquals("block/andesite", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ANDESITE_SLAB));
        assertEquals("block/andesite", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ANDESITE_WALL));

        // Wood blocks resolve to log
        assertEquals("block/acacia_log", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ACACIA_WOOD));
        assertEquals("block/oak_log", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.OAK_WOOD));

        // Block items with item textures
        assertEquals("item/acacia_door", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ACACIA_DOOR));
        assertEquals("item/acacia_sign", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ACACIA_SIGN));
        assertEquals("item/bell", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.BELL));
        assertEquals("item/redstone", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.REDSTONE));

        // Multi-face blocks
        assertEquals("block/ancient_debris_side", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.ANCIENT_DEBRIS));
        assertEquals("block/barrel_side", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.BARREL));

        // Heads and skulls are drawn as a head wearing the mob's own texture, not an atlas sprite
        assertEquals("entity/creeper/creeper", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getHeadTexture(Material.CREEPER_HEAD));
        assertEquals("entity/zombie/zombie", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getHeadTexture(Material.ZOMBIE_HEAD));
        assertEquals("entity/piglin/piglin", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getHeadTexture(Material.PIGLIN_HEAD));
        assertEquals("entity/skeleton/skeleton", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getHeadTexture(Material.SKELETON_SKULL));
        assertEquals("entity/skeleton/wither_skeleton", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getHeadTexture(Material.WITHER_SKELETON_SKULL));
        assertEquals("entity/player/wide/steve", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getHeadTexture(Material.PLAYER_HEAD));
        assertEquals("<head:entity/creeper/creeper>", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpriteTag(Material.CREEPER_HEAD));

        // The dragon head keeps an atlas sprite: its model does not lay a face out like a skin does
        assertEquals("", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getHeadTexture(Material.DRAGON_HEAD));
        assertEquals("block/dragon_egg", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.DRAGON_HEAD));
        assertEquals("item/iron_ingot", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpritePath(Material.SHIELD));

        // Ordinary items are unaffected and stay on the item atlas
        assertEquals("", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getHeadTexture(Material.APPLE));

        // Sprite tag format
        assertEquals("<item:item/apple>", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpriteTag(Material.APPLE));
        assertEquals("<item:block/acacia_planks>", com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSpriteTag(Material.ACACIA_PLANKS));
    }

    @Test
    void getSelectableMaterialsFiltersAndSortsCorrectly() {
        List<Material> allMaterials = com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSelectableMaterials(null, "");
        assertFalse(allMaterials.isEmpty(), "Selectable materials should not be empty in default mode");
        assertTrue(allMaterials.contains(Material.APPLE));

        List<Material> apples = com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSelectableMaterials(null, "apple");
        assertFalse(apples.isEmpty());
        assertTrue(apples.contains(Material.APPLE));
        assertTrue(apples.contains(Material.GOLDEN_APPLE));

        for (Material m : apples) {
            assertTrue(m.name().toLowerCase(Locale.ROOT).contains("apple"));
        }
    }

    @Test
    void chooseItemBlacklistHidesSurvivalUnsuitableItems() {
        List<Material> allMaterials = com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSelectableMaterials(null, "");
        assertTrue(allMaterials.contains(Material.APPLE));
        assertTrue(allMaterials.contains(Material.STONE));
        assertFalse(allMaterials.contains(Material.COMMAND_BLOCK));
        assertFalse(allMaterials.contains(Material.CHAIN_COMMAND_BLOCK));
        assertFalse(allMaterials.contains(Material.REPEATING_COMMAND_BLOCK));
        assertFalse(allMaterials.contains(Material.COMMAND_BLOCK_MINECART));
        assertFalse(allMaterials.contains(Material.BEDROCK));
        assertFalse(allMaterials.contains(Material.BARRIER));
        assertFalse(allMaterials.contains(Material.STRUCTURE_BLOCK));
        assertFalse(allMaterials.contains(Material.STRUCTURE_VOID));
        assertFalse(allMaterials.contains(Material.JIGSAW));
        assertFalse(allMaterials.contains(Material.LIGHT));
        assertFalse(allMaterials.contains(Material.DEBUG_STICK));
        assertFalse(allMaterials.contains(Material.KNOWLEDGE_BOOK));
        assertFalse(allMaterials.contains(Material.END_PORTAL_FRAME));
        assertFalse(allMaterials.contains(Material.SPAWNER));
        assertFalse(allMaterials.contains(Material.REINFORCED_DEEPSLATE));
        assertFalse(allMaterials.contains(Material.BUDDING_AMETHYST));
        assertFalse(allMaterials.contains(Material.FARMLAND));
        assertFalse(allMaterials.contains(Material.DIRT_PATH));
        assertFalse(allMaterials.contains(Material.CHORUS_PLANT));
        assertFalse(allMaterials.contains(Material.PETRIFIED_OAK_SLAB));
        assertFalse(allMaterials.contains(Material.FROGSPAWN));
        assertFalse(allMaterials.contains(Material.SUSPICIOUS_SAND));
        assertFalse(allMaterials.contains(Material.SUSPICIOUS_GRAVEL));
        assertFalse(allMaterials.contains(Material.TRIAL_SPAWNER));
        assertFalse(allMaterials.contains(Material.VAULT));
        assertFalse(allMaterials.contains(Material.COW_SPAWN_EGG));
        assertFalse(allMaterials.contains(Material.INFESTED_STONE));

        for (Material material : allMaterials) {
            assertFalse(
                    com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.isBlacklisted(null, material),
                    "selectable list leaked blacklisted material: " + material
            );
        }
        for (Material material : Material.values()) {
            if (material.isLegacy() || material.name().startsWith("LEGACY_")) {
                continue;
            }
            if (com.bx.ultimateDonutSmp2.dialogs.screens.ChooseItemBlacklist.isBuiltInBlacklistedName(material.name())) {
                assertFalse(
                        allMaterials.contains(material),
                        "Choose Item still offers built-in blacklisted item: " + material
                );
            }
        }

        List<Material> commandSearch = com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSelectableMaterials(null, "command");
        assertFalse(commandSearch.contains(Material.COMMAND_BLOCK));
        assertFalse(commandSearch.contains(Material.CHAIN_COMMAND_BLOCK));
        assertFalse(commandSearch.contains(Material.REPEATING_COMMAND_BLOCK));
        assertFalse(commandSearch.contains(Material.COMMAND_BLOCK_MINECART));

        List<Material> bedrockSearch = com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.getSelectableMaterials(null, "bedrock");
        assertFalse(bedrockSearch.contains(Material.BEDROCK));

        List<String> leaked = new ArrayList<>();
        for (Material material : allMaterials) {
            String name = material.name();
            if (name.contains("COMMAND")
                    || name.contains("STRUCTURE")
                    || name.startsWith("TEST_")
                    || name.startsWith("INFESTED_")
                    || name.endsWith("_SPAWN_EGG")
                    || name.contains("SPAWNER")
                    || name.equals("BEDROCK")
                    || name.equals("BARRIER")
                    || name.equals("JIGSAW")
                    || name.equals("LIGHT")
                    || name.equals("DEBUG_STICK")
                    || name.equals("KNOWLEDGE_BOOK")
                    || name.equals("FARMLAND")
                    || name.equals("DIRT_PATH")
                    || name.equals("CHORUS_PLANT")
                    || name.equals("FROGSPAWN")
                    || name.equals("VAULT")
                    || name.equals("SUSPICIOUS_SAND")
                    || name.equals("SUSPICIOUS_GRAVEL")) {
                leaked.add(name);
            }
        }
        assertTrue(leaked.isEmpty(), "Choose Item still offers survival-unsuitable items: " + leaked);
    }

    @Test
    void chooseItemBlacklistMatchesBuiltInFamiliesAndConfigWildcards() {
        assertTrue(com.bx.ultimateDonutSmp2.dialogs.screens.ChooseItemBlacklist.isBlacklisted(Material.COMMAND_BLOCK, List.of()));
        assertTrue(com.bx.ultimateDonutSmp2.dialogs.screens.ChooseItemBlacklist.isBlacklisted(Material.BEDROCK, List.of()));
        assertTrue(com.bx.ultimateDonutSmp2.dialogs.screens.ChooseItemBlacklist.isBlacklisted(Material.INFESTED_STONE, List.of()));
        assertTrue(com.bx.ultimateDonutSmp2.dialogs.screens.ChooseItemBlacklist.isBlacklisted(Material.COW_SPAWN_EGG, List.of()));
        assertFalse(com.bx.ultimateDonutSmp2.dialogs.screens.ChooseItemBlacklist.isBlacklisted(Material.APPLE, List.of()));
        assertTrue(com.bx.ultimateDonutSmp2.dialogs.screens.ChooseItemBlacklist.isBlacklisted(Material.STONE, List.of("STONE")));
        assertTrue(com.bx.ultimateDonutSmp2.dialogs.screens.ChooseItemBlacklist.isBlacklisted(Material.OAK_LOG, List.of("*_LOG")));
        assertFalse(com.bx.ultimateDonutSmp2.dialogs.screens.ChooseItemBlacklist.isBlacklisted(Material.OAK_PLANKS, List.of("*_LOG")));
        assertFalse(com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.isBlacklisted(null, Material.APPLE));
        assertTrue(com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.isBlacklisted(null, Material.COMMAND_BLOCK));
    }

    @Test
    void chooseItemWorthTooltipPutsTheWorthLoreLineUnderTheItemName() {
        String tooltip = com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.formatChooseItemWorthTooltip(
                "Crafting Table",
                "&#00FC00$ &f55"
        );

        assertEquals("&fCrafting Table\n&7Worth: &#00FC00$ &f55", tooltip);
        assertEquals(
                "&7Worth: &#00FC00$ &f55",
                com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.formatChooseItemWorthTooltip(
                        "",
                        "&#00FC00$ &f55"
                )
        );
        assertTrue(tooltip.contains("Worth:"));
        assertFalse(tooltip.contains("~"));
    }

    @Test
    void chooseItemWorthTooltipIsOmittedWhenTheItemHasNoWorth() {
        assertEquals(
                null,
                com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.formatChooseItemWorthTooltip("Apple", null)
        );
        assertEquals(
                null,
                com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.formatChooseItemWorthTooltip("Apple", "  ")
        );
        assertEquals(
                null,
                com.bx.ultimateDonutSmp2.dialogs.screens.QuickBuyItemDialog.chooseItemWorthTooltip(
                        null, null, Material.APPLE, "Apple"
                )
        );
    }

    @Test
    void quickBuyAndOrdersChooseItemDialogsAttachWorthLoreToItemButtons() throws Exception {
        String quickBuy = java.nio.file.Files.readString(java.nio.file.Path.of(
                "src/main/java/com/bx/ultimateDonutSmp2/dialogs/screens/QuickBuyItemDialog.java"
        ));
        String orders = java.nio.file.Files.readString(java.nio.file.Path.of(
                "src/main/java/com/bx/ultimateDonutSmp2/dialogs/screens/OrdersDialog.java"
        ));
        assertTrue(quickBuy.contains("chooseItemWorthTooltip(plugin, player, mat, name)"));
        assertTrue(orders.contains("QuickBuyItemDialog.chooseItemWorthTooltip(plugin, player, mat, name)"));
    }

    @Test
    void amountDialogMatchesBuyItemDesignThree() throws Exception {
        String source = java.nio.file.Files.readString(java.nio.file.Path.of(
                "src/main/java/com/bx/ultimateDonutSmp2/dialogs/screens/QuickBuyItemDialog.java"
        ));
        String method = source.substring(
                source.indexOf("public boolean openAmount"),
                source.indexOf("public boolean openAuctionSearch")
        );
        assertTrue(source.contains("AMOUNT_COLUMNS = 2"), "Design 3.png places Cancel and Add to Quick Buy on one row");
        assertTrue(source.contains("AMOUNT_BUTTON_WIDTH = 150"), "Design 3.png uses vanilla 150-wide dialog buttons");
        assertTrue(source.contains("AMOUNT_INPUT_WIDTH = 200"));
        assertTrue(method.contains(".columns(AMOUNT_COLUMNS)"));
        assertTrue(method.contains("How many to buy?"));
        assertTrue(method.contains("&fMax per purchase:"), "Max per purchase is white, not gray");
        assertFalse(method.contains("&7Max per purchase"));
        assertTrue(method.contains("\"Amount\""));
        int cancel = method.indexOf("&cCancel");
        int add = method.indexOf("&fAdd to Quick Buy");
        assertTrue(cancel >= 0 && add > cancel, "Cancel is the left button; Add to Quick Buy is the right");
        assertTrue(method.contains("AMOUNT_BUTTON_WIDTH"));
        assertTrue(method.contains("AMOUNT_INPUT_WIDTH"));
    }
}
