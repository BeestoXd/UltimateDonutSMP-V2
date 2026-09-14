# `hide.yml`

Lets a player appear under a different name and skin. `SCRAMBLE` generates a randomised
name, while `ALIASES` and `SKINS` supply the pool of hand-picked identities `/disguise`
can draw from. Name changes are applied at the packet level through ProtocolLib; skins
resolve through SkinsRestorer when it is installed, and fall back to Mojang profile
lookups otherwise.

Access is split across several permissions rather than one: `ultimatedonutsmp2.hide.scramble`,
`.disguise`, `.admin` and `.bypass` (the last letting staff see through other players'
disguises). The `hide` PlaceholderAPI expansion exposes the current alias and mode for use
in chat and tab list formats.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/hide.yml` |
| **Commands** | `/hide`, `/disguise` |
| **Player-facing text** | Not translated. Edit this file directly. |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`ENABLED`](#section-enabled) | boolean | `true` |
| [`COOLDOWN-SECONDS`](#section-cooldown-seconds) | integer | `30` |
| [`MAX-NAME-LENGTH`](#section-max-name-length) | integer | `16` |
| [`STAFF-MARKER`](#section-staff-marker) | string | `&8[&cH&8] ` |
| [`SCRAMBLE`](#section-scramble) | section | 4 keys |
| [`ALIASES`](#section-aliases) | section | 4 keys |
| [`SKINS`](#section-skins) | section | 4 keys |
| [`GUI`](#section-gui) | section | 4 keys |
| [`MESSAGES`](#section-messages) | section | 22 keys |

---

## Section: `ENABLED`

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `hide.yml` section on or off. |

<details>
<summary>Default <code>ENABLED</code> block as shipped</summary>

```yaml
# Determines whether Enabled is enabled or disabled. Available options: true, false
```

</details>

---

## Section: `COOLDOWN-SECONDS`

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `COOLDOWN-SECONDS` | `integer` | Any integer | `30` | Cooldown seconds. Seconds. |

<details>
<summary>Default <code>COOLDOWN-SECONDS</code> block as shipped</summary>

```yaml
# The numerical value for Cooldown Seconds. Available options: Any valid integer
```

</details>

---

## Section: `MAX-NAME-LENGTH`

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MAX-NAME-LENGTH` | `integer` | Any integer | `16` | Max name length. |

<details>
<summary>Default <code>MAX-NAME-LENGTH</code> block as shipped</summary>

```yaml
# The numerical value for Max Name Length. Available options: Any valid integer
```

</details>

---

## Section: `STAFF-MARKER`

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `STAFF-MARKER` | `string` | Any text | `&8[&cH&8] ` | Staff marker. |

<details>
<summary>Default <code>STAFF-MARKER</code> block as shipped</summary>

```yaml
# The text or value for Staff Marker. Available options: Any valid string text
```

</details>

---

## Section: `SCRAMBLE`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SCRAMBLE.LENGTH` | `integer` | Any integer | `10` | Length. |
| `SCRAMBLE.CHARACTERS` | `string` | Any text | `ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234…` | Characters. |
| `SCRAMBLE.OBFUSCATED` | `boolean` | `true`, `false` | `true` | On/off for obfuscated. |
| `SCRAMBLE.NAMETAG-OFFSET-Y` | `decimal` | Any decimal number | `1.0` | How far above the player the obfuscated name is drawn, in blocks. The client draws it where a passenger sits, around the waist, so this is what carries it up to where a username belongs. |

<details>
<summary>Default <code>SCRAMBLE</code> block as shipped</summary>

```yaml
# Configuration section for Scramble.
SCRAMBLE:
  # The numerical value for Length. Available options: Any valid integer
  LENGTH: 10
  # The text or value for Characters. Available options: Any valid string text
  CHARACTERS: ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_
  # Determines whether Obfuscated is enabled or disabled. Available options: true, false
  OBFUSCATED: true
  # How far above the player the obfuscated name is drawn, in blocks. The client draws it where a
  # passenger sits, around the waist, so this is what carries it up to where a username belongs.
  # Available options: Any decimal number
  NAMETAG-OFFSET-Y: 1.0
```

</details>

---

## Section: `ALIASES`

### `ALIASES.mrbeast`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ALIASES.mrbeast.NAME` | `string` | Any text | `MrBeast` | Display name shown to players. |
| `ALIASES.mrbeast.SKIN` | `string` | Any text | `mrbeast` | Skin. |

### `ALIASES.technoblade`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ALIASES.technoblade.NAME` | `string` | Any text | `Technoblade` | Display name shown to players. |
| `ALIASES.technoblade.SKIN` | `string` | Any text | `technoblade` | Skin. |

### `ALIASES.dream`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ALIASES.dream.NAME` | `string` | Any text | `Dream` | Display name shown to players. |
| `ALIASES.dream.SKIN` | `string` | Any text | `dream` | Skin. |

### `ALIASES.drdonutt`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ALIASES.drdonutt.NAME` | `string` | Any text | `DrDonutt` | Display name shown to players. |
| `ALIASES.drdonutt.SKIN` | `string` | Any text | `drdonutt` | Skin. |

<details>
<summary>Default <code>ALIASES</code> block as shipped</summary>

```yaml
# Configuration section for Aliases.
ALIASES:
  # Configuration section for Mrbeast.
  mrbeast:
    NAME: MrBeast
    # The text or value for Skin. Available options: Any valid string text
    SKIN: mrbeast
  # Configuration section for Technoblade.
  technoblade:
    NAME: Technoblade
    # The text or value for Skin. Available options: Any valid string text
    SKIN: technoblade
  # Configuration section for Dream.
  dream:
    NAME: Dream
    # The text or value for Skin. Available options: Any valid string text
    SKIN: dream
  # Configuration section for Drdonutt.
  drdonutt:
    NAME: DrDonutt
    # The text or value for Skin. Available options: Any valid string text
    SKIN: drdonutt
```

</details>

---

## Section: `SKINS`

### `SKINS.mrbeast`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SKINS.mrbeast.DISPLAY-NAME` | `string` | Any text | `MrBeast` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `SKINS.mrbeast.USERNAME` | `string` | Any text | `MrBeast` | Login username. |

### `SKINS.technoblade`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SKINS.technoblade.DISPLAY-NAME` | `string` | Any text | `Technoblade` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `SKINS.technoblade.USERNAME` | `string` | Any text | `Technoblade` | Login username. |

### `SKINS.dream`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SKINS.dream.DISPLAY-NAME` | `string` | Any text | `Dream` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `SKINS.dream.USERNAME` | `string` | Any text | `Dream` | Login username. |

### `SKINS.drdonutt`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SKINS.drdonutt.DISPLAY-NAME` | `string` | Any text | `DrDonutt` | Item name. Supports `&` colours and `&#RRGGBB` hex. |
| `SKINS.drdonutt.USERNAME` | `string` | Any text | `DrDonutt` | Login username. |

<details>
<summary>Default <code>SKINS</code> block as shipped</summary>

```yaml
# Configuration section for Skins.
SKINS:
  # Configuration section for Mrbeast.
  mrbeast:
    DISPLAY-NAME: MrBeast
    # The text or value for Username. Available options: Any valid string text
    USERNAME: MrBeast
  # Configuration section for Technoblade.
  technoblade:
    DISPLAY-NAME: Technoblade
    # The text or value for Username. Available options: Any valid string text
    USERNAME: Technoblade
  # Configuration section for Dream.
  dream:
    DISPLAY-NAME: Dream
    # The text or value for Username. Available options: Any valid string text
    USERNAME: Dream
  # Configuration section for Drdonutt.
  drdonutt:
    DISPLAY-NAME: DrDonutt
    # The text or value for Username. Available options: Any valid string text
    USERNAME: DrDonutt
```

</details>

---

## Section: `GUI`

### `GUI.MAIN`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.MAIN.TITLE` | `string` | Any text | `&8hide` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.MAIN.SIZE` | `integer` | Any integer | `27` | Chest size. Must be a multiple of 9 between 9 and 54. |

### `GUI.ALIASES`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.ALIASES.TITLE` | `string` | Any text | `&8select a name - {page}/{pages}` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.ALIASES.SIZE` | `integer` | Any integer | `54` | Chest size. Must be a multiple of 9 between 9 and 54. |

### `GUI.SKINS`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.SKINS.TITLE` | `string` | Any text | `&8select a skin - {page}/{pages}` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.SKINS.SIZE` | `integer` | Any integer | `54` | Chest size. Must be a multiple of 9 between 9 and 54. |

### `GUI.LIST`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.LIST.TITLE` | `string` | Any text | `&8hidden players - {page}/{pages}` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.LIST.SIZE` | `integer` | Any integer | `54` | Chest size. Must be a multiple of 9 between 9 and 54. |

<details>
<summary>Default <code>GUI</code> block as shipped</summary>

```yaml
# Configuration section for Gui.
GUI:
  # Configuration section for Main.
  MAIN:
    TITLE: '&8hide'
    SIZE: 27
  # Configuration section for Aliases.
  ALIASES:
    TITLE: '&8select a name - {page}/{pages}'
    SIZE: 54
  # Configuration section for Skins.
  SKINS:
    TITLE: '&8select a skin - {page}/{pages}'
    SIZE: 54
  # Configuration section for List.
  LIST:
    TITLE: '&8hidden players - {page}/{pages}'
    SIZE: 54
```

</details>

---

## Section: `MESSAGES`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MESSAGES.PLAYER-ONLY` | `string` | Any text | `&conly players can use this command.` | Player only. |
| `MESSAGES.NO-PERMISSION` | `string` | Any text | `&cyou do not have permission.` | Permission node. Leave empty to allow everyone. |
| `MESSAGES.DEPENDENCY-MISSING` | `string` | Any text | `&chide requires protocollib to be installed and enabled.` | Dependency missing. |
| `MESSAGES.DISABLED` | `string` | Any text | `&cthe hide feature is currently disabled.` | Disabled. |
| `MESSAGES.IN-COMBAT` | `string` | Any text | `&cyou cannot change your hide state while in combat.` | In combat. |
| `MESSAGES.COOLDOWN` | `string` | Any text | `&cwait &f{seconds}s &cbefore changing hide again.` | Wait time before the action can run again. |
| `MESSAGES.ALREADY-HIDDEN` | `string` | Any text | `&cyou are already using that hide identity.` | Already hidden. |
| `MESSAGES.NOT-HIDDEN` | `string` | Any text | `&cthat player is not hidden.` | Not hidden. |
| `MESSAGES.PLAYER-NOT-FOUND` | `string` | Any text | `&cplayer not found.` | Player not found. |
| `MESSAGES.INVALID-ALIAS` | `string` | Any text | `&cthat disguise alias is not configured or is invalid.` | Invalid alias. |
| `MESSAGES.INVALID-SKIN` | `string` | Any text | `&cunable to resolve that skin name or url.` | Invalid skin. |
| `MESSAGES.SKIN-SEARCHING` | `string` | Any text | `&7searching skin for &f{skin}&7...` | Skin searching. |
| `MESSAGES.ALIAS-IN-USE` | `string` | Any text | `&cthat alias is already in use.` | Alias in use. |
| `MESSAGES.SCRAMBLED` | `string` | Any text | `&ayour identity is now scrambled as &f{alias}&a.` | Scrambled. |
| `MESSAGES.DISGUISED` | `string` | Any text | `&ayou are now disguised as &f{alias}&a.` | Disguised. |
| `MESSAGES.REMOVED` | `string` | Any text | `&ayour hide state has been removed.` | Removed. |
| `MESSAGES.ADMIN-REMOVED` | `string` | Any text | `&asuccessfully removed hide from &f{player}&a.` | Admin removed. |
| `MESSAGES.REMOVED-BY-ADMIN` | `string` | Any text | `&cyour hide state has been removed by an administrator.` | Removed by admin. |
| `MESSAGES.STATUS-NONE` | `string` | Any text | `&7hide status: &cinactive` | Status none. |
| `MESSAGES.STATUS-ACTIVE` | `string` | Any text | `&7hide status: &a{mode} &8- &f{alias}` | Status active. |
| `MESSAGES.CHECK` | `string` | Any text | `&bhide check &7real name: &f{real} &7alias: &f{alias} &7m…` | Check. |
| `MESSAGES.PERMISSION-REMOVED` | `string` | Any text | `&cyour hide state was removed because its permission is n…` | Permission removed. |

<details>
<summary>Default <code>MESSAGES</code> block as shipped</summary>

```yaml
MESSAGES:
  # The text or value for Player Only. Available options: Any valid string text
  PLAYER-ONLY: '&conly players can use this command.'
  # The text or value for No Permission. Available options: Any valid string text
  NO-PERMISSION: '&cyou do not have permission.'
  # The text or value for Dependency Missing. Available options: Any valid string text
  DEPENDENCY-MISSING: '&chide requires protocollib to be installed and enabled.'
  # The text or value for Disabled. Available options: Any valid string text
  DISABLED: '&cthe hide feature is currently disabled.'
  # The text or value for In Combat. Available options: Any valid string text
  IN-COMBAT: '&cyou cannot change your hide state while in combat.'
  # The text or value for Cooldown. Available options: Any valid string text
  COOLDOWN: '&cwait &f{seconds}s &cbefore changing hide again.'
  # The text or value for Already Hidden. Available options: Any valid string text
  ALREADY-HIDDEN: '&cyou are already using that hide identity.'
  # The text or value for Not Hidden. Available options: Any valid string text
  NOT-HIDDEN: '&cthat player is not hidden.'
  # The text or value for Player Not Found. Available options: Any valid string text
  PLAYER-NOT-FOUND: '&cplayer not found.'
  # The text or value for Invalid Alias. Available options: Any valid string text
  INVALID-ALIAS: '&cthat disguise alias is not configured or is invalid.'
  # The text or value for Invalid Skin. Available options: Any valid string text
  INVALID-SKIN: '&cunable to resolve that skin name or url.'
  # The text or value for Skin Searching. Available options: Any valid string text
  SKIN-SEARCHING: '&7searching skin for &f{skin}&7...'
  # The text or value for Alias In Use. Available options: Any valid string text
  ALIAS-IN-USE: '&cthat alias is already in use.'
  # The text or value for Scrambled. Available options: Any valid string text
  SCRAMBLED: '&ayour identity is now scrambled as &f{alias}&a.'
  # The text or value for Disguised. Available options: Any valid string text
  DISGUISED: '&ayou are now disguised as &f{alias}&a.'
  # The text or value for Removed. Available options: Any valid string text
  REMOVED: '&ayour hide state has been removed.'
  # The text or value for Admin Removed. Available options: Any valid string text
  ADMIN-REMOVED: '&asuccessfully removed hide from &f{player}&a.'
  # The text or value for Removed By Admin. Available options: Any valid string text
  REMOVED-BY-ADMIN: '&cyour hide state has been removed by an administrator.'
  # The text or value for Status None. Available options: Any valid string text
  STATUS-NONE: '&7hide status: &cinactive'
  # The text or value for Status Active. Available options: Any valid string text
  STATUS-ACTIVE: '&7hide status: &a{mode} &8- &f{alias}'
  # The text or value for Check. Available options: Any valid string text
  CHECK: |-
    &bhide check
    &7real name: &f{real}
    &7alias: &f{alias}
    &7mode: &f{mode}
    &7skin: &f{skin}
# ... section continues, see the file on disk for the full block
```

</details>

---

Defaults above match the file shipped in the jar.
