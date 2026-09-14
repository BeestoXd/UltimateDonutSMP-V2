# `death-messages.yml`

Replaces vanilla death messages with the plugin's own wording, keyed by damage cause.
Because the text is localized, translating these is usually better done under
`DEATH_MESSAGES` in `languages/<locale>.yml` than by editing this file directly.

Whether a player actually sees another player's death message is a per-player choice —
the `deathmessages` player setting can limit them to friends only, or turn them off.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/death-messages.yml` |
| **Player-facing text** | Edit `DEATH_MESSAGES` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`MESSAGES`](#section-messages) | section | 22 keys |

---

## Section: `MESSAGES`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MESSAGES.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `MESSAGES` section on or off. |
| `MESSAGES.PREFIX` | `string` | Any text | `&c☠ ` | Text prepended to every message from this feature. |
| `MESSAGES.BLOCK-EXPLOSION` | `string` | Any text | `{player} got blown to pieces` | Block explosion. |
| `MESSAGES.CONTACT` | `string` | Any text | `{player} was pricked to death` | Contact. |
| `MESSAGES.ENTITY-ATTACK` | `string` | Any text | `{player} was slain by {killer}` | Entity attack. |
| `MESSAGES.FALLING-BLOCK` | `string` | Any text | `{player} got freaking squashed by a block` | Falling block. |
| `MESSAGES.LIGHTNING` | `string` | Any text | `{player} got lit the hell up by a lightning` | Lightning. |
| `MESSAGES.POISON` | `string` | Any text | `{player} was poisoned` | Poison. |
| `MESSAGES.STARVATION` | `string` | Any text | `{player} starved to death` | Starvation. |
| `MESSAGES.SUFFOCATION` | `string` | Any text | `{player} suffocated in a wall` | Suffocation. |
| `MESSAGES.SUICIDE` | `string` | Any text | `{player} took his own life like a peasant` | Suicide. |
| `MESSAGES.THORNS` | `string` | Any text | `{player} killed themself by trying to kill someone` | Thorns. |
| `MESSAGES.WITHER` | `string` | Any text | `{player} withered away` | Wither. |
| `MESSAGES.DEFAULT` | `string` | Any text | `{player} died` | Default. |

### Entry schema (8 entries)

Each entry under `MESSAGES` is keyed by a name you choose, and every entry accepts the same options:

### Shipped entries

| Entry key |  |
| :--- |  |
| `DROWNING` |  |
| `FALL` |  |
| `FIRE` |  |
| `FIRE-TICK` |  |
| `LAVA` |  |
| `PROJECTILE` |  |
| `VOID` |  |
| `ENTITY-EXPLOSION` |  |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `NORMAL` | `string` | Any text | Required | Normal. |
| `PVP` | `string` | Any text | Required | Pvp. |

<details>
<summary>Default <code>MESSAGES</code> block as shipped</summary>

```yaml
MESSAGES:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # The text or value for Prefix. Available options: Any valid string text
  PREFIX: '&c☠ '
  # The text or value for Block Explosion. Available options: Any valid string text
  BLOCK-EXPLOSION: '{player} got blown to pieces'
  # The text or value for Contact. Available options: Any valid string text
  CONTACT: '{player} was pricked to death'
  # Configuration section for Drowning.
  DROWNING:
    # The text or value for Normal. Available options: Any valid string text
    NORMAL: '{player} drowned!'
    # The text or value for Pvp. Available options: Any valid string text
    PVP: '{player} drowned whilst trying to escape {killer}'
  # The text or value for Entity Attack. Available options: Any valid string text
  ENTITY-ATTACK: '{player} was slain by {killer}'
  # Configuration section for Fall.
  FALL:
    # The text or value for Normal. Available options: Any valid string text
    NORMAL: '{player} hit the ground too hard'
    # The text or value for Pvp. Available options: Any valid string text
    PVP: '{player} was doomed to fall by {killer}'
  # The text or value for Falling Block. Available options: Any valid string text
  FALLING-BLOCK: '{player} got freaking squashed by a block'
  # Configuration section for Fire.
  FIRE:
    # The text or value for Normal. Available options: Any valid string text
    NORMAL: '{player} went up in flames'
    # The text or value for Pvp. Available options: Any valid string text
    PVP: '{player} walked into a fire whilst fighting {killer}'
  # Configuration section for Fire Tick.
  FIRE-TICK:
    # The text or value for Normal. Available options: Any valid string text
    NORMAL: '{player} burned to death'
    # The text or value for Pvp. Available options: Any valid string text
    PVP: '{player} was burnt to a crisp whilst fighting {killer}'
  # Configuration section for Lava.
  LAVA:
    # The text or value for Normal. Available options: Any valid string text
    NORMAL: '{player} tried to swim in lava'
    # The text or value for Pvp. Available options: Any valid string text
    PVP: '{player} tried to swim in lava while trying to escape {killer}'
  # The text or value for Lightning. Available options: Any valid string text
  LIGHTNING: '{player} got lit the hell up by a lightning'
  # The text or value for Poison. Available options: Any valid string text
  POISON: '{player} was poisoned'
  # Configuration section for Projectile.
# ... section continues, see the file on disk for the full block
```

</details>

---

Defaults above match the file shipped in the jar.
