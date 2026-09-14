package com.bx.ultimateDonutSmp2.dialogs;

import com.bx.ultimateDonutSmp2.utils.ColorUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import io.papermc.paper.datapack.Datapack;
import io.papermc.paper.datapack.DatapackManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Writes the datapack that puts the main menu on the ESC screen.
 *
 * <p>The vanilla {@code #minecraft:pause_screen_additions} tag lives in a frozen registry: only a
 * datapack or a Paper bootstrapper can add to it, and this is a Bukkit plugin. Paper's
 * {@code DATAPACK_DISCOVERY} lifecycle event is bootstrap-only too, so the menu is mirrored into
 * a generated datapack on disk instead.
 *
 * <p>The write happens in {@code onLoad}, which is the last moment before Minecraft reads the
 * world's datapacks. Writing it from {@code onEnable} instead would always be one restart behind:
 * the pack would land after the load that was supposed to pick it up.
 *
 * <p>Only the labels are a snapshot. Every button in the generated dialog is a static
 * {@code minecraft:custom} click carrying one of this plugin's own action ids, so pressing one
 * lands in {@link DialogManager} exactly as it would from {@code /menu} and opens the live,
 * config-driven screen. {@code dynamic/custom} is the input-form variant; using it in a datapack
 * with no inputs made 26.2 clients reject the dialog while still receiving the tag, which Fabric
 * treats as an unbound {@code #minecraft:pause_screen_additions} and disconnects.
 */
public final class PauseScreenDatapack {

    static final String PACK_NAME = "ultimatedonutsmp2";
    static final String DEFAULT_KEY = DialogActions.NAMESPACE + ":main_menu";
    /** Only consulted by a loader that ignores supported_formats. */
    static final int DEFAULT_PACK_FORMAT = 80;

    private static final String DIALOG_PATH = "DONUT_SMP_DIALOG";
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.SECTION_CHAR)
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Plugin plugin;

    public PauseScreenDatapack(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Writes the pack from {@code onLoad}, before the world's datapacks are read, so the button
     * is there on this start rather than the next one. The config manager does not exist yet at
     * that point, so {@code dialog.yml} is read straight off disk — or out of the jar on a first
     * run, where the file has not been saved yet.
     */
    public static void writeBeforeWorldLoad(Plugin plugin) {
        if (!DialogSupport.isAvailable()) {
            return;
        }
        try {
            new PauseScreenDatapack(plugin).write(earlyConfig(plugin), resolvePackRoot());
        } catch (RuntimeException exception) {
            plugin.getLogger().log(Level.WARNING, "Could not prepare the pause-screen datapack", exception);
        }
    }

    private static FileConfiguration earlyConfig(Plugin plugin) {
        File onDisk = new File(plugin.getDataFolder(), "dialog.yml");
        if (onDisk.isFile()) {
            return YamlConfiguration.loadConfiguration(onDisk);
        }
        try (InputStream stream = plugin.getResource("dialog.yml")) {
            if (stream == null) {
                return new YamlConfiguration();
            }
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return YamlConfiguration.loadConfiguration(reader);
            }
        } catch (IOException ignored) {
            return new YamlConfiguration();
        }
    }

    /**
     * Where Minecraft actually looks for datapacks: the level root, which is the world container
     * plus the configured level name, confirmed by the {@code level.dat} sitting in it.
     *
     * <p>This deliberately does not use {@code World#getWorldFolder()}. On the dimension-folder
     * layout that returns {@code world/dimensions/minecraft/overworld}, and a pack written there
     * is never read. It also has to work before any world is loaded, which is when the pack has
     * to land to be picked up on this start.
     */
    static Path resolvePackRoot() {
        return packRootIn(Bukkit.getWorldContainer().toPath(), levelName());
    }

    /** The level root is the folder holding {@code level.dat}, not the dimension inside it. */
    static Path packRootIn(Path container, String levelName) {
        Path candidate = container.resolve(levelName);
        if (!Files.isRegularFile(candidate.resolve("level.dat")) && Files.isDirectory(container)) {
            try (var entries = Files.list(container)) {
                candidate = entries
                        .filter(path -> Files.isRegularFile(path.resolve("level.dat")))
                        .findFirst()
                        .orElse(candidate);
            } catch (IOException ignored) {
                // Keep the configured name; the write reports whatever goes wrong with it.
            }
        }
        return candidate.resolve("datapacks").resolve(PACK_NAME);
    }

    private static String levelName() {
        Path properties = Path.of("server.properties");
        if (Files.isRegularFile(properties)) {
            Properties values = new Properties();
            try (Reader reader = Files.newBufferedReader(properties, StandardCharsets.UTF_8)) {
                values.load(reader);
                String name = values.getProperty("level-name");
                if (name != null && !name.isBlank()) {
                    return name.trim();
                }
            } catch (IOException ignored) {
                // Fall through to the vanilla default.
            }
        }
        return "world";
    }

    /**
     * Rewrites the pack after a config reload and reports whether the server actually has it
     * enabled, so "the button is missing" turns into a log line that says why.
     */
    public void refresh(FileConfiguration dialogConfig) {
        if (!DialogSupport.isAvailable()) {
            return;
        }
        deleteStrayPacks();
        write(dialogConfig, resolvePackRoot());
        reportStatus(dialogConfig);
    }

    /**
     * Removes copies left in a dimension folder by a build that resolved the path with
     * {@code World#getWorldFolder()}. Minecraft never loaded those, so they are pure litter.
     */
    private void deleteStrayPacks() {
        // Compared absolutely: on the flat world layout getWorldFolder() *is* the level root, and
        // a "." against an absolute path would otherwise read as a stray and delete the real pack.
        Path real = resolvePackRoot().toAbsolutePath().normalize();
        for (World world : plugin.getServer().getWorlds()) {
            Path stray = world.getWorldFolder().toPath()
                    .resolve("datapacks").resolve(PACK_NAME)
                    .toAbsolutePath().normalize();
            if (!stray.equals(real) && Files.isDirectory(stray)) {
                delete(stray);
            }
        }
    }

    /** Writes the three files, or removes the pack when the feature is off. */
    private void write(FileConfiguration dialogConfig, Path packRoot) {
        if (!dialogConfig.getBoolean("PAUSE-SCREEN.ENABLED", true)) {
            delete(packRoot);
            Datapack pack = findPack();
            if (pack != null && pack.isEnabled()) {
                try {
                    pack.setEnabled(false);
                } catch (RuntimeException ignored) {
                }
            }
            return;
        }

        String dialogKey = dialogKey(dialogConfig);
        int separator = dialogKey.indexOf(':');
        String namespace = dialogKey.substring(0, separator);
        String name = dialogKey.substring(separator + 1);

        try {
            boolean changed = writeFile(packRoot.resolve("pack.mcmeta"),
                    GSON.toJson(packMeta(dialogConfig.getInt("PAUSE-SCREEN.PACK-FORMAT", DEFAULT_PACK_FORMAT))));
            changed |= writeFile(
                    packRoot.resolve("data").resolve(namespace).resolve("dialog").resolve(name + ".json"),
                    GSON.toJson(dialog(dialogConfig))
            );
            changed |= writeFile(
                    packRoot.resolve("data").resolve("minecraft").resolve("tags")
                            .resolve("dialog").resolve("pause_screen_additions.json"),
                    GSON.toJson(tag(dialogKey))
            );

            if (changed) {
                plugin.getLogger().info("Wrote the pause-screen datapack to " + packRoot + ".");
            }
        } catch (IOException exception) {
            plugin.getLogger().log(Level.WARNING,
                    "Could not write the pause-screen datapack to " + packRoot, exception);
        }
    }

    /**
     * Says whether the server picked the pack up. If the pack is installed but disabled,
     * enables it so the pause screen button appears without requiring a manual command.
     */
    private void reportStatus(FileConfiguration dialogConfig) {
        if (!dialogConfig.getBoolean("PAUSE-SCREEN.ENABLED", true)) {
            return;
        }
        Datapack pack = findPack();
        if (pack == null) {
            plugin.getLogger().info("The pause-screen datapack is written but this server has not"
                    + " loaded it yet - restart once and the ESC menu button appears.");
            return;
        }
        if (!pack.isEnabled()) {
            try {
                pack.setEnabled(true);
                plugin.getLogger().info("Enabled the pause-screen datapack (" + pack.getName() + ").");
            } catch (RuntimeException exception) {
                plugin.getLogger().warning("The pause-screen datapack is installed but disabled,"
                        + " compatibility " + pack.getCompatibility() + ". Enable it with"
                        + " /datapack enable \"file/" + PACK_NAME + "\", or if it reports as incompatible"
                        + " raise PAUSE-SCREEN.PACK-FORMAT in dialog.yml.");
            }
        }
    }

    /**
     * {@code Server#getDatapackManager} is Paper-only and this compiles against the Spigot API,
     * so the manager is fetched reflectively. The manager type itself is a Paper class and
     * resolves normally.
     */
    private Datapack findPack() {
        try {
            Object raw = plugin.getServer().getClass().getMethod("getDatapackManager").invoke(plugin.getServer());
            if (!(raw instanceof DatapackManager manager)) {
                return null;
            }
            Datapack pack = manager.getPack("file/" + PACK_NAME);
            if (pack == null) {
                try {
                    manager.refreshPacks();
                    pack = manager.getPack("file/" + PACK_NAME);
                } catch (RuntimeException ignored) {
                }
            }
            return pack != null ? pack : manager.getPack(PACK_NAME);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError ignored) {
            return null;
        }
    }

    private void delete(Path packRoot) {
        if (!Files.isDirectory(packRoot)) {
            return;
        }
        try (var paths = Files.walk(packRoot)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // A file the server still holds open is left behind; the next start retries.
                }
            });
            plugin.getLogger().info("Removed the pause-screen datapack at " + packRoot + ".");
        } catch (IOException exception) {
            plugin.getLogger().log(Level.WARNING, "Could not remove " + packRoot, exception);
        }
    }

    // ── Generated files ────────────────────────────────────────────────────────

    /** The dialog's own resource location, falling back when the config gives a broken one. */
    static String dialogKey(FileConfiguration dialogConfig) {
        String configured = dialogConfig.getString("PAUSE-SCREEN.KEY", DEFAULT_KEY);
        if (configured == null) {
            return DEFAULT_KEY;
        }
        int separator = configured.indexOf(':');
        if (separator <= 0 || separator == configured.length() - 1) {
            return DEFAULT_KEY;
        }
        String namespace = DialogActions.sanitise(configured.substring(0, separator));
        String value = DialogActions.sanitise(configured.substring(separator + 1));
        return namespace.isEmpty() || value.isEmpty() ? DEFAULT_KEY : namespace + ':' + value;
    }

    /**
     * A wide {@code supported_formats} range is what actually keeps the pack loading: since 1.20.2
     * it takes precedence over {@code pack_format}, and the generated files use only fields that
     * have been stable since dialogs arrived. {@code pack_format} is still written for a loader
     * that reads it instead, and can be overridden from the config if a future version ever
     * refuses the pack over it.
     */
    static JsonObject packMeta(int packFormat) {
        JsonObject supported = new JsonObject();
        supported.addProperty("min_inclusive", 1);
        supported.addProperty("max_inclusive", 9999);
        supported.addProperty("min_format", 1);
        supported.addProperty("max_format", 9999);

        JsonObject pack = new JsonObject();
        pack.addProperty("description", "UltimateDonutSmp2 pause screen menu (generated)");
        pack.addProperty("pack_format", packFormat);
        pack.addProperty("min_format", 1);
        pack.addProperty("max_format", 9999);
        pack.add("supported_formats", supported);

        JsonObject root = new JsonObject();
        root.add("pack", pack);
        return root;
    }

    static JsonObject tag(String dialogKey) {
        JsonObject entry = new JsonObject();
        entry.addProperty("id", dialogKey);
        // If the dialog JSON fails to parse, Fabric clients disconnect on an unbound tag. An
        // optional entry lets them join; the ESC button is simply missing until the dialog loads.
        entry.addProperty("required", false);

        JsonArray values = new JsonArray();
        values.add(entry);

        JsonObject root = new JsonObject();
        root.addProperty("replace", false);
        root.add("values", values);
        return root;
    }

    /** The main grid, as a multi_action dialog. */
    static JsonObject dialog(FileConfiguration dialogConfig) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:multi_action");
        String title = dialogConfig.getString(DIALOG_PATH + ".TITLE", "UltimateDonutSmp V2");
        root.add("title", text(title));
        String externalTitle = dialogConfig.getString(DIALOG_PATH + ".EXTERNAL-TITLE", title);
        if (externalTitle != null && !externalTitle.isBlank()) {
            root.add("external_title", text(externalTitle));
        }
        root.addProperty("can_close_with_escape",
                dialogConfig.getBoolean(DIALOG_PATH + ".CAN-CLOSE-WITH-ESCAPE", true));
        root.addProperty("pause", dialogConfig.getBoolean(DIALOG_PATH + ".PAUSE", false));
        root.addProperty("after_action", afterAction(dialogConfig));
        root.addProperty("columns",
                Math.max(1, Math.min(8, dialogConfig.getInt(DIALOG_PATH + ".COLUMNS", 2))));
        root.add("actions", actions(dialogConfig));
        return root;
    }

    private static String afterAction(FileConfiguration dialogConfig) {
        String raw = dialogConfig.getString(DIALOG_PATH + ".AFTER-ACTION", "NONE");
        String lower = raw == null ? "close" : raw.trim().toLowerCase(Locale.ROOT);
        return switch (lower) {
            case "none", "wait_for_response" -> lower;
            default -> "close";
        };
    }

    private static JsonArray actions(FileConfiguration dialogConfig) {
        JsonArray actions = new JsonArray();
        int defaultWidth = DialogConfig.clampWidth(dialogConfig.getInt(DIALOG_PATH + ".BUTTON-WIDTH", 150));
        List<Map<?, ?>> buttons = dialogConfig.getMapList(DIALOG_PATH + ".BUTTONS");

        for (int index = 0; index < buttons.size(); index++) {
            Map<?, ?> entry = buttons.get(index);
            Object label = entry.get("LABEL");
            if (label == null) {
                continue;
            }
            String id = actionIdFor(entry, index);
            if (id == null) {
                continue;
            }

            JsonObject action = new JsonObject();
            action.addProperty("type", "minecraft:custom");
            action.addProperty("id", id);

            JsonObject button = new JsonObject();
            button.add("label", text(String.valueOf(label)));
            button.addProperty("width", width(entry, defaultWidth));
            button.add("action", action);
            actions.add(button);
        }
        return actions;
    }

    /**
     * A configured ACTION goes through as-is. A COMMAND cannot: the generated file is static, so
     * the button carries its position instead and {@code MainDialog} reads the command back out
     * of the config when the click arrives.
     */
    private static String actionIdFor(Map<?, ?> entry, int index) {
        Object action = entry.get("ACTION");
        if (action != null && !String.valueOf(action).isBlank()) {
            var key = DialogActions.key(String.valueOf(action));
            return key == null ? null : key.asString();
        }
        Object command = entry.get("COMMAND");
        if (command != null && !String.valueOf(command).isBlank()) {
            return DialogActions.NAMESPACE + ':' + DialogActions.MAIN_COMMAND + index;
        }
        return null;
    }

    private static int width(Map<?, ?> entry, int fallback) {
        Object value = entry.get("WIDTH");
        if (value instanceof Number number) {
            return DialogConfig.clampWidth(number.intValue());
        }
        return fallback;
    }

    /** Colourises without a viewer — the file is shared by everyone, so no placeholders. */
    private static JsonElement text(String raw) {
        Component component = DialogText.of(raw, null);
        return GsonComponentSerializer.gson().serializeToTree(component);
    }

    /** Returns true when the file was created or its content changed. */
    private static boolean writeFile(Path path, String content) throws IOException {
        String body = content + System.lineSeparator();
        if (Files.exists(path) && Files.readString(path, StandardCharsets.UTF_8).equals(body)) {
            return false;
        }
        Files.createDirectories(path.getParent());
        Files.writeString(path, body, StandardCharsets.UTF_8);
        return true;
    }
}
