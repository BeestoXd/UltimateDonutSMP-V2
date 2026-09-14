package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.utils.SpigotScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.entity.Player;
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
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RTPManagerTest {

    private Server originalServer;
    private Server mockServer;
    private List<World> mockWorlds;
    private List<Player> mockPlayers;
    private AtomicInteger scheduledTasks;
    private UltimateDonutSmp2 lastMockPlugin;

    @BeforeEach
    void setUp() throws Exception {
        originalServer = Bukkit.getServer();
        mockWorlds = new ArrayList<>();
        mockPlayers = new ArrayList<>();
        scheduledTasks = new AtomicInteger();

        Object mockScheduler = Proxy.newProxyInstance(
                BukkitScheduler.class.getClassLoader(),
                new Class<?>[]{BukkitScheduler.class},
                (proxy, method, args) -> {
                    if (method.getName().startsWith("runTask")) {
                        scheduledTasks.incrementAndGet();
                    }
                    return null;
                }
        );

        mockServer = (Server) Proxy.newProxyInstance(
                Server.class.getClassLoader(),
                new Class<?>[]{Server.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getWorlds")) {
                        return mockWorlds;
                    }
                    if (method.getName().equals("getWorld")) {
                        String name = (String) args[0];
                        for (World world : mockWorlds) {
                            if (world.getName().equalsIgnoreCase(name)) {
                                return world;
                            }
                        }
                        return null;
                    }
                    if (method.getName().equals("getWorldContainer")) {
                        return new java.io.File(".");
                    }
                    if (method.getName().equals("getLogger")) {
                        return java.util.logging.Logger.getLogger("RTPManagerTest");
                    }
                    if (method.getName().equals("getOnlinePlayers")) {
                        return mockPlayers;
                    }
                    if (method.getName().equals("getScheduler")) {
                        return mockScheduler;
                    }
                    if (method.getName().equals("getLogger")) {
                        return java.util.logging.Logger.getLogger("Minecraft");
                    }
                    return null;
                }
        );

        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, mockServer);
    }

    @AfterEach
    void tearDown() throws Exception {
        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, originalServer);
    }

    private World createMockWorld(String name, World.Environment environment) {
        World mockWorld = (World) Proxy.newProxyInstance(
                World.class.getClassLoader(),
                new Class<?>[]{World.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getName")) {
                        return name;
                    }
                    if (method.getName().equals("getEnvironment")) {
                        return environment;
                    }
                    return null;
                }
        );
        mockWorlds.add(mockWorld);
        return mockWorld;
    }

    private Player addOnlinePlayer(String name) {
        Player mockPlayer = (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getName")) {
                        return name;
                    }
                    if (method.getName().equals("getUniqueId")) {
                        return UUID.nameUUIDFromBytes(name.getBytes());
                    }
                    if (method.getName().equals("isOnline")) {
                        return true;
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
        mockPlayers.add(mockPlayer);
        return mockPlayer;
    }

    private UltimateDonutSmp2 createMockPlugin(YamlConfiguration rtpConfig) throws Exception {
        Constructor<Object> objectConstructor = Object.class.getConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor<?> newConstructor = reflectionFactory.newConstructorForSerialization(UltimateDonutSmp2.class, objectConstructor);
        UltimateDonutSmp2 plugin = (UltimateDonutSmp2) newConstructor.newInstance();

        ConfigManager configManager = new ConfigManager(plugin);
        Field rtpField = ConfigManager.class.getDeclaredField("rtp");
        rtpField.setAccessible(true);
        rtpField.set(configManager, rtpConfig);

        Field configField = ConfigManager.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(configManager, new YamlConfiguration());

        Field cmField = UltimateDonutSmp2.class.getDeclaredField("configManager");
        cmField.setAccessible(true);
        cmField.set(plugin, configManager);

        attachLogger(plugin);
        lastMockPlugin = plugin;
        return plugin;
    }

    /** Collects everything the plugin warns about from this point on. */
    private List<String> captureWarnings() {
        List<String> warnings = new ArrayList<>();
        lastMockPlugin.getLogger().addHandler(new java.util.logging.Handler() {
            @Override
            public void publish(java.util.logging.LogRecord record) {
                if (record.getLevel().intValue() >= java.util.logging.Level.WARNING.intValue()) {
                    warnings.add(record.getMessage());
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });
        return warnings;
    }

    /**
     * A plugin built without running its constructor has no logger, so anything that warns blows up
     * with a NullPointerException instead of reaching the assertion the test is actually about.
     */
    private void attachLogger(UltimateDonutSmp2 plugin) throws Exception {
        Class<?> javaPlugin = org.bukkit.plugin.java.JavaPlugin.class;

        Field serverField = javaPlugin.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(plugin, mockServer);

        Field descriptionField = javaPlugin.getDeclaredField("description");
        descriptionField.setAccessible(true);
        descriptionField.set(plugin, new org.bukkit.plugin.PluginDescriptionFile(
                "UltimateDonutSmp2", "test", UltimateDonutSmp2.class.getName()));

        Field loggerField = javaPlugin.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(plugin, new org.bukkit.plugin.PluginLogger(plugin));
    }

    @Test
    void testGetLoadedNormalWorldNameExcludesSpawnHubDenied() throws Exception {
        createMockWorld("afk", World.Environment.NORMAL);
        createMockWorld("spawn", World.Environment.NORMAL);
        createMockWorld("hub", World.Environment.NORMAL);
        createMockWorld("lobby", World.Environment.NORMAL);
        createMockWorld("survival", World.Environment.NORMAL);

        YamlConfiguration rtpConfig = new YamlConfiguration();
        rtpConfig.set("DENIED-WORLDS", List.of("lobby", "afk"));
        UltimateDonutSmp2 plugin = createMockPlugin(rtpConfig);

        RTPManager rtpManager = new RTPManager(plugin);

        Method getLoadedNormalWorldName = RTPManager.class.getDeclaredMethod("getLoadedNormalWorldName");
        getLoadedNormalWorldName.setAccessible(true);

        String normalWorld = (String) getLoadedNormalWorldName.invoke(rtpManager);
        assertEquals("survival", normalWorld);
    }

    @SuppressWarnings("unchecked")
    private void seedLocationCache(RTPManager rtpManager, String worldName, Location location, long cachedAtMillis)
            throws Exception {
        Class<?> cachedLocationClass = Class.forName("com.bx.ultimateDonutSmp2.managers.RTPManager$CachedLocation");
        Constructor<?> cachedLocationConstructor = cachedLocationClass.getDeclaredConstructor(Location.class, long.class);
        cachedLocationConstructor.setAccessible(true);
        Object entry = cachedLocationConstructor.newInstance(location, cachedAtMillis);

        Field cacheField = RTPManager.class.getDeclaredField("locationPreCache");
        cacheField.setAccessible(true);
        Map<String, Queue<Object>> cache = (Map<String, Queue<Object>>) cacheField.get(rtpManager);
        Queue<Object> queue = new ConcurrentLinkedQueue<>();
        queue.add(entry);
        cache.put(worldName.toLowerCase(java.util.Locale.ROOT), queue);
    }

    private Location pollCachedLocation(RTPManager rtpManager, String worldName) throws Exception {
        Method pollPreCachedLocation = RTPManager.class.getDeclaredMethod("pollPreCachedLocation", String.class);
        pollPreCachedLocation.setAccessible(true);
        return (Location) pollPreCachedLocation.invoke(rtpManager, worldName);
    }

    private int preloadRadius(RTPManager rtpManager) throws Exception {
        Method method = RTPManager.class.getDeclaredMethod("getPreloadRadius");
        method.setAccessible(true);
        return (int) method.invoke(rtpManager);
    }

    private int preloadChunksPerTick(RTPManager rtpManager) throws Exception {
        Method method = RTPManager.class.getDeclaredMethod("getPreloadChunksPerTick");
        method.setAccessible(true);
        return (int) method.invoke(rtpManager);
    }
    private YamlConfiguration overworldRtpConfig() {
        YamlConfiguration rtpConfig = new YamlConfiguration();
        rtpConfig.set("WORLD-SETTINGS.world.MIN-RADIUS", 500);
        rtpConfig.set("WORLD-SETTINGS.world.MAX-RADIUS", 5000);
        return rtpConfig;
    }

    @Test
    void testSearchAttemptsPerTickReadsConfiguredValue() throws Exception {
        YamlConfiguration rtpConfig = new YamlConfiguration();
        RTPManager rtpManager = new RTPManager(createMockPlugin(rtpConfig));
        assertEquals(1, rtpManager.getSearchAttemptsPerTick());

        rtpConfig.set("SETTINGS.SEARCH-ATTEMPTS-PER-TICK", 8);
        assertEquals(8, rtpManager.getSearchAttemptsPerTick());

        rtpConfig.set("SETTINGS.SEARCH-ATTEMPTS-PER-TICK", 0);
        assertEquals(1, rtpManager.getSearchAttemptsPerTick());
    }

    @Test
    void testSearchDisplayTicksReadConfiguredValue() throws Exception {
        YamlConfiguration rtpConfig = new YamlConfiguration();
        RTPManager rtpManager = new RTPManager(createMockPlugin(rtpConfig));
        assertEquals(0L, rtpManager.getMinSearchDisplayTicks());

        rtpConfig.set("SETTINGS.SEARCH-DISPLAY-MIN-TICKS", 60);
        assertEquals(60L, rtpManager.getMinSearchDisplayTicks());

        // 0 is the point of the setting rather than an edge case: it is what a server sets to
        // have the teleport happen the moment a spot is found.
        rtpConfig.set("SETTINGS.SEARCH-DISPLAY-MIN-TICKS", 0);
        assertEquals(0L, rtpManager.getMinSearchDisplayTicks());

        rtpConfig.set("SETTINGS.SEARCH-DISPLAY-MIN-TICKS", -20);
        assertEquals(0L, rtpManager.getMinSearchDisplayTicks());
    }

    @Test
    void testFoundDisplayTicksReadConfiguredValue() throws Exception {
        YamlConfiguration rtpConfig = new YamlConfiguration();
        RTPManager rtpManager = new RTPManager(createMockPlugin(rtpConfig));
        assertEquals(0L, rtpManager.getFoundDisplayTicks());

        rtpConfig.set("SETTINGS.FOUND-DISPLAY-TICKS", 45);
        assertEquals(45L, rtpManager.getFoundDisplayTicks());

        rtpConfig.set("SETTINGS.FOUND-DISPLAY-TICKS", 0);
        assertEquals(0L, rtpManager.getFoundDisplayTicks());

        rtpConfig.set("SETTINGS.FOUND-DISPLAY-TICKS", -20);
        assertEquals(0L, rtpManager.getFoundDisplayTicks());
    }

    @Test
    void testPreloadDefaultsMatchTheFloorsTheyPassThrough() throws Exception {
        YamlConfiguration rtpConfig = new YamlConfiguration();
        RTPManager rtpManager = new RTPManager(createMockPlugin(rtpConfig));

        // Both settings floor at 2, so a default below that could never be the value in use.
        // These two assertions are what keeps the number in rtp.yml honest.
        assertEquals(2, preloadChunksPerTick(rtpManager));
        rtpConfig.set("SETTINGS.PRELOAD-CHUNKS-PER-TICK", 1);
        assertEquals(2, preloadChunksPerTick(rtpManager));
        rtpConfig.set("SETTINGS.PRELOAD-CHUNKS-PER-TICK", 6);
        assertEquals(6, preloadChunksPerTick(rtpManager));

        // With the throttle off the radius is the admin's, floored at 2 and capped at 4.
        rtpConfig.set("SETTINGS.POST-TELEPORT-CHUNK-THROTTLE", false);
        assertEquals(2, preloadRadius(rtpManager));
        rtpConfig.set("SETTINGS.PRELOAD-RADIUS", 1);
        assertEquals(2, preloadRadius(rtpManager));
        rtpConfig.set("SETTINGS.PRELOAD-RADIUS", 3);
        assertEquals(3, preloadRadius(rtpManager));
        rtpConfig.set("SETTINGS.PRELOAD-RADIUS", 9);
        assertEquals(4, preloadRadius(rtpManager));

        // Back on, the throttled view distance raises the radius, which is why the config
        // comment calls PRELOAD-RADIUS a floor rather than a cap.
        rtpConfig.set("SETTINGS.POST-TELEPORT-CHUNK-THROTTLE", true);
        rtpConfig.set("SETTINGS.PRELOAD-RADIUS", 2);
        assertEquals(4, preloadRadius(rtpManager));
        rtpConfig.set("SETTINGS.POST-TELEPORT-VIEW-DISTANCE", 2);
        assertEquals(2, preloadRadius(rtpManager));
    }

    @Test
    void testPreCacheSizeIsClampedAndRespectsToggle() throws Exception {
        YamlConfiguration rtpConfig = new YamlConfiguration();
        RTPManager rtpManager = new RTPManager(createMockPlugin(rtpConfig));
        assertEquals(3, rtpManager.getPreCacheSize());

        rtpConfig.set("SETTINGS.LOCATION-CACHE.SIZE", 64);
        assertEquals(16, rtpManager.getPreCacheSize());

        rtpConfig.set("SETTINGS.LOCATION-CACHE.SIZE", -4);
        assertEquals(0, rtpManager.getPreCacheSize());

        rtpConfig.set("SETTINGS.LOCATION-CACHE.SIZE", 5);
        rtpConfig.set("SETTINGS.LOCATION-CACHE.ENABLED", false);
        assertEquals(0, rtpManager.getPreCacheSize());
    }

    @Test
    void testPollPreCachedLocationReturnsUsableEntry() throws Exception {
        World world = createMockWorld("world", World.Environment.NORMAL);
        RTPManager rtpManager = new RTPManager(createMockPlugin(overworldRtpConfig()));

        Location cached = new Location(world, 1000.5, 70.0, 1000.5);
        seedLocationCache(rtpManager, "world", cached, System.currentTimeMillis());

        Location polled = pollCachedLocation(rtpManager, "world");
        assertEquals(cached, polled);
        assertSame(world, polled.getWorld());
        assertNull(pollCachedLocation(rtpManager, "world"));
    }

    @Test
    void testPollPreCachedLocationDropsEntryOutsideRadius() throws Exception {
        World world = createMockWorld("world", World.Environment.NORMAL);
        RTPManager rtpManager = new RTPManager(createMockPlugin(overworldRtpConfig()));

        seedLocationCache(rtpManager, "world", new Location(world, 100.5, 70.0, 100.5), System.currentTimeMillis());

        assertNull(pollCachedLocation(rtpManager, "world"));
    }

    @Test
    void testPollPreCachedLocationDropsExpiredEntry() throws Exception {
        World world = createMockWorld("world", World.Environment.NORMAL);
        YamlConfiguration rtpConfig = overworldRtpConfig();
        rtpConfig.set("SETTINGS.LOCATION-CACHE.MAX-AGE-SECONDS", 60);
        RTPManager rtpManager = new RTPManager(createMockPlugin(rtpConfig));

        Location cached = new Location(world, 1000.5, 70.0, 1000.5);
        seedLocationCache(rtpManager, "world", cached, System.currentTimeMillis() - 120_000L);

        assertNull(pollCachedLocation(rtpManager, "world"));
    }

    @Test
    void testPollPreCachedLocationIgnoresCacheWhenDisabled() throws Exception {
        World world = createMockWorld("world", World.Environment.NORMAL);
        YamlConfiguration rtpConfig = overworldRtpConfig();
        rtpConfig.set("SETTINGS.LOCATION-CACHE.ENABLED", false);
        RTPManager rtpManager = new RTPManager(createMockPlugin(rtpConfig));

        seedLocationCache(rtpManager, "world", new Location(world, 1000.5, 70.0, 1000.5), System.currentTimeMillis());

        assertNull(pollCachedLocation(rtpManager, "world"));
    }

    private Chunk createMockChunk(World world, int x, int z) {
        return (Chunk) Proxy.newProxyInstance(
                Chunk.class.getClassLoader(),
                new Class<?>[]{Chunk.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getX" -> x;
                    case "getZ" -> z;
                    case "getWorld" -> world;
                    default -> null;
                }
        );
    }

    /**
     * A world that reports the given loaded chunks and records every column the search probes, so a
     * test can prove the probe never reads outside the chunk it was handed. The recorded height sits
     * below the minimum so the probe stops before it reaches any block.
     */
    private World createProbeMockWorld(String name, List<int[]> loadedChunkCoords, List<int[]> probes) {
        List<Chunk> chunks = new ArrayList<>();
        World mockWorld = (World) Proxy.newProxyInstance(
                World.class.getClassLoader(),
                new Class<?>[]{World.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getName":
                            return name;
                        case "getEnvironment":
                            return World.Environment.NORMAL;
                        case "getLoadedChunks":
                            return chunks.toArray(new Chunk[0]);
                        case "getMinHeight":
                            return 0;
                        case "getHighestBlockYAt":
                            if (args != null && args.length == 2 && args[0] instanceof Integer x
                                    && args[1] instanceof Integer z) {
                                probes.add(new int[]{x, z});
                            }
                            return -1;
                        default:
                            return null;
                    }
                }
        );
        for (int[] coords : loadedChunkCoords) {
            chunks.add(createMockChunk(mockWorld, coords[0], coords[1]));
        }
        mockWorlds.add(mockWorld);
        return mockWorld;
    }

    @Test
    void testNextLoadedChunkSampleReturnsNullWhenNothingIsLoaded() throws Exception {
        World world = createProbeMockWorld("world", List.of(), new ArrayList<>());
        RTPManager rtpManager = new RTPManager(createMockPlugin(overworldRtpConfig()));

        assertNull(rtpManager.nextLoadedChunkSample(world));
    }

    @Test
    void testNextLoadedChunkSampleReportsTheChunkCoordinates() throws Exception {
        World world = createProbeMockWorld("world", List.of(new int[]{666, 339}), new ArrayList<>());
        RTPManager rtpManager = new RTPManager(createMockPlugin(overworldRtpConfig()));

        int[] sample = rtpManager.nextLoadedChunkSample(world);

        assertNotNull(sample);
        assertEquals(666, sample[0]);
        assertEquals(339, sample[1]);
    }

    @Test
    void testLoadedChunkProbeNeverLeavesTheChunkItWasGiven() throws Exception {
        List<int[]> probes = new ArrayList<>();
        // A second loaded chunk sitting in a different region. The probe must never touch it.
        createProbeMockWorld("world", List.of(new int[]{666, 339}, new int[]{10, 10}), probes);
        RTPManager rtpManager = new RTPManager(createMockPlugin(overworldRtpConfig()));

        Method probe = RTPManager.class.getDeclaredMethod(
                "tryLoadedChunkLocationAttempt", RTPManager.SearchSettings.class, int.class, int.class);
        probe.setAccessible(true);
        RTPManager.SearchSettings settings = rtpManager.getWorldSearchSettings("world");

        for (int run = 0; run < 200; run++) {
            probe.invoke(rtpManager, settings, 666, 339);
        }

        assertEquals(200, probes.size());
        for (int[] probed : probes) {
            assertTrue(probed[0] >= 666 * 16 && probed[0] < 666 * 16 + 16,
                    "probe read x " + probed[0] + ", which is outside chunk 666");
            assertTrue(probed[1] >= 339 * 16 && probed[1] < 339 * 16 + 16,
                    "probe read z " + probed[1] + ", which is outside chunk 339");
        }
    }

    /** Gives the mock plugin the two collaborators the background warm-up needs to run. */
    private void attachSchedulerAndFeatures(UltimateDonutSmp2 plugin) throws Exception {
        Field featureField = UltimateDonutSmp2.class.getDeclaredField("featureManager");
        featureField.setAccessible(true);
        featureField.set(plugin, new FeatureManager(plugin));

        Field schedulerField = UltimateDonutSmp2.class.getDeclaredField("SpigotScheduler");
        schedulerField.setAccessible(true);
        schedulerField.set(plugin, new SpigotScheduler(plugin));
    }

    private YamlConfiguration preCacheRtpConfig() {
        YamlConfiguration rtpConfig = overworldRtpConfig();
        rtpConfig.set("SETTINGS.SEARCH-ATTEMPTS-PER-TICK", 25);
        rtpConfig.set("SETTINGS.LOCATION-CACHE.ENABLED", true);
        rtpConfig.set("SETTINGS.LOCATION-CACHE.BACKGROUND-WARM-UP", true);
        rtpConfig.set("SETTINGS.LOCATION-CACHE.SIZE", 5);
        rtpConfig.set("WORLD-SETTINGS.world_nether.MIN-RADIUS", 500);
        rtpConfig.set("WORLD-SETTINGS.world_nether.MAX-RADIUS", 5000);
        rtpConfig.set("RTP-MENU.BUTTONS.OVERWORLD.SLOT", 11);
        rtpConfig.set("RTP-MENU.BUTTONS.OVERWORLD.WORLD", "world");
        rtpConfig.set("RTP-MENU.BUTTONS.OVERWORLD.ENABLED", true);
        rtpConfig.set("RTP-MENU.BUTTONS.NETHER.SLOT", 13);
        rtpConfig.set("RTP-MENU.BUTTONS.NETHER.WORLD", "world_nether");
        rtpConfig.set("RTP-MENU.BUTTONS.NETHER.ENABLED", true);
        return rtpConfig;
    }

    @SuppressWarnings("unchecked")
    private AtomicReference<CompletableFuture<Location>> preCacheSearch(RTPManager rtpManager) throws Exception {
        Field field = RTPManager.class.getDeclaredField("preCacheSearch");
        field.setAccessible(true);
        return (AtomicReference<CompletableFuture<Location>>) field.get(rtpManager);
    }

    private void setLongField(RTPManager rtpManager, String name, long value) throws Exception {
        Field field = RTPManager.class.getDeclaredField(name);
        field.setAccessible(true);
        field.setLong(rtpManager, value);
    }

    private RTPManager preCacheManager() throws Exception {
        createMockWorld("world", World.Environment.NORMAL);
        createMockWorld("world_nether", World.Environment.NETHER);
        UltimateDonutSmp2 plugin = createMockPlugin(preCacheRtpConfig());
        attachSchedulerAndFeatures(plugin);
        addOnlinePlayer("cache-warmup");
        return new RTPManager(plugin);
    }

    @Test
    void testPreCacheDoesNotWarmUpWhileTheServerIsEmpty() throws Exception {
        createMockWorld("world", World.Environment.NORMAL);
        createMockWorld("world_nether", World.Environment.NETHER);
        UltimateDonutSmp2 plugin = createMockPlugin(preCacheRtpConfig());
        attachSchedulerAndFeatures(plugin);
        RTPManager rtpManager = new RTPManager(plugin);

        assertEquals(0, Bukkit.getOnlinePlayers().size());
        assertNull(preCacheSearch(rtpManager).get(), "warm-up must not generate chunks while nobody is online");
        assertEquals(0, scheduledTasks.get());
    }

    @Test
    void testOnlyOneBackgroundSearchRunsAcrossTheWholeServer() throws Exception {
        RTPManager rtpManager = preCacheManager();

        CompletableFuture<Location> first = preCacheSearch(rtpManager).get();
        int afterFirst = scheduledTasks.get();

        setLongField(rtpManager, "nextPreCacheSearchAtMillis", 0L);
        rtpManager.refillPreCacheAllWorlds();
        rtpManager.refillPreCacheAllWorlds();

        assertSame(first, preCacheSearch(rtpManager).get(), "a second search must not start alongside the first");
        assertEquals(afterFirst, scheduledTasks.get(), "no extra search chains should have been scheduled");
        assertFalse(rtpManager.refillPreCache("world"));
    }

    @Test
    void testOverdueBackgroundSearchIsStoppedRatherThanForgotten() throws Exception {
        RTPManager rtpManager = preCacheManager();

        CompletableFuture<Location> overdue = preCacheSearch(rtpManager).get();
        assertNotNull(overdue);
        assertFalse(overdue.isDone());

        setLongField(rtpManager, "preCacheSearchDeadlineMillis", 0L);
        rtpManager.refillPreCacheAllWorlds();

        assertTrue(overdue.isDone(), "the deadline must complete the search so its chains wind down");
        assertNull(preCacheSearch(rtpManager).get());
    }

    @Test
    void testCooldownHoldsTheNextSearchBack() throws Exception {
        RTPManager rtpManager = preCacheManager();

        setLongField(rtpManager, "preCacheSearchDeadlineMillis", 0L);
        rtpManager.refillPreCacheAllWorlds();
        int afterExpiry = scheduledTasks.get();

        rtpManager.refillPreCacheAllWorlds();

        assertNull(preCacheSearch(rtpManager).get(), "the cooldown should keep the slot empty");
        assertEquals(afterExpiry, scheduledTasks.get());
    }

    @Test
    void testSearchCountNeverDisplaysMoreThanTheConfiguredLimit() {
        // The reporter on #151 saw "attempts 72/64" after several parallel checks landed past the cap.
        assertEquals(64, RTPManager.displaySearchCount(72, 64));
        assertEquals(64, RTPManager.displaySearchCount(64, 64));
        assertEquals(63, RTPManager.displaySearchCount(63, 64));
        assertEquals(0, RTPManager.displaySearchCount(0, 64));
    }

    @Test
    void testSearchCountLeavesUnlimitedAndNegativeAlone() {
        assertEquals(72, RTPManager.displaySearchCount(72, 0));
        assertEquals(72, RTPManager.displaySearchCount(72, -1));
        assertEquals(0, RTPManager.displaySearchCount(-5, 64));
        assertEquals(0, RTPManager.displaySearchCount(-5, 0));
    }

    @Test
    void nearbyLandingIsRejectedAgainstMinPlayerDistance() {
        // 261,71,1517 -> 87,68,1764 is about 302 blocks, which players read as "RTP dropped me next door".
        assertFalse(RTPManager.isFarEnoughFromOrigin(261, 1517, 87, 1764, 2000));
        assertFalse(RTPManager.isFarEnoughFromOrigin(261, 1517, 87, 1764, 500));
        assertTrue(RTPManager.isFarEnoughFromOrigin(261, 1517, 87, 1764, 200));
        assertTrue(RTPManager.isFarEnoughFromOrigin(0, 0, 2500, 0, 2000));
        assertTrue(RTPManager.isFarEnoughFromOrigin(0, 0, 0, 0, 0));
    }

    @Test
    void minPlayerDistanceUsesWorldOverrideAndIsCappedByMaxRadius() throws Exception {
        YamlConfiguration rtpConfig = overworldRtpConfig();
        rtpConfig.set("SETTINGS.MIN-PLAYER-DISTANCE", 2000);
        rtpConfig.set("WORLD-SETTINGS.world.MIN-PLAYER-DISTANCE", 1500);
        rtpConfig.set("WORLD-SETTINGS.world_nether.MIN-RADIUS", 50);
        rtpConfig.set("WORLD-SETTINGS.world_nether.MAX-RADIUS", 500);
        rtpConfig.set("WORLD-SETTINGS.world_nether.MIN-PLAYER-DISTANCE", 2000);
        RTPManager rtpManager = new RTPManager(createMockPlugin(rtpConfig));

        assertEquals(1500, rtpManager.getMinPlayerDistance(rtpManager.getWorldSearchSettings("world")));
        assertEquals(500, rtpManager.getMinPlayerDistance(rtpManager.getWorldSearchSettings("world_nether")));
    }

    @Test
    void testBackgroundSearchNeverGeneratesWhenItIsNotAllowedTo() {
        // The case from #151: GENERATE-CHUNKS on for players, warm-up still must not generate.
        assertFalse(RTPManager.shouldGenerateForSample(false, true, true));
        assertFalse(RTPManager.shouldGenerateForSample(false, true, false));
        assertFalse(RTPManager.shouldGenerateForSample(false, false, true));
    }

    @Test
    void testSearchAllowedToGenerateStillFollowsTheConfig() {
        assertTrue(RTPManager.shouldGenerateForSample(true, true, false));
        assertTrue(RTPManager.shouldGenerateForSample(true, false, true));
        assertFalse(RTPManager.shouldGenerateForSample(true, false, false));
    }

    @Test
    void testBackgroundWarmUpDoesNotLoadDiskChunksUnlessGenerationIsOn() {
        assertFalse(RTPManager.shouldLoadDiskChunksForBackgroundWarmUp(false));
        assertTrue(RTPManager.shouldLoadDiskChunksForBackgroundWarmUp(true));
    }

    @Test
    void testBackgroundWarmUpUsesASingleChainWhenItCannotGenerate() {
        assertEquals(1, RTPManager.preCacheParallelAttempts(false, 4));
        assertEquals(4, RTPManager.preCacheParallelAttempts(true, 4));
        assertEquals(1, RTPManager.preCacheParallelAttempts(true, 1));
    }

    @Test
    void testPreCacheChunkGenerationIsOffUnlessTurnedOn() throws Exception {
        createMockWorld("world", World.Environment.NORMAL);

        YamlConfiguration rtpConfig = overworldRtpConfig();
        RTPManager offByDefault = new RTPManager(createMockPlugin(rtpConfig));
        assertFalse(offByDefault.isPreCacheChunkGenerationEnabled(),
                "an admin who changes nothing should not get background generating");

        YamlConfiguration globalOnly = overworldRtpConfig();
        globalOnly.set("SETTINGS.GENERATE-CHUNKS", true);
        RTPManager globalDoesNotLeak = new RTPManager(createMockPlugin(globalOnly));
        assertFalse(globalDoesNotLeak.isPreCacheChunkGenerationEnabled(),
                "the player-facing setting must not switch the warm-up on");

        YamlConfiguration optedIn = overworldRtpConfig();
        optedIn.set("SETTINGS.LOCATION-CACHE.GENERATE-CHUNKS", true);
        RTPManager turnedOn = new RTPManager(createMockPlugin(optedIn));
        assertTrue(turnedOn.isPreCacheChunkGenerationEnabled());
    }

    @Test
    void testWarmUpStandsDownWhenNothingLetsItReachAChunk() throws Exception {
        // #175: with generating off and reading generated chunks off too, every sample returns
        // before it touches the world, so the warm-up burns 128 samples and warns, every few
        // seconds, forever.
        createMockWorld("world", World.Environment.NORMAL);
        createMockWorld("world_nether", World.Environment.NETHER);

        YamlConfiguration rtpConfig = preCacheRtpConfig();
        rtpConfig.set("SETTINGS.GENERATE-CHUNKS", false);
        rtpConfig.set("SETTINGS.GENERATE-FALLBACK-CHUNKS", false);
        rtpConfig.set("SETTINGS.LOAD-GENERATED-CHUNKS", false);
        rtpConfig.set("SETTINGS.LOCATION-CACHE.GENERATE-CHUNKS", false);

        UltimateDonutSmp2 plugin = createMockPlugin(rtpConfig);
        attachSchedulerAndFeatures(plugin);
        RTPManager rtpManager = new RTPManager(plugin);

        assertNull(preCacheSearch(rtpManager).get(),
                "a search with no way to reach a chunk should never be started");
        assertFalse(rtpManager.refillPreCache("world"));
        assertEquals(0, scheduledTasks.get(), "no search chains should have been scheduled at all");
    }

    @Test
    void testWarmUpBacksOffTheWorldThatCameBackEmpty() throws Exception {
        RTPManager rtpManager = preCacheManager();

        CompletableFuture<Location> first = preCacheSearch(rtpManager).get();
        assertNotNull(first);
        first.complete(null);

        setLongField(rtpManager, "nextPreCacheSearchAtMillis", 0L);

        assertFalse(rtpManager.refillPreCache("world"),
                "a world that just came back empty should not be searched again straight away");
        assertTrue(rtpManager.refillPreCache("world_nether"),
                "the backoff belongs to the world that failed, not to the whole sweep");
    }

    @Test
    void testAWarmUpThatFindsSomewhereKeepsItsNormalCadence() throws Exception {
        RTPManager rtpManager = preCacheManager();
        World world = Bukkit.getWorld("world");

        CompletableFuture<Location> first = preCacheSearch(rtpManager).get();
        assertNotNull(first);
        first.complete(new Location(world, 1000.5, 70.0, 1000.5));

        setLongField(rtpManager, "nextPreCacheSearchAtMillis", 0L);

        assertTrue(rtpManager.refillPreCache("world"),
                "a world that just produced a location should not be held back");
    }

    @Test
    void testAWorldWithNothingToOfferIsWarnedAboutOnceRatherThanEverySweep() throws Exception {
        // The console spam from #175: a doomed warm-up restarted every few seconds, warning each time.
        createMockWorld("world", World.Environment.NORMAL);

        YamlConfiguration rtpConfig = overworldRtpConfig();
        rtpConfig.set("SETTINGS.LOCATION-CACHE.ENABLED", true);
        rtpConfig.set("SETTINGS.LOCATION-CACHE.BACKGROUND-WARM-UP", true);
        rtpConfig.set("SETTINGS.LOCATION-CACHE.SIZE", 5);
        rtpConfig.set("RTP-MENU.BUTTONS.OVERWORLD.SLOT", 11);
        rtpConfig.set("RTP-MENU.BUTTONS.OVERWORLD.WORLD", "world");
        rtpConfig.set("RTP-MENU.BUTTONS.OVERWORLD.ENABLED", true);

        UltimateDonutSmp2 plugin = createMockPlugin(rtpConfig);
        attachSchedulerAndFeatures(plugin);
        addOnlinePlayer("cache-warmup");
        RTPManager rtpManager = new RTPManager(plugin);

        List<String> warnings = captureWarnings();
        preCacheSearch(rtpManager).get().complete(null);

        for (int sweep = 0; sweep < 5; sweep++) {
            setLongField(rtpManager, "nextPreCacheSearchAtMillis", 0L);
            rtpManager.refillPreCacheAllWorlds();
        }

        assertNull(preCacheSearch(rtpManager).get(), "the backed-off world should be left alone");
        assertEquals(1, warnings.size(), "one warning, not one per sweep: " + warnings);
    }

    @Test
    void testReloadingDoesNotWarnAboutTheWarmUpItInterrupted() throws Exception {
        RTPManager rtpManager = preCacheManager();
        assertNotNull(preCacheSearch(rtpManager).get());

        List<String> warnings = captureWarnings();
        rtpManager.reload();

        assertTrue(warnings.isEmpty(),
                "a reload cuts the search short, it does not prove the world had nothing: " + warnings);
    }

    @Test
    void testShippedConfigLeavesTheSearchAWayToReachAChunk() {
        YamlConfiguration shipped = YamlConfiguration.loadConfiguration(new File("src/main/resources/rtp.yml"));

        assertTrue(
                shipped.getBoolean("SETTINGS.LOAD-GENERATED-CHUNKS")
                        || shipped.getBoolean("SETTINGS.GENERATE-CHUNKS"),
                "the bundled rtp.yml must not forbid both loading and generating, or no search can"
                        + " prepare a single chunk"
        );
        assertFalse(
                shipped.getBoolean("SETTINGS.LOCATION-CACHE.BACKGROUND-WARM-UP"),
                "background warm-up must stay off so a join does not scan nether/end/overworld"
        );
        assertEquals(2000, shipped.getInt("SETTINGS.MIN-PLAYER-DISTANCE"));
        assertEquals(2000, shipped.getInt("WORLD-SETTINGS.world.MIN-PLAYER-DISTANCE"));
        assertEquals(32, shipped.getInt("SETTINGS.GENERATE-FALLBACK-AFTER-SAMPLES"),
                "generating too early is the wait after clicking RTP on a pregenerated world");
    }

    @Test
    void innerSampleRadiusMovesOffTheSpawnRingWhenThePlayerIsStillNearCentre() {
        // min 500 / max 5000 / min-player-distance 2000, player at spawn: the inner 500-2000
        // band can never be far enough, so those samples only delay the teleport.
        assertEquals(2000, RTPManager.innerSampleRadius(0, 0, 500, 5000, 261, 1517, 2000));
        assertEquals(2000, RTPManager.innerSampleRadius(0, 0, 500, 5000, 0, 0, 2000));
        assertEquals(500, RTPManager.innerSampleRadius(0, 0, 500, 5000, 4000, 0, 2000));
        assertEquals(500, RTPManager.innerSampleRadius(0, 0, 500, 5000, 0, 0, 0));
    }

    @Test
    void sameWorldOriginIgnoresAPlayerWithNoLocation() throws Exception {
        createMockWorld("world", World.Environment.NORMAL);
        RTPManager rtpManager = new RTPManager(createMockPlugin(overworldRtpConfig()));
        Player player = addOnlinePlayer("no-location");

        Method sameWorldOrigin = RTPManager.class.getDeclaredMethod("sameWorldOrigin", Player.class, String.class);
        sameWorldOrigin.setAccessible(true);

        assertNull(sameWorldOrigin.invoke(rtpManager, player, "world"),
                "a missing getLocation() must not throw; RTP just skips the min-distance origin");
    }

    @Test
    void testGetLoadedNormalWorldNameWithDeniedNormalWorld() throws Exception {
        createMockWorld("world", World.Environment.NORMAL); // denied world
        createMockWorld("smp", World.Environment.NORMAL); // normal world

        YamlConfiguration rtpConfig = new YamlConfiguration();
        rtpConfig.set("DENIED-WORLDS", List.of("world"));
        UltimateDonutSmp2 plugin = createMockPlugin(rtpConfig);

        RTPManager rtpManager = new RTPManager(plugin);

        Method getLoadedNormalWorldName = RTPManager.class.getDeclaredMethod("getLoadedNormalWorldName");
        getLoadedNormalWorldName.setAccessible(true);

        String normalWorld = (String) getLoadedNormalWorldName.invoke(rtpManager);
        assertEquals("smp", normalWorld);
    }

    private Player addOnlinePlayerInWorld(String name, World world) {
        Player mockPlayer = (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getName")) {
                        return name;
                    }
                    if (method.getName().equals("getUniqueId")) {
                        return UUID.nameUUIDFromBytes(name.getBytes());
                    }
                    if (method.getName().equals("isOnline")) {
                        return true;
                    }
                    if (method.getName().equals("getWorld")) {
                        return world;
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
        mockPlayers.add(mockPlayer);
        return mockPlayer;
    }

    @Test
    void testResolveWorldSelectorDimensionsAndLobbies() throws Exception {
        createMockWorld("world", World.Environment.NORMAL);
        createMockWorld("world_nether", World.Environment.NETHER);
        createMockWorld("world_the_end", World.Environment.THE_END);
        createMockWorld("lobby", World.Environment.NORMAL);
        createMockWorld("mining", World.Environment.NORMAL);
        createMockWorld("custom_survival", World.Environment.NORMAL);

        YamlConfiguration rtpConfig = new YamlConfiguration();
        rtpConfig.set("DENIED-WORLDS", List.of("afk"));
        rtpConfig.set("LOBBY-WORLDS", List.of("lobby", "hub", "spawn", "waiting_lobby"));
        rtpConfig.set("WORLD-SETTINGS.world.MIN-RADIUS", 500);
        rtpConfig.set("WORLD-SETTINGS.world.MAX-RADIUS", 5000);
        rtpConfig.set("WORLD-SETTINGS.world_nether.MIN-RADIUS", 50);
        rtpConfig.set("WORLD-SETTINGS.world_nether.MAX-RADIUS", 500);
        rtpConfig.set("WORLD-SETTINGS.world_the_end.MIN-RADIUS", 150);
        rtpConfig.set("WORLD-SETTINGS.world_the_end.MAX-RADIUS", 2000);
        rtpConfig.set("WORLD-SETTINGS.mining.MIN-RADIUS", 100);
        rtpConfig.set("WORLD-SETTINGS.mining.MAX-RADIUS", 1000);

        UltimateDonutSmp2 plugin = createMockPlugin(rtpConfig);
        RTPManager rtpManager = new RTPManager(plugin);

        // Overworld tests
        assertEquals("world", rtpManager.resolveWorldSelector("world"));
        assertEquals("world", rtpManager.resolveWorldSelector("overworld"));

        // Nether tests
        assertEquals("world_nether", rtpManager.resolveWorldSelector("world_nether"));
        assertEquals("world_nether", rtpManager.resolveWorldSelector("nether"));

        // End tests
        assertEquals("world_the_end", rtpManager.resolveWorldSelector("world_the_end"));
        assertEquals("world_the_end", rtpManager.resolveWorldSelector("end"));
        assertEquals("world_the_end", rtpManager.resolveWorldSelector("the_end"));

        // Lobby redirection tests (without explicit WORLD-SETTINGS) -> redirects to normal world
        assertEquals("world", rtpManager.resolveWorldSelector("lobby"));
        assertEquals("world", rtpManager.resolveWorldSelector("hub"));
        assertEquals("world", rtpManager.resolveWorldSelector("spawn"));
        assertEquals("world", rtpManager.resolveWorldSelector("waiting_lobby"));

        // Custom survival world WITH explicit WORLD-SETTINGS stays in custom world
        assertEquals("mining", rtpManager.resolveWorldSelector("mining"));

        // Custom survival world WITHOUT explicit settings (not a lobby) stays in custom world
        assertEquals("custom_survival", rtpManager.resolveWorldSelector("custom_survival"));
    }

    @Test
    void testQueueCommandTeleportBlockedWhenPlayerInDeniedWorld() throws Exception {
        World afkWorld = createMockWorld("afk", World.Environment.NORMAL);
        createMockWorld("world", World.Environment.NORMAL);

        YamlConfiguration rtpConfig = overworldRtpConfig();
        rtpConfig.set("DENIED-WORLDS", List.of("afk"));
        UltimateDonutSmp2 plugin = createMockPlugin(rtpConfig);
        attachSchedulerAndFeatures(plugin);

        Player afkPlayer = addOnlinePlayerInWorld("afk-player", afkWorld);
        RTPManager rtpManager = new RTPManager(plugin);

        assertFalse(rtpManager.queueCommandTeleport(afkPlayer, "world"),
                "player currently inside a denied world must not be allowed to perform RTP");
    }
}
