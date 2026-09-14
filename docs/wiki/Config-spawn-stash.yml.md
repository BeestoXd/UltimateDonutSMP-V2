# `spawn-stash.yml`

Bait for catching players who steal from containers they should not touch. Staff place a
stash of tempting items, and taking from it raises a staff alert naming the player.

`TYPES` defines the different stashes and what each one contains. Alerts go to holders of
`ultimatedonutsmp2.staff.spawnstash.alert`, staff who should not trip their own trap need
`ultimatedonutsmp2.staff.spawnstash.bypass`, and placing or removing stashes needs
`ultimatedonutsmp2.staff.spawnstash`.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/spawn-stash.yml` |
| **Commands** | `/spawnstash` |
| **Player-facing text** | Edit `CONFIG.SPAWN_STASH` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`SETTINGS`](#section-settings) | section | 11 keys |
| [`MESSAGES`](#section-messages) | section | 17 keys |
| [`TYPES`](#section-types) | section | 5 keys |

---

## Section: `SETTINGS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `SETTINGS` section on or off. |
| `SETTINGS.DEFAULT_TTL_SECONDS` | `integer` | Any integer | `900` | Default ttl seconds. Seconds. |
| `SETTINGS.DEFAULT_ALERT_RADIUS` | `decimal` | Any decimal number | `8.0` | Default alert radius. |
| `SETTINGS.ALERT_COOLDOWN_SECONDS` | `integer` | Any integer | `30` | Alert cooldown seconds. Seconds. |
| `SETTINGS.CHECK_INTERVAL_TICKS` | `integer` | Any integer | `20` | Check interval ticks. Ticks (20 = 1 second). |
| `SETTINGS.OVERWRITE_BLOCKS` | `boolean` | `true`, `false` | `true` | On/off for overwrite blocks. |
| `SETTINGS.PROTECT_BLOCKS` | `boolean` | `true`, `false` | `false` | On/off for protect blocks. |
| `SETTINGS.CLAIM_SPAWNERS_ON_BREAK` | `boolean` | `true`, `false` | `false` | On/off for claim spawners on break. |
| `SETTINGS.ROLLBACK_ON_RELOAD` | `boolean` | `true`, `false` | `true` | On/off for rollback on reload. |
| `SETTINGS.LOG_TO_CONSOLE` | `boolean` | `true`, `false` | `true` | On/off for log to console. |
| `SETTINGS.MAX_BLOCKS_PER_STASH` | `integer` | Any integer | `256` | Max blocks per stash. |

<details>
<summary>Default <code>SETTINGS</code> block as shipped</summary>

```yaml
# Configuration section for Settings.
SETTINGS:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # The numerical value for Default Ttl Seconds. Available options: Any valid integer
  DEFAULT_TTL_SECONDS: 900
  # The decimal value for Default Alert Radius. Available options: Any decimal number
  DEFAULT_ALERT_RADIUS: 8.0
  # The numerical value for Alert Cooldown Seconds. Available options: Any valid integer
  ALERT_COOLDOWN_SECONDS: 30
  # The numerical value for Check Interval Ticks. Available options: Any valid integer
  CHECK_INTERVAL_TICKS: 20
  # Determines whether Overwrite Blocks is enabled or disabled. Available options: true, false
  OVERWRITE_BLOCKS: true
  # Determines whether Protect Blocks is enabled or disabled. Available options: true, false
  PROTECT_BLOCKS: false
  # Determines whether Claim Spawners On Break is enabled or disabled. Available options: true, false
  CLAIM_SPAWNERS_ON_BREAK: false
  # Determines whether Rollback On Reload is enabled or disabled. Available options: true, false
  ROLLBACK_ON_RELOAD: true
  # Determines whether Log To Console is enabled or disabled. Available options: true, false
  LOG_TO_CONSOLE: true
  # The numerical value for Max Blocks Per Stash. Available options: Any valid integer
  MAX_BLOCKS_PER_STASH: 256
```

</details>

---

## Section: `MESSAGES`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MESSAGES.USAGE` | `list` | A list of values | _list of 7 items_ | The usage list. |
| `MESSAGES.SPAWNED` | `string` | Any text | `&aspawned stash #{type} successfully. &7(id: {id})` | Spawned. |
| `MESSAGES.REMOVED` | `string` | Any text | `&aremoved stash &f#{id}&a.` | Removed. |
| `MESSAGES.REMOVED-ALL` | `string` | Any text | `&aremoved &f{count}&a active stash(es).` | Removed all. |
| `MESSAGES.EXPIRED` | `string` | Any text | `&7spawnstash #{id} expired and was rolled back.` | Expired. |
| `MESSAGES.RELOADED` | `string` | Any text | `&aspawnstash settings reloaded.` | Reloaded. |
| `MESSAGES.DISABLED` | `string` | Any text | `&cspawnstash is currently disabled.` | Disabled. |
| `MESSAGES.PLAYER-ONLY` | `string` | Any text | `&conly players can use this command.` | Player only. |
| `MESSAGES.NO-PERMISSION` | `string` | Any text | `&cyou do not have permission.` | Permission node. Leave empty to allow everyone. |
| `MESSAGES.NO-ACTIVE` | `string` | Any text | `&cno active stash found.` | No active. |
| `MESSAGES.BLOCKED-BREAK` | `string` | Any text | `&cthis stash is protected. use &f/spawnstash remove neare…` | Blocked break. |
| `MESSAGES.SPAWNER-CLAIMED` | `string` | Any text | `&aclaimed &f{amount}x {spawner}&a from spawnstash.` | Spawner claimed. |
| `MESSAGES.SPAWNER-CLAIMED-DROPPED` | `string` | Any text | `&aclaimed &f{amount}x {spawner}&a. &7inventory full, item…` | Spawner claimed dropped. |
| `MESSAGES.INVALID-TYPE` | `string` | Any text | `&cunknown stash type '&f{type}&c'. use &f/spawnstash list…` | Invalid type. |
| `MESSAGES.INVALID-CONFIG` | `string` | Any text | `&cspawnstash config is invalid: &f{reason}&c.` | Invalid config. |
| `MESSAGES.ALERT` | `list` | A list of values | _list of 2 items_ | The alert list. |
| `MESSAGES.ALERT-HOVER` | `string` | Any text | `&eclick to teleport to &f{player}` | Alert hover. |

<details>
<summary>Default contents of <code>MESSAGES.USAGE</code> (7 items)</summary>

```yaml
USAGE:
  - '&8&m----------- &espawnstash &8&m-----------'
  - '&f/{label} &7- spawn random bait stash'
  - '&f/{label} <type> &7- spawn a bait stash'
  - '&f/{label} spawn <type> &7- spawn a bait stash'
  - '&f/{label} list &7- list active/configured stashes'
  - '&f/{label} remove <id|nearest|all> &7- rollback stashes'
  - '&f/{label} reload &7- reload spawn-stash.yml'
```

</details>

<details>
<summary>Default contents of <code>MESSAGES.ALERT</code> (2 items)</summary>

```yaml
ALERT:
  - '&8[&espawnstash&8] &f{player} &7triggered &e{reason}&7 on stash &f#{id}&7 (&f{type}&7)'
  - '&7location: &f{world} {x}, {y}, {z} &8| &7created by: &f{creator}'
```

</details>

<details>
<summary>Default <code>MESSAGES</code> block as shipped</summary>

```yaml
MESSAGES:
  # Configuration section for Usage.
  USAGE:
  - '&8&m----------- &espawnstash &8&m-----------'
  - '&f/{label} &7- spawn random bait stash'
  - '&f/{label} <type> &7- spawn a bait stash'
  - '&f/{label} spawn <type> &7- spawn a bait stash'
  - '&f/{label} list &7- list active/configured stashes'
  - '&f/{label} remove <id|nearest|all> &7- rollback stashes'
  - '&f/{label} reload &7- reload spawn-stash.yml'
  # The text or value for Spawned. Available options: Any valid string text
  SPAWNED: '&aspawned stash #{type} successfully. &7(id: {id})'
  # The text or value for Removed. Available options: Any valid string text
  REMOVED: '&aremoved stash &f#{id}&a.'
  # The text or value for Removed All. Available options: Any valid string text
  REMOVED-ALL: '&aremoved &f{count}&a active stash(es).'
  # The text or value for Expired. Available options: Any valid string text
  EXPIRED: '&7spawnstash #{id} expired and was rolled back.'
  # The text or value for Reloaded. Available options: Any valid string text
  RELOADED: '&aspawnstash settings reloaded.'
  # The text or value for Disabled. Available options: Any valid string text
  DISABLED: '&cspawnstash is currently disabled.'
  # The text or value for Player Only. Available options: Any valid string text
  PLAYER-ONLY: '&conly players can use this command.'
  # The text or value for No Permission. Available options: Any valid string text
  NO-PERMISSION: '&cyou do not have permission.'
  # The text or value for No Active. Available options: Any valid string text
  NO-ACTIVE: '&cno active stash found.'
  # The text or value for Blocked Break. Available options: Any valid string text
  BLOCKED-BREAK: '&cthis stash is protected. use &f/spawnstash remove nearest &cto
    rollback it.'
  # The text or value for Spawner Claimed. Available options: Any valid string text
  SPAWNER-CLAIMED: '&aclaimed &f{amount}x {spawner}&a from spawnstash.'
  # The text or value for Spawner Claimed Dropped. Available options: Any valid string text
  SPAWNER-CLAIMED-DROPPED: '&aclaimed &f{amount}x {spawner}&a. &7inventory full, item
    dropped.'
  # The text or value for Invalid Type. Available options: Any valid string text
  INVALID-TYPE: '&cunknown stash type ''&f{type}&c''. use &f/spawnstash list&c.'
  # The text or value for Invalid Config. Available options: Any valid string text
  INVALID-CONFIG: '&cspawnstash config is invalid: &f{reason}&c.'
  # Configuration section for Alert.
  ALERT:
  - '&8[&espawnstash&8] &f{player} &7triggered &e{reason}&7 on stash &f#{id}&7 (&f{type}&7)'
  - '&7location: &f{world} {x}, {y}, {z} &8| &7created by: &f{creator}'
  # The text or value for Alert Hover. Available options: Any valid string text
  ALERT-HOVER: '&eclick to teleport to &f{player}'
```

</details>

---

## Section: `TYPES`

### Entry schema (5 entries)

Each entry under `TYPES` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key |  |
| :--- |  |
| `1` |  |
| `2` |  |
| `3` |  |
| `4` |  |
| `5` |  |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `BLOCKS` | `list` | A list of values | Required | The blocks list. |
| `DISPLAY_NAME` | `string` | Any text | Required | Display name. |
| `ENABLED` | `boolean` | `true`, `false` | Required | Turns the `1` section on or off. |
| `PASTE_OFFSET` | `list` | A list of values | Required | The paste offset list. |
| `ALERT_RADIUS` | `decimal` | Any decimal number | Optional (2/5) | Alert radius. |
| `TTL_SECONDS` | `integer` | Any integer | Optional (2/5) | Ttl seconds. Seconds. |

<details>
<summary>Default <code>TYPES</code> block as shipped</summary>

```yaml
# Configuration section for Types.
TYPES:
  # Configuration section for '1'.
  '1':
    # Determines whether Enabled is enabled or disabled. Available options: true, false
    ENABLED: true
    # The text or value for Display Name. Available options: Any valid string text
    DISPLAY_NAME: '&ecompact amethyst stash'
    # The numerical value for Ttl Seconds. Available options: Any valid integer
    TTL_SECONDS: 900
    # The decimal value for Alert Radius. Available options: Any decimal number
    ALERT_RADIUS: 8.0
    # Configuration section for Paste Offset.
    PASTE_OFFSET:
    - 0
    - 0
    - 0
    # Configuration section for Blocks.
    BLOCKS:
    - OFFSET:
      - -1
      - 0
      - 0
      MATERIAL: DEEPSLATE
    - OFFSET:
      - 0
      - 0
      - 0
      MATERIAL: SPAWNER
      # The text or value for Spawner Type. Available options: Any valid string text
      SPAWNER_TYPE: SKELETON
      # The text or value for Spawner Access. Available options: Any valid string text
      SPAWNER_ACCESS: PUBLIC
    - OFFSET:
      - 1
      - 0
      - 0
      MATERIAL: DEEPSLATE
    - OFFSET:
      - -1
      - 0
      - 1
      MATERIAL: COBBLED_DEEPSLATE
    - OFFSET:
      - 0
      - 0
      - 1
      MATERIAL: AMETHYST_BLOCK
# ... section continues, see the file on disk for the full block
```

</details>

---

Defaults above match the file shipped in the jar.
