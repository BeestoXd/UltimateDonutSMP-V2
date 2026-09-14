package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import com.bx.ultimateDonutSmp2.utils.PlayerSettingUtils;
import com.bx.ultimateDonutSmp2.utils.RtpChunkPreloadPolicy;
import com.bx.ultimateDonutSmp2.utils.SoundUtils;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class TeleportManager {

    private final UltimateDonutSmp2 plugin;
    private final Map<UUID, BukkitTask> pendingTasks = new ConcurrentHashMap<>();
    private final Map<UUID, String> pendingTypes = new ConcurrentHashMap<>();
    private final Map<UUID, Location> startLocations = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerDistanceState> rtpDistanceStates = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> rtpDistanceRestoreTasks = new ConcurrentHashMap<>();

    private record PlayerDistanceState(
            Integer viewDistance,
            Integer simulationDistance,
            Integer noTickViewDistance,
            Integer sendViewDistance
    ) {
    }

    public TeleportManager(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
    }

    public void queue(Player player, Location destination, String type,
                      Consumer<Player> onSuccess) {
        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        if (plugin.getDuelManager() != null && (plugin.getDuelManager().isInDuel(uuid) || plugin.getDuelManager().isTransitioning(uuid))) {
            if (!plugin.getDuelManager().isInternalTeleport(uuid)) {
                player.sendMessage(ColorUtils.toComponent(plugin.getDuelManager().getCommandBlockedMessage()));
                return;
            }
        }

        String normalizedType = normalizeType(type);
        int cooldownSecs = warmupSeconds(plugin.getConfigManager().getConfig(), normalizedType);
        boolean quietSpawn = "SPAWN".equals(normalizedType)
                && PlayerSettingUtils.quietSpawnEnabled(plugin, player);
        cancel(player.getUniqueId());

        // RTP and HOME are instant. TPA / TPAHERE keep their 5s stand-still warmup.
        if (cooldownSecs <= 0 || "RTP".equals(normalizedType) || "HOME".equals(normalizedType)) {
            teleportNow(player, destination, normalizedType, onSuccess);
            return;
        }

        startLocations.put(player.getUniqueId(), player.getLocation().clone());

        if (!quietSpawn) {
            sendCountdownFeedback(player, cooldownSecs);
        }
        if (!"RTP".equals(normalizedType) && !"HOME".equals(normalizedType) && !quietSpawn) {
            sendMovementWarning(player, cooldownSecs);
        }

        final int[] remaining = {cooldownSecs};
        BukkitTask task = plugin.getSpigotScheduler().runEntityTimer(player, () -> {
            remaining[0]--;

            if (!player.isOnline()) {
                cancel(player.getUniqueId());
                return;
            }

            if (remaining[0] <= 0) {
                cancel(player.getUniqueId());
                PlayerSettingUtils.clearActionBar(player);
                teleportNow(player, destination, normalizedType, onSuccess);
            } else {
                if (!quietSpawn) {
                    sendCountdownFeedback(player, remaining[0]);
                }
            }
        }, 20L, 20L);

        if (task != null) {
            pendingTasks.put(player.getUniqueId(), task);
        }
        pendingTypes.put(player.getUniqueId(), normalizedType);
    }

    private void sendActionBar(Player player, int seconds) {
        String template = plugin.getConfigManager().getMessage("TELEPORT.COUNTDOWN",
                "{seconds}", String.valueOf(seconds));
        PlayerSettingUtils.sendActionBar(plugin, player, ColorUtils.toComponent(template));
    }

    private void sendCountdownFeedback(Player player, int seconds) {
        sendActionBar(player, seconds);
        SoundUtils.play(player, plugin.getConfigManager().getSound("TELEPORT.COUNTDOWN"));
    }

    private void sendMovementWarning(Player player, int seconds) {
        String warning = plugin.getConfigManager().getMessage(
                "TELEPORT.WARNING",
                "{seconds}", String.valueOf(seconds)
        );
        if (warning == null || warning.isBlank()) {
            return;
        }
        player.sendMessage(ColorUtils.toComponent(warning));
    }

    private void teleportNow(Player player, Location destination, String normalizedType, Consumer<Player> onSuccess) {
        boolean rtpThrottleActive = "RTP".equals(normalizedType) && applyRtpChunkThrottle(player);
        boolean quietSpawn = "SPAWN".equals(normalizedType)
                && PlayerSettingUtils.quietSpawnEnabled(plugin, player);
        plugin.getSpigotScheduler().teleport(player, destination).whenComplete((success, throwable) ->
                plugin.getSpigotScheduler().runEntity(player, () -> {
                    if (!player.isOnline()) {
                        if (rtpThrottleActive) {
                            restoreRtpChunkThrottle(player.getUniqueId());
                        }
                        return;
                    }
                    if (throwable != null || !Boolean.TRUE.equals(success)) {
                        if (rtpThrottleActive) {
                            restoreRtpChunkThrottle(player.getUniqueId());
                        }
                        String failed = plugin.getConfigManager().getMessageOrDefault(
                                "TELEPORT.FAILED",
                                "&cTeleport failed. Please try again."
                        );
                        player.sendMessage(ColorUtils.toComponent(failed));
                        return;
                    }

                    if (!quietSpawn) {
                        String msg = plugin.getConfigManager().getMessage("TELEPORT.SUCCESS");
                        player.sendMessage(ColorUtils.toComponent(msg));
                        SoundUtils.play(player, plugin.getConfigManager().getSound("TELEPORT.SUCCESS"));
                    }
                    if ("RTP".equals(normalizedType)) {
                        scheduleRtpChunkRefresh(player, destination);
                    }
                    if (rtpThrottleActive) {
                        scheduleRtpChunkThrottleRestore(player);
                    }
                    if (onSuccess != null) {
                        onSuccess.accept(player);
                    }
                    if ("RTP".equals(normalizedType) && plugin.getRtpManager() != null) {
                        plugin.getRtpManager().processNextInQueue();
                    }
                }));
    }

    public void cancel(UUID uuid) {
        BukkitTask task = pendingTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
        String pendingType = pendingTypes.remove(uuid);
        startLocations.remove(uuid);
        restoreRtpChunkThrottle(uuid);
        if ("RTP".equals(pendingType) && plugin.getRtpManager() != null) {
            plugin.getRtpManager().processNextInQueue();
        }
    }

    public boolean hasPending(UUID uuid) {
        return pendingTasks.containsKey(uuid);
    }

    public boolean hasPendingType(UUID uuid, String type) {
        String pendingType = pendingTypes.get(uuid);
        return pendingType != null && pendingType.equals(normalizeType(type));
    }

    public int countPendingByType(String type) {
        String normalizedType = normalizeType(type);
        int count = 0;
        for (String pendingType : pendingTypes.values()) {
            if (normalizedType.equals(pendingType)) {
                count++;
            }
        }
        return count;
    }

    public void checkMovement(Player player) {
        Location start = startLocations.get(player.getUniqueId());
        if (start == null) {
            return;
        }

        Location now = player.getLocation();
        if (movedEnoughToCancel(start.getX(), start.getZ(), now.getX(), now.getZ())) {
            cancel(player.getUniqueId());
            PlayerSettingUtils.clearActionBar(player);
            String msg = plugin.getConfigManager().getMessage("TELEPORT.CANCELED");
            player.sendMessage(ColorUtils.toComponent(msg));
            SoundUtils.play(player, plugin.getConfigManager().getSound("TELEPORT.CANCELLED"));
        }
    }

    /**
     * Seconds to wait before landing. RTP and HOME are always instant. TPA and TPAHERE read
     * {@code TELEPORT-COOLDOWN.TPA} / {@code TPAHERE}, defaulting to 5, and moving cancels them.
     */
    static int warmupSeconds(org.bukkit.configuration.ConfigurationSection config, String type) {
        String normalized = normalizeType(type);
        if ("RTP".equals(normalized) || "HOME".equals(normalized)) {
            return 0;
        }
        if (config == null) {
            return 5;
        }
        if ("TPAHERE".equals(normalized) && !config.contains("TELEPORT-COOLDOWN.TPAHERE")) {
            return Math.max(0, config.getInt("TELEPORT-COOLDOWN.TPA", 5));
        }
        return Math.max(0, config.getInt("TELEPORT-COOLDOWN." + normalized, 5));
    }

    static boolean movedEnoughToCancel(double fromX, double fromZ, double toX, double toZ) {
        return Math.abs(toX - fromX) > 0.5 || Math.abs(toZ - fromZ) > 0.5;
    }

    private static String normalizeType(String type) {
        return type == null ? "" : type.trim().toUpperCase(Locale.ROOT);
    }

    public void restoreAllRtpChunkThrottles() {
        for (UUID uuid : rtpDistanceStates.keySet().toArray(UUID[]::new)) {
            restoreRtpChunkThrottle(uuid);
        }
    }

    private boolean applyRtpChunkThrottle(Player player) {
        if (!plugin.getConfigManager().getRtp().getBoolean("SETTINGS.POST-TELEPORT-CHUNK-THROTTLE", true)) {
            return false;
        }

        UUID uuid = player.getUniqueId();
        BukkitTask restoreTask = rtpDistanceRestoreTasks.remove(uuid);
        if (restoreTask != null) {
            restoreTask.cancel();
        }

        int targetViewDistance = getRtpThrottleDistance("SETTINGS.POST-TELEPORT-VIEW-DISTANCE", 4);
        int targetSimulationDistance = getRtpThrottleDistance("SETTINGS.POST-TELEPORT-SIMULATION-DISTANCE", 4);
        Integer currentViewDistance = invokeDistanceGetter(player, "getViewDistance");
        Integer currentSimulationDistance = invokeDistanceGetter(player, "getSimulationDistance");
        Integer currentNoTickViewDistance = invokeDistanceGetter(player, "getNoTickViewDistance");
        Integer currentSendViewDistance = invokeDistanceGetter(player, "getSendViewDistance");
        PlayerDistanceState existingState = rtpDistanceStates.get(uuid);

        boolean shouldChangeView = currentViewDistance != null && currentViewDistance > targetViewDistance;
        boolean shouldChangeSimulation = currentSimulationDistance != null
                && currentSimulationDistance > targetSimulationDistance;
        boolean shouldChangeNoTick = currentNoTickViewDistance != null && currentNoTickViewDistance > targetViewDistance;
        boolean shouldChangeSend = currentSendViewDistance != null && currentSendViewDistance > targetViewDistance;
        if (existingState == null
                && !shouldChangeView
                && !shouldChangeSimulation
                && !shouldChangeNoTick
                && !shouldChangeSend) {
            return false;
        }

        rtpDistanceStates.putIfAbsent(uuid, new PlayerDistanceState(
                currentViewDistance,
                currentSimulationDistance,
                currentNoTickViewDistance,
                currentSendViewDistance
        ));
        if (shouldChangeSend) {
            invokeDistanceSetter(player, "setSendViewDistance", targetViewDistance);
        }
        if (shouldChangeNoTick) {
            invokeDistanceSetter(player, "setNoTickViewDistance", targetViewDistance);
        }
        if (shouldChangeView) {
            invokeDistanceSetter(player, "setViewDistance", targetViewDistance);
        }
        if (shouldChangeSimulation) {
            invokeDistanceSetter(player, "setSimulationDistance", targetSimulationDistance);
        }
        return true;
    }

    private void scheduleRtpChunkThrottleRestore(Player player) {
        UUID uuid = player.getUniqueId();
        int delayTicks = Math.max(20, plugin.getConfigManager().getRtp()
                .getInt("SETTINGS.POST-TELEPORT-THROTTLE-TICKS", 80));
        BukkitTask existingTask = rtpDistanceRestoreTasks.remove(uuid);
        if (existingTask != null) {
            existingTask.cancel();
        }
        BukkitTask task = plugin.getSpigotScheduler().runEntityLater(
                player,
                () -> restoreRtpChunkThrottleStep(uuid),
                delayTicks
        );
        if (task != null) {
            rtpDistanceRestoreTasks.put(uuid, task);
        } else {
            restoreRtpChunkThrottle(uuid);
        }
    }

    private void restoreRtpChunkThrottle(UUID uuid) {
        BukkitTask restoreTask = rtpDistanceRestoreTasks.remove(uuid);
        if (restoreTask != null) {
            restoreTask.cancel();
        }

        PlayerDistanceState state = rtpDistanceStates.remove(uuid);
        if (state == null) {
            return;
        }

        Player player = plugin.getServer().getPlayer(uuid);
        if (player == null || !player.isOnline()) {
            return;
        }
        if (state.viewDistance() != null) {
            invokeDistanceSetter(player, "setViewDistance", state.viewDistance());
        }
        if (state.simulationDistance() != null) {
            invokeDistanceSetter(player, "setSimulationDistance", state.simulationDistance());
        }
        if (state.noTickViewDistance() != null) {
            invokeDistanceSetter(player, "setNoTickViewDistance", state.noTickViewDistance());
        }
        if (state.sendViewDistance() != null) {
            invokeDistanceSetter(player, "setSendViewDistance", state.sendViewDistance());
        }
    }

    private void restoreRtpChunkThrottleStep(UUID uuid) {
        rtpDistanceRestoreTasks.remove(uuid);
        PlayerDistanceState state = rtpDistanceStates.get(uuid);
        if (state == null) {
            return;
        }

        Player player = plugin.getServer().getPlayer(uuid);
        if (player == null || !player.isOnline()) {
            rtpDistanceStates.remove(uuid);
            return;
        }

        boolean viewDone = restoreDistanceStep(player, "getViewDistance", "setViewDistance", state.viewDistance());
        boolean simulationDone = restoreDistanceStep(
                player,
                "getSimulationDistance",
                "setSimulationDistance",
                state.simulationDistance()
        );
        boolean noTickDone = restoreDistanceStep(
                player,
                "getNoTickViewDistance",
                "setNoTickViewDistance",
                state.noTickViewDistance()
        );
        boolean sendDone = restoreDistanceStep(
                player,
                "getSendViewDistance",
                "setSendViewDistance",
                state.sendViewDistance()
        );
        if (viewDone && simulationDone && noTickDone && sendDone) {
            rtpDistanceStates.remove(uuid);
            return;
        }

        BukkitTask task = plugin.getSpigotScheduler().runEntityLater(
                player,
                () -> restoreRtpChunkThrottleStep(uuid),
                20L
        );
        if (task != null) {
            rtpDistanceRestoreTasks.put(uuid, task);
        } else {
            restoreRtpChunkThrottle(uuid);
        }
    }

    private boolean restoreDistanceStep(Player player, String getterName, String setterName, Integer originalDistance) {
        if (originalDistance == null) {
            return true;
        }

        Integer currentDistance = invokeDistanceGetter(player, getterName);
        if (currentDistance == null) {
            invokeDistanceSetter(player, setterName, originalDistance);
            return true;
        }
        if (currentDistance >= originalDistance) {
            if (currentDistance > originalDistance) {
                invokeDistanceSetter(player, setterName, originalDistance);
            }
            return true;
        }

        int nextDistance = Math.min(originalDistance, currentDistance + 2);
        invokeDistanceSetter(player, setterName, nextDistance);
        return nextDistance >= originalDistance;
    }

    private int getRtpThrottleDistance(String path, int fallback) {
        return Math.max(2, plugin.getConfigManager().getRtp().getInt(path, fallback));
    }

    private void scheduleRtpChunkRefresh(Player player, Location destination) {
        if (destination == null || destination.getWorld() == null) {
            return;
        }

        World world = destination.getWorld();
        int centerChunkX = destination.getBlockX() >> 4;
        int centerChunkZ = destination.getBlockZ() >> 4;
        List<int[]> chunks = buildRtpChunkOrder(centerChunkX, centerChunkZ, getRtpChunkStabilizationRadius());
        if (chunks.isEmpty()) {
            return;
        }

        int chunksPerTick = getRtpChunkStabilizationChunksPerTick();
        final int[] nextIndex = {0};
        final BukkitTask[] taskRef = new BukkitTask[1];
        taskRef[0] = plugin.getSpigotScheduler().runEntityTimer(player, () -> {
            if (!player.isOnline()) {
                if (taskRef[0] != null) {
                    taskRef[0].cancel();
                }
                return;
            }

            int scheduled = 0;
            while (scheduled < chunksPerTick && nextIndex[0] < chunks.size()) {
                int[] chunk = chunks.get(nextIndex[0]++);
                scheduled++;
                plugin.getSpigotScheduler().runRegion(world, chunk[0], chunk[1], () ->
                        refreshLoadedChunk(world, chunk[0], chunk[1]));
            }

            if (nextIndex[0] >= chunks.size() && taskRef[0] != null) {
                taskRef[0].cancel();
            }
        }, 2L, 1L);
    }

    private List<int[]> buildRtpChunkOrder(int centerChunkX, int centerChunkZ, int radius) {
        return RtpChunkPreloadPolicy.chunkOrder(centerChunkX, centerChunkZ, radius);
    }

    private int getRtpChunkStabilizationRadius() {
        return RtpChunkPreloadPolicy.radius(plugin.getConfigManager().getRtp());
    }

    private int getRtpChunkStabilizationChunksPerTick() {
        return RtpChunkPreloadPolicy.chunksPerTick(plugin.getConfigManager().getRtp());
    }

    private void refreshLoadedChunk(World world, int chunkX, int chunkZ) {
        if (!world.isChunkLoaded(chunkX, chunkZ)) {
            return;
        }
        try {
            Method method = world.getClass().getMethod("refreshChunk", int.class, int.class);
            method.invoke(world, chunkX, chunkZ);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
    }

    private Integer invokeDistanceGetter(Player player, String methodName) {
        try {
            Method method = player.getClass().getMethod(methodName);
            Object value = method.invoke(player);
            return value instanceof Number number ? number.intValue() : null;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private void invokeDistanceSetter(Player player, String methodName, int value) {
        try {
            Method method = player.getClass().getMethod(methodName, int.class);
            method.invoke(player, value);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
    }
}
