# `scoreboard.yml`

The sidebar scoreboard. Two layouts ship side by side — `MODERN`, which labels each
statistic, and `LEGACY`, which shows icons and values only — and players pick between them
in `/settings`, with the choice saved per player. Cycling past `LEGACY` hides the sidebar
entirely.

Lines accept `%economy_*%` placeholders (see [Placeholders & Integrations](Placeholders-and-Integrations))
plus a few tokens specific to this file: `{team}`, `{shard_booster}` and `{shard_cuboid}`
expand to their line or vanish when not applicable, and `{money_icon}`, `{shards_icon}` and
`{sb_icon:<glyph>}` handle the aligned icon column.

Leave `TITLE-UPDATE-TICKS` at `20` unless the title itself is an animation. Statistics such
as ping and playtime do not need to refresh ten times a second, and this task runs for
every online player.

Comment at the top of the shipped file:

```text
UltimateDonutSMP2 - Scoreboard Configuration
```

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/scoreboard.yml` |
| **Player-facing text** | Not translated. Edit this file directly. |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`SCOREBOARD`](#section-scoreboard) | section | 12 keys |

---

## Section: `SCOREBOARD`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SCOREBOARD.ENABLED` | `boolean` | `true`, `false` | `true` | Enable or disable the animated sidebar scoreboard (true / false) |
| `SCOREBOARD.MODE` | `string` | Any text | `LEGACY` | Scoreboard layout mode: MODERN or LEGACY |
| `SCOREBOARD.TITLE-UPDATE-TICKS` | `integer` | Any integer | `20` | Ticks between sidebar updates (20 ticks = 1 second). Keep this at 20 unless the title itself is an animation that must cycle faster; stats such as ping and playtime do not need to refresh ten times a second. |
| `SCOREBOARD.ALIGN-ICON-COLUMN` | `boolean` | `true`, `false` | `true` | Enable column alignment for icons on the left side of scoreboard lines (true / false) |
| `SCOREBOARD.ICON-COLUMN-WIDTH` | `integer` | Any integer | `10` | Column width in pixels for icon alignment padding |
| `SCOREBOARD.TITLE` | `list` | A list of values | _list of 1 item_ | Fallback used by both styles for anything they leave unset. |
| `SCOREBOARD.LINES` | `list` | A list of values | _list of 12 items_ | The lines list. |
| `SCOREBOARD.TEAM` | `string` | Any text | `&#00A4FC🪓 &fTeam &#00A4FC%economy_team%     ` | Team. |
| `SCOREBOARD.SHARD-BOOSTER` | `string` | Any text | `&#A303F9⚡ &fBooster &#A303F9%economy_booster_countdown%  …` | Shard booster. |
| `SCOREBOARD.SHARD-CUBOID` | `string` | Any text | `&#A303F9⌛ &fShards &#A303F9%economy_shard_cuboid_display%…` | Shard cuboid. |

<details>
<summary>Default contents of <code>SCOREBOARD.TITLE</code> (1 item)</summary>

```yaml
TITLE:
  - '&#0069d6&lE&#0374da&lc&#067fdf&lo&#0a8be3&ln&#0d96e7&lo&#10a1ec&lm&#13acf0&ly&#17b8f4&lS&#1ac3f9&lM&#1dcefd&lP'
```

</details>

<details>
<summary>Default contents of <code>SCOREBOARD.LINES</code> (12 items)</summary>

```yaml
LINES:
  - ''
  - '&#00FC00&l$ &fMoney &#00FC00%economy_nicestMoney%     '
  - '&#A303F9★ &fShards &#A303F9%economy_shards%     '
  - '&#FC0000🗡 &fKills &#FC0000%economy_kills%      '
  - '&#F97603☠ &fDeaths &#F97603%economy_deaths%   '
  - '&#00A4FC⌛ &fKeyall &#00A4FC%economy_keyall_countdown%'
  - '&#FCE300⌚ &fPlaytime &#FCE300%economy_playtime%   '
  - '{team}'
  - '{shard_cuboid}'
  - '{shard_booster}'
  - ''
  - '&7NA East &7(&#0069D6%economy_ping%ms&7)'
```

</details>

### `SCOREBOARD.MODERN`

Labelled layout: an animated server name over one line per statistic.

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SCOREBOARD.MODERN.TITLE` | `list` | A list of values | _list of 1 item_ | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `SCOREBOARD.MODERN.LINES` | `list` | A list of values | _list of 12 items_ | The lines list. |
| `SCOREBOARD.MODERN.TEAM` | `string` | Any text | `&#00A4FC🪓 &fTeam &#00A4FC%economy_team%     ` | Team. |
| `SCOREBOARD.MODERN.SHARD-BOOSTER` | `string` | Any text | `&#A303F9⚡ &fBooster &#A303F9%economy_booster_countdown%  …` | Shard booster. |
| `SCOREBOARD.MODERN.SHARD-CUBOID` | `string` | Any text | `&#A303F9⌛ &fShards &#A303F9%economy_shard_cuboid_display%…` | Shard cuboid. |

<details>
<summary>Default contents of <code>SCOREBOARD.MODERN.TITLE</code> (1 item)</summary>

```yaml
TITLE:
  - '&#0069d6&lE&#0374da&lc&#067fdf&lo&#0a8be3&ln&#0d96e7&lo&#10a1ec&lm&#13acf0&ly&#17b8f4&lS&#1ac3f9&lM&#1dcefd&lP'
```

</details>

<details>
<summary>Default contents of <code>SCOREBOARD.MODERN.LINES</code> (12 items)</summary>

```yaml
LINES:
  - ''
  - '&#00FC00&l$ &fMoney &#00FC00%economy_nicestMoney%     '
  - '&#A303F9★ &fShards &#A303F9%economy_nicestShards%     '
  - '&#FC0000🗡 &fKills &#FC0000%economy_kills%      '
  - '&#F97603☠ &fDeaths &#F97603%economy_deaths%   '
  - '&#00A4FC⌛ &fKeyall &#00A4FC%economy_keyall_countdown%'
  - '&#FCE300⌚ &fPlaytime &#FCE300%economy_playtime%   '
  - '{team}'
  - '{shard_cuboid}'
  - '{shard_booster}'
  - ''
  - '&7NA East &7(&#0069D6%economy_ping%ms&7)'
```

</details>

### `SCOREBOARD.LEGACY`

Compact layout: the player's own name in the title, icons and values only.

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SCOREBOARD.LEGACY.TITLE` | `list` | A list of values | _list of 1 item_ | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `SCOREBOARD.LEGACY.LINES` | `list` | A list of values | _list of 7 items_ | The lines list. |
| `SCOREBOARD.LEGACY.TEAM` | `string` | Any text | `&6Team: &f%economy_team%` | Team. |
| `SCOREBOARD.LEGACY.SHARD-BOOSTER` | `string` | Any text | `&e⚡ Booster: &f%economy_booster_countdown%` | Shard booster. |
| `SCOREBOARD.LEGACY.SHARD-CUBOID` | `string` | Any text | `&#A303F9⌛ &f%economy_shard_cuboid_display%` | Shard cuboid. |

<details>
<summary>Default contents of <code>SCOREBOARD.LEGACY.TITLE</code> (1 item)</summary>

```yaml
TITLE:
  - '&f%economy_username%'
```

</details>

<details>
<summary>Default contents of <code>SCOREBOARD.LEGACY.LINES</code> (7 items)</summary>

```yaml
LINES:
  - '&#00FC00$ &f%economy_nicestMoney%'
  - '&#A303F9★ &f%economy_nicestShards%'
  - '&#FC0000🗡 &f%economy_kills%'
  - '&#F97603☠ &f%economy_deaths%'
  - '&#FCE300⌚ &f%economy_playtime%'
  - '{team}'
  - '{shard_booster}'
```

</details>

<details>
<summary>Default <code>SCOREBOARD</code> block as shipped</summary>

```yaml
SCOREBOARD:
  # Enable or disable the animated sidebar scoreboard (true / false)
  ENABLED: true

  # Scoreboard layout mode: MODERN or LEGACY
  MODE: LEGACY

  # Ticks between sidebar updates (20 ticks = 1 second). Keep this at 20 unless the title
  # itself is an animation that must cycle faster; stats such as ping and playtime do not
  # need to refresh ten times a second.
  TITLE-UPDATE-TICKS: 20

  # Enable column alignment for icons on the left side of scoreboard lines (true / false)
  ALIGN-ICON-COLUMN: true

  # Column width in pixels for icon alignment padding
  ICON-COLUMN-WIDTH: 10

  # Players pick between the two layouts below in /settings > Scoreboard, and the choice is
  # saved per player. Cycling past LEGACY hides the sidebar entirely; the next press brings it
  # back on MODERN.
  #
  # A scoreboard.yml written before the split can leave MODERN and LEGACY out and keep using the
  # flat TITLE / LINES / TEAM / SHARD-BOOSTER / SHARD-CUBOID keys — both styles fall back to
  # them. Anything a style leaves unset falls back the same way.

  # Labelled layout: an animated server name over one line per statistic.
  MODERN:
    TITLE:
      - '&#0069d6&lE&#0374da&lc&#067fdf&lo&#0a8be3&ln&#0d96e7&lo&#10a1ec&lm&#13acf0&ly&#17b8f4&lS&#1ac3f9&lM&#1dcefd&lP'
    LINES:
      - ''
      - '&#00FC00&l$ &fMoney &#00FC00%economy_nicestMoney%     '
      - '&#A303F9★ &fShards &#A303F9%economy_nicestShards%     '
      - '&#FC0000🗡 &fKills &#FC0000%economy_kills%      '
      - '&#F97603☠ &fDeaths &#F97603%economy_deaths%   '
      - '&#00A4FC⌛ &fKeyall &#00A4FC%economy_keyall_countdown%'
      - '&#FCE300⌚ &fPlaytime &#FCE300%economy_playtime%   '
      - '{team}'
      - '{shard_cuboid}'
      - '{shard_booster}'
      - ''
      - '&7NA East &7(&#0069D6%economy_ping%ms&7)'
    TEAM: '&#00A4FC🪓 &fTeam &#00A4FC%economy_team%     '
    SHARD-BOOSTER: '&#A303F9⚡ &fBooster &#A303F9%economy_booster_countdown%     '
    SHARD-CUBOID: '&#A303F9⌛ &fShards &#A303F9%economy_shard_cuboid_display%     '

  # Compact layout: the player's own name in the title, icons and values only.
# ... section continues, see the file on disk for the full block
```

</details>

---

Defaults above match the file shipped in the jar.
