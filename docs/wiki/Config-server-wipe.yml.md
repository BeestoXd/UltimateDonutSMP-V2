# `server-wipe.yml`

Configuration for a full season reset. `/serverwipe` deletes and regenerates the worlds
listed in `RESET-WORLDS` and clears plugin data, which is exactly as final as it sounds.

The safety mechanisms are the part to understand before enabling this. `PROTECTED-WORLDS`
can never be wiped regardless of the rest of the file, and `TOKEN-TTL-SECONDS` bounds the
confirmation token the command issues, so a wipe cannot be triggered by a stray command
days later. A wipe also suppresses the normal shutdown save so that in-memory data is not
written back over the fresh state, and progress is tracked in `server-wipe-pending.yml` so
an interrupted wipe resumes on the next boot.

Comment at the top of the shipped file:

```text
UltimateDonutSMP2 - Server Wipe (Season Reset) Configuration
```

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/server-wipe.yml` |
| **Commands** | `/serverwipe` |
| **Player-facing text** | Edit `CONFIG.SERVER_WIPE` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`ENABLED`](#section-enabled) | boolean | `true` |
| [`RESET-WORLDS`](#section-reset-worlds) | list | 0 entries |
| [`PROTECTED-WORLDS`](#section-protected-worlds) | list | 0 entries |
| [`TOKEN-TTL-SECONDS`](#section-token-ttl-seconds) | integer | `300` |
| [`BACKUP-DIRECTORY`](#section-backup-directory) | string | `server-wipe-backups` |
| [`MESSAGES`](#section-messages) | section | 2 keys |

---

## Section: `ENABLED`

Enable or disable the season reset / server wipe feature (true / false)

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `ENABLED` | `boolean` | `true`, `false` | `true` | Enable or disable the season reset / server wipe feature (true / false) |

<details>
<summary>Default <code>ENABLED</code> block as shipped</summary>

```yaml
# Enable or disable the season reset / server wipe feature (true / false)
ENABLED: true
```

</details>

---

## Section: `RESET-WORLDS`

List of world names to be reset during a server wipe (e.g., ["world", "world_nether", "world_the_end"])

A list of 0 values:

```yaml
RESET-WORLDS:
```

<details>
<summary>Default <code>RESET-WORLDS</code> block as shipped</summary>

```yaml
# List of world names to be reset during a server wipe (e.g., ["world", "world_nether", "world_the_end"])
RESET-WORLDS: []
```

</details>

---

## Section: `PROTECTED-WORLDS`

List of world names protected from being reset during a server wipe (e.g., ["spawn", "lobby"])

A list of 0 values:

```yaml
PROTECTED-WORLDS:
```

<details>
<summary>Default <code>PROTECTED-WORLDS</code> block as shipped</summary>

```yaml
# List of world names protected from being reset during a server wipe (e.g., ["spawn", "lobby"])
PROTECTED-WORLDS: []
```

</details>

---

## Section: `TOKEN-TTL-SECONDS`

Time-to-live duration in seconds for admin confirmation tokens before expiration

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `TOKEN-TTL-SECONDS` | `integer` | Any integer | `300` | Time-to-live duration in seconds for admin confirmation tokens before expiration |

<details>
<summary>Default <code>TOKEN-TTL-SECONDS</code> block as shipped</summary>

```yaml
# Time-to-live duration in seconds for admin confirmation tokens before expiration
TOKEN-TTL-SECONDS: 300
```

</details>

---

## Section: `BACKUP-DIRECTORY`

Subfolder path where world backups are saved before wiping

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `BACKUP-DIRECTORY` | `string` | Any text | `server-wipe-backups` | Subfolder path where world backups are saved before wiping |

<details>
<summary>Default <code>BACKUP-DIRECTORY</code> block as shipped</summary>

```yaml
# Subfolder path where world backups are saved before wiping
BACKUP-DIRECTORY: server-wipe-backups
```

</details>

---

## Section: `MESSAGES`

Notification messages broadcast during server reset maintenance

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MESSAGES.MAINTENANCE` | `string` | Any text | `&cthe server is preparing a season reset. try again after…` | Maintenance message shown when players attempt to log in during a wipe |
| `MESSAGES.KICK` | `string` | Any text | `&ca season reset is starting. the server will restart sho…` | Kick message shown to all online players when a wipe sequence begins |

<details>
<summary>Default <code>MESSAGES</code> block as shipped</summary>

```yaml
# Notification messages broadcast during server reset maintenance
MESSAGES:
  # Maintenance message shown when players attempt to log in during a wipe
  MAINTENANCE: '&cthe server is preparing a season reset. try again after the restart.'
  # Kick message shown to all online players when a wipe sequence begins
  KICK: '&ca season reset is starting. the server will restart shortly.'
```

</details>

---

Defaults above match the file shipped in the jar.
