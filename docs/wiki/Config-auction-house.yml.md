# `auction-house.yml`

The player-to-player marketplace behind `/ah`. Listings live in the database, so this file
controls the rules around them rather than the listings themselves: how long a listing
lasts, what it may cost, what may be sold, how unclaimed items come back to their owner,
and how the browse GUI is laid out.

Two things are worth calling out. `RESTRICTIONS` is the safety net that stops players
listing items you never intended to be tradable, and `BOTS` generates synthetic listings
so a quiet server still looks alive — turn it off if you want a purely player-driven
economy. Listing limits are also permission-tiered through
`ultimatedonutsmp2.auctionhouse.limit.<N>`, which overrides the default limit set here.

Comment at the top of the shipped file:

```text
UltimateDonutSMP2 - Auction House Configuration
```

| | |
| :--- | :--- |
| **On disk** | `plugins/UltimateDonutSmp2/auction-house.yml` |
| **Commands** | `/auctionhouse` (`/ah`) with `sell`, `my`, `claims`, `cancel`, `limit`, `fastbuy`, `fastsell`, `reload` |
| **Player-facing text** | Edit `CONFIG.AUCTION_HOUSE` in `languages/<locale>.yml` — not this file |
| **Reload** | `/auctionhouse reload` or `/ultimatedonutsmp2 reload` |

## Sections in this file

| Section | Type | Contents |
| :--- | :--- | :--- |
| [`SETTINGS`](#section-settings) | section | 6 keys |
| [`PRICING`](#section-pricing) | section | 4 keys |
| [`RESTRICTIONS`](#section-restrictions) | section | 2 keys |
| [`CLAIMS`](#section-claims) | section | 1 keys |
| [`GUI`](#section-gui) | section | 3 keys |
| [`SORTING`](#section-sorting) | section | 2 keys |
| [`BOTS`](#section-bots) | section | 9 keys |

---

## Section: `SETTINGS`

General Auction House settings

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.ENABLED` | `boolean` | `true`, `false` | `true` | Enable or disable the Auction House system globally (true / false) |
| `SETTINGS.LISTING_DURATION_HOURS` | `integer` | Any integer | `48` | Duration in hours before an unpurchased auction listing expires |
| `SETTINGS.MAX_ACTIVE_LISTINGS_DEFAULT` | `integer` | Any integer | `5` | Default maximum active listings a player can post simultaneously |
| `SETTINGS.CLICK_COOLDOWN_MS` | `integer` | Any integer | `750` | Anti-spam click cooldown between menu interactions (in milliseconds) |
| `SETTINGS.EXPIRE_CHECK_SECONDS` | `integer` | Any integer | `30` | Interval in seconds to check for expired auction listings |

### `SETTINGS.MAX_ACTIVE_LISTINGS_BY_PERMISSION`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SETTINGS.MAX_ACTIVE_LISTINGS_BY_PERMISSION.ultimatedonutsmp2.auctionhouse.limit.10` | `integer` | Any integer | `10` | Ultimatedonutsmp2.auctionhouse.limit.10. |
| `SETTINGS.MAX_ACTIVE_LISTINGS_BY_PERMISSION.ultimatedonutsmp2.auctionhouse.limit.15` | `integer` | Any integer | `15` | Ultimatedonutsmp2.auctionhouse.limit.15. |

<details>
<summary>Default <code>SETTINGS</code> block as shipped</summary>

```yaml
# General Auction House settings
SETTINGS:
  # Enable or disable the Auction House system globally (true / false)
  ENABLED: true
  # Duration in hours before an unpurchased auction listing expires
  LISTING_DURATION_HOURS: 48
  # Default maximum active listings a player can post simultaneously
  MAX_ACTIVE_LISTINGS_DEFAULT: 5
  # Explicit mapping from permission node to a maximum active listing count
  # Players can also be given ultimatedonutsmp2.auctionhouse.limit.<1-100> directly, for example
  # ultimatedonutsmp2.auctionhouse.limit.25 for 25 active listings, without adding it below
  # Each value is a total, not a bonus added on top of MAX_ACTIVE_LISTINGS_DEFAULT
  # The highest value the player has wins, and a negative value here means unlimited
  # Wildcards do not grant a limit, the node has to be set on the player or their group
  MAX_ACTIVE_LISTINGS_BY_PERMISSION:
    ultimatedonutsmp2.auctionhouse.limit.10: 10
    ultimatedonutsmp2.auctionhouse.limit.15: 15
  # Anti-spam click cooldown between menu interactions (in milliseconds)
  CLICK_COOLDOWN_MS: 750
  # Interval in seconds to check for expired auction listings
  EXPIRE_CHECK_SECONDS: 30
```

</details>

---

## Section: `PRICING`

Pricing, fees, and tax limits

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `PRICING.MIN_PRICE` | `integer` | Any integer | `100` | Minimum allowed listing price for an item |
| `PRICING.MAX_PRICE` | `integer` | Any integer | `100000000` | Maximum allowed listing price for an item |
| `PRICING.LISTING_FEE` | `integer` | Any integer | `0` | Flat fee charged upfront to create an auction listing |
| `PRICING.TAX_PERCENT` | `decimal` | Any decimal number | `5.0` | Percentage tax deducted from the final sale price upon purchase (%) |

<details>
<summary>Default <code>PRICING</code> block as shipped</summary>

```yaml
# Pricing, fees, and tax limits
PRICING:
  # Minimum allowed listing price for an item
  MIN_PRICE: 100
  # Maximum allowed listing price for an item
  MAX_PRICE: 100000000
  # Flat fee charged upfront to create an auction listing
  LISTING_FEE: 0
  # Percentage tax deducted from the final sale price upon purchase (%)
  TAX_PERCENT: 5.0
```

</details>

---

## Section: `RESTRICTIONS`

Item restrictions and blacklist

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `RESTRICTIONS.BLOCKED_MATERIALS` | `list` | A list of values | _list of 9 items_ | Materials blocked from being listed on the Auction House |
| `RESTRICTIONS.BLOCKED_IF_HAS_LORE_CONTAINS` | `list` | A list of values | _(empty list)_ | Lore text phrases that block an item from being listed if present |

<details>
<summary>Default contents of <code>RESTRICTIONS.BLOCKED_MATERIALS</code> (9 items)</summary>

```yaml
BLOCKED_MATERIALS:
  - 'BEDROCK'
  - 'BARRIER'
  - 'COMMAND_BLOCK'
  - 'CHAIN_COMMAND_BLOCK'
  - 'REPEATING_COMMAND_BLOCK'
  - 'STRUCTURE_BLOCK'
  - 'STRUCTURE_VOID'
  - 'JIGSAW'
  - 'LIGHT'
```

</details>

<details>
<summary>Default <code>RESTRICTIONS</code> block as shipped</summary>

```yaml
# Item restrictions and blacklist
RESTRICTIONS:
  # Materials blocked from being listed on the Auction House
  BLOCKED_MATERIALS:
    - BEDROCK
    - BARRIER
    - COMMAND_BLOCK
    - CHAIN_COMMAND_BLOCK
    - REPEATING_COMMAND_BLOCK
    - STRUCTURE_BLOCK
    - STRUCTURE_VOID
    - JIGSAW
    - LIGHT
  # Lore text phrases that block an item from being listed if present
  BLOCKED_IF_HAS_LORE_CONTAINS: []
```

</details>

---

## Section: `CLAIMS`

Claims system configuration

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `CLAIMS.ENABLED` | `boolean` | `true`, `false` | `true` | Enable or disable auction claims collection system (true / false) |

<details>
<summary>Default <code>CLAIMS</code> block as shipped</summary>

```yaml
# Claims system configuration
CLAIMS:
  # Enable or disable auction claims collection system (true / false)
  ENABLED: true
```

</details>

---

## Section: `GUI`

GUI Inventory Titles and Sizes

### `GUI.BROWSE`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.BROWSE.TITLE` | `string` | Any text | `Auction (Page {page})` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.BROWSE.SIZE` | `integer` | Any integer | `54` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `GUI.BROWSE.ITEMS_PER_PAGE` | `integer` | Any integer | `45` | Items per page. |

#### `GUI.BROWSE.CONTROLS`

##### Entry schema (7 entries)

Each entry under `GUI.BROWSE.CONTROLS` is keyed by a name you choose, and every entry accepts the same options:

##### Shipped entries

| Entry key | `NAME` | `MATERIAL` | `SLOT` |
| :--- | :--- | :--- | :--- |
| `PREVIOUS` | `&fPrevious page` | `ARROW` | `45` |
| `FILTER` | `&fFilter` | `HOPPER` | `47` |
| `QUICK_BUY` | `&fQuick Buy` | `ENDER_CHEST` | `48` |
| `REFRESH` | `&fAuction` | `ANVIL` | `49` |
| `SEARCH` | `&fSearch` | `OAK_SIGN` | `50` |
| `PLAYER_ITEMS` | `&fYour Items` | `CHEST` | `51` |
| `NEXT` | `&fNext page` | `ARROW` | `53` |

| Key | Type | Accepted values | Required? | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `LORE` | `list` | A list of values | Required | Tooltip lines under the item name. |
| `MATERIAL` | `string` | Any text | Required | Bukkit `Material` name for the icon. |
| `NAME` | `string` | Any text | Required | Display name shown to players. |
| `SLOT` | `integer` | Any integer | Required | Inventory slot, `0` is the top-left cell. |

### `GUI.MY_LISTINGS`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.MY_LISTINGS.TITLE` | `string` | Any text | `Auction -&gt; Your Items` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.MY_LISTINGS.SIZE` | `integer` | Any integer | `54` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `GUI.MY_LISTINGS.ITEMS_PER_PAGE` | `integer` | Any integer | `45` | Items per page. |

### `GUI.CLAIMS`

#### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `GUI.CLAIMS.TITLE` | `string` | Any text | `Auction -&gt; Your Items` | Title text. Supports `&` colours and `&#RRGGBB` hex. |
| `GUI.CLAIMS.SIZE` | `integer` | Any integer | `54` | Chest size. Must be a multiple of 9 between 9 and 54. |
| `GUI.CLAIMS.ITEMS_PER_PAGE` | `integer` | Any integer | `45` | Items per page. |

<details>
<summary>Default <code>GUI</code> block as shipped</summary>

```yaml
# GUI Inventory Titles and Sizes
GUI:
  BROWSE:
    TITLE: 'Auction (Page {page})'
    SIZE: 54
    ITEMS_PER_PAGE: 45
    CONTROLS:
      PREVIOUS:
        SLOT: 45
        MATERIAL: ARROW
        NAME: '&fPrevious page'
        LORE:
          - '&7Go to page &f{page}'
      FILTER:
        SLOT: 47
        MATERIAL: HOPPER
        NAME: '&fFilter'
        LORE:
          - '&o&7Click to change'
      QUICK_BUY:
        SLOT: 48
        MATERIAL: ENDER_CHEST
        NAME: '&fQuick Buy'
        LORE:
          - '&o&7Click to view'
      REFRESH:
        SLOT: 49
        MATERIAL: ANVIL
        NAME: '&fAuction'
        LORE:
          - '&o&7Buy and sell items'
      SEARCH:
        SLOT: 50
        MATERIAL: OAK_SIGN
        NAME: '&fSearch'
        LORE:
          - '&o&7Click to search'
      PLAYER_ITEMS:
        SLOT: 51
        MATERIAL: CHEST
        NAME: '&fYour Items'
        LORE:
          - '&o&7Click to view'
      NEXT:
        SLOT: 53
        MATERIAL: ARROW
        NAME: '&fNext page'
        LORE:
# ... section continues, see the file on disk for the full block
```

</details>

---

## Section: `SORTING`

Auction sorting configuration

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `SORTING.DEFAULT` | `string` | Any text | `PRICE_LOWEST` | Default sorting method: PRICE_LOWEST, PRICE_HIGHEST, NEWEST, EXPIRING_SOON, OLDEST |
| `SORTING.ALLOWED` | `list` | A list of values | _list of 5 items_ | List of allowed sorting options in the GUI |

<details>
<summary>Default contents of <code>SORTING.ALLOWED</code> (5 items)</summary>

```yaml
ALLOWED:
  - 'PRICE_LOWEST'
  - 'PRICE_HIGHEST'
  - 'NEWEST'
  - 'EXPIRING_SOON'
  - 'OLDEST'
```

</details>

<details>
<summary>Default <code>SORTING</code> block as shipped</summary>

```yaml
# Auction sorting configuration
SORTING:
  # Default sorting method: PRICE_LOWEST, PRICE_HIGHEST, NEWEST, EXPIRING_SOON, OLDEST
  DEFAULT: PRICE_LOWEST
  # List of allowed sorting options in the GUI
  ALLOWED:
    - PRICE_LOWEST
    - PRICE_HIGHEST
    - NEWEST
    - EXPIRING_SOON
    - OLDEST
```

</details>

---

## Section: `BOTS`

Automated Auction House Bot System Configuration

### Options

| Option path | Type | Accepted values | Default | What it does |
| :--- | :--- | :--- | :--- | :--- |
| `BOTS.ENABLED` | `boolean` | `true`, `false` | `false` | Enable or disable automated bot auction listings (true / false) |
| `BOTS.MIN_CHECK_INTERVAL_SECONDS` | `integer` | Any integer | `60` | Minimum interval in seconds between bot listing checks |
| `BOTS.MAX_CHECK_INTERVAL_SECONDS` | `integer` | Any integer | `300` | Maximum interval in seconds between bot listing checks |
| `BOTS.CHANCE` | `decimal` | Any decimal number | `0.5` | Chance (0.0 to 1.0) for a bot to post a listing on each interval check |
| `BOTS.MAX_ACTIVE_BOT_LISTINGS` | `integer` | Any integer | `10` | Maximum active bot listings allowed concurrently |
| `BOTS.MIN_DURATION_HOURS` | `integer` | Any integer | `12` | Minimum duration in hours for bot auction listings |
| `BOTS.MAX_DURATION_HOURS` | `integer` | Any integer | `48` | Maximum duration in hours for bot auction listings |
| `BOTS.BOT_NAMES` | `list` | A list of values | _list of 3 items_ | List of bot names displayed as sellers |
| `BOTS.ITEMS` | `list` | A list of values | _list of 3 items_ | Items that bots can list on the Auction House |

<details>
<summary>Default contents of <code>BOTS.BOT_NAMES</code> (3 items)</summary>

```yaml
BOT_NAMES:
  - 'DonutBot'
  - 'AuctionBot'
  - 'ShopKeeper'
```

</details>

<details>
<summary>Default contents of <code>BOTS.ITEMS</code> (3 items)</summary>

```yaml
ITEMS:
  - {ENCHANTS: ['SHARPNESS:5', 'UNBREAKING:3'], MATERIAL: DIAMOND_SWORD, MAX_AMOUNT: 1,
  MAX_PRICE: 15000, MIN_AMOUNT: 1, MIN_PRICE: 5000}
  - {MATERIAL: GOLDEN_APPLE, MAX_AMOUNT: 16, MAX_PRICE: 8000, MIN_AMOUNT: 4, MIN_PRICE: 2000}
  - {MATERIAL: COBBLESTONE, MAX_AMOUNT: 320, MAX_PRICE: 500, MIN_AMOUNT: 64, MIN_PRICE: 100}
```

</details>

<details>
<summary>Default <code>BOTS</code> block as shipped</summary>

```yaml
# Automated Auction House Bot System Configuration
BOTS:
  # Enable or disable automated bot auction listings (true / false)
  ENABLED: false
  # Minimum interval in seconds between bot listing checks
  MIN_CHECK_INTERVAL_SECONDS: 60
  # Maximum interval in seconds between bot listing checks
  MAX_CHECK_INTERVAL_SECONDS: 300
  # Chance (0.0 to 1.0) for a bot to post a listing on each interval check
  CHANCE: 0.5
  # Maximum active bot listings allowed concurrently
  MAX_ACTIVE_BOT_LISTINGS: 10
  # Minimum duration in hours for bot auction listings
  MIN_DURATION_HOURS: 12
  # Maximum duration in hours for bot auction listings
  MAX_DURATION_HOURS: 48
  # List of bot names displayed as sellers
  BOT_NAMES:
    - "DonutBot"
    - "AuctionBot"
    - "ShopKeeper"
  # Items that bots can list on the Auction House
  ITEMS:
    - MATERIAL: DIAMOND_SWORD
      MIN_AMOUNT: 1
      MAX_AMOUNT: 1
      MIN_PRICE: 5000
      MAX_PRICE: 15000
      ENCHANTS:
        - "SHARPNESS:5"
        - "UNBREAKING:3"
    - MATERIAL: GOLDEN_APPLE
      MIN_AMOUNT: 4
      MAX_AMOUNT: 16
      MIN_PRICE: 2000
      MAX_PRICE: 8000
    - MATERIAL: COBBLESTONE
      MIN_AMOUNT: 64
      MAX_AMOUNT: 320
      MIN_PRICE: 100
      MAX_PRICE: 500
```

</details>

---

Defaults above match the file shipped in the jar.
