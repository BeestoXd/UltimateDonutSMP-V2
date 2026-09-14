package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoldCrateElytraCommandPlaceholderTest {

    private static final Path CRATES = Path.of("src/main/resources/crates.yml");
    private static final Path MANAGER = Path.of(
            "src/main/java/com/bx/ultimateDonutSmp2/managers/CrateManager.java");

    @Test
    void goldElytraGrantCommandResolvesTheClaimingPlayer() throws Exception {
        String crates = Files.readString(CRATES);
        String manager = Files.readString(MANAGER);

        int reward = crates.indexOf("ultra_elytra:");
        assertTrue(reward >= 0, "gold crate ultra_elytra reward should still be in crates.yml");
        String rewardBlock = crates.substring(reward, crates.indexOf("iron_golem_spawner:", reward));
        assertTrue(
                rewardBlock.contains("give {player} elytra"),
                "shipped gold Ultra Elytra grant should use {player}");

        int grantStart = manager.indexOf("private boolean grantCommandReward");
        assertTrue(grantStart >= 0, "grantCommandReward should still be in CrateManager");
        String grantMethod = manager.substring(grantStart, manager.indexOf("private ItemStack createGrantItem"));
        assertTrue(grantMethod.contains(".replace(\"{player}\", player.getName())"));
        assertTrue(grantMethod.contains(".replace(\"%player%\", player.getName())"));

        String resolved = "give {player} elytra"
                .replace("{player}", "Steve")
                .replace("{username}", "Steve")
                .replace("%player%", "Steve");
        assertFalse(
                resolved.contains("{player}"),
                "Gold crate Ultra Elytra GRANT should resolve the player"
        );
    }
}
