# Duels & Instanced FFA

1v1 duels and throwaway FFA copies. After a match the map rolls back.

Not the persistent ranked map — that is [Ranked PvP Arena](Ranked-PvP-Arena).
Crystal speed is `FAST-CRYSTALS` in [config.yml](Config-config.yml), and the `duels` world
is excluded by default.

[duels.yml](Config-duels.yml) · [ffa.yml](Config-ffa.yml) · [Commands](Commands-and-Permissions)

---

## Duels

Feature toggle: `FEATURES.DUELS`. Arenas are created with `/arena` (alias `/duelarena`) and need
`ultimatedonutsmp2.admin.duels` for every mutating subcommand.

### Setting up an arena

```
/arena create <name>
/arena setpos1 <name>
/arena setpos2 <name>
/arena setreturn <name>
/arena setdisplay <name>
/arena enable <name>
```

`setpos1` and `setpos2` are the two fighter spawn points, not a WorldEdit-style boundary. The
plugin builds a managed world around those points and rolls it back after the match.

| Subcommand | Effect |
| :--- | :--- |
| `/arena create <name>` | Create the arena |
| `/arena delete <name>` | Delete it |
| `/arena setpos1 <name>` / `setpos2` | Fighter 1 / fighter 2 spawn |
| `/arena setreturn <name>` | Where both players land after the match |
| `/arena setdisplay <name>` | Icon in the arena picker |
| `/arena enable` / `disable <name>` | Open or close the arena for matchmaking |
| `/arena queue` | Inspect the queue |
| `/arena list` | List arenas |
| `/arena reload` | Reload [duels.yml](Config-duels.yml) |

### Player commands

| Command | Effect |
| :--- | :--- |
| `/duel <player>` | Challenge someone |
| `/duel accept [player]` | Accept |
| `/duel deny [player]` | Deny |
| `/duel claims` | Open leftover duel claims |
| `/duel reload` | Reload duels (`ultimatedonutsmp2.admin.duels`) |
| `/create <invite\|friends> <player> [map]` | Challenge with an explicit privacy mode |
| `/queue` / `/queue join` / `/queue leave` | Duel queue |
| `/draw` | Offer or accept a draw |
| `/leave` | Forfeit or leave the queue |

`SETTINGS` in [duels.yml](Config-duels.yml) holds countdown, match duration, request timeouts and
rollback padding. `COMMAND_BLOCK` is the list of commands players may not run mid-match.
`CROSS_SERVER` spans a queue across backends and needs Redis in [database.yml](Config-database.yml).
`MAP_SOURCES` decides how arena worlds are produced. `ARENA_SETTINGS` ships empty and is filled as
you create arenas in-game.

---

## Instanced FFA

Feature toggle: `FEATURES.FFA`. Matches run in a throwaway copy of an arena; `ROLLBACK` restores
the blocks and `PLAYER_STATE` restores inventory, health and effects on the way out.

### Setting up an arena

```
/ffaarena create <name>
/ffaarena setpos <name>
/ffaarena settings <name> [nohunger|noweather|alwaysmorning|nofalldamage] [on|off]
/ffaarena setdisplay <name>
/ffaarena enable <name>
```

`setpos` (also `setpos1` / `setpos2`) sets the combat-arena centre. `setspawn1`, `setspawn2` and
`setreturn` no longer place manual spawns — the command tells you to use `setpos` instead.

| Subcommand | Effect |
| :--- | :--- |
| `/ffaarena create <name>` | Create the arena |
| `/ffaarena delete <name>` | Delete it |
| `/ffaarena setpos <name>` | Combat centre |
| `/ffaarena setdisplay <name>` | Picker icon |
| `/ffaarena settings <name> …` | Hunger, weather, always-morning, no-fall |
| `/ffaarena enable` / `disable <name>` | Open or close the arena |
| `/ffaarena list` | List arenas |
| `/ffaarena reload` | Reload [ffa.yml](Config-ffa.yml) |

### Player commands

| Command | Effect |
| :--- | :--- |
| `/ffa` / `/ffa join` | Join the queue |
| `/ffa arena …` | Admin alias of `/ffaarena` (`ultimatedonutsmp2.admin.ffa`) |
| `/ffa reload` | Reload FFA (`ultimatedonutsmp2.admin.ffa`) |
| `/leave` | Leave the instance or queue |
| `/ffastats [player]` | View FFA info (FFA currently does not use a victory, defeat, draw, or streak system) |

`RULES.BLOCK_COMMANDS` stops general commands inside the instance.
`COUNT_TOWARD_GLOBAL_STATS` and `GIVE_SURVIVAL_REWARDS` default to `false`, so FFA fights do not
feed the survival leaderboards or pay money/shards unless you turn them on.

---

## Fast crystals

`duels.yml` has **no** `FAST-CRYSTAL` section. Crystal speed is global:

```yaml
# config.yml
FAST-CRYSTALS:
  ENABLED: true
  EXCLUDED-WORLDS:
    - duels
  PLACE:
    ENABLED-COOLDOWN-TICKS: 0
```

The bundled exclusion list includes `duels`, so crystals place at vanilla speed **inside duel
worlds** even when the feature is on. Remove that entry if you want the same speed in a duel.
Players can also disable the feature for themselves with the `fastcrystals` toggle in `/settings`.
