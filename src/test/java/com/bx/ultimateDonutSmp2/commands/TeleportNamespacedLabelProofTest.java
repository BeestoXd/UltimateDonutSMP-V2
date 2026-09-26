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

import static org.junit.jupiter.api.Assertions.assertTrue;

class TeleportNamespacedLabelProofTest {

    @Test
    void namespacedTphereUsesTphereAliasBranch() throws Exception {
        List<String> messages = new ArrayList<>();
        Player player = createPlayer(messages);
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        TeleportCommand command = new TeleportCommand(plugin);
        Command cmd = new StubCommand("tphere");

        // Calling with no args triggers usage message for whichever branch matched.
        // /tphere usage is "Usage: /tphere <player>"
        // Fallthrough usage is "Usage: /... <player|here <player>|all|top|x y z [world]>"
        command.onCommand(player, cmd, "ultimatedonutsmp2:tphere", new String[0]);

        assertTrue(
                messages.stream().anyMatch(msg -> msg.contains("Usage: /tphere <player>")),
                "Expected /ultimatedonutsmp2:tphere with no args to invoke handleTeleportHereAlias and send tphere usage, but got: " + messages
        );
    }

    @Test
    void namespacedTpallUsesTpallAliasBranch() throws Exception {
        List<String> messages = new ArrayList<>();
        Player player = createPlayer(messages);
        UltimateDonutSmp2 plugin = allocate(UltimateDonutSmp2.class);
        TeleportCommand command = new TeleportCommand(plugin);
        Command cmd = new StubCommand("tpall");

        // Calling with args on tpall triggers usage message for tpall branch: "Usage: /tpall"
        // Fallthrough with 2 args triggers teleportHere or teleportToPosition or sendUsage
        command.onCommand(player, cmd, "ultimatedonutsmp2:tpall", new String[]{"extra", "arg"});

        assertTrue(
                messages.stream().anyMatch(msg -> msg.contains("Usage: /tpall")),
                "Expected /ultimatedonutsmp2:tpall with invalid args to invoke handleTeleportAllAlias and send tpall usage, but got: " + messages
        );
    }

    private static Player createPlayer(List<String> messages) {
        UUID uuid = UUID.randomUUID();
        return (Player) Proxy.newProxyInstance(
                TeleportNamespacedLabelProofTest.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "hasPermission" -> {
                            return true;
                        }
                        case "sendMessage" -> {
                            if (args != null && args.length > 0) {
                                if (args[0] instanceof Component component) {
                                    messages.add(PlainTextComponentSerializer.plainText().serialize(component));
                                } else {
                                    messages.add(String.valueOf(args[0]));
                                }
                            }
                            return null;
                        }
                        case "getUniqueId" -> {
                            return uuid;
                        }
                        case "getName" -> {
                            return "Tester";
                        }
                        default -> {
                            return null;
                        }
                    }
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
