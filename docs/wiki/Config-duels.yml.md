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
| `MAP_SOURCES.RANDOM_BIOMES.FLAT_POOL.SIZE` | `integer` | Any integer | `2` | Number of pre-prepared flat worlds to keep ready in pool |
| `MAP_SOURCES.RANDOM_BIOMES.FLAT_POOL.PREPARE_INTERVAL_TICKS` | `integer` | Any integer | `20` | Interval in ticks between pool preparation checks |

#### `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL`

Pre-prepared world pool settings for VANILLA terrain mode

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.ENABLED` | `boolean` | `true`, `false` | `true` | Enable pre-generating vanilla terrain duel worlds (true / false) |
| `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.RUNTIME_GENERATION` | `boolean` | `true`, `false` | `true` | Allow background chunk generation for vanilla terrain (true / false) |
| `MAP_SOURCES.RANDOM_BIOMES.VANILLA_POOL.SIZE` | `integer` | Any integer | `2` | Number of pre-prepared vanilla worlds to keep ready in pool |
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
| `GUI.QUEUE.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |

### `GUI.CREATE`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.CREATE.TITLE` | `string` | Any text | `&8Create Duel -&gt; {player}` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.CREATE.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |

### `GUI.CLAIMS`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.CLAIMS.TITLE` | `string` | Any text | `&8Duel Claims` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.CLAIMS.SIZE` | `integer` | Any integer | `54` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `GUI.CLAIMS.ITEMS_PER_PAGE` | `integer` | Any integer | `45` | Items per page. |

<details>
<summary>Default <code>GUI</code> block as shipped</summary>

```yaml
# GUI Inventory Titles and Sizes
GUI:
  QUEUE:
    TITLE: '&8Casual Queue'
    SIZE: 27
  CREATE:
    TITLE: '&8Create Duel -> {player}'
    SIZE: 27
  CLAIMS:
    TITLE: '&8Duel Claims'
    SIZE: 54
    ITEMS_PER_PAGE: 45
```

</details>

---

Defaults above match the file shipped in the jar.
