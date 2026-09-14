# `invsee.yml`

The staff inventory inspector. `LAYOUT` maps the target's inventory, armour and offhand
onto the slots of the viewing GUI, and `INVSEE` decides whether staff may edit what they
see or only look.

Sessions are live: changes made in the GUI apply to the target player immediately, so
read-only mode is the safer default for junior staff.

Comment at the top of the shipped file:

```text
UltimateDonutSMP2 - Invsee (Staff Inventory View) Configuration
```

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/invsee.yml` |
| **Commands** | `/invsee` |
| **Player-facing text** | Edit `CONFIG.INVSEE` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`INVSEE`](#section-invsee) | section | 12 keys |
| [`LAYOUT`](#section-layout) | section | 7 keys |
| [`MESSAGES`](#section-messages) | section | 9 keys |

---

## Section: `INVSEE`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `INVSEE.ENABLED` | `boolean` | `true`, `false` | `true` | Enable or disable the invsee system globally (true / false) |
| `INVSEE.TITLE` | `string` | Any text | `&8Inventory of {player}` | Title header displayed on the invsee GUI ({player} placeholder supported) |
| `INVSEE.REQUIRE-ONLINE` | `boolean` | `true`, `false` | `true` | Require target player to be online to open invsee (true / false) |
| `INVSEE.ALLOW-EDIT` | `boolean` | `true`, `false` | `false` | Allow staff members with modify permission to edit target inventory items (true / false) |
| `INVSEE.ALLOW-SELF-VIEW` | `boolean` | `true`, `false` | `false` | Allow staff members to view their own inventory via /invsee (true / false) |
| `INVSEE.NOTIFY-TARGET` | `boolean` | `true`, `false` | `false` | Notify target player when staff opens their inventory (true / false) |
| `INVSEE.LOG-USAGE` | `boolean` | `true`, `false` | `true` | Log invsee usage and item modifications to console/audit log (true / false) |
| `INVSEE.FREEZE-ON-LOGOUT` | `boolean` | `true`, `false` | `true` | Freeze inventory GUI into a snapshot if target logs out while being viewed (true / false) |
| `INVSEE.AUTO-REFRESH-TICKS` | `integer` | Any integer | `10` | Auto-refresh interval in ticks to synchronize target inventory updates (20 ticks = 1s) |
| `INVSEE.VIEW-PERMISSION` | `string` | Any text | `ultimatedonutsmp2.staff.invsee` | Permission node to view target player inventory |
| `INVSEE.MODIFY-PERMISSION` | `string` | Any text | `ultimatedonutsmp2.staff.invsee.modify` | Permission node to modify items in target player inventory |
| `INVSEE.ADMIN-PERMISSION` | `string` | Any text | `ultimatedonutsmp2.admin.invsee` | Permission node for admin invsee commands |

<details>
<summary>Default <code>INVSEE</code> block as shipped</summary>

```yaml
INVSEE:
  # Enable or disable the invsee system globally (true / false)
  ENABLED: true

  # Title header displayed on the invsee GUI ({player} placeholder supported)
  TITLE: '&8Inventory of {player}'

  # Require target player to be online to open invsee (true / false)
  REQUIRE-ONLINE: true

  # Allow staff members with modify permission to edit target inventory items (true / false)
  ALLOW-EDIT: false

  # Allow staff members to view their own inventory via /invsee (true / false)
  ALLOW-SELF-VIEW: false

  # Notify target player when staff opens their inventory (true / false)
  NOTIFY-TARGET: false

  # Log invsee usage and item modifications to console/audit log (true / false)
  LOG-USAGE: true

  # Freeze inventory GUI into a snapshot if target logs out while being viewed (true / false)
  FREEZE-ON-LOGOUT: true

  # Auto-refresh interval in ticks to synchronize target inventory updates (20 ticks = 1s)
  AUTO-REFRESH-TICKS: 10

  # Permission node to view target player inventory
  VIEW-PERMISSION: ultimatedonutsmp2.staff.invsee

  # Permission node to modify items in target player inventory
  MODIFY-PERMISSION: ultimatedonutsmp2.staff.invsee.modify

  # Permission node for admin invsee commands
  ADMIN-PERMISSION: ultimatedonutsmp2.admin.invsee
```

</details>

---

## Section: `LAYOUT`

Inventory GUI layout slot mapping

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `LAYOUT.SIZE` | `integer` | Any integer | `54` | Total GUI container size (must be multiple of 9, max 54) |
| `LAYOUT.ARMOR-SLOTS` | `list` | A list of values | _list of 4 items_ | GUI slot indexes assigned to armor slots [Helmet, Chestplate, Leggings, Boots] |
| `LAYOUT.OFFHAND-SLOT` | `integer` | Any integer | `4` | GUI slot index assigned to offhand item |
| `LAYOUT.SUMMARY-SLOT` | `integer` | Any integer | `6` | GUI slot index assigned to player stats summary icon |
| `LAYOUT.STATUS-SLOT` | `integer` | Any integer | `8` | GUI slot index assigned to staff connection status icon |
| `LAYOUT.MAIN-INVENTORY-START` | `integer` | Any integer | `18` | GUI slot index start for main inventory items (slots 9-35) |
| `LAYOUT.HOTBAR-START` | `integer` | Any integer | `45` | GUI slot index start for player hotbar items (slots 0-8) |

<details>
<summary>Default contents of <code>LAYOUT.ARMOR-SLOTS</code> (4 items)</summary>

```yaml
ARMOR-SLOTS:
  - 0
  - 1
  - 2
  - 3
```

</details>

<details>
<summary>Default <code>LAYOUT</code> block as shipped</summary>

```yaml
# Inventory GUI layout slot mapping
LAYOUT:
  # Total GUI container size (must be multiple of 9, max 54)
  SIZE: 54

  # GUI slot indexes assigned to armor slots [Helmet, Chestplate, Leggings, Boots]
  ARMOR-SLOTS:
    - 0
    - 1
    - 2
    - 3

  # GUI slot index assigned to offhand item
  OFFHAND-SLOT: 4

  # GUI slot index assigned to player stats summary icon
  SUMMARY-SLOT: 6

  # GUI slot index assigned to staff connection status icon
  STATUS-SLOT: 8

  # GUI slot index start for main inventory items (slots 9-35)
  MAIN-INVENTORY-START: 18

  # GUI slot index start for player hotbar items (slots 0-8)
  HOTBAR-START: 45
```

</details>

---

## Section: `MESSAGES`

System and feedback messages

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MESSAGES.NO-PERMISSION` | `string` | Any text | `&cYou do not have permission.` | Permission node. Leave empty to allow everyone. |
| `MESSAGES.FEATURE-DISABLED` | `string` | Any text | `&cThe Invsee system is disabled.` | Feature disabled. |
| `MESSAGES.PLAYER-NOT-ONLINE` | `string` | Any text | `&cThat player must be online.` | Player not online. |
| `MESSAGES.PLAYER-NOT-FOUND` | `string` | Any text | `&cPlayer not found.` | Player not found. |
| `MESSAGES.SELF-VIEW-DISABLED` | `string` | Any text | `&cYou cannot invsee yourself.` | Self view disabled. |
| `MESSAGES.EDIT-CONFLICT` | `string` | Any text | `&eAnother staff member is already editing &f{target}&e. O…` | Edit conflict. |
| `MESSAGES.TARGET-LOGGED-OUT` | `string` | Any text | `&eThis inventory is now a frozen snapshot because &f{targ…` | Target logged out. |
| `MESSAGES.TARGET-NOTIFY` | `string` | Any text | `&eYour inventory is being viewed by &f{viewer}&e in &f{mo…` | Target notify. |
| `MESSAGES.RELOAD-SUCCESS` | `string` | Any text | `&aInvsee config reloaded.` | Reload success. |

<details>
<summary>Default <code>MESSAGES</code> block as shipped</summary>

```yaml
# System and feedback messages
MESSAGES:
  NO-PERMISSION: '&cYou do not have permission.'
  FEATURE-DISABLED: '&cThe Invsee system is disabled.'
  PLAYER-NOT-ONLINE: '&cThat player must be online.'
  PLAYER-NOT-FOUND: '&cPlayer not found.'
  SELF-VIEW-DISABLED: '&cYou cannot invsee yourself.'
  EDIT-CONFLICT: '&eAnother staff member is already editing &f{target}&e. Opened in read-only mode.'
  TARGET-LOGGED-OUT: '&eThis inventory is now a frozen snapshot because &f{target}&e logged out.'
  TARGET-NOTIFY: '&eYour inventory is being viewed by &f{viewer}&e in &f{mode}&e mode.'
  RELOAD-SUCCESS: '&aInvsee config reloaded.'
```

</details>

---

Defaults above match the file shipped in the jar.
