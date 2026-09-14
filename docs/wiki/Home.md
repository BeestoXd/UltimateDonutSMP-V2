# UltimateDonutSMP2

One plugin for a DonutSMP-style survival network: money and shards, shops and auctions, homes
and RTP, duels / FFA / ranked PvP, crates, spawners, and staff tools — one database, one
config set.

**Must install first:** [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) and
[PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/). Without either, the
plugin disables itself.

---

## Read in this order

| Step | Page |
| :--- | :--- |
| 1. Install and first boot | [Installation & Setup](Installation-and-Setup) |
| 2. Pick a file to edit | [Configuration Reference](Configuration-Reference) |
| 3. Grant commands | [Commands & Permissions](Commands-and-Permissions) |
| 4. Learn one feature | The six pages under **Features** in the sidebar |

Stuck? Start at the [FAQ](FAQ).

---

## Features

| Page | What you set up |
| :--- | :--- |
| [Economy & Marketplaces](Economy-and-Marketplaces) | Money, shards, `/shop`, `/sell`, Auction House, Orders |
| [Crates & Spawners](Crates-and-Spawners) | Keys, chest crates, stacked spawners, Amethyst Tools |
| [Duels & Instanced FFA](Duels-and-FFA) | 1v1 queue and throwaway FFA instances |
| [Ranked PvP Arena](Ranked-PvP-Arena) | Persistent arena, Elo, kits (ships **off**) |
| [Cuboids & Portals](Cuboids-and-Portals) | Regions for spawn, AFK, shards, RTP, portals |
| [Staff & Security](Staff-and-Security) | Staff mode, freeze, vanish, punishments, wipes |

## Also

- [Placeholders & Integrations](Placeholders-and-Integrations) — `%economy_*%`, Vault, Redis, Discord
- [Localization & Messages](Localization-and-Messages) — 8 languages; there is no `messages.yml`

`/billford` is listed in the jar but does nothing. Do not configure it. Details in the [FAQ](FAQ).

---

## At a glance

| | |
| :--- | :--- |
| Version | `1.0` |
| Servers | Paper, Purpur, Pufferfish, Spigot, Folia |
| Minecraft | `26.1.2`–`26.2` (checked on startup) |
| Java | Compiled for 21. Use the JDK your server build needs. |
| Storage | SQLite (default), MySQL, or MongoDB |
| Network | Redis, optional |
| Config | 31 YAML files, 8 language files |
| Commands | 143 declared; `/billford` has no handler |
