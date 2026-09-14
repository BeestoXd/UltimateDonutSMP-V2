package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.CurrencyManager;
import com.bx.ultimateDonutSmp2.managers.SpawnerManager.ActionResult;
import com.bx.ultimateDonutSmp2.models.WorthResult;
import com.bx.ultimateDonutSmp2.models.SpawnerInstance;
import com.bx.ultimateDonutSmp2.models.SpawnerLootEntry;
import com.bx.ultimateDonutSmp2.models.SpawnerTypeDefinition;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import com.bx.ultimateDonutSmp2.utils.ItemUtils;
import com.bx.ultimateDonutSmp2.utils.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SpawnerStorageMenu extends BaseMenu {

    private final long spawnerId;
    private final int page;
    private long lastInteractionTime = 0L;

    public SpawnerStorageMenu(UltimateDonutSmp2 plugin, long spawnerId, int page) {
        super(plugin, " ", plugin.getSpawnerManager().getStorageSize());
        this.spawnerId = spawnerId;
        this.page = Math.max(1, page);
    }

    public long getSpawnerId() {
        return spawnerId;
    }

    public int getPage() {
        return page;
    }

    @Override
    public void open(Player player) {
        super.open(player);
        plugin.getSpawnerManager().registerOpenStorageMenu(player, this);
    }

    @Override
    public void onClose(Player player) {
        plugin.getSpawnerManager().unregisterOpenStorageMenu(player, this);
    }

    public void refresh(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        // Skip background auto-refresh if player clicked/interacted in the last 2 seconds
        if (System.currentTimeMillis() - lastInteractionTime < 2000L) {
            return;
        }

        SpawnerInstance instance = plugin.getSpawnerManager().getSpawner(spawnerId);
        if (instance == null) {
            return;
        }

        // compare the view by identity rather than through getHolder(): resolving the
        // holder of a block backed inventory reads the world, which Folia only allows
        // from the region owning that block
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        if (topInventory != getInventory()) {
            plugin.getSpawnerManager().unregisterOpenStorageMenu(player, this);
            return;
        }

        int itemsPerPage = plugin.getSpawnerManager().getStorageItemsPerPage();
        int contentSlots = Math.min(itemsPerPage, topInventory.getSize() - 9);
        int pageOffset = (page - 1) * itemsPerPage;

        for (int slot = 0; slot < contentSlots; slot++) {
            int slotIndex = pageOffset + slot;
            SpawnerLootEntry entry = instance.getSlotLoot(slotIndex);

            if (entry != null && entry.getAmount() > 0L) {
                ItemStack current = topInventory.getItem(slot);
                if (current == null || current.getType() != entry.getMaterial() || current.getAmount() != (int) entry.getAmount()) {
                    topInventory.setItem(slot, applyStorageMeta(plugin, instance, entry.getMaterial(), (int) entry.getAmount()));
                }
            } else {
                ItemStack current = topInventory.getItem(slot);
                if (current != null && !current.getType().isAir()) {
                    topInventory.setItem(slot, null);
                }
            }
        }

        renderControlButtons(instance);
        player.updateInventory();
    }

    public static ItemStack applyStorageMeta(UltimateDonutSmp2 plugin, SpawnerInstance instance, Material material, int amount) {
        ItemStack item = new ItemStack(material, Math.max(1, Math.min(amount, material.getMaxStackSize())));
        var meta = item.getItemMeta();
        if (meta != null) {
            FileConfiguration config = plugin.getConfigManager().getMenus();

            String defaultTitle = "&f" + prettifyMaterial(material);
            String titleFormat = config.getString("SPAWNER-MENUS.STORAGE-MENU.ITEM-META.TITLE", defaultTitle);
            meta.setDisplayName(ColorUtils.toComponent(titleFormat.replace("{material}", prettifyMaterial(material))));

            ItemStack dummy = new ItemStack(material, Math.max(1, amount));
            WorthResult worthResult = plugin.getWorthManager().resolveWorth(dummy);
            double displayWorth = worthResult.sellable() ? worthResult.totalWorth() : 0D;
            String formattedPrice = plugin.getCurrencyManager().formatCompactAmount(CurrencyManager.CurrencyType.MONEY, displayWorth);

            List<String> rawLore = config.getStringList("SPAWNER-MENUS.STORAGE-MENU.ITEM-META.LORE");
            List<String> lore = new ArrayList<>();
            if (rawLore.isEmpty()) {
                lore.add("&a$ &f" + formattedPrice);
            } else {
                for (String line : rawLore) {
                    lore.add(line.replace("{price}", formattedPrice)
                            .replace("{material}", prettifyMaterial(material)));
                }
            }
            meta.setLore(ColorUtils.toComponentList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createSpawnerMobHead(UltimateDonutSmp2 plugin, SpawnerInstance instance) {
        if (instance == null) {
            return new ItemStack(Material.SKELETON_SKULL);
        }

        SpawnerTypeDefinition def = plugin.getSpawnerManager().getTypeDefinition(instance.getMobTypeKey());

        FileConfiguration config = plugin.getConfigManager().getMenus();
        String titleTemplate = config != null
                ? config.getString("SPAWNER-MENUS.STORAGE-MENU.MOB-HEAD-BUTTON.TITLE", "&a{amount} {type} Spawner")
                : "&a{amount} {type} Spawner";
        String cleanMob = plugin.getSpawnerManager().prettifyKey(instance.getMobTypeKey());
        String headTitle = formatSpawnerMobHeadTitle(titleTemplate, instance.getStackAmount(), cleanMob);

        // Aggregate stored loot counts
        Map<Material, Long> totals = new LinkedHashMap<>();
        for (SpawnerLootEntry entry : instance.getStoredLootEntries()) {
            if (entry != null && entry.getAmount() > 0L) {
                totals.merge(entry.getMaterial(), entry.getAmount(), Long::sum);
            }
        }

        List<String> headLore = new ArrayList<>();
        // If drops defined in spawner type, show them in order
        if (def != null && def.drops() != null) {
            for (var dropDef : def.drops()) {
                Material mat = dropDef.material();
                long count = totals.getOrDefault(mat, 0L);
                if (count > 0L) {
                    headLore.add("&a" + formatDropCompactCount(count) + " &f" + pluralizeMaterial(mat));
                }
            }
        }
        for (Map.Entry<Material, Long> entry : totals.entrySet()) {
            Material mat = entry.getKey();
            if (def == null || def.drops() == null || def.drops().stream().noneMatch(d -> d.material() == mat)) {
                headLore.add("&a" + formatDropCompactCount(entry.getValue()) + " &f" + pluralizeMaterial(mat));
            }
        }

        long totalCapacity = plugin.getSpawnerManager().getTotalStorageCapacity(instance);
        long currentTotal = instance.getTotalStoredItems();
        double fillPercentage = calculateFillPercentage(currentTotal, totalCapacity);
        String pctStr = (fillPercentage <= 0.0) ? "0" : (fillPercentage < 1.0 ? String.format(Locale.US, "%.1f", fillPercentage) : String.valueOf(Math.round(fillPercentage)));
        headLore.add("&a(" + pctStr + "% filled)");

        String customTexture = def != null ? def.headTexture() : null;
        return ItemUtils.createMobHead(
                instance.getMobTypeKey(),
                customTexture,
                headTitle,
                headLore
        );
    }

    public static String formatSpawnerMobHeadTitle(String template, long stack, String cleanMob) {
        if (template == null || template.isBlank()) {
            template = "&a{amount} {type} Spawner";
        }
        String formattedStack = NumberUtils.format(stack);
        String result = template
                .replace("{amount}", formattedStack)
                .replace("{stack}", formattedStack)
                .replace("{type}", cleanMob)
                .replace("{mob}", cleanMob);

        if (stack == 1L) {
            result = result.replaceAll("(?i)\\bSpawners\\b", "Spawner");
        } else {
            if (result.endsWith("Spawner") && !result.endsWith("Spawners")) {
                result = result + "s";
            }
        }
        return result;
    }

    public static String formatDropCompactCount(long count) {
        if (count < 1_000L) {
            return String.valueOf(count);
        }
        if (count < 1_000_000L) {
            double value = count / 1_000.0;
            if (count % 1_000L == 0 || Math.round(value * 10) % 10 == 0) {
                return String.format(Locale.US, "%.0fk", value);
            }
            String formatted = String.format(Locale.US, "%.1fk", value);
            return formatted.replace(".0k", "k");
        }
        double value = count / 1_000_000.0;
        if (count % 1_000_000L == 0 || Math.round(value * 10) % 10 == 0) {
            return String.format(Locale.US, "%.0fM", value);
        }
        String formatted = String.format(Locale.US, "%.1fM", value);
        return formatted.replace(".0M", "M");
    }

    public static String pluralizeMaterial(Material material) {
        if (material == null) return "";
        return pluralizeName(prettifyMaterial(material));
    }

    public static String prettifyMaterial(Material material) {
        if (material == null) return "";
        String[] tokens = material.name().toLowerCase(Locale.US).split("_");
        StringBuilder builder = new StringBuilder();
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            if (!builder.isEmpty()) builder.append(' ');
            builder.append(Character.toUpperCase(token.charAt(0))).append(token.substring(1));
        }
        return builder.toString();
    }

    public static String pluralizeName(String name) {
        if (name == null || name.isBlank()) return "";
        String lower = name.toLowerCase(Locale.US);
        if (lower.equals("beef") || lower.equals("rotten flesh") || lower.equals("gunpowder")
                || lower.equals("string") || lower.equals("leather")) {
            return name;
        }
        if (lower.endsWith("ch") || lower.endsWith("sh") || lower.endsWith("s") || lower.endsWith("x") || lower.endsWith("z")) {
            return name + "es";
        }
        if (lower.endsWith("potato")) {
            return name + "es";
        }
        if (lower.endsWith("y") && !lower.endsWith("ay") && !lower.endsWith("ey") && !lower.endsWith("oy") && !lower.endsWith("uy")) {
            return name.substring(0, name.length() - 1) + "ies";
        }
        return name + "s";
    }

    public static double calculateFillPercentage(long currentTotal, long totalCapacity) {
        if (totalCapacity <= 0L || currentTotal <= 0L) {
            return 0.0;
        }
        return Math.min(100.0, (currentTotal / (double) totalCapacity) * 100.0);
    }

    public static String formatFillPercentage(double fillPercentage) {
        return String.format(Locale.US, "%.1f", fillPercentage);
    }

    public static int countStoredPages(SpawnerInstance instance, int itemsPerPage) {
        if (instance == null || itemsPerPage <= 0) {
            return 1;
        }

        int maxSlotIndex = 0;
        for (SpawnerLootEntry entry : instance.getStoredLootEntries()) {
            if (entry.getKey().startsWith("SLOT_")) {
                try {
                    int idx = Integer.parseInt(entry.getKey().substring(5));
                    maxSlotIndex = Math.max(maxSlotIndex, idx);
                } catch (NumberFormatException ignored) {}
            }
        }
        return Math.max(1, (int) Math.ceil((maxSlotIndex + 1) / (double) itemsPerPage));
    }

    public static ItemStack stripStorageMeta(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return item;
        }
        ItemStack copy = item.clone();
        var meta = copy.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(null);
            meta.setLore(null);
            copy.setItemMeta(meta);
        }
        return copy;
    }

    public static boolean hasNextPage(SpawnerInstance instance, int page, int itemsPerPage, int contentSlots) {
        if (instance == null || page < 1 || itemsPerPage <= 0 || contentSlots <= 0) {
            return false;
        }
        int pageOffset = (page - 1) * itemsPerPage;
        int occupiedSlots = 0;
        for (int s = 0; s < contentSlots; s++) {
            int slotIndex = pageOffset + s;
            SpawnerLootEntry entry = instance.getSlotLoot(slotIndex);
            if (entry != null && entry.getAmount() > 0L) {
                occupiedSlots++;
            }
        }
        return occupiedSlots >= contentSlots;
    }

    public static boolean hasPreviousPage(int page) {
        return page > 1;
    }

    @Override
    public void build(Player player) {
        SpawnerInstance instance = plugin.getSpawnerManager().getSpawner(spawnerId);
        if (instance == null) {
            inventory = Bukkit.createInventory(this, plugin.getSpawnerManager().getStorageSize(), ColorUtils.toComponent("&8Spawner Missing"));
            clear();
            fill(Material.GRAY_STAINED_GLASS_PANE);
            set(inventory.getSize() / 2, ItemUtils.createItem(Material.BARRIER, "&cSpawner Not Found"));
            return;
        }

        int itemsPerPage = plugin.getSpawnerManager().getStorageItemsPerPage();
        int totalPages = countStoredPages(instance, itemsPerPage);

        FileConfiguration config = plugin.getConfigManager().getMenus();
        String titleStr = config.getString("SPAWNER-MENUS.STORAGE-MENU.TITLE", "{type} Spawners");
        if (titleStr == null || titleStr.isBlank()) {
            titleStr = "{type} Spawners";
        }
        if (titleStr.contains("{mob}") || titleStr.contains("{type}") || titleStr.contains("{page}")) {
            String cleanMob = plugin.getSpawnerManager().prettifyKey(instance.getMobTypeKey());
            titleStr = titleStr
                    .replace("{type}", cleanMob)
                    .replace("{mob}", cleanMob)
                    .replace("{page}", String.valueOf(page))
                    .replace("{max_page}", String.valueOf(Math.max(totalPages, page)));
            titleStr = titleStr.replaceAll("(?i)\\bspawners?\\s+spawners?\\b", "Spawners");
        }

        inventory = Bukkit.createInventory(
                this,
                plugin.getSpawnerManager().getStorageSize(),
                ColorUtils.toComponent(titleStr)
        );

        clear();
        // Note: Row 6 (slots 45..53) is left as clean empty air by default, matching reference design

        int contentSlots = Math.min(itemsPerPage, inventory.getSize() - 9);
        int pageOffset = (page - 1) * itemsPerPage;

        for (int slot = 0; slot < contentSlots; slot++) {
            int slotIndex = pageOffset + slot;
            SpawnerLootEntry entry = instance.getSlotLoot(slotIndex);
            if (entry != null && entry.getAmount() > 0L) {
                set(slot, applyStorageMeta(plugin, instance, entry.getMaterial(), (int) entry.getAmount()));
            }
        }

        renderControlButtons(instance);
    }

    private void renderControlButtons(SpawnerInstance instance) {
        FileConfiguration config = plugin.getConfigManager().getMenus();

        // 1. Sell All Button (Slot 48)
        int sellSlot = config.getInt("SPAWNER-MENUS.STORAGE-MENU.SELL-ALL-BUTTON.SLOT", 48);
        String sellMatName = config.getString("SPAWNER-MENUS.STORAGE-MENU.SELL-ALL-BUTTON.MATERIAL", "GOLD_INGOT");
        Material sellMat = Material.matchMaterial(sellMatName);
        if (sellMat == null) sellMat = Material.GOLD_INGOT;
        String sellTitle = config.getString("SPAWNER-MENUS.STORAGE-MENU.SELL-ALL-BUTTON.TITLE", "&fSell All");
        List<String> sellLore = config.getStringList("SPAWNER-MENUS.STORAGE-MENU.SELL-ALL-BUTTON.LORE");
        if (sellLore.isEmpty()) {
            sellLore = List.of("&7&oClick to sell all loot");
        }
        set(sellSlot, ItemUtils.createItem(sellMat, sellTitle, sellLore));

        // 2. Mob Head Button (Slot 49)
        int headSlot = config.getInt("SPAWNER-MENUS.STORAGE-MENU.MOB-HEAD-BUTTON.SLOT", 49);
        set(headSlot, createSpawnerMobHead(plugin, instance));

        // 3. Drop Loot Button (Slot 50)
        int dropSlot = config.getInt("SPAWNER-MENUS.STORAGE-MENU.DROP-LOOT-BUTTON.SLOT", 50);
        String dropMatName = config.getString("SPAWNER-MENUS.STORAGE-MENU.DROP-LOOT-BUTTON.MATERIAL", "DROPPER");
        Material dropMat = Material.matchMaterial(dropMatName);
        if (dropMat == null) dropMat = Material.DROPPER;
        String dropTitle = config.getString("SPAWNER-MENUS.STORAGE-MENU.DROP-LOOT-BUTTON.TITLE", "&fDrop Loot");
        List<String> dropLore = config.getStringList("SPAWNER-MENUS.STORAGE-MENU.DROP-LOOT-BUTTON.LORE");
        if (dropLore.isEmpty()) {
            dropLore = List.of("&7&oClick to drop all loot on the page");
        }
        set(dropSlot, ItemUtils.createItem(dropMat, dropTitle, dropLore));

        int itemsPerPage = plugin.getSpawnerManager().getStorageItemsPerPage();
        int contentSlots = Math.min(itemsPerPage, inventory.getSize() - 9);

        // 4. Next Page Button (Slot 53) - shown when items fill the page or multiple pages exist
        int nextSlot = config.getInt("SPAWNER-MENUS.STORAGE-MENU.NEXT-PAGE-BUTTON.SLOT", 53);
        if (hasNextPage(instance, page, itemsPerPage, contentSlots)) {
            String nextMatName = config.getString("SPAWNER-MENUS.STORAGE-MENU.NEXT-PAGE-BUTTON.MATERIAL", "ARROW");
            Material nextMat = Material.matchMaterial(nextMatName);
            if (nextMat == null) nextMat = Material.ARROW;
            String nextTitle = config.getString("SPAWNER-MENUS.STORAGE-MENU.NEXT-PAGE-BUTTON.TITLE", "&fNext page");
            List<String> nextLore = config.getStringList("SPAWNER-MENUS.STORAGE-MENU.NEXT-PAGE-BUTTON.LORE");
            if (nextLore.isEmpty()) {
                nextLore = List.of("&7&oClick to view next page");
            }
            set(nextSlot, ItemUtils.createItem(nextMat, nextTitle, nextLore));
        } else {
            set(nextSlot, null);
        }

        // 5. Previous Page Button (Slot 45) - shown when on page > 1
        int prevSlot = config.getInt("SPAWNER-MENUS.STORAGE-MENU.PREVIOUS-PAGE-BUTTON.SLOT", 45);
        if (hasPreviousPage(page)) {
            String prevMatName = config.getString("SPAWNER-MENUS.STORAGE-MENU.PREVIOUS-PAGE-BUTTON.MATERIAL", "ARROW");
            Material prevMat = Material.matchMaterial(prevMatName);
            if (prevMat == null) prevMat = Material.ARROW;
            String prevTitle = config.getString("SPAWNER-MENUS.STORAGE-MENU.PREVIOUS-PAGE-BUTTON.TITLE", "&fPrevious page");
            List<String> prevLore = config.getStringList("SPAWNER-MENUS.STORAGE-MENU.PREVIOUS-PAGE-BUTTON.LORE");
            if (prevLore.isEmpty()) {
                prevLore = List.of("&7&oClick to view previous page");
            }
            set(prevSlot, ItemUtils.createItem(prevMat, prevTitle, prevLore));
        } else {
            set(prevSlot, null);
        }
    }

    public void handleInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        this.lastInteractionTime = System.currentTimeMillis();

        SpawnerInstance instance = plugin.getSpawnerManager().getSpawner(spawnerId);
        if (instance == null) {
            event.setCancelled(true);
            player.closeInventory();
            return;
        }

        Inventory topInventory = event.getView().getTopInventory();
        Inventory clickedInventory = event.getClickedInventory();
        int rawSlot = event.getRawSlot();
        ClickType clickType = event.getClick();
        ItemStack cursorItem = event.getCursor();

        int itemsPerPage = plugin.getSpawnerManager().getStorageItemsPerPage();
        int lastRow = topInventory.getSize() - 9;
        int pageOffset = (page - 1) * itemsPerPage;

        // 1. Click in player's bottom inventory
        if (clickedInventory != null && !clickedInventory.equals(topInventory)) {
            if (clickType.isShiftClick()) {
                ItemStack current = event.getCurrentItem();
                if (current != null && !current.getType().isAir()) {
                    event.setCancelled(true);
                    Material mat = current.getType();
                    int remainingToAdd = current.getAmount();
                    int maxStack = mat.getMaxStackSize();
                    if (maxStack <= 0) maxStack = 64;

                    // Step A: Top up existing slots of same material in top inventory (0..44)
                    for (int s = 0; s < lastRow && remainingToAdd > 0; s++) {
                        ItemStack inSlot = topInventory.getItem(s);
                        if (inSlot != null && inSlot.getType() == mat && inSlot.getAmount() < maxStack) {
                            int space = maxStack - inSlot.getAmount();
                            int add = Math.min(space, remainingToAdd);
                            int newAmount = inSlot.getAmount() + add;
                            int slotIndex = pageOffset + s;

                            topInventory.setItem(s, applyStorageMeta(plugin, instance, mat, newAmount));
                            instance.setSlotLoot(slotIndex, mat, newAmount);
                            remainingToAdd -= add;
                        }
                    }

                    // Step B: Fill first available empty slots if remainder > 0
                    for (int s = 0; s < lastRow && remainingToAdd > 0; s++) {
                        ItemStack inSlot = topInventory.getItem(s);
                        if (inSlot == null || inSlot.getType().isAir()) {
                            int add = Math.min(maxStack, remainingToAdd);
                            int slotIndex = pageOffset + s;

                            topInventory.setItem(s, applyStorageMeta(plugin, instance, mat, add));
                            instance.setSlotLoot(slotIndex, mat, add);
                            remainingToAdd -= add;
                        }
                    }

                    instance.setUpdatedAt(System.currentTimeMillis());
                    plugin.getSpawnerManager().saveLoot(instance);

                    if (remainingToAdd <= 0) {
                        event.setCurrentItem(null);
                    } else {
                        current.setAmount(remainingToAdd);
                    }
                    renderControlButtons(instance);
                    player.updateInventory();
                }
            }
            return;
        }

        FileConfiguration config = plugin.getConfigManager().getMenus();
        int sellSlot = config.getInt("SPAWNER-MENUS.STORAGE-MENU.SELL-ALL-BUTTON.SLOT", 48);
        int headSlot = config.getInt("SPAWNER-MENUS.STORAGE-MENU.MOB-HEAD-BUTTON.SLOT", 49);
        int dropSlot = config.getInt("SPAWNER-MENUS.STORAGE-MENU.DROP-LOOT-BUTTON.SLOT", 50);
        int nextSlot = config.getInt("SPAWNER-MENUS.STORAGE-MENU.NEXT-PAGE-BUTTON.SLOT", 53);
        int prevSlot = config.getInt("SPAWNER-MENUS.STORAGE-MENU.PREVIOUS-PAGE-BUTTON.SLOT", 45);

        // 2. Click in control row / bottom bar (Slots 45..53)
        if (rawSlot >= lastRow && rawSlot < topInventory.getSize()) {
            event.setCancelled(true);
            if (rawSlot == sellSlot) {
                plugin.getSpawnerManager().playSellConfirmOpenSound(player);
                new SpawnerSellConfirmMenu(plugin, spawnerId, page).open(player);
            } else if (rawSlot == dropSlot) {
                ActionResult result = plugin.getSpawnerManager().dropPageLoot(player, instance, page);
                player.sendMessage(ColorUtils.toComponent(result.message()));
                refresh(player);
            } else if (rawSlot == nextSlot) {
                int contentSlots = Math.min(itemsPerPage, topInventory.getSize() - 9);
                if (hasNextPage(instance, page, itemsPerPage, contentSlots)) {
                    plugin.getSpawnerManager().playOpenMenuSound(player);
                    new SpawnerStorageMenu(plugin, spawnerId, page + 1).open(player);
                }
            } else if (rawSlot == prevSlot) {
                if (hasPreviousPage(page)) {
                    plugin.getSpawnerManager().playOpenMenuSound(player);
                    new SpawnerStorageMenu(plugin, spawnerId, page - 1).open(player);
                }
            }
            player.updateInventory();
            return;
        }

        // 3. Click in storage content slots (Slots 0 to 44)
        if (rawSlot >= 0 && rawSlot < lastRow) {
            event.setCancelled(true);
            int slotIndex = pageOffset + rawSlot;
            ItemStack slotItem = topInventory.getItem(rawSlot);

            // Shift + Right Click (or Middle Click) -> Toggle Filter Status
            if (clickType == ClickType.SHIFT_RIGHT || clickType == ClickType.MIDDLE) {
                if (slotItem != null && !slotItem.getType().isAir()) {
                    boolean currentState = instance.isLootDisabled(slotItem.getType().name());
                    instance.setLootDisabled(slotItem.getType().name(), !currentState);
                    instance.setUpdatedAt(System.currentTimeMillis());
                    plugin.getSpawnerManager().saveSpawnerAndLoot(instance);

                    String statusMsg = !currentState ? "&cDisabled &7(Not Storing)" : "&aEnabled &7(Storing)";
                    player.sendMessage(ColorUtils.toComponent("&aToggled filter for &f"
                            + prettifyMaterial(slotItem.getType())
                            + " &ato " + statusMsg + "&a."));

                    plugin.getSpawnerManager().playFilterToggleSound(player);
                    topInventory.setItem(rawSlot, applyStorageMeta(plugin, instance, slotItem.getType(), slotItem.getAmount()));
                }
                player.updateInventory();
                return;
            }

            // Case A: Holding Item on Cursor
            if (cursorItem != null && !cursorItem.getType().isAir()) {
                if (clickType.isRightClick()) {
                    // Right Click -> Place 1 item from cursor into slot (1-by-1 split or merge 1)
                    if (slotItem == null || slotItem.getType().isAir()) {
                        topInventory.setItem(rawSlot, applyStorageMeta(plugin, instance, cursorItem.getType(), 1));
                        instance.setSlotLoot(slotIndex, cursorItem.getType(), 1);
                        instance.setUpdatedAt(System.currentTimeMillis());
                        plugin.getSpawnerManager().saveLoot(instance);

                        cursorItem.setAmount(cursorItem.getAmount() - 1);
                        event.setCursor(cursorItem.getAmount() <= 0 ? null : cursorItem);
                    } else if (slotItem.getType() == cursorItem.getType()) {
                        int maxStack = slotItem.getType().getMaxStackSize();
                        if (slotItem.getAmount() < maxStack) {
                            int newAmount = slotItem.getAmount() + 1;
                            topInventory.setItem(rawSlot, applyStorageMeta(plugin, instance, slotItem.getType(), newAmount));
                            instance.setSlotLoot(slotIndex, slotItem.getType(), newAmount);
                            instance.setUpdatedAt(System.currentTimeMillis());
                            plugin.getSpawnerManager().saveLoot(instance);

                            cursorItem.setAmount(cursorItem.getAmount() - 1);
                            event.setCursor(cursorItem.getAmount() <= 0 ? null : cursorItem);
                        }
                    }
                    renderControlButtons(instance);
                    player.updateInventory();
                    return;
                }

                // Left Click -> Place entire cursor stack into slot (or merge cursor stack into slot)
                if (slotItem == null || slotItem.getType().isAir()) {
                    topInventory.setItem(rawSlot, applyStorageMeta(plugin, instance, cursorItem.getType(), cursorItem.getAmount()));
                    instance.setSlotLoot(slotIndex, cursorItem.getType(), cursorItem.getAmount());
                    instance.setUpdatedAt(System.currentTimeMillis());
                    plugin.getSpawnerManager().saveLoot(instance);
                    event.setCursor(null);
                } else if (slotItem.getType() == cursorItem.getType()) {
                    int maxStack = slotItem.getType().getMaxStackSize();
                    int space = maxStack - slotItem.getAmount();
                    if (space > 0) {
                        int add = Math.min(space, cursorItem.getAmount());
                        int newAmount = slotItem.getAmount() + add;
                        topInventory.setItem(rawSlot, applyStorageMeta(plugin, instance, slotItem.getType(), newAmount));
                        instance.setSlotLoot(slotIndex, slotItem.getType(), newAmount);
                        instance.setUpdatedAt(System.currentTimeMillis());
                        plugin.getSpawnerManager().saveLoot(instance);

                        cursorItem.setAmount(cursorItem.getAmount() - add);
                        event.setCursor(cursorItem.getAmount() <= 0 ? null : cursorItem);
                    }
                }
                renderControlButtons(instance);
                player.updateInventory();
                return;
            }

            // Case B: Cursor is Empty
            if (slotItem != null && !slotItem.getType().isAir()) {
                if (clickType.isShiftClick()) {
                    // Shift + Left Click -> Collect stack to player inventory
                    ItemStack cleanStack = stripStorageMeta(slotItem);
                    HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(cleanStack);
                    if (leftover.isEmpty()) {
                        topInventory.setItem(rawSlot, null);
                        instance.removeSlotLoot(slotIndex);
                    } else {
                        int keptAmount = 0;
                        for (ItemStack stack : leftover.values()) {
                            if (stack != null && stack.getType() == slotItem.getType()) {
                                keptAmount += stack.getAmount();
                            }
                        }
                        int takenAmount = slotItem.getAmount() - keptAmount;
                        if (takenAmount > 0) {
                            topInventory.setItem(rawSlot, applyStorageMeta(plugin, instance, slotItem.getType(), keptAmount));
                            instance.setSlotLoot(slotIndex, slotItem.getType(), keptAmount);
                        }
                    }
                    instance.setUpdatedAt(System.currentTimeMillis());
                    plugin.getSpawnerManager().saveLoot(instance);
                    plugin.getSpawnerManager().playCollectLootSound(player);
                    renderControlButtons(instance);
                    player.updateInventory();
                    return;
                }

                if (clickType.isRightClick()) {
                    // Right Click with empty cursor -> Pick up HALF stack onto cursor
                    int totalAmount = slotItem.getAmount();
                    int half = (int) Math.ceil(totalAmount / 2.0);
                    int rem = totalAmount - half;

                    ItemStack pickedUpHalf = stripStorageMeta(slotItem);
                    pickedUpHalf.setAmount(half);

                    if (rem <= 0) {
                        topInventory.setItem(rawSlot, null);
                        instance.removeSlotLoot(slotIndex);
                    } else {
                        topInventory.setItem(rawSlot, applyStorageMeta(plugin, instance, slotItem.getType(), rem));
                        instance.setSlotLoot(slotIndex, slotItem.getType(), rem);
                    }
                    instance.setUpdatedAt(System.currentTimeMillis());
                    plugin.getSpawnerManager().saveLoot(instance);
                    event.setCursor(pickedUpHalf);
                    renderControlButtons(instance);
                    player.updateInventory();
                    return;
                }

                // Normal Left Click -> Pick up FULL stack onto cursor
                ItemStack pickedUp = stripStorageMeta(slotItem);
                topInventory.setItem(rawSlot, null);
                instance.removeSlotLoot(slotIndex);
                instance.setUpdatedAt(System.currentTimeMillis());
                plugin.getSpawnerManager().saveLoot(instance);
                event.setCursor(pickedUp);
                renderControlButtons(instance);
                player.updateInventory();
            }
        }
    }

    public void handleInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        this.lastInteractionTime = System.currentTimeMillis();

        SpawnerInstance instance = plugin.getSpawnerManager().getSpawner(spawnerId);
        if (instance == null) {
            event.setCancelled(true);
            return;
        }

        int lastRow = plugin.getSpawnerManager().getStorageSize() - 9;
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot >= lastRow && rawSlot < plugin.getSpawnerManager().getStorageSize()) {
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
        }
    }
}
