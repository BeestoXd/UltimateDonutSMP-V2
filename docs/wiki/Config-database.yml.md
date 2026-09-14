# `database.yml`

Chooses where player data is stored, and optionally connects the server to Redis.

`DATABASE.TYPE` picks one of three backends. `SQLITE` is the default and needs no setup —
everything lands in a single file under the plugin folder. `MYSQL` is the right choice when
several servers must share one set of balances, homes and punishments. `MONGODB` is
supported as well, backed by a local cache file so the server survives a brief outage.

`REDIS` is separate from your database choice and purely for cross-server messaging:
staff chat, network alerts, maintenance state, cross-server duels and order syncing all
ride on it. Leave `REDIS.ENABLED` off on a single server; nothing else depends on it.

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/database.yml` |
| **Commands** | `/ultimatedonutsmp2 reload` |
| **Player-facing text** | Not translated. Edit this file directly. |
| **Reload** | `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`DATABASE`](#section-database) | section | 4 keys |
| [`REDIS`](#section-redis) | section | 13 keys |

---

## Section: `DATABASE`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DATABASE.TYPE` | `string` | Any text | `SQLITE` | Storage backend. `SQLITE` (default, zero setup), `MYSQL` (shared across a network), or `MONGODB`. |

### `DATABASE.SQLITE`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DATABASE.SQLITE.FILE` | `string` | Any text | `data/data.db` | Path to the SQLite file, relative to the plugin folder. |

### `DATABASE.MYSQL`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DATABASE.MYSQL.HOST` | `string` | Any text | `localhost` | Hostname or IP of the remote server. |
| `DATABASE.MYSQL.PORT` | `integer` | Any integer | `3306` | TCP port. |
| `DATABASE.MYSQL.DATABASE` | `string` | Any text | `ultimatedonutsmp2` | Database name. |
| `DATABASE.MYSQL.USERNAME` | `string` | Any text | `root` | Login username. |
| `DATABASE.MYSQL.PASSWORD` | `string` | Any text | `''` | Password. Leave empty if none is set. |
| `DATABASE.MYSQL.CREATE-DATABASE` | `boolean` | `true`, `false` | `true` | Issue `CREATE DATABASE IF NOT EXISTS` on connect. Requires a MySQL user with create rights. |
| `DATABASE.MYSQL.PARAMETERS` | `string` | Any text | `useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=…` | JDBC query string appended to the connection URL. |

### `DATABASE.MONGODB`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `DATABASE.MONGODB.URI` | `string` | Any text | `mongodb://localhost:27017` | Connection URI. |
| `DATABASE.MONGODB.DATABASE` | `string` | Any text | `ultimatedonutsmp2` | Database name. |
| `DATABASE.MONGODB.CACHE-FILE` | `string` | Any text | `data/mongodb-cache.db` | Local cache used to keep serving reads while MongoDB is unreachable. |
| `DATABASE.MONGODB.SYNC-ON-AUTOSAVE` | `boolean` | `true`, `false` | `true` | Flush the local cache to MongoDB on every autosave rather than only on shutdown. |

<details>
<summary>Default <code>DATABASE</code> block as shipped</summary>

```yaml
# Configuration section for Database.
DATABASE:
  TYPE: SQLITE
  # Configuration section for Sqlite.
  SQLITE:
    # The text or value for File. Available options: Any valid string text
    FILE: data/data.db
  # Configuration section for Mysql.
  MYSQL:
    # The text or value for Host. Available options: Any valid string text
    HOST: localhost
    # The numerical value for Port. Available options: Any valid integer
    PORT: 3306
    # The text or value for Database. Available options: Any valid string text
    DATABASE: ultimatedonutsmp2
    # The text or value for Username. Available options: Any valid string text
    USERNAME: root
    # The text or value for Password. Available options: Any valid string text
    PASSWORD: ''
    # Determines whether Create Database is enabled or disabled. Available options: true, false
    CREATE-DATABASE: true
    # The text or value for Parameters. Available options: Any valid string text
    PARAMETERS: useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8
  # Configuration section for Mongodb.
  MONGODB:
    # The text or value for Uri. Available options: Any valid string text
    URI: mongodb://localhost:27017
    # The text or value for Database. Available options: Any valid string text
    DATABASE: ultimatedonutsmp2
    # The text or value for Cache File. Available options: Any valid string text
    CACHE-FILE: data/mongodb-cache.db
    # Determines whether Sync On Autosave is enabled or disabled. Available options: true, false
    SYNC-ON-AUTOSAVE: true
```

</details>

---

## Section: `REDIS`

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `REDIS.ENABLED` | `boolean` | `true`, `false` | `false` | Enable Redis. Required for cross-server staff chat, alerts, maintenance sync, cross-server duels and order syncing. |
| `REDIS.HOST` | `string` | Any text | `localhost` | Hostname or IP of the remote server. |
| `REDIS.PORT` | `integer` | Any integer | `6379` | TCP port. |
| `REDIS.TIMEOUT` | `integer` | Any integer | `2000` | Timeout. |
| `REDIS.PASSWORD` | `string` | Any text | `''` | Password. Leave empty if none is set. |
| `REDIS.DATABASE` | `integer` | Any integer | `0` | Redis logical database index. Use a different index per network if several plugins share the server. |
| `REDIS.MAX-TOTAL` | `integer` | Any integer | `50` | Max total. |
| `REDIS.MAX-IDLE` | `integer` | Any integer | `10` | Max idle. |
| `REDIS.MIN-IDLE` | `integer` | Any integer | `5` | Min idle. |
| `REDIS.TEST-ON-BORROW` | `boolean` | `true`, `false` | `false` | On/off for test on borrow. |
| `REDIS.TEST-ON-RETURN` | `boolean` | `true`, `false` | `false` | On/off for test on return. |
| `REDIS.TEST-WHILE-IDLE` | `boolean` | `true`, `false` | `false` | On/off for test while idle. |
| `REDIS.RECONNECT-DELAY-MS` | `integer` | Any integer | `5000` | Delay before retrying a dropped Redis connection, in milliseconds. |

<details>
<summary>Default <code>REDIS</code> block as shipped</summary>

```yaml
# Configuration section for Redis.
REDIS:
  # Determines whether Enabled is enabled or disabled. Available options: true, false
  ENABLED: false
  # The text or value for Host. Available options: Any valid string text
  HOST: localhost
  # The numerical value for Port. Available options: Any valid integer
  PORT: 6379
  # The numerical value for Timeout. Available options: Any valid integer
  TIMEOUT: 2000
  # The text or value for Password. Available options: Any valid string text
  PASSWORD: ''
  # The numerical value for Database. Available options: Any valid integer
  DATABASE: 0
  # The numerical value for Max Total. Available options: Any valid integer
  MAX-TOTAL: 50
  # The numerical value for Max Idle. Available options: Any valid integer
  MAX-IDLE: 10
  # The numerical value for Min Idle. Available options: Any valid integer
  MIN-IDLE: 5
  # Determines whether Test On Borrow is enabled or disabled. Available options: true, false
  TEST-ON-BORROW: false
  # Determines whether Test On Return is enabled or disabled. Available options: true, false
  TEST-ON-RETURN: false
  # Determines whether Test While Idle is enabled or disabled. Available options: true, false
  TEST-WHILE-IDLE: false
  # The numerical value for Reconnect Delay Ms. Available options: Any valid integer
  RECONNECT-DELAY-MS: 5000
```

</details>

---

Defaults above match the file shipped in the jar.
