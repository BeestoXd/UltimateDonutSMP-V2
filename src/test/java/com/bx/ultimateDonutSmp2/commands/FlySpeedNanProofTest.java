package com.bx.ultimateDonutSmp2.commands;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.managers.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlySpeedNanProofTest {

    @Test
    void parseSpeedRejectsNonFiniteValues() {
        assertNull(FlySpeedCommand.parseSpeed("NaN"), "NaN must be rejected");
        assertNull(FlySpeedCommand.parseSpeed("Infinity"), "Infinity must be rejected");
        assertNull(FlySpeedCommand.parseSpeed("+Infinity"), "+Infinity must be rejected");
        assertNull(FlySpeedCommand.parseSpeed("-Infinity"), "-Infinity must be rejected");
        assertNull(FlySpeedCommand.parseSpeed("abc"), "non-numeric string must be rejected");
        assertEquals(5.0, FlySpeedCommand.parseSpeed("5"));
    }

    @Test
    void nanMustNotReachSetFlySpeed() throws Exception {
        RecordingPlayer player = runFlySpeed("NaN");
        assertFalse(player.flySpeedSet, "setFlySpeed must never be called for NaN");
        assertTrue(
                player.messages.stream().anyMatch(message -> message.toLowerCase().contains("invalid")),
                "NaN should hit FLYSPEED.INVALID instead of setFlySpeed"
        );
    }

    @Test
    void infinityIsRejectedAsInvalidSpeed() throws Exception {
        RecordingPlayer player = runFlySpeed("Infinity");
        assertFalse(player.flySpeedSet, "setFlySpeed must never be called for Infinity");
        assertTrue(
                player.messages.stream().anyMatch(message -> message.toLowerCase().contains("invalid")),
                "Infinity should hit FLYSPEED.INVALID"
        );
    }

    @Test
    void validSpeedSetsFlySpeed() throws Exception {
        RecordingPlayer player = runFlySpeed("5");
        assertTrue(player.flySpeedSet, "setFlySpeed must be called for valid speed");
        assertEquals(0.5f, player.flySpeed, 0.001f);
    }

    private RecordingPlayer runFlySpeed(String speedArg) throws Exception {
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        ConfigManager configManager = new ConfigManager(plugin);
        YamlConfiguration config = new YamlConfiguration();
        config.set("FLY-SYSTEM.MIN-SPEED", 1);
        config.set("FLY-SYSTEM.MAX-SPEED", 10);
        config.set("FLY-SYSTEM.PLAYER-FLY-PERMISSION", "ultimatedonutsmp2.player.fly");
        set(ConfigManager.class, configManager, "config", config);

        YamlConfiguration messages = new YamlConfiguration();
        messages.set("FLYSPEED.INVALID", "&cInvalid speed. Please enter a number from 1 to 10.");
        messages.set("FLYSPEED.SET", "&aFly speed set to &f{speed}&a.");
        set(ConfigManager.class, configManager, "messages", messages);

        set(UltimateDonutSmp2.class, plugin, "configManager", configManager);

        RecordingPlayer recorder = new RecordingPlayer();
        Player player = recorder.createProxy();
        Command command = new Command("flyspeed") {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };

        new FlySpeedCommand(plugin).onCommand(player, command, "flyspeed", new String[]{speedArg});
        return recorder;
    }

    private static class RecordingPlayer {
        final UUID uuid = UUID.randomUUID();
        final List<String> messages = new ArrayList<>();
        boolean flySpeedSet = false;
        float flySpeed = 0.0f;

        @SuppressWarnings("unchecked")
        Player createProxy() {
            return (Player) Proxy.newProxyInstance(
                    FlySpeedNanProofTest.class.getClassLoader(),
                    new Class<?>[]{Player.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "getUniqueId" -> uuid;
                        case "getName" -> "Tester";
                        case "hasPermission" -> true;
                        case "sendMessage" -> {
                            if (args.length > 0 && args[0] != null) {
                                messages.add(args[0].toString());
                            }
                            yield null;
                        }
                        case "setFlySpeed" -> {
                            flySpeedSet = true;
                            if (args.length > 0 && args[0] instanceof Float f) {
                                flySpeed = f;
                            }
                            yield null;
                        }
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> proxy == args[0];
                        default -> null;
                    }
            );
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T allocate(Class<T> type) throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        Constructor<?> constructor = ReflectionFactory.getReflectionFactory()
                .newConstructorForSerialization(type, objectConstructor);
        return (T) constructor.newInstance();
    }

    private static void set(Class<?> owner, Object instance, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(instance, value);
    }
}
