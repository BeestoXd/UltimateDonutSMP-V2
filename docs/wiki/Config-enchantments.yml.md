# `enchantments.yml`

Backs the enchantment picker GUI. When a player creates an order for an enchantable item,
this file decides which enchantments they may ask for, and where each one sits in the menu.

The layout is one section per item type (`helmet`, `sword`, `pickaxe`, and so on), and one
entry per selectable enchantment level. Each entry maps an `enchantment` string in
`name;level` form to a `slot` and a `page`, so a full set of five Protection levels is five
separate entries. There is no standalone command for this GUI — it is reached through
Orders, not from a `/enchant`-style command.

Note that although the language files carry a `CONFIG.ENCHANTMENTS` block, this file is
read raw and is **not** overlaid per locale, so translate the `messages` section here
rather than in `languages/`.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/enchantments.yml` |
| **Commands** | Opened from the Orders flow when a listed item accepts enchantments |
| **Player-facing text** | Not translated. Edit this file directly. |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`messages`](#section-messages) | section | 3 keys |
| [`gui`](#section-gui) | section | 3 keys |
| [`helmet`](#section-helmet) | section | 33 keys |
| [`chestplate`](#section-chestplate) | section | 29 keys |
| [`leggings`](#section-leggings) | section | 32 keys |
| [`boots`](#section-boots) | section | 41 keys |
| [`elytra`](#section-elytra) | section | 6 keys |
| [`bow`](#section-bow) | section | 14 keys |
| [`crossbow`](#section-crossbow) | section | 13 keys |
| [`fishing_rod`](#section-fishing-rod) | section | 11 keys |
| [`shovel`](#section-shovel) | section | 14 keys |
| [`pickaxe`](#section-pickaxe) | section | 14 keys |
| [`axe`](#section-axe) | section | 33 keys |
| [`hoe`](#section-hoe) | section | 14 keys |
| [`shield`](#section-shield) | section | 5 keys |
| [`sword`](#section-sword) | section | 34 keys |

---

## Section: `messages`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `messages.select` | `string` | Any text | `&fClick to select` | Select. |
| `messages.selected` | `string` | Any text | `&aSelected` | Selected. |
| `messages.cannot` | `string` | Any text | `&fCannot add this enchantment` | Cannot. |

<details>
<summary>Default <code>messages</code> block as shipped</summary>

```yaml
# Configuration section for Messages.
messages:
  # The text or value for Select. Available options: Any valid string text
  select: '&fClick to select'
  # The text or value for Selected. Available options: Any valid string text
  selected: '&aSelected'
  # The text or value for Cannot. Available options: Any valid string text
  cannot: '&fCannot add this enchantment'
```

</details>

---

## Section: `gui`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `gui.title` | `string` | Any text | `&#444444pick enchantments` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `gui.rows` | `integer` | Any integer | `6` | Menu rows. Each row is 9 slots. |

### `gui.slots`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `gui.slots.item` | `integer` | Any integer | `0` | Item. |
| `gui.slots.cancel` | `integer` | Any integer | `46` | Cancel. |
| `gui.slots.prev` | `integer` | Any integer | `45` | Prev. |
| `gui.slots.next` | `integer` | Any integer | `53` | Next. |
| `gui.slots.confirm` | `integer` | Any integer | `52` | Confirm. |

<details>
<summary>Default <code>gui</code> block as shipped</summary>

```yaml
# Configuration section for Gui.
gui:
  # The text or value for Title. Available options: Any valid string text
  title: '&#444444pick enchantments'
  # The numerical value for Rows. Available options: Any valid integer
  rows: 6
  # Configuration section for Slots.
  slots:
    # The numerical value for Item. Available options: Any valid integer
    item: 0
    # The numerical value for Cancel. Available options: Any valid integer
    cancel: 46
    # The numerical value for Prev. Available options: Any valid integer
    prev: 45
    # The numerical value for Next. Available options: Any valid integer
    next: 53
    # The numerical value for Confirm. Available options: Any valid integer
    confirm: 52
```

</details>

---

## Section: `helmet`

### Entry schema (33 entries)

Each entry under `helmet` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `protection1` | `protection;1` | `2` | `1` |
| `protection2` | `protection;2` | `3` | `1` |
| `protection3` | `protection;3` | `4` | `1` |
| `protection4` | `protection;4` | `5` | `1` |
| `fire_protection1` | `fire_protection;1` | `11` | `1` |
| `fire_protection2` | `fire_protection;2` | `12` | `1` |
| `fire_protection3` | `fire_protection;3` | `13` | `1` |
| `fire_protection4` | `fire_protection;4` | `14` | `1` |
| `mending1` | `mending;1` | `18` | `1` |
| `mending201` | `mending;1` | `18` | `2` |
| `thorns1` | `thorns;1` | `11` | `2` |
| `thorns2` | `thorns;2` | `12` | `2` |
| `thorns3` | `thorns;3` | `13` | `2` |
| `blast_protection1` | `blast_protection;1` | `20` | `1` |
| `blast_protection2` | `blast_protection;2` | `21` | `1` |
| `blast_protection3` | `blast_protection;3` | `22` | `1` |
| `blast_protection4` | `blast_protection;4` | `23` | `1` |
| `respiration1` | `respiration;1` | `38` | `1` |
| `respiration2` | `respiration;2` | `39` | `1` |
| `respiration3` | `respiration;3` | `40` | `1` |
| `aqua_affinity1` | `aqua_affinity;1` | `2` | `2` |
| `projectile_protection1` | `projectile_protection;1` | `29` | `1` |
| `projectile_protection2` | `projectile_protection;2` | `30` | `1` |
| `projectile_protection3` | `projectile_protection;3` | `31` | `1` |
| `projectile_protection4` | `projectile_protection;4` | `32` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `unbreaking201` | `unbreaking;1` | `17` | `2` |
| `unbreaking202` | `unbreaking;2` | `26` | `2` |
| `unbreaking203` | `unbreaking;3` | `35` | `2` |
| `binding_curse1` | `binding_curse;1` | `29` | `2` |
| `vanishing_curse1` | `vanishing_curse;1` | `20` | `2` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>helmet</code> block as shipped</summary>

```yaml
# Configuration section for Helmet.
helmet:
  # Configuration section for Protection1.
  protection1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection2.
  protection2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 3
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection3.
  protection3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 4
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection4.
  protection4:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;4
    # The numerical value for Slot. Available options: Any valid integer
    slot: 5
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Fire Protection1.
  fire_protection1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: fire_protection;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 11
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Fire Protection2.
  fire_protection2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: fire_protection;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 12
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `chestplate`

### Entry schema (29 entries)

Each entry under `chestplate` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `protection1` | `protection;1` | `2` | `1` |
| `protection2` | `protection;2` | `3` | `1` |
| `protection3` | `protection;3` | `4` | `1` |
| `protection4` | `protection;4` | `5` | `1` |
| `fire_protection1` | `fire_protection;1` | `11` | `1` |
| `fire_protection2` | `fire_protection;2` | `12` | `1` |
| `fire_protection3` | `fire_protection;3` | `13` | `1` |
| `fire_protection4` | `fire_protection;4` | `14` | `1` |
| `mending1` | `mending;1` | `18` | `1` |
| `mending201` | `mending;1` | `18` | `2` |
| `thorns1` | `thorns;1` | `38` | `1` |
| `thorns2` | `thorns;2` | `39` | `1` |
| `thorns3` | `thorns;3` | `40` | `1` |
| `blast_protection1` | `blast_protection;1` | `20` | `1` |
| `blast_protection2` | `blast_protection;2` | `21` | `1` |
| `blast_protection3` | `blast_protection;3` | `22` | `1` |
| `blast_protection4` | `blast_protection;4` | `23` | `1` |
| `projectile_protection1` | `projectile_protection;1` | `29` | `1` |
| `projectile_protection2` | `projectile_protection;2` | `30` | `1` |
| `projectile_protection3` | `projectile_protection;3` | `31` | `1` |
| `projectile_protection4` | `projectile_protection;4` | `32` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `unbreaking201` | `unbreaking;1` | `17` | `2` |
| `unbreaking202` | `unbreaking;2` | `26` | `2` |
| `unbreaking203` | `unbreaking;3` | `35` | `2` |
| `binding_curse1` | `binding_curse;1` | `2` | `2` |
| `vanishing_curse1` | `vanishing_curse;1` | `11` | `2` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>chestplate</code> block as shipped</summary>

```yaml
# Configuration section for Chestplate.
chestplate:
  # Configuration section for Protection1.
  protection1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection2.
  protection2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 3
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection3.
  protection3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 4
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection4.
  protection4:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;4
    # The numerical value for Slot. Available options: Any valid integer
    slot: 5
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Fire Protection1.
  fire_protection1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: fire_protection;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 11
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Fire Protection2.
  fire_protection2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: fire_protection;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 12
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `leggings`

### Entry schema (32 entries)

Each entry under `leggings` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `protection1` | `protection;1` | `2` | `1` |
| `protection2` | `protection;2` | `3` | `1` |
| `protection3` | `protection;3` | `4` | `1` |
| `protection4` | `protection;4` | `5` | `1` |
| `fire_protection1` | `fire_protection;1` | `11` | `1` |
| `fire_protection2` | `fire_protection;2` | `12` | `1` |
| `fire_protection3` | `fire_protection;3` | `13` | `1` |
| `fire_protection4` | `fire_protection;4` | `14` | `1` |
| `mending1` | `mending;1` | `18` | `1` |
| `mending201` | `mending;1` | `18` | `2` |
| `swift_sneak1` | `swift_sneak;1` | `38` | `1` |
| `swift_sneak2` | `swift_sneak;2` | `39` | `1` |
| `swift_sneak3` | `swift_sneak;3` | `40` | `1` |
| `thorns1` | `thorns;1` | `2` | `2` |
| `thorns2` | `thorns;2` | `3` | `2` |
| `thorns3` | `thorns;3` | `4` | `2` |
| `blast_protection1` | `blast_protection;1` | `20` | `1` |
| `blast_protection2` | `blast_protection;2` | `21` | `1` |
| `blast_protection3` | `blast_protection;3` | `22` | `1` |
| `blast_protection4` | `blast_protection;4` | `23` | `1` |
| `projectile_protection1` | `projectile_protection;1` | `29` | `1` |
| `projectile_protection2` | `projectile_protection;2` | `30` | `1` |
| `projectile_protection3` | `projectile_protection;3` | `31` | `1` |
| `projectile_protection4` | `projectile_protection;4` | `32` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `unbreaking201` | `unbreaking;1` | `17` | `2` |
| `unbreaking202` | `unbreaking;2` | `26` | `2` |
| `unbreaking203` | `unbreaking;3` | `35` | `2` |
| `binding_curse1` | `binding_curse;1` | `11` | `2` |
| `vanishing_curse1` | `vanishing_curse;1` | `20` | `2` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>leggings</code> block as shipped</summary>

```yaml
# Configuration section for Leggings.
leggings:
  # Configuration section for Protection1.
  protection1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection2.
  protection2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 3
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection3.
  protection3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 4
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection4.
  protection4:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;4
    # The numerical value for Slot. Available options: Any valid integer
    slot: 5
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Fire Protection1.
  fire_protection1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: fire_protection;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 11
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Fire Protection2.
  fire_protection2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: fire_protection;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 12
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `boots`

### Entry schema (41 entries)

Each entry under `boots` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `protection1` | `protection;1` | `2` | `1` |
| `protection2` | `protection;2` | `3` | `1` |
| `protection3` | `protection;3` | `4` | `1` |
| `protection4` | `protection;4` | `5` | `1` |
| `fire_protection1` | `fire_protection;1` | `11` | `1` |
| `fire_protection2` | `fire_protection;2` | `12` | `1` |
| `fire_protection3` | `fire_protection;3` | `13` | `1` |
| `fire_protection4` | `fire_protection;4` | `14` | `1` |
| `mending1` | `mending;1` | `18` | `1` |
| `mending201` | `mending;1` | `18` | `2` |
| `thorns1` | `thorns;1` | `11` | `2` |
| `thorns2` | `thorns;2` | `12` | `2` |
| `thorns3` | `thorns;3` | `13` | `2` |
| `soul_speed1` | `soul_speed;1` | `2` | `2` |
| `soul_speed2` | `soul_speed;2` | `3` | `2` |
| `soul_speed3` | `soul_speed;3` | `4` | `2` |
| `frost_walker1` | `frost_walker;1` | `29` | `2` |
| `frost_walker2` | `frost_walker;2` | `30` | `2` |
| `depth_strider1` | `depth_strider;1` | `20` | `2` |
| `depth_strider2` | `depth_strider;2` | `21` | `2` |
| `depth_strider3` | `depth_strider;3` | `22` | `2` |
| `blast_protection1` | `blast_protection;1` | `29` | `1` |
| `blast_protection2` | `blast_protection;2` | `30` | `1` |
| `blast_protection3` | `blast_protection;3` | `31` | `1` |
| `blast_protection4` | `blast_protection;4` | `32` | `1` |
| `projectile_protection1` | `projectile_protection;1` | `38` | `1` |
| `projectile_protection2` | `projectile_protection;2` | `39` | `1` |
| `projectile_protection3` | `projectile_protection;3` | `40` | `1` |
| `projectile_protection4` | `projectile_protection;4` | `41` | `1` |
| `feather_falling1` | `feather_falling;1` | `20` | `1` |
| `feather_falling2` | `feather_falling;2` | `21` | `1` |
| `feather_falling3` | `feather_falling;3` | `22` | `1` |
| `feather_falling4` | `feather_falling;4` | `23` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `unbreaking201` | `unbreaking;1` | `17` | `2` |
| `unbreaking202` | `unbreaking;2` | `26` | `2` |
| `unbreaking203` | `unbreaking;3` | `35` | `2` |
| `binding_curse1` | `binding_curse;1` | `39` | `2` |
| `vanishing_curse1` | `vanishing_curse;1` | `38` | `2` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>boots</code> block as shipped</summary>

```yaml
# Configuration section for Boots.
boots:
  # Configuration section for Protection1.
  protection1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection2.
  protection2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 3
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection3.
  protection3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 4
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Protection4.
  protection4:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: protection;4
    # The numerical value for Slot. Available options: Any valid integer
    slot: 5
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Fire Protection1.
  fire_protection1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: fire_protection;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 11
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Fire Protection2.
  fire_protection2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: fire_protection;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 12
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `elytra`

### Entry schema (6 entries)

Each entry under `elytra` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `mending1` | `mending;1` | `18` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `binding_curse1` | `binding_curse;1` | `11` | `1` |
| `vanishing_curse1` | `vanishing_curse;1` | `2` | `1` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>elytra</code> block as shipped</summary>

```yaml
# Configuration section for Elytra.
elytra:
  # Configuration section for Mending1.
  mending1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: mending;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 18
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking1.
  unbreaking1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 17
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking2.
  unbreaking2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 26
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking3.
  unbreaking3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 35
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Binding Curse1.
  binding_curse1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: binding_curse;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 11
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Vanishing Curse1.
  vanishing_curse1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: vanishing_curse;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `bow`

### Entry schema (14 entries)

Each entry under `bow` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `power1` | `power;1` | `2` | `1` |
| `power2` | `power;2` | `3` | `1` |
| `power3` | `power;3` | `4` | `1` |
| `power4` | `power;4` | `5` | `1` |
| `power5` | `power;5` | `6` | `1` |
| `punch1` | `punch;1` | `11` | `1` |
| `punch2` | `punch;2` | `12` | `1` |
| `flame` | `flame;1` | `20` | `1` |
| `infinity` | `infinity;1` | `29` | `1` |
| `mending` | `mending;1` | `18` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `vanishing_curse1` | `vanishing_curse;1` | `38` | `1` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>bow</code> block as shipped</summary>

```yaml
# Configuration section for Bow.
bow:
  # Configuration section for Power1.
  power1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: power;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Power2.
  power2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: power;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 3
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Power3.
  power3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: power;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 4
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Power4.
  power4:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: power;4
    # The numerical value for Slot. Available options: Any valid integer
    slot: 5
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Power5.
  power5:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: power;5
    # The numerical value for Slot. Available options: Any valid integer
    slot: 6
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Punch1.
  punch1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: punch;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 11
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `crossbow`

### Entry schema (13 entries)

Each entry under `crossbow` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `multishot` | `multishot;1` | `2` | `1` |
| `quick_charge1` | `quick_charge;1` | `11` | `1` |
| `quick_charge2` | `quick_charge;2` | `12` | `1` |
| `quick_charge3` | `quick_charge;3` | `13` | `1` |
| `piercing1` | `piercing;1` | `20` | `1` |
| `piercing2` | `piercing;2` | `21` | `1` |
| `piercing3` | `piercing;3` | `22` | `1` |
| `piercing4` | `piercing;4` | `23` | `1` |
| `mending` | `mending;1` | `18` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `vanishing_curse1` | `vanishing_curse;1` | `29` | `1` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>crossbow</code> block as shipped</summary>

```yaml
# Configuration section for Crossbow.
crossbow:
  # Configuration section for Multishot.
  multishot:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: multishot;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Quick Charge1.
  quick_charge1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: quick_charge;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 11
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Quick Charge2.
  quick_charge2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: quick_charge;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 12
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Quick Charge3.
  quick_charge3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: quick_charge;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 13
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Piercing1.
  piercing1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: piercing;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 20
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Piercing2.
  piercing2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: piercing;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 21
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `fishing_rod`

### Entry schema (11 entries)

Each entry under `fishing_rod` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `mending` | `mending;1` | `18` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `luck_of_the_sea1` | `luck_of_the_sea;1` | `2` | `1` |
| `luck_of_the_sea2` | `luck_of_the_sea;2` | `3` | `1` |
| `luck_of_the_sea3` | `luck_of_the_sea;3` | `4` | `1` |
| `lure1` | `lure;1` | `11` | `1` |
| `lure2` | `lure;2` | `12` | `1` |
| `lure3` | `lure;3` | `13` | `1` |
| `vanishing_curse1` | `vanishing_curse;1` | `20` | `1` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>fishing_rod</code> block as shipped</summary>

```yaml
# Configuration section for Fishing Rod.
fishing_rod:
  # Configuration section for Mending.
  mending:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: mending;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 18
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking1.
  unbreaking1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 17
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking2.
  unbreaking2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 26
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking3.
  unbreaking3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 35
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Luck Of The Sea1.
  luck_of_the_sea1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: luck_of_the_sea;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Luck Of The Sea2.
  luck_of_the_sea2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: luck_of_the_sea;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 3
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `shovel`

### Entry schema (14 entries)

Each entry under `shovel` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `mending` | `mending;1` | `18` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `efficiency1` | `efficiency;1` | `2` | `1` |
| `efficiency2` | `efficiency;2` | `3` | `1` |
| `efficiency3` | `efficiency;3` | `4` | `1` |
| `efficiency4` | `efficiency;4` | `5` | `1` |
| `efficiency5` | `efficiency;5` | `6` | `1` |
| `fortune1` | `fortune;1` | `11` | `1` |
| `fortune2` | `fortune;2` | `12` | `1` |
| `fortune3` | `fortune;3` | `13` | `1` |
| `silktouch` | `silk_touch;1` | `20` | `1` |
| `vanishing_curse1` | `vanishing_curse;1` | `29` | `1` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>shovel</code> block as shipped</summary>

```yaml
# Configuration section for Shovel.
shovel:
  # Configuration section for Mending.
  mending:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: mending;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 18
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking1.
  unbreaking1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 17
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking2.
  unbreaking2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 26
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking3.
  unbreaking3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 35
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Efficiency1.
  efficiency1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: efficiency;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Efficiency2.
  efficiency2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: efficiency;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 3
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `pickaxe`

### Entry schema (14 entries)

Each entry under `pickaxe` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `mending` | `mending;1` | `18` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `efficiency1` | `efficiency;1` | `2` | `1` |
| `efficiency2` | `efficiency;2` | `3` | `1` |
| `efficiency3` | `efficiency;3` | `4` | `1` |
| `efficiency4` | `efficiency;4` | `5` | `1` |
| `efficiency5` | `efficiency;5` | `6` | `1` |
| `fortune1` | `fortune;1` | `11` | `1` |
| `fortune2` | `fortune;2` | `12` | `1` |
| `fortune3` | `fortune;3` | `13` | `1` |
| `silktouch` | `silk_touch;1` | `20` | `1` |
| `vanishing_curse1` | `vanishing_curse;1` | `29` | `1` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>pickaxe</code> block as shipped</summary>

```yaml
# Configuration section for Pickaxe.
pickaxe:
  # Configuration section for Mending.
  mending:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: mending;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 18
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking1.
  unbreaking1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 17
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking2.
  unbreaking2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 26
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking3.
  unbreaking3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 35
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Efficiency1.
  efficiency1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: efficiency;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Efficiency2.
  efficiency2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: efficiency;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 3
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `axe`

### Entry schema (33 entries)

Each entry under `axe` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `mending` | `mending;1` | `18` | `1` |
| `mending201` | `mending;1` | `18` | `2` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `unbreaking201` | `unbreaking;1` | `17` | `2` |
| `unbreaking202` | `unbreaking;2` | `26` | `2` |
| `unbreaking203` | `unbreaking;3` | `35` | `2` |
| `sharp1` | `sharpness;1` | `2` | `1` |
| `sharp2` | `sharpness;2` | `3` | `1` |
| `sharp3` | `sharpness;3` | `4` | `1` |
| `sharp4` | `sharpness;4` | `5` | `1` |
| `sharp5` | `sharpness;5` | `6` | `1` |
| `efficiency1` | `efficiency;1` | `11` | `1` |
| `efficiency2` | `efficiency;2` | `12` | `1` |
| `efficiency3` | `efficiency;3` | `13` | `1` |
| `efficiency4` | `efficiency;4` | `14` | `1` |
| `efficiency5` | `efficiency;5` | `15` | `1` |
| `Smite1` | `smite;1` | `38` | `1` |
| `Smite2` | `smite;2` | `39` | `1` |
| `Smite3` | `smite;3` | `40` | `1` |
| `Smite4` | `smite;4` | `41` | `1` |
| `Smite5` | `smite;5` | `42` | `1` |
| `bane1` | `bane_of_arthropods;1` | `2` | `2` |
| `bane2` | `bane_of_arthropods;2` | `3` | `2` |
| `bane3` | `bane_of_arthropods;3` | `4` | `2` |
| `bane4` | `bane_of_arthropods;4` | `5` | `2` |
| `bane5` | `bane_of_arthropods;5` | `6` | `2` |
| `fortune1` | `fortune;1` | `20` | `1` |
| `fortune2` | `fortune;2` | `21` | `1` |
| `fortune3` | `fortune;3` | `22` | `1` |
| `silktouch` | `silk_touch;1` | `29` | `1` |
| `vanishing_curse1` | `vanishing_curse;1` | `11` | `2` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>axe</code> block as shipped</summary>

```yaml
# Configuration section for Axe.
axe:
  # Configuration section for Mending.
  mending:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: mending;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 18
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Mending201.
  mending201:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: mending;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 18
    # The numerical value for Page. Available options: Any valid integer
    page: 2
  # Configuration section for Unbreaking1.
  unbreaking1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 17
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking2.
  unbreaking2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 26
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking3.
  unbreaking3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 35
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking201.
  unbreaking201:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 17
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `hoe`

### Entry schema (14 entries)

Each entry under `hoe` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `mending` | `mending;1` | `18` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `efficiency1` | `efficiency;1` | `2` | `1` |
| `efficiency2` | `efficiency;2` | `3` | `1` |
| `efficiency3` | `efficiency;3` | `4` | `1` |
| `efficiency4` | `efficiency;4` | `5` | `1` |
| `efficiency5` | `efficiency;5` | `6` | `1` |
| `fortune1` | `fortune;1` | `11` | `1` |
| `fortune2` | `fortune;2` | `12` | `1` |
| `fortune3` | `fortune;3` | `13` | `1` |
| `silktouch` | `silk_touch;1` | `20` | `1` |
| `vanishing_curse1` | `vanishing_curse;1` | `29` | `1` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>hoe</code> block as shipped</summary>

```yaml
# Configuration section for Hoe.
hoe:
  # Configuration section for Mending.
  mending:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: mending;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 18
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking1.
  unbreaking1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 17
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking2.
  unbreaking2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 26
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking3.
  unbreaking3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 35
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Efficiency1.
  efficiency1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: efficiency;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Efficiency2.
  efficiency2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: efficiency;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 3
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `shield`

### Entry schema (5 entries)

Each entry under `shield` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `mending` | `mending;1` | `18` | `1` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `vanishing_curse1` | `vanishing_curse;1` | `2` | `1` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>shield</code> block as shipped</summary>

```yaml
# Configuration section for Shield.
shield:
  # Configuration section for Mending.
  mending:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: mending;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 18
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking1.
  unbreaking1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 17
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking2.
  unbreaking2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 26
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking3.
  unbreaking3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 35
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Vanishing Curse1.
  vanishing_curse1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: vanishing_curse;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 2
    # The numerical value for Page. Available options: Any valid integer
    page: 1
```

</details>

---

## Section: `sword`

### Entry schema (34 entries)

Each entry under `sword` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key | `enchantment` | `slot` | `page` |
| :--- | :--- | :--- | :--- |
| `mending` | `mending;1` | `18` | `1` |
| `mending201` | `mending;1` | `18` | `2` |
| `unbreaking1` | `unbreaking;1` | `17` | `1` |
| `unbreaking2` | `unbreaking;2` | `26` | `1` |
| `unbreaking3` | `unbreaking;3` | `35` | `1` |
| `unbreaking201` | `unbreaking;1` | `17` | `2` |
| `unbreaking202` | `unbreaking;2` | `26` | `2` |
| `unbreaking203` | `unbreaking;3` | `35` | `2` |
| `looting1` | `looting;1` | `11` | `1` |
| `looting2` | `looting;2` | `12` | `1` |
| `looting3` | `looting;3` | `13` | `1` |
| `sweeping_edge1` | `sweeping_edge;1` | `20` | `1` |
| `sweeping_edge2` | `sweeping_edge;2` | `21` | `1` |
| `sweeping_edge3` | `sweeping_edge;3` | `22` | `1` |
| `knockback1` | `knockback;1` | `29` | `1` |
| `knockback2` | `knockback;2` | `30` | `1` |
| `fire_aspect1` | `fire_aspect;1` | `38` | `1` |
| `fire_aspect2` | `fire_aspect;2` | `39` | `1` |
| `sharp1` | `sharpness;1` | `2` | `1` |
| `sharp2` | `sharpness;2` | `3` | `1` |
| `sharp3` | `sharpness;3` | `4` | `1` |
| `sharp4` | `sharpness;4` | `5` | `1` |
| `sharp5` | `sharpness;5` | `6` | `1` |
| `Smite1` | `smite;1` | `2` | `2` |
| `Smite2` | `smite;2` | `3` | `2` |
| `Smite3` | `smite;3` | `4` | `2` |
| `Smite4` | `smite;4` | `5` | `2` |
| `Smite5` | `smite;5` | `6` | `2` |
| `bane1` | `bane_of_arthropods;1` | `11` | `2` |
| `bane2` | `bane_of_arthropods;2` | `12` | `2` |
| `bane3` | `bane_of_arthropods;3` | `13` | `2` |
| `bane4` | `bane_of_arthropods;4` | `14` | `2` |
| `bane5` | `bane_of_arthropods;5` | `15` | `2` |
| `vanishing_curse1` | `vanishing_curse;1` | `20` | `2` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `enchantment` | `string` | Any text | Required | Enchantment. |
| `page` | `integer` | Any integer | Required | Page. |
| `slot` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

<details>
<summary>Default <code>sword</code> block as shipped</summary>

```yaml
# Configuration section for Sword.
sword:
  # Configuration section for Mending.
  mending:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: mending;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 18
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Mending201.
  mending201:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: mending;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 18
    # The numerical value for Page. Available options: Any valid integer
    page: 2
  # Configuration section for Unbreaking1.
  unbreaking1:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 17
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking2.
  unbreaking2:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;2
    # The numerical value for Slot. Available options: Any valid integer
    slot: 26
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking3.
  unbreaking3:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;3
    # The numerical value for Slot. Available options: Any valid integer
    slot: 35
    # The numerical value for Page. Available options: Any valid integer
    page: 1
  # Configuration section for Unbreaking201.
  unbreaking201:
    # The text or value for Enchantment. Available options: Any valid string text
    enchantment: unbreaking;1
    # The numerical value for Slot. Available options: Any valid integer
    slot: 17
# ... section continues, see the file on disk for the full block
```

</details>

---

Defaults above match the file shipped in the jar.
