# `network.yml`

Ties several backend servers together. `NETWORK` identifies this server and lists its
siblings, `NETWORK-STATUS` drives the `/servers` menu and its player counts, and
`MAINTENANCE` configures the lock-down mode that keeps everyone but staff out.

Almost everything here depends on Redis being enabled in `database.yml`; without it the
plugin still runs, but staff chat, alerts and maintenance stay local to this server.
Player transfers use BungeeCord plugin messaging, so the names configured here must match
the server names in your proxy configuration exactly.

Comment at the top of the shipped file:

```text
UltimateDonutSMP2 - Cross-Server Network System Configuration
```

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/network.yml` |
| **Commands** | `/servers`, `/staffchat`, `/helpop`, `/report`, `/maintenance` |
| **Player-facing text** | Edit `CONFIG.NETWORK` in `languages/<locale>.yml` — not this file |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`NETWORK`](#section-network) | section | 23 keys |
| [`NETWORK-STATUS`](#section-network-status) | section | 7 keys |
| [`MAINTENANCE`](#section-maintenance) | section | 7 keys |

---

## Section: `NETWORK`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `NETWORK.ENABLED` | `boolean` | `true`, `false` | `true` | Enable or disable the cross-server network system globally (true / false) |
| `NETWORK.STAFF_CHAT_ENABLED` | `boolean` | `true`, `false` | `true` | Enable cross-server staff chat sync via Redis (true / false) |
| `NETWORK.HELPOP_ENABLED` | `boolean` | `true`, `false` | `true` | Enable cross-server helpop notification sync (true / false) |
| `NETWORK.REPORT_ENABLED` | `boolean` | `true`, `false` | `true` | Enable cross-server report notification sync (true / false) |
| `NETWORK.STAFF_JOIN_LEAVE_ENABLED` | `boolean` | `true`, `false` | `true` | Enable cross-server staff join/leave notifications (true / false) |
| `NETWORK.SERVER_STATUS_ENABLED` | `boolean` | `true`, `false` | `true` | Enable cross-server status heartbeat monitoring (true / false) |
| `NETWORK.LOCAL_SERVER_ID` | `string` | Any text | `crystal` | Unique server identifier for this local server instance |
| `NETWORK.LOCAL_DISPLAY_NAME` | `string` | Any text | `Crystal` | User-friendly server display name |
| `NETWORK.REDIS_CHANNEL` | `string` | Any text | `ultimatedonutsmp2:staff-chat` | Redis pub/sub channel for staff chat messages |
| `NETWORK.HELPOP_REDIS_CHANNEL` | `string` | Any text | `ultimatedonutsmp2:staff-alerts` | Redis pub/sub channel for helpop alerts |
| `NETWORK.REPORT_REDIS_CHANNEL` | `string` | Any text | `ultimatedonutsmp2:staff-alerts` | Redis pub/sub channel for player reports |
| `NETWORK.SEND_LOCAL_FALLBACK_ON_REDIS_ERROR` | `boolean` | `true`, `false` | `true` | Warn the sender when their staff chat message reached this server's staff but could not be published to the other servers. Local delivery happens either way (true / false) |
| `NETWORK.STAFF_ALERTS_WARN_SENDER_ON_REDIS_ERROR` | `boolean` | `true`, `false` | `false` | Warn sending player if staff alert Redis delivery fails (true / false) |
| `NETWORK.LOG_TO_CONSOLE` | `boolean` | `true`, `false` | `true` | Log staff chat messages to local server console (true / false) |
| `NETWORK.STAFF_ALERTS_LOG_TO_CONSOLE` | `boolean` | `true`, `false` | `true` | Log staff alerts to local server console (true / false) |
| `NETWORK.MAX_MESSAGE_LENGTH` | `integer` | Any integer | `512` | Maximum allowed staff chat message length (in characters) |
| `NETWORK.STAFF_ALERTS_MAX_REASON_LENGTH` | `integer` | Any integer | `256` | Maximum allowed report/helpop reason text length (in characters) |
| `NETWORK.HELPOP_COOLDOWN_SECONDS` | `integer` | Any integer | `30` | Cooldown between helpop submissions per player (in seconds) |
| `NETWORK.REPORT_COOLDOWN_SECONDS` | `integer` | Any integer | `60` | Cooldown between report submissions per player (in seconds) |
| `NETWORK.SERVER_STATUS` | `string` | Any text | `&6%server% &eis now %status%&e.` | Message format for server online/offline status broadcasts |
| `NETWORK.STAFF_CHAT` | `string` | Any text | `&8[&eNetwork&8] &7[%server%] &e%player%&8: &f%message%` | Message format for cross-server staff chat messages |
| `NETWORK.STAFF_JOIN` | `string` | Any text | `&8[&a+&8] &a%player% &7joined &b%server%` | Message format for staff member server join alert |
| `NETWORK.STAFF_LEAVE` | `string` | Any text | `&8[&c-&8] &a%player% &7left &b%server%` | Message format for staff member server leave alert |

<details>
<summary>Default <code>NETWORK</code> block as shipped</summary>

```yaml
NETWORK:
  # Enable or disable the cross-server network system globally (true / false)
  ENABLED: true

  # Enable cross-server staff chat sync via Redis (true / false)
  STAFF_CHAT_ENABLED: true

  # Enable cross-server helpop notification sync (true / false)
  HELPOP_ENABLED: true

  # Enable cross-server report notification sync (true / false)
  REPORT_ENABLED: true

  # Enable cross-server staff join/leave notifications (true / false)
  STAFF_JOIN_LEAVE_ENABLED: true

  # Enable cross-server status heartbeat monitoring (true / false)
  SERVER_STATUS_ENABLED: true

  # Unique server identifier for this local server instance
  LOCAL_SERVER_ID: crystal

  # User-friendly server display name
  LOCAL_DISPLAY_NAME: Crystal

  # Redis pub/sub channel for staff chat messages
  REDIS_CHANNEL: ultimatedonutsmp2:staff-chat

  # Redis pub/sub channel for helpop alerts
  HELPOP_REDIS_CHANNEL: ultimatedonutsmp2:staff-alerts

  # Redis pub/sub channel for player reports
  REPORT_REDIS_CHANNEL: ultimatedonutsmp2:staff-alerts

  # Warn the sender when their staff chat message reached this server's staff but could not be
  # published to the other servers. Local delivery happens either way (true / false)
  SEND_LOCAL_FALLBACK_ON_REDIS_ERROR: true

  # Warn sending player if staff alert Redis delivery fails (true / false)
  STAFF_ALERTS_WARN_SENDER_ON_REDIS_ERROR: false

  # Log staff chat messages to local server console (true / false)
  LOG_TO_CONSOLE: true

  # Log staff alerts to local server console (true / false)
  STAFF_ALERTS_LOG_TO_CONSOLE: true

  # Maximum allowed staff chat message length (in characters)
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `NETWORK-STATUS`

Network status monitoring & HTTP endpoint configuration

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `NETWORK-STATUS.ENABLED` | `boolean` | `true`, `false` | `true` | Enable network status monitoring dashboard (true / false) |
| `NETWORK-STATUS.LOCAL-SERVER-ID` | `string` | Any text | `crystal` | Local server ID alias for status check |
| `NETWORK-STATUS.LOCAL-DISPLAY-NAME` | `string` | Any text | `Crystal` | Local display name alias for status check |
| `NETWORK-STATUS.REFRESH-SECONDS` | `integer` | Any integer | `5` | Interval in seconds between network heartbeat status refreshes |
| `NETWORK-STATUS.TIMEOUT-MS` | `integer` | Any integer | `1500` | Timeout in milliseconds for server ping status checks |

### `NETWORK-STATUS.ENDPOINT`

Internal REST API HTTP endpoint for external monitoring

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `NETWORK-STATUS.ENDPOINT.ENABLED` | `boolean` | `true`, `false` | `false` | Enable HTTP status endpoint server (true / false) |
| `NETWORK-STATUS.ENDPOINT.HOST` | `string` | Any text | `0.0.0.0` | Host IP address to bind HTTP endpoint server |
| `NETWORK-STATUS.ENDPOINT.PORT` | `integer` | Any integer | `8123` | Port number for HTTP status endpoint |
| `NETWORK-STATUS.ENDPOINT.PATH` | `string` | Any text | `/status` | Endpoint URI path |
| `NETWORK-STATUS.ENDPOINT.TOKEN` | `string` | Any text | `change-me` | Secret authorization token for HTTP status queries |

### `NETWORK-STATUS.SERVERS`

Configuration for remote network servers to monitor

#### `NETWORK-STATUS.SERVERS.crystal`

##### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `NETWORK-STATUS.SERVERS.crystal.DISPLAY` | `string` | Any text | `Crystal` | Display. |

##### `NETWORK-STATUS.SERVERS.crystal.SOURCE`

###### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `NETWORK-STATUS.SERVERS.crystal.SOURCE.TYPE` | `string` | Any text | `LOCAL` | Which option this section uses. |

<details>
<summary>Default <code>NETWORK-STATUS</code> block as shipped</summary>

```yaml
# Network status monitoring & HTTP endpoint configuration
NETWORK-STATUS:
  # Enable network status monitoring dashboard (true / false)
  ENABLED: true

  # Local server ID alias for status check
  LOCAL-SERVER-ID: crystal

  # Local display name alias for status check
  LOCAL-DISPLAY-NAME: Crystal

  # Interval in seconds between network heartbeat status refreshes
  REFRESH-SECONDS: 5

  # Timeout in milliseconds for server ping status checks
  TIMEOUT-MS: 1500

  # Internal REST API HTTP endpoint for external monitoring
  ENDPOINT:
    # Enable HTTP status endpoint server (true / false)
    ENABLED: false
    # Host IP address to bind HTTP endpoint server
    HOST: 0.0.0.0
    # Port number for HTTP status endpoint
    PORT: 8123
    # Endpoint URI path
    PATH: /status
    # Secret authorization token for HTTP status queries
    TOKEN: change-me

  # Configuration for remote network servers to monitor
  SERVERS:
    crystal:
      DISPLAY: Crystal
      SOURCE:
        TYPE: LOCAL
```

</details>

---

## Section: `MAINTENANCE`

Maintenance mode behaviour (/maintenance on|off|status|setlobby)

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MAINTENANCE.BYPASS_PERMISSION` | `string` | Any text | `ULTIMATEDONUTSMP2.ADMIN.MAINTENANCE.BYPASS` | Permission node that lets a player join while maintenance mode is active |
| `MAINTENANCE.USE_PROXY` | `boolean` | `true`, `false` | `true` | Move players to another server through the proxy (true) or keep them on this one (false) |
| `MAINTENANCE.LOBBY_SERVER` | `string` | Any text | `lobby` | Proxy server players are moved to while maintenance is active, used when USE_PROXY is true. Leave it empty when this server has no lobby to hand players to: maintenance then refuses the connection at login instead of letting players in and kicking them a moment later |
| `MAINTENANCE.LOBBY_WORLD` | `string` | Any text | `WORLD` | World players are teleported to while maintenance is active, used when USE_PROXY is false. Falls back to the spawn location when that world is not loaded. Leave it empty when this server has nowhere to put players: maintenance then kicks everyone who is online and refuses the connection at login, so nobody is left standing in the world while the server is shut |
| `MAINTENANCE.RECONNECT_DELAY_SECONDS` | `integer` | Any integer | `5` | Countdown in seconds shown before players are sent back once the server returns |

### `MAINTENANCE.MESSAGES`

Everything maintenance says to players. Colour codes work in all of them

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MAINTENANCE.MESSAGES.ENTERING` | `string` | Any text | `&e[Maintenance] &7server is entering maintenance. Moving …` | Sent to everyone still online when /maintenance on runs |
| `MAINTENANCE.MESSAGES.NOT_ALLOWED` | `string` | Any text | `&e[Maintenance] &cthis server is currently in maintenance…` | Sent to a player who joins during maintenance without the bypass permission |
| `MAINTENANCE.MESSAGES.BYPASS_JOIN` | `string` | Any text | `&e[Maintenance] &7you joined while maintenance mode is ac…` | Sent instead to a player who joins holding the bypass permission |
| `MAINTENANCE.MESSAGES.KICK_FALLBACK` | `string` | Any text | `&cThis server is in maintenance and no lobby is available.` | Disconnect screen text, used when a proxy handoff fails and on every login refused because no lobby is set |
| `MAINTENANCE.MESSAGES.RECONNECTING_TITLE` | `string` | Any text | `&a&lServer online` | Title shown while players wait to be sent back once the server returns |
| `MAINTENANCE.MESSAGES.RECONNECTING_SUBTITLE` | `string` | Any text | `&7Sending you back in %seconds% seconds...` | Subtitle under it. %seconds% is the time left on RECONNECT_DELAY_SECONDS |

### `MAINTENANCE.SERVER_LIST`

How this server looks in the multiplayer list while maintenance mode is active

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `MAINTENANCE.SERVER_LIST.ENABLED` | `boolean` | `true`, `false` | `true` | Set to false to leave the server list entry alone and only gate the login |
| `MAINTENANCE.SERVER_LIST.LINES` | `list` | A list of values | _list of 2 items_ | The lines shown under the server name. The client draws the first two. %time% is the time left before maintenance lifts itself, written as 03:29, or 1:03:29 once more than an hour is left |
| `MAINTENANCE.SERVER_LIST.LINES_NO_TIMER` | `list` | A list of values | _list of 2 items_ | Used instead of LINES when maintenance was started with no duration, as in a plain /maintenance on, so the entry never shows a countdown with nothing to count down to |
| `MAINTENANCE.SERVER_LIST.VERSION_LABEL` | `string` | Any text | `&cMaintenance` | Text shown where the player count normally sits. The client draws it in red next to a broken connection icon, which is what makes the entry stand out in a long server list. Leave it empty to keep the real player count on show |
| `MAINTENANCE.SERVER_LIST.HOVER` | `list` | A list of values | _list of 1 item_ | Lines shown when the player count is hovered. Leaving the list empty keeps the usual sample of online player names |

<details>
<summary>Default contents of <code>MAINTENANCE.SERVER_LIST.LINES</code> (2 items)</summary>

```yaml
LINES:
  - '&cCurrently under maintenance'
  - '&bCome back in: &e%time%'
```

</details>

<details>
<summary>Default contents of <code>MAINTENANCE.SERVER_LIST.LINES_NO_TIMER</code> (2 items)</summary>

```yaml
LINES_NO_TIMER:
  - '&cCurrently under maintenance'
  - '&7come back later'
```

</details>

<details>
<summary>Default contents of <code>MAINTENANCE.SERVER_LIST.HOVER</code> (1 item)</summary>

```yaml
HOVER:
  - '&cCurrently under maintenance'
```

</details>

<details>
<summary>Default <code>MAINTENANCE</code> block as shipped</summary>

```yaml
# Maintenance mode behaviour (/maintenance on|off|status|setlobby)
MAINTENANCE:
  # Permission node that lets a player join while maintenance mode is active
  BYPASS_PERMISSION: ULTIMATEDONUTSMP2.ADMIN.MAINTENANCE.BYPASS

  # Move players to another server through the proxy (true) or keep them on this one (false)
  USE_PROXY: true

  # Proxy server players are moved to while maintenance is active, used when USE_PROXY is true.
  # Leave it empty when this server has no lobby to hand players to: maintenance then refuses
  # the connection at login instead of letting players in and kicking them a moment later
  LOBBY_SERVER: lobby

  # World players are teleported to while maintenance is active, used when USE_PROXY is false.
  # Falls back to the spawn location when that world is not loaded. Leave it empty when this
  # server has nowhere to put players: maintenance then kicks everyone who is online and refuses
  # the connection at login, so nobody is left standing in the world while the server is shut
  LOBBY_WORLD: WORLD

  # Countdown in seconds shown before players are sent back once the server returns
  RECONNECT_DELAY_SECONDS: 5

  # Everything maintenance says to players. Colour codes work in all of them
  MESSAGES:
    # Sent to everyone still online when /maintenance on runs
    ENTERING: '&e[Maintenance] &7server is entering maintenance. Moving you to the lobby...'

    # Sent to a player who joins during maintenance without the bypass permission
    NOT_ALLOWED: '&e[Maintenance] &cthis server is currently in maintenance. Redirecting to lobby...'

    # Sent instead to a player who joins holding the bypass permission
    BYPASS_JOIN: '&e[Maintenance] &7you joined while maintenance mode is active.'

    # Disconnect screen text, used when a proxy handoff fails and on every login refused
    # because no lobby is set
    KICK_FALLBACK: '&cThis server is in maintenance and no lobby is available.'

    # Title shown while players wait to be sent back once the server returns
    RECONNECTING_TITLE: '&a&lServer online'

    # Subtitle under it. %seconds% is the time left on RECONNECT_DELAY_SECONDS
    RECONNECTING_SUBTITLE: '&7Sending you back in %seconds% seconds...'

  # How this server looks in the multiplayer list while maintenance mode is active
  SERVER_LIST:
    # Set to false to leave the server list entry alone and only gate the login
    ENABLED: true

# ... section continues, see the file on disk for the full block
```

</details>

---

Defaults above match the file shipped in the jar.
