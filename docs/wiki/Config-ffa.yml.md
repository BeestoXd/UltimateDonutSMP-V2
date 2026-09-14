# `ffa.yml`

Instanced free-for-all arenas. Unlike the ranked arena in `pvp.yml`, FFA arenas are
disposable: players drop into an instance, fight, and the arena is rolled back afterwards.

`ROLLBACK` controls that restoration, `PLAYER_STATE` decides what players arrive with and
what they get back on leaving, and `RULES` holds the in-arena restrictions. Keep
`PLAYER_STATE` in mind when changing anything else here — it is what guarantees a player's
survival inventory is not affected by what happens inside the arena.

Comment at the top of the shipped file:

```text
UltimateDonutSMP2 - Free For All (FFA) Configuration
```

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/ffa.yml` |
| **Commands** | `/ffa`, `/ffaarena`, `/ffastats` |
| **Player-facing text** | Edit `CONFIG.FFA` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`SETTINGS`](#section-settings) | section | 2 keys |
| [`ROLLBACK`](#section-rollback) | section | 7 keys |
| [`PLAYER_STATE`](#section-player-state) | section | 3 keys |
| [`RULES`](#section-rules) | section | 3 keys |
| [`RESULT-TITLES`](#section-result-titles) | section | 2 keys |
| [`ARENA_SETTINGS`](#section-arena-settings) | section | 0 keys |

---

## Section: `SETTINGS`

General FFA arena settings

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.ENABLED` | `boolean` | `true`, `false` | `true` | Enable or disable the FFA system globally (true / false) |
| `SETTINGS.RETURN_DELAY_SECONDS` | `integer` | Any integer | `3` | Delay before teleporting players back after leaving/dying in FFA (in seconds) |

<details>
<summary>Default <code>SETTINGS</code> block as shipped</summary>

```yaml
# General FFA arena settings
SETTINGS:
  # Enable or disable the FFA system globally (true / false)
  ENABLED: true
  # Delay before teleporting players back after leaving/dying in FFA (in seconds)
  RETURN_DELAY_SECONDS: 3
```

</details>

---

## Section: `ROLLBACK`

Arena Rollback and restoration settings

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ROLLBACK.ENABLED` | `boolean` | `true`, `false` | `true` | Enable arena block rollback and cleanup after FFA activity (true / false) |
| `ROLLBACK.PADDING_HORIZONTAL` | `integer` | Any integer | `8` | Extra horizontal padding blocks preserved around FFA arena (in blocks) |
| `ROLLBACK.PADDING_VERTICAL` | `integer` | Any integer | `6` | Extra vertical padding blocks preserved around FFA arena (in blocks) |
| `ROLLBACK.TIMEOUT_SECONDS` | `integer` | Any integer | `10` | Delay in seconds before starting block rollback |
| `ROLLBACK.CLEANUP_PROJECTILES` | `boolean` | `true`, `false` | `true` | Automatically remove projectiles (arrows, snowballs) in FFA arena (true / false) |
| `ROLLBACK.CLEANUP_DROPS` | `boolean` | `true`, `false` | `true` | Automatically remove dropped item entities in FFA arena (true / false) |
| `ROLLBACK.CLEANUP_FIRE_AND_FLUIDS` | `boolean` | `true`, `false` | `true` | Automatically extinguish fires and drain fluid blocks placed during FFA (true / false) |

<details>
<summary>Default <code>ROLLBACK</code> block as shipped</summary>

```yaml
# Arena Rollback and restoration settings
ROLLBACK:
  # Enable arena block rollback and cleanup after FFA activity (true / false)
  ENABLED: true
  # Extra horizontal padding blocks preserved around FFA arena (in blocks)
  PADDING_HORIZONTAL: 8
  # Extra vertical padding blocks preserved around FFA arena (in blocks)
  PADDING_VERTICAL: 6
  # Delay in seconds before starting block rollback
  TIMEOUT_SECONDS: 10
  # Automatically remove projectiles (arrows, snowballs) in FFA arena (true / false)
  CLEANUP_PROJECTILES: true
  # Automatically remove dropped item entities in FFA arena (true / false)
  CLEANUP_DROPS: true
  # Automatically extinguish fires and drain fluid blocks placed during FFA (true / false)
  CLEANUP_FIRE_AND_FLUIDS: true
```

</details>

---

## Section: `PLAYER_STATE`

Player state management upon entering and exiting FFA

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `PLAYER_STATE.RESTORE_INVENTORY` | `boolean` | `true`, `false` | `true` | Restore player inventory upon exiting FFA match (true / false) |
| `PLAYER_STATE.RESTORE_HEALTH` | `boolean` | `true`, `false` | `true` | Restore health to max upon exiting FFA match (true / false) |
| `PLAYER_STATE.RESTORE_EFFECTS` | `boolean` | `true`, `false` | `true` | Clear active potion effects upon entering/exiting FFA (true / false) |

<details>
<summary>Default <code>PLAYER_STATE</code> block as shipped</summary>

```yaml
# Player state management upon entering and exiting FFA
PLAYER_STATE:
  # Restore player inventory upon exiting FFA match (true / false)
  RESTORE_INVENTORY: true
  # Restore health to max upon exiting FFA match (true / false)
  RESTORE_HEALTH: true
  # Clear active potion effects upon entering/exiting FFA (true / false)
  RESTORE_EFFECTS: true
```

</details>

---

## Section: `RULES`

Match rules and restrictions

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RULES.BLOCK_COMMANDS` | `boolean` | `true`, `false` | `true` | Block general player commands while inside FFA arena (true / false) |
| `RULES.COUNT_TOWARD_GLOBAL_STATS` | `boolean` | `true`, `false` | `false` | Whether FFA kills/deaths count toward global player statistics (true / false) |
| `RULES.GIVE_SURVIVAL_REWARDS` | `boolean` | `true`, `false` | `false` | Whether survival rewards (money/shards) are granted for kills in FFA (true / false) |

<details>
<summary>Default <code>RULES</code> block as shipped</summary>

```yaml
# Match rules and restrictions
RULES:
  # Block general player commands while inside FFA arena (true / false)
  BLOCK_COMMANDS: true
  # Whether FFA kills/deaths count toward global player statistics (true / false)
  COUNT_TOWARD_GLOBAL_STATS: false
  # Whether survival rewards (money/shards) are granted for kills in FFA (true / false)
  GIVE_SURVIVAL_REWARDS: false
```

</details>

---

## Section: `RESULT-TITLES`

End match screen titles and subtitles

### `RESULT-TITLES.victory`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RESULT-TITLES.victory.title` | `string` | Any text | `&e&lVICTORY!` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `RESULT-TITLES.victory.subtitle` | `string` | Any text | `&e&lt;player&gt; &fwon the FFA Match!` | Subtitle. |

### `RESULT-TITLES.defeat`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RESULT-TITLES.defeat.title` | `string` | Any text | `&c&lDEFEAT!` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `RESULT-TITLES.defeat.subtitle` | `string` | Any text | `&c&lt;opponent&gt; &feliminated you!` | Subtitle. |

<details>
<summary>Default <code>RESULT-TITLES</code> block as shipped</summary>

```yaml
# End match screen titles and subtitles
RESULT-TITLES:
  victory:
    title: '&e&lVICTORY!'
    subtitle: '&e<player> &fwon the FFA Match!'
  defeat:
    title: '&c&lDEFEAT!'
    subtitle: '&c<opponent> &feliminated you!'
```

</details>

---

## Section: `ARENA_SETTINGS`

Configuration for static FFA arena definitions (managed via /ffa commands)

This section ships empty. The plugin fills it in as you create entries in-game, so there is nothing to configure by hand here.

<details>
<summary>Default <code>ARENA_SETTINGS</code> block as shipped</summary>

```yaml
# Configuration for static FFA arena definitions (managed via /ffa commands)
ARENA_SETTINGS: {}
```

</details>

---

Defaults above match the file shipped in the jar.
