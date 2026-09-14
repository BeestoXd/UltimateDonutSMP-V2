# Cuboids & Portals

A cuboid is a named box (two corners). Spawn, AFK, shard zones, RTP, and portals all ask
"is the player inside this box?"

Regions are stored in the database. Bindings live in [config.yml](Config-config.yml).
There is no `/portal` command — use `/portalmanager`.

---

## Cuboid Basics

`CuboidManager` loads every saved region on enable and keeps them in memory for the point-in-region tests other features perform. Creating one is a three step process driven by `/cuboid` and the selection wand.

### Commands

`/cuboid` requires `ultimatedonutsmp2.admin.cuboid`; the command node `ultimatedonutsmp2.command.cuboid` gates the command itself.

| Subcommand | Description |
| --- | --- |
| `/cuboid wand` | Gives the selection wand |
| `/cuboid create <name>` | Saves the current selection under a name. `save` is accepted as an alias |
| `/cuboid delete <name>` | Removes a saved region |
| `/cuboid list` | Lists saved regions |
| `/cuboid setspawn <name>` | Stores your current location as that region's spawn point |
| `/cuboid delspawn <name>` | Clears the stored spawn point |
| `/cuboid bind <cuboid> <spawn\|shard\|rtp-zone\|rtp-queue> <true\|false>` | Binds or unbinds the region to a feature. `system` is accepted as an alias for `bind` |
| `/cuboid reload` | Reloads regions from storage |

### The selection wand

`/cuboid wand` hands out a golden shovel named "cuboid wand". `CuboidWandListener` recognises it by a persistent data marker as well as by that display name, so a renamed copy still works. Left-clicking a block sets the first corner and right-clicking sets the second; both interactions are cancelled, and block breaking with the wand in hand is blocked, so you cannot damage the world while selecting.

Once both corners are set the wand is removed from your inventory automatically and you are shown a clickable prompt that fills in `/cuboid create `. The listener only responds for players holding `ultimatedonutsmp2.admin.cuboid`, so a wand that falls into someone else's hands behaves like an ordinary shovel.

### Region spawn points

`/cuboid setspawn <name>` stores a location for a region under `CUBOID-SPAWNS` in `config.yml`, written at runtime rather than shipped in the default file. The location must lie inside the region's bounds; `CuboidManager` rejects a stored point that falls outside. `/cuboid delspawn <name>` removes it.

---

## Binding Cuboids to Features

`/cuboid bind` writes into `config.yml` (and, for the RTP queue, into `rtp.yml`). The four targets behave differently, so it is worth knowing where each one lands.

| Bind target | Where it is written | What it controls |
| --- | --- | --- |
| `spawn` | `CUBOID-BINDS.SPAWN`, a list | Regions treated as spawn. Multiple regions can be bound |
| `shard` | `SHARDS.CUBOIDS.REGIONS.<name>` | Enables shard rewards for that region and marks it bound |
| `rtp-zone` | `RTP-ZONE.CUBOID`, a single value | The region that triggers the RTP countdown |
| `rtp-queue` | `QUEUE.CUBOID` in [rtp.yml](Config-rtp.yml) | The region used for RTP queue matchmaking |

`CUBOID-BINDS` also carries an `AFK` list, which is not settable through `/cuboid bind`. Use `/ultimatedonutsmp2 setup setafk` or edit `config.yml` directly.

Both lists ship empty, which means that on a fresh install nothing is treated as spawn or AFK until you bind a region.

### What the spawn and AFK binds affect

Being inside a bound spawn or AFK region changes several unrelated behaviours, so binding the wrong box has visible consequences:

- `PREVENT-ITEM-DROP.SPAWN` and `PREVENT-ITEM-DROP.AFK` in `config.yml` stop items being dropped inside those areas. `ultimatedonutsmp2.preventdrop.bypass` exempts a player.
- `AFKManager` uses the spawn cuboid to decide whether a player counts as at spawn.
- Non-staff flight requires `FLY-SYSTEM.PLAYER-FLY-PERMISSION` and only works inside spawn or a cuboid; `FLY-SYSTEM.AUTO-DISABLE-OUTSIDE` turns it off again on leaving.
- Shard regions under `SHARDS.CUBOIDS.REGIONS.<name>` each have their own reward interval, `EXCLUDED-WORLDS`, and anti-idle rules such as `MIN-MOVEMENT-BLOCKS` and `RECENT-MOVEMENT-WINDOW`. The top-level `SHARDS` keys are only a fallback used when no region is configured.

---

## Portals

`PortalManager` watches player movement and fires when a player crosses into the cuboid of an enabled portal. There is no `/portal` command; portals are administered entirely through `/portalmanager`, which requires `ultimatedonutsmp2.admin.portalmanager`. Definitions are stored in the database alongside the regions.

### Destination types

A portal has exactly two possible destination types:

| Type | Destination value | Behaviour on entry |
| --- | --- | --- |
| `RTP` | An RTP selector: either a destination ID from [rtp.yml](Config-rtp.yml) or a world name | Queues a random teleport through `RTPManager` |
| `AFK` | The AFK location | Sends the player to AFK through `AFKManager` |

There is no `SPAWN`, `WARP` or `LOCATION` type. `/portalmanager create <id> <cuboid> <rtp_selector>` is the short form of the `RTP` case, so a three-argument create always produces an RTP portal.

An `RTP` destination is only considered usable when the RTP system is enabled, the selector resolves to a real world, that world is not denied or disabled in `rtp.yml`, and search settings exist for it. A portal whose destination fails those checks reports `INVALID_DESTINATION` in `/portalmanager info` and tells the player the destination is unavailable rather than teleporting them.

### Commands

| Subcommand | Description |
| --- | --- |
| `/portalmanager list` | Lists portals with their status |
| `/portalmanager info <id>` | Shows cuboid, destination, priority, permission and status |
| `/portalmanager create <id> <cuboid> <destination_type> <value>` | Creates a portal |
| `/portalmanager create <id> <cuboid> <rtp_selector>` | Short form for an RTP portal |
| `/portalmanager delete <id>` | Deletes a portal |
| `/portalmanager setcuboid <id> <cuboid>` | Re-points the portal at another region |
| `/portalmanager setdestination <id> <destination_type> <value>` | Changes the destination |
| `/portalmanager setdestination <id> <rtp_selector>` | Short form for an RTP destination |
| `/portalmanager setdisplay <id> <display name...>` | Sets the display name used in messages and holograms |
| `/portalmanager toggle <id>` | Enables or disables the portal |
| `/portalmanager setpriority <id> <number>` | Breaks ties when regions overlap |
| `/portalmanager sethologramhere <id>` | Anchors the hologram at your current position |

A portal can carry a permission node, which the manager checks on entry, but there is no subcommand that sets it. Set it in storage or leave it blank so the portal is open to everyone.

### Entry conditions

When a player moves into a portal's region, `PortalManager` refuses the teleport and explains why in these cases: the portal is disabled, its cuboid no longer exists, its destination is not usable, the player lacks the portal's permission, the player is in combat while `PORTAL-SYSTEM.BLOCK-IN-COMBAT` is on, or another teleport of a different kind is already pending.

Two timers stop a portal from firing repeatedly. `DEFAULT-TRIGGER-COOLDOWN-MS` is the per-portal debounce after an entry, and `POST-TELEPORT-GRACE-MS` suppresses triggers for a short window after any teleport, which is what keeps a player who lands inside a portal region from being bounced straight back out. Moving between two points inside the same portal does not re-trigger it.

### Configuration

`PORTAL-SYSTEM` in [config.yml](Config-config.yml) holds the global settings:

| Key | Default | Purpose |
| --- | --- | --- |
| `ENABLED` | `true` | Master switch for the portal system |
| `BLOCK-IN-COMBAT` | `true` | Refuse portal use while the player is in combat |
| `DEFAULT-TRIGGER-COOLDOWN-MS` | `1500` | Per-portal debounce after an entry |
| `POST-TELEPORT-GRACE-MS` | `2000` | Suppression window after a teleport |
| `HOLOGRAM.ENABLED` | `true` | Draw floating text above portals |
| `HOLOGRAM.DEFAULT-REGION` | `NA East` | Value substituted for `{region}` |
| `HOLOGRAM.DEFAULT-SERVER-ID` | empty | Server ID used in hologram text |
| `HOLOGRAM.OFFSET-Y` | `1.2` | Vertical offset of the hologram |
| `HOLOGRAM.SET-HERE-OFFSET-Y` | `1.6` | Offset applied by `sethologramhere` |
| `HOLOGRAM.LINE-SPACING` | `0.27` | Spacing between hologram lines |
| `HOLOGRAM.UPDATE-TICKS` | `40` | Refresh interval |
| `HOLOGRAM.LINES` | see file | Line templates, supporting `{portal}`, `{region}` and `<total_player>` |
| `HOLOGRAM.PORTALS` | empty | Per-portal hologram overrides |

---

## Setting Up a Spawn Portal Room

A typical arrangement is a spawn region with several portal boxes cut into it, each sending players to a different RTP world.

1. Run `/cuboid wand`, left-click one corner of the spawn area and right-click the opposite corner, then `/cuboid create spawn`.
2. Bind it with `/cuboid bind spawn spawn true` so the spawn-dependent features recognise it.
3. Optionally set the return point with `/cuboid setspawn spawn` while standing where players should arrive.
4. Select the block volume of the first portal frame and save it, for example `/cuboid create portal_overworld`.
5. Create the portal with `/portalmanager create overworld portal_overworld world`, replacing `world` with the RTP destination ID or world name you want.
6. Give it a display name with `/portalmanager setdisplay overworld &aOverworld` and, if the hologram should not sit at the region centre, stand where you want it and run `/portalmanager sethologramhere overworld`.
7. Check it with `/portalmanager info overworld`; the status should read `READY`.

Repeat steps 4 to 7 per portal. Where two regions overlap, `setpriority` decides which one wins.

---

## Related Pages

- [Commands & Permissions](Commands-and-Permissions)
- [config.yml](Config-config.yml)
- [rtp.yml](Config-rtp.yml)
