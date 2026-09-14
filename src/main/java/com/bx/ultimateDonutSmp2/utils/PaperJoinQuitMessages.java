package com.bx.ultimateDonutSmp2.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * Paper stores join and quit text as Adventure components. Calling
 * {@link PlayerQuitEvent#getQuitMessage()} converts that component through the legacy serializer
 * on the server thread, which is where the watchdog dump froze. These helpers read and clear the
 * Paper component when it exists, and only touch the String getters on plain Spigot.
 */
public final class PaperJoinQuitMessages {

    private static final MethodHandle JOIN_GET;
    private static final MethodHandle JOIN_SET;
    private static final MethodHandle QUIT_GET;
    private static final MethodHandle QUIT_SET;
    private static final MethodHandle PLAYER_SEND_COMPONENT;
    private static final Object EMPTY_COMPONENT;
    private static final boolean PAPER_COMPONENTS;

    static {
        MethodHandle joinGet = null;
        MethodHandle joinSet = null;
        MethodHandle quitGet = null;
        MethodHandle quitSet = null;
        MethodHandle playerSend = null;
        Object empty = null;
        boolean paper = false;
        try {
            Method joinGetter = PlayerJoinEvent.class.getMethod("joinMessage");
            Class<?> componentClass = joinGetter.getReturnType();
            if (componentClass != null && !"java.lang.String".equals(componentClass.getName())) {
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                joinGet = lookup.unreflect(joinGetter);
                joinSet = lookup.unreflect(PlayerJoinEvent.class.getMethod("joinMessage", componentClass));
                quitGet = lookup.unreflect(PlayerQuitEvent.class.getMethod("quitMessage"));
                quitSet = lookup.unreflect(PlayerQuitEvent.class.getMethod("quitMessage", componentClass));
                paper = true;
                try {
                    playerSend = unreflectSendMessage(lookup, componentClass);
                } catch (ReflectiveOperationException ignored) {
                    playerSend = null;
                }
                try {
                    empty = componentClass.getMethod("empty").invoke(null);
                } catch (ReflectiveOperationException ignored) {
                    empty = null;
                }
            }
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) {
            // Spigot's event types have no Adventure join/quit accessors.
        }
        JOIN_GET = joinGet;
        JOIN_SET = joinSet;
        QUIT_GET = quitGet;
        QUIT_SET = quitSet;
        PLAYER_SEND_COMPONENT = playerSend;
        EMPTY_COMPONENT = empty;
        PAPER_COMPONENTS = paper;
    }

    private PaperJoinQuitMessages() {
    }

    public static Object captureJoin(PlayerJoinEvent event) {
        if (PAPER_COMPONENTS) {
            return invoke(JOIN_GET, event);
        }
        return event.getJoinMessage();
    }

    public static void clearJoin(PlayerJoinEvent event) {
        if (PAPER_COMPONENTS) {
            invokeSet(JOIN_SET, event, null);
        }
        event.setJoinMessage(null);
    }

    public static Object captureQuit(PlayerQuitEvent event) {
        if (PAPER_COMPONENTS) {
            return invoke(QUIT_GET, event);
        }
        return event.getQuitMessage();
    }

    public static void clearQuit(PlayerQuitEvent event) {
        if (PAPER_COMPONENTS) {
            invokeSet(QUIT_SET, event, null);
        }
        event.setQuitMessage(null);
    }

    public static boolean hasText(Object message) {
        if (message == null) {
            return false;
        }
        if (message instanceof String text) {
            return !text.isEmpty();
        }
        return EMPTY_COMPONENT == null || !EMPTY_COMPONENT.equals(message);
    }

    public static void send(Player player, Object message) {
        if (player == null || !hasText(message)) {
            return;
        }
        if (message instanceof String text) {
            player.sendMessage(ColorUtils.toComponent(text));
            return;
        }
        if (PLAYER_SEND_COMPONENT != null) {
            try {
                PLAYER_SEND_COMPONENT.invoke(player, message);
                return;
            } catch (Throwable ignored) {
                // Fall through to a String send only if Paper's audience call failed.
            }
        }
    }

    static boolean usesPaperComponents() {
        return PAPER_COMPONENTS;
    }

    private static MethodHandle unreflectSendMessage(MethodHandles.Lookup lookup, Class<?> componentClass)
            throws IllegalAccessException, NoSuchMethodException {
        try {
            return lookup.unreflect(Player.class.getMethod("sendMessage", componentClass));
        } catch (NoSuchMethodException missing) {
            for (Method method : Player.class.getMethods()) {
                if ("sendMessage".equals(method.getName())
                        && method.getParameterCount() == 1
                        && method.getParameterTypes()[0] == componentClass) {
                    return lookup.unreflect(method);
                }
            }
            throw missing;
        }
    }

    private static Object invoke(MethodHandle handle, Object event) {
        try {
            return handle.invoke(event);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void invokeSet(MethodHandle handle, Object event, Object value) {
        try {
            handle.invokeWithArguments(event, value);
        } catch (Throwable ignored) {
            // The String setter below is the Spigot fallback; Paper failures leave the event alone.
        }
    }
}
