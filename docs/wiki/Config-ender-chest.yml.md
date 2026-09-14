# `ender-chest.yml`

Replaces the vanilla ender chest with a plugin-managed one stored in the database, which is
what makes it work across a network and lets staff inspect it with `/ecsee`.

Row count is permission-tiered: `ultimatedonutsmp2.enderchest.rows.<N>` overrides the
default size configured here, so donor ranks can be given a larger chest without a separate
feature. Editing another player's chest through `/ecsee` additionally requires
`ultimatedonutsmp2.admin.ecsee.edit`.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/ender-chest.yml` |
| **Commands** | `/enderchest` (`/ec`), `/ecsee` |
| **Player-facing text** | Edit `CONFIG.ENDER_CHEST` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`ENDER-CHEST`](#section-ender-chest) | section | 10 keys |
| [`MESSAGES`](#section-messages) | section | 7 keys |

---

## Section: `ENDER-CHEST`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ENDER-CHEST.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `ENDER-CHEST` section on or off. |
| `ENDER-CHEST.DEFAULT-ROWS` | `integer` | 1 to 6 | `6` | Rows every player gets when no ROW-PERMISSIONS entry applies to them 6 rows is 54 slots, already the largest a chest can be, so lower this if you want ROW-PERMISSIONS below to hand out bigger chests as a rank perk |
| `ENDER-CHEST.TITLE` | `string` | Any text | `&5Ender Chest` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `ENDER-CHEST.INTERCEPT-VANILLA-OPEN` | `boolean` | `true`, `false` | `true` | On/off for intercept vanilla open. |
| `ENDER-CHEST.ALLOW-COMMAND` | `boolean` | `true`, `false` | `true` | Console command, without a leading slash. Empty means none. |
| `ENDER-CHEST.COMMAND-REQUIRES-PERMISSION` | `boolean` | `true`, `false` | `false` | Permission node. Leave empty to allow everyone. |
| `ENDER-CHEST.PERMISSION` | `string` | Any text | `ultimatedonutsmp2.enderchest` | Permission node. Leave empty to allow everyone. |
| `ENDER-CHEST.AUTO-SAVE-TICKS` | `integer` | Any integer | `1200` | Auto save ticks. Ticks (20 = 1 second). |

### `ENDER-CHEST.ROW-PERMISSIONS`

Per-rank Ender Chest size resolved from permissions

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ENDER-CHEST.ROW-PERMISSIONS.ENABLED` | `boolean` | `true`, `false` | `true` | Enable or disable permission based Ender Chest sizes |
| `ENDER-CHEST.ROW-PERMISSIONS.ON-DOWNGRADE` | `string` | Any text | `KEEP-SIZE` | On downgrade. |

#### `ENDER-CHEST.ROW-PERMISSIONS.PERMISSIONS`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ENDER-CHEST.ROW-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.enderchest.rows.vip++` | `integer` | Any integer | `6` | Ultimatedonutsmp2.enderchest.rows.vip++. |
| `ENDER-CHEST.ROW-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.enderchest.rows.vip+` | `integer` | Any integer | `5` | Ultimatedonutsmp2.enderchest.rows.vip+. |
| `ENDER-CHEST.ROW-PERMISSIONS.PERMISSIONS.ultimatedonutsmp2.enderchest.rows.vip` | `integer` | Any integer | `4` | Ultimatedonutsmp2.enderchest.rows.vip. |

### `ENDER-CHEST.ECSEE`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ENDER-CHEST.ECSEE.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `ECSEE` section on or off. |
| `ENDER-CHEST.ECSEE.PERMISSION` | `string` | Any text | `ultimatedonutsmp2.admin.ecsee` | Permission node. Leave empty to allow everyone. |
| `ENDER-CHEST.ECSEE.AUTO-REFRESH-TICKS` | `integer` | Any integer | `20` | Auto refresh ticks. Ticks (20 = 1 second). |
| `ENDER-CHEST.ECSEE.EDITABLE` | `boolean` | `true`, `false` | `false` | Determines whether staff can edit other players' ender chests. |

<details>
<summary>Default <code>ENDER-CHEST</code> block as shipped</summary>

```yaml
# Configuration section for Ender Chest.
ENDER-CHEST:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # Rows every player gets when no ROW-PERMISSIONS entry applies to them
  # 6 rows is 54 slots, already the largest a chest can be, so lower this if you want
  # ROW-PERMISSIONS below to hand out bigger chests as a rank perk
  # Available options: 1 to 6
  DEFAULT-ROWS: 6
  TITLE: '&5Ender Chest'
  # Determines whether Intercept Vanilla Open is enabled or disabled. Available options: true, false
  INTERCEPT-VANILLA-OPEN: true
  # Determines whether Allow Command is enabled or disabled. Available options: true, false
  ALLOW-COMMAND: true
  # Determines whether Command Requires Permission is enabled or disabled. Available options: true, false
  COMMAND-REQUIRES-PERMISSION: false
  # The text or value for Permission. Available options: Any valid string text
  PERMISSION: ultimatedonutsmp2.enderchest
  # The numerical value for Auto Save Ticks. Available options: Any valid integer
  AUTO-SAVE-TICKS: 1200
  # Per-rank Ender Chest size resolved from permissions
  ROW-PERMISSIONS:
    # Enable or disable permission based Ender Chest sizes
    ENABLED: true
    # What happens when a player's permissions no longer cover the size their chest was saved at,
    # for example after a rank expires
    # KEEP-SIZE keeps the chest at the larger size so nothing can be lost
    # RETURN-ITEMS shrinks the chest and hands back everything that no longer fits
    # Available options: KEEP-SIZE, RETURN-ITEMS
    ON-DOWNGRADE: KEEP-SIZE
    # Explicit mapping from permission node to Ender Chest rows
    # Players can also be given ultimatedonutsmp2.enderchest.rows.<1-6> directly, for example
    # ultimatedonutsmp2.enderchest.rows.4 for a 36 slot chest
    # The highest value the player has wins
    # Players without any of these permissions keep DEFAULT-ROWS above
    PERMISSIONS:
      "ultimatedonutsmp2.enderchest.rows.vip++": 6
      "ultimatedonutsmp2.enderchest.rows.vip+": 5
      "ultimatedonutsmp2.enderchest.rows.vip": 4
  # Configuration section for Ecsee.
  ECSEE:
    # Determines whether Enabled is enabled or disabled. Available options: true, false
    ENABLED: true
    # The text or value for Permission. Available options: Any valid string text
    PERMISSION: ultimatedonutsmp2.admin.ecsee
    # The numerical value for Auto Refresh Ticks. Available options: Any valid integer
    AUTO-REFRESH-TICKS: 20
    # Determines whether staff can edit other players' ender chests. Available options: true, false
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `MESSAGES`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MESSAGES.FEATURE-DISABLED` | `string` | Any text | `&cThe Ender Chest 6 Rows system is disabled.` | Feature disabled. |
| `MESSAGES.COMMAND-DISABLED` | `string` | Any text | `&cThe /enderchest command is disabled.` | Command disabled. |
| `MESSAGES.NO-PERMISSION` | `string` | Any text | `&cYou do not have permission to use this command.` | Permission node. Leave empty to allow everyone. |
| `MESSAGES.OPEN-FAILED` | `string` | Any text | `&cFailed to open your Ender Chest. Please try again.` | Open failed. |
| `MESSAGES.SAVE-FAILED` | `string` | Any text | `&cFailed to save your Ender Chest. Contact staff.` | Save failed. |
| `MESSAGES.RELOAD-SUCCESS` | `string` | Any text | `&aEnder Chest config reloaded.` | Reload success. |
| `MESSAGES.ROWS-DOWNGRADED` | `string` | Any text | `&eYour Ender Chest is now {rows} rows. {amount} item(s) t…` | Shown when a chest shrinks and the items that no longer fit are handed back The text or value for Rows Downgraded. |

<details>
<summary>Default <code>MESSAGES</code> block as shipped</summary>

```yaml
MESSAGES:
  # The text or value for Feature Disabled. Available options: Any valid string text
  FEATURE-DISABLED: '&cThe Ender Chest 6 Rows system is disabled.'
  # The text or value for Command Disabled. Available options: Any valid string text
  COMMAND-DISABLED: '&cThe /enderchest command is disabled.'
  # The text or value for No Permission. Available options: Any valid string text
  NO-PERMISSION: '&cYou do not have permission to use this command.'
  # The text or value for Open Failed. Available options: Any valid string text
  OPEN-FAILED: '&cFailed to open your Ender Chest. Please try again.'
  # The text or value for Save Failed. Available options: Any valid string text
  SAVE-FAILED: '&cFailed to save your Ender Chest. Contact staff.'
  # The text or value for Reload Success. Available options: Any valid string text
  RELOAD-SUCCESS: '&aEnder Chest config reloaded.'
  # Shown when a chest shrinks and the items that no longer fit are handed back
  # The text or value for Rows Downgraded. Available options: Any valid string text
  ROWS-DOWNGRADED: '&eYour Ender Chest is now {rows} rows. {amount} item(s) that no longer fit were returned to you.'
```

</details>

---

Defaults above match the file shipped in the jar.
