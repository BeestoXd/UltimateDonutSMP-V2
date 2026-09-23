# `duels.yml`

Runs the 1v1 duel system: the queue players join, the arenas matches are played in, the
countdown and result titles, and the rollback that restores an arena afterwards.

`MAP_SOURCES` is the piece that decides how arenas are produced — duels run in managed
worlds so that damage from one match never leaks into the next. `CROSS_SERVER` lets a
queue span several backend servers, which requires Redis to be enabled in `database.yml`.
`COMMAND_BLOCK` is the list of commands players may not run mid-match, and is your main
defence against someone escaping a losing duel.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/duels.yml` |
| **Commands** | `/duel`, `/queue`, `/arena`, `/create`, `/draw`, `/leave` |
| **Player-facing text** | Edit `CONFIG.DUELS` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`SETTINGS`](#section-settings) | section | 9 keys |
| [`START-COUNTDOWN`](#section-start-countdown) | section | 5 keys |
| [`RESULT-TITLES`](#section-result-titles) | section | 3 keys |
| [`COMMAND_BLOCK`](#section-command-block) | section | 4 keys |
| [`WORLDBORDER`](#section-worldborder) | section | 8 keys |
| [`MAP_SOURCES`](#section-map-sources) | section | 2 keys |
| [`CROSS_SERVER`](#section-cross-server) | section | 9 keys |
| [`ARENA_SETTINGS`](#section-arena-settings) | section | 0 keys |
| [`GUI`](#section-gui) | section | 3 keys |

---

## Section: `SETTINGS`

General duel settings

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.ENABLED` | `boolean` | `true`, `false` | `true` | Enable or disable the duels system globally (true / false) |
| `SETTINGS.COUNTDOWN_SECONDS` | `integer` | Any integer | `5` | Countdown duration before match starts (in seconds) |
| `SETTINGS.MATCH_DURATION_SECONDS` | `integer` | Any integer | `900` | Maximum allowed match duration before forcing a draw (in seconds) |
| `SETTINGS.REQUEST_TIMEOUT_SECONDS` | `integer` | Any integer | `30` | Time before an outgoing duel request expires (in seconds) |
| `SETTINGS.DRAW_REQUEST_TIMEOUT_SECONDS` | `integer` | Any integer | `15` | Time before a draw offer expires (in seconds) |
| `SETTINGS.RETURN_DELAY_SECONDS` | `integer` | Any integer | `3` | Delay before teleporting players back after match ends (in seconds) |
| `SETTINGS.WINNER_RETURN_DELAY_SECONDS` | `integer` | Any integer | `3` | Delay before returning winner (in seconds) |
| `SETTINGS.ROLLBACK_PADDING_HORIZONTAL` | `integer` | Any integer | `8` | Extra horizontal padding blocks preserved around duel arena during arena rollback |
| `SETTINGS.ROLLBACK_PADDING_VERTICAL` | `integer` | Any integer | `6` | Extra vertical padding blocks preserved around duel arena during arena rollback |

<details>
<summary>Default <code>SETTINGS</code> block as shipped</summary>

```yaml
# General duel settings
SETTINGS:
  # Enable or disable the duels system globally (true / false)
  ENABLED: true
  # Countdown duration before match starts (in seconds)
  COUNTDOWN_SECONDS: 5
  # Maximum allowed match duration before forcing a draw (in seconds)
  MATCH_DURATION_SECONDS: 900
  # Time before an outgoing duel request expires (in seconds)
  REQUEST_TIMEOUT_SECONDS: 30
  # Time before a draw offer expires (in seconds)
  DRAW_REQUEST_TIMEOUT_SECONDS: 15
  # Delay before teleporting players back after match ends (in seconds)
  RETURN_DELAY_SECONDS: 3
  # Delay before returning winner (in seconds)
  WINNER_RETURN_DELAY_SECONDS: 3
  # Extra horizontal padding blocks preserved around duel arena during arena rollback
  ROLLBACK_PADDING_HORIZONTAL: 8
  # Extra vertical padding blocks preserved around duel arena during arena rollback
  ROLLBACK_PADDING_VERTICAL: 6
```

</details>

---

## Section: `START-COUNTDOWN`

Countdown titles and sound notifications

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `START-COUNTDOWN.ENABLED` | `boolean` | `true`, `false` | `true` | Enable countdown messages and titles |
| `START-COUNTDOWN.START-MESSAGE` | `string` | Any text | `&aMatch Started!` | Start message. |

### `START-COUNTDOWN.SOUNDS`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `START-COUNTDOWN.SOUNDS.ENABLED` | `boolean` | `true`, `false` | `true` | Play tick sound effects during countdown |

### `START-COUNTDOWN.TITLES`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `START-COUNTDOWN.TITLES.6` | `string` | Any text | `''` | 6. |
| `START-COUNTDOWN.TITLES.5` | `string` | Any text | `&e5` | 5. |
| `START-COUNTDOWN.TITLES.4` | `string` | Any text | `&e4` | 4. |
| `START-COUNTDOWN.TITLES.3` | `string` | Any text | `&c3` | 3. |
| `START-COUNTDOWN.TITLES.2` | `string` | Any text | `&c2` | 2. |
| `START-COUNTDOWN.TITLES.1` | `string` | Any text | `&c1` | 1. |
| `START-COUNTDOWN.TITLES.0` | `string` | Any text | `&a&lFight!` | 0. |

### `START-COUNTDOWN.MESSAGES`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `START-COUNTDOWN.MESSAGES.5` | `string` | Any text | `&a5` | 5. |
| `START-COUNTDOWN.MESSAGES.4` | `string` | Any text | `&a4` | 4. |
| `START-COUNTDOWN.MESSAGES.3` | `string` | Any text | `&a3` | 3. |
| `START-COUNTDOWN.MESSAGES.2` | `string` | Any text | `&a2` | 2. |
| `START-COUNTDOWN.MESSAGES.1` | `string` | Any text | `&a1` | 1. |

<details>
<summary>Default <code>START-COUNTDOWN</code> block as shipped</summary>

```yaml
# Countdown titles and sound notifications
START-COUNTDOWN:
  # Enable countdown messages and titles
  ENABLED: true
  SOUNDS:
    # Play tick sound effects during countdown
    ENABLED: true
  TITLES:
    6: ''
    5: '&e5'
    4: '&e4'
    3: '&c3'
    2: '&c2'
    1: '&c1'
    0: '&a&lFight!'
  MESSAGES:
    '5': '&a5'
    '4': '&a4'
    '3': '&a3'
    '2': '&a2'
    '1': '&a1'
  START-MESSAGE: '&aMatch Started!'
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
| `RESULT-TITLES.victory.subtitle` | `string` | Any text | `&e&lt;player&gt; &fwon the Match!` | Subtitle. |

### `RESULT-TITLES.defeat`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RESULT-TITLES.defeat.title` | `string` | Any text | `&c&lDEFEAT!` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `RESULT-TITLES.defeat.subtitle` | `string` | Any text | `&c&lt;opponent&gt; &fwon this Match!` | Subtitle. |

### `RESULT-TITLES.draw`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RESULT-TITLES.draw.title` | `string` | Any text | `&e&lDRAW!` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `RESULT-TITLES.draw.subtitle` | `string` | Any text | `&fTime's up - no winner.` | Subtitle. |
| `RESULT-TITLES.draw.message` | `string` | Any text | `&e[Timer] &fTime limit reached! Match ended as a &eDRAW &…` | Message. |

<details>
<summary>Default <code>RESULT-TITLES</code> block as shipped</summary>

```yaml
# End match screen titles and subtitles
RESULT-TITLES:
  victory:
    title: '&e&lVICTORY!'
    subtitle: '&e<player> &fwon the Match!'
  defeat:
    title: '&c&lDEFEAT!'
    subtitle: '&c<opponent> &fwon this Match!'
  draw:
    title: '&e&lDRAW!'
    subtitle: '&fTime''s up - no winner.'
    message: '&e[Timer] &fTime limit reached! Match ended as a &eDRAW &f- streaks unchanged.'
```

</details>

---

## Section: `COMMAND_BLOCK`

Command restrictions during a duel

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `COMMAND_BLOCK.ENABLED` | `boolean` | `true`, `false` | `true` | Enable command blocking during a duel match (true / false) |
| `COMMAND_BLOCK.MODE` | `string` | Any text | `ALLOWLIST` | Filtering mode: ALLOWLIST (only specified commands allowed) or BLOCKLIST (specified commands blocked) |
| `COMMAND_BLOCK.COMMANDS` | `list` | A list of values | _list of 4 items_ | List of commands allowed (or blocked depending on MODE) during a duel match |
| `COMMAND_BLOCK.MESSAGE` | `string` | Any text | `&cYou cannot use that command during a duel.` | Message shown to players attempting blocked commands |

<details>
<summary>Default contents of <code>COMMAND_BLOCK.COMMANDS</code> (4 items)</summary>

```yaml
COMMANDS:
  - '/duel'
  - '/draw'
  - '/leave'
  - '/queue'
```

</details>

<details>
<summary>Default <code>COMMAND_BLOCK</code> block as shipped</summary>

```yaml
# Command restrictions during a duel
COMMAND_BLOCK:
  # Enable command blocking during a duel match (true / false)
  ENABLED: true
  # Filtering mode: ALLOWLIST (only specified commands allowed) or BLOCKLIST (specified commands blocked)
  MODE: ALLOWLIST
  # List of commands allowed (or blocked depending on MODE) during a duel match
  COMMANDS:
    - "/duel"
    - "/draw"
    - "/leave"
    - "/queue"
  # Message shown to players attempting blocked commands
  MESSAGE: "&cYou cannot use that command during a duel."
```

</details>

---

## Section: `WORLDBORDER`

World Border settings applied during dynamic random biome duel matches

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `WORLDBORDER.ENABLED` | `boolean` | `true`, `false` | `true` | Enable world border restrictions during duel matches (true / false) |
| `WORLDBORDER.SIZE` | `decimal` | Any decimal number | `96.0` | Size/diameter of the world border in blocks |
| `WORLDBORDER.DAMAGE_BUFFER` | `decimal` | Any decimal number | `0.0` | Safe buffer zone size in blocks before border damage is applied |
| `WORLDBORDER.WARNING_DISTANCE` | `integer` | Any integer | `4` | Distance in blocks from border to display border warning vignette |
| `WORLDBORDER.WARNING_TIME` | `integer` | Any integer | `5` | Time in seconds for border warning pulse animation |
| `WORLDBORDER.ESCAPE_GRACE_TICKS` | `integer` | Any integer | `40` | Grace period in ticks before penalizing a player outside the border |
| `WORLDBORDER.ACTION` | `string` | Any text | `PUSH_BACK` | Action to take when a player steps outside: PUSH_BACK, TELEPORT, or DAMAGE |
| `WORLDBORDER.FALLBACK_ACTION` | `string` | Any text | `FORFEIT` | Fallback action if push back fails: FORFEIT or KILL |

<details>
<summary>Default <code>WORLDBORDER</code> block as shipped</summary>

```yaml
# World Border settings applied during dynamic random biome duel matches
WORLDBORDER:
  # Enable world border restrictions during duel matches (true / false)
  ENABLED: true
  # Size/diameter of the world border in blocks
  SIZE: 96.0
  # Safe buffer zone size in blocks before border damage is applied
  DAMAGE_BUFFER: 0.0
  # Distance in blocks from border to display border warning vignette
  WARNING_DISTANCE: 4
  # Time in seconds for border warning pulse animation
  WARNING_TIME: 5
  # Grace period in ticks before penalizing a player outside the border
  ESCAPE_GRACE_TICKS: 40
  # Action to take when a player steps outside: PUSH_BACK, TELEPORT, or DAMAGE
  ACTION: PUSH_BACK
  # Fallback action if push back fails: FORFEIT or KILL
  FALLBACK_ACTION: FORFEIT
```

</details>

---

## Section: `MAP_SOURCES`

Arena and World Sources configuration

### `MAP_SOURCES.STATIC_WORLDS`

Configuration for pre-built static arenas in existing server worlds

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MAP_SOURCES.STATIC_WORLDS.ENABLED` | `boolean` | `true`, `false` | `true` | Enable static world duel arenas (true / false) |
| `MAP_SOURCES.STATIC_WORLDS.AUTO_LOAD` | `boolean` | `true`, `false` | `true` | Automatically load configured static duel worlds on server startup (true / false) |
| `MAP_SOURCES.STATIC_WORLDS.WORLDS` | `list` | A list of values | _(empty list)_ | List of world names containing static duel arenas (e.g., ["world_duels"]) |

### `MAP_SOURCES.RANDOM_BIOMES`

Configuration for dynamic auto-generated random biome duel worlds

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MAP_SOURCES.RANDOM_BIOMES.ENABLED` | `boolean` | `true`, `false` | `true` | Enable auto-generating random biome duel worlds for matches (true / false) |
| `MAP_SOURCES.RANDOM_BIOMES.TERRAIN_MODE` | `string` | Any text | `FLAT` | Terrain generation mode: FLAT (superflat with biome theme) or VANILLA (natural terrain) |
| `MAP_SOURCES.RANDOM_BIOMES.GENERATE_STRUCTURES` | `boolean` | `true`, `false` | `false` | Whether to generate structures (villages, fortresses) in duel worlds (true / false) |
| `MAP_SOURCES.RANDOM_BIOMES.CLEANUP_AFTER_MATCH` | `boolean` | `true`, `false` | `true` | Automatically unload and delete generated duel worlds after match ends (true / false) |
| `MAP_SOURCES.RANDOM_BIOMES.ARENA_RADIUS` | `integer` | Any integer | `48` | Radius of arena play zone in blocks |
| `MAP_SOURCES.RANDOM_BIOMES.SPAWN_DISTANCE` | `integer` | Any integer | `16` | Distance between player spawn points in blocks |
| `MAP_SOURCES.RANDOM_BIOMES.SPAWN_SEARCH_RADIUS` | `integer` | Any integer | `16` | Search radius when scanning for safe spawn points on vanilla terrain |
| `MAP_SOURCES.RANDOM_BIOMES.WORLD_PREFIX` | `string` | Any text | `duel_biome_` | World name prefix for auto-generated duel worlds |
| `MAP_SOURCES.RANDOM_BIOMES.WORLD_FOLDER` | `string` | Any text | `duel` | Subfolder name for generated duel world files |
| `MAP_SOURCES.RANDOM_BIOMES.ALLOWLIST` | `list` | A list of values | _(empty list)_ | Whitelist of biome keys allowed for selection (empty [] = all biomes allowed) |
| `MAP_SOURCES.RANDOM_BIOMES.EXCLUDE` | `list` | A list of values | _list of 10 items_ | Blacklist of biome keys excluded from selection |

<details>
<summary>Default contents of <code>MAP_SOURCES.RANDOM_BIOMES.EXCLUDE</code> (10 items)</summary>

```yaml
EXCLUDE:
  - 'minecraft:the_end'
  - 'minecraft:end_highlands'
  - 'minecraft:end_midlands'
  - 'minecraft:end_barrens'
  - 'minecraft:small_end_islands'
  - 'minecraft:nether_wastes'
  - 'minecraft:soul_sand_valley'
  - 'minecraft:crimson_forest'
  - 'minecraft:warped_forest'
  - 'minecraft:basalt_deltas'
```

</details>

#### `MAP_SOURCES.RANDOM_BIOMES.FLAT_POOL`

Pre-prepared world pool settings for FLAT terrain mode

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MAP_SOURCES.RANDOM_BIOMES.FLAT_POOL.ENABLED` | `boolean` | `true`, `false` | `true` | Enable pre-generating flat duel worlds in advance (true / false) |
| `MAP_SOURCES.RANDOM_BIOMES.FLAT_POOL.REUSE_WORLDS` | `boolean` | `true`, `false` | `true` | Recycle and reuse clean flat worlds for subsequent matches (true / false) |
| `MAP_SOURCES.RANDOM_BIOMES.FLAT_POOL.PREPARE_INTERVAL_TICKS` | `integer` | Any integer | `20` | Interval in ticks between pool preparation checks |

#### `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL`

Pre-prepared world pool settings for VANILLA terrain mode

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.ENABLED` | `boolean` | `true`, `false` | `true` | Enable pre-generating vanilla terrain duel worlds (true / false) |
| `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.RUNTIME_GENERATION` | `boolean` | `true`, `false` | `true` | Allow background chunk generation for vanilla terrain (true / false) |
| `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.CHUNKS_PER_TICK` | `integer` | Any integer | `1` | Chunks generated per tick to prevent server lag |
| `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.PREPARE_INTERVAL_TICKS` | `integer` | Any integer | `20` | Interval in ticks between vanilla pool preparation ticks |
| `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.MAX_SYNC_STEP_MS` | `integer` | Any integer | `2000` | Maximum allowed time in milliseconds per sync preparation step before pausing |
| `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.PAUSE_ON_SLOW_STEP` | `boolean` | `true`, `false` | `true` | Pause background chunk generation if a step exceeds MAX_SYNC_STEP_MS (true / false) |
| `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.MAX_WATER_PERCENT` | `integer` | Any integer | `40` | Maximum percentage of the arena allowed to be water before it is regenerated (100 = allow any) |
| `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.MAX_TERRAIN_ATTEMPTS` | `integer` | Any integer | `5` | How many times a too-watery arena is regenerated before it is used anyway |

<details>
<summary>Default <code>MAP_SOURCES</code> block as shipped</summary>

```yaml
# Arena and World Sources configuration
MAP_SOURCES:
  # Configuration for pre-built static arenas in existing server worlds
  STATIC_WORLDS:
    # Enable static world duel arenas (true / false)
    ENABLED: true
    # Automatically load configured static duel worlds on server startup (true / false)
    AUTO_LOAD: true
    # List of world names containing static duel arenas (e.g., ["world_duels"])
    WORLDS: []

  # Configuration for dynamic auto-generated random biome duel worlds
  RANDOM_BIOMES:
    # Enable auto-generating random biome duel worlds for matches (true / false)
    ENABLED: true
    # Terrain generation mode: FLAT (superflat with biome theme) or VANILLA (natural terrain)
    TERRAIN_MODE: FLAT
    # Whether to generate structures (villages, fortresses) in duel worlds (true / false)
    GENERATE_STRUCTURES: false
    # Automatically unload and delete generated duel worlds after match ends (true / false)
    CLEANUP_AFTER_MATCH: true
    # Radius of arena play zone in blocks
    ARENA_RADIUS: 48
    # Distance between player spawn points in blocks
    SPAWN_DISTANCE: 16
    # Search radius when scanning for safe spawn points on vanilla terrain
    SPAWN_SEARCH_RADIUS: 16
    # World name prefix for auto-generated duel worlds
    WORLD_PREFIX: duel_biome_
    # Subfolder name for generated duel world files
    WORLD_FOLDER: duel
    # Whitelist of biome keys allowed for selection (empty [] = all biomes allowed)
    ALLOWLIST: []
    # Blacklist of biome keys excluded from selection
    EXCLUDE:
      - "minecraft:the_end"
      - "minecraft:end_highlands"
      - "minecraft:end_midlands"
      - "minecraft:end_barrens"
      - "minecraft:small_end_islands"
      - "minecraft:nether_wastes"
      - "minecraft:soul_sand_valley"
      - "minecraft:crimson_forest"
      - "minecraft:warped_forest"
      - "minecraft:basalt_deltas"

    # Pre-prepared world pool settings for FLAT terrain mode
    FLAT_POOL:
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `CROSS_SERVER`

Cross-server BungeeCord / Velocity Redis sync settings

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CROSS_SERVER.ENABLED` | `boolean` | `true`, `false` | `false` | Enable cross-server duel matchmaking (true / false) |
| `CROSS_SERVER.LOCAL_SERVER_ID` | `string` | Any text | `''` | Unique identifier for this local server instance |
| `CROSS_SERVER.REDIS_CHANNEL` | `string` | Any text | `ultimatedonutsmp2:duels` | Redis channel for duel match communications |
| `CROSS_SERVER.KEY_PREFIX` | `string` | Any text | `uds:duels:` | Redis key prefix for duel data |
| `CROSS_SERVER.STALE_QUEUE_TIMEOUT_SECONDS` | `integer` | Any integer | `45` | Stale queue request timeout (in seconds) |
| `CROSS_SERVER.TRANSFER_TIMEOUT_SECONDS` | `integer` | Any integer | `20` | Player proxy transfer timeout (in seconds) |
| `CROSS_SERVER.PROXY_SERVER_NAME` | `string` | Any text | `''` | Proxy server target name |
| `CROSS_SERVER.ALLOWED_QUEUE_SERVERS` | `list` | A list of values | _(empty list)_ | List of server IDs allowed for duel matchmaking queues |
| `CROSS_SERVER.ALLOWED_MATCH_SERVERS` | `list` | A list of values | _(empty list)_ | List of server IDs allowed to host duel matches |

<details>
<summary>Default <code>CROSS_SERVER</code> block as shipped</summary>

```yaml
# Cross-server BungeeCord / Velocity Redis sync settings
CROSS_SERVER:
  # Enable cross-server duel matchmaking (true / false)
  ENABLED: false
  # Unique identifier for this local server instance
  LOCAL_SERVER_ID: ""
  # Redis channel for duel match communications
  REDIS_CHANNEL: "ultimatedonutsmp2:duels"
  # Redis key prefix for duel data
  KEY_PREFIX: "uds:duels:"
  # Stale queue request timeout (in seconds)
  STALE_QUEUE_TIMEOUT_SECONDS: 45
  # Player proxy transfer timeout (in seconds)
  TRANSFER_TIMEOUT_SECONDS: 20
  # Proxy server target name
  PROXY_SERVER_NAME: ""
  # List of server IDs allowed for duel matchmaking queues
  ALLOWED_QUEUE_SERVERS: []
  # List of server IDs allowed to host duel matches
  ALLOWED_MATCH_SERVERS: []
```

</details>

---

## Section: `ARENA_SETTINGS`

Configuration for static arena definitions (managed via /arena commands)

This section ships empty. The plugin fills it in as you create entries in-game, so there is nothing to configure by hand here.

<details>
<summary>Default <code>ARENA_SETTINGS</code> block as shipped</summary>

```yaml
# Configuration for static arena definitions (managed via /arena commands)
ARENA_SETTINGS: {}
```

</details>

---

## Section: `GUI`

GUI Inventory Titles and Sizes

### `GUI.QUEUE`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.QUEUE.TITLE` | `string` | Any text | `&8Casual Queue` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.QUEUE.SIZE` | `integer` | `9`, `18`, `27`, `36`, `45`, `54` | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `GUI.QUEUE.ITEMS` | `section` | Configuration keys | - | Item titles and lore for queue actions, stats, and map selection. |

### `GUI.QUEUE_MAP_SELECT`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.QUEUE_MAP_SELECT.TITLE` | `string` | Any text | `&8Select duel map` | Title of the queue map selector menu. |
| `GUI.QUEUE_MAP_SELECT.ITEMS` | `section` | Configuration keys | - | Item titles and lores for selecting duel maps and queue back buttons. |

### `GUI.CREATE`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.CREATE.TITLE` | `string` | Any text | `&8Create Duel -&gt; {player}` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.CREATE.SIZE` | `integer` | `9`, `18`, `27`, `36`, `45`, `54` | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `GUI.CREATE.ITEMS` | `section` | Configuration keys | - | Item titles and lores for challenge maps, targets, and privacy mode toggles. |

### `GUI.CLAIMS`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.CLAIMS.TITLE` | `string` | Any text | `&8Duel Claims` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.CLAIMS.SIZE` | `integer` | `9`, `18`, `27`, `36`, `45`, `54` | `54` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `GUI.CLAIMS.ITEMS_PER_PAGE` | `integer` | Any integer `1-45` | `45` | Items per page. |
| `GUI.CLAIMS.ITEMS` | `section` | Configuration keys | - | Item titles and lores for pagination, refresh, and claim entries. |

### `GUI.CLAIM_PREVIEW`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.CLAIM_PREVIEW.TITLE` | `string` | Any text | `&8duel loot preview` | Title of the single claim package preview menu. |
| `GUI.CLAIM_PREVIEW.SIZE` | `integer` | `9`, `18`, `27`, `36`, `45`, `54` | `54` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `GUI.CLAIM_PREVIEW.ITEMS` | `section` | Configuration keys | - | Item titles and lores for claiming all items or deleting the package. |

<details>
<summary>Default <code>GUI</code> block as shipped</summary>

```yaml
# GUI Inventory Titles and Sizes
GUI:
  QUEUE:
    TITLE: '&8Casual Queue'
    SIZE: 27
    ITEMS:
      LEAVE_QUEUE:
        NAME: '&cleave queue'
        LORE:
          - '&7players queued: &f{queued}'
          - '&7click to leave the duel queue.'
      NO_MAPS:
        NAME: '&cno queue maps available'
        LORE:
          - '&7configure queue arenas or enable random biomes.'
      JOIN_QUEUE:
        NAME: '&ajoin casual queue'
        LORE_QUEUED: '&7players queued: &f{queued}'
        LORE_SELECTED: '&7selected: &f{selected}'
        LORE_VANILLA_DISABLED: '&7mode: &fvanilla generation disabled'
        LORE_ENABLE_VANILLA: '&7enable vanilla_pool.runtime_generation.'
        LORE_FLAT_MODE: '&7mode: &fflat biome arena'
        LORE_FLAT_DESC: '&7uses lightweight generated flat terrain.'
        LORE_STATIC_MAP: '&7map: &f{selected}'
        LORE_STATIC_DESC: '&7uses a configured custom duel map.'
        LORE_DEFAULT_MODE: '&7mode: &fdefault queue arena'
        LORE_DEFAULT_DESC: '&7uses an available configured duel arena.'
        LORE_CLICK: '&eclick to join queue.'
      SELECT_MAP:
        NAME: '&bselect map'
        LORE_NONE: '&7no map is selected.'
        LORE_SELECTED: '&7selected: &f{selected}'
        LORE_CLICK: '&eclick to choose arena or biome.'
      STATS:
        NAME: '&eyour duel stats'
        LORE:
          - '&7wins: &f{wins}'
          - '&7losses: &f{losses}'
          - '&7draws: &f{draws}'
          - '&7streak: &f{streak}'
          - '&7best streak: &f{best_streak}'
      CLAIMS:
        NAME: '&dclaims'
        LORE:
          - '&7open duel loot claim packages.'
      CLOSE:
        NAME: '&cclose'
  QUEUE_MAP_SELECT:
    TITLE: '&8Select duel map'
    ITEMS:
      SELECTED_PREFIX: '&a'
      UNSELECTED_PREFIX: '&e'
      CURRENTLY_SELECTED_LORE: '&aCurrently selected.'
      CLICK_TO_SELECT_LORE: '&eClick to select.'
      NO_MAPS:
        NAME: '&cNo queue maps available'
        LORE:
          - '&7Configure queue arenas or enable random biomes.'
      BACK:
        NAME: '&eBack'
        LORE:
          - '&7Return to queue menu.'
      CLOSE:
        NAME: '&cClose'
  CREATE:
    TITLE: '&8Create Duel -> {player}'
    SIZE: 27
    ITEMS:
      TARGET_OFFLINE:
        NAME: '&ctarget offline'
        LORE:
          - '&7this player is no longer online.'
      NO_MAPS:
        NAME: '&cno duel maps available'
        LORE:
          - '&7configure arenas or enable random biomes.'
      MAP_OPTION:
        NAME: '&a{map}'
        LORE:
          - '&7privacy: &f{privacy}'
          - '&7target: &f{target}'
          - '&7{description}'
          - '&eclick to send challenge.'
      TARGET_HEAD:
        NAME: '&etarget: &f{target}'
        LORE:
          - '&7choose a map to send a duel request.'
      PRIVACY_BUTTON:
        NAME: '&bprivacy: &f{privacy}'
        LORE_FRIENDS: '&7only same-team members can accept this duel.'
        LORE_INVITE: '&7direct invite duel.'
      CLOSE:
        NAME: '&cclose'
  CLAIMS:
    TITLE: '&8Duel Claims'
    SIZE: 54
    ITEMS_PER_PAGE: 45
    ITEMS:
      PREVIOUS_PAGE:
        NAME: '&aprevious page'
      REFRESH:
        NAME: '&erefresh'
      NEXT_PAGE:
        NAME: '&anext page'
      BACK:
        NAME: '&cback'
      NO_CLAIMS:
        NAME: '&cno pending claims'
        LORE:
          - '&7loot from duel wins will show up here.'
      CLAIM_ENTRY:
        NAME: '&eloot from &f{player}'
        LORE:
          - '&7match: &f#{match_id}'
          - '&7stored items: &f{count}'
          - '&7click to preview this loot package.'
          - '&8delete is available inside the preview.'
  CLAIM_PREVIEW:
    TITLE: '&8duel loot preview'
    SIZE: 54
    ITEMS:
      CLAIM_NOT_FOUND:
        NAME: '&cclaim not found'
        LORE:
          - '&7this duel loot package no longer exists.'
      BACK_ARROW:
        NAME: '&aback'
      BACK_BARRIER:
        NAME: '&cback'
      SUMMARY:
        NAME: '&eloot summary'
        LORE:
          - '&7defeated player: &f{player}'
          - '&7match: &f#{match_id}'
          - '&7stored items: &f{count}'
      CLAIM_ALL:
        NAME: '&aclaim all'
        LORE:
          - '&7move all fitting items into your inventory.'
          - '&7if some do not fit, they stay in claims.'
      DELETE_CLAIM:
        NAME: '&cdelete claim'
        LORE:
          - '&7delete this entire loot package.'
          - '&7this action cannot be undone.'
```

</details>

---

## Section: `MESSAGES`

Duel system feedback and notification messages

### `MESSAGES`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MESSAGES.PREFIX` | `string` | Any string | `''` | Optional prefix prepended to all duel chat messages. |
| `MESSAGES.*` | `string` | Any string with color codes and placeholders | Various | Chat feedback strings for queues, requests, matches, claims, and arena validation. |

<details>
<summary>Default <code>MESSAGES</code> block as shipped</summary>

```yaml
# Duel system feedback and notification messages
MESSAGES:
  PREFIX: ''
  DISABLED: '&cduels are currently disabled.'
  NO_PERMISSION_RELOAD: '&cyou do not have permission to reload duels.'
  RELOAD_SUCCESS: '&aduels config reloaded.'
  PLAYER_NOT_ONLINE: '&cthat player is not online.'
  USAGE_INVITE: '&cusage: /create invite <player> [map]'
  USAGE_FRIENDS: '&cusage: /create friends <player> [map]'
  QUEUE_JOINED: '&ajoined the casual duel queue.'
  QUEUE_LEFT: '&eyou left the casual duel queue.'
  QUEUE_ALREADY_IN: '&cyou are already in the queue.'
  QUEUE_ALREADY_IN_CASUAL: '&eyou are already in the casual duel queue.'
  PREPARING_CANCELLED: '&eyour preparing duel has been cancelled.'
  PENDING_CLEARED: '&eyour pending duel request was cleared.'
  OUTGOING_CANCELLED: '&eyour outgoing duel request was cancelled.'
  NOT_IN_DUEL_OR_QUEUE: '&cyou are not in a duel or queue.'
  DRAW_ONLY_ACTIVE: '&cyou can only request a draw during an active duel.'
  DRAW_OPPONENT_OFFLINE: '&cyour opponent is no longer online.'
  DRAW_SENT: '&edraw request sent to &f{player}&e.'
  DRAW_RECEIVED: '&e{player} &fhas requested a draw. use &f/draw &fto accept.'
  DRAW_ALREADY_REQUESTED: '&eyou already requested a draw.'
  DRAW_EXPIRED: '&cyour draw request expired.'
  DEFEATED_PLAYER: '&ayou defeated &f{player}&a.'
  LOST_DUEL: '&cyou lost the duel against &f{player}&c.'
  MOVED_OUT_OF_ARENA: '&eyou were moved out of duel arena &f{arena}&e after reconnecting.'
  DUEL_STARTED: '&aduel started against &f{player}&a.'
  LOOT_SENT_TO_CLAIMS: '&eloot from &f{player} &ehas been sent to your duel claims.'
  PREPARING_BIOME: '&epreparing duel biome arena...'
  CANCELLED_PLAYER_LEFT: '&cduel cancelled because one player left preparation.'
  CANCELLED_PLAYER_UNAVAILABLE: '&cduel cancelled because one player is no longer available.'
  NO_BIOME_ARENA: '&cno duel biome arena is available right now.'
  UNSAFE_ITEMS: '&cthe duel could not start because your opponent has unsafe item data.'
  COULD_NOT_START: '&ccould not start the duel right now.'
  DUEL_FOUND: '&aduel found against &f{player}&a on arena &f{arena}&a.'
  ALREADY_IN_DUEL: '&cyou are already in a duel.'
  ARENA_PREPARING: '&cyour duel arena is preparing.'
  ALREADY_HAVE_REQUEST: '&cyou already have a pending duel request.'
  CANNOT_USE_FFA: '&cyou cannot use duels while inside the ffa system.'
  FRIENDS_SAME_TEAM: '&cfriends-only duels require both players to be in the same team.'
  FRIENDS_ONLY_TEAM: '&cfriends-only duels can only target members of your team.'
  CROSS_SERVER_PREPARING: '&across-server duel found. preparing match...'
  CROSS_SERVER_TRANSFERRING: '&across-server duel found. transferring to match server...'
  CROSS_SERVER_NO_ARENA: '&ccross-server duel could not start because no arena is available.'
  REQUEST_EXPIRED_TO: '&cyour duel request to &f{player} &cexpired.'
  REQUEST_EXPIRED_FROM: '&cyour duel request from &f{player} &cexpired.'
  REQUEST_CLEARED: '&cyour duel request was cleared.'
  REQUEST_CANCELLED: '&cthat duel request was cancelled.'
  REQUEST_SENT: '&asent a duel request to &f{player}&a.'
  CHALLENGE_RECEIVED: '&e{player} &fhas challenged you to a duel.'
  CHALLENGE_INSTRUCTION: '&7use &f/duel accept {player} &7or &f/duel deny {player}&7.'
  NO_PENDING_REQUEST: '&cyou have no pending duel request.'
  REQUEST_EXPIRED: '&cthat duel request has expired.'
  REQUEST_PENDING_FROM: '&cyour pending duel request is from &f{player}&c.'
  CHALLENGER_OFFLINE: '&cthat challenger is no longer online.'
  REQUEST_DENIED_SENDER: '&edenied duel request from &f{player}&e.'
  REQUEST_DENIED_TARGET: '&c{player} denied your duel request.'
  CANNOT_DUEL_SELF: '&cyou cannot duel yourself.'
  TARGET_NOT_ACCEPTING: '&cthat player is not accepting duel requests.'
  NO_ARENA_AVAILABLE: '&cno duel arena is available right now.'
  NO_MAP_AVAILABLE: '&cthat duel map is not available.'
  ARENA_NOT_AVAILABLE: '&cthat arena is not available.'
  NO_READY_ARENAS: '&cthere are no duel arenas ready yet.'
  REQUEST_COULD_NOT_START: '&cyour duel request could not start because no arena is available.'
  CLAIM_NO_LONGER_EXISTS: '&cthat duel claim no longer exists.'
  CLAIM_MAKE_ROOM: '&cmake room in your inventory before claiming that loot.'
  CLAIM_PARTIAL: '&eclaimed some duel loot from &f{player}&e. &7some items are still waiting in claims.'
  CLAIM_SUCCESS: '&aclaimed duel loot from &f{player}&a.'
  CLAIM_DELETE_FAILED: '&ccould not delete that duel claim right now.'
  CLAIM_DELETED: '&cdeleted duel loot claim from &f{player}&c.'
  QUEUE_ARENAS_NOT_READY: '&cqueue arenas exist but are not ready yet. &7use &f/arena setpos1 <id> &7and &f/arena setpos2 <id> &7for: &f{arenas}&7.'
  QUEUE_NO_READY_ARENAS: '&cno ready queue arenas are configured yet. &7enable queue with &f/arena queue <id> true&7, then set &fpos1 &7and &fpos2&7.'
```

</details>

---

Defaults above match the file shipped in the jar.
