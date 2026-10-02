package com.bx.ultimateDonutSmp2.managers;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrdersClaimDropLocationTest {

    @Test
    void claimDropLocationUsesTheCenterOfTheBlockUnderThePlayer() {
        Location positive = OrdersManager.claimDropLocation(new Location(null, 10.3, 64.0, 5.1));
        assertEquals(10.5, positive.getX(), 0.001);
        assertEquals(64.5, positive.getY(), 0.001);
        assertEquals(5.5, positive.getZ(), 0.001);

        Location negative = OrdersManager.claimDropLocation(new Location(null, -0.2, 70.0, -5.8));
        assertEquals(-0.5, negative.getX(), 0.001);
        assertEquals(70.5, negative.getY(), 0.001);
        assertEquals(-5.5, negative.getZ(), 0.001);
    }
}
