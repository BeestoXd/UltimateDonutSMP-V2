# `spawners.yml`

DonutSMP-style stacked spawners. Instead of a vanilla spawner spitting out mobs, a managed
spawner accumulates its drops over time and hands them over when the player collects, which
is far cheaper than keeping hundreds of entities alive.

`TYPES` defines one entry per mob: what it produces, how fast, and what stacking it allows.
`SETTINGS` holds the global limits and `GUI` the management panel opened by right-clicking
a placed spawner. Placed spawners are stored in the database and survive restarts; note
that they are deliberately preserved by the offence `wipe` flag when a player is banned.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/spawners.yml` |
| **Commands** | `/spawner` |
| **Player-facing text** | Edit `CONFIG.SPAWNERS` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`SETTINGS`](#section-settings) | section | 13 keys |
| [`GUI`](#section-gui) | section | 4 keys |
| [`TYPES`](#section-types) | section | 9 keys |

---

## Section: `SETTINGS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `SETTINGS` section on or off. |
| `SETTINGS.ACCESS_MODE` | `string` | OWNER_ONLY, OWNER_AND_TEAM, PUBLIC | `OWNER_ONLY` | The Access Mode setting. |
| `SETTINGS.ALLOW_SPAWNER_STEAL` | `boolean` | `true`, `false` | `false` | Determines whether spawner stealing (breaking or accessing other players' spawners) is allowed globally. |
| `SETTINGS.GENERATION_INTERVAL_SECONDS` | `integer` | Any integer | `5` | Generation interval seconds. Seconds. |
| `SETTINGS.PROCESS_ONLY_LOADED_CHUNKS` | `boolean` | `true`, `false` | `true` | On/off for process only loaded chunks. |
| `SETTINGS.REQUIRE_PLAYER_NEARBY` | `boolean` | `true`, `false` | `false` | On/off for require player nearby. |
| `SETTINGS.PLAYER_NEARBY_RADIUS` | `integer` | Any integer | `16` | Player nearby radius. |
| `SETTINGS.MAX_STACK_PER_BLOCK` | `integer` | Any integer | `100000` | Max stack per block. |
| `SETTINGS.STORAGE_CAP_PER_LOOT_KEY` | `integer` | Any integer | `1000000` | Storage cap per loot key. |
| `SETTINGS.DROP_ON_BREAK_IF_INVENTORY_FULL` | `boolean` | `true`, `false` | `true` | On/off for drop on break if inventory full. |
| `SETTINGS.REQUIRE_SILK_TOUCH` | `boolean` | `true`, `false` | `true` | Determines whether a Silk Touch pickaxe is required to break and collect spawners. Applies to vanilla spawners as well. Only Creative mode and the ultimatedonutsmp2.spawner.bypass permission are exempt (operators are not). |
| `SETTINGS.CANCEL_MOB_SPAWN` | `boolean` | `true`, `false` | `true` | Determines whether physical vanilla mob spawning is cancelled (set to true for virtual storage anti-lag drops, set to false to allow physical mobs to spawn in world). |
| `SETTINGS.XP_ENABLED` | `boolean` | `true`, `false` | `true` | Determines whether XP generation and XP collection is enabled for spawners. |

<details>
<summary>Default <code>SETTINGS</code> block as shipped</summary>

```yaml
# Configuration section for Settings.
SETTINGS:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # The Access Mode setting. Available options: OWNER_ONLY, OWNER_AND_TEAM, PUBLIC
  ACCESS_MODE: OWNER_ONLY
  # Determines whether spawner stealing (breaking or accessing other players' spawners) is allowed globally. Available options: true, false
  ALLOW_SPAWNER_STEAL: false
  # The numerical value for Generation Interval Seconds. Available options: Any valid integer
  GENERATION_INTERVAL_SECONDS: 5
  # Determines whether Process Only Loaded Chunks is enabled or disabled. Available options: true, false
  PROCESS_ONLY_LOADED_CHUNKS: true
  # Determines whether Require Player Nearby is enabled or disabled. Available options: true, false
  REQUIRE_PLAYER_NEARBY: false
  # The numerical value for Player Nearby Radius. Available options: Any valid integer
  PLAYER_NEARBY_RADIUS: 16
  # The numerical value for Max Stack Per Block. Available options: Any valid integer
  MAX_STACK_PER_BLOCK: 100000
  # The numerical value for Storage Cap Per Loot Key. Available options: Any valid integer
  STORAGE_CAP_PER_LOOT_KEY: 1000000
  # Determines whether Drop On Break If Inventory Full is enabled or disabled. Available options: true, false
  DROP_ON_BREAK_IF_INVENTORY_FULL: true
  # Determines whether a Silk Touch pickaxe is required to break and collect spawners.
  # Applies to vanilla spawners as well. Only Creative mode and the ultimatedonutsmp2.spawner.bypass permission are exempt (operators are not).
  REQUIRE_SILK_TOUCH: true
  # Determines whether physical vanilla mob spawning is cancelled (set to true for virtual storage anti-lag drops, set to false to allow physical mobs to spawn in world).
  CANCEL_MOB_SPAWN: true
  # Determines whether XP generation and XP collection is enabled for spawners. Available options: true, false
  XP_ENABLED: true
```

</details>

---

## Section: `GUI`

### `GUI.MAIN_MENU`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.MAIN_MENU.TITLE` | `string` | Any text | `{stack} {mob}` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.MAIN_MENU.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |

### `GUI.STORAGE`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.STORAGE.TITLE` | `string` | Any text | `&8{mob} Spawners - {page}/{max_page}` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.STORAGE.SIZE` | `integer` | Any integer | `54` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `GUI.STORAGE.ITEMS_PER_PAGE` | `integer` | Any integer | `45` | Items per page. |

### `GUI.PANEL`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.PANEL.TITLE` | `string` | Any text | `&8Spawners - {world}` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.PANEL.SIZE` | `integer` | Any integer | `54` | Chest size. Must be a multiple of 9 between 9 and 54. |

### `GUI.WORLD_LIST`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.WORLD_LIST.TITLE` | `string` | Any text | `&8Spawners Panel` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.WORLD_LIST.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |

<details>
<summary>Default <code>GUI</code> block as shipped</summary>

```yaml
# Configuration section for Gui.
GUI:
  # Configuration section for Main Menu.
  MAIN_MENU:
    TITLE: '{stack} {mob}'
    SIZE: 27
  # Configuration section for Storage.
  STORAGE:
    TITLE: '&8{mob} Spawners - {page}/{max_page}'
    SIZE: 54
    # The numerical value for Items Per Page. Available options: Any valid integer
    ITEMS_PER_PAGE: 45
  # Configuration section for Panel.
  PANEL:
    TITLE: '&8Spawners - {world}'
    SIZE: 54
  # Configuration section for World List.
  WORLD_LIST:
    TITLE: '&8Spawners Panel'
    SIZE: 27
```

</details>

---

## Section: `TYPES`

### Entry schema (9 entries)

Each entry under `TYPES` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key |  |
| :--- |  |
| `PIG` |  |
| `COW` |  |
| `ZOMBIE` |  |
| `SPIDER` |  |
| `SKELETON` |  |
| `CREEPER` |  |
| `PIGLIN` |  |
| `BLAZE` |  |
| `IRON_GOLEM` |  |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `BASE_ITEMS_PER_CYCLE` | `integer` | Any integer | Required | Base items per cycle. |
| `DISPLAY_NAME` | `string` | Any text | Required | Display name. |
| `DROPS` | `section` | — | Required | Options for drops, listed below. |
| `ENTITY_TYPE` | `string` | Any text | Required | Entity type. |
| `HEAD_TEXTURE` | `string` | Any text | Required | Custom head texture URL or Base64 (leave empty to use default mob head). |
| `ICON_MATERIAL` | `string` | Any text | Required | Icon material. |
| `XP_PER_CYCLE` | `decimal` | Any decimal number | Optional (1/9) | Xp per cycle. |

#### `TYPES.<entry>.DROPS`

#### `TYPES.<entry>.DROPS` — one block per key

Keys under `TYPES.<entry>.DROPS` are identifiers rather than fixed options; every one of them takes the same block described below. The shipped file defines 16:

`ARROW`, `BEEF`, `BLAZE_ROD`, `BONE`, `CARROT`, `GOLD_INGOT`, `GOLD_NUGGET`, `GUNPOWDER`, `IRON_INGOT`, `LEATHER`, `POPPY`, `PORKCHOP`, `POTATO`, `ROTTEN_FLESH`, `SPIDER_EYE`, `STRING`

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHANCE` | `decimal` | Any decimal number | Required | Relative weight in the pool, not a percentage. |
| `MATERIAL` | `string` | Any text | Required | Bukkit `Material` name for the icon. |
| `MAX` | `integer` | Any integer | Required | Max. |
| `MIN` | `integer` | Any integer | Required | Min. |

<details>
<summary>Default <code>TYPES</code> block as shipped</summary>

```yaml
# Configuration section for Types.
TYPES:
  # Configuration section for Pig.
  PIG:
    # The text or value for Display Name. Available options: Any valid string text
    DISPLAY_NAME: '&ePig Spawner'
    # The text or value for Entity Type. Available options: Any valid string text
    ENTITY_TYPE: PIG
    # Custom head texture URL or Base64 (leave empty to use default mob head).
    HEAD_TEXTURE: 'https://textures.minecraft.net/texture/d875eb45aca34a4d24c3dc1395fc020ccf37f825a17b054a22fd24b189c24c'
    # The text or value for Icon Material. Available options: Any valid string text
    ICON_MATERIAL: PORKCHOP
    # The numerical value for Base Items Per Cycle. Available options: Any valid integer
    BASE_ITEMS_PER_CYCLE: 1
    # Configuration section for Drops.
    DROPS:
      # Configuration section for Porkchop.
      PORKCHOP:
        MATERIAL: PORKCHOP
        # The numerical value for Min. Available options: Any valid integer
        MIN: 1
        # The numerical value for Max. Available options: Any valid integer
        MAX: 3
        # The decimal value for Chance. Available options: Any decimal number
        CHANCE: 1.0
      # Configuration section for Leather.
      LEATHER:
        MATERIAL: LEATHER
        # The numerical value for Min. Available options: Any valid integer
        MIN: 0
        # The numerical value for Max. Available options: Any valid integer
        MAX: 1
        # The decimal value for Chance. Available options: Any decimal number
        CHANCE: 0.35
  # Configuration section for Cow.
  COW:
    # The text or value for Display Name. Available options: Any valid string text
    DISPLAY_NAME: '&eCow Spawner'
    # The text or value for Entity Type. Available options: Any valid string text
    ENTITY_TYPE: COW
    # Custom head texture URL or Base64 (leave empty to use default mob head).
    HEAD_TEXTURE: 'https://textures.minecraft.net/texture/61527b63294bebcb2a21d2b0e4f3da50c2c65d6c5354185881e675c123c331c5'
    # The text or value for Icon Material. Available options: Any valid string text
    ICON_MATERIAL: BEEF
    # The numerical value for Base Items Per Cycle. Available options: Any valid integer
    BASE_ITEMS_PER_CYCLE: 1
    # The decimal value for XP generated per spawner cycle per stack.
    XP_PER_CYCLE: 3.7
# ... section continues, see the file on disk for the full block
```

</details>

---

Defaults above match the file shipped in the jar.
