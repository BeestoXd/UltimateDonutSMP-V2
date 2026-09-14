# Localization & Messages

All chat, menu, and dialog text lives in `plugins/UltimateDonutSmp2/languages/`.
Set `LANGUAGE.ACTIVE` in [config.yml](Config-config.yml).

**There is no `messages.yml`.** A leftover file from an old install is only a last-resort
fallback for `en_US`. New installs never create one. Edit `MESSAGES` in the language file.

---

## Choosing a language

```yaml
LANGUAGE:
  ACTIVE: en_US
  FALLBACK: en_US
```

`ACTIVE` is the file name without `.yml`. Missing keys fall back to `FALLBACK`, then to a
hardcoded default, then to `missing language key: <path>`.

Aliases work: `en`, `english`, `id`, `pt-BR`. Language is **server-wide** — no per-player locale.

## Bundled locales

| File | Language | Keys |
| :--- | :--- | ---: |
| `en_US.yml` | English (US) | 2,919 |
| `de_DE.yml` | Deutsch | 2,761 |
| `id_ID.yml` | Bahasa Indonesia | 2,759 |
| `es_ES.yml` | Español | 2,720 |
| `fr_FR.yml` | Français | 2,720 |
| `pt_BR.yml` | Português (Brasil) | 2,720 |
| `ru_RU.yml` | Русский | 2,720 |
| `zh_CN.yml` | 简体中文 | 2,720 |

`en_US` is the complete file. The others catch up after updates via fallback.

---

## What's in a language file

| Section | Keys | Holds |
| :--- | ---: | :--- |
| `MESSAGES` | 541 | Command and chat replies |
| `MENUS` | 490 | Chest GUI titles and lore |
| `CONFIG` | 347 | Overrides for text inside feature YAML |
| `BUILTIN` | 1,327 | Hash keys — translate values, never rename keys |
| `ORDERS` | 143 | Orders board |
| Other | ~71 | Death messages, time units, Quick Buy, common, display, meta |

---

## Edit layout in YAML, wording in the language file

```
crates.yml   +  CONFIG.CRATES in languages/<locale>.yml   →  what players see
menus.yml    +  MENUS
```

A slot number or price stays in `crates.yml`. A button name goes in `CONFIG.CRATES`.

| Overlay | File |
| :--- | :--- |
| `MENUS` | [menus.yml](Config-menus.yml) |
| `DIALOG` | [dialog.yml](Config-dialog.yml) |
| `DEATH_MESSAGES` | [death-messages.yml](Config-death-messages.yml) |
| `CONFIG.SHOP` / `WORTH` / `RTP` / `CRATES` / `SPAWNERS` / `SPAWN_STASH` / `AMETHYST_TOOLS` / `AUCTION_HOUSE` / `ORDERS` / `DUELS` / `FFA` / `PVP` / `ENDER_CHEST` / `INVSEE` / `FREEZE` / `STAFF_MODE` / `NETWORK` / `SERVER_WIPE` | matching feature file |

Not overlaid (edit the file itself): `config.yml`, `database.yml`, `discord.yml`,
`scoreboard.yml`, `sounds.yml`, `filter.yml`, `hide.yml`, `anvil-moderation.yml`,
`enchantments.yml`, `offenses.yml`.

`CONFIG.HIDE`, `CONFIG.ENCHANTMENTS`, and `CONFIG.BILLFORD` exist in the language files but
nothing reads them.

---

## Formatting

`&a`, `&#RRGGBB`, and MiniMessage tags (`<red>`, `<#RRGGBB>`) can share a line.
`%economy_*%` and `{player}` / `{amount}` tokens work in language strings.

---

## Add a language

1. Copy `en_US.yml` to `languages/<your_locale>.yml`.
2. Set `META.NAME`. Translate values; leave key paths and `BUILTIN` hashes alone.
3. Set `LANGUAGE.ACTIVE` and keep `FALLBACK: en_US`.
4. `/uds reload`.

Custom files in `languages/` are not overwritten on update. Prefer a new file name over
editing `en_US.yml` in place.
