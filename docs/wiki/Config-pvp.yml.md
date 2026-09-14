# `pvp.yml`

The persistent ranked arena — the one players queue into repeatedly to climb a ladder,
as opposed to the throwaway instances in `ffa.yml`.

It is the largest PvP file because it carries several systems at once: `ELO` for rating,
`RANKS` for the LT/HT ladder those ratings map onto, `LEVELS` for the parallel progression
track, `KITS` for the editable loadouts, `ANTI_KILL_FARMING` to stop two accounts trading
kills, and its own `SCOREBOARD` shown only inside the arena.

`RESET` schedules arena regeneration by pasting a schematic through WorldEdit or FAWE via
console commands — if neither is installed the reset is skipped, so verify one is present
before relying on it. `ARENA` and `KITS` are treated as your data and are never overwritten
by config merging on update.

Comment at the top of the shipped file:

```text
UltimateDonutSMP2 - Ranked PvP Arena Configuration
The ranked arena is a persistent free-for-all area with kits, Elo, ranks and
levels. It does not tag combat on its own - whatever combat plugin you already
run stays in charge of that.
```

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/pvp.yml` |
| **Commands** | `/pvp` |
| **Player-facing text** | Edit `CONFIG.PVP` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`SETTINGS`](#section-settings) | section | 8 keys |
| [`ARENA`](#section-arena) | section | 8 keys |
| [`RESET`](#section-reset) | section | 8 keys |
| [`MATCH`](#section-match) | section | 8 keys |
| [`SYNC`](#section-sync) | section | 3 keys |
| [`BLOCKED_COMMANDS`](#section-blocked-commands) | section | 3 keys |
| [`ELO`](#section-elo) | section | 5 keys |
| [`RANKS`](#section-ranks) | section | 10 keys |
| [`LEVELS`](#section-levels) | section | 4 keys |
| [`ANTI_KILL_FARMING`](#section-anti-kill-farming) | section | 6 keys |
| [`BROADCASTS`](#section-broadcasts) | section | 3 keys |
| [`SCOREBOARD`](#section-scoreboard) | section | 5 keys |
| [`MENUS`](#section-menus) | section | 4 keys |
| [`KITS`](#section-kits) | section | 0 keys |
| [`MESSAGES`](#section-messages) | section | 44 keys |

---

## Section: `SETTINGS`

General ranked arena settings

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.ENABLED` | `boolean` | `true`, `false` | `false` | Enable or disable the ranked PvP arena globally (true / false) |
| `SETTINGS.RESPAWN_DELAY_SECONDS` | `integer` | Any integer | `3` | Seconds a player stays in spectator after dying before the kit menu reopens |
| `SETTINGS.DROP_KIT_ON_DEATH` | `boolean` | `true`, `false` | `false` | Drop the kit items when a player dies in the arena (true / false) |
| `SETTINGS.HEAL_ON_SPAWN` | `boolean` | `true`, `false` | `true` | Restore full health, hunger and remove potion effects on every arena spawn (true / false) |
| `SETTINGS.KILL_OUTSIDE_BOUNDARY` | `boolean` | `true`, `false` | `true` | Kill and remove a player who leaves the arena boundary (true / false) |
| `SETTINGS.SPAWN_PROTECTION_SECONDS` | `integer` | Any integer | `3` | Seconds of protection after spawning, during which the player cannot deal or take damage |
| `SETTINGS.LOBBY_ON_REJOIN` | `boolean` | `true`, `false` | `true` | Send players who disconnect inside the arena to the lobby when they rejoin (true / false) |
| `SETTINGS.CLEAR_KIT_ON_LEAVE` | `boolean` | `true`, `false` | `true` | Take the kit back when a player leaves the arena (true / false). Turn this off if Multiverse-Inventories, or anything else that separates inventories per world, already owns the arena world - it will swap the inventory back on its own. |

<details>
<summary>Default <code>SETTINGS</code> block as shipped</summary>

```yaml
# General ranked arena settings
SETTINGS:
  # Enable or disable the ranked PvP arena globally (true / false)
  ENABLED: false
  # Seconds a player stays in spectator after dying before the kit menu reopens
  RESPAWN_DELAY_SECONDS: 3
  # Drop the kit items when a player dies in the arena (true / false)
  DROP_KIT_ON_DEATH: false
  # Restore full health, hunger and remove potion effects on every arena spawn (true / false)
  HEAL_ON_SPAWN: true
  # Kill and remove a player who leaves the arena boundary (true / false)
  KILL_OUTSIDE_BOUNDARY: true
  # Seconds of protection after spawning, during which the player cannot deal or take damage
  SPAWN_PROTECTION_SECONDS: 3
  # Send players who disconnect inside the arena to the lobby when they rejoin (true / false)
  LOBBY_ON_REJOIN: true
  # Take the kit back when a player leaves the arena (true / false). Turn this off if
  # Multiverse-Inventories, or anything else that separates inventories per world, already
  # owns the arena world - it will swap the inventory back on its own.
  CLEAR_KIT_ON_LEAVE: true
```

</details>

---

## Section: `ARENA`

Arena geometry. These are written by /pvp create, /pvp wand and /pvp setlobby, so there is normally no reason to edit them by hand.

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ARENA.NAME` | `string` | Any text | `Arena` | Display name shown in messages and on the scoreboard |
| `ARENA.WORLD` | `string` | Any text | `''` | World the arena lives in. Empty means the world of the spawn point below. |
| `ARENA.SPAWN` | `string` | Any text | `''` | Where players are put when they join or respawn (world,x,y,z,yaw,pitch) |
| `ARENA.SPAWN_2` | `string` | Any text | `''` | Where the second player in a ranked 1v1 match starts. Empty puts both on SPAWN. |
| `ARENA.LOBBY` | `string` | Any text | `''` | Where players are sent on /pvp leave and after a disconnect (world,x,y,z,yaw,pitch) |
| `ARENA.BOUNDARY_POS1` | `string` | Any text | `''` | The two wand corners of the arena boundary (world,x,y,z,yaw,pitch) |
| `ARENA.BOUNDARY_POS2` | `string` | Any text | `''` | Boundary pos2. |
| `ARENA.BOUNDARY_PADDING` | `integer` | Any integer | `2` | Extra blocks of slack around the boundary before a player counts as outside |

<details>
<summary>Default <code>ARENA</code> block as shipped</summary>

```yaml
# Arena geometry. These are written by /pvp create, /pvp wand and /pvp setlobby,
# so there is normally no reason to edit them by hand.
ARENA:
  # Display name shown in messages and on the scoreboard
  NAME: 'Arena'
  # World the arena lives in. Empty means the world of the spawn point below.
  WORLD: ''
  # Where players are put when they join or respawn (world,x,y,z,yaw,pitch)
  SPAWN: ''
  # Where the second player in a ranked 1v1 match starts. Empty puts both on SPAWN.
  SPAWN_2: ''
  # Where players are sent on /pvp leave and after a disconnect (world,x,y,z,yaw,pitch)
  LOBBY: ''
  # The two wand corners of the arena boundary (world,x,y,z,yaw,pitch)
  BOUNDARY_POS1: ''
  BOUNDARY_POS2: ''
  # Extra blocks of slack around the boundary before a player counts as outside
  BOUNDARY_PADDING: 2
```

</details>

---

## Section: `RESET`

Automatic arena resets, driven by a schematic paste rather than a world regen

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RESET.ENABLED` | `boolean` | `true`, `false` | `false` | Enable the scheduled arena reset (true / false) |
| `RESET.INTERVAL` | `string` | Any text | `24h` | How often the arena resets. Accepts 1d10h15m, 24h, 30m, 90s and combinations. |
| `RESET.WARNING_SECONDS` | `integer` | Any integer | `30` | Seconds of warning broadcast before the reset runs |
| `RESET.EVACUATE` | `boolean` | `true`, `false` | `true` | Teleport everyone in the arena to the lobby before pasting (true / false) |
| `RESET.SCHEMATIC` | `string` | Any text | `arena` | Schematic name as WorldEdit/FastAsyncWorldEdit knows it, without the file extension |
| `RESET.PASTE_LOCATION` | `string` | Any text | `''` | Where the schematic is pasted. Empty pastes at the origin stored in the schematic itself, which is what -o in the paste command below uses. |
| `RESET.COMMANDS` | `list` | A list of values | _list of 2 items_ | Commands run from console to perform the reset. {schematic} is replaced with the schematic name and {x} {y} {z} {world} with the paste location when one is set. FastAsyncWorldEdit understands the same commands as WorldEdit and is much faster on a large arena, so leaving these alone works with either one installed. |
| `RESET.PASTE_DELAY_SECONDS` | `integer` | Any integer | `5` | Seconds to wait between the load command and the paste command |

<details>
<summary>Default contents of <code>RESET.COMMANDS</code> (2 items)</summary>

```yaml
COMMANDS:
  - '//schematic load {schematic}'
  - '//paste -o -a'
```

</details>

<details>
<summary>Default <code>RESET</code> block as shipped</summary>

```yaml
# Automatic arena resets, driven by a schematic paste rather than a world regen
RESET:
  # Enable the scheduled arena reset (true / false)
  ENABLED: false
  # How often the arena resets. Accepts 1d10h15m, 24h, 30m, 90s and combinations.
  INTERVAL: '24h'
  # Seconds of warning broadcast before the reset runs
  WARNING_SECONDS: 30
  # Teleport everyone in the arena to the lobby before pasting (true / false)
  EVACUATE: true
  # Schematic name as WorldEdit/FastAsyncWorldEdit knows it, without the file extension
  SCHEMATIC: 'arena'
  # Where the schematic is pasted. Empty pastes at the origin stored in the schematic
  # itself, which is what -o in the paste command below uses.
  PASTE_LOCATION: ''
  # Commands run from console to perform the reset. {schematic} is replaced with the
  # schematic name and {x} {y} {z} {world} with the paste location when one is set.
  # FastAsyncWorldEdit understands the same commands as WorldEdit and is much faster
  # on a large arena, so leaving these alone works with either one installed.
  COMMANDS:
  - '//schematic load {schematic}'
  - '//paste -o -a'
  # Seconds to wait between the load command and the paste command
  PASTE_DELAY_SECONDS: 5
```

</details>

---

## Section: `MATCH`

Ranked 1v1 matches, the queue that feeds them, and the history they leave behind

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MATCH.ENABLED` | `boolean` | `true`, `false` | `true` | Enable the ranked queue, /pvp assign and the match history (true / false) |
| `MATCH.COUNTDOWN_SECONDS` | `integer` | Any integer | `5` | Seconds both players stand protected at the start of a match before the fight opens |
| `MATCH.MAX_DURATION` | `string` | Any text | `5m` | Longest a match may run before it is called a draw. Accepts the same 1d10h15m format as RESET.INTERVAL. Set it to 0 to let a match run forever. |
| `MATCH.ELO_WIN` | `integer` | Any integer | `20` | Elo the winner gains |
| `MATCH.ELO_LOSS` | `integer` | Any integer | `15` | Elo the loser drops |
| `MATCH.ELO_DRAW` | `integer` | Any integer | `0` | Elo both players take on a draw or an aborted match |
| `MATCH.HEAL_ON_START` | `boolean` | `true`, `false` | `true` | Heal both players and re-hand the kit when a match starts (true / false) |
| `MATCH.DATE_FORMAT` | `string` | Any text | `dd/MM/yyyy HH:mm` | How the date is written in the match history. Standard Java date patterns. |

<details>
<summary>Default <code>MATCH</code> block as shipped</summary>

```yaml
# Ranked 1v1 matches, the queue that feeds them, and the history they leave behind
MATCH:
  # Enable the ranked queue, /pvp assign and the match history (true / false)
  ENABLED: true
  # Seconds both players stand protected at the start of a match before the fight opens
  COUNTDOWN_SECONDS: 5
  # Longest a match may run before it is called a draw. Accepts the same 1d10h15m format
  # as RESET.INTERVAL. Set it to 0 to let a match run forever.
  MAX_DURATION: '5m'
  # Elo the winner gains
  ELO_WIN: 20
  # Elo the loser drops
  ELO_LOSS: 15
  # Elo both players take on a draw or an aborted match
  ELO_DRAW: 0
  # Heal both players and re-hand the kit when a match starts (true / false)
  HEAL_ON_START: true
  # How the date is written in the match history. Standard Java date patterns.
  DATE_FORMAT: 'dd/MM/yyyy HH:mm'
```

</details>

---

## Section: `SYNC`

One-time codes players use to link their Minecraft account to the Discord bot. The bot reads this table, verifies the code and stores the link on its own side, so the plugin never has to know anybody's Discord id.

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SYNC.ENABLED` | `boolean` | `true`, `false` | `true` | Enable /pvp sync (true / false) |
| `SYNC.CODE_LENGTH` | `integer` | Any integer | `6` | How many characters a code has. The alphabet leaves out characters that read as each other, so a player copying it out of chat by eye does not get it wrong. |
| `SYNC.EXPIRES` | `string` | Any text | `10m` | How long a code stays valid. Accepts the same 1d10h15m format as RESET.INTERVAL. |

<details>
<summary>Default <code>SYNC</code> block as shipped</summary>

```yaml
# One-time codes players use to link their Minecraft account to the Discord bot.
# The bot reads this table, verifies the code and stores the link on its own side, so the
# plugin never has to know anybody's Discord id.
SYNC:
  # Enable /pvp sync (true / false)
  ENABLED: true
  # How many characters a code has. The alphabet leaves out characters that read as each
  # other, so a player copying it out of chat by eye does not get it wrong.
  CODE_LENGTH: 6
  # How long a code stays valid. Accepts the same 1d10h15m format as RESET.INTERVAL.
  EXPIRES: '10m'
```

</details>

---

## Section: `BLOCKED_COMMANDS`

Commands players cannot run while they are inside the arena

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `BLOCKED_COMMANDS.ENABLED` | `boolean` | `true`, `false` | `true` | Enable command blocking inside the arena (true / false) |
| `BLOCKED_COMMANDS.COMMANDS` | `list` | A list of values | _list of 7 items_ | Commands to block, with or without the leading slash. Subcommands are covered too, so "shop" also blocks "/shop sell". |
| `BLOCKED_COMMANDS.BLOCK_ENDER_CHEST_BLOCK` | `boolean` | `true`, `false` | `true` | Also block the ender chest when it is opened by clicking the block (true / false) |

<details>
<summary>Default contents of <code>BLOCKED_COMMANDS.COMMANDS</code> (7 items)</summary>

```yaml
COMMANDS:
  - 'shop'
  - 'sell'
  - 'ah'
  - 'auctionhouse'
  - 'ec'
  - 'enderchest'
  - 'orders'
```

</details>

<details>
<summary>Default <code>BLOCKED_COMMANDS</code> block as shipped</summary>

```yaml
# Commands players cannot run while they are inside the arena
BLOCKED_COMMANDS:
  # Enable command blocking inside the arena (true / false)
  ENABLED: true
  # Commands to block, with or without the leading slash. Subcommands are covered too,
  # so "shop" also blocks "/shop sell".
  COMMANDS:
  - 'shop'
  - 'sell'
  - 'ah'
  - 'auctionhouse'
  - 'ec'
  - 'enderchest'
  - 'orders'
  # Also block the ender chest when it is opened by clicking the block (true / false)
  BLOCK_ENDER_CHEST_BLOCK: true
```

</details>

---

## Section: `ELO`

Elo awarded and taken on every rewarded kill

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ELO.STARTING` | `integer` | Any integer | `0` | Elo every player starts on |
| `ELO.GAIN_PER_KILL` | `integer` | Any integer | `25` | Elo the killer gains |
| `ELO.LOSS_PER_DEATH` | `integer` | Any integer | `20` | Elo the victim loses |
| `ELO.MINIMUM` | `integer` | Any integer | `0` | Elo can never fall below this |
| `ELO.MAXIMUM` | `integer` | Any integer | `0` | Elo can never rise above this. 0 removes the cap. |

<details>
<summary>Default <code>ELO</code> block as shipped</summary>

```yaml
# Elo awarded and taken on every rewarded kill
ELO:
  # Elo every player starts on
  STARTING: 0
  # Elo the killer gains
  GAIN_PER_KILL: 25
  # Elo the victim loses
  LOSS_PER_DEATH: 20
  # Elo can never fall below this
  MINIMUM: 0
  # Elo can never rise above this. 0 removes the cap.
  MAXIMUM: 0
```

</details>

---

## Section: `RANKS`

PvP ranks, lowest requirement first. Rank ids are free-form, so LT/HT is only the default naming - rename them, add more or remove some as you like. A player holds the highest rank whose ELO requirement they meet, and drops back down on their own when their Elo falls under it.

### Entry schema (10 entries)

Each entry under `RANKS` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key |  |
| :--- |  |
| `LT5` |  |
| `LT4` |  |
| `LT3` |  |
| `LT2` |  |
| `LT1` |  |
| `HT5` |  |
| `HT4` |  |
| `HT3` |  |
| `HT2` |  |
| `HT1` |  |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DISPLAY` | `string` | Any text | Required | Display. |
| `ELO` | `integer` | Any integer | Required | Elo. |

<details>
<summary>Default <code>RANKS</code> block as shipped</summary>

```yaml
# PvP ranks, lowest requirement first. Rank ids are free-form, so LT/HT is only the
# default naming - rename them, add more or remove some as you like. A player holds
# the highest rank whose ELO requirement they meet, and drops back down on their own
# when their Elo falls under it.
RANKS:
  LT5:
    DISPLAY: '&7LT5'
    ELO: 0
  LT4:
    DISPLAY: '&7LT4'
    ELO: 100
  LT3:
    DISPLAY: '&fLT3'
    ELO: 200
  LT2:
    DISPLAY: '&fLT2'
    ELO: 300
  LT1:
    DISPLAY: '&eLT1'
    ELO: 400
  HT5:
    DISPLAY: '&eHT5'
    ELO: 600
  HT4:
    DISPLAY: '&6HT4'
    ELO: 800
  HT3:
    DISPLAY: '&6HT3'
    ELO: 1000
  HT2:
    DISPLAY: '&cHT2'
    ELO: 1200
  HT1:
    DISPLAY: '&c&lHT1'
    ELO: 1500
```

</details>

---

## Section: `LEVELS`

PvP level progression, separate from Elo

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `LEVELS.XP_PER_KILL` | `integer` | Any integer | `50` | XP a rewarded kill is worth |
| `LEVELS.BASE_XP` | `integer` | Any integer | `100` | XP needed to reach level 2 |
| `LEVELS.XP_INCREASE_PER_LEVEL` | `integer` | Any integer | `50` | Extra XP each further level costs on top of the previous one |
| `LEVELS.MAX_LEVEL` | `integer` | Any integer | `100` | Highest level a player can reach. 0 removes the cap. |

<details>
<summary>Default <code>LEVELS</code> block as shipped</summary>

```yaml
# PvP level progression, separate from Elo
LEVELS:
  # XP a rewarded kill is worth
  XP_PER_KILL: 50
  # XP needed to reach level 2
  BASE_XP: 100
  # Extra XP each further level costs on top of the previous one
  XP_INCREASE_PER_LEVEL: 50
  # Highest level a player can reach. 0 removes the cap.
  MAX_LEVEL: 100
```

</details>

---

## Section: `ANTI_KILL_FARMING`

Stops a player farming Elo and XP off the same opponent

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ANTI_KILL_FARMING.ENABLED` | `boolean` | `true`, `false` | `true` | Enable kill-farm protection (true / false) |
| `ANTI_KILL_FARMING.MAX_REWARDED_KILLS` | `integer` | Any integer | `3` | Rewarded kills allowed against the same player before rewards drop |
| `ANTI_KILL_FARMING.COOLDOWN` | `string` | Any text | `5m` | How long the counter remembers a victim. Accepts the same format as RESET.INTERVAL. |
| `ANTI_KILL_FARMING.REDUCED_ELO` | `integer` | Any integer | `0` | Elo given for a kill past the limit |
| `ANTI_KILL_FARMING.REDUCED_XP` | `integer` | Any integer | `0` | XP given for a kill past the limit |
| `ANTI_KILL_FARMING.APPLY_ELO_LOSS` | `boolean` | `true`, `false` | `false` | Take Elo from the victim on an unrewarded kill (true / false) |

<details>
<summary>Default <code>ANTI_KILL_FARMING</code> block as shipped</summary>

```yaml
# Stops a player farming Elo and XP off the same opponent
ANTI_KILL_FARMING:
  # Enable kill-farm protection (true / false)
  ENABLED: true
  # Rewarded kills allowed against the same player before rewards drop
  MAX_REWARDED_KILLS: 3
  # How long the counter remembers a victim. Accepts the same format as RESET.INTERVAL.
  COOLDOWN: '5m'
  # Elo given for a kill past the limit
  REDUCED_ELO: 0
  # XP given for a kill past the limit
  REDUCED_XP: 0
  # Take Elo from the victim on an unrewarded kill (true / false)
  APPLY_ELO_LOSS: false
```

</details>

---

## Section: `BROADCASTS`

Broadcasts sent when a player levels up or changes rank

### `BROADCASTS.LEVEL_UP`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `BROADCASTS.LEVEL_UP.GLOBAL` | `boolean` | `true`, `false` | `true` | Announce level ups to the whole server instead of only the player (true / false) |
| `BROADCASTS.LEVEL_UP.MESSAGE` | `string` | Any text | `&8[&cPVP&8] &f%player_name% &7has reached PvP Level &c%pv…` | Message. |

### `BROADCASTS.RANK_UP`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `BROADCASTS.RANK_UP.GLOBAL` | `boolean` | `true`, `false` | `true` | Announce rank ups to the whole server instead of only the player (true / false) |
| `BROADCASTS.RANK_UP.MESSAGE` | `string` | Any text | `&8[&cPVP&8] &f%player_name% &7ranked up to %pvp_rank% &7w…` | Message. |

### `BROADCASTS.RANK_DOWN`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `BROADCASTS.RANK_DOWN.GLOBAL` | `boolean` | `true`, `false` | `false` | Announce rank downs to the whole server instead of only the player (true / false) |
| `BROADCASTS.RANK_DOWN.MESSAGE` | `string` | Any text | `&8[&cPVP&8] &f%player_name% &7dropped to %pvp_rank% &7wit…` | Message. |

<details>
<summary>Default <code>BROADCASTS</code> block as shipped</summary>

```yaml
# Broadcasts sent when a player levels up or changes rank
BROADCASTS:
  LEVEL_UP:
    # Announce level ups to the whole server instead of only the player (true / false)
    GLOBAL: true
    MESSAGE: '&8[&cPVP&8] &f%player_name% &7has reached PvP Level &c%pvp_level%&7!'
  RANK_UP:
    # Announce rank ups to the whole server instead of only the player (true / false)
    GLOBAL: true
    MESSAGE: '&8[&cPVP&8] &f%player_name% &7ranked up to %pvp_rank% &7with &c%pvp_elo% &7elo!'
  RANK_DOWN:
    # Announce rank downs to the whole server instead of only the player (true / false)
    GLOBAL: false
    MESSAGE: '&8[&cPVP&8] &f%player_name% &7dropped to %pvp_rank% &7with &c%pvp_elo% &7elo!'
```

</details>

---

## Section: `SCOREBOARD`

The sidebar shown while a player is inside the arena. It replaces the normal scoreboard for as long as they are in there and takes any PlaceholderAPI placeholder, including the %pvp_...% ones this feature registers.

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SCOREBOARD.ENABLED` | `boolean` | `true`, `false` | `true` | Show the arena sidebar instead of the survival one (true / false) |
| `SCOREBOARD.TITLE` | `list` | A list of values | _list of 1 item_ | Animated title frames, cycled at the same speed as the survival scoreboard |
| `SCOREBOARD.LINES` | `list` | A list of values | _list of 10 items_ | Sidebar lines from top to bottom |
| `SCOREBOARD.RESET_FORMAT` | `string` | Any text | `{D}d {H}h {M}m {S}s` | How the reset countdown is written. {d} {h} {m} {s} are the padded parts and {D} {H} {M} {S} the unpadded ones. |
| `SCOREBOARD.RESET_DISABLED_TEXT` | `string` | Any text | `&7-` | Shown by %pvp_arena_reset% when no reset is scheduled |

<details>
<summary>Default contents of <code>SCOREBOARD.TITLE</code> (1 item)</summary>

```yaml
TITLE:
  - '&c&lPVP ARENA'
```

</details>

<details>
<summary>Default contents of <code>SCOREBOARD.LINES</code> (10 items)</summary>

```yaml
LINES:
  - ''
  - '&fPLAYER: &c%player_name%'
  - '&fRANK: %pvp_rank%'
  - '&fLEVEL: &c%pvp_level%'
  - '&fK/D: &c%pvp_kills%&7/&c%pvp_deaths%'
  - ''
  - '&fARENA RESET'
  - '&c%pvp_arena_reset%'
  - ''
  - '&7play.example.net'
```

</details>

<details>
<summary>Default <code>SCOREBOARD</code> block as shipped</summary>

```yaml
# The sidebar shown while a player is inside the arena. It replaces the normal
# scoreboard for as long as they are in there and takes any PlaceholderAPI
# placeholder, including the %pvp_...% ones this feature registers.
SCOREBOARD:
  # Show the arena sidebar instead of the survival one (true / false)
  ENABLED: true
  # Animated title frames, cycled at the same speed as the survival scoreboard
  TITLE:
  - '&c&lPVP ARENA'
  # Sidebar lines from top to bottom
  LINES:
  - ''
  - '&fPLAYER: &c%player_name%'
  - '&fRANK: %pvp_rank%'
  - '&fLEVEL: &c%pvp_level%'
  - '&fK/D: &c%pvp_kills%&7/&c%pvp_deaths%'
  - ''
  - '&fARENA RESET'
  - '&c%pvp_arena_reset%'
  - ''
  - '&7play.example.net'
  # How the reset countdown is written. {d} {h} {m} {s} are the padded parts and
  # {D} {H} {M} {S} the unpadded ones.
  RESET_FORMAT: '{D}d {H}h {M}m {S}s'
  # Shown by %pvp_arena_reset% when no reset is scheduled
  RESET_DISABLED_TEXT: '&7-'
```

</details>

---

## Section: `MENUS`

The four menus this feature opens. Titles and sizes are yours; the layouts are fixed so that a size change cannot leave a button with nowhere to sit.

### `MENUS.QUEUE`

/pvp queue - pick a kit, then confirm to join the ranked queue

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MENUS.QUEUE.TITLE` | `string` | Any text | `&8PvP queue` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `MENUS.QUEUE.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |

#### `MENUS.QUEUE.CONFIRM`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MENUS.QUEUE.CONFIRM.MATERIAL` | `string` | Any text | `LIME_STAINED_GLASS_PANE` | Bukkit `Material` name for the icon. |
| `MENUS.QUEUE.CONFIRM.DISPLAY-NAME` | `string` | Any text | `&aCONFIRM` | Item name. Supports `&` colours and `&#RRGGBB` hex. |

#### `MENUS.QUEUE.CANCEL`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MENUS.QUEUE.CANCEL.MATERIAL` | `string` | Any text | `RED_STAINED_GLASS_PANE` | Bukkit `Material` name for the icon. |
| `MENUS.QUEUE.CANCEL.DISPLAY-NAME` | `string` | Any text | `&cLEAVE` | Item name. Supports `&` colours and `&#RRGGBB` hex. |

### `MENUS.LEADERBOARD`

/pvp leaderboard - one icon per board, each holding its own ranking

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MENUS.LEADERBOARD.TITLE` | `string` | Any text | `&8Leaderboards` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `MENUS.LEADERBOARD.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `MENUS.LEADERBOARD.ENTRIES` | `integer` | Any integer | `10` | How many players each board lists |
| `MENUS.LEADERBOARD.LINE` | `string` | Any text | `&7#{position} &f{player} &8- &c{value}` | How one ranking row is written |

#### `MENUS.LEADERBOARD.ICONS`

The icon each board uses

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MENUS.LEADERBOARD.ICONS.ELO` | `string` | Any text | `DIAMOND` | Elo. |
| `MENUS.LEADERBOARD.ICONS.LEVEL` | `string` | Any text | `EXPERIENCE_BOTTLE` | Level. |
| `MENUS.LEADERBOARD.ICONS.KILLS` | `string` | Any text | `DIAMOND_SWORD` | Kills. |
| `MENUS.LEADERBOARD.ICONS.DEATHS` | `string` | Any text | `SKELETON_SKULL` | Deaths. |
| `MENUS.LEADERBOARD.ICONS.STREAK` | `string` | Any text | `BLAZE_POWDER` | Streak. |
| `MENUS.LEADERBOARD.ICONS.JOINS` | `string` | Any text | `IRON_DOOR` | Joins. |

### `MENUS.HISTORY`

/pvp history - past ranked matches, newest first

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MENUS.HISTORY.TITLE` | `string` | Any text | `&8Match history &7- &f{player}` | Title text. Supports `&` colours and `&#RRGGBB` hex. |

### `MENUS.ASSIGN`

/pvp assign - put two players into a match without either of them queueing

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MENUS.ASSIGN.TITLE` | `string` | Any text | `&8Assign match` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `MENUS.ASSIGN.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |

#### `MENUS.ASSIGN.CONFIRM`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MENUS.ASSIGN.CONFIRM.MATERIAL` | `string` | Any text | `LIME_STAINED_GLASS_PANE` | Bukkit `Material` name for the icon. |
| `MENUS.ASSIGN.CONFIRM.DISPLAY-NAME` | `string` | Any text | `&aSTART MATCH` | Item name. Supports `&` colours and `&#RRGGBB` hex. |

#### `MENUS.ASSIGN.BLOCKED`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MENUS.ASSIGN.BLOCKED.MATERIAL` | `string` | Any text | `RED_STAINED_GLASS_PANE` | Bukkit `Material` name for the icon. |

<details>
<summary>Default <code>MENUS</code> block as shipped</summary>

```yaml
# The four menus this feature opens. Titles and sizes are yours; the layouts are fixed
# so that a size change cannot leave a button with nowhere to sit.
MENUS:
  # /pvp queue - pick a kit, then confirm to join the ranked queue
  QUEUE:
    TITLE: '&8PvP queue'
    SIZE: 27
    CONFIRM:
      MATERIAL: 'LIME_STAINED_GLASS_PANE'
      DISPLAY-NAME: '&aCONFIRM'
    CANCEL:
      MATERIAL: 'RED_STAINED_GLASS_PANE'
      DISPLAY-NAME: '&cLEAVE'
  # /pvp leaderboard - one icon per board, each holding its own ranking
  LEADERBOARD:
    TITLE: '&8Leaderboards'
    SIZE: 27
    # How many players each board lists
    ENTRIES: 10
    # How one ranking row is written
    LINE: '&7#{position} &f{player} &8- &c{value}'
    # The icon each board uses
    ICONS:
      ELO: 'DIAMOND'
      LEVEL: 'EXPERIENCE_BOTTLE'
      KILLS: 'DIAMOND_SWORD'
      DEATHS: 'SKELETON_SKULL'
      STREAK: 'BLAZE_POWDER'
      JOINS: 'IRON_DOOR'
  # /pvp history - past ranked matches, newest first
  HISTORY:
    TITLE: '&8Match history &7- &f{player}'
  # /pvp assign - put two players into a match without either of them queueing
  ASSIGN:
    TITLE: '&8Assign match'
    SIZE: 27
    CONFIRM:
      MATERIAL: 'LIME_STAINED_GLASS_PANE'
      DISPLAY-NAME: '&aSTART MATCH'
    BLOCKED:
      MATERIAL: 'RED_STAINED_GLASS_PANE'
```

</details>

---

## Section: `KITS`

Kit definitions. These are written by /pvp kit create and /pvp kit edit, which store the real items with their enchantments, names and lore, so hand editing is not expected here.

This section ships empty. The plugin fills it in as you create entries in-game, so there is nothing to configure by hand here.

<details>
<summary>Default <code>KITS</code> block as shipped</summary>

```yaml
# Kit definitions. These are written by /pvp kit create and /pvp kit edit, which
# store the real items with their enchantments, names and lore, so hand editing is
# not expected here.
KITS: {}
```

</details>

---

## Section: `MESSAGES`

Messages this feature sends. Every one of them goes through the plugin's colour codes, so &a and &#RRGGBB both work.

### Values (44 entries)

| Key | Value | Key | Value | Key | Value |
| :--- | ---: | :--- | ---: | :--- | ---: |
| `DISABLED` | `&cThe PvP arena is not enabled.` | `NOT_SET_UP` | `&cThe PvP arena has not been set up yet.` | `NO_KITS` | `&cThere are no PvP kits to choose from …` |
| `JOINED` | `&aYou joined the PvP arena.` | `LEFT` | `&aYou left the PvP arena.` | `ALREADY_IN` | `&cYou are already in the PvP arena.` |
| `NOT_IN` | `&cYou are not in the PvP arena.` | `KIT_GIVEN` | `&aYou picked the &f{kit} &akit.` | `KIT_NO_PERMISSION` | `&cYou do not have access to that kit.` |
| `RESPAWN_COUNTDOWN` | `&fRespawning in &c{seconds}&f...` | `BLOCKED_COMMAND` | `&cYou cannot use that command inside th…` | `LEFT_BOUNDARY` | `&cYou left the arena and were removed f…` |
| `KILL_REWARD` | `&8[&cPVP&8] &7You killed &f{victim} &8(…` | `DEATH_PENALTY` | `&8[&cPVP&8] &f{killer} &7killed you &8(…` | `KILL_FARM_LIMIT` | `&7No reward - you have already killed &…` |
| `LEVEL_UP` | `&aYou reached PvP Level &f{level}&a!` | `RANK_UP` | `&aYou ranked up to {rank}&a!` | `RANK_DOWN` | `&cYou dropped to {rank}&c.` |
| `RESET_WARNING` | `&8[&cPVP&8] &7The arena resets in &c{ti…` | `RESET_DONE` | `&8[&cPVP&8] &7The arena has been reset.` | `RESET_NO_SCHEMATIC` | `&cNo schematic is configured for the ar…` |
| `STATS_HEADER` | `&8&m--------&r &cPvP stats: &f{player} …` | `STATS_LINE` | `&7{label}: &f{value}` | `TOP_HEADER` | `&8&m--------&r &cTop PvP players &8&m--…` |
| `TOP_LINE` | `&7#{position} &f{player} &8- &c{elo} el…` | `TOP_EMPTY` | `&7Nobody has fought in the arena yet.` | `QUEUE_JOINED` | `&aYou joined the ranked queue. Waiting …` |
| `QUEUE_LEFT` | `&aYou left the ranked queue.` | `QUEUE_ALREADY_IN` | `&cYou are already in the queue.` | `QUEUE_NOT_IN` | `&cYou are not in the queue.` |
| `MATCH_ALREADY_IN` | `&cYou are already in a ranked match.` | `MATCH_LEAVE_ARENA_FIRST` | `&cLeave the arena before queueing.` | `MATCH_NEEDS_TWO` | `&cA ranked match needs two different pl…` |
| `MATCH_BUSY` | `&c{player} is already in the arena.` | `SYNC_DISABLED` | `&cAccount syncing is switched off.` | `SYNC_FAILED` | `&cCould not create a sync code right no…` |
| `SYNC_HEADER` | `&e&lTier Sync` | `SYNC_CODE` | `&fYour sync code: &e{code}` | `SYNC_HINT` | `&7Use &f/sync {code} &7on our Discord t…` |
| `MATCH_STARTED` | `&8[&cPVP&8] &f{first} &7vs &f{second}` | `MATCH_COUNTDOWN` | `&fStarting in &c{seconds}&f...` | `MATCH_WIN` | `&aYou beat &f{opponent} &8(&a{elo} elo&…` |
| `MATCH_LOSS` | `&cYou lost to &f{opponent} &8(&c{elo} e…` | `MATCH_DRAW` | `&7The match against &f{opponent} &7ende…` |  |  |

<details>
<summary>Default <code>MESSAGES</code> block as shipped</summary>

```yaml
# Messages this feature sends. Every one of them goes through the plugin's colour
# codes, so &a and &#RRGGBB both work.
MESSAGES:
  DISABLED: '&cThe PvP arena is not enabled.'
  NOT_SET_UP: '&cThe PvP arena has not been set up yet.'
  NO_KITS: '&cThere are no PvP kits to choose from yet.'
  JOINED: '&aYou joined the PvP arena.'
  LEFT: '&aYou left the PvP arena.'
  ALREADY_IN: '&cYou are already in the PvP arena.'
  NOT_IN: '&cYou are not in the PvP arena.'
  KIT_GIVEN: '&aYou picked the &f{kit} &akit.'
  KIT_NO_PERMISSION: '&cYou do not have access to that kit.'
  RESPAWN_COUNTDOWN: '&fRespawning in &c{seconds}&f...'
  BLOCKED_COMMAND: '&cYou cannot use that command inside the PvP arena.'
  LEFT_BOUNDARY: '&cYou left the arena and were removed from the fight.'
  KILL_REWARD: '&8[&cPVP&8] &7You killed &f{victim} &8(&c+{elo} elo&8, &c+{xp} xp&8)'
  DEATH_PENALTY: '&8[&cPVP&8] &f{killer} &7killed you &8(&c-{elo} elo&8)'
  KILL_FARM_LIMIT: '&7No reward - you have already killed &f{victim} &7too recently.'
  LEVEL_UP: '&aYou reached PvP Level &f{level}&a!'
  RANK_UP: '&aYou ranked up to {rank}&a!'
  RANK_DOWN: '&cYou dropped to {rank}&c.'
  RESET_WARNING: '&8[&cPVP&8] &7The arena resets in &c{time}&7.'
  RESET_DONE: '&8[&cPVP&8] &7The arena has been reset.'
  RESET_NO_SCHEMATIC: '&cNo schematic is configured for the arena reset.'
  STATS_HEADER: '&8&m--------&r &cPvP stats: &f{player} &8&m--------'
  STATS_LINE: '&7{label}: &f{value}'
  TOP_HEADER: '&8&m--------&r &cTop PvP players &8&m--------'
  TOP_LINE: '&7#{position} &f{player} &8- &c{elo} elo &8(&7{rank}&8)'
  TOP_EMPTY: '&7Nobody has fought in the arena yet.'
  QUEUE_JOINED: '&aYou joined the ranked queue. Waiting for an opponent...'
  QUEUE_LEFT: '&aYou left the ranked queue.'
  QUEUE_ALREADY_IN: '&cYou are already in the queue.'
  QUEUE_NOT_IN: '&cYou are not in the queue.'
  MATCH_ALREADY_IN: '&cYou are already in a ranked match.'
  MATCH_LEAVE_ARENA_FIRST: '&cLeave the arena before queueing.'
  MATCH_NEEDS_TWO: '&cA ranked match needs two different players.'
  MATCH_BUSY: '&c{player} is already in the arena.'
  SYNC_DISABLED: '&cAccount syncing is switched off.'
  SYNC_FAILED: '&cCould not create a sync code right now.'
  SYNC_HEADER: '&e&lTier Sync'
  SYNC_CODE: '&fYour sync code: &e{code}'
  SYNC_HINT: '&7Use &f/sync {code} &7on our Discord to link your account.'
  MATCH_STARTED: '&8[&cPVP&8] &f{first} &7vs &f{second}'
  MATCH_COUNTDOWN: '&fStarting in &c{seconds}&f...'
  MATCH_WIN: '&aYou beat &f{opponent} &8(&a{elo} elo&8, &a{hits} hits&8)'
  MATCH_LOSS: '&cYou lost to &f{opponent} &8(&c{elo} elo&8, &c{hits} hits&8)'
  MATCH_DRAW: '&7The match against &f{opponent} &7ended in a draw.'
```

</details>

---

Defaults above match the file shipped in the jar.
