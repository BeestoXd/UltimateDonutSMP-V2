# `discord.yml`

Sends punishment and staff events to Discord through incoming webhooks. There is no bot
and no JDA dependency — the plugin posts JSON over HTTPS, so all you need is a webhook URL
from a channel's integration settings.

Message templates accept `%player%`, `%uuid%`, `%staff%`, `%reason%`, `%duration%`,
`%date%`, `%id%`, `%server%`, `%scope%` and `%type%`. Leaving a webhook URL empty disables
that notification silently.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/discord.yml` |
| **Player-facing text** | Not translated. Edit this file directly. |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`WEBHOOKS`](#section-webhooks) | section | 6 keys |

---

## Section: `WEBHOOKS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `WEBHOOKS.ENABLED` | `boolean` | `true`, `false` | `true` | Turns the `WEBHOOKS` section on or off. |
| `WEBHOOKS.URL` | `string` | Any text | `https://discord.com/api/webhooks/your_webhook_here` | Url. |
| `WEBHOOKS.AVATAR_API` | `string` | Any text | `https://visage.surgeplay.com/face/128/%uuid_no_dash%` | Avatar api. |
| `WEBHOOKS.MODEL_API` | `string` | Any text | `https://visage.surgeplay.com/full/384/%uuid_no_dash%` | Model api. |
| `WEBHOOKS.BUST_API` | `string` | Any text | `https://visage.surgeplay.com/bust/384/%uuid_no_dash%` | Bust api. |

### `WEBHOOKS.MESSAGES`

#### Entry schema (5 entries)

Each entry under `WEBHOOKS.MESSAGES` is keyed by a name you choose, and every entry accepts the same options:

#### Shipped entries

| Entry key | `TITLE` |
| :--- | :--- |
| `BAN` | `Player Banned - %player%` |
| `MUTE` | `Player Muted - %player%` |
| `WARN` | `Player Warned - %player%` |
| `KICK` | `Player Kicked - %player%` |
| `BLACKLIST` | `PLAYER BLACKLISTED - %player%` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `AUTHOR_NAME` | `string` | Any text | Required | Author name. |
| `COLOR` | `string` | Any text | Required | Color. |
| `DESCRIPTION` | `string` | Any text | Required | Description. |
| `ENABLED` | `boolean` | `true`, `false` | Required | Turns the `BAN` section on or off. |
| `FOOTER` | `string` | Any text | Required | Footer. |
| `THUMBNAIL` | `string` | Any text | Required | Thumbnail. |
| `TITLE` | `string` | Any text | Required | Title text. Supports `&` colours and `&#RRGGBB` hex. |

<details>
<summary>Default <code>WEBHOOKS</code> block as shipped</summary>

```yaml
# Configuration section for Webhooks.
WEBHOOKS:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: true
  # The text or value for Url. Available options: Any valid string text
  URL: https://discord.com/api/webhooks/your_webhook_here
  # The text or value for Avatar Api. Available options: Any valid string text
  AVATAR_API: https://visage.surgeplay.com/face/128/%uuid_no_dash%
  # The text or value for Model Api. Available options: Any valid string text
  MODEL_API: https://visage.surgeplay.com/full/384/%uuid_no_dash%
  # The text or value for Bust Api. Available options: Any valid string text
  BUST_API: https://visage.surgeplay.com/bust/384/%uuid_no_dash%
  MESSAGES:
    # Configuration section for Ban.
    BAN:
      # Determines whether Enabled is enabled or disabled. Available options: true, false
      ENABLED: true
      TITLE: Player Banned - %player%
      # The text or value for Color. Available options: Any valid string text
      COLOR: '#FF0000'
      # The text or value for Description. Available options: Any valid string text
      DESCRIPTION: |-
        :hammer: **Punishment Type:** Ban

        **Player:**
        %player%

        **Staff:**
        %staff%

        **Reason:**
        ||%reason%||

        **Duration:**
        %duration%

        **Date:**
        %date%

        **ID:** `%id%`
      # The text or value for Thumbnail. Available options: Any valid string text
      THUMBNAIL: '%skin_bust%'
      # The text or value for Author Name. Available options: Any valid string text
      AUTHOR_NAME: Ban System
      # The text or value for Footer. Available options: Any valid string text
      FOOTER: Punishment issued via server
    # Configuration section for Mute.
    MUTE:
# ... section continues, see the file on disk for the full block
```

</details>

---

Defaults above match the file shipped in the jar.
