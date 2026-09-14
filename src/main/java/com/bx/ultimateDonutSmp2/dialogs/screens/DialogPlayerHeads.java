package com.bx.ultimateDonutSmp2.dialogs.screens;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.SkinsRestorerSkinLookup;
import com.bx.ultimateDonutSmp2.managers.TablistManager;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Inline player heads for dialog text.
 *
 * <p>Minecraft 1.21.9 treats a head with a name and UUID but no {@code properties} as static, then
 * paints the offline-mode default skin for that UUID. One of those defaults is Sunny — the yellow
 * face with two dark eyes. Dialogs never refresh after that, so the placeholder stays.
 *
 * <p>These components therefore send the texture bytes and nothing else: no player UUID, no
 * username. That keeps the profile static with a real skin instead of a default.
 */
final class DialogPlayerHeads {

    private static final Map<UUID, CacheEntry> TEXTURE_CACHE = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 60_000L;
    private static final long NEGATIVE_CACHE_TTL_MS = 10_000L;

    private record CacheEntry(TablistManager.SkinTexture texture, long timestamp, boolean isNegative) {
        boolean isExpired() {
            long ttl = isNegative ? NEGATIVE_CACHE_TTL_MS : CACHE_TTL_MS;
            return System.currentTimeMillis() - timestamp > ttl;
        }
    }

    private DialogPlayerHeads() {
    }

    static TablistManager.SkinTexture resolveSkinTexture(UUID uuid, String name) {
        if (Bukkit.getServer() == null || (uuid == null && (name == null || name.isBlank()))) {
            return null;
        }

        Player online = findOnlinePlayer(uuid, name);
        if (online != null) {
            uuid = online.getUniqueId();
            name = online.getName();
        } else if (uuid != null && (name == null || name.isBlank())) {
            name = offlineName(uuid);
        }

        if (online == null && uuid != null) {
            CacheEntry entry = TEXTURE_CACHE.get(uuid);
            if (entry != null && !entry.isExpired()) {
                return entry.texture();
            }
        }

        TablistManager.SkinTexture texture = resolveFreshSkinTexture(uuid, name, online);
        if (uuid != null) {
            TEXTURE_CACHE.put(uuid, new CacheEntry(
                    valid(texture) ? texture : null,
                    System.currentTimeMillis(),
                    !valid(texture)
            ));
        }
        return valid(texture) ? texture : null;
    }

    static Component component(UUID uuid, String name) {
        return componentFromTexture(resolveSkinTexture(uuid, name));
    }

    /**
     * Object sprites inherit the parent text colour and use it as a tint. Rank {@code #1} is
     * yellow, so a head appended onto that prefix paints Sunny-yellow over the skin. Keep the
     * head as a sibling of the coloured text instead of a child, and clear its own style.
     */
    static Component insertBetween(Component prefix, Component head, Component suffix) {
        return Component.empty()
                .append(prefix == null ? Component.empty() : prefix)
                .append(untinted(head))
                .append(suffix == null ? Component.empty() : suffix);
    }

    static Component untinted(Component head) {
        if (head == null || Component.empty().equals(head)) {
            return Component.empty();
        }
        return head.style(Style.empty());
    }

    static Component componentFromTexture(TablistManager.SkinTexture texture) {
        if (!valid(texture)) {
            return Component.empty();
        }

        Component adventure = adventurePropertyComponent(texture);
        if (adventure != null && !adventure.equals(Component.empty())) {
            return adventure;
        }
        Component paper = paperProfileComponent(texture);
        return paper != null ? paper : Component.empty();
    }

    private static Component paperProfileComponent(TablistManager.SkinTexture texture) {
        try {
            ProfileProperty property = texture.signature() != null && !texture.signature().isBlank()
                    ? new ProfileProperty("textures", texture.value(), texture.signature())
                    : new ProfileProperty("textures", texture.value());
            ResolvableProfile profile = ResolvableProfile.resolvableProfile()
                    .addProperty(property)
                    .build();
            return Component.object(ObjectContents.playerHead()
                    .hat(true)
                    .skin(profile)
                    .build());
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Component adventurePropertyComponent(TablistManager.SkinTexture texture) {
        try {
            PlayerHeadObjectContents.ProfileProperty property = texture.signature() != null
                    && !texture.signature().isBlank()
                    ? PlayerHeadObjectContents.property("textures", texture.value(), texture.signature())
                    : PlayerHeadObjectContents.property("textures", texture.value());
            PlayerHeadObjectContents contents = ObjectContents.playerHead()
                    .hat(true)
                    .profileProperties(List.of(property))
                    .build();
            if (contents.profileProperties().isEmpty()) {
                return Component.empty();
            }
            return Component.object(contents);
        } catch (Throwable ignored) {
            return Component.empty();
        }
    }

    private static TablistManager.SkinTexture resolveFreshSkinTexture(UUID uuid, String name, Player online) {
        UltimateDonutSmp2 plugin = UltimateDonutSmp2.getInstance();
        TablistManager tablist = plugin != null ? plugin.getTablistManager() : null;

        if (online != null) {
            TablistManager.SkinTexture worn = resolvePaperProfileTexture(online);
            if (valid(worn)) {
                return worn;
            }
            if (tablist != null) {
                TablistManager.SkinTexture current = tablist.resolveCurrentSkinTexture(online);
                if (valid(current)) {
                    return current;
                }
            }
        }

        if (tablist != null && uuid != null) {
            TablistManager.SkinTexture cachedSr = tablist.cachedSkinsRestorerTexture(uuid);
            if (valid(cachedSr)) {
                return cachedSr;
            }
        }

        if (skinsRestorerEnabled()) {
            try {
                TablistManager.SkinTexture srTexture = SkinsRestorerSkinLookup.resolve(uuid, name);
                if (valid(srTexture)) {
                    if (uuid != null && tablist != null) {
                        tablist.updateSkinTexture(uuid, srTexture.value(), srTexture.signature());
                    }
                    return srTexture;
                }
            } catch (Throwable ignored) {
            }
        }

        if (tablist != null && uuid != null) {
            TablistManager.SkinTexture cached = tablist.cachedSkinTexture(uuid);
            if (valid(cached)) {
                return cached;
            }
        }

        if (online != null && tablist != null) {
            TablistManager.SkinTexture live = tablist.resolveLiveGameProfileSkinTexture(online);
            if (valid(live)) {
                return live;
            }
        }

        if (tablist != null) {
            TablistManager.SkinTexture stored = tablist.resolveSkinTextureForFakePlayer(uuid, name);
            if (valid(stored)) {
                return stored;
            }
        }

        return null;
    }

    private static TablistManager.SkinTexture resolvePaperProfileTexture(Player player) {
        try {
            if (player.getPlayerProfile() instanceof com.destroystokyo.paper.profile.PlayerProfile paperProfile) {
                for (ProfileProperty property : paperProfile.getProperties()) {
                    if (property != null
                            && "textures".equalsIgnoreCase(property.getName())
                            && property.getValue() != null
                            && !property.getValue().isBlank()) {
                        return new TablistManager.SkinTexture(property.getValue(), property.getSignature());
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static Player findOnlinePlayer(UUID uuid, String name) {
        if (name != null && !name.isBlank()) {
            Player byName = Bukkit.getPlayerExact(name);
            if (byName != null && byName.isOnline()) {
                return byName;
            }
        }
        if (uuid != null) {
            Player byId = Bukkit.getPlayer(uuid);
            if (byId != null && byId.isOnline()) {
                return byId;
            }
        }
        return null;
    }

    private static String offlineName(UUID uuid) {
        try {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
            return offline.getName();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean skinsRestorerEnabled() {
        try {
            return Bukkit.getPluginManager() != null
                    && Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer");
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean valid(TablistManager.SkinTexture texture) {
        return texture != null && texture.isValid();
    }
}
