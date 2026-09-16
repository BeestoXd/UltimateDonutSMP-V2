package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.TablistManager.SkinTexture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Manages player skin textures, ensuring offline players retain their skin heads in menus.
 */
public class PlayerSkinManager {

    private static final String CACHE_FILE_NAME = "skin-cache.json";
    private static final long NEGATIVE_CACHE_TTL_MS = 60_000L;
    private static final long DEBOUNCE_SAVE_TICKS = 100L; // 5 seconds

    private final UltimateDonutSmp2 plugin;
    private final File cacheFile;
    private final Map<UUID, SkinTexture> skinCache = new ConcurrentHashMap<>();
    private final Set<UUID> pendingLookups = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Long> negativeCache = new ConcurrentHashMap<>();
    private final AtomicBoolean dirty = new AtomicBoolean(false);
    private boolean saveScheduled = false;

    public PlayerSkinManager(UltimateDonutSmp2 plugin) {
        this(plugin, plugin != null ? plugin.getDataFolder() : new File("target/test-data"));
    }

    public PlayerSkinManager(UltimateDonutSmp2 plugin, File dataFolder) {
        this.plugin = plugin;
        this.cacheFile = new File(dataFolder, CACHE_FILE_NAME);
    }

    /**
     * Loads persisted skins from disk into memory.
     */
    public void loadCache() {
        if (!cacheFile.exists() || cacheFile.length() == 0) {
            return;
        }

        try (FileReader reader = new FileReader(cacheFile, StandardCharsets.UTF_8)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (!parsed.isJsonObject()) {
                return;
            }

            JsonObject root = parsed.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                try {
                    UUID uuid = UUID.fromString(entry.getKey());
                    if (entry.getValue().isJsonObject()) {
                        JsonObject data = entry.getValue().getAsJsonObject();
                        String value = data.has("value") && !data.get("value").isJsonNull()
                                ? data.get("value").getAsString()
                                : null;
                        String signature = data.has("signature") && !data.get("signature").isJsonNull()
                                ? data.get("signature").getAsString()
                                : null;
                        if (value != null && !value.isBlank()) {
                            skinCache.put(uuid, new SkinTexture(value, signature));
                        }
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (plugin != null) {
                plugin.getLogger().info(() -> "Loaded " + skinCache.size() + " player skin textures from cache.");
            }
        } catch (Throwable t) {
            if (plugin != null) {
                plugin.getLogger().warning(() -> "Failed to load skin cache: " + t.getMessage());
            }
        }
    }

    /**
     * Saves the in-memory skin cache to disk atomically.
     */
    public synchronized void saveCache() {
        if (skinCache.isEmpty()) {
            return;
        }

        dirty.set(false);
        File folder = cacheFile.getParentFile();
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }
        File tempFile = new File(folder, CACHE_FILE_NAME + ".tmp");
        try {
            JsonObject root = new JsonObject();
            for (Map.Entry<UUID, SkinTexture> entry : skinCache.entrySet()) {
                SkinTexture texture = entry.getValue();
                if (texture != null && texture.isValid()) {
                    JsonObject data = new JsonObject();
                    data.addProperty("value", texture.value());
                    if (texture.signature() != null) {
                        data.addProperty("signature", texture.signature());
                    }
                    root.add(entry.getKey().toString(), data);
                }
            }

            try (FileWriter writer = new FileWriter(tempFile, StandardCharsets.UTF_8)) {
                writer.write(root.toString());
            }

            Files.move(tempFile.toPath(), cacheFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Throwable t) {
            if (plugin != null) {
                plugin.getLogger().warning(() -> "Failed to save skin cache: " + t.getMessage());
            }
            try {
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private synchronized void scheduleSave() {
        if (plugin == null || plugin.getSpigotScheduler() == null) {
            saveCache();
            return;
        }
        if (saveScheduled) {
            return;
        }
        saveScheduled = true;
        plugin.getSpigotScheduler().runAsyncLater(() -> {
            synchronized (this) {
                saveScheduled = false;
                if (dirty.get()) {
                    saveCache();
                }
            }
        }, DEBOUNCE_SAVE_TICKS);
    }

    /**
     * Captures and caches the skin texture of an online player.
     */
    public SkinTexture captureSkin(Player player) {
        if (player == null || !player.isOnline()) {
            return null;
        }

        UUID uuid = player.getUniqueId();
        SkinTexture texture = extractLiveSkinTexture(player);
        if (texture != null && texture.isValid()) {
            cacheSkin(uuid, texture);
            return texture;
        }
        return null;
    }

    /**
     * Caches a skin texture for a player UUID.
     */
    public void cacheSkin(UUID uuid, SkinTexture texture) {
        if (uuid == null || texture == null || !texture.isValid()) {
            return;
        }

        SkinTexture existing = skinCache.get(uuid);
        if (existing == null || !texture.value().equals(existing.value())) {
            skinCache.put(uuid, texture);
            negativeCache.remove(uuid);
            dirty.set(true);
            scheduleSave();

            TablistManager tablist = plugin != null ? plugin.getTablistManager() : null;
            if (tablist != null) {
                tablist.updateSkinTexture(uuid, texture.value(), texture.signature());
            }
        }
    }

    /**
     * Resolves the skin texture synchronously from available caches and online profiles.
     */
    public SkinTexture resolveSkinTexture(UUID uuid, String name) {
        if (uuid == null && (name == null || name.isBlank())) {
            return null;
        }

        // 1. Check in-memory skin cache
        if (uuid != null) {
            SkinTexture cached = skinCache.get(uuid);
            if (cached != null && cached.isValid()) {
                return cached;
            }
        }

        // 2. If disguised in HideManager, return disguise skin
        if (uuid != null && plugin != null && plugin.getHideManager() != null && plugin.getHideManager().isHidden(uuid)) {
            var state = plugin.getHideManager().getState(uuid);
            if (state != null && state.hasTexture()) {
                return new SkinTexture(state.textureValue(), state.textureSignature());
            }
        }

        // 3. Check if player is currently online
        if (Bukkit.getServer() != null) {
            Player online = uuid != null ? Bukkit.getPlayer(uuid) : null;
            if (online == null && name != null && !name.isBlank()) {
                online = Bukkit.getPlayerExact(name);
            }
            if (online != null && online.isOnline()) {
                SkinTexture captured = captureSkin(online);
                if (captured != null && captured.isValid()) {
                    return captured;
                }
            }
        }

        // 4. Check TablistManager cache
        TablistManager tablist = plugin != null ? plugin.getTablistManager() : null;
        if (tablist != null && uuid != null) {
            SkinTexture tablistSr = tablist.cachedSkinsRestorerTexture(uuid);
            if (tablistSr != null && tablistSr.isValid()) {
                cacheSkin(uuid, tablistSr);
                return tablistSr;
            }
            SkinTexture tablistCached = tablist.cachedSkinTexture(uuid);
            if (tablistCached != null && tablistCached.isValid()) {
                cacheSkin(uuid, tablistCached);
                return tablistCached;
            }
        }

        // 5. Check SkinsRestorer if installed
        if ((uuid != null || (name != null && !name.isBlank())) && Bukkit.getServer() != null) {
            try {
                if (Bukkit.getPluginManager() != null && Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
                    SkinTexture sr = SkinsRestorerSkinLookup.resolve(uuid, name);
                    if (sr != null && sr.isValid()) {
                        if (uuid != null) {
                            cacheSkin(uuid, sr);
                        }
                        return sr;
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        // 6. Check Paper OfflinePlayer profile cache if available
        if (uuid != null && Bukkit.getServer() != null) {
            try {
                OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
                Object profile = offline.getClass().getMethod("getPlayerProfile").invoke(offline);
                if (profile instanceof com.destroystokyo.paper.profile.PlayerProfile paperProfile) {
                    for (com.destroystokyo.paper.profile.ProfileProperty prop : paperProfile.getProperties()) {
                        if (prop != null && "textures".equalsIgnoreCase(prop.getName())
                                && prop.getValue() != null && !prop.getValue().isBlank()) {
                            SkinTexture texture = new SkinTexture(prop.getValue(), prop.getSignature());
                            cacheSkin(uuid, texture);
                            return texture;
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        return null;
    }

    /**
     * Resolves the skin texture asynchronously, invoking callback on completion.
     */
    public void resolveSkinTextureAsync(UUID uuid, String name, Consumer<SkinTexture> callback) {
        if (uuid == null && (name == null || name.isBlank())) {
            return;
        }

        SkinTexture cached = resolveSkinTexture(uuid, name);
        if (cached != null && cached.isValid()) {
            if (callback != null) {
                callback.accept(cached);
            }
            return;
        }

        if (uuid != null) {
            if (pendingLookups.contains(uuid)) {
                return;
            }
            Long negativeTime = negativeCache.get(uuid);
            if (negativeTime != null && System.currentTimeMillis() - negativeTime < NEGATIVE_CACHE_TTL_MS) {
                return;
            }
            pendingLookups.add(uuid);
        }

        plugin.getSpigotScheduler().runAsync(() -> {
            try {
                SkinTexture resolved = fetchOnlineProfileTexture(uuid, name);
                if (resolved != null && resolved.isValid()) {
                    if (uuid != null) {
                        cacheSkin(uuid, resolved);
                    }
                    if (callback != null) {
                        plugin.getSpigotScheduler().runGlobal(() -> callback.accept(resolved));
                    }
                } else if (uuid != null) {
                    negativeCache.put(uuid, System.currentTimeMillis());
                }
            } finally {
                if (uuid != null) {
                    pendingLookups.remove(uuid);
                }
            }
        });
    }

    private SkinTexture extractLiveSkinTexture(Player player) {
        if (player == null || !player.isOnline()) {
            return null;
        }

        // 1. Paper PlayerProfile
        try {
            if (player.getPlayerProfile() instanceof com.destroystokyo.paper.profile.PlayerProfile paperProfile) {
                for (com.destroystokyo.paper.profile.ProfileProperty property : paperProfile.getProperties()) {
                    if (property != null
                            && "textures".equalsIgnoreCase(property.getName())
                            && property.getValue() != null
                            && !property.getValue().isBlank()) {
                        return new SkinTexture(property.getValue(), property.getSignature());
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        // 2. TablistManager
        try {
            TablistManager tablist = plugin != null ? plugin.getTablistManager() : null;
            if (tablist != null) {
                SkinTexture live = tablist.resolveLiveGameProfileSkinTexture(player);
                if (live != null && live.isValid()) {
                    return live;
                }
                SkinTexture current = tablist.resolveCurrentSkinTexture(player);
                if (current != null && current.isValid()) {
                    return current;
                }
            }
        } catch (Throwable ignored) {
        }

        // 3. Authlib GameProfile reflection
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            if (handle != null) {
                Object gameProfile = handle.getClass().getMethod("getGameProfile").invoke(handle);
                if (gameProfile != null) {
                    SkinTexture texture = extractAuthlibTexture(gameProfile);
                    if (texture != null && texture.isValid()) {
                        return texture;
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    private SkinTexture extractAuthlibTexture(Object gameProfile) {
        try {
            Object propMap = gameProfile.getClass().getMethod("getProperties").invoke(gameProfile);
            if (propMap != null) {
                Object values = propMap.getClass().getMethod("get", Object.class).invoke(propMap, "textures");
                if (values instanceof Collection<?> collection) {
                    for (Object prop : collection) {
                        String value = (String) prop.getClass().getMethod("getValue").invoke(prop);
                        String signature = null;
                        try {
                            signature = (String) prop.getClass().getMethod("getSignature").invoke(prop);
                        } catch (Throwable ignored) {
                        }
                        if (value != null && !value.isBlank()) {
                            return new SkinTexture(value, signature);
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private SkinTexture fetchOnlineProfileTexture(UUID uuid, String name) {
        // 1. Paper profile update
        if (Bukkit.getServer() != null) {
            try {
                Object profile = name != null && !name.isBlank()
                        ? Bukkit.createPlayerProfile(name)
                        : Bukkit.createPlayerProfile(uuid);
                Object updateResult = profile.getClass().getMethod("update").invoke(profile);
                if (updateResult instanceof CompletableFuture<?> future) {
                    Object updated = future.get(4L, TimeUnit.SECONDS);
                    if (updated instanceof com.destroystokyo.paper.profile.PlayerProfile paperProfile) {
                        for (com.destroystokyo.paper.profile.ProfileProperty prop : paperProfile.getProperties()) {
                            if (prop != null && "textures".equalsIgnoreCase(prop.getName())
                                    && prop.getValue() != null && !prop.getValue().isBlank()) {
                                return new SkinTexture(prop.getValue(), prop.getSignature());
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        // 2. Mojang Session Server HTTP fallback
        if (uuid != null) {
            String compact = uuid.toString().replace("-", "");
            SkinTexture sessionTexture = fetchMojangSessionTexture("https://sessionserver.mojang.com/session/minecraft/profile/" + compact + "?unsigned=false");
            if (sessionTexture != null && sessionTexture.isValid()) {
                return sessionTexture;
            }
        }

        if (name != null && !name.isBlank()) {
            HttpURLConnection conn = null;
            try {
                URI uri = URI.create("https://api.mojang.com/users/profiles/minecraft/" + name);
                conn = (HttpURLConnection) uri.toURL().openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                if (conn.getResponseCode() == 200) {
                    try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                        JsonObject obj = JsonParser.parseReader(isr).getAsJsonObject();
                        if (obj.has("id")) {
                            String compact = obj.get("id").getAsString();
                            return fetchMojangSessionTexture("https://sessionserver.mojang.com/session/minecraft/profile/" + compact + "?unsigned=false");
                        }
                    }
                }
            } catch (Throwable ignored) {
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        return null;
    }

    private SkinTexture fetchMojangSessionTexture(String urlString) {
        HttpURLConnection conn = null;
        try {
            URI uri = URI.create(urlString);
            conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            if (conn.getResponseCode() == 200) {
                try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject root = JsonParser.parseReader(isr).getAsJsonObject();
                    JsonArray properties = root.has("properties") ? root.getAsJsonArray("properties") : null;
                    if (properties != null) {
                        for (JsonElement element : properties) {
                            if (element.isJsonObject()) {
                                JsonObject prop = element.getAsJsonObject();
                                String propName = prop.has("name") ? prop.get("name").getAsString() : null;
                                if ("textures".equalsIgnoreCase(propName) && prop.has("value")) {
                                    String value = prop.get("value").getAsString();
                                    String sig = prop.has("signature") && !prop.get("signature").isJsonNull()
                                            ? prop.get("signature").getAsString()
                                            : null;
                                    return new SkinTexture(value, sig);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public boolean hasCachedSkin(UUID uuid) {
        return uuid != null && skinCache.containsKey(uuid);
    }

    public SkinTexture getCachedSkin(UUID uuid) {
        return uuid == null ? null : skinCache.get(uuid);
    }
}
