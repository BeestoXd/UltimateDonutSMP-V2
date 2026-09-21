# Dialog API and older Minecraft clients

Players on **Minecraft 1.21.5 or older** cannot use UltimateDonutSMP V2 menus correctly.
This is a Minecraft client limitation, not a plugin bug, and it cannot be patched in
UltimateDonutSMP V2.

**Require Java clients 1.21.6 or newer.** Block 1.21.5 and below on the proxy or with
ViaVersion / ViaBackwards. Do not let those clients onto a live network.

---

## What happens

Join on 1.21.5 or lower (usually through ViaVersion or ViaBackwards) and dialog screens
render as a broken, unusable layout. Commands that open those screens — `/menu`, `/ah`,
`/homes`, `/pay`, `/settings`, `/stats`, `/friends`, `/rtp` queue, and similar — do not
present a usable UI. Some chest-based menus still exist for a few flows, but dialog-first
features (Auction House included) will not work for that player.

That is the same result any other plugin gets when it uses Mojang's Dialog API.

---

## Why it cannot be fixed here

[Dialogs](https://minecraft.wiki/w/Dialog) are a **vanilla Minecraft client feature**.
Mojang added the Dialog API in **1.21.6**. The server sends a dialog packet; the client
draws the screen. A 1.21.5 (or older) client has no renderer for that packet, so the
layout is garbage.

UltimateDonutSMP V2 cannot:

- Repair the old client's missing UI
- Detect each player's protocol and swap that player onto chest menus
- Ship a "DonutSMP-style" dual UI that uses dialogs on 1.21.6+ and chests on older
  clients

`DialogSupport` only checks whether **this server** exposes Paper's dialog classes. On
Spigot, or on Paper older than 1.21.6, every command falls back to the chest menus in
`menus.yml`. On a current Paper / Purpur / Pufferfish / Folia build (the versions this
plugin actually supports: Paper `26.1.2`–`26.3`, Folia `26.1.2`–`26.2`), dialogs are on for everyone. The plugin does
not look at ViaVersion protocol versions.

Turning `ENABLED: false` in [dialog.yml](Config-dialog.yml) forces chest menus for the
whole server. That is a global switch, not a per-client fallback.

---

## What operators should do

1. Tell players the Java client must be **1.21.6 or newer**.
2. On Velocity, BungeeCord, or ViaVersion, **block protocol versions below 1.21.6**.
   That is the supported way to keep 1.21.5-and-older clients off the network.
3. Do not rely on ViaBackwards as a compatibility layer for this plugin's menus.

Geyser / Floodgate Bedrock clients also lack the Java Dialog API. Treat them the same
way: do not expect dialog screens to work.

Layout and copy for the dialogs themselves live in [dialog.yml](Config-dialog.yml) and
`DIALOG` in `languages/<locale>.yml`.
