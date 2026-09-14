package com.bx.ultimateDonutSmp2.dialogs.screens;

import com.bx.ultimateDonutSmp2.managers.TablistManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DialogPlayerHeadsTest {

    @Test
    void resolveSkinTextureNullInputs() {
        assertNull(DialogPlayerHeads.resolveSkinTexture(null, null));
        assertNull(DialogPlayerHeads.resolveSkinTexture(null, ""));
    }

    @Test
    void componentDoesNotThrowOnArbitraryUuid() {
        UUID uuid = UUID.randomUUID();
        Component component = DialogPlayerHeads.component(uuid, "TestUser");
        assertNotNull(component);
    }

    @Test
    void componentHandlesNullNameOrUuid() {
        assertNotNull(DialogPlayerHeads.component(null, "TestUser"));
        assertNotNull(DialogPlayerHeads.component(UUID.randomUUID(), null));
        assertNotNull(DialogPlayerHeads.component(null, null));
    }

    @Test
    void missingTextureDoesNotEmitAYellowPlaceholderHead() {
        Component component = DialogPlayerHeads.component(UUID.randomUUID(), "Ryaa");
        assertEquals(Component.empty(), component);
        assertEquals(Component.empty(), DialogPlayerHeads.componentFromTexture(null));
        assertEquals(Component.empty(), DialogPlayerHeads.componentFromTexture(new TablistManager.SkinTexture(" ", null)));
    }

    @Test
    void texturedHeadEmbedsTheSkinWithoutAPlayerUuid() {
        String value = Base64.getEncoder().encodeToString(
                "{\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/deadbeef\"}}}"
                        .getBytes(StandardCharsets.UTF_8));
        UUID playerId = UUID.fromString("12345678-1234-1234-1234-123456789abc");
        Component component = DialogPlayerHeads.componentFromTexture(new TablistManager.SkinTexture(value, "signature"));
        String json = GsonComponentSerializer.gson().serialize(component);

        assertTrue(json.contains(value), json);
        assertTrue(json.contains("\"name\":\"textures\""), json);
        assertFalse(json.contains(playerId.toString()), json);
        assertFalse(json.contains("Ryaa"), json);
        assertFalse(json.contains("\"id\":"), json);
    }

    @Test
    void signedTextureStillOmitsAProfileIdSoTheClientCannotPickSunny() {
        UUID profileId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        String compact = profileId.toString().replace("-", "");
        String value = Base64.getEncoder().encodeToString(
                ("{\"profileId\":\"" + compact + "\",\"textures\":{\"SKIN\":{\"url\":"
                        + "\"https://textures.minecraft.net/texture/abcd\"}}}")
                        .getBytes(StandardCharsets.UTF_8));
        Component component = DialogPlayerHeads.componentFromTexture(
                new TablistManager.SkinTexture(value, "real-signature"));
        String json = GsonComponentSerializer.gson().serialize(component);

        assertTrue(json.contains(value), json);
        assertTrue(json.contains("real-signature"), json);
        assertFalse(json.contains("\"id\":"), json);
    }

    @Test
    void rankColorDoesNotTintAnInsertedHead() {
        String value = Base64.getEncoder().encodeToString(
                "{\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/deadbeef\"}}}"
                        .getBytes(StandardCharsets.UTF_8));
        Component head = DialogPlayerHeads.componentFromTexture(new TablistManager.SkinTexture(value, null));
        Component prefix = Component.text("#1 ").color(TextColor.color(0xFCFC00));
        Component row = DialogPlayerHeads.insertBetween(prefix, head, Component.text("Ryaa"));

        assertEquals(3, row.children().size(), row.children().toString());
        assertEquals(TextColor.color(0xFCFC00), row.children().get(0).color());
        assertNull(row.children().get(1).color(), "the head must not inherit the #1 yellow");
        String json = GsonComponentSerializer.gson().serialize(row);
        assertTrue(json.contains(value), json);
    }
}
