# `filter.yml`

Item category definitions for the marketplace browse filters — the Blocks / Tools / Food /
Combat buttons players use to narrow down Orders and Auction House listings.

Despite the name this is **not** a chat filter; profanity and advertising handling lives in
`config.yml` under `CHAT` and in `anvil-moderation.yml`. The structure here is deliberately
plain: every top-level key becomes a category, and its value is the list of Bukkit
`Material` names that belong to it. Add a key and it becomes a new category; names that do
not resolve to a real material are skipped silently, which is worth remembering after a
Minecraft update renames an item.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/filter.yml` |
| **Player-facing text** | Not translated. Edit this file directly. |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`Blocks`](#section-blocks) | list | 522 entries |
| [`Tools`](#section-tools) | list | 33 entries |
| [`Food`](#section-food) | list | 43 entries |
| [`Combat`](#section-combat) | list | 41 entries |
| [`Potions`](#section-potions) | list | 4 entries |
| [`Books`](#section-books) | list | 10 entries |
| [`Ingredients`](#section-ingredients) | list | 114 entries |
| [`Utilities`](#section-utilities) | list | 110 entries |

---

## Section: `Blocks`

A list of 522 values:

```yaml
Blocks:
  - STONE
  - GRANITE
  - POLISHED_GRANITE
  - DIORITE
  - POLISHED_DIORITE
  - ANDESITE
  - POLISHED_ANDESITE
  - DEEPSLATE
  - COBBLED_DEEPSLATE
  - POLISHED_DEEPSLATE
  - TUFF
  - CALCITE
  - DRIPSTONE_BLOCK
  - MOSS_BLOCK
  - MOSS_CARPET
  - GRASS_BLOCK
  - DIRT
  - COARSE_DIRT
  - ROOTED_DIRT
  - MUD
  - PACKED_MUD
  - CLAY
  - GRAVEL
  - SAND
  - RED_SAND
  - SANDSTONE
  - CHISELED_SANDSTONE
  - CUT_SANDSTONE
  - SMOOTH_SANDSTONE
  - RED_SANDSTONE
  - CHISELED_RED_SANDSTONE
  - CUT_RED_SANDSTONE
  - SMOOTH_RED_SANDSTONE
  - GLASS
  - TINTED_GLASS
  - GLASS_PANE
  - WHITE_GLASS
  - ORANGE_GLASS
  - MAGENTA_GLASS
  - LIGHT_BLUE_GLASS
  # ... 482 more
```

<details>
<summary>Default <code>Blocks</code> block as shipped</summary>

```yaml
# Configuration section for Blocks.
```

</details>

---

## Section: `Tools`

A list of 33 values:

```yaml
Tools:
  - WOODEN_PICKAXE
  - STONE_PICKAXE
  - IRON_PICKAXE
  - GOLDEN_PICKAXE
  - DIAMOND_PICKAXE
  - NETHERITE_PICKAXE
  - WOODEN_AXE
  - STONE_AXE
  - IRON_AXE
  - GOLDEN_AXE
  - DIAMOND_AXE
  - NETHERITE_AXE
  - WOODEN_SHOVEL
  - STONE_SHOVEL
  - IRON_SHOVEL
  - GOLDEN_SHOVEL
  - DIAMOND_SHOVEL
  - NETHERITE_SHOVEL
  - WOODEN_HOE
  - STONE_HOE
  - IRON_HOE
  - GOLDEN_HOE
  - DIAMOND_HOE
  - NETHERITE_HOE
  - SHEARS
  - FISHING_ROD
  - FLINT_AND_STEEL
  - BRUSH
  - CARROT_ON_A_STICK
  - WARPED_FUNGUS_ON_A_STICK
  - LEAD
  - NAME_TAG
  - SPYGLASS
```

<details>
<summary>Default <code>Tools</code> block as shipped</summary>

```yaml
# Configuration section for Tools.
```

</details>

---

## Section: `Food`

A list of 43 values:

```yaml
Food:
  - APPLE
  - GOLDEN_APPLE
  - ENCHANTED_GOLDEN_APPLE
  - BREAD
  - BEETROOT
  - BEETROOT_SOUP
  - CARROT
  - GOLDEN_CARROT
  - POTATO
  - BAKED_POTATO
  - POISONOUS_POTATO
  - PUMPKIN_PIE
  - COOKIE
  - MELON_SLICE
  - SWEET_BERRIES
  - GLOW_BERRIES
  - CHORUS_FRUIT
  - DRIED_KELP
  - MUSHROOM_STEW
  - RABBIT_STEW
  - SUSPICIOUS_STEW
  - HONEY_BOTTLE
  - CAKE
  - EGG
  - MILK_BUCKET
  - PORKCHOP
  - COOKED_PORKCHOP
  - BEEF
  - COOKED_BEEF
  - MUTTON
  - COOKED_MUTTON
  - CHICKEN
  - COOKED_CHICKEN
  - RABBIT
  - COOKED_RABBIT
  - COD
  - COOKED_COD
  - SALMON
  - COOKED_SALMON
  - TROPICAL_FISH
  # ... 3 more
```

<details>
<summary>Default <code>Food</code> block as shipped</summary>

```yaml
# Configuration section for Food.
```

</details>

---

## Section: `Combat`

A list of 41 values:

```yaml
Combat:
  - WOODEN_SWORD
  - STONE_SWORD
  - IRON_SWORD
  - GOLDEN_SWORD
  - DIAMOND_SWORD
  - NETHERITE_SWORD
  - BOW
  - CROSSBOW
  - TRIDENT
  - SHIELD
  - ARROW
  - SPECTRAL_ARROW
  - TIPPED_ARROW
  - TURTLE_HELMET
  - LEATHER_HELMET
  - LEATHER_CHESTPLATE
  - LEATHER_LEGGINGS
  - LEATHER_BOOTS
  - CHAINMAIL_HELMET
  - CHAINMAIL_CHESTPLATE
  - CHAINMAIL_LEGGINGS
  - CHAINMAIL_BOOTS
  - IRON_HELMET
  - IRON_CHESTPLATE
  - IRON_LEGGINGS
  - IRON_BOOTS
  - GOLDEN_HELMET
  - GOLDEN_CHESTPLATE
  - GOLDEN_LEGGINGS
  - GOLDEN_BOOTS
  - DIAMOND_HELMET
  - DIAMOND_CHESTPLATE
  - DIAMOND_LEGGINGS
  - DIAMOND_BOOTS
  - NETHERITE_HELMET
  - NETHERITE_CHESTPLATE
  - NETHERITE_LEGGINGS
  - NETHERITE_BOOTS
  - ELYTRA
  - FIREWORK_ROCKET
  # ... 1 more
```

<details>
<summary>Default <code>Combat</code> block as shipped</summary>

```yaml
# Configuration section for Combat.
```

</details>

---

## Section: `Potions`

A list of 4 values:

```yaml
Potions:
  - POTION
  - SPLASH_POTION
  - LINGERING_POTION
  - DRAGON_BREATH
```

<details>
<summary>Default <code>Potions</code> block as shipped</summary>

```yaml
# Configuration section for Potions.
```

</details>

---

## Section: `Books`

A list of 10 values:

```yaml
Books:
  - BOOK
  - ENCHANTED_BOOK
  - WRITABLE_BOOK
  - WRITTEN_BOOK
  - KNOWLEDGE_BOOK
  - PAPER
  - MAP
  - FILLED_MAP
  - COMPASS
  - RECOVERY_COMPASS
```

<details>
<summary>Default <code>Books</code> block as shipped</summary>

```yaml
# Configuration section for Books.
```

</details>

---

## Section: `Ingredients`

A list of 114 values:

```yaml
Ingredients:
  - STICK
  - STRING
  - FEATHER
  - LEATHER
  - RABBIT_HIDE
  - WOOL
  - HONEYCOMB
  - SLIME_BALL
  - MAGMA_CREAM
  - BLAZE_ROD
  - BLAZE_POWDER
  - GHAST_TEAR
  - PHANTOM_MEMBRANE
  - ENDER_PEARL
  - ENDER_EYE
  - SPIDER_EYE
  - FERMENTED_SPIDER_EYE
  - GUNPOWDER
  - BONE
  - BONE_MEAL
  - FLINT
  - NETHER_WART
  - SUGAR
  - WHEAT
  - BEETROOT_SEEDS
  - MELON_SEEDS
  - PUMPKIN_SEEDS
  - WHEAT_SEEDS
  - KELP
  - BAMBOO
  - ECHO_SHARD
  - PRISMARINE_SHARD
  - PRISMARINE_CRYSTALS
  - NAUTILUS_SHELL
  - HEART_OF_THE_SEA
  - COAL
  - CHARCOAL
  - RAW_IRON
  - IRON_INGOT
  - RAW_COPPER
  # ... 74 more
```

<details>
<summary>Default <code>Ingredients</code> block as shipped</summary>

```yaml
# Configuration section for Ingredients.
```

</details>

---

## Section: `Utilities`

A list of 110 values:

```yaml
Utilities:
  - REDSTONE_TORCH
  - REDSTONE_LAMP
  - REPEATER
  - COMPARATOR
  - OBSERVER
  - TARGET
  - DAYLIGHT_DETECTOR
  - HOPPER
  - DROPPER
  - DISPENSER
  - PISTON
  - STICKY_PISTON
  - NOTE_BLOCK
  - LIGHTNING_ROD
  - CHEST
  - ENDER_CHEST
  - BARREL
  - SHULKER_BOX
  - WHITE_SHULKER_BOX
  - ORANGE_SHULKER_BOX
  - MAGENTA_SHULKER_BOX
  - LIGHT_BLUE_SHULKER_BOX
  - YELLOW_SHULKER_BOX
  - LIME_SHULKER_BOX
  - PINK_SHULKER_BOX
  - GRAY_SHULKER_BOX
  - LIGHT_GRAY_SHULKER_BOX
  - CYAN_SHULKER_BOX
  - PURPLE_SHULKER_BOX
  - BLUE_SHULKER_BOX
  - BROWN_SHULKER_BOX
  - GREEN_SHULKER_BOX
  - RED_SHULKER_BOX
  - BLACK_SHULKER_BOX
  - JUKEBOX
  - CARTOGRAPHY_TABLE
  - SMITHING_TABLE
  - STONECUTTER
  - LOOM
  - FLETCHING_TABLE
  # ... 70 more
```

<details>
<summary>Default <code>Utilities</code> block as shipped</summary>

```yaml
# Configuration section for Utilities.
```

</details>

---

Defaults above match the file shipped in the jar.
