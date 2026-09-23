# `config.yml`

The main configuration file, and the one you will spend the most time in. It carries the
server-wide settings that do not deserve a file of their own: language selection, stored
locations, the feature toggle table, chat, combat and crystal behaviour, teleport
cooldowns, currency formatting, the tab list, performance tuning, and much more.

Two sections deserve attention before you change anything else:

- **`LANGUAGE`** picks the active locale from `languages/`. `LANGUAGE.ACTIVE` selects the
  file, `LANGUAGE.FALLBACK` is used for any key the active locale is missing.
- **`FEATURES`** / **`FEATURES_SETTINGS`** switch whole subsystems on and off.
  `FeatureManager` reads `FEATURES.<KEY>.ENABLED` first and falls back to the older
  `COMMANDS.<KEY>` boolean, so both styles work. `FEATURES_SETTINGS.DISABLED_COMMAND_ACTION`
  decides what a disabled command does: send a `MESSAGE`, pretend it is `UNKNOWN`, or
  `UNREGISTER` it from the server entirely.

On reload the plugin merges any keys that are new in the jar into your file rather than
overwriting it, and drops a copy of the previous version into `config-backups/<timestamp>/`.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/config.yml` |
| **Commands** | `/ultimatedonutsmp2 reload`, `/ultimatedonutsmp2 features`, `/ultimatedonutsmp2 setup` |
| **Player-facing text** | Not translated. Edit this file directly. |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`LANGUAGE`](#section-language) | section | 2 keys |
| [`LOCATIONS`](#section-locations) | section | 2 keys |
| [`PORTAL-SYSTEM`](#section-portal-system) | section | 5 keys |
| [`SETTINGS`](#section-settings) | section | 20 keys |
| [`FEATURES_SETTINGS`](#section-features-settings) | section | 1 keys |
| [`CHAT`](#section-chat) | section | 11 keys |
| [`SERVER-NOTIFICATIONS`](#section-server-notifications) | section | 5 keys |
| [`AFK-SYSTEM`](#section-afk-system) | section | 5 keys |
| [`PREVENT-ITEM-DROP`](#section-prevent-item-drop) | section | 4 keys |
| [`CUBOID-BINDS`](#section-cuboid-binds) | section | 2 keys |
| [`FLY-SYSTEM`](#section-fly-system) | section | 4 keys |
| [`WORTH-LORE`](#section-worth-lore) | section | 2 keys |
| [`MONEY-NAMETAGS`](#section-money-nametags) | section | 4 keys |
| [`END-CRYSTAL`](#section-end-crystal) | section | 2 keys |
| [`FAST-CRYSTALS`](#section-fast-crystals) | section | 5 keys |
| [`RESPAWN-ANCHOR`](#section-respawn-anchor) | section | 2 keys |
| [`BOSS-SOUNDS`](#section-boss-sounds) | section | 4 keys |
| [`ENDER-CHEST`](#section-ender-chest) | section | 1 keys |
| [`LUNAR-CLIENT`](#section-lunar-client) | section | 2 keys |
| [`SHARDS`](#section-shards) | section | 12 keys |
| [`KEY-ALL`](#section-key-all) | section | 7 keys |
| [`TEAM`](#section-team) | section | 3 keys |
| [`LEADERBOARD`](#section-leaderboard) | section | 2 keys |
| [`TABLIST`](#section-tablist) | section | 13 keys |
| [`SERVER-LIST`](#section-server-list) | section | 2 keys |
| [`OPTIMIZATION`](#section-optimization) | section | 10 keys |
| [`JOIN-WARMUP`](#section-join-warmup) | section | 15 keys |
| [`ENTITY-PRESSURE`](#section-entity-pressure) | section | 4 keys |
| [`CLEAR-LAG`](#section-clear-lag) | section | 12 keys |
| [`COMBAT-MANAGER`](#section-combat-manager) | section | 11 keys |
| [`RTP-ZONE`](#section-rtp-zone) | section | 10 keys |
| [`FIRST-JOIN-RTP`](#section-first-join-rtp) | section | 6 keys |
| [`RESPAWN-RTP`](#section-respawn-rtp) | section | 5 keys |
| [`TELEPORT-COOLDOWN`](#section-teleport-cooldown) | section | 8 keys |
| [`BOUNTY`](#section-bounty) | section | 1 keys |
| [`VOICE-CHAT`](#section-voice-chat) | section | 3 keys |
| [`FEATURES`](#section-features) | section | 1 keys |
| [`COMMANDS`](#section-commands) | section | 31 keys |

---

## Section: `LANGUAGE`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `LANGUAGE.ACTIVE` | `string` | Any text | `en_US` | Active. |
| `LANGUAGE.FALLBACK` | `string` | Any text | `en_US` | Supplies any message the active language is missing |

<details>
<summary>Default <code>LANGUAGE</code> block as shipped</summary>

```yaml
# Configuration section for Language.
LANGUAGE:
  # The language players see, taken from the matching file in the languages folder
  # Bundled locales: en_US, es_ES, id_ID, pt_BR, de_DE, fr_FR, ru_RU, zh_CN
  # Names such as Bahasa Indonesia, Spanish or pt-BR resolve to those, and a custom
  # file dropped into the languages folder can be selected by its file name
  # Available options: Any valid string text
  ACTIVE: en_US
  # Supplies any message the active language is missing
  # Available options: Any valid string text
  FALLBACK: en_US
```

</details>

---

## Section: `LOCATIONS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `LOCATIONS.SPAWN-LOCATION` | `string` | Any text | `''` | Spawn location. |
| `LOCATIONS.AFK-LOCATION` | `string` | Any text | `''` | Afk location. |

<details>
<summary>Default <code>LOCATIONS</code> block as shipped</summary>

```yaml
# Configuration section for Locations.
LOCATIONS:
  # The text or value for Spawn Location. Available options: Any valid string text
  SPAWN-LOCATION: ''
  # The text or value for Afk Location. Available options: Any valid string text
  AFK-LOCATION: ''
```

</details>

---

## Section: `PORTAL-SYSTEM`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `PORTAL-SYSTEM.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `PORTAL-SYSTEM` section on or off. |
| `PORTAL-SYSTEM.BLOCK-IN-COMBAT` | `boolean` | `true`, `false` | `true` | On/off for block in combat. |
| `PORTAL-SYSTEM.DEFAULT-TRIGGER-COOLDOWN-MS` | `integer` | Any integer | `1500` | Default trigger cooldown ms. Milliseconds. |
| `PORTAL-SYSTEM.POST-TELEPORT-GRACE-MS` | `integer` | Any integer | `2000` | Post teleport grace ms. Milliseconds. |

### `PORTAL-SYSTEM.HOLOGRAM`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `PORTAL-SYSTEM.HOLOGRAM.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `HOLOGRAM` section on or off. |
| `PORTAL-SYSTEM.HOLOGRAM.DEFAULT-REGION` | `string` | Any text | `NA East` | Default region. |
| `PORTAL-SYSTEM.HOLOGRAM.DEFAULT-SERVER-ID` | `string` | Any text | `''` | Default server id. |
| `PORTAL-SYSTEM.HOLOGRAM.PORTALS` | `empty` | — | _(empty)_ | Portals. |
| `PORTAL-SYSTEM.HOLOGRAM.OFFSET-Y` | `decimal` | Any decimal number | `1.2` | Offset y. |
| `PORTAL-SYSTEM.HOLOGRAM.SET-HERE-OFFSET-Y` | `decimal` | Any decimal number | `1.6` | Set here offset y. |
| `PORTAL-SYSTEM.HOLOGRAM.LINE-SPACING` | `decimal` | Any decimal number | `0.27` | Line spacing. |
| `PORTAL-SYSTEM.HOLOGRAM.UPDATE-TICKS` | `integer` | Any integer | `40` | Update ticks. Ticks (20 = 1 second). |
| `PORTAL-SYSTEM.HOLOGRAM.LINES` | `list` | A list of values | _list of 4 items_ | The lines list. |

<details>
<summary>Default contents of <code>PORTAL-SYSTEM.HOLOGRAM.LINES</code> (4 items)</summary>

```yaml
LINES:
  - '&f{portal}'
  - '&7Region {region}'
  - ''
  - '&f<total_player> Players'
```

</details>

<details>
<summary>Default <code>PORTAL-SYSTEM</code> block as shipped</summary>

```yaml
# Configuration section for Portal System.
PORTAL-SYSTEM:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # Determines whether Block In Combat is enabled or disabled. Available options: true, false
  BLOCK-IN-COMBAT: true
  # The numerical value for Default Trigger Cooldown Ms. Available options: Any valid integer
  DEFAULT-TRIGGER-COOLDOWN-MS: 1500
  # The numerical value for Post Teleport Grace Ms. Available options: Any valid integer
  POST-TELEPORT-GRACE-MS: 2000
  # Configuration section for Hologram.
  HOLOGRAM:
    # Determines whether Enabled is enabled or disabled. Available options: true, false
    ENABLED: true
    # The text or value for Default Region. Available options: Any valid string text
    DEFAULT-REGION: NA East
    # The text or value for Default Server Id. Available options: Any valid string text
    DEFAULT-SERVER-ID: ''
    # The text or value for Portals. Available options: Any valid string text
    PORTALS: null
    # The decimal value for Offset Y. Available options: Any decimal number
    OFFSET-Y: 1.2
    # The decimal value for Set Here Offset Y. Available options: Any decimal number
    SET-HERE-OFFSET-Y: 1.6
    # The decimal value for Line Spacing. Available options: Any decimal number
    LINE-SPACING: 0.27
    # The numerical value for Update Ticks. Available options: Any valid integer
    UPDATE-TICKS: 40
    # Configuration section for Lines.
    LINES:
    - '&f{portal}'
    - '&7Region {region}'
    - ''
    - '&f<total_player> Players'
```

</details>

---

## Section: `SETTINGS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.RESPAWN-ON-BED` | `boolean` | `true`, `false` | `false` | On/off for respawn on bed. |
| `SETTINGS.HOME-DEFAULT` | `integer` | Any integer | `3` | Homes every player gets when no HOME-PERMISSIONS entry applies to them Raise the HOME-PERMISSIONS entries below to hand out extra homes as a rank perk |
| `SETTINGS.SHARDS-PER-KILL` | `integer` | Any integer | `1` | Shards per kill. |
| `SETTINGS.SHARDS-KILL-MESSAGE` | `string` | Any text | `&#A303F9+{shards} Shard` | Shards kill message. |
| `SETTINGS.SHARDS-KILL-MESSAGE-BOOSTED` | `string` | Any text | `&#A303F9+{shards} Shards &7(&ax{multiplier}&7)` | Shards kill message boosted. |
| `SETTINGS.SHARDS-KILL-COOLDOWN-SECONDS` | `integer` | Any integer | `600` | Shards kill cooldown seconds. Seconds. |
| `SETTINGS.SHARDS-KILL-COOLDOWN-MESSAGE` | `string` | Any text | `&cNo Shard &7(killed recently, {time} left)` | Shards kill cooldown message. |
| `SETTINGS.MONEY-PER-DEFAULT` | `decimal` | Any decimal number | `1000.0` | Money per default. |
| `SETTINGS.SELL-MESSAGE` | `string` | Any text | `&a+$%price%` | Sell message. |
| `SETTINGS.SPAWN-MENU` | `boolean` | `true`, `false` | `true` | On/off for spawn menu. |
| `SETTINGS.AFK-MENU` | `boolean` | `true`, `false` | `true` | On/off for afk menu. |
| `SETTINGS.TELEPORT-SPAWN-ON-FIRST-JOIN` | `boolean` | `true`, `false` | `true` | Determines whether players are teleported to the spawn location the first time they join the server. Ignored while First Join Rtp Enabled is true. |
| `SETTINGS.FIRST-JOIN-SPAWN-DELAY-TICKS` | `integer` | Any integer | `20` | First join spawn delay ticks. Ticks (20 = 1 second). |
| `SETTINGS.WORTH-DEFAULT-VALUE` | `decimal` | Any decimal number | `1.0` | Worth default value. |
| `SETTINGS.MOB-SPAWN-RADIUS` | `integer` | Any integer | `50` | Mob spawn radius. Blocks. |
| `SETTINGS.PHANTOM-SPAWN-RADIUS` | `integer` | Any integer | `40` | Phantom spawn radius. Blocks. |
| `SETTINGS.DISABLE-MOB-SPAWN-LIMIT-SECONDS` | `integer` | Any integer | `-1` | Disable mob spawn limit seconds. Seconds. |
| `SETTINGS.DISABLE-PHANTOM-SPAWN-LIMIT-SECONDS` | `integer` | Any integer | `3600` | Disable phantom spawn limit seconds. Seconds. |
| `SETTINGS.MOB-SPAWN-TOGGLE-BLOCKS-TRIAL-SPAWNERS` | `boolean` | `true`, `false` | `false` | Determines whether the per player mob spawn toggle also stops trial spawners in trial chambers. Leave it false so the chamber still has to be fought. A trial spawner ejects its rewards once the mobs it released are gone, so blocking those spawns hands out the loot for free. |

### `SETTINGS.HOME-PERMISSIONS`

Per-rank home limits resolved from permissions

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.HOME-PERMISSIONS.ENABLED` | `boolean` | `true`, `false` | `true` | Enable or disable permission based home limits |

#### `SETTINGS.HOME-PERMISSIONS.PERMISSIONS`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.donutplusplusplus` | `integer` | Any integer | `90` | Ultimatedonutsmp2.donutplusplusplus. |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.donutplusplusplus` | `integer` | Any integer | `90` | Donutplusplusplus. |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.homes.donutplusplusplus` | `integer` | Any integer | `90` | Ultimatedonutsmp2.homes.donutplusplusplus. |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.donutplusplus` | `integer` | Any integer | `27` | Ultimatedonutsmp2.donutplusplus. |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.donutplusplus` | `integer` | Any integer | `27` | Donutplusplus. |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.homes.donutplusplus` | `integer` | Any integer | `27` | Ultimatedonutsmp2.homes.donutplusplus. |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.donutplus` | `integer` | Any integer | `9` | Ultimatedonutsmp2.donutplus. |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.donutplus` | `integer` | Any integer | `9` | Donutplus. |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.homes.donutplus` | `integer` | Any integer | `9` | Ultimatedonutsmp2.homes.donutplus. |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.homes.vip++` | `integer` | Any integer | `90` | Ultimatedonutsmp2.homes.vip++. |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.homes.vip+` | `integer` | Any integer | `27` | Ultimatedonutsmp2.homes.vip+. |
| `SETTINGS.HOME-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.homes.vip` | `integer` | Any integer | `9` | Ultimatedonutsmp2.homes.vip. |

<details>
<summary>Default <code>SETTINGS</code> block as shipped</summary>

```yaml
# Configuration section for Settings.
SETTINGS:
  # Determines whether Respawn On Bed is enabled or disabled. Available options: true, false
  RESPAWN-ON-BED: false
  # Homes every player gets when no HOME-PERMISSIONS entry applies to them
  # Raise the HOME-PERMISSIONS entries below to hand out extra homes as a rank perk
  # Available options: Any valid integer
  HOME-DEFAULT: 3
  # Per-rank home limits resolved from permissions
  HOME-PERMISSIONS:
    # Enable or disable permission based home limits
    ENABLED: true
    # Explicit mapping from permission node to home limit
    # Players can also be given ultimatedonutsmp2.homes.<1-100> directly, for example
    # ultimatedonutsmp2.homes.10 for ten homes, or ultimatedonutsmp2.homes.page.<1-100> to
    # hand out whole pages of five at a time
    # Players with Donut+, Donut++, or Donut+++ automatically receive their configured home slots
    # The highest value the player has wins
    # Players without any of these permissions keep HOME-DEFAULT above
    PERMISSIONS:
      "ultimatedonutsmp2.donutplusplusplus": 90
      "donutplusplusplus": 90
      "ultimatedonutsmp2.homes.donutplusplusplus": 90
      "ultimatedonutsmp2.donutplusplus": 27
      "donutplusplus": 27
      "ultimatedonutsmp2.homes.donutplusplus": 27
      "ultimatedonutsmp2.donutplus": 9
      "donutplus": 9
      "ultimatedonutsmp2.homes.donutplus": 9
      "ultimatedonutsmp2.homes.vip++": 90
      "ultimatedonutsmp2.homes.vip+": 27
      "ultimatedonutsmp2.homes.vip": 9
  # The numerical value for Shards Per Kill. Available options: Any valid integer
  SHARDS-PER-KILL: 1
  # The text or value for Shards Kill Message. Available options: Any valid string text
  SHARDS-KILL-MESSAGE: '&#A303F9+{shards} Shard'
  # The text or value for Shards Kill Message Boosted, shown instead of Shards Kill
  # Message while a shard booster multiplies the kill reward. Supports {multiplier}.
  # Available options: Any valid string text
  SHARDS-KILL-MESSAGE-BOOSTED: '&#A303F9+{shards} Shards &7(&ax{multiplier}&7)'
  # The numerical value for Shards Kill Cooldown Seconds. Blocks repeated kill rewards
  # against the same victim until the cooldown expires. Set to 0 to disable.
  # Available options: Any valid integer
  SHARDS-KILL-COOLDOWN-SECONDS: 600
  # The text or value for Shards Kill Cooldown Message, shown when the kill reward is
  # skipped because the same victim was killed recently. Leave empty to stay silent.
  # Available options: Any valid string text
  SHARDS-KILL-COOLDOWN-MESSAGE: '&cNo Shard &7(killed recently, {time} left)'
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `FEATURES_SETTINGS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `FEATURES_SETTINGS.DISABLED_COMMAND_ACTION` | `string` | Any text | `MESSAGE` | Disabled command action. |

<details>
<summary>Default <code>FEATURES_SETTINGS</code> block as shipped</summary>

```yaml
# Configuration section for Features.
FEATURES_SETTINGS:
  # Action when executing a command linked to a disabled feature.
  # Options:
  # - "MESSAGE": Shows "The <feature> feature is currently disabled."
  # - "UNKNOWN": Shows default unknown command message.
  # - "UNREGISTER": Dynamically unregister command from Bukkit command map.
  DISABLED_COMMAND_ACTION: "MESSAGE"
```

</details>

---

## Section: `CHAT`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHAT.FORMAT-ENABLED` | `boolean` | `true`, `false` | `true` | On/off for format enabled. |
| `CHAT.FORMAT` | `string` | Any text | `&f%prefix%%player%&7: &f%message%` | How the value is printed. Placeholders are listed on the feature page. |
| `CHAT.GLOBAL-CHAT-MUTED` | `boolean` | `true`, `false` | `false` | On/off for global chat muted. |
| `CHAT.GLOBAL-CHAT-DELAY-ENABLED` | `boolean` | `true`, `false` | `false` | On/off for global chat delay enabled. |
| `CHAT.GLOBAL-CHAT-DELAY` | `integer` | Any integer | `3` | Global chat delay. |
| `CHAT.MAX-DELAY-SECONDS` | `integer` | Any integer | `30` | Max delay seconds. Seconds. |
| `CHAT.CLEAR-LINES` | `integer` | Any integer | `150` | Clear lines. |

### `CHAT.MESSAGE-COLORS`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHAT.MESSAGE-COLORS.default` | `string` | Any text | `&f` | Default. |
| `CHAT.MESSAGE-COLORS.owner` | `string` | Any text | `&#0000FF` | Owner. |

### `CHAT.CLICKABLE-NAME`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHAT.CLICKABLE-NAME.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `CLICKABLE-NAME` section on or off. |
| `CHAT.CLICKABLE-NAME.HOVER-TEXT` | `list` | A list of values | _list of 9 items_ | The hover text list. |
| `CHAT.CLICKABLE-NAME.SUGGEST-COMMAND` | `string` | Any text | `/msg &lt;player&gt; ` | Console command, without a leading slash. Empty means none. |

<details>
<summary>Default contents of <code>CHAT.CLICKABLE-NAME.HOVER-TEXT</code> (9 items)</summary>

```yaml
HOVER-TEXT:
  - '%luckperms_prefix%%player%'
  - '&7&m----------'
  - '&#00FC00&l$ &fmoney &#00FC00%economy_money%'
  - '&#FC0000⚔ &fkills &#FC0000%economy_kills%'
  - '&#FCE300⌚ &fplaytime &#FCE300%economy_playtime%'
  - '&#F97603☠ &fdeaths &#F97603%economy_deaths%'
  - '&#A303F9★ &fshards &#A303F9%economy_shards%'
  - '&7&m----------'
  - '&7click to view stats'
```

</details>

### `CHAT.LOGGING`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHAT.LOGGING.ENABLED` | `boolean` | `true`, `false` | `true` | Master switch for chat logging. With this off, neither public nor private messages are recorded, whatever the two switches below say. |
| `CHAT.LOGGING.PUBLIC-MESSAGES` | `boolean` | `true`, `false` | `true` | Records normal public chat. Only messages that actually reach chat are stored, so muted, filtered and rate-limited messages are left out. |
| `CHAT.LOGGING.PRIVATE-MESSAGES` | `boolean` | `true`, `false` | `true` | Records private messages sent with /msg and /reply, on both sides of the conversation. |

### `CHAT.FILTER`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHAT.FILTER.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `FILTER` section on or off. |
| `CHAT.FILTER.BLOCK-MESSAGE` | `string` | Any text | `&7Please avoid using inappropriate words.` | Block message. |
| `CHAT.FILTER.WORDS` | `list` | A list of values | _list of 3 items_ | The words list. |

<details>
<summary>Default contents of <code>CHAT.FILTER.WORDS</code> (3 items)</summary>

```yaml
WORDS:
  - 'fuck'
  - 'shit'
  - 'bitch'
```

</details>

#### `CHAT.FILTER.LANGUAGE`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHAT.FILTER.LANGUAGE.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `LANGUAGE` section on or off. |
| `CHAT.FILTER.LANGUAGE.ALLOWED-ALPHABETS` | `list` | A list of values | _list of 3 items_ | The allowed alphabets list. |
| `CHAT.FILTER.LANGUAGE.BLOCK-MESSAGE` | `string` | Any text | `&cYour message contains characters that are not allowed o…` | Block message. |

<details>
<summary>Default contents of <code>CHAT.FILTER.LANGUAGE.ALLOWED-ALPHABETS</code> (3 items)</summary>

```yaml
ALLOWED-ALPHABETS:
  - 'LATIN'
  - 'NUMBERS'
  - 'SYMBOLS'
```

</details>

#### `CHAT.FILTER.CAPS`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHAT.FILTER.CAPS.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `CAPS` section on or off. |
| `CHAT.FILTER.CAPS.PERCENTAGE` | `integer` | Any integer | `70` | Percentage. |
| `CHAT.FILTER.CAPS.MIN-LENGTH` | `integer` | Any integer | `5` | Min length. |
| `CHAT.FILTER.CAPS.BLOCK-MESSAGE` | `string` | Any text | `&cPlease avoid using too many capital letters.` | Block message. |

#### `CHAT.FILTER.ANTI-REPEAT`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHAT.FILTER.ANTI-REPEAT.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `ANTI-REPEAT` section on or off. |
| `CHAT.FILTER.ANTI-REPEAT.BLOCK-MESSAGE` | `string` | Any text | `&cYou cannot repeat the same message!` | Block message. |

#### `CHAT.FILTER.ANTI-LINK`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHAT.FILTER.ANTI-LINK.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `ANTI-LINK` section on or off. |
| `CHAT.FILTER.ANTI-LINK.ALLOWED` | `list` | A list of values | _list of 2 items_ | The allowed list. |
| `CHAT.FILTER.ANTI-LINK.BLOCK-MESSAGE` | `string` | Any text | `&cLinks are not allowed in the chat!` | Block message. |

<details>
<summary>Default contents of <code>CHAT.FILTER.ANTI-LINK.ALLOWED</code> (2 items)</summary>

```yaml
ALLOWED:
  - 'google.com'
  - 'youtube.com'
```

</details>

#### `CHAT.FILTER.LENGTH`

##### `CHAT.FILTER.LENGTH.MIN`

###### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHAT.FILTER.LENGTH.MIN.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `MIN` section on or off. |
| `CHAT.FILTER.LENGTH.MIN.VALUE` | `integer` | Any integer | `1` | Value. |
| `CHAT.FILTER.LENGTH.MIN.BLOCK-MESSAGE` | `string` | Any text | `&cYour message is too short! (Min: %min%)` | Block message. |

##### `CHAT.FILTER.LENGTH.MAX`

###### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CHAT.FILTER.LENGTH.MAX.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `MAX` section on or off. |
| `CHAT.FILTER.LENGTH.MAX.VALUE` | `integer` | Any integer | `100` | Value. |
| `CHAT.FILTER.LENGTH.MAX.BLOCK-MESSAGE` | `string` | Any text | `&cYour message is too long! (Max: %max%)` | Block message. |

<details>
<summary>Default <code>CHAT</code> block as shipped</summary>

```yaml
# Configuration section for Chat.
CHAT:
  # Determines whether Format Enabled is enabled or disabled. Available options: true, false
  FORMAT-ENABLED: true
  # The text or value for Format. Available options: Any valid string text
  FORMAT: '&f%prefix%%player%&7: &f%message%'
  # Configuration section for Message Colors.
  MESSAGE-COLORS:
    # The text or value for Default. Available options: Any valid string text
    default: '&f'
    # The text or value for Owner. Available options: Any valid string text
    owner: '&#0000FF'
  # Configuration section for Clickable Name.
  CLICKABLE-NAME:
    # Determines whether Enabled is enabled or disabled. Available options: true, false
    ENABLED: true
    # Configuration section for Hover Text.
    HOVER-TEXT:
    - '%luckperms_prefix%%player%'
    - '&7&m----------'
    - '&#00FC00&l$ &fmoney &#00FC00%economy_money%'
    - '&#FC0000⚔ &fkills &#FC0000%economy_kills%'
    - '&#FCE300⌚ &fplaytime &#FCE300%economy_playtime%'
    - '&#F97603☠ &fdeaths &#F97603%economy_deaths%'
    - '&#A303F9★ &fshards &#A303F9%economy_shards%'
    - '&7&m----------'
    - '&7click to view stats'
    # The text or value for Suggest Command. Available options: Any valid string text
    SUGGEST-COMMAND: '/msg <player> '
  # Determines whether Global Chat Muted is enabled or disabled. Available options: true, false
  GLOBAL-CHAT-MUTED: false
  # Determines whether Global Chat Delay Enabled is enabled or disabled. Available options: true, false
  GLOBAL-CHAT-DELAY-ENABLED: false
  # The numerical value for Global Chat Delay. Available options: Any valid integer
  GLOBAL-CHAT-DELAY: 3
  # The numerical value for Max Delay Seconds. Available options: Any valid integer
  MAX-DELAY-SECONDS: 30
  # The numerical value for Clear Lines. Available options: Any valid integer
  CLEAR-LINES: 150
  # Configuration section for Logging. Writes chat into each player's own log, so staff can
  # read it back with /logs <player> or browse the whole server with /chatlog. None of these
  # switches change what players see in chat.
  LOGGING:
    # Master switch for chat logging. With this off, neither public nor private messages are
    # recorded, whatever the two switches below say. Available options: true, false
    ENABLED: true
    # Records normal public chat. Only messages that actually reach chat are stored, so muted,
    # filtered and rate-limited messages are left out. Available options: true, false
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `SERVER-NOTIFICATIONS`

### `SERVER-NOTIFICATIONS.JOIN`

The line everyone sees when a player connects. While this is off the server's own join message is relayed instead, exactly as it is today.

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.JOIN.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `JOIN` section on or off. |
| `SERVER-NOTIFICATIONS.JOIN.MESSAGE` | `string` | Any text | `&8[&a+&8] &a{player} &7joined the server.` | Message. |

#### `SERVER-NOTIFICATIONS.JOIN.BY-PERMISSION`

Per-rank wording, resolved from permissions. Supports {player} like MESSAGE above. Delete the examples you do not want; they are never merged back once removed.

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.JOIN.BY-PERMISSION.ultimatedonutsmp2.notifications.join.vip++` | `string` | Any text | `&8[&a+&8] &6{player} &7joined the server.` | Ultimatedonutsmp2.notifications.join.vip++. |
| `SERVER-NOTIFICATIONS.JOIN.BY-PERMISSION.ultimatedonutsmp2.notifications.join.vip+` | `string` | Any text | `&8[&a+&8] &b{player} &7joined the server.` | Ultimatedonutsmp2.notifications.join.vip+. |
| `SERVER-NOTIFICATIONS.JOIN.BY-PERMISSION.ultimatedonutsmp2.notifications.join.vip` | `string` | Any text | `&8[&a+&8] &e{player} &7joined the server.` | Ultimatedonutsmp2.notifications.join.vip. |

### `SERVER-NOTIFICATIONS.LEAVE`

The line everyone sees when a player disconnects. While this is off the server's own quit message is relayed instead, exactly as it is today.

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.LEAVE.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `LEAVE` section on or off. |
| `SERVER-NOTIFICATIONS.LEAVE.MESSAGE` | `string` | Any text | `&8[&c-&8] &c{player} &7left the server.` | Message. |

#### `SERVER-NOTIFICATIONS.LEAVE.BY-PERMISSION`

Per-rank wording, resolved from permissions. Supports {player} like MESSAGE above. Delete the examples you do not want; they are never merged back once removed.

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.LEAVE.BY-PERMISSION.ultimatedonutsmp2.notifications.leave.vip++` | `string` | Any text | `&8[&c-&8] &6{player} &7left the server.` | Ultimatedonutsmp2.notifications.leave.vip++. |
| `SERVER-NOTIFICATIONS.LEAVE.BY-PERMISSION.ultimatedonutsmp2.notifications.leave.vip+` | `string` | Any text | `&8[&c-&8] &b{player} &7left the server.` | Ultimatedonutsmp2.notifications.leave.vip+. |
| `SERVER-NOTIFICATIONS.LEAVE.BY-PERMISSION.ultimatedonutsmp2.notifications.leave.vip` | `string` | Any text | `&8[&c-&8] &e{player} &7left the server.` | Ultimatedonutsmp2.notifications.leave.vip. |

### `SERVER-NOTIFICATIONS.FIRST-JOIN`

Sent in place of the join line the very first time a player ever connects. It replaces the join message rather than arriving alongside it, so nobody gets announced twice.

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.FIRST-JOIN.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `FIRST-JOIN` section on or off. |
| `SERVER-NOTIFICATIONS.FIRST-JOIN.MESSAGE` | `string` | Any text | `&aWelcome &e{player} &ato the server for the first time!` | Message. |

#### `SERVER-NOTIFICATIONS.FIRST-JOIN.BY-PERMISSION`

Per-rank wording, resolved from permissions. Supports {player} like MESSAGE above. Delete the examples you do not want; they are never merged back once removed.

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.FIRST-JOIN.BY-PERMISSION.ultimatedonutsmp2.notifications.first-join.vip++` | `string` | Any text | `&aWelcome &6{player} &ato the server for the first time!` | Ultimatedonutsmp2.notifications.first join.vip++. |
| `SERVER-NOTIFICATIONS.FIRST-JOIN.BY-PERMISSION.ultimatedonutsmp2.notifications.first-join.vip+` | `string` | Any text | `&aWelcome &b{player} &ato the server for the first time!` | Ultimatedonutsmp2.notifications.first join.vip+. |
| `SERVER-NOTIFICATIONS.FIRST-JOIN.BY-PERMISSION.ultimatedonutsmp2.notifications.first-join.vip` | `string` | Any text | `&aWelcome &e{player} &ato the server for the first time!` | Ultimatedonutsmp2.notifications.first join.vip. |

### `SERVER-NOTIFICATIONS.AUCTION-HOUSE`

Announcements for the Auction House. Bot listings are never announced.

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.AUCTION-HOUSE.ENABLED` | `boolean` | `true`, `false` | `true` | Determines whether Auction House announcements are enabled or disabled. Turning this off silences both lines below. |

#### `SERVER-NOTIFICATIONS.AUCTION-HOUSE.LISTING`

Sent when a player puts an item up for sale.

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.AUCTION-HOUSE.LISTING.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `LISTING` section on or off. |
| `SERVER-NOTIFICATIONS.AUCTION-HOUSE.LISTING.MESSAGE` | `string` | Any text | `&8[&6AH&8] &f{player} &7listed &e{amount}x {item} &7for &…` | Message. |

#### `SERVER-NOTIFICATIONS.AUCTION-HOUSE.PURCHASE`

Sent when a player buys a listing.

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.AUCTION-HOUSE.PURCHASE.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `PURCHASE` section on or off. |
| `SERVER-NOTIFICATIONS.AUCTION-HOUSE.PURCHASE.MESSAGE` | `string` | Any text | `&8[&6AH&8] &f{player} &7bought &e{amount}x {item} &7for &…` | Message. |

### `SERVER-NOTIFICATIONS.ORDERS`

Announcements for Orders. Bot orders are never announced.

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.ORDERS.ENABLED` | `boolean` | `true`, `false` | `true` | Determines whether Order announcements are enabled or disabled. Turning this off silences both lines below. |

#### `SERVER-NOTIFICATIONS.ORDERS.CREATE`

Sent when a player opens a new order.

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.ORDERS.CREATE.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `CREATE` section on or off. |
| `SERVER-NOTIFICATIONS.ORDERS.CREATE.MESSAGE` | `string` | Any text | `&8[&6ORDER&8] &f{player} &7created an order for &e{amount…` | Message. |

#### `SERVER-NOTIFICATIONS.ORDERS.COMPLETE`

Sent once an order has been filled all the way, not on every partial delivery. {player} is whoever handed over the last of it and {owner} is whoever opened the order.

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-NOTIFICATIONS.ORDERS.COMPLETE.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `COMPLETE` section on or off. |
| `SERVER-NOTIFICATIONS.ORDERS.COMPLETE.MESSAGE` | `string` | Any text | `&8[&6ORDER&8] &f{player} &7completed &e{owner}&7's order …` | Message. |

<details>
<summary>Default <code>SERVER-NOTIFICATIONS</code> block as shipped</summary>

```yaml
# Server-wide announcements for joins, leaves and the marketplaces. Every message here uses
# the same colour codes as the rest of the plugin: &a style codes, &#RRGGBB for a single hex
# colour and <#RRGGBB>text</#RRGGBB> for a gradient. Each announcement ships switched off, so
# updating the jar never changes how an existing server's chat looks - turn on the ones you
# want. Join, leave and first-join lines still follow each player's Join/Leave Messages choice
# under /settings, and the marketplace lines follow their Server Broadcasts switch.
#
# Each of the three lines below can also read differently per rank. Put the permission a rank
# carries under BY-PERMISSION with the wording that rank should get. The first entry a player
# matches wins, so list your highest rank first, exactly as the examples do. A player matching
# nothing gets the plain MESSAGE above it, which is also what happens while BY-PERMISSION is
# empty, so leaving it alone keeps the wording every server has today. Matching is on the exact
# node: a wildcard such as ultimatedonutsmp2.* does not pick these up, so an operator is
# announced with the plain line unless you grant them one of these nodes yourself.
SERVER-NOTIFICATIONS:
  # The line everyone sees when a player connects. While this is off the server's own join
  # message is relayed instead, exactly as it is today.
  JOIN:
    # Determines whether Join is enabled or disabled. Available options: true, false
    ENABLED: false
    # The text or value for Message. Supports {player}. Available options: Any valid string text
    MESSAGE: '&8[&a+&8] &a{player} &7joined the server.'
    # Per-rank wording, resolved from permissions. Supports {player} like MESSAGE above.
    # Delete the examples you do not want; they are never merged back once removed.
    BY-PERMISSION:
      "ultimatedonutsmp2.notifications.join.vip++": '&8[&a+&8] &6{player} &7joined the server.'
      "ultimatedonutsmp2.notifications.join.vip+": '&8[&a+&8] &b{player} &7joined the server.'
      "ultimatedonutsmp2.notifications.join.vip": '&8[&a+&8] &e{player} &7joined the server.'
  # The line everyone sees when a player disconnects. While this is off the server's own quit
  # message is relayed instead, exactly as it is today.
  LEAVE:
    # Determines whether Leave is enabled or disabled. Available options: true, false
    ENABLED: false
    # The text or value for Message. Supports {player}. Available options: Any valid string text
    MESSAGE: '&8[&c-&8] &c{player} &7left the server.'
    # Per-rank wording, resolved from permissions. Supports {player} like MESSAGE above.
    # Delete the examples you do not want; they are never merged back once removed.
    BY-PERMISSION:
      "ultimatedonutsmp2.notifications.leave.vip++": '&8[&c-&8] &6{player} &7left the server.'
      "ultimatedonutsmp2.notifications.leave.vip+": '&8[&c-&8] &b{player} &7left the server.'
      "ultimatedonutsmp2.notifications.leave.vip": '&8[&c-&8] &e{player} &7left the server.'
  # Sent in place of the join line the very first time a player ever connects. It replaces the
  # join message rather than arriving alongside it, so nobody gets announced twice.
  FIRST-JOIN:
    # Determines whether First Join is enabled or disabled. Available options: true, false
    ENABLED: false
    # The text or value for Message. Supports {player}. Available options: Any valid string text
    MESSAGE: '&aWelcome &e{player} &ato the server for the first time!'
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `AFK-SYSTEM`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `AFK-SYSTEM.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `AFK-SYSTEM` section on or off. |
| `AFK-SYSTEM.TIME` | `integer` | Any integer | `180` | Time. |
| `AFK-SYSTEM.SPAWN-CUBOID-NAME` | `string` | Any text | `spawn` | Spawn cuboid name. |
| `AFK-SYSTEM.AFK-CUBOID-NAME` | `string` | Any text | `''` | Afk cuboid name. |
| `AFK-SYSTEM.MESSAGE` | `string` | Any text | `&7You have been moved to the AFK area for being inactive …` | Message. |

<details>
<summary>Default <code>AFK-SYSTEM</code> block as shipped</summary>

```yaml
# Configuration section for Afk System.
AFK-SYSTEM:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # The numerical value for Time. Available options: Any valid integer
  TIME: 180
  # The text or value for Spawn Cuboid Name. Available options: Any valid string text
  SPAWN-CUBOID-NAME: spawn
  # The text or value for Afk Cuboid Name. Available options: Any valid string text
  AFK-CUBOID-NAME: ''
  # The text or value for Message. Available options: Any valid string text
  MESSAGE: '&7You have been moved to the AFK area for being inactive in the spawn.'
```

</details>

---

## Section: `PREVENT-ITEM-DROP`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `PREVENT-ITEM-DROP.SPAWN` | `boolean` | `true`, `false` | `true` | Prevent players from dropping items while in the spawn region. |
| `PREVENT-ITEM-DROP.AFK` | `boolean` | `true`, `false` | `true` | Prevent players from dropping items while AFK (either in the AFK area or has AFK status). |
| `PREVENT-ITEM-DROP.BYPASS-PERMISSION` | `string` | Any text | `ultimatedonutsmp2.preventdrop.bypass` | Bypass permission for admins/staff to allow item dropping. |
| `PREVENT-ITEM-DROP.MESSAGE` | `string` | Any text | `&c✗ You are not allowed to drop items in spawn or AFK are…` | Message sent to player when their drop is cancelled. Set to '' to disable message. |

<details>
<summary>Default <code>PREVENT-ITEM-DROP</code> block as shipped</summary>

```yaml
# Configuration section for Item Drop Prevention.
PREVENT-ITEM-DROP:
  # Prevent players from dropping items while in the spawn region.
  SPAWN: true
  # Prevent players from dropping items while AFK (either in the AFK area or has AFK status).
  AFK: true
  # Bypass permission for admins/staff to allow item dropping.
  BYPASS-PERMISSION: 'ultimatedonutsmp2.preventdrop.bypass'
  # Message sent to player when their drop is cancelled. Set to '' to disable message.
  MESSAGE: '&c✗ You are not allowed to drop items in spawn or AFK areas!'
```

</details>

---

## Section: `CUBOID-BINDS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CUBOID-BINDS.SPAWN` | `list` | Multiple items | _(empty list)_ | A list configuration for Spawn. |
| `CUBOID-BINDS.AFK` | `list` | Multiple items | _(empty list)_ | A list configuration for Afk. |

<details>
<summary>Default <code>CUBOID-BINDS</code> block as shipped</summary>

```yaml
# Configuration section for Cuboid Binds.
CUBOID-BINDS:
  # A list configuration for Spawn. Available options: Multiple items
  SPAWN: []
  # A list configuration for Afk. Available options: Multiple items
  AFK: []
```

</details>

---

## Section: `FLY-SYSTEM`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `FLY-SYSTEM.PLAYER-FLY-PERMISSION` | `string` | Any text | `ultimatedonutsmp2.player.fly` | The permission required for regular players/ranks to fly in spawn or cuboids. |
| `FLY-SYSTEM.AUTO-DISABLE-OUTSIDE` | `boolean` | `true`, `false` | `true` | Disable flight automatically when the player leaves the allowed areas or enters combat. |
| `FLY-SYSTEM.MIN-SPEED` | `integer` | Any integer | `1` | Minimum allowed flying speed (integer/decimal between 1 and 10) |
| `FLY-SYSTEM.MAX-SPEED` | `integer` | Any integer | `10` | Maximum allowed flying speed (integer/decimal between 1 and 10) |

<details>
<summary>Default <code>FLY-SYSTEM</code> block as shipped</summary>

```yaml
# Configuration section for Fly System.
FLY-SYSTEM:
  # The permission required for regular players/ranks to fly in spawn or cuboids.
  PLAYER-FLY-PERMISSION: 'ultimatedonutsmp2.player.fly'
  # Disable flight automatically when the player leaves the allowed areas or enters combat.
  AUTO-DISABLE-OUTSIDE: true
  # Minimum allowed flying speed (integer/decimal between 1 and 10)
  MIN-SPEED: 1
  # Maximum allowed flying speed (integer/decimal between 1 and 10)
  MAX-SPEED: 10
```

</details>

---

## Section: `WORTH-LORE`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `WORTH-LORE.ENABLED` | `boolean` | `true`, `false` | `true` | Determines whether the worth line is shown at all. Turning this off takes the line away from everyone, whatever players picked under /settings &gt; Worth Display. |
| `WORTH-LORE.FORMAT` | `string` | Any text | `&#00FC00$ &f%price%` | Tooltip line under the item name: currency icon and compact amount only. Do not prefix this with ~. |

<details>
<summary>Default <code>WORTH-LORE</code> block as shipped</summary>

```yaml
# Configuration section for Worth Lore.
WORTH-LORE:
  # Determines whether the worth line is shown at all. Turning this off takes the line away
  # from everyone, whatever players picked under /settings > Worth Display.
  # Available options: true, false
  ENABLED: true
  # Tooltip line under the item name: currency icon and compact amount only.
  # Do not prefix this with ~. Available options: Any valid string text
  FORMAT: '&#00FC00$ &f%price%'
```

</details>

---

## Section: `MONEY-NAMETAGS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MONEY-NAMETAGS.ENABLED` | `boolean` | `true`, `false` | `true` | Determines whether Money Nametags is enabled or disabled. Turning this off takes the line away from everyone, whatever players picked in /settings. |
| `MONEY-NAMETAGS.FORMAT` | `string` | Any text | `&a$ &f{balance}` | How the value is printed. Placeholders are listed on the feature page. |
| `MONEY-NAMETAGS.SHORT-FORMAT` | `boolean` | `true`, `false` | `true` | Determines whether balances are shortened to 1.1K, 1.1M, 1.1B and so on instead of being written out as 1,100,000. |
| `MONEY-NAMETAGS.UPDATE-INTERVAL-TICKS` | `integer` | 1 to 40 | `10` | How quickly a balance change shows up on the line, in ticks. Where the line sits is the client's business, so this only decides how fresh the number is. |

<details>
<summary>Default <code>MONEY-NAMETAGS</code> block as shipped</summary>

```yaml
# The balance line players can switch on under /settings > Money Nametags. It uses the
# scoreboard slot Minecraft reserves for a line under a username, so the client draws it
# itself, one line below the name and never anywhere else. Two of the client's own rules
# come with that: the line only appears within about ten blocks, and only one plugin can
# own that slot at a time. Every player keeps their own switch, so turning it on only
# changes what that player sees under other players' names.
MONEY-NAMETAGS:
  # Determines whether Money Nametags is enabled or disabled. Turning this off takes the
  # line away from everyone, whatever players picked in /settings.
  # Available options: true, false
  ENABLED: true
  # The text or value for Format. Supports {balance} and PlaceholderAPI placeholders.
  # Available options: Any valid string text
  FORMAT: '&a$ &f{balance}'
  # Determines whether balances are shortened to 1.1K, 1.1M, 1.1B and so on instead of
  # being written out as 1,100,000. Available options: true, false
  SHORT-FORMAT: true
  # How quickly a balance change shows up on the line, in ticks. Where the line sits is the
  # client's business, so this only decides how fresh the number is.
  # Available options: 1 to 40
  UPDATE-INTERVAL-TICKS: 10
```

</details>

---

## Section: `END-CRYSTAL`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `END-CRYSTAL.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `END-CRYSTAL` section on or off. |
| `END-CRYSTAL.DAMAGE` | `decimal` | Any decimal number | `2.0` | Damage. |

<details>
<summary>Default <code>END-CRYSTAL</code> block as shipped</summary>

```yaml
# Configuration section for End Crystal.
END-CRYSTAL:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: false
  # The decimal value for Damage. Available options: Any decimal number
  DAMAGE: 2.0
```

</details>

---

## Section: `FAST-CRYSTALS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `FAST-CRYSTALS.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `FAST-CRYSTALS` section on or off. |
| `FAST-CRYSTALS.DEFAULT-PLAYER-STATE` | `boolean` | `true`, `false` | `true` | On/off for default player state. |
| `FAST-CRYSTALS.EXCLUDED-WORLDS` | `list` | A list of values | _list of 1 item_ | The excluded worlds list. |

<details>
<summary>Default contents of <code>FAST-CRYSTALS.EXCLUDED-WORLDS</code> (1 item)</summary>

```yaml
EXCLUDED-WORLDS:
  - 'duels'
```

</details>

### `FAST-CRYSTALS.PLACE`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `FAST-CRYSTALS.PLACE.ENABLED-COOLDOWN-TICKS` | `integer` | Any integer | `0` | Enabled cooldown ticks. Ticks (20 = 1 second). |
| `FAST-CRYSTALS.PLACE.DISABLED-COOLDOWN-TICKS` | `integer` | Any integer | `8` | Disabled cooldown ticks. Ticks (20 = 1 second). |
| `FAST-CRYSTALS.PLACE.DEBOUNCE-MS` | `integer` | Any integer | `40` | Debounce ms. Milliseconds. |
| `FAST-CRYSTALS.PLACE.REQUIRE-VALID-BASE` | `boolean` | `true`, `false` | `true` | On/off for require valid base. |
| `FAST-CRYSTALS.PLACE.VALID-BASES` | `list` | A list of values | _list of 2 items_ | The valid bases list. |
| `FAST-CRYSTALS.PLACE.REQUIRE-AIR-ABOVE` | `boolean` | `true`, `false` | `true` | On/off for require air above. |
| `FAST-CRYSTALS.PLACE.REQUIRE-AIR-TWO-ABOVE` | `boolean` | `true`, `false` | `true` | On/off for require air two above. |

<details>
<summary>Default contents of <code>FAST-CRYSTALS.PLACE.VALID-BASES</code> (2 items)</summary>

```yaml
VALID-BASES:
  - 'OBSIDIAN'
  - 'BEDROCK'
```

</details>

### `FAST-CRYSTALS.BREAK`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `FAST-CRYSTALS.BREAK.CLEAR-COOLDOWN-AFTER-HIT` | `boolean` | `true`, `false` | `true` | On/off for clear cooldown after hit. |

<details>
<summary>Default <code>FAST-CRYSTALS</code> block as shipped</summary>

```yaml
# Configuration section for Fast Crystals.
FAST-CRYSTALS:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # Determines whether Default Player State is enabled or disabled. Available options: true, false
  DEFAULT-PLAYER-STATE: true
  # Configuration section for Excluded Worlds.
  EXCLUDED-WORLDS:
  - duels
  # Configuration section for Place.
  PLACE:
    # The numerical value for Enabled Cooldown Ticks. Available options: Any valid integer
    ENABLED-COOLDOWN-TICKS: 0
    # The numerical value for Disabled Cooldown Ticks. Available options: Any valid integer
    DISABLED-COOLDOWN-TICKS: 8
    # The numerical value for Debounce Ms. Available options: Any valid integer
    DEBOUNCE-MS: 40
    # Determines whether Require Valid Base is enabled or disabled. Available options: true, false
    REQUIRE-VALID-BASE: true
    # Configuration section for Valid Bases.
    VALID-BASES:
    - OBSIDIAN
    - BEDROCK
    # Determines whether Require Air Above is enabled or disabled. Available options: true, false
    REQUIRE-AIR-ABOVE: true
    # Determines whether Require Air Two Above is enabled or disabled. Available options: true, false
    REQUIRE-AIR-TWO-ABOVE: true
  # Configuration section for Break.
  BREAK:
    # Determines whether Clear Cooldown After Hit is enabled or disabled. Available options: true, false
    CLEAR-COOLDOWN-AFTER-HIT: true
```

</details>

---

## Section: `RESPAWN-ANCHOR`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RESPAWN-ANCHOR.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `RESPAWN-ANCHOR` section on or off. |
| `RESPAWN-ANCHOR.DAMAGE` | `decimal` | Any decimal number | `2.0` | Damage. |

<details>
<summary>Default <code>RESPAWN-ANCHOR</code> block as shipped</summary>

```yaml
# Configuration section for Respawn Anchor.
RESPAWN-ANCHOR:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: false
  # The decimal value for Damage. Available options: Any decimal number
  DAMAGE: 2.0
```

</details>

---

## Section: `BOSS-SOUNDS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `BOSS-SOUNDS.ENABLED` | `boolean` | `true`, `false` | `true` | Determines whether Enabled is enabled or disabled. When true, the two boss sounds Minecraft plays to everyone online are kept to players within RADIUS blocks of the boss. Set it to false to leave the vanilla behaviour alone. |
| `BOSS-SOUNDS.RADIUS` | `integer` | Any integer | `1600` | Radius. Blocks. |
| `BOSS-SOUNDS.WITHER-SPAWN` | `boolean` | `true`, `false` | `true` | Determines whether Wither Spawn is enabled or disabled. Covers the roar a wither makes once it finishes charging up. |
| `BOSS-SOUNDS.ENDER-DRAGON-DEATH` | `boolean` | `true`, `false` | `true` | Determines whether Ender Dragon Death is enabled or disabled. Covers the growl an ender dragon makes as it starts dying. |

<details>
<summary>Default <code>BOSS-SOUNDS</code> block as shipped</summary>

```yaml
# Configuration section for Boss Sounds.
BOSS-SOUNDS:
  # Determines whether Enabled is enabled or disabled. When true, the two boss sounds Minecraft
  # plays to everyone online are kept to players within RADIUS blocks of the boss. Set it to false
  # to leave the vanilla behaviour alone. Available options: true, false
  ENABLED: true
  # The numerical value for Radius. How far the sound carries, in blocks, measured from the boss to
  # the player. 1600 is 100 chunks. Players in another world never hear it. A value of 0 or less
  # turns the limit off. Available options: Any valid integer
  RADIUS: 1600
  # Determines whether Wither Spawn is enabled or disabled. Covers the roar a wither makes once it
  # finishes charging up. Available options: true, false
  WITHER-SPAWN: true
  # Determines whether Ender Dragon Death is enabled or disabled. Covers the growl an ender dragon
  # makes as it starts dying. Available options: true, false
  ENDER-DRAGON-DEATH: true
```

</details>

---

## Section: `ENDER-CHEST`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ENDER-CHEST.SIX-ROW` | `boolean` | `true`, `false` | `true` | On/off for six row. |

<details>
<summary>Default <code>ENDER-CHEST</code> block as shipped</summary>

```yaml
# Configuration section for Ender Chest.
ENDER-CHEST:
  # Determines whether Six Row is enabled or disabled. Available options: true, false
  SIX-ROW: true
```

</details>

---

## Section: `LUNAR-CLIENT`

### `LUNAR-CLIENT.RICH-PRESENCE`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `LUNAR-CLIENT.RICH-PRESENCE.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `RICH-PRESENCE` section on or off. |
| `LUNAR-CLIENT.RICH-PRESENCE.UPDATE` | `integer` | Any integer | `1` | Update. |
| `LUNAR-CLIENT.RICH-PRESENCE.PLAYER-STATE` | `string` | Any text | `Playing` | Player state. |
| `LUNAR-CLIENT.RICH-PRESENCE.GAME-STATE` | `string` | Any text | `Playing` | Game state. |
| `LUNAR-CLIENT.RICH-PRESENCE.GAME-NAME` | `string` | Any text | `Economy` | Game name. |
| `LUNAR-CLIENT.RICH-PRESENCE.VARIANT` | `string` | Any text | `%economy_username% ($%economy_nicestMoney%)` | Variant. |
| `LUNAR-CLIENT.RICH-PRESENCE.WORLD-NAME` | `string` | Any text | `Economy` | World name. |
| `LUNAR-CLIENT.RICH-PRESENCE.SUB-SERVER-NAME` | `string` | Any text | `SMP` | Sub server name. |
| `LUNAR-CLIENT.RICH-PRESENCE.TEAM-CURRENT-SIZE` | `string` | Any text | `{team_size}` | Team current size. |
| `LUNAR-CLIENT.RICH-PRESENCE.TEAM-MAX-SIZE` | `string` | Any text | `{team_max_size}` | Team max size. |
| `LUNAR-CLIENT.RICH-PRESENCE.MAX-FIELD-LENGTH` | `integer` | Any integer | `128` | Max field length. |

### `LUNAR-CLIENT.TEAM-VIEW`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `LUNAR-CLIENT.TEAM-VIEW.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `TEAM-VIEW` section on or off. |
| `LUNAR-CLIENT.TEAM-VIEW.UPDATE` | `integer` | Any integer | `20` | Update. |

<details>
<summary>Default <code>LUNAR-CLIENT</code> block as shipped</summary>

```yaml
# Configuration section for Lunar Client.
LUNAR-CLIENT:
  # Configuration section for Rich Presence.
  RICH-PRESENCE:
    # Determines whether Enabled is enabled or disabled. Available options: true, false
    ENABLED: true
    # The numerical value for Update. Available options: Any valid integer
    UPDATE: 1
    # The text or value for Player State. Available options: Any valid string text
    PLAYER-STATE: Playing
    # The text or value for Game State. Available options: Any valid string text
    GAME-STATE: Playing
    # The text or value for Game Name. Available options: Any valid string text
    GAME-NAME: Economy
    # The text or value for Variant. Available options: Any valid string text
    VARIANT: '%economy_username% ($%economy_nicestMoney%)'
    # The text or value for World Name. Available options: Any valid string text
    WORLD-NAME: Economy
    # The text or value for Sub Server Name. Available options: Any valid string text
    SUB-SERVER-NAME: SMP
    # The text or value for Team Current Size. Available options: Any valid string text
    TEAM-CURRENT-SIZE: '{team_size}'
    # The text or value for Team Max Size. Available options: Any valid string text
    TEAM-MAX-SIZE: '{team_max_size}'
    # The numerical value for Max Field Length. Available options: Any valid integer
    MAX-FIELD-LENGTH: 128
  # Configuration section for Team View.
  TEAM-VIEW:
    # Determines whether Enabled is enabled or disabled. Available options: true, false
    ENABLED: true
    # The numerical value for Update. Available options: Any valid integer
    UPDATE: 20
```

</details>

---

## Section: `SHARDS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SHARDS.EVERY` | `integer` | Any integer | `1` | Every. |
| `SHARDS.AMOUNT` | `integer` | Any integer | `1` | Shards paid out each time the fallback timer runs down. |
| `SHARDS.COUNTDOWN` | `string` | Any text | `&7Next shard in &#A303F9%time%` | Countdown shown on the action bar in the fallback zone. %time% is the time remaining, %seconds% the same thing as a plain number. |
| `SHARDS.RECEIVED` | `string` | Any text | `&#A303F9You received %amount% Shard &7(Total: &#A303F9%to…` | Shown when a fallback reward lands. %amount% is the payout and %total% the new balance. |
| `SHARDS.RECEIVED-BOOSTED` | `string` | Any text | `&#A303F9You received %amount% Shards &7(&ax%multiplier%&7…` | Replaces RECEIVED while a shard booster is running. %multiplier% is the boost. |
| `SHARDS.CANCELLED-MESSAGE` | `string` | Any text | `&cShard reward cancelled &7(Left %cuboid% zone)` | Shown when a player leaves the fallback zone before the timer finishes. %cuboid% is the zone name. There is no %total% here, since nothing was paid out. |
| `SHARDS.RESET-ON-LEAVE` | `boolean` | `true`, `false` | `true` | On/off for reset on leave. |
| `SHARDS.BOOSTER-MULTIPLIER` | `integer` | Any integer | `4` | Booster multiplier. |
| `SHARDS.BOOSTER-APPLIES-TO-KILLS` | `boolean` | `true`, `false` | `true` | Determines whether the shard booster also multiplies player kill rewards. |
| `SHARDS.BOOSTER-KILL-MULTIPLIER` | `integer` | Any integer | `0` | Booster kill multiplier. |

### `SHARDS.CUBOIDS`

#### `SHARDS.CUBOIDS.REGIONS`

##### `SHARDS.CUBOIDS.REGIONS.spawn`

###### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SHARDS.CUBOIDS.REGIONS.spawn.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `spawn` section on or off. |
| `SHARDS.CUBOIDS.REGIONS.spawn.BOUND` | `boolean` | `true`, `false` | `false` | On/off for bound. |
| `SHARDS.CUBOIDS.REGIONS.spawn.PRIORITY` | `integer` | Any integer | `100` | Priority. |
| `SHARDS.CUBOIDS.REGIONS.spawn.CUBOID` | `string` | Any text | `''` | Cuboid. |
| `SHARDS.CUBOIDS.REGIONS.spawn.WORLD` | `string` | Any text | `world` | World name, as shown in `/minecraft:worlds` / Multiverse. |
| `SHARDS.CUBOIDS.REGIONS.spawn.RADIUS` | `integer` | Any integer | `16` | Radius in blocks for the point-based fallbacks. LOCATION gets a sphere this wide that pays shards alongside CUBOID, and the AFK point gets one that counts as the AFK area when no AFK cuboid is bound. One number covers both. Set 0 to switch the shard sphere off and leave the AFK sphere at its built-in 16. |
| `SHARDS.CUBOIDS.REGIONS.spawn.INTERVAL` | `integer` | Any integer | `60` | Interval. |
| `SHARDS.CUBOIDS.REGIONS.spawn.AMOUNT` | `integer` | Any integer | `1` | How many to give or take. |
| `SHARDS.CUBOIDS.REGIONS.spawn.COUNTDOWN-MESSAGE` | `string` | Any text | `&7Next shard in &#A303F9%time%` | Countdown message. |
| `SHARDS.CUBOIDS.REGIONS.spawn.REWARD-MESSAGE` | `string` | Any text | `&#A303F9You received %amount% Shard &7(Total: &#A303F9%to…` | Reward message. |
| `SHARDS.CUBOIDS.REGIONS.spawn.BOOSTED-REWARD-MESSAGE` | `string` | Any text | `&#A303F9You received %amount% Shards &7(&ax%multiplier%&7…` | Boosted reward message. |
| `SHARDS.CUBOIDS.REGIONS.spawn.LEAVE-MESSAGE` | `string` | Any text | `&cShard reward cancelled &7(Left %cuboid% zone)` | Leave message. |
| `SHARDS.CUBOIDS.REGIONS.spawn.AFK-TIME` | `integer` | Any integer | `120` | Afk time. |
| `SHARDS.CUBOIDS.REGIONS.spawn.AFK-CUBOID` | `string` | Any text | `''` | Afk cuboid. |
| `SHARDS.CUBOIDS.REGIONS.spawn.AFK-LOCATION` | `string` | Any text | `''` | Afk location. |
| `SHARDS.CUBOIDS.REGIONS.spawn.AFK-MESSAGE` | `string` | Any text | `&7You have been moved to the AFK area for being inactive …` | Afk message. |
| `SHARDS.CUBOIDS.REGIONS.spawn.TELEPORT-ON-AFK` | `boolean` | `true`, `false` | `true` | On/off for teleport on afk. |
| `SHARDS.CUBOIDS.REGIONS.spawn.EXCLUDED-WORLDS` | `list` | A list of values | _list of 1 item_ | The excluded worlds list. |
| `SHARDS.CUBOIDS.REGIONS.spawn.RECENT-MOVEMENT-WINDOW` | `integer` | Any integer | `15` | Recent movement window. |
| `SHARDS.CUBOIDS.REGIONS.spawn.MIN-MOVEMENT-BLOCKS` | `integer` | Any integer | `5` | Min movement blocks. |
| `SHARDS.CUBOIDS.REGIONS.spawn.RESET-ON-LEAVE` | `boolean` | `true`, `false` | `true` | On/off for reset on leave. |
| `SHARDS.CUBOIDS.REGIONS.spawn.PAUSED-MESSAGE` | `string` | Any text | `&eMove to keep earning shards &7(%movement%/%required_mov…` | Paused message. |
| `SHARDS.CUBOIDS.REGIONS.spawn.AFK-PAUSED-MESSAGE` | `string` | Any text | `&cYou are AFK. Move to resume shard gain` | Afk paused message. |
| `SHARDS.CUBOIDS.REGIONS.spawn.EXCLUDED-WORLD-MESSAGE` | `string` | Any text | `&cShards are disabled in this world` | Excluded world message. |

<details>
<summary>Default contents of <code>SHARDS.CUBOIDS.REGIONS.spawn.EXCLUDED-WORLDS</code> (1 item)</summary>

```yaml
EXCLUDED-WORLDS:
  - 'duels'
```

</details>

### `SHARDS.EVERYWHERE`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SHARDS.EVERYWHERE.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `EVERYWHERE` section on or off. |
| `SHARDS.EVERYWHERE.EVERY` | `integer` | Any integer | `3` | Every. |
| `SHARDS.EVERYWHERE.AMOUNT` | `integer` | Any integer | `1` | How many to give or take. |
| `SHARDS.EVERYWHERE.REQUIRED-PERMISSION` | `string` | Any text | `ultimatedonutsmp2.shards.everywhere` | Permission node. Leave empty to allow everyone. |
| `SHARDS.EVERYWHERE.RECENT-MOVEMENT-WINDOW` | `integer` | Any integer | `15` | Recent movement window. |
| `SHARDS.EVERYWHERE.DISABLE-WHILE-IN-SHARD-CUBOID` | `boolean` | `true`, `false` | `false` | On/off for disable while in shard cuboid. |
| `SHARDS.EVERYWHERE.RECEIVED` | `string` | Any text | `&#A303F9You received %amount% Shard &8[Everywhere] &7(Tot…` | Received. |
| `SHARDS.EVERYWHERE.RECEIVED-BOOSTED` | `string` | Any text | `&#A303F9You received %amount% Shards &7(&ax%multiplier%&7…` | Received boosted. |
| `SHARDS.EVERYWHERE.EXCLUDED-WORLDS` | `list` | A list of values | _list of 1 item_ | The excluded worlds list. |

<details>
<summary>Default contents of <code>SHARDS.EVERYWHERE.EXCLUDED-WORLDS</code> (1 item)</summary>

```yaml
EXCLUDED-WORLDS:
  - 'duels'
```

</details>

<details>
<summary>Default <code>SHARDS</code> block as shipped</summary>

```yaml
# Configuration section for Shards.
SHARDS:
  # EVERY down to CANCELLED-MESSAGE are a fallback for servers that have no shard region
  # at all. They are read only when CUBOIDS.REGIONS below is empty, and this file ships a
  # 'spawn' region, so on a normal install they do nothing. The settings that actually run
  # are the ones inside CUBOIDS.REGIONS.<region>. RESET-ON-LEAVE is not part of this group:
  # it stays live as the default for the matching per-region key.
  # Minutes between rewards in the fallback zone. Watch the unit, because the per-region
  # INTERVAL is in seconds and this one is multiplied by 60.
  EVERY: 1
  # Shards paid out each time the fallback timer runs down.
  AMOUNT: 1
  # Countdown shown on the action bar in the fallback zone. %time% is the time remaining,
  # %seconds% the same thing as a plain number.
  COUNTDOWN: '&7Next shard in &#A303F9%time%'
  # Shown when a fallback reward lands. %amount% is the payout and %total% the new balance.
  RECEIVED: '&#A303F9You received %amount% Shard &7(Total: &#A303F9%total%&7)'
  # Replaces RECEIVED while a shard booster is running. %multiplier% is the boost.
  RECEIVED-BOOSTED: '&#A303F9You received %amount% Shards &7(&ax%multiplier%&7) &7(Total:
    &#A303F9%total%&7)'
  # Shown when a player leaves the fallback zone before the timer finishes. %cuboid% is the
  # zone name. There is no %total% here, since nothing was paid out.
  CANCELLED-MESSAGE: '&cShard reward cancelled &7(Left %cuboid% zone)'
  # Determines whether Reset On Leave is enabled or disabled. Available options: true, false
  RESET-ON-LEAVE: true
  # Configuration section for Cuboids.
  CUBOIDS:
    # Configuration section for Regions.
    REGIONS:
      # Configuration section for Spawn.
      spawn:
        # Determines whether Enabled is enabled or disabled. Available options: true, false
        ENABLED: false
        # Determines whether Bound is enabled or disabled. Available options: true, false
        BOUND: false
        # The numerical value for Priority. Available options: Any valid integer
        PRIORITY: 100
        # The text or value for Cuboid. Available options: Any valid string text
        CUBOID: ''
        # The text or value for World. Available options: Any valid string text
        WORLD: world
        # Radius in blocks for the point-based fallbacks. LOCATION gets a sphere this wide
        # that pays shards alongside CUBOID, and the AFK point gets one that counts as the
        # AFK area when no AFK cuboid is bound. One number covers both. Set 0 to switch the
        # shard sphere off and leave the AFK sphere at its built-in 16.
        RADIUS: 16
        # The numerical value for Interval. Available options: Any valid integer
        INTERVAL: 60
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `KEY-ALL`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `KEY-ALL.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `KEY-ALL` section on or off. |
| `KEY-ALL.EVERY` | `integer` | Any integer | `60` | Every. |
| `KEY-ALL.COMMANDS` | `list` | A list of values | _list of 1 item_ | The commands list. |
| `KEY-ALL.TYPE` | `string` | Any text | `RANDOM` | Which option this section uses. |

<details>
<summary>Default contents of <code>KEY-ALL.COMMANDS</code> (1 item)</summary>

```yaml
COMMANDS:
  - ''
```

</details>

### `KEY-ALL.RANDOM`

#### `KEY-ALL.RANDOM.KEYS`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `KEY-ALL.RANDOM.KEYS.common` | `integer` | Any integer | `60` | Common. |
| `KEY-ALL.RANDOM.KEYS.rare` | `integer` | Any integer | `30` | Rare. |
| `KEY-ALL.RANDOM.KEYS.epic` | `integer` | Any integer | `10` | Epic. |

### `KEY-ALL.ONE-KEY-ONLY`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `KEY-ALL.ONE-KEY-ONLY.KEY` | `string` | Any text | `common` | Key. |

### `KEY-ALL.NOTIFICATION`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `KEY-ALL.NOTIFICATION.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `NOTIFICATION` section on or off. |
| `KEY-ALL.NOTIFICATION.MESSAGE` | `list` | A list of values | _list of 4 items_ | The message list. |

<details>
<summary>Default contents of <code>KEY-ALL.NOTIFICATION.MESSAGE</code> (4 items)</summary>

```yaml
MESSAGE:
  - ''
  - '&#00A4FCKey-All reward!'
  - '&fYou received &b{amount}x {crate}&f key.'
  - ''
```

</details>

<details>
<summary>Default <code>KEY-ALL</code> block as shipped</summary>

```yaml
# Configuration section for Key All.
KEY-ALL:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # The numerical value for Every. Available options: Any valid integer
  EVERY: 60
  # Configuration section for Commands.
  COMMANDS:
  - ''
  TYPE: RANDOM
  # Configuration section for Random.
  RANDOM:
    # Configuration section for Keys.
    KEYS:
      # The numerical value for Common. Available options: Any valid integer
      common: 60
      # The numerical value for Rare. Available options: Any valid integer
      rare: 30
      # The numerical value for Epic. Available options: Any valid integer
      epic: 10
  # Configuration section for One Key Only.
  ONE-KEY-ONLY:
    # The text or value for Key. Available options: Any valid string text
    KEY: common
  # Configuration section for Notification.
  NOTIFICATION:
    # Determines whether Enabled is enabled or disabled. Available options: true, false
    ENABLED: true
    # Configuration section for Message.
    MESSAGE:
    - ''
    - '&#00A4FCKey-All reward!'
    - '&fYou received &b{amount}x {crate}&f key.'
    - ''
```

</details>

---

## Section: `TEAM`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `TEAM.NAME-MIN-LENGTH` | `integer` | Any integer | `3` | Name min length. |
| `TEAM.NAME-MAX-LENGTH` | `integer` | Any integer | `5` | Name max length. |
| `TEAM.LIMIT-MEMBERS` | `integer` | Any integer | `10` | Limit members. |

<details>
<summary>Default <code>TEAM</code> block as shipped</summary>

```yaml
# Configuration section for Team.
TEAM:
  # The numerical value for Name Min Length. Available options: Any valid integer
  NAME-MIN-LENGTH: 3
  # The numerical value for Name Max Length. Available options: Any valid integer
  NAME-MAX-LENGTH: 5
  # The numerical value for Limit Members. Available options: Any valid integer
  LIMIT-MEMBERS: 10
```

</details>

---

## Section: `LEADERBOARD`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `LEADERBOARD.UPDATE` | `integer` | Any integer | `10` | Update. |
| `LEADERBOARD.NPC-REFRESH` | `integer` | Any integer | `1` | Npc refresh. |

<details>
<summary>Default <code>LEADERBOARD</code> block as shipped</summary>

```yaml
# Configuration section for Leaderboard.
LEADERBOARD:
  # The numerical value for Update. Available options: Any valid integer
  UPDATE: 10
  # The numerical value for Npc Refresh. Available options: Any valid integer
  NPC-REFRESH: 1
```

</details>

---

## Section: `TABLIST`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `TABLIST.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `TABLIST` section on or off. |
| `TABLIST.LUCKPERMS-PRIORITY` | `boolean` | `true`, `false` | `true` | On/off for luckperms priority. |
| `TABLIST.SHOW-TEAM-NAME` | `boolean` | `true`, `false` | `false` | On/off for show team name. |
| `TABLIST.ICON-HEAD-SKIN` | `string` | Any text | `&lt;head:%player_name%&gt;` | Icon head skin. |
| `TABLIST.ICON-MEDIA` | `string` | Any text | `📹` | Icon media. |
| `TABLIST.MEDIA-BADGE-FORMAT` | `string` | Any text | `&lt;#FF00A6&gt;&lt;icon_media&gt;` | Media badge format. |
| `TABLIST.MEDIA-BADGE-PERMISSION` | `string` | Any text | `media` | Permission node. Leave empty to allow everyone. |
| `TABLIST.DONUT-PLUS-FORMAT` | `string` | Any text | `&lt;#00A4FC&gt;+` | Donut plus format. |
| `TABLIST.DONUT-PLUS-PLUS-FORMAT` | `string` | Any text | `&lt;#00A4FC&gt;++` | Donut plus plus format. |
| `TABLIST.DONUT-PLUS-PLUS-PLUS-FORMAT` | `string` | Any text | `&lt;#00A4FC&gt;+++` | Donut plus plus plus format. |
| `TABLIST.NAME-FORMAT` | `string` | Any text | `&lt;media_badge&gt;%prefix%&lt;donut_badge&gt;&f&lt;nick&gt;%team_suffix%` | Name format. |
| `TABLIST.HEADER` | `list` | A list of values | _list of 4 items_ | The header list. |
| `TABLIST.FOOTER` | `list` | A list of values | _list of 3 items_ | The footer list. |

<details>
<summary>Default contents of <code>TABLIST.HEADER</code> (4 items)</summary>

```yaml
HEADER:
  - ''
  - '<#00A6FF><bold>UltimateDonutSMP V2'
  - '&f%online_compact% Players'
  - ''
```

</details>

<details>
<summary>Default contents of <code>TABLIST.FOOTER</code> (3 items)</summary>

```yaml
FOOTER:
  - ''
  - '<#00FF00>$ &f%money% <#00FF00>• &f%ping% ms'
  - ''
```

</details>

<details>
<summary>Default <code>TABLIST</code> block as shipped</summary>

```yaml
# Configuration section for Tablist.
TABLIST:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # Determines whether Luckperms Priority is enabled or disabled. Available options: true, false
  LUCKPERMS-PRIORITY: true
  # Determines whether Show Team Name is enabled or disabled. Available options: true, false
  SHOW-TEAM-NAME: false
  # The text or value for Icon Head Skin. Available options: Any valid string text
  ICON-HEAD-SKIN: <head:%player_name%>
  # The text or value for Icon Media. Available options: Any valid string text
  ICON-MEDIA: 📹
  # The text or value for Media Badge Format. Available options: Any valid string text
  MEDIA-BADGE-FORMAT: '<#FF00A6><icon_media>'
  # The text or value for Media Badge Permission. Available options: Any valid string text
  MEDIA-BADGE-PERMISSION: media
  # The text or value for Donut+ Badge Format. Available options: Any valid string text
  DONUT-PLUS-FORMAT: '<#00A4FC>+'
  # The text or value for Donut++ Badge Format. Available options: Any valid string text
  DONUT-PLUS-PLUS-FORMAT: '<#00A4FC>++'
  # The text or value for Donut+++ Badge Format. Available options: Any valid string text
  DONUT-PLUS-PLUS-PLUS-FORMAT: '<#00A4FC>+++'
  # The text or value for Name Format. Available options: Any valid string text
  NAME-FORMAT: '<media_badge>%prefix%<donut_badge>&f<nick>%team_suffix%'
  # Configuration section for Header.
  HEADER:
  - ''
  - <#00A6FF><bold>UltimateDonutSMP V2
  - '&f%online_compact% Players'
  - ''
  # Configuration section for Footer.
  FOOTER:
  - ''
  - '<#00FF00>$ &f%money% <#00FF00>• &f%ping% ms'
  - ''
```

</details>

---

## Section: `SERVER-LIST`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SERVER-LIST.ENABLED` | `boolean` | `true`, `false` | `false` | Determines whether Enabled is enabled or disabled. When true, the text under this server's name in the multiplayer list is taken from MOTD below instead of the motd line in server.properties. Set it to false to leave that entry alone. |
| `SERVER-LIST.MOTD` | `list` | A list of values | _list of 2 items_ | Configuration section for Motd. The lines shown under the server name, written in the same colour codes as the rest of the plugin. The client only draws the first two. %online% becomes the number of players on the server and %max_players% the slot count, the same two tokens the tablist header takes. Maintenance mode keeps its own text while it is on, set in network.yml |

<details>
<summary>Default contents of <code>SERVER-LIST.MOTD</code> (2 items)</summary>

```yaml
MOTD:
  - '&e&lUltimateDonutSMP2'
  - '&7%online%&8/&7%max_players% &7online'
```

</details>

<details>
<summary>Default <code>SERVER-LIST</code> block as shipped</summary>

```yaml
# Configuration section for Server List.
SERVER-LIST:
  # Determines whether Enabled is enabled or disabled. When true, the text under this server's name
  # in the multiplayer list is taken from MOTD below instead of the motd line in server.properties.
  # Set it to false to leave that entry alone. Available options: true, false
  ENABLED: false
  # Configuration section for Motd. The lines shown under the server name, written in the same
  # colour codes as the rest of the plugin. The client only draws the first two. %online% becomes
  # the number of players on the server and %max_players% the slot count, the same two tokens the
  # tablist header takes. Maintenance mode keeps its own text while it is on, set in network.yml
  MOTD:
  - '&e&lUltimateDonutSMP2'
  - '&7%online%&8/&7%max_players% &7online'
```

</details>

---

## Section: `OPTIMIZATION`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `OPTIMIZATION.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `OPTIMIZATION` section on or off. |
| `OPTIMIZATION.MONITOR-INTERVAL-TICKS` | `integer` | Any integer | `100` | Monitor interval ticks. Ticks (20 = 1 second). |
| `OPTIMIZATION.STARTUP-GRACE-TICKS` | `integer` | Any integer | `1200` | Ignore TPS/MSPT for this many ticks after the plugin enables. Paper's one-minute TPS right after Done is the whole short window, including JVM JIT. |
| `OPTIMIZATION.TPS-WARN-THRESHOLD` | `decimal` | Any decimal number | `18.5` | Tps warn threshold. |
| `OPTIMIZATION.TPS-CRITICAL-THRESHOLD` | `decimal` | Any decimal number | `16.0` | Tps critical threshold. |
| `OPTIMIZATION.MSPT-WARN-THRESHOLD` | `decimal` | Any decimal number | `45.0` | Mspt warn threshold. |
| `OPTIMIZATION.MSPT-CRITICAL-THRESHOLD` | `decimal` | Any decimal number | `55.0` | Mspt critical threshold. |
| `OPTIMIZATION.RECOVERY-SAMPLES` | `integer` | Any integer | `3` | Recovery samples. |
| `OPTIMIZATION.LOG-STATE-CHANGES` | `boolean` | `true`, `false` | `true` | On/off for log state changes. |

### `OPTIMIZATION.ADAPTIVE-TASKS`

#### `OPTIMIZATION.ADAPTIVE-TASKS.SCOREBOARD`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `OPTIMIZATION.ADAPTIVE-TASKS.SCOREBOARD.ENABLED` | `boolean` | `true`, `false` | `true` | Whether the scoreboard task may be slowed down while the server is under load. Setting this to false removes the slowdown and lets the task keep running at full rate; it does not turn the scoreboard off. That switch is SCOREBOARD.ENABLED in scoreboard.yml. |
| `OPTIMIZATION.ADAPTIVE-TASKS.SCOREBOARD.WARN-MIN-INTERVAL-TICKS` | `integer` | Any integer | `4` | Warn min interval ticks. Ticks (20 = 1 second). |
| `OPTIMIZATION.ADAPTIVE-TASKS.SCOREBOARD.CRITICAL-MIN-INTERVAL-TICKS` | `integer` | Any integer | `10` | Critical min interval ticks. Ticks (20 = 1 second). |

#### `OPTIMIZATION.ADAPTIVE-TASKS.TABLIST`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `OPTIMIZATION.ADAPTIVE-TASKS.TABLIST.ENABLED` | `boolean` | `true`, `false` | `true` | Whether the tablist task may be slowed down while the server is under load. Setting this to false removes the slowdown and lets the task keep running at full rate; it does not turn the tablist off. That switch is the TABLIST section near the top of this file. |
| `OPTIMIZATION.ADAPTIVE-TASKS.TABLIST.WARN-MIN-INTERVAL-TICKS` | `integer` | Any integer | `80` | Warn min interval ticks. Ticks (20 = 1 second). |
| `OPTIMIZATION.ADAPTIVE-TASKS.TABLIST.CRITICAL-MIN-INTERVAL-TICKS` | `integer` | Any integer | `140` | Critical min interval ticks. Ticks (20 = 1 second). |

#### `OPTIMIZATION.ADAPTIVE-TASKS.LUNAR-TEAMMATES`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `OPTIMIZATION.ADAPTIVE-TASKS.LUNAR-TEAMMATES.ENABLED` | `boolean` | `true`, `false` | `true` | Whether the lunar teammates task may be slowed down while the server is under load. Setting this to false removes the slowdown and lets the task keep running at full rate; it does not turn the lunar teammate overlay off. |
| `OPTIMIZATION.ADAPTIVE-TASKS.LUNAR-TEAMMATES.WARN-MIN-INTERVAL-TICKS` | `integer` | Any integer | `40` | Warn min interval ticks. Ticks (20 = 1 second). |
| `OPTIMIZATION.ADAPTIVE-TASKS.LUNAR-TEAMMATES.CRITICAL-MIN-INTERVAL-TICKS` | `integer` | Any integer | `100` | Critical min interval ticks. Ticks (20 = 1 second). |

<details>
<summary>Default <code>OPTIMIZATION</code> block as shipped</summary>

```yaml
# Configuration section for Optimization.
OPTIMIZATION:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # The numerical value for Monitor Interval Ticks. Available options: Any valid integer
  MONITOR-INTERVAL-TICKS: 100
  # Ignore TPS/MSPT for this many ticks after the plugin enables. Paper's one-minute
  # TPS right after Done is the whole short window, including JVM JIT.
  STARTUP-GRACE-TICKS: 1200
  # The decimal value for Tps Warn Threshold. Available options: Any decimal number
  TPS-WARN-THRESHOLD: 18.5
  # The decimal value for Tps Critical Threshold. Available options: Any decimal number
  TPS-CRITICAL-THRESHOLD: 16.0
  # The decimal value for Mspt Warn Threshold. Available options: Any decimal number
  MSPT-WARN-THRESHOLD: 45.0
  # The decimal value for Mspt Critical Threshold. Available options: Any decimal number
  MSPT-CRITICAL-THRESHOLD: 55.0
  # The numerical value for Recovery Samples. Available options: Any valid integer
  RECOVERY-SAMPLES: 3
  # Determines whether Log State Changes is enabled or disabled. Available options: true, false
  LOG-STATE-CHANGES: true
  # Configuration section for Adaptive Tasks. Each entry below decides how often one task is
  # allowed to run while the server is struggling. None of them switch a feature on or off; the
  # switches for that live in their own sections, either at the top level of this file or in the
  # file named after the feature.
  ADAPTIVE-TASKS:
    # Configuration section for Scoreboard.
    SCOREBOARD:
      # Whether the scoreboard task may be slowed down while the server is under load. Setting this
      # to false removes the slowdown and lets the task keep running at full rate; it does not turn
      # the scoreboard off. That switch is SCOREBOARD.ENABLED in scoreboard.yml.
      # Available options: true, false
      ENABLED: true
      # The numerical value for Warn Min Interval Ticks. Available options: Any valid integer
      WARN-MIN-INTERVAL-TICKS: 4
      # The numerical value for Critical Min Interval Ticks. Available options: Any valid integer
      CRITICAL-MIN-INTERVAL-TICKS: 10
    # Configuration section for Tablist.
    TABLIST:
      # Whether the tablist task may be slowed down while the server is under load. Setting this to
      # false removes the slowdown and lets the task keep running at full rate; it does not turn the
      # tablist off. That switch is the TABLIST section near the top of this file.
      # Available options: true, false
      ENABLED: true
      # The numerical value for Warn Min Interval Ticks. Available options: Any valid integer
      WARN-MIN-INTERVAL-TICKS: 80
      # The numerical value for Critical Min Interval Ticks. Available options: Any valid integer
      CRITICAL-MIN-INTERVAL-TICKS: 140
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `JOIN-WARMUP`

After a restart, do not preload terrain. Loading spawn or logout chunks at boot ticks every mob in them and drops TPS while nobody is online. Login only waits for a small already-generated square; Paper streams the rest of view-distance. Duel/FFA/lobby worlds are skipped.

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `JOIN-WARMUP.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `JOIN-WARMUP` section on or off. |
| `JOIN-WARMUP.KEEP-SPAWN-IN-MEMORY` | `boolean` | `true`, `false` | `false` | Leave false. Multiverse already sets keep-spawn-in-memory false. Forcing spawn chunks to stay loaded makes an empty server tick hundreds of mobs. |
| `JOIN-WARMUP.PIN-LOADED-CHUNKS` | `boolean` | `true`, `false` | `true` | Pin only the inner logout square while that player is logging in. |
| `JOIN-WARMUP.RELEASE-TICKETS-AFTER-EMPTY-TICKS` | `integer` | Any integer | `200` | Release tickets after empty ticks. Ticks (20 = 1 second). |
| `JOIN-WARMUP.SPAWN-CHUNK-RADIUS` | `integer` | Any integer | `10` | Spawn chunk radius. Blocks. |
| `JOIN-WARMUP.CHUNKS-PER-TICK` | `integer` | Any integer | `1` | Chunks scheduled per tick while a player is logging in. Keep this at 1 so join does not freeze the tick thread. |
| `JOIN-WARMUP.WARM-ENTITY-API` | `boolean` | `true`, `false` | `true` | On/off for warm entity api. |
| `JOIN-WARMUP.PRELOAD-LOGOUT-CHUNKS` | `boolean` | `true`, `false` | `true` | On/off for preload logout chunks. |
| `JOIN-WARMUP.LOGOUT-CHUNK-RADIUS` | `integer` | Any integer | `2` | Logout chunk radius. Blocks. |
| `JOIN-WARMUP.LOGOUT-WAIT-RADIUS` | `integer` | Any integer | `2` | Logout wait radius. Blocks. |
| `JOIN-WARMUP.LOGOUT-WAIT-MILLIS` | `integer` | Any integer | `1500` | Logout wait millis. Milliseconds. |
| `JOIN-WARMUP.STARTUP-LOGOUT-ANCHORS` | `integer` | Any integer | `4` | Startup logout anchors. |
| `JOIN-WARMUP.MOVEMENT-AHEAD-CHUNKS` | `integer` | Any integer | `2` | Movement ahead chunks. |
| `JOIN-WARMUP.MOVEMENT-AHEAD-RADIUS` | `integer` | Any integer | `2` | Movement ahead radius. Blocks. |
| `JOIN-WARMUP.MOVEMENT-AHEAD-MAX-CHUNKS` | `integer` | Any integer | `10` | Movement ahead max chunks. |

<details>
<summary>Default <code>JOIN-WARMUP</code> block as shipped</summary>

```yaml
# After a restart, do not preload terrain. Loading spawn or logout chunks at boot
# ticks every mob in them and drops TPS while nobody is online. Login only waits
# for a small already-generated square; Paper streams the rest of view-distance.
# Duel/FFA/lobby worlds are skipped.
JOIN-WARMUP:
  ENABLED: true
  # Leave false. Multiverse already sets keep-spawn-in-memory false. Forcing spawn
  # chunks to stay loaded makes an empty server tick hundreds of mobs.
  KEEP-SPAWN-IN-MEMORY: false
  # Pin only the inner logout square while that player is logging in.
  PIN-LOADED-CHUNKS: true
  RELEASE-TICKETS-AFTER-EMPTY-TICKS: 200
  SPAWN-CHUNK-RADIUS: 10
  # Chunks scheduled per tick while a player is logging in. Keep this at 1 so join
  # does not freeze the tick thread.
  CHUNKS-PER-TICK: 1
  WARM-ENTITY-API: true
  PRELOAD-LOGOUT-CHUNKS: true
  LOGOUT-CHUNK-RADIUS: 2
  LOGOUT-WAIT-RADIUS: 2
  LOGOUT-WAIT-MILLIS: 1500
  STARTUP-LOGOUT-ANCHORS: 4
  MOVEMENT-AHEAD-CHUNKS: 2
  MOVEMENT-AHEAD-RADIUS: 2
  MOVEMENT-AHEAD-MAX-CHUNKS: 10
```

</details>

---

## Section: `ENTITY-PRESSURE`

Caps ticking mobs per chunk when that chunk's entities are loaded. Trimming on ChunkLoadEvent is too early: the mobs are not in the chunk yet, so a join still spends seconds ticking every stored bat and sheep. Named and tamed mobs stay.

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ENTITY-PRESSURE.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `ENTITY-PRESSURE` section on or off. |
| `ENTITY-PRESSURE.MAX-AMBIENT-PER-CHUNK` | `integer` | Any integer | `2` | Bats and other ambient mobs. The watchdog dump was frozen in Bat.tick. |
| `ENTITY-PRESSURE.MAX-ANIMALS-PER-CHUNK` | `integer` | Any integer | `24` | Named and tamed mobs are never removed. Spawner and egg spawns are not cancelled; only natural/chunk-load piles are trimmed so grinders keep working. |
| `ENTITY-PRESSURE.MAX-MONSTERS-PER-CHUNK` | `integer` | Any integer | `24` | Zombies, creepers, and other monsters. Spawner grinders are not cancelled at spawn time. |

<details>
<summary>Default <code>ENTITY-PRESSURE</code> block as shipped</summary>

```yaml
# Caps ticking mobs per chunk when that chunk's entities are loaded. Trimming on
# ChunkLoadEvent is too early: the mobs are not in the chunk yet, so a join still
# spends seconds ticking every stored bat and sheep. Named and tamed mobs stay.
ENTITY-PRESSURE:
  ENABLED: true
  # Bats and other ambient mobs. The watchdog dump was frozen in Bat.tick.
  MAX-AMBIENT-PER-CHUNK: 2
  # Named and tamed mobs are never removed. Spawner and egg spawns are not cancelled;
  # only natural/chunk-load piles are trimmed so grinders keep working.
  MAX-ANIMALS-PER-CHUNK: 24
  # Zombies, creepers, and other monsters. Spawner grinders are not cancelled at spawn time.
  MAX-MONSTERS-PER-CHUNK: 24
```

</details>

---

## Section: `CLEAR-LAG`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CLEAR-LAG.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `CLEAR-LAG` section on or off. |
| `CLEAR-LAG.EVERY` | `integer` | Any integer | `5` | Every. |
| `CLEAR-LAG.ANIMALS` | `boolean` | `true`, `false` | `false` | On/off for animals. |
| `CLEAR-LAG.MONSTERS` | `boolean` | `true`, `false` | `false` | On/off for monsters. |
| `CLEAR-LAG.DROPPED-ITEMS` | `boolean` | `true`, `false` | `true` | On/off for dropped items. |
| `CLEAR-LAG.MIN-ITEM-AGE-SECONDS` | `integer` | Any integer | `60` | Min item age seconds. Seconds. |
| `CLEAR-LAG.EXCLUDED-WORLDS` | `list` | A list of values | _list of 1 item_ | The excluded worlds list. |
| `CLEAR-LAG.EXCLUDE-NAMED` | `boolean` | `true`, `false` | `true` | On/off for exclude named. |
| `CLEAR-LAG.EXCLUDE-TAMED` | `boolean` | `true`, `false` | `true` | On/off for exclude tamed. |
| `CLEAR-LAG.EXCLUDE-VILLAGERS` | `boolean` | `true`, `false` | `true` | On/off for exclude villagers. |
| `CLEAR-LAG.EXCLUDED-ENTITY-TYPES` | `list` | A list of values | _(empty list)_ | The excluded entity types list. |
| `CLEAR-LAG.EXCLUDED-ITEM-MATERIALS` | `list` | A list of values | _(empty list)_ | The excluded item materials list. |

<details>
<summary>Default contents of <code>CLEAR-LAG.EXCLUDED-WORLDS</code> (1 item)</summary>

```yaml
EXCLUDED-WORLDS:
  - 'duels'
```

</details>

<details>
<summary>Default <code>CLEAR-LAG</code> block as shipped</summary>

```yaml
# Configuration section for Clear Lag.
CLEAR-LAG:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # The numerical value for Every. Available options: Any valid integer
  EVERY: 5
  # Determines whether Animals is enabled or disabled. Available options: true, false
  ANIMALS: false
  # Determines whether Monsters is enabled or disabled. Available options: true, false
  MONSTERS: false
  # Determines whether Dropped Items is enabled or disabled. Available options: true, false
  DROPPED-ITEMS: true
  # The numerical value for Min Item Age Seconds. Dropped items younger than this are kept,
  # so items dropped just before a cleanup are not wiped. Set to 0 to disable the delay.
  # Available options: Any valid integer
  MIN-ITEM-AGE-SECONDS: 60
  # Configuration section for Excluded Worlds.
  EXCLUDED-WORLDS:
  - duels
  EXCLUDE-NAMED: true
  EXCLUDE-TAMED: true
  EXCLUDE-VILLAGERS: true
  # Configuration section for Excluded Entity Types. Entity types listed here are never
  # cleared, for example ALLAY or IRON_GOLEM.
  EXCLUDED-ENTITY-TYPES: []
  # Configuration section for Excluded Item Materials. Dropped items of these materials are
  # never cleared, for example NETHERITE_INGOT or ELYTRA.
  EXCLUDED-ITEM-MATERIALS: []
```

</details>

---

## Section: `COMBAT-MANAGER`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `COMBAT-MANAGER.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `COMBAT-MANAGER` section on or off. |
| `COMBAT-MANAGER.COOLDOWN` | `integer` | Any integer | `16` | Wait time before the action can run again. |
| `COMBAT-MANAGER.KILL-ON-LOGOUT` | `boolean` | `true`, `false` | `false` | Determines whether Kill On Logout is enabled or disabled. When true, a player who disconnects while their combat tag is still running is killed, so logging out mid-fight is not a way to escape a fight. |
| `COMBAT-MANAGER.ACTION-BAR` | `string` | Any text | `&fCombat: &b${time}s` | Action bar. |
| `COMBAT-MANAGER.MOBS` | `boolean` | `true`, `false` | `false` | Determines whether Mobs is enabled or disabled. When true, damage from mobs also puts players into combat, not just damage dealt by other players. |
| `COMBAT-MANAGER.ENDER-CRYSTAL` | `boolean` | `true`, `false` | `true` | On/off for ender crystal. |
| `COMBAT-MANAGER.ENDER-PEARL` | `boolean` | `true`, `false` | `true` | On/off for ender pearl. |
| `COMBAT-MANAGER.RESPAWN-ANCHOR` | `boolean` | `true`, `false` | `true` | On/off for respawn anchor. |
| `COMBAT-MANAGER.BLOCK-MESSAGE` | `string` | Any text | `&cYou can't use this command in your current status.` | Block message. |
| `COMBAT-MANAGER.BLOCK-COMMANDS` | `list` | A list of values | _list of 5 items_ | The block commands list. |
| `COMBAT-MANAGER.EXCLUDED-WORLDS` | `list` | A list of values | _list of 1 item_ | The excluded worlds list. |

<details>
<summary>Default contents of <code>COMBAT-MANAGER.BLOCK-COMMANDS</code> (5 items)</summary>

```yaml
BLOCK-COMMANDS:
  - '/spawn'
  - '/afk'
  - '/rtp'
  - '/homes'
  - '/tpa'
```

</details>

<details>
<summary>Default contents of <code>COMBAT-MANAGER.EXCLUDED-WORLDS</code> (1 item)</summary>

```yaml
EXCLUDED-WORLDS:
  - 'duels'
```

</details>

<details>
<summary>Default <code>COMBAT-MANAGER</code> block as shipped</summary>

```yaml
# Configuration section for Combat Manager.
COMBAT-MANAGER:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # The numerical value for Cooldown. Available options: Any valid integer
  COOLDOWN: 16
  # Determines whether Kill On Logout is enabled or disabled. When true, a player who
  # disconnects while their combat tag is still running is killed, so logging out mid-fight
  # is not a way to escape a fight. Available options: true, false
  KILL-ON-LOGOUT: false
  # The text or value for Action Bar. Available options: Any valid string text
  ACTION-BAR: '&fCombat: &b${time}s'
  # Determines whether Mobs is enabled or disabled. When true, damage from mobs also puts
  # players into combat, not just damage dealt by other players. Available options: true, false
  MOBS: false
  # Determines whether Ender Crystal is enabled or disabled. Available options: true, false
  ENDER-CRYSTAL: true
  # Determines whether Ender Pearl is enabled or disabled. Available options: true, false
  ENDER-PEARL: true
  # Determines whether Respawn Anchor is enabled or disabled. Available options: true, false
  RESPAWN-ANCHOR: true
  # The text or value for Block Message. Available options: Any valid string text
  BLOCK-MESSAGE: '&cYou can''t use this command in your current status.'
  # Configuration section for Block Commands.
  BLOCK-COMMANDS:
  - /spawn
  - /afk
  - /rtp
  - /homes
  - /tpa
  # Configuration section for Excluded Worlds.
  EXCLUDED-WORLDS:
  - duels
```

</details>

---

## Section: `RTP-ZONE`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RTP-ZONE.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `RTP-ZONE` section on or off. |
| `RTP-ZONE.CUBOID` | `string` | Any text | `''` | Cuboid. |
| `RTP-ZONE.EVERY` | `integer` | Any integer | `30` | Every. |
| `RTP-ZONE.TITLE` | `string` | Any text | `&c&lRTP Zone` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `RTP-ZONE.SUB-TITLE` | `string` | Any text | `&fTeleporting in %countdown%` | Sub title. |
| `RTP-ZONE.CANCELLED-MESSAGE` | `string` | Any text | `&cRTP cancelled because you left the zone.` | Cancelled message. |
| `RTP-ZONE.FAILED-MESSAGE` | `string` | Any text | `&cCould not find a safe RTP zone location.` | Failed message. |
| `RTP-ZONE.SUCCESS-MESSAGE` | `string` | Any text | `''` | Success message. |
| `RTP-ZONE.TITLE-FADE-OUT-TICKS` | `integer` | Any integer | `10` | Title fade out ticks. Ticks (20 = 1 second). |

### `RTP-ZONE.WORLD`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RTP-ZONE.WORLD.NAME` | `string` | Any text | `world` | Display name shown to players. |
| `RTP-ZONE.WORLD.CENTER-X` | `integer` | Any integer | `0` | Center x. |
| `RTP-ZONE.WORLD.CENTER-Z` | `integer` | Any integer | `0` | Center z. |
| `RTP-ZONE.WORLD.MIN-RADIUS` | `integer` | Any integer | `500` | Min radius. Blocks. |
| `RTP-ZONE.WORLD.MAX-RADIUS` | `integer` | Any integer | `2000` | Max radius. Blocks. |

<details>
<summary>Default <code>RTP-ZONE</code> block as shipped</summary>

```yaml
# Configuration section for Rtp Zone.
RTP-ZONE:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # The text or value for Cuboid. Available options: Any valid string text
  CUBOID: ''
  # The numerical value for Every. Available options: Any valid integer
  EVERY: 30
  TITLE: '&c&lRTP Zone'
  # The text or value for Sub Title. Available options: Any valid string text
  SUB-TITLE: '&fTeleporting in %countdown%'
  # The text or value for Cancelled Message. Available options: Any valid string text
  CANCELLED-MESSAGE: '&cRTP cancelled because you left the zone.'
  # The text or value for Failed Message. Available options: Any valid string text
  FAILED-MESSAGE: '&cCould not find a safe RTP zone location.'
  # The text or value for Success Message. Available options: Any valid string text
  SUCCESS-MESSAGE: ''
  # Configuration section for World.
  WORLD:
    NAME: world
    # The numerical value for Center X. Available options: Any valid integer
    CENTER-X: 0
    # The numerical value for Center Z. Available options: Any valid integer
    CENTER-Z: 0
    # The numerical value for Min Radius. Available options: Any valid integer
    MIN-RADIUS: 500
    # The numerical value for Max Radius. Available options: Any valid integer
    MAX-RADIUS: 2000
  # The numerical value for Title Fade Out Ticks. Available options: Any valid integer
  TITLE-FADE-OUT-TICKS: 10
```

</details>

---

## Section: `FIRST-JOIN-RTP`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `FIRST-JOIN-RTP.ENABLED` | `boolean` | `true`, `false` | `false` | Determines whether First Join Rtp is enabled or disabled. Takes priority over Settings Teleport Spawn On First Join. |
| `FIRST-JOIN-RTP.FALLBACK-TO-SPAWN` | `boolean` | `true`, `false` | `true` | Determines whether the player is sent to the spawn location when no safe random location can be found. |
| `FIRST-JOIN-RTP.SEARCHING-MESSAGE` | `string` | Any text | `&7Finding you a safe place to start...` | Searching message. |
| `FIRST-JOIN-RTP.SUCCESS-MESSAGE` | `string` | Any text | `&aWelcome! You spawned at &fX:{x} Y:{y} Z:{z}&a.` | Success message. |
| `FIRST-JOIN-RTP.FAILED-MESSAGE` | `string` | Any text | `&cCould not find a random spawn location for you.` | Failed message. |

### `FIRST-JOIN-RTP.WORLD`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `FIRST-JOIN-RTP.WORLD.NAME` | `string` | Any text | `''` | The world to drop new players in. Leave empty to use the world they join in. |
| `FIRST-JOIN-RTP.WORLD.CENTER-X` | `integer` | Any integer | `0` | Center x. |
| `FIRST-JOIN-RTP.WORLD.CENTER-Z` | `integer` | Any integer | `0` | Center z. |
| `FIRST-JOIN-RTP.WORLD.MIN-RADIUS` | `integer` | Any integer | `500` | Min radius. Blocks. |
| `FIRST-JOIN-RTP.WORLD.MAX-RADIUS` | `integer` | Any integer | `5000` | Max radius. Blocks. |

<details>
<summary>Default <code>FIRST-JOIN-RTP</code> block as shipped</summary>

```yaml
# Configuration section for First Join Rtp. Drops brand new players at a random location
# the first time they join instead of leaving them on the vanilla world spawn. The search
# ignores RTP cooldowns, playtime requirements, and the RTP queue, but it does require the
# RTP feature itself to be enabled.
FIRST-JOIN-RTP:
  # Determines whether First Join Rtp is enabled or disabled. Takes priority over Settings
  # Teleport Spawn On First Join. Available options: true, false
  ENABLED: false
  # Determines whether the player is sent to the spawn location when no safe random
  # location can be found. Available options: true, false
  FALLBACK-TO-SPAWN: true
  # The text or value for Searching Message, sent while the safe location is being looked
  # up. Set to '' to disable. Available options: Any valid string text
  SEARCHING-MESSAGE: '&7Finding you a safe place to start...'
  # The text or value for Success Message, sent once the player has been dropped.
  # Supports {world}, {x}, {y}, {z}. Set to '' to disable. Available options: Any valid
  # string text
  SUCCESS-MESSAGE: '&aWelcome! You spawned at &fX:{x} Y:{y} Z:{z}&a.'
  # The text or value for Failed Message, sent when no safe location could be found. Set
  # to '' to disable. Available options: Any valid string text
  FAILED-MESSAGE: '&cCould not find a random spawn location for you.'
  # Configuration section for World.
  WORLD:
    # The world to drop new players in. Leave empty to use the world they join in.
    # Available options: Any valid string text
    NAME: ''
    # The numerical value for Center X. Available options: Any valid integer
    CENTER-X: 0
    # The numerical value for Center Z. Available options: Any valid integer
    CENTER-Z: 0
    # The numerical value for Min Radius. Available options: Any valid integer
    MIN-RADIUS: 500
    # The numerical value for Max Radius. Available options: Any valid integer
    MAX-RADIUS: 5000
```

</details>

---

## Section: `RESPAWN-RTP`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RESPAWN-RTP.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `RESPAWN-RTP` section on or off. |
| `RESPAWN-RTP.SEARCHING-MESSAGE` | `string` | Any text | `&7Finding you a safe place to respawn...` | Searching message. |
| `RESPAWN-RTP.SUCCESS-MESSAGE` | `string` | Any text | `&aYou respawned at &fX:{x} Y:{y} Z:{z}&a.` | Success message. |
| `RESPAWN-RTP.FAILED-MESSAGE` | `string` | Any text | `&cCould not find a random respawn location for you.` | Failed message. |

### `RESPAWN-RTP.WORLD`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RESPAWN-RTP.WORLD.NAME` | `string` | Any text | `''` | The world to drop dead players in. Leave empty to use the world they died in. |
| `RESPAWN-RTP.WORLD.USE-RTP-BOUNDS` | `boolean` | `true`, `false` | `true` | Determines whether the boundaries from World Settings in rtp.yml are reused for that world. The Center X, Center Z, Min Radius, and Max Radius below are only read when this is false, or when the world has no entry in rtp.yml. |
| `RESPAWN-RTP.WORLD.CENTER-X` | `integer` | Any integer | `0` | Center x. |
| `RESPAWN-RTP.WORLD.CENTER-Z` | `integer` | Any integer | `0` | Center z. |
| `RESPAWN-RTP.WORLD.MIN-RADIUS` | `integer` | Any integer | `500` | Min radius. Blocks. |
| `RESPAWN-RTP.WORLD.MAX-RADIUS` | `integer` | Any integer | `5000` | Max radius. Blocks. |

<details>
<summary>Default <code>RESPAWN-RTP</code> block as shipped</summary>

```yaml
# Configuration section for Respawn Rtp. Sends players back out into the world at a random
# location after they die, instead of leaving them standing on spawn. The search ignores RTP
# cooldowns, playtime requirements, and the RTP queue, but it does require the RTP feature
# itself to be enabled. Players who keep their bed or anchor respawn under Settings Respawn
# On Bed are left alone.
RESPAWN-RTP:
  # Determines whether Respawn Rtp is enabled or disabled. Available options: true, false
  ENABLED: false
  # The text or value for Searching Message, sent while the safe location is being looked
  # up. Set to '' to disable. Available options: Any valid string text
  SEARCHING-MESSAGE: '&7Finding you a safe place to respawn...'
  # The text or value for Success Message, sent once the player has been dropped. Supports
  # {world}, {x}, {y}, {z}. Set to '' to disable. Available options: Any valid string text
  SUCCESS-MESSAGE: '&aYou respawned at &fX:{x} Y:{y} Z:{z}&a.'
  # The text or value for Failed Message, sent when no safe location could be found. The
  # player is left on the normal respawn location. Set to '' to disable. Available options:
  # Any valid string text
  FAILED-MESSAGE: '&cCould not find a random respawn location for you.'
  # Configuration section for World.
  WORLD:
    # The world to drop dead players in. Leave empty to use the world they died in.
    # Available options: Any valid string text
    NAME: ''
    # Determines whether the boundaries from World Settings in rtp.yml are reused for that
    # world. The Center X, Center Z, Min Radius, and Max Radius below are only read when
    # this is false, or when the world has no entry in rtp.yml. Available options: true,
    # false
    USE-RTP-BOUNDS: true
    # The numerical value for Center X. Available options: Any valid integer
    CENTER-X: 0
    # The numerical value for Center Z. Available options: Any valid integer
    CENTER-Z: 0
    # The numerical value for Min Radius. Available options: Any valid integer
    MIN-RADIUS: 500
    # The numerical value for Max Radius. Available options: Any valid integer
    MAX-RADIUS: 5000
```

</details>

---

## Section: `TELEPORT-COOLDOWN`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `TELEPORT-COOLDOWN.HOME` | `integer` | Any integer | `0` | Home. |
| `TELEPORT-COOLDOWN.TEAM-HOME` | `integer` | Any integer | `5` | Team home. |
| `TELEPORT-COOLDOWN.SPAWN` | `integer` | Any integer | `5` | Spawn. |
| `TELEPORT-COOLDOWN.AFK` | `integer` | Any integer | `5` | Afk. |
| `TELEPORT-COOLDOWN.TPA` | `integer` | Any integer | `5` | Tpa. |
| `TELEPORT-COOLDOWN.TPAHERE` | `integer` | Any integer | `5` | Tpahere. |
| `TELEPORT-COOLDOWN.WARP` | `integer` | Any integer | `5` | Warp. |
| `TELEPORT-COOLDOWN.RTP` | `integer` | Any integer | `0` | Rtp. |

<details>
<summary>Default <code>TELEPORT-COOLDOWN</code> block as shipped</summary>

```yaml
# Configuration section for Teleport Cooldown.
TELEPORT-COOLDOWN:
  # The numerical value for Home. Available options: Any valid integer
  HOME: 0
  # The numerical value for Team Home. Available options: Any valid integer
  TEAM-HOME: 5
  # The numerical value for Spawn. Available options: Any valid integer
  SPAWN: 5
  # The numerical value for Afk. Available options: Any valid integer
  AFK: 5
  # The numerical value for Tpa. Available options: Any valid integer
  TPA: 5
  # The numerical value for Tpahere. Available options: Any valid integer
  TPAHERE: 5
  # The numerical value for Warp. Available options: Any valid integer
  WARP: 5
  # The numerical value for Rtp. Available options: Any valid integer
  RTP: 0
```

</details>

---

## Section: `BOUNTY`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `BOUNTY.EXCLUDED-WORLDS` | `list` | A list of values | _list of 1 item_ | The excluded worlds list. |

<details>
<summary>Default contents of <code>BOUNTY.EXCLUDED-WORLDS</code> (1 item)</summary>

```yaml
EXCLUDED-WORLDS:
  - 'duels'
```

</details>

<details>
<summary>Default <code>BOUNTY</code> block as shipped</summary>

```yaml
# Configuration section for Bounty.
BOUNTY:
  # Configuration section for Excluded Worlds.
  EXCLUDED-WORLDS:
  - duels
```

</details>

---

## Section: `VOICE-CHAT`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `VOICE-CHAT.PROMPT-ON-JOIN` | `boolean` | `true`, `false` | `true` | On/off for prompt on join. |
| `VOICE-CHAT.PROMPT-DELAY-TICKS` | `integer` | Any whole number | `40` | The number for Prompt Delay Ticks. |
| `VOICE-CHAT.MUTE-UNTIL-ACCEPTED` | `boolean` | `true`, `false` | `true` | On/off for mute until accepted. |

<details>
<summary>Default <code>VOICE-CHAT</code> block as shipped</summary>

```yaml
# Configuration section for Commands.
# Configuration section for Voice Chat.
VOICE-CHAT:
  # Determines whether Prompt On Join is enabled or disabled. Available options: true, false
  PROMPT-ON-JOIN: true
  # The number for Prompt Delay Ticks. Available options: Any whole number
  PROMPT-DELAY-TICKS: 40
  # Determines whether Mute Until Accepted is enabled or disabled. Available options: true, false
  MUTE-UNTIL-ACCEPTED: true
```

</details>

---

## Section: `FEATURES`

### `FEATURES.TEAMS`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `FEATURES.TEAMS.ENABLED` | `boolean` | `true`, `false` | `false` | Turns the `TEAMS` section on or off. |

<details>
<summary>Default <code>FEATURES</code> block as shipped</summary>

```yaml
# Configuration section for Features.
FEATURES:
  TEAMS:
    ENABLED: false
```

</details>

---

## Section: `COMMANDS`

### Values (31 entries)

| Key | Value | Key | Value | Key | Value |
| :--- | ---: | :--- | ---: | :--- | ---: |
| `CHAT` | `true` | `IGNORE` | `true` | `MESSAGE` | `true` |
| `BOUNTY` | `true` | `CUBOID` | `true` | `AFK` | `true` |
| `SHARDS` | `true` | `WARP` | `true` | `TEAM` | `false` |
| `HOME` | `true` | `LEADERBOARDS` | `true` | `NIGHT-VISION` | `true` |
| `PHANTOM` | `true` | `RTP` | `true` | `SELL` | `true` |
| `SETTINGS` | `true` | `SHOP` | `true` | `ENDERCHEST` | `true` |
| `GAMEMODE` | `true` | `SOCIAL` | `true` | `SPAWN` | `true` |
| `STATS` | `true` | `TPA` | `true` | `TPAUTO` | `true` |
| `FINDPLAYER` | `true` | `CRATE` | `true` | `SHARDPAY` | `false` |
| `RANKS` | `true` | `RULES` | `true` | `HELP` | `true` |
| `SERVERS` | `true` |  |  |  |  |

<details>
<summary>Default <code>COMMANDS</code> block as shipped</summary>

```yaml
COMMANDS:
  # Determines whether Chat is enabled or disabled. Available options: true, false
  CHAT: true
  # Determines whether Ignore is enabled or disabled. Available options: true, false
  IGNORE: true
  # Determines whether Message is enabled or disabled. Available options: true, false
  MESSAGE: true
  # Determines whether Bounty is enabled or disabled. Available options: true, false
  BOUNTY: true
  # Determines whether Cuboid is enabled or disabled. Available options: true, false
  CUBOID: true
  # Determines whether Afk is enabled or disabled. Available options: true, false
  AFK: true
  # Determines whether Shards is enabled or disabled. Available options: true, false
  SHARDS: true
  # Determines whether Warp is enabled or disabled. Available options: true, false
  WARP: true
  # Determines whether Team is enabled or disabled. Available options: true, false
  TEAM: false
  # Determines whether Home is enabled or disabled. Available options: true, false
  HOME: true
  # Determines whether Leaderboards is enabled or disabled. Available options: true, false
  LEADERBOARDS: true
  # Determines whether Night Vision is enabled or disabled. Available options: true, false
  NIGHT-VISION: true
  # Determines whether Phantom is enabled or disabled. Available options: true, false
  PHANTOM: true
  # Determines whether Rtp is enabled or disabled. Available options: true, false
  RTP: true
  # Determines whether Sell is enabled or disabled. Available options: true, false
  SELL: true
  # Determines whether Settings is enabled or disabled. Available options: true, false
  SETTINGS: true
  # Determines whether Shop is enabled or disabled. Available options: true, false
  SHOP: true
  # Determines whether Enderchest is enabled or disabled. Available options: true, false
  ENDERCHEST: true
  # Determines whether Gamemode is enabled or disabled. Available options: true, false
  GAMEMODE: true
  # Determines whether Social is enabled or disabled. Available options: true, false
  SOCIAL: true
  # Determines whether Spawn is enabled or disabled. Available options: true, false
  SPAWN: true
  # Determines whether Stats is enabled or disabled. Available options: true, false
  STATS: true
  # Determines whether Tpa is enabled or disabled. Available options: true, false
  TPA: true
  # Determines whether Tpauto is enabled or disabled. Available options: true, false
# ... section continues, see the file on disk for the full block
```

</details>

---

Defaults above match the file shipped in the jar.
