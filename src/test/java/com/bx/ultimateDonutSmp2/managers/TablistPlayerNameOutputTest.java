package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TablistPlayerNameOutputTest {

    private TablistManager createManager() throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

        Constructor<?> pluginConstructor = reflectionFactory
                .newConstructorForSerialization(UltimateDonutSmp2.class, objectConstructor);
        UltimateDonutSmp2 plugin = (UltimateDonutSmp2) pluginConstructor.newInstance();

        YamlConfiguration mainConfig = new YamlConfiguration();
        mainConfig.load(new File("src/main/resources/config.yml"));

        ConfigManager configManager = new ConfigManager(plugin);
        Field configField = ConfigManager.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(configManager, mainConfig);

        Field configManagerField = UltimateDonutSmp2.class.getDeclaredField("configManager");
        configManagerField.setAccessible(true);
        configManagerField.set(plugin, configManager);

        TeamManager teamManager = new TeamManager(plugin);

        Field teamManagerField = UltimateDonutSmp2.class.getDeclaredField("teamManager");
        teamManagerField.setAccessible(true);
        teamManagerField.set(plugin, teamManager);

        return new TablistManager(plugin);
    }

    private Player createMockPlayer(String name, Set<String> permissions) {
        return createMockPlayer(name, permissions, false);
    }

    private Player createMockPlayer(String name, Set<String> permissions, boolean isOp) {
        final Player[] playerHolder = new Player[1];
        Player playerProxy = (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getName")) {
                        return name;
                    }
                    if (method.getName().equals("getUniqueId")) {
                        return UUID.nameUUIDFromBytes(name.getBytes());
                    }
                    if (method.getName().equals("isOp")) {
                        return isOp;
                    }
                    if (method.getName().equals("hasPermission")) {
                        if (isOp) {
                            return true;
                        }
                        return permissions.contains(((String) args[0]).toLowerCase(Locale.ROOT));
                    }
                    if (method.getName().equals("getEffectivePermissions")) {
                        Set<PermissionAttachmentInfo> effective = new HashSet<>();
                        for (String permission : permissions) {
                            effective.add(new PermissionAttachmentInfo(playerHolder[0], permission, null, true));
                        }
                        return effective;
                    }
                    if (method.getName().equals("isOnline")) {
                        return true;
                    }
                    return null;
                }
        );
        playerHolder[0] = playerProxy;
        return playerProxy;
    }

    private String invokeResolveNameFormat(TablistManager manager, Player player) throws Exception {
        Method method = TablistManager.class.getDeclaredMethod("resolveNameFormat", Player.class);
        method.setAccessible(true);
        return (String) method.invoke(manager, player);
    }

    @Test
    void outputTrueFormatForDonutPlusPlusWithoutMedia() throws Exception {
        TablistManager manager = createManager();
        Player player = createMockPlayer("Tester", Set.of("ultimatedonutsmp2.donutplusplus"));

        String formatted = invokeResolveNameFormat(manager, player);

        // output (true): ++<nick>
        assertTrue(formatted.contains("++&fTester") || formatted.contains("++Tester"),
                "Expected ++ before nick, was: " + formatted);

        // output (false): <nick>++ must NOT happen
        assertFalse(formatted.contains("Tester++"),
                "Must NOT have ++ after nick: " + formatted);
    }

    @Test
    void outputTrueFormatForDonutPlusPlusWithMedia() throws Exception {
        TablistManager manager = createManager();
        Player player = createMockPlayer("Tester", Set.of(
                "ultimatedonutsmp2.donutplusplus",
                "media"
        ));

        String formatted = invokeResolveNameFormat(manager, player);

        // with media: 📹++<nick>
        int mediaIndex = formatted.indexOf("📹");
        int plusIndex = formatted.indexOf("++");
        int nickIndex = formatted.indexOf("Tester");

        assertTrue(mediaIndex >= 0, "Must contain media icon 📹");
        assertTrue(plusIndex >= 0, "Must contain ++ badge");
        assertTrue(nickIndex >= 0, "Must contain player nickname");

        assertTrue(mediaIndex < plusIndex, "Media 📹 must come before ++: " + formatted);
        assertTrue(plusIndex < nickIndex, "++ must come before nick: " + formatted);

        // output (false): 📹<nick>++ must NOT happen
        assertFalse(formatted.contains("Tester++"),
                "Must NOT have ++ after nick: " + formatted);
    }

    @Test
    void outputTrueFormatForDonutPlusWithoutMedia() throws Exception {
        TablistManager manager = createManager();
        Player player = createMockPlayer("Tester", Set.of("ultimatedonutsmp2.donutplus"));

        String formatted = invokeResolveNameFormat(manager, player);

        assertTrue(formatted.contains("+&fTester") || formatted.contains("+Tester"),
                "Expected + before nick, was: " + formatted);
        assertFalse(formatted.contains("Tester+"),
                "Must NOT have + after nick: " + formatted);
    }

    @Test
    void outputTrueFormatForDonutPlusWithMedia() throws Exception {
        TablistManager manager = createManager();
        Player player = createMockPlayer("Tester", Set.of(
                "ultimatedonutsmp2.donutplus",
                "rank.media"
        ));

        String formatted = invokeResolveNameFormat(manager, player);

        int mediaIndex = formatted.indexOf("📹");
        int plusIndex = formatted.indexOf("+");
        int nickIndex = formatted.indexOf("Tester");

        assertTrue(mediaIndex < plusIndex, "Media 📹 must come before +: " + formatted);
        assertTrue(plusIndex < nickIndex, "+ must come before nick: " + formatted);
        assertFalse(formatted.contains("Tester+"),
                "Must NOT have + after nick: " + formatted);
    }

    @Test
    void opPlayerWithoutExplicitDonutPlusPermissionDoesNotHaveBadge() throws Exception {
        TablistManager manager = createManager();
        Player player = createMockPlayer("BeestoXd", Set.of(), true);

        String formatted = invokeResolveNameFormat(manager, player);

        assertFalse(formatted.contains("+"), "OP player without LP permission must not have + badge: " + formatted);
        assertFalse(formatted.contains("📹"), "OP player without media permission must not have 📹 badge: " + formatted);
        assertEquals("&fBeestoXd", formatted);
    }

    @Test
    void playerWithWildcardPermissionDoesNotHaveDonutPlusBadge() throws Exception {
        TablistManager manager = createManager();
        Player player = createMockPlayer("BeestoXd", Set.of("*", "ultimatedonutsmp2.*"), false);

        String formatted = invokeResolveNameFormat(manager, player);

        assertFalse(formatted.contains("+"), "Wildcard permission must not grant + badge: " + formatted);
        assertFalse(formatted.contains("📹"), "Wildcard permission must not grant 📹 badge: " + formatted);
        assertEquals("&fBeestoXd", formatted);
    }

    @Test
    void playerWithExplicitDonutPlusPlusPlusHasBadge() throws Exception {
        TablistManager manager = createManager();
        Player player = createMockPlayer("BeestoXd", Set.of("ultimatedonutsmp2.donutplusplusplus"), true);

        String formatted = invokeResolveNameFormat(manager, player);

        assertTrue(formatted.contains("+++&fBeestoXd") || formatted.contains("+++BeestoXd"),
                "Expected +++ before nick, was: " + formatted);
        assertFalse(formatted.contains("BeestoXd+++"),
                "Must NOT have +++ after nick: " + formatted);
    }
}
