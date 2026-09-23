package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatTeleportBypassTest {

    private Server originalServer;
    private Server mockServer;
    private List<Player> mockPlayers;

    @BeforeEach
    void setUp() {
        originalServer = Bukkit.getServer();
        mockPlayers = new ArrayList<>();

        Object mockScheduler = Proxy.newProxyInstance(
                BukkitScheduler.class.getClassLoader(),
                new Class<?>[]{BukkitScheduler.class},
                (proxy, method, args) -> null
        );

        mockServer = (Server) Proxy.newProxyInstance(
                Server.class.getClassLoader(),
                new Class<?>[]{Server.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getOnlinePlayers")) {
                        return mockPlayers;
                    }
                    if (method.getName().equals("getPlayer")) {
                        if (args[0] instanceof UUID uuid) {
                            for (Player player : mockPlayers) {
                                if (player.getUniqueId().equals(uuid)) {
                                    return player;
                                }
                            }
                        } else if (args[0] instanceof String name) {
                            for (Player player : mockPlayers) {
                                if (player.getName().equalsIgnoreCase(name)) {
                                    return player;
                                }
                            }
                        }
                        return null;
                    }
                    if (method.getName().equals("getScheduler")) {
                        return mockScheduler;
                    }
                    if (method.getName().equals("getLogger")) {
                        return java.util.logging.Logger.getLogger("CombatTeleportBypassTest");
                    }
                    if (method.getName().equals("getWorldContainer")) {
                        return new File(".");
                    }
                    return null;
                }
        );

        setBukkitServer(mockServer);
    }

    @AfterEach
    void tearDown() {
        setBukkitServer(originalServer);
    }

    private static void setBukkitServer(Server server) {
        try {
            Field serverField = Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(null, server);
        } catch (Exception ignored) {
        }
    }

    private Player createMockPlayer(String name, List<String> receivedMessages) {
        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
        Location loc = new Location(null, 100, 64, 100);
        Player player = (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getName")) {
                        return name;
                    }
                    if (method.getName().equals("getUniqueId")) {
                        return uuid;
                    }
                    if (method.getName().equals("isOnline")) {
                        return true;
                    }
                    if (method.getName().equals("getLocation")) {
                        return loc;
                    }
                    if (method.getName().equals("sendMessage") || method.getName().equals("sendActionBar")) {
                        if (receivedMessages != null && args.length > 0 && args[0] != null) {
                            receivedMessages.add(args[0].toString());
                        }
                        return null;
                    }
                    Class<?> type = method.getReturnType();
                    if (type == boolean.class) {
                        return false;
                    }
                    if (type == int.class) {
                        return 0;
                    }
                    if (type == long.class) {
                        return 0L;
                    }
                    return null;
                }
        );
        mockPlayers.add(player);
        return player;
    }

    private UltimateDonutSmp2 createPluginWithCombat(boolean combatEnabled) throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor<?> newConstructor = reflectionFactory.newConstructorForSerialization(UltimateDonutSmp2.class, objectConstructor);
        UltimateDonutSmp2 plugin = (UltimateDonutSmp2) newConstructor.newInstance();

        YamlConfiguration config = new YamlConfiguration();
        config.set("COMBAT-MANAGER.ENABLED", combatEnabled);
        config.set("COMBAT-MANAGER.COOLDOWN", 16);
        config.set("COMBAT-MANAGER.BLOCK-MESSAGE", "&cyou can't use this command in your current status.");

        ConfigManager configManager = new ConfigManager(plugin);
        Field configField = ConfigManager.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(configManager, config);

        Field rtpField = ConfigManager.class.getDeclaredField("rtp");
        rtpField.setAccessible(true);
        rtpField.set(configManager, new YamlConfiguration());

        Field menusField = ConfigManager.class.getDeclaredField("menus");
        menusField.setAccessible(true);
        menusField.set(configManager, new YamlConfiguration());

        Field cmField = UltimateDonutSmp2.class.getDeclaredField("configManager");
        cmField.setAccessible(true);
        cmField.set(plugin, configManager);

        FeatureManager featureManager = new FeatureManager(plugin);
        Field fmField = UltimateDonutSmp2.class.getDeclaredField("featureManager");
        fmField.setAccessible(true);
        fmField.set(plugin, featureManager);

        CombatManager combatManager = new CombatManager(plugin);
        Field combatField = UltimateDonutSmp2.class.getDeclaredField("combatManager");
        combatField.setAccessible(true);
        combatField.set(plugin, combatManager);

        TeleportManager teleportManager = new TeleportManager(plugin);
        Field tpField = UltimateDonutSmp2.class.getDeclaredField("teleportManager");
        tpField.setAccessible(true);
        tpField.set(plugin, teleportManager);

        Field serverField = org.bukkit.plugin.java.JavaPlugin.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(plugin, mockServer);

        return plugin;
    }

    @Test
    void teleportManagerQueueRejectsWhenInCombat() throws Exception {
        UltimateDonutSmp2 plugin = createPluginWithCombat(true);
        List<String> messages = new ArrayList<>();
        Player player = createMockPlayer("Steve", messages);

        plugin.getCombatManager().tag(player);
        assertTrue(plugin.getCombatManager().isInCombat(player.getUniqueId()), "Player should be tagged in combat");

        AtomicBoolean onSuccessRan = new AtomicBoolean(false);
        plugin.getTeleportManager().queue(player, player.getLocation(), "RTP", p -> onSuccessRan.set(true));

        assertFalse(onSuccessRan.get(), "Teleport onSuccess should not run while player is in combat");
        assertFalse(plugin.getTeleportManager().hasPending(player.getUniqueId()), "Player should not have pending teleport in combat");
        assertFalse(messages.isEmpty(), "Player should receive a combat blocked message");
    }

    @Test
    void combatTagCancelsPendingTeleportWarmup() throws Exception {
        UltimateDonutSmp2 plugin = createPluginWithCombat(true);
        List<String> messages = new ArrayList<>();
        Player player = createMockPlayer("Alex", messages);

        // Put a pending task into TeleportManager manually
        Field pendingTasksField = TeleportManager.class.getDeclaredField("pendingTasks");
        pendingTasksField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<UUID, org.bukkit.scheduler.BukkitTask> map =
                (java.util.Map<UUID, org.bukkit.scheduler.BukkitTask>) pendingTasksField.get(plugin.getTeleportManager());

        AtomicBoolean taskCancelled = new AtomicBoolean(false);
        org.bukkit.scheduler.BukkitTask mockTask = (org.bukkit.scheduler.BukkitTask) Proxy.newProxyInstance(
                org.bukkit.scheduler.BukkitTask.class.getClassLoader(),
                new Class<?>[]{org.bukkit.scheduler.BukkitTask.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("cancel")) {
                        taskCancelled.set(true);
                    }
                    return null;
                }
        );
        map.put(player.getUniqueId(), mockTask);
        assertTrue(plugin.getTeleportManager().hasPending(player.getUniqueId()));

        // Tag player into combat
        plugin.getCombatManager().tag(player);

        assertTrue(taskCancelled.get(), "Pending teleport task must be cancelled on combat tag");
        assertFalse(plugin.getTeleportManager().hasPending(player.getUniqueId()), "Pending teleport must be cleared");
    }

    @Test
    void tpaAcceptIsBlockedWhenTargetOrRequesterIsInCombat() throws Exception {
        UltimateDonutSmp2 plugin = createPluginWithCombat(true);
        List<String> targetMessages = new ArrayList<>();
        List<String> requesterMessages = new ArrayList<>();
        Player target = createMockPlayer("TargetPlayer", targetMessages);
        Player requester = createMockPlayer("RequesterPlayer", requesterMessages);

        TPAManager tpaManager = new TPAManager(plugin);
        Field tpaField = UltimateDonutSmp2.class.getDeclaredField("tpaManager");
        tpaField.setAccessible(true);
        tpaField.set(plugin, tpaManager);

        // Target enters combat
        plugin.getCombatManager().tag(target);

        TPAManager.TpaRequest request = new TPAManager.TpaRequest(
                requester.getUniqueId(),
                target.getUniqueId(),
                false,
                false
        );

        Method acceptRequestMethod = TPAManager.class.getDeclaredMethod("acceptRequest", Player.class, TPAManager.TpaRequest.class);
        acceptRequestMethod.setAccessible(true);

        boolean acceptedWhileTargetInCombat = (boolean) acceptRequestMethod.invoke(tpaManager, target, request);
        assertFalse(acceptedWhileTargetInCombat, "TPA accept must return false when target is in combat");

        // Clear target combat, put requester into combat
        plugin.getCombatManager().clearTag(target.getUniqueId());
        plugin.getCombatManager().tag(requester);

        boolean acceptedWhileRequesterInCombat = (boolean) acceptRequestMethod.invoke(tpaManager, target, request);
        assertFalse(acceptedWhileRequesterInCombat, "TPA accept must return false when requester is in combat");
    }

    @Test
    void combatManagerBlockMessageMatchesConfig() throws Exception {
        UltimateDonutSmp2 plugin = createPluginWithCombat(true);
        assertEquals("&cyou can't use this command in your current status.", plugin.getCombatManager().getBlockMessage());
    }
}
