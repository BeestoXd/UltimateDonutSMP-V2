# `freeze.yml`

Freezes a suspected cheater in place while staff talk to them. The listener blocks
movement, interaction and most commands, and this file decides how obvious that is to the
player: the on-screen messaging, the effects applied, and what happens if they log out
while frozen.

The logout rule is the important one — without it, disconnecting is a free escape.

Comment at the top of the shipped file:

```text
UltimateDonutSMP2 - Freeze System Configuration
```

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/freeze.yml` |
| **Commands** | `/freeze` |
| **Player-facing text** | Edit `CONFIG.FREEZE` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`FREEZE`](#section-freeze) | section | 17 keys |
| [`MESSAGES`](#section-messages) | section | 10 keys |

---

## Section: `FREEZE`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `FREEZE.ENABLED` | `boolean` | `true`, `false` | `true` | Enable or disable the freeze system globally (true / false) |
| `FREEZE.PERSIST-ON-QUIT` | `boolean` | `true`, `false` | `true` | Preserve frozen status across player logouts (true / false) |
| `FREEZE.PERSIST-ON-RESTART` | `boolean` | `true`, `false` | `true` | Preserve frozen status across server restarts (true / false) |
| `FREEZE.ALLOW-LOOK` | `boolean` | `true`, `false` | `true` | Allow frozen players to look around (true / false) |
| `FREEZE.ALERT-INTERVAL-TICKS` | `integer` | Any integer | `100` | Interval in ticks between periodic freeze alert messages sent to frozen players (20 ticks = 1s) |
| `FREEZE.LOG-USAGE` | `boolean` | `true`, `false` | `true` | Log freeze system actions to console/database (true / false) |
| `FREEZE.SERVER-NAME` | `string` | Any text | `survival-01` | Server identifier used in quit broadcast alerts |
| `FREEZE.STAFF-PERMISSION` | `string` | Any text | `ultimatedonutsmp2.staff.freeze` | Required permission node to execute /freeze |
| `FREEZE.ALERT-PERMISSION` | `string` | Any text | `ultimatedonutsmp2.staff.freeze.alert` | Permission node to receive staff freeze broadcast alerts |
| `FREEZE.EXEMPT-PERMISSION` | `string` | Any text | `ultimatedonutsmp2.staff.freeze.exempt` | Permission node granting exemption from being frozen |
| `FREEZE.ADMIN-PERMISSION` | `string` | Any text | `ultimatedonutsmp2.admin.freeze` | Permission node to reload/administer freeze system |
| `FREEZE.ALLOWED-COMMANDS` | `list` | A list of values | _list of 5 items_ | Commands allowed for frozen players (e.g., messaging staff or discord) |
| `FREEZE.STATUS_ON` | `string` | Any text | `&a&lON` | Display tag for active freeze status |
| `FREEZE.STATUS_OFF` | `string` | Any text | `&c&lOFF` | Display tag for inactive freeze status |
| `FREEZE.MESSAGE` | `string` | Any text | `&bFreeze &a%player% &7is now %status%` | Toggle feedback message sent to staff (%player% and %status% placeholders) |
| `FREEZE.ALERT` | `list` | A list of values | _list of 8 items_ | Periodic notification screen broadcast to frozen players |
| `FREEZE.QUIT_MESSAGE` | `string` | Any text | `&c[Freeze] &4%player% &cleft while frozen on &4%server%` | Alert broadcast to staff when a frozen player leaves the server |

<details>
<summary>Default contents of <code>FREEZE.ALLOWED-COMMANDS</code> (5 items)</summary>

```yaml
ALLOWED-COMMANDS:
  - '/discord'
  - '/social'
  - '/msg'
  - '/r'
  - '/helpop'
```

</details>

<details>
<summary>Default contents of <code>FREEZE.ALERT</code> (8 items)</summary>

```yaml
ALERT:
  - ''
  - '&c&lYou''re currently frozen!'
  - ''
  - '&7- You cannot move or interact'
  - '&7- Staff members are reviewing your actions'
  - '&7- Join our Discord for support:'
  - '&6&ohttps://discord.com'
  - ''
```

</details>

<details>
<summary>Default <code>FREEZE</code> block as shipped</summary>

```yaml
FREEZE:
  # Enable or disable the freeze system globally (true / false)
  ENABLED: true

  # Preserve frozen status across player logouts (true / false)
  PERSIST-ON-QUIT: true

  # Preserve frozen status across server restarts (true / false)
  PERSIST-ON-RESTART: true

  # Allow frozen players to look around (true / false)
  ALLOW-LOOK: true

  # Interval in ticks between periodic freeze alert messages sent to frozen players (20 ticks = 1s)
  ALERT-INTERVAL-TICKS: 100

  # Log freeze system actions to console/database (true / false)
  LOG-USAGE: true

  # Server identifier used in quit broadcast alerts
  SERVER-NAME: survival-01

  # Required permission node to execute /freeze
  STAFF-PERMISSION: ultimatedonutsmp2.staff.freeze

  # Permission node to receive staff freeze broadcast alerts
  ALERT-PERMISSION: ultimatedonutsmp2.staff.freeze.alert

  # Permission node granting exemption from being frozen
  EXEMPT-PERMISSION: ultimatedonutsmp2.staff.freeze.exempt

  # Permission node to reload/administer freeze system
  ADMIN-PERMISSION: ultimatedonutsmp2.admin.freeze

  # Commands allowed for frozen players (e.g., messaging staff or discord)
  ALLOWED-COMMANDS:
    - /discord
    - /social
    - /msg
    - /r
    - /helpop

  # Display tag for active freeze status
  STATUS_ON: '&a&lON'

  # Display tag for inactive freeze status
  STATUS_OFF: '&c&lOFF'

# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `MESSAGES`

System and user feedback messages

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MESSAGES.NO-PERMISSION` | `string` | Any text | `&cYou do not have permission.` | Permission node. Leave empty to allow everyone. |
| `MESSAGES.FEATURE-DISABLED` | `string` | Any text | `&cThe Freeze system is disabled.` | Feature disabled. |
| `MESSAGES.PLAYER-NOT-FOUND` | `string` | Any text | `&cPlayer not found.` | Player not found. |
| `MESSAGES.TARGET-OFFLINE` | `string` | Any text | `&cThat player must be online.` | Target offline. |
| `MESSAGES.TARGET-EXEMPT` | `string` | Any text | `&cYou cannot freeze that player.` | Target exempt. |
| `MESSAGES.SELF-TARGET` | `string` | Any text | `&cYou cannot freeze yourself.` | Self target. |
| `MESSAGES.COMMAND-BLOCKED` | `string` | Any text | `&cYou cannot use commands while frozen.` | Command blocked. |
| `MESSAGES.STILL-FROZEN` | `string` | Any text | `&cYou are still frozen. Wait for staff instructions.` | Still frozen. |
| `MESSAGES.UNFROZEN` | `string` | Any text | `&aYou are no longer frozen.` | Unfrozen. |
| `MESSAGES.RELOAD-SUCCESS` | `string` | Any text | `&aFreeze config reloaded.` | Reload success. |

<details>
<summary>Default <code>MESSAGES</code> block as shipped</summary>

```yaml
# System and user feedback messages
MESSAGES:
  NO-PERMISSION: '&cYou do not have permission.'
  FEATURE-DISABLED: '&cThe Freeze system is disabled.'
  PLAYER-NOT-FOUND: '&cPlayer not found.'
  TARGET-OFFLINE: '&cThat player must be online.'
  TARGET-EXEMPT: '&cYou cannot freeze that player.'
  SELF-TARGET: '&cYou cannot freeze yourself.'
  COMMAND-BLOCKED: '&cYou cannot use commands while frozen.'
  STILL-FROZEN: '&cYou are still frozen. Wait for staff instructions.'
  UNFROZEN: '&aYou are no longer frozen.'
  RELOAD-SUCCESS: '&aFreeze config reloaded.'
```

</details>

---

Defaults above match the file shipped in the jar.
