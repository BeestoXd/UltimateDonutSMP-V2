# `anvil-moderation.yml`

Blocks players from renaming items to slurs or advertising through an anvil. The listener
checks the requested name against `banned-words`, and repeat offenders escalate through
`punishments`.

Unlike most files here this one is written back to disk at runtime: `players` is the
plugin's own record of who has tripped the filter and how often, so treat it as state
rather than configuration. Edit `banned-words` and `punishments`, and leave `players`
alone unless you are deliberately clearing someone's history.

Comment at the top of the shipped file:

```text
UltimateDonutSMP2 - Anvil Moderation Configuration
```

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/anvil-moderation.yml` |
| **Commands** | `/amod` |
| **Player-facing text** | Not translated. Edit this file directly. |
| **Reload** | `/amod reload` or `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`banned-words`](#section-banned-words) | list | 5 entries |
| [`punishments`](#section-punishments) | list | 5 entries |
| [`players`](#section-players) | section | 0 keys |

---

## Section: `banned-words`

List of banned words and phrases blocked when renaming items in an anvil

A list of 5 values:

```yaml
banned-words:
  - example
  - bannedword
  - inappropriateword
  - offensivephrase
  - slur
```

<details>
<summary>Default <code>banned-words</code> block as shipped</summary>

```yaml
# List of banned words and phrases blocked when renaming items in an anvil
banned-words:
  - example
  - bannedword
  - inappropriateword
  - offensivephrase
  - slur
```

</details>

---

## Section: `punishments`

Progressive punishments executed when players attempt to rename items using banned words Options: Commands executed sequentially per offense count (%player% placeholder supported)

A list of 5 values:

```yaml
punishments:
  - mute %player% 30d Anvil inappropriate content - 1st offense
  - tempban %player% 7d Anvil inappropriate content - 2nd offense
  - tempban %player% 14d Anvil inappropriate content - 3rd offense
  - tempban %player% 30d Anvil inappropriate content - 4th offense
  - tempban %player% 50d Anvil inappropriate content - 5th offense
```

<details>
<summary>Default <code>punishments</code> block as shipped</summary>

```yaml
# Progressive punishments executed when players attempt to rename items using banned words
# Options: Commands executed sequentially per offense count (%player% placeholder supported)
punishments:
  - mute %player% 30d Anvil inappropriate content - 1st offense
  - tempban %player% 7d Anvil inappropriate content - 2nd offense
  - tempban %player% 14d Anvil inappropriate content - 3rd offense
  - tempban %player% 30d Anvil inappropriate content - 4th offense
  - tempban %player% 50d Anvil inappropriate content - 5th offense
```

</details>

---

## Section: `players`

Persistent player offense tracking data (managed automatically by plugin)

This section ships empty. The plugin fills it in as you create entries in-game, so there is nothing to configure by hand here.

<details>
<summary>Default <code>players</code> block as shipped</summary>

```yaml
# Persistent player offense tracking data (managed automatically by plugin)
players: {}
```

</details>

---

Defaults above match the file shipped in the jar.
