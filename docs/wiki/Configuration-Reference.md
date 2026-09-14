# Configuration Reference

31 YAML files live in `plugins/UltimateDonutSmp2/`. Each one has its own page with every
key, type, and shipped default. `plugin.yml` is not listed — edit commands through
[Commands & Permissions](Commands-and-Permissions), not that file.

## Rules that apply to every file

- Use spaces, never tabs.
- `/ultimatedonutsmp2 reload` (also `/uds reload`) picks up most edits. `dialog.yml` →
  `PAUSE-SCREEN` needs a full restart.
- New keys from an update are merged in. Your old file is copied to `config-backups/`.
  Crates you defined, plus `pvp.yml` `ARENA`/`KITS` and the arena blocks in `duels.yml` /
  `ffa.yml`, are never overwritten.
- If a file is marked **translated**, change wording in `languages/<locale>.yml` and leave
  this file for layout and numbers. See [Localization & Messages](Localization-and-Messages).

## I want to…

| … | Open |
| :--- | :--- |
| Change language, features, chat, combat, teleports | [config.yml](Config-config.yml) |
| Switch storage, or join servers with Redis | [database.yml](Config-database.yml), [network.yml](Config-network.yml) |
| Change sell prices | [worth.yml](Config-worth.yml) |
| Change the shop / shard shop | [shop.yml](Config-shop.yml) |
| Move a menu button | [menus.yml](Config-menus.yml) |
| Set up crates or spawners | [crates.yml](Config-crates.yml), [spawners.yml](Config-spawners.yml) |
| Set up ranked PvP, duels, or FFA | [pvp.yml](Config-pvp.yml), [duels.yml](Config-duels.yml), [ffa.yml](Config-ffa.yml) |
| Change staff tools or punishments | [staff-mode.yml](Config-staff-mode.yml), [offenses.yml](Config-offenses.yml) |
| Change the sidebar | [scoreboard.yml](Config-scoreboard.yml) |
| Change a sound | [sounds.yml](Config-sounds.yml) |

## Every file

| File | What it is for | Translated? |
| :--- | :--- | :---: |
| [`amethyst-tools.yml`](Config-amethyst-tools.yml) | Timed Drill, Chopper, and other premium tools | Yes |
| [`anvil-moderation.yml`](Config-anvil-moderation.yml) | Block slurs and ads in anvil renames | No |
| [`auction-house.yml`](Config-auction-house.yml) | Rules for `/ah` listings, claims, and bots | Yes |
| [`config.yml`](Config-config.yml) | Language, features, chat, combat, teleports, shards | No |
| [`crates.yml`](Config-crates.yml) | Crate keys, animations, and weighted rewards | Yes |
| [`database.yml`](Config-database.yml) | SQLite / MySQL / MongoDB, plus optional Redis | No |
| [`death-messages.yml`](Config-death-messages.yml) | Wording for custom death messages | Yes |
| [`dialog.yml`](Config-dialog.yml) | Native 1.21.6+ dialog screens (pay, homes, pause) | Yes |
| [`discord.yml`](Config-discord.yml) | Punishment and staff webhooks (no bot) | No |
| [`duels.yml`](Config-duels.yml) | 1v1 queue, arenas, rollback, cross-server | Yes |
| [`enchantments.yml`](Config-enchantments.yml) | Orders enchantment-picker layout | No |
| [`ender-chest.yml`](Config-ender-chest.yml) | Network ender chest size and `/ecsee` | Yes |
| [`ffa.yml`](Config-ffa.yml) | Instanced FFA rollback and player state | Yes |
| [`filter.yml`](Config-filter.yml) | Item categories for AH / Orders browse buttons | No |
| [`freeze.yml`](Config-freeze.yml) | What a frozen player can still do | Yes |
| [`hide.yml`](Config-hide.yml) | Scramble names and `/disguise` skins | No |
| [`invsee.yml`](Config-invsee.yml) | Staff inventory inspector layout | Yes |
| [`menus.yml`](Config-menus.yml) | Every chest GUI layout | Yes |
| [`network.yml`](Config-network.yml) | This server's name, `/servers`, maintenance | Yes |
| [`offenses.yml`](Config-offenses.yml) | Preset `/offend` punishments and wipe flag | No |
| [`orders.yml`](Config-orders.yml) | Buy-order board, matching, Bedrock forms | Yes |
| [`pvp.yml`](Config-pvp.yml) | Ranked arena, Elo, kits, schematic reset | Yes |
| [`rtp.yml`](Config-rtp.yml) | Random teleport worlds, cache, and queue | Yes |
| [`scoreboard.yml`](Config-scoreboard.yml) | Sidebar layouts (MODERN / LEGACY) | No |
| [`server-wipe.yml`](Config-server-wipe.yml) | Season reset worlds and confirmation token | Yes |
| [`shop.yml`](Config-shop.yml) | Quick Buy + shard shop | Yes |
| [`sounds.yml`](Config-sounds.yml) | Every sound the plugin plays | No |
| [`spawn-stash.yml`](Config-spawn-stash.yml) | Staff bait chests and alerts | Yes |
| [`spawners.yml`](Config-spawners.yml) | Stacked spawners that store drops | Yes |
| [`staff-mode.yml`](Config-staff-mode.yml) | Staff hotbar, vanish, fake players | Yes |
| [`worth.yml`](Config-worth.yml) | Sell prices and worth tooltips | Yes |

---

_Option counts are generated from `src/main/resources`, so they track the shipped files exactly._
