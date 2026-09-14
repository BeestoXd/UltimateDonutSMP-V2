package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.managers.TablistManager.SkinTexture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerSkinManagerTest {

    private Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("skin-manager-test-");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    void testCacheStoreAndRetrieve() {
        PlayerSkinManager manager = new PlayerSkinManager(null, tempDir.toFile());
        UUID uuid = UUID.randomUUID();
        SkinTexture texture = new SkinTexture("testBase64Value", "testSignature");

        assertFalse(manager.hasCachedSkin(uuid));
        assertNull(manager.getCachedSkin(uuid));
        assertNull(manager.resolveSkinTexture(uuid, "Steve"));

        manager.cacheSkin(uuid, texture);

        assertTrue(manager.hasCachedSkin(uuid));
        assertEquals(texture, manager.getCachedSkin(uuid));
        assertEquals(texture, manager.resolveSkinTexture(uuid, "Steve"));
    }

    @Test
    void testDiskPersistenceAcrossReload() {
        File dataFolder = tempDir.toFile();
        PlayerSkinManager manager1 = new PlayerSkinManager(null, dataFolder);

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        SkinTexture texture1 = new SkinTexture("textureOne", "sigOne");
        SkinTexture texture2 = new SkinTexture("textureTwo", null);

        manager1.cacheSkin(uuid1, texture1);
        manager1.cacheSkin(uuid2, texture2);
        manager1.saveCache();

        File cacheFile = new File(dataFolder, "skin-cache.json");
        assertTrue(cacheFile.exists(), "skin-cache.json should be written to disk");

        // Simulate new server startup with fresh manager instance
        PlayerSkinManager manager2 = new PlayerSkinManager(null, dataFolder);
        manager2.loadCache();

        assertTrue(manager2.hasCachedSkin(uuid1));
        assertTrue(manager2.hasCachedSkin(uuid2));
        assertEquals(texture1, manager2.getCachedSkin(uuid1));
        assertEquals("textureTwo", manager2.getCachedSkin(uuid2).value());
        assertNull(manager2.getCachedSkin(uuid2).signature());
    }

    @Test
    void testInvalidOrBlankTexturesAreNotCached() {
        PlayerSkinManager manager = new PlayerSkinManager(null, tempDir.toFile());
        UUID uuid = UUID.randomUUID();

        manager.cacheSkin(uuid, null);
        assertFalse(manager.hasCachedSkin(uuid));

        manager.cacheSkin(uuid, new SkinTexture("", null));
        assertFalse(manager.hasCachedSkin(uuid));

        manager.cacheSkin(uuid, new SkinTexture("   ", "sig"));
        assertFalse(manager.hasCachedSkin(uuid));
    }
}
