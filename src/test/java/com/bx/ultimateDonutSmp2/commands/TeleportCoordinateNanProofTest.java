package com.bx.ultimateDonutSmp2.commands;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeleportCoordinateNanProofTest {

    @Test
    void parseCoordinateRejectsNonFiniteValues() {
        assertNull(TeleportCommand.parseCoordinate("NaN"), "NaN must be rejected");
        assertNull(TeleportCommand.parseCoordinate("Infinity"), "Infinity must be rejected");
        assertNull(TeleportCommand.parseCoordinate("+Infinity"), "+Infinity must be rejected");
        assertNull(TeleportCommand.parseCoordinate("-Infinity"), "-Infinity must be rejected");
        assertNull(TeleportCommand.parseCoordinate("invalid"), "non-numeric string must be rejected");
        assertNull(TeleportCommand.parseCoordinate(null), "null string must be rejected");
        assertNull(TeleportCommand.parseCoordinate("   "), "blank string must be rejected");
        assertEquals(10.5, TeleportCommand.parseCoordinate("10.5"));
    }

    @Test
    void nanCoordinatesMustBeRejectedInCommand() throws Exception {
        List<String> messages = new ArrayList<>();
        Player player = createPlayer(messages);
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        TeleportCommand command = new TeleportCommand(plugin);
        Command cmd = new StubCommand("tp");

        try {
            command.onCommand(player, cmd, "tp", new String[]{"NaN", "100", "NaN"});
        } catch (NullPointerException ignored) {
            // Unhandled fallthrough when non-finite coordinates bypass validation
        }

        assertTrue(
                messages.stream().anyMatch(msg -> msg.contains("Coordinates must be valid numbers.")),
                "Expected rejection message for NaN coordinates, but got: " + messages
        );
    }

    @Test
    void infinityCoordinatesMustBeRejectedInCommand() throws Exception {
        List<String> messages = new ArrayList<>();
        Player player = createPlayer(messages);
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        TeleportCommand command = new TeleportCommand(plugin);
        Command cmd = new StubCommand("tp");

        try {
            command.onCommand(player, cmd, "tp", new String[]{"Infinity", "0", "0"});
        } catch (NullPointerException ignored) {
            // Unhandled fallthrough when non-finite coordinates bypass validation
        }

        assertTrue(
                messages.stream().anyMatch(msg -> msg.contains("Coordinates must be valid numbers.")),
                "Expected rejection message for Infinity coordinates, but got: " + messages
        );
    }

    private static Player createPlayer(List<String> messages) {
        UUID uuid = UUID.randomUUID();
        return (Player) Proxy.newProxyInstance(
                TeleportCoordinateNanProofTest.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "hasPermission" -> true;
                    case "sendMessage" -> {
                        if (args != null && args.length > 0) {
                            if (args[0] instanceof Component component) {
                                messages.add(PlainTextComponentSerializer.plainText().serialize(component));
                            } else {
                                messages.add(String.valueOf(args[0]));
                            }
                        }
                        yield null;
                    }
                    case "getUniqueId" -> uuid;
                    case "getName" -> "Tester";
                    default -> null;
                }
        );
    }

    private static final class StubCommand extends Command {
        private StubCommand(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T allocate(Class<T> type) throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        Constructor<?> constructor = ReflectionFactory.getReflectionFactory()
                .newConstructorForSerialization(type, objectConstructor);
        return (T) constructor.newInstance();
    }
}
