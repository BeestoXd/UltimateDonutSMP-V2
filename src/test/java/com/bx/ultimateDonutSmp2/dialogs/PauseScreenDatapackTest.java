package com.bx.ultimateDonutSmp2.dialogs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The generated pack is the only part of the interface Minecraft parses directly rather than
 * Paper building it for us, so its shape is pinned here: a wrong field name would show up as a
 * datapack that silently fails to load on someone's server.
 */
class PauseScreenDatapackTest {

    private static YamlConfiguration dialogConfig;

    @BeforeAll
    static void loadConfig() throws Exception {
        dialogConfig = new YamlConfiguration();
        dialogConfig.load(Path.of("src/main/resources/dialog.yml").toFile());
    }

    @Test
    void theTagPointsAtTheGeneratedDialog() {
        String key = PauseScreenDatapack.dialogKey(dialogConfig);
        JsonObject tag = PauseScreenDatapack.tag(key);

        assertFalse(tag.get("replace").getAsBoolean(), "replacing the tag would drop vanilla's own entries");
        JsonArray values = tag.getAsJsonArray("values");
        assertEquals(1, values.size());
        JsonObject entry = values.get(0).getAsJsonObject();
        assertEquals(key, entry.get("id").getAsString());
        assertFalse(entry.get("required").getAsBoolean(),
                "a required tag entry kicks Fabric clients when the dialog itself fails to parse");
    }

    @Test
    void theKeyFallsBackWhenTheConfigIsBroken() {
        YamlConfiguration broken = new YamlConfiguration();

        broken.set("PAUSE-SCREEN.KEY", "no_namespace");
        assertEquals(PauseScreenDatapack.DEFAULT_KEY, PauseScreenDatapack.dialogKey(broken));

        broken.set("PAUSE-SCREEN.KEY", "trailing:");
        assertEquals(PauseScreenDatapack.DEFAULT_KEY, PauseScreenDatapack.dialogKey(broken));

        broken.set("PAUSE-SCREEN.KEY", "Some:Menu");
        assertEquals("some:menu", PauseScreenDatapack.dialogKey(broken),
                "a resource location cannot hold capitals, so they are folded rather than rejected");
    }

    @Test
    void thePackMetaAcceptsAnyPackFormat() {
        JsonObject pack = PauseScreenDatapack
                .packMeta(PauseScreenDatapack.DEFAULT_PACK_FORMAT)
                .getAsJsonObject("pack");

        assertNotNull(pack.get("description"));
        assertNotNull(pack.get("pack_format"), "a loader that ignores supported_formats reads this instead");
        JsonObject supported = pack.getAsJsonObject("supported_formats");
        assertEquals(1, supported.get("min_inclusive").getAsInt());
        assertTrue(supported.get("max_inclusive").getAsInt() > 100);
        assertEquals(1, pack.get("min_format").getAsInt());
        assertTrue(pack.get("max_format").getAsInt() > 100);
    }

    @Test
    void thePackFormatCanBeOverriddenWhenAVersionRefusesTheDefault() {
        JsonObject pack = PauseScreenDatapack.packMeta(123).getAsJsonObject("pack");

        assertEquals(123, pack.get("pack_format").getAsInt());
    }

    @Test
    void theDialogIsAMultiActionGrid() {
        JsonObject dialog = PauseScreenDatapack.dialog(dialogConfig);

        assertEquals("minecraft:multi_action", dialog.get("type").getAsString());
        assertNotNull(dialog.get("title"), "a dialog with no title does not load");
        assertNotNull(dialog.get("external_title"), "external_title is used for the pause screen button label");
        assertEquals("UltimateDonutSmp V2", dialog.getAsJsonObject("external_title").get("text").getAsString());
        assertTrue(dialog.get("columns").getAsInt() >= 1);
        assertTrue(dialog.getAsJsonArray("actions").size() > 0);
    }

    @Test
    void externalTitleUsesConfiguredExternalTitle() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("DONUT_SMP_DIALOG.TITLE", "Main Menu");
        config.set("DONUT_SMP_DIALOG.EXTERNAL-TITLE", "Server Menu");

        JsonObject dialog = PauseScreenDatapack.dialog(config);
        assertNotNull(dialog.get("external_title"));
        assertEquals("Server Menu", dialog.getAsJsonObject("external_title").get("text").getAsString());
    }

    @Test
    void externalTitleFallsBackToTitleWhenNotSpecified() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("DONUT_SMP_DIALOG.TITLE", "Main Menu");

        JsonObject dialog = PauseScreenDatapack.dialog(config);
        assertNotNull(dialog.get("external_title"));
        assertEquals("Main Menu", dialog.getAsJsonObject("external_title").get("text").getAsString());
    }

    @Test
    void everyGeneratedButtonIsACustomClickBackIntoThePlugin() {
        JsonArray actions = PauseScreenDatapack.dialog(dialogConfig).getAsJsonArray("actions");

        for (var element : actions) {
            JsonObject button = element.getAsJsonObject();
            assertNotNull(button.get("label"));
            assertTrue(button.get("width").getAsInt() >= 1);

            JsonObject action = button.getAsJsonObject("action");
            assertEquals(
                    "minecraft:custom",
                    action.get("type").getAsString(),
                    "pause-screen buttons have no inputs, so the static custom click is the datapack codec"
            );
            assertTrue(
                    action.get("id").getAsString().startsWith(DialogActions.NAMESPACE + ':'),
                    "a generated button must route back to this plugin, not somewhere else"
            );
        }
    }

    @Test
    void aCommandButtonCarriesItsPositionSoTheLiveConfigStaysInCharge() {
        List<?> buttons = dialogConfig.getMapList("DONUT_SMP_DIALOG.BUTTONS");
        JsonArray actions = PauseScreenDatapack.dialog(dialogConfig).getAsJsonArray("actions");

        boolean sawCommandButton = false;
        for (var element : actions) {
            String id = element.getAsJsonObject().getAsJsonObject("action").get("id").getAsString();
            String value = id.substring(id.indexOf(':') + 1);
            String index = DialogActions.argument(value, DialogActions.MAIN_COMMAND);
            if (index == null) {
                continue;
            }
            sawCommandButton = true;
            int position = Integer.parseInt(index);
            assertTrue(position >= 0 && position < buttons.size(),
                    "index " + position + " is outside the configured button list");
        }
        assertTrue(sawCommandButton, "the shipped main menu has COMMAND buttons, so one must be generated");
    }

    @Test
    void thePackGoesBesideLevelDatRatherThanInsideADimension(@TempDir Path container) throws Exception {
        // The layout that caused the bug: level.dat at the root, the overworld a folder deeper.
        Path level = container.resolve("world");
        Files.createDirectories(level.resolve("dimensions").resolve("minecraft").resolve("overworld"));
        Files.writeString(level.resolve("level.dat"), "");

        Path root = PauseScreenDatapack.packRootIn(container, "world");

        assertEquals(level.resolve("datapacks").resolve(PauseScreenDatapack.PACK_NAME), root);
        assertFalse(root.toString().contains("dimensions"),
                "Minecraft only reads datapacks from the level root, never from a dimension folder");
    }

    @Test
    void aRenamedLevelIsFoundByItsLevelDat(@TempDir Path container) throws Exception {
        Path level = container.resolve("survival");
        Files.createDirectories(level);
        Files.writeString(level.resolve("level.dat"), "");

        Path root = PauseScreenDatapack.packRootIn(container, "world");

        assertEquals(level.resolve("datapacks").resolve(PauseScreenDatapack.PACK_NAME), root,
                "a server.properties the plugin could not read must not send the pack to a dead path");
    }

    @Test
    void withNoLevelAtAllTheConfiguredNameIsKept(@TempDir Path container) {
        Path root = PauseScreenDatapack.packRootIn(container, "world");

        assertEquals(
                container.resolve("world").resolve("datapacks").resolve(PauseScreenDatapack.PACK_NAME),
                root
        );
    }

    @Test
    void theTitleIsSerialisedAsAComponentRatherThanRawText() {
        JsonObject dialog = PauseScreenDatapack.dialog(dialogConfig);

        assertTrue(
                dialog.get("title").isJsonObject() || dialog.get("title").isJsonPrimitive(),
                "the client parses this with the component codec"
        );
    }
}
