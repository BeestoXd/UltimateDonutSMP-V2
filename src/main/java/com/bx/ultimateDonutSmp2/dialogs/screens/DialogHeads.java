package com.bx.ultimateDonutSmp2.dialogs.screens;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * Builds the player head shown at the top of a dialog.
 *
 * <p>The client resolves the skin from the profile on the item, so setting the owning player is
 * enough; when that fails — an unknown uuid, a server with no profile cache — the caller gets
 * null and the dialog simply renders without a banner.
 */
final class DialogHeads {

    private DialogHeads() {
    }

    static ItemStack item(UltimateDonutSmp2 plugin, UUID uuid, String name) {
        if (uuid == null || Bukkit.getServer() == null) {
            return null;
        }
        try {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            if (!(head.getItemMeta() instanceof SkullMeta meta)) {
                return null;
            }
            OfflinePlayer owner = Bukkit.getOfflinePlayer(uuid);
            meta.setOwningPlayer(owner);
            if (name != null && !name.isBlank()) {
                meta.setDisplayName(name);
            }

            com.bx.ultimateDonutSmp2.managers.TablistManager.SkinTexture texture = DialogPlayerHeads.resolveSkinTexture(uuid, name);
            if (texture != null && texture.isValid()) {
                com.bx.ultimateDonutSmp2.utils.ItemUtils.applyTextureToSkullMeta(meta, texture.value());
            }

            head.setItemMeta(meta);
            return head;
        } catch (RuntimeException exception) {
            if (plugin != null) {
                plugin.getLogger().fine(() -> "Could not build a dialog head for " + uuid + ": " + exception);
            }
            return null;
        }
    }
}
