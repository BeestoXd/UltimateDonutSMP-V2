# `crates.yml`

Defines every crate on the server: the key that opens it, the animation played, the reward
pool and the odds attached to each reward. Crates work both virtually (through `/crate`)
and physically, by binding a crate to a chest in the world.

Rewards are weighted rather than percentage-based — a reward's chance is its weight divided
by the total weight of the pool — so adding a new reward dilutes everything else in that
crate unless you rebalance. The `CRATES` subtree is treated as yours: config merging on
startup will add missing top-level defaults but will never rewrite crates you have defined.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/crates.yml` |
| **Commands** | `/crate` (including `/crate keyall`), `/crates`, `/keys` |
| **Player-facing text** | Edit `CONFIG.CRATES` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`SETTINGS`](#section-settings) | section | 5 keys |
| [`CRATES`](#section-crates) | section | 4 keys |

---

## Section: `SETTINGS`

### `SETTINGS.LIST-MENU`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.LIST-MENU.TITLE` | `string` | Any text | `&8Crates` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `SETTINGS.LIST-MENU.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `SETTINGS.LIST-MENU.FILLER` | `string` | Any text | `GRAY_STAINED_GLASS_PANE` | Material used to fill empty menu slots. |
| `SETTINGS.LIST-MENU.CONTENT-SLOTS` | `list` | A list of values | _list of 7 items_ | The content slots list. |
| `SETTINGS.LIST-MENU.EMPTY-SLOT` | `integer` | Any integer | `13` | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default contents of <code>SETTINGS.LIST-MENU.CONTENT-SLOTS</code> (7 items)</summary>

```yaml
CONTENT-SLOTS:
  - 10
  - 11
  - 12
  - 13
  - 14
  - 15
  - 16
```

</details>

#### `SETTINGS.LIST-MENU.EMPTY`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.LIST-MENU.EMPTY.MATERIAL` | `string` | Any text | `BARRIER` | Bukkit `Material` name for the icon. |
| `SETTINGS.LIST-MENU.EMPTY.DISPLAY-NAME` | `string` | Any text | `&cNo Crates` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `SETTINGS.LIST-MENU.EMPTY.LORE` | `list` | A list of values | _list of 1 item_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>SETTINGS.LIST-MENU.EMPTY.LORE</code> (1 item)</summary>

```yaml
LORE:
  - '&7No crates are available right now.'
```

</details>

#### `SETTINGS.LIST-MENU.CLOSE-BUTTON`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.LIST-MENU.CLOSE-BUTTON.SLOT` | `integer` | Any integer | `26` | Inventory slot, `0` is the top-left cell. |
| `SETTINGS.LIST-MENU.CLOSE-BUTTON.MATERIAL` | `string` | Any text | `BARRIER` | Bukkit `Material` name for the icon. |
| `SETTINGS.LIST-MENU.CLOSE-BUTTON.DISPLAY-NAME` | `string` | Any text | `&cClose` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `SETTINGS.LIST-MENU.CLOSE-BUTTON.LORE` | `list` | A list of values | _list of 1 item_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>SETTINGS.LIST-MENU.CLOSE-BUTTON.LORE</code> (1 item)</summary>

```yaml
LORE:
  - '&7Close this menu.'
```

</details>

### `SETTINGS.CONFIRM-MENU`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.CONFIRM-MENU.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `SETTINGS.CONFIRM-MENU.FILLER` | `string` | Any text | `GRAY_STAINED_GLASS_PANE` | Material used to fill empty menu slots. |
| `SETTINGS.CONFIRM-MENU.PREVIEW-SLOT` | `integer` | Any integer | `13` | Inventory slot, `0` is the top-left cell. |
| `SETTINGS.CONFIRM-MENU.CONFIRM-SLOT` | `integer` | Any integer | `15` | Inventory slot, `0` is the top-left cell. |

#### `SETTINGS.CONFIRM-MENU.CONFIRM-BUTTON`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.CONFIRM-MENU.CONFIRM-BUTTON.MATERIAL` | `string` | Any text | `LIME_STAINED_GLASS_PANE` | Bukkit `Material` name for the icon. |
| `SETTINGS.CONFIRM-MENU.CONFIRM-BUTTON.DISPLAY-NAME` | `string` | Any text | `&aConfirm` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `SETTINGS.CONFIRM-MENU.CONFIRM-BUTTON.LORE` | `list` | A list of values | _list of 2 items_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>SETTINGS.CONFIRM-MENU.CONFIRM-BUTTON.LORE</code> (2 items)</summary>

```yaml
LORE:
  - '&7Claim &f{reward}&7 from'
  - '&b{crate}&7.'
```

</details>

#### `SETTINGS.CONFIRM-MENU.CANCEL-BUTTON`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.CONFIRM-MENU.CANCEL-BUTTON.SLOT` | `integer` | Any integer | `11` | Inventory slot, `0` is the top-left cell. |
| `SETTINGS.CONFIRM-MENU.CANCEL-BUTTON.MATERIAL` | `string` | Any text | `RED_STAINED_GLASS_PANE` | Bukkit `Material` name for the icon. |
| `SETTINGS.CONFIRM-MENU.CANCEL-BUTTON.DISPLAY-NAME` | `string` | Any text | `&cCancel` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `SETTINGS.CONFIRM-MENU.CANCEL-BUTTON.LORE` | `list` | A list of values | _list of 1 item_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>SETTINGS.CONFIRM-MENU.CANCEL-BUTTON.LORE</code> (1 item)</summary>

```yaml
LORE:
  - '&7Return to the reward list.'
```

</details>

### `SETTINGS.HOLOGRAM`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.HOLOGRAM.OFFSET-Y` | `decimal` | Any decimal number | `1.6` | Height of the first line above the crate block. Raise it to lift the whole stack of text away from the item spinning on top of the crate. |
| `SETTINGS.HOLOGRAM.LINE-SPACING` | `decimal` | Any decimal number | `0.27` | Vertical gap between one line of the hologram and the next, in blocks. The same option the portal holograms use in config.yml, and the same default. Values outside 0.05 to 0.5 are pulled back into that range, which is wide enough for any readable stack and keeps the hologram inside the area the plugin searches when it checks its own text is still there. |
| `SETTINGS.HOLOGRAM.LINES` | `list` | A list of values | _list of 2 items_ | The lines list. |
| `SETTINGS.HOLOGRAM.KEY-LINE` | `string` | Any text | `&7Keys: &f{keys}` | Key line. |

<details>
<summary>Default contents of <code>SETTINGS.HOLOGRAM.LINES</code> (2 items)</summary>

```yaml
LINES:
  - '{crate}'
  - '&7Right-click to open'
```

</details>

### `SETTINGS.PARTICLES`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.PARTICLES.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `PARTICLES` section on or off. |
| `SETTINGS.PARTICLES.TYPE` | `string` | Any text | `ENCHANT` | Which option this section uses. |
| `SETTINGS.PARTICLES.COUNT` | `integer` | Any integer | `4` | Count. |

### `SETTINGS.GACHA`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.GACHA.TITLE` | `string` | Any text | `&8Rolling Reward` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `SETTINGS.GACHA.FILLER` | `string` | Any text | `BLACK_STAINED_GLASS_PANE` | Material used to fill empty menu slots. |
| `SETTINGS.GACHA.PREVIEW-SLOTS` | `list` | A list of values | _list of 7 items_ | The preview slots list. |
| `SETTINGS.GACHA.POINTER-SLOT` | `integer` | Any integer | `13` | Inventory slot, `0` is the top-left cell. |
| `SETTINGS.GACHA.TOTAL-STEPS` | `integer` | Any integer | `38` | Total steps. |
| `SETTINGS.GACHA.TICK-INTERVAL` | `integer` | Any integer | `2` | Tick interval. Ticks (20 = 1 second). |

<details>
<summary>Default contents of <code>SETTINGS.GACHA.PREVIEW-SLOTS</code> (7 items)</summary>

```yaml
PREVIEW-SLOTS:
  - 10
  - 11
  - 12
  - 13
  - 14
  - 15
  - 16
```

</details>

<details>
<summary>Default <code>SETTINGS</code> block as shipped</summary>

```yaml
SETTINGS:
  LIST-MENU:
    TITLE: '&8Crates'
    SIZE: 27
    FILLER: GRAY_STAINED_GLASS_PANE
    CONTENT-SLOTS:
    - 10
    - 11
    - 12
    - 13
    - 14
    - 15
    - 16
    EMPTY-SLOT: 13
    EMPTY:
      MATERIAL: BARRIER
      DISPLAY-NAME: '&cNo Crates'
      LORE:
      - '&7No crates are available right now.'
    CLOSE-BUTTON:
      SLOT: 26
      MATERIAL: BARRIER
      DISPLAY-NAME: '&cClose'
      LORE:
      - '&7Close this menu.'
  
  CONFIRM-MENU:
    SIZE: 27
    FILLER: GRAY_STAINED_GLASS_PANE
    PREVIEW-SLOT: 13
    CONFIRM-SLOT: 15
    CONFIRM-BUTTON:
      MATERIAL: LIME_STAINED_GLASS_PANE
      DISPLAY-NAME: '&aConfirm'
      LORE:
      - '&7Claim &f{reward}&7 from'
      - '&b{crate}&7.'
    CANCEL-BUTTON:
      SLOT: 11
      MATERIAL: RED_STAINED_GLASS_PANE
      DISPLAY-NAME: '&cCancel'
      LORE:
      - '&7Return to the reward list.'
  
  HOLOGRAM:
    # Height of the first line above the crate block. Raise it to lift the whole stack of text
    # away from the item spinning on top of the crate.
    OFFSET-Y: 1.6
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `CRATES`

### `CRATES.common`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.common.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `common` section on or off. |
| `CRATES.common.OPEN-TYPE` | `string` | Any text | `CHOOSE_ONE` | Open type. |
| `CRATES.common.PERMISSION` | `string` | Any text | `''` | Permission node. Leave empty to allow everyone. |
| `CRATES.common.BROADCAST-ON-CLAIM` | `boolean` | `true`, `false` | `false` | On/off for broadcast on claim. |

#### `CRATES.common.DISPLAY`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.common.DISPLAY.MATERIAL` | `string` | Any text | `LIME_SHULKER_BOX` | Bukkit `Material` name for the icon. |
| `CRATES.common.DISPLAY.DISPLAY-NAME` | `string` | Any text | `&fCommon Crate` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.common.DISPLAY.LORE` | `list` | A list of values | _list of 2 items_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.common.DISPLAY.LORE</code> (2 items)</summary>

```yaml
LORE:
  - '&7Keys: &f{keys}'
  - '&aClick to open and choose 1 reward.'
```

</details>

#### `CRATES.common.KEY-ITEM`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.common.KEY-ITEM.MATERIAL` | `string` | Any text | `TRIPWIRE_HOOK` | Bukkit `Material` name for the icon. |
| `CRATES.common.KEY-ITEM.DISPLAY-NAME` | `string` | Any text | `&fcommon key` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.common.KEY-ITEM.LORE` | `list` | A list of values | _list of 1 item_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.common.KEY-ITEM.LORE</code> (1 item)</summary>

```yaml
LORE:
  - '&7Opens the &fCommon Crate&7.'
```

</details>

#### `CRATES.common.MENU`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.common.MENU.OPEN-TITLE` | `string` | Any text | `&8Common Crate` | Open title. |
| `CRATES.common.MENU.CONFIRM-TITLE` | `string` | Any text | `&8Confirm Reward` | Confirm title. |
| `CRATES.common.MENU.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `CRATES.common.MENU.FILLER` | `string` | Any text | `BLACK_STAINED_GLASS_PANE` | Material used to fill empty menu slots. |
| `CRATES.common.MENU.BACK-SLOT` | `integer` | Any integer | `26` | Inventory slot, `0` is the top-left cell. |

##### `CRATES.common.MENU.BACK-BUTTON`

###### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.common.MENU.BACK-BUTTON.MATERIAL` | `string` | Any text | `BARRIER` | Bukkit `Material` name for the icon. |
| `CRATES.common.MENU.BACK-BUTTON.DISPLAY-NAME` | `string` | Any text | `&cBack` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.common.MENU.BACK-BUTTON.LORE` | `list` | A list of values | _list of 1 item_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.common.MENU.BACK-BUTTON.LORE</code> (1 item)</summary>

```yaml
LORE:
  - '&7Return to the crate list.'
```

</details>

#### `CRATES.common.REWARDS`

##### Entry schema (7 entries)

Each entry under `CRATES.common.REWARDS` is keyed by a name you choose, and every entry accepts the same options:

##### Shipped entries

| Entry key | `SLOT` |
| :--- | :--- |
| `diamond_helmet` | `10` |
| `diamond_chestplate` | `11` |
| `diamond_leggings` | `12` |
| `diamond_boots` | `13` |
| `diamond_sword` | `14` |
| `diamond_pickaxe` | `15` |
| `diamond_shovel` | `16` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DISPLAY` | `section` | — | Required | Options for display, listed below. |
| `GRANT` | `section` | — | Required | Options for grant, listed below. |
| `SLOT` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

###### `CRATES.common.REWARDS.<entry>.DISPLAY`

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DISPLAY-NAME` | `string` | Any text | Required | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `ENCHANTMENTS` | `list` | A list of values | Required | The enchantments list. |
| `MATERIAL` | `string` | Any text | Required | Bukkit `Material` name for the icon. |

###### `CRATES.common.REWARDS.<entry>.GRANT`

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `AMOUNT` | `integer` | Any integer | Required | How many to give or take. |
| `DISPLAY-NAME` | `string` | Any text | Required | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `ENCHANTMENTS` | `list` | A list of values | Required | The enchantments list. |
| `MATERIAL` | `string` | Any text | Required | Bukkit `Material` name for the icon. |
| `TYPE` | `string` | Any text | Required | Which option this section uses. |

### `CRATES.prime`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.prime.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `prime` section on or off. |
| `CRATES.prime.OPEN-TYPE` | `string` | Any text | `CHOOSE_ONE` | Open type. |
| `CRATES.prime.PERMISSION` | `string` | Any text | `''` | Permission node. Leave empty to allow everyone. |
| `CRATES.prime.BROADCAST-ON-CLAIM` | `boolean` | `true`, `false` | `false` | On/off for broadcast on claim. |

#### `CRATES.prime.DISPLAY`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.prime.DISPLAY.MATERIAL` | `string` | Any text | `BLUE_SHULKER_BOX` | Bukkit `Material` name for the icon. |
| `CRATES.prime.DISPLAY.DISPLAY-NAME` | `string` | Any text | `&9Prime Crate` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.prime.DISPLAY.LORE` | `list` | A list of values | _list of 2 items_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.prime.DISPLAY.LORE</code> (2 items)</summary>

```yaml
LORE:
  - '&7Keys: &f{keys}'
  - '&aClick to open and choose 1 reward.'
```

</details>

#### `CRATES.prime.KEY-ITEM`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.prime.KEY-ITEM.MATERIAL` | `string` | Any text | `TRIPWIRE_HOOK` | Bukkit `Material` name for the icon. |
| `CRATES.prime.KEY-ITEM.DISPLAY-NAME` | `string` | Any text | `&9prime key` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.prime.KEY-ITEM.LORE` | `list` | A list of values | _list of 1 item_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.prime.KEY-ITEM.LORE</code> (1 item)</summary>

```yaml
LORE:
  - '&7Opens the &9Prime Crate&7.'
```

</details>

#### `CRATES.prime.MENU`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.prime.MENU.OPEN-TITLE` | `string` | Any text | `&8Prime Crate` | Open title. |
| `CRATES.prime.MENU.CONFIRM-TITLE` | `string` | Any text | `&8Confirm Prime Reward` | Confirm title. |
| `CRATES.prime.MENU.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `CRATES.prime.MENU.FILLER` | `string` | Any text | `BLACK_STAINED_GLASS_PANE` | Material used to fill empty menu slots. |
| `CRATES.prime.MENU.BACK-SLOT` | `integer` | Any integer | `26` | Inventory slot, `0` is the top-left cell. |

##### `CRATES.prime.MENU.BACK-BUTTON`

###### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.prime.MENU.BACK-BUTTON.MATERIAL` | `string` | Any text | `BARRIER` | Bukkit `Material` name for the icon. |
| `CRATES.prime.MENU.BACK-BUTTON.DISPLAY-NAME` | `string` | Any text | `&cBack` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.prime.MENU.BACK-BUTTON.LORE` | `list` | A list of values | _list of 1 item_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.prime.MENU.BACK-BUTTON.LORE</code> (1 item)</summary>

```yaml
LORE:
  - '&7Return to the crate list.'
```

</details>

#### `CRATES.prime.REWARDS`

##### Entry schema (7 entries)

Each entry under `CRATES.prime.REWARDS` is keyed by a name you choose, and every entry accepts the same options:

##### Shipped entries

| Entry key | `SLOT` |
| :--- | :--- |
| `prime_helmet` | `10` |
| `prime_chestplate` | `11` |
| `prime_leggings` | `12` |
| `prime_boots` | `13` |
| `prime_sword` | `14` |
| `prime_crossbow` | `15` |
| `prime_mace` | `16` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DISPLAY` | `section` | — | Required | Options for display, listed below. |
| `GRANT` | `section` | — | Required | Options for grant, listed below. |
| `SLOT` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

###### `CRATES.prime.REWARDS.<entry>.DISPLAY`

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DISPLAY-NAME` | `string` | Any text | Required | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `ENCHANTMENTS` | `list` | A list of values | Required | The enchantments list. |
| `MATERIAL` | `string` | Any text | Required | Bukkit `Material` name for the icon. |

###### `CRATES.prime.REWARDS.<entry>.GRANT`

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `AMOUNT` | `integer` | Any integer | Required | How many to give or take. |
| `DISPLAY-NAME` | `string` | Any text | Required | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `ENCHANTMENTS` | `list` | A list of values | Required | The enchantments list. |
| `MATERIAL` | `string` | Any text | Required | Bukkit `Material` name for the icon. |
| `TYPE` | `string` | Any text | Required | Which option this section uses. |

### `CRATES.crimson`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.crimson.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `crimson` section on or off. |
| `CRATES.crimson.OPEN-TYPE` | `string` | Any text | `CHOOSE_ONE` | Open type. |
| `CRATES.crimson.PERMISSION` | `string` | Any text | `''` | Permission node. Leave empty to allow everyone. |
| `CRATES.crimson.BROADCAST-ON-CLAIM` | `boolean` | `true`, `false` | `true` | On/off for broadcast on claim. |

#### `CRATES.crimson.DISPLAY`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.crimson.DISPLAY.MATERIAL` | `string` | Any text | `RED_SHULKER_BOX` | Bukkit `Material` name for the icon. |
| `CRATES.crimson.DISPLAY.DISPLAY-NAME` | `string` | Any text | `&cCrimson Crate` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.crimson.DISPLAY.LORE` | `list` | A list of values | _list of 2 items_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.crimson.DISPLAY.LORE</code> (2 items)</summary>

```yaml
LORE:
  - '&7Keys: &f{keys}'
  - '&aClick to open and choose 1 reward.'
```

</details>

#### `CRATES.crimson.KEY-ITEM`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.crimson.KEY-ITEM.MATERIAL` | `string` | Any text | `TRIPWIRE_HOOK` | Bukkit `Material` name for the icon. |
| `CRATES.crimson.KEY-ITEM.DISPLAY-NAME` | `string` | Any text | `&ccrimson key` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.crimson.KEY-ITEM.LORE` | `list` | A list of values | _list of 1 item_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.crimson.KEY-ITEM.LORE</code> (1 item)</summary>

```yaml
LORE:
  - '&7Opens the &cCrimson Crate&7.'
```

</details>

#### `CRATES.crimson.MENU`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.crimson.MENU.OPEN-TITLE` | `string` | Any text | `&8Crimson Crate` | Open title. |
| `CRATES.crimson.MENU.CONFIRM-TITLE` | `string` | Any text | `&8Confirm Crimson Reward` | Confirm title. |
| `CRATES.crimson.MENU.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `CRATES.crimson.MENU.FILLER` | `string` | Any text | `BLACK_STAINED_GLASS_PANE` | Material used to fill empty menu slots. |
| `CRATES.crimson.MENU.BACK-SLOT` | `integer` | Any integer | `26` | Inventory slot, `0` is the top-left cell. |

##### `CRATES.crimson.MENU.BACK-BUTTON`

###### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.crimson.MENU.BACK-BUTTON.MATERIAL` | `string` | Any text | `BARRIER` | Bukkit `Material` name for the icon. |
| `CRATES.crimson.MENU.BACK-BUTTON.DISPLAY-NAME` | `string` | Any text | `&cBack` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.crimson.MENU.BACK-BUTTON.LORE` | `list` | A list of values | _list of 1 item_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.crimson.MENU.BACK-BUTTON.LORE</code> (1 item)</summary>

```yaml
LORE:
  - '&7Return to the crate list.'
```

</details>

#### `CRATES.crimson.REWARDS`

##### Entry schema (7 entries)

Each entry under `CRATES.crimson.REWARDS` is keyed by a name you choose, and every entry accepts the same options:

##### Shipped entries

| Entry key | `SLOT` |
| :--- | :--- |
| `crimson_helmet` | `10` |
| `crimson_chestplate` | `11` |
| `crimson_leggings` | `12` |
| `crimson_boots` | `13` |
| `crimson_sword` | `14` |
| `crimson_axe` | `15` |
| `crimson_pickaxe` | `16` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DISPLAY` | `section` | — | Required | Options for display, listed below. |
| `GRANT` | `section` | — | Required | Options for grant, listed below. |
| `SLOT` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

###### `CRATES.crimson.REWARDS.<entry>.DISPLAY`

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DISPLAY-NAME` | `string` | Any text | Required | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `ENCHANTMENTS` | `list` | A list of values | Required | The enchantments list. |
| `MATERIAL` | `string` | Any text | Required | Bukkit `Material` name for the icon. |

###### `CRATES.crimson.REWARDS.<entry>.GRANT`

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `AMOUNT` | `integer` | Any integer | Required | How many to give or take. |
| `DISPLAY-NAME` | `string` | Any text | Required | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `ENCHANTMENTS` | `list` | A list of values | Required | The enchantments list. |
| `MATERIAL` | `string` | Any text | Required | Bukkit `Material` name for the icon. |
| `TYPE` | `string` | Any text | Required | Which option this section uses. |

### `CRATES.gold`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.gold.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `gold` section on or off. |
| `CRATES.gold.OPEN-TYPE` | `string` | Any text | `GACHA` | Open type. |
| `CRATES.gold.PERMISSION` | `string` | Any text | `''` | Permission node. Leave empty to allow everyone. |
| `CRATES.gold.BROADCAST-ON-CLAIM` | `boolean` | `true`, `false` | `true` | On/off for broadcast on claim. |

#### `CRATES.gold.DISPLAY`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.gold.DISPLAY.MATERIAL` | `string` | Any text | `YELLOW_SHULKER_BOX` | Bukkit `Material` name for the icon. |
| `CRATES.gold.DISPLAY.DISPLAY-NAME` | `string` | Any text | `&6Gold Crate` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.gold.DISPLAY.LORE` | `list` | A list of values | _list of 2 items_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.gold.DISPLAY.LORE</code> (2 items)</summary>

```yaml
LORE:
  - '&7Keys: &f{keys}'
  - '&aClick to open and choose 1 reward.'
```

</details>

#### `CRATES.gold.KEY-ITEM`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.gold.KEY-ITEM.MATERIAL` | `string` | Any text | `TRIPWIRE_HOOK` | Bukkit `Material` name for the icon. |
| `CRATES.gold.KEY-ITEM.DISPLAY-NAME` | `string` | Any text | `&6gold key` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.gold.KEY-ITEM.LORE` | `list` | A list of values | _list of 1 item_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.gold.KEY-ITEM.LORE</code> (1 item)</summary>

```yaml
LORE:
  - '&7Opens the &6Gold Crate&7.'
```

</details>

#### `CRATES.gold.MENU`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.gold.MENU.OPEN-TITLE` | `string` | Any text | `&8Gold Crate` | Open title. |
| `CRATES.gold.MENU.CONFIRM-TITLE` | `string` | Any text | `&8Confirm Gold Reward` | Confirm title. |
| `CRATES.gold.MENU.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `CRATES.gold.MENU.FILLER` | `string` | Any text | `BLACK_STAINED_GLASS_PANE` | Material used to fill empty menu slots. |
| `CRATES.gold.MENU.BACK-SLOT` | `integer` | Any integer | `26` | Inventory slot, `0` is the top-left cell. |

##### `CRATES.gold.MENU.BACK-BUTTON`

###### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CRATES.gold.MENU.BACK-BUTTON.MATERIAL` | `string` | Any text | `BARRIER` | Bukkit `Material` name for the icon. |
| `CRATES.gold.MENU.BACK-BUTTON.DISPLAY-NAME` | `string` | Any text | `&cBack` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `CRATES.gold.MENU.BACK-BUTTON.LORE` | `list` | A list of values | _list of 1 item_ | Tooltip lines under the item name. |

<details>
<summary>Default contents of <code>CRATES.gold.MENU.BACK-BUTTON.LORE</code> (1 item)</summary>

```yaml
LORE:
  - '&7Return to the crate list.'
```

</details>

#### `CRATES.gold.REWARDS`

##### Entry schema (13 entries)

Each entry under `CRATES.gold.REWARDS` is keyed by a name you choose, and every entry accepts the same options:

##### Shipped entries

| Entry key | `SLOT` |
| :--- | :--- |
| `skeleton_spawner` | `14` |
| `ultra_elytra` | `15` |
| `iron_golem_spawner` | `16` |
| `reward_13` | `13` |
| `reward_12` | `12` |
| `reward_11` | `11` |
| `reward_10` | `10` |
| `reward_3` | `3` |
| `reward_5` | `5` |
| `reward_22` | `22` |
| `reward_4` | `4` |
| `reward_23` | `23` |
| `reward_21` | `21` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DISPLAY` | `section` | — | Required | Options for display, listed below. |
| `GRANT` | `section` | — | Required | Options for grant, listed below. |
| `SLOT` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

###### `CRATES.gold.REWARDS.<entry>.DISPLAY`

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DISPLAY-NAME` | `string` | Any text | Required | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `MATERIAL` | `string` | Any text | Required | Bukkit `Material` name for the icon. |
| `AMOUNT` | `integer` | Any integer | Optional (12/13) | How many to give or take. |
| `ENCHANTMENTS` | `list` | A list of values | Optional (12/13) | The enchantments list. |
| `LORE` | `list` | A list of values | Optional (12/13) | Tooltip lines under the item name. |

###### `CRATES.gold.REWARDS.<entry>.GRANT`

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `TYPE` | `string` | Any text | Required | Which option this section uses. |
| `AMOUNT` | `integer` | Any integer | Optional (10/13) | How many to give or take. |
| `DISPLAY-NAME` | `string` | Any text | Optional (10/13) | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `ENCHANTMENTS` | `list` | A list of values | Optional (10/13) | The enchantments list. |
| `LORE` | `list` | A list of values | Optional (10/13) | Tooltip lines under the item name. |
| `MATERIAL` | `string` | Any text | Optional (10/13) | Bukkit `Material` name for the icon. |
| `REQUIRES-INVENTORY-SPACE` | `boolean` | `true`, `false` | Optional (10/13) | On/off for requires inventory space. |
| `COMMANDS` | `list` | A list of values | Optional (3/13) | The commands list. |

<details>
<summary>Default <code>CRATES</code> block as shipped</summary>

```yaml
CRATES:
  common:
    ENABLED: true
    OPEN-TYPE: CHOOSE_ONE
    DISPLAY:
      MATERIAL: LIME_SHULKER_BOX
      DISPLAY-NAME: '&fCommon Crate'
      LORE:
      - '&7Keys: &f{keys}'
      - '&aClick to open and choose 1 reward.'
    KEY-ITEM:
      MATERIAL: TRIPWIRE_HOOK
      DISPLAY-NAME: '&fcommon key'
      LORE:
      - '&7Opens the &fCommon Crate&7.'
    PERMISSION: ''
    BROADCAST-ON-CLAIM: false
    MENU:
      OPEN-TITLE: '&8Common Crate'
      CONFIRM-TITLE: '&8Confirm Reward'
      SIZE: 27
      FILLER: BLACK_STAINED_GLASS_PANE
      BACK-SLOT: 26
      BACK-BUTTON:
        MATERIAL: BARRIER
        DISPLAY-NAME: '&cBack'
        LORE:
        - '&7Return to the crate list.'
    REWARDS:
      diamond_helmet:
        SLOT: 10
        DISPLAY:
          MATERIAL: DIAMOND_HELMET
          DISPLAY-NAME: '&bDiamond Helmet'
          ENCHANTMENTS:
          - aqua_affinity:1
          - protection:1
          - respiration:2
        GRANT:
          TYPE: ITEM
          MATERIAL: DIAMOND_HELMET
          DISPLAY-NAME: '&bDiamond Helmet'
          AMOUNT: 1
          ENCHANTMENTS:
          - aqua_affinity:1
          - protection:1
          - respiration:2
      diamond_chestplate:
# ... section continues, see the file on disk for the full block
```

</details>

---

Defaults above match the file shipped in the jar.
