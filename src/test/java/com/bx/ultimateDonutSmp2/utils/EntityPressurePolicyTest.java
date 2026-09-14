package com.bx.ultimateDonutSmp2.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.bx.ultimateDonutSmp2.utils.EntityPressurePolicy.Bucket;

class EntityPressurePolicyTest {

    @Test
    void excessIsZeroWhenTheChunkIsAtOrUnderTheCap() {
        assertEquals(0, EntityPressurePolicy.excessCount(2, 2));
        assertEquals(0, EntityPressurePolicy.excessCount(0, 8));
        assertEquals(0, EntityPressurePolicy.excessCount(8, -1));
    }

    @Test
    void excessIsTheCountAboveTheCap() {
        assertEquals(14, EntityPressurePolicy.excessCount(16, 2));
        assertEquals(1, EntityPressurePolicy.excessCount(25, 24));
    }

    @Test
    void capsMatchTheConfiguredBucket() {
        assertEquals(2, EntityPressurePolicy.capFor(Bucket.AMBIENT, 2, 24, 24));
        assertEquals(24, EntityPressurePolicy.capFor(Bucket.ANIMAL, 2, 24, 16));
        assertEquals(16, EntityPressurePolicy.capFor(Bucket.MONSTER, 2, 24, 16));
        assertEquals(Integer.MAX_VALUE, EntityPressurePolicy.capFor(Bucket.SKIP, 2, 24, 16));
    }
}
