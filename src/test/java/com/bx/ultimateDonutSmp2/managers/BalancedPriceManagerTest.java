package com.bx.ultimateDonutSmp2.managers;

import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BalancedPriceManagerTest {

    private BalancedPriceManager manager;

    @BeforeEach
    void setUp() {
        manager = new BalancedPriceManager(null);
    }

    @Test
    void unobtainableAndAdminItemsAreBlocked() {
        assertEquals(-1.0, manager.getBalancedPrice(Material.AIR));
        assertEquals(-1.0, manager.getBalancedPrice(Material.BEDROCK));
        assertEquals(-1.0, manager.getBalancedPrice(Material.BARRIER));
        assertEquals(-1.0, manager.getBalancedPrice(Material.COMMAND_BLOCK));
        assertEquals(-1.0, manager.getBalancedPrice(Material.CHAIN_COMMAND_BLOCK));
        assertEquals(-1.0, manager.getBalancedPrice(Material.REPEATING_COMMAND_BLOCK));
        assertEquals(-1.0, manager.getBalancedPrice(Material.STRUCTURE_BLOCK));
        assertEquals(-1.0, manager.getBalancedPrice(Material.STRUCTURE_VOID));
        assertEquals(-1.0, manager.getBalancedPrice(Material.JIGSAW));
        assertEquals(-1.0, manager.getBalancedPrice(Material.LIGHT));
        assertEquals(-1.0, manager.getBalancedPrice(Material.DEBUG_STICK));
        assertEquals(-1.0, manager.getBalancedPrice(null));
    }

    @Test
    void curated121ItemsHaveFairBalancedPrices() {
        assertEquals(50000.0, manager.getBalancedPrice(Material.HEAVY_CORE));
        assertEquals(80.0, manager.getBalancedPrice(Material.BREEZE_ROD));
        assertEquals(20.0, manager.getBalancedPrice(Material.WIND_CHARGE));
        assertEquals(50080.0, manager.getBalancedPrice(Material.MACE));
        assertEquals(500.0, manager.getBalancedPrice(Material.TRIAL_KEY));
        assertEquals(2500.0, manager.getBalancedPrice(Material.OMINOUS_TRIAL_KEY));
        assertEquals(1500.0, manager.getBalancedPrice(Material.OMINOUS_BOTTLE));
        assertEquals(100.0, manager.getBalancedPrice(Material.ARMADILLO_SCUTE));
        assertEquals(600.0, manager.getBalancedPrice(Material.WOLF_ARMOR));
    }

    @Test
    void curatedSpawnEggsAndTemplatesHaveBalancedPrices() {
        assertEquals(50000.0, manager.getBalancedPrice(Material.WARDEN_SPAWN_EGG));
        assertEquals(25000.0, manager.getBalancedPrice(Material.VILLAGER_SPAWN_EGG));
        assertEquals(1500.0, manager.getBalancedPrice(Material.COW_SPAWN_EGG));
        assertEquals(3500.0, manager.getBalancedPrice(Material.ZOMBIE_SPAWN_EGG));
        assertEquals(7500.0, manager.getBalancedPrice(Material.BLAZE_SPAWN_EGG));
        assertEquals(20000.0, manager.getBalancedPrice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
        assertEquals(5000.0, manager.getBalancedPrice(Material.MUSIC_DISC_PIGSTEP));
    }

    @Test
    void patternDerivationsYieldPositiveBalancedPrices() {
        double wallPrice = manager.getBalancedPrice(Material.ANDESITE_WALL);
        assertTrue(wallPrice > 0, "Andesite wall price should be positive");

        double slabPrice = manager.getBalancedPrice(Material.OAK_SLAB);
        assertTrue(slabPrice > 0, "Oak slab price should be positive");

        double stairsPrice = manager.getBalancedPrice(Material.STONE_STAIRS);
        assertTrue(stairsPrice > 0, "Stone stairs price should be positive");

        double doorPrice = manager.getBalancedPrice(Material.OAK_DOOR);
        assertTrue(doorPrice > 0, "Oak door price should be positive");

        double fencePrice = manager.getBalancedPrice(Material.OAK_FENCE);
        assertTrue(fencePrice > 0, "Oak fence price should be positive");
    }

    @Test
    void cacheStoresAndReloadClears() {
        double p1 = manager.getBalancedPrice(Material.BREEZE_ROD);
        assertEquals(80.0, p1);

        double p2 = manager.getBalancedPrice(Material.BREEZE_ROD);
        assertEquals(p1, p2);

        manager.reload();
        double p3 = manager.getBalancedPrice(Material.BREEZE_ROD);
        assertEquals(p1, p3);
    }
}
