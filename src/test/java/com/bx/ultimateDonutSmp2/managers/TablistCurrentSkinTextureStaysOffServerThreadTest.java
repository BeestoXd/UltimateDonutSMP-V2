package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TablistCurrentSkinTextureStaysOffServerThreadTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/TablistManager.java");

    @Test
    void currentTextureSyncGuardsAgainstMojangLookupsOnTheServerThread() throws Exception {
        String source = Files.readString(SOURCE);
        int asyncStart = source.indexOf("private SkinTexture resolveSkinsRestorerTexture(");
        int asyncEnd = source.indexOf("private SkinTexture resolveSkinsRestorerStoredTexture(");
        assertTrue(asyncStart >= 0 && asyncEnd > asyncStart, "async SkinsRestorer lookup should still sit above stored lookup");
        String asyncMethod = source.substring(asyncStart, asyncEnd);
        assertTrue(
                asyncMethod.contains("isPrimaryThread"),
                "The async SkinsRestorer path should still refuse to run on the server thread"
        );

        int syncStart = source.indexOf("private SkinTexture resolveSkinsRestorerCurrentTextureSync(");
        int syncEnd = source.indexOf("private SkinTexture resolveSkinsRestorerApiTexture(");
        assertTrue(syncStart >= 0 && syncEnd > syncStart, "current-texture sync lookup should still sit above the API helper");
        String syncMethod = source.substring(syncStart, syncEnd);
        assertTrue(
                syncMethod.contains("isPrimaryThread"),
                "resolveSkinsRestorerCurrentTextureSync still calls resolveSkinsRestorerPlayerStorageTexture, "
                        + "whose comment says getSkinForPlayer can contact Mojang and must stay off the server thread. "
                        + "Hide, fakeplayer, and dialog heads call this from the player thread, and unlike "
                        + "resolveSkinsRestorerTexture this method has no primary-thread guard."
        );
    }
}
