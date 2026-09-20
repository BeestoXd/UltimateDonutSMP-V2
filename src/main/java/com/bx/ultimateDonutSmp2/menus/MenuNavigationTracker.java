package com.bx.ultimateDonutSmp2.menus;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.dialogs.DialogSupport;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks navigation history across {@link BaseMenu} GUIs per player, allowing the Escape key
 * to navigate back to previous/parent menus instead of closing the entire GUI.
 */
public class MenuNavigationTracker {

    private final UltimateDonutSmp2 plugin;
    private final Map<UUID, Deque<BaseMenu>> history = new ConcurrentHashMap<>();
    private final Set<UUID> transitioning = ConcurrentHashMap.newKeySet();
    private final Set<UUID> handlingClick = ConcurrentHashMap.newKeySet();
    private final Set<UUID> explicitClose = ConcurrentHashMap.newKeySet();

    private static final MethodHandle GET_REASON_HANDLE;

    static {
        MethodHandle handle = null;
        try {
            Method method = InventoryCloseEvent.class.getMethod("getReason");
            handle = MethodHandles.publicLookup().unreflect(method);
        } catch (Throwable ignored) {
            // Running on a server / Spigot version without getReason()
        }
        GET_REASON_HANDLE = handle;
    }

    public MenuNavigationTracker(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if ESC-back navigation is enabled in menus.yml.
     */
    public boolean isEnabled() {
        if (plugin == null || plugin.getConfigManager() == null || plugin.getConfigManager().getMenus() == null) {
            return true;
        }
        return plugin.getConfigManager().getMenus().getBoolean("GLOBAL.ESC-BACK-TO-MENU", true);
    }

    /**
     * Records opening a menu for the given player.
     * Updates the player's navigation history stack.
     */
    public void recordOpen(Player player, BaseMenu menu) {
        if (player == null || menu == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        Deque<BaseMenu> stack = history.computeIfAbsent(uuid, k -> new ArrayDeque<>());

        if (stack.isEmpty()) {
            stack.addLast(menu);
            return;
        }

        BaseMenu current = stack.peekLast();
        if (current.isSameScreen(menu)) {
            // Same screen (e.g. page turn, filter change, refresh in-place) -> replace top
            stack.removeLast();
            stack.addLast(menu);
            return;
        }

        // If the player navigated back to a menu that already exists in the stack
        // (e.g. clicked an in-menu back button), unwind the stack to that point.
        boolean foundInStack = false;
        for (BaseMenu existing : stack) {
            if (existing.isSameScreen(menu)) {
                foundInStack = true;
                break;
            }
        }

        if (foundInStack) {
            while (!stack.isEmpty() && !stack.peekLast().isSameScreen(menu)) {
                stack.removeLast();
            }
            if (!stack.isEmpty()) {
                stack.removeLast();
            }
            stack.addLast(menu);
        } else {
            stack.addLast(menu);
        }
    }

    /**
     * Pops the current menu from the history stack and returns the previous menu,
     * or null if no previous menu exists.
     */
    public BaseMenu popPrevious(Player player) {
        if (player == null) {
            return null;
        }
        UUID uuid = player.getUniqueId();
        Deque<BaseMenu> stack = history.get(uuid);
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        // Pop current menu
        stack.removeLast();

        while (!stack.isEmpty() && !stack.peekLast().isEscBackTarget()) {
            stack.removeLast();
        }

        if (stack.isEmpty()) {
            history.remove(uuid);
            return null;
        }
        return stack.peekLast();
    }

    /**
     * Clears navigation history and flags for a player.
     */
    public void clear(UUID uuid) {
        if (uuid == null) {
            return;
        }
        history.remove(uuid);
        transitioning.remove(uuid);
        handlingClick.remove(uuid);
        explicitClose.remove(uuid);
    }

    public void clear(Player player) {
        if (player != null) {
            clear(player.getUniqueId());
        }
    }

    public void setTransitioning(UUID uuid, boolean isTransitioning) {
        if (uuid == null) {
            return;
        }
        if (isTransitioning) {
            transitioning.add(uuid);
        } else {
            transitioning.remove(uuid);
        }
    }

    public boolean isTransitioning(UUID uuid) {
        return uuid != null && transitioning.contains(uuid);
    }

    public void setHandlingClick(UUID uuid, boolean isHandling) {
        if (uuid == null) {
            return;
        }
        if (isHandling) {
            handlingClick.add(uuid);
        } else {
            handlingClick.remove(uuid);
        }
    }

    public boolean isHandlingClick(UUID uuid) {
        return uuid != null && handlingClick.contains(uuid);
    }

    public void setExplicitClose(UUID uuid, boolean isExplicit) {
        if (uuid == null) {
            return;
        }
        if (isExplicit) {
            explicitClose.add(uuid);
        } else {
            explicitClose.remove(uuid);
        }
    }

    public boolean isExplicitClose(UUID uuid) {
        return uuid != null && explicitClose.contains(uuid);
    }

    /**
     * Inspects the InventoryCloseEvent to determine whether it represents a player-initiated
     * close (ESC / E key) as opposed to a plugin programmatic close or new window opening.
     */
    public boolean isPlayerInitiatedClose(InventoryCloseEvent event, UUID uuid) {
        if (isTransitioning(uuid)) {
            return false;
        }
        if (isHandlingClick(uuid)) {
            return false;
        }
        if (isExplicitClose(uuid)) {
            return false;
        }
        if (DialogSupport.isClosingInventoryForDialog(uuid)) {
            return false;
        }

        if (GET_REASON_HANDLE != null) {
            try {
                Object reasonObj = GET_REASON_HANDLE.invoke(event);
                if (reasonObj != null) {
                    String name = reasonObj.toString();
                    // On Paper: PLAYER means ESC / E key.
                    // OPEN_NEW, PLUGIN, TELEPORT, CANT_USE, UNLOADED, DEATH, DISCONNECT are not ESC.
                    return "PLAYER".equalsIgnoreCase(name);
                }
            } catch (Throwable ignored) {
            }
        }

        return true;
    }

    /**
     * Internal accessor for unit tests to inspect the stack depth for a player.
     */
    Deque<BaseMenu> getStack(UUID uuid) {
        return history.get(uuid);
    }
}
