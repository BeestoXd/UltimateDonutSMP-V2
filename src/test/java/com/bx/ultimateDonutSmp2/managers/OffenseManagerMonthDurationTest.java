package com.bx.ultimateDonutSmp2.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OffenseManagerMonthDurationTest {

    @Test
    void oneMoIsThirtyDaysNotOneMinute() {
        Long parsed = OffenseManager.parseDurationToMillis("1mo");
        assertNotNull(parsed, "1mo is a documented duration unit, not permanent");
        assertEquals(
                30L * 24L * 60L * 60L * 1000L,
                parsed,
                "Javadoc and the mo switch arm treat 1mo as thirty days. The unit group is a "
                        + "single-character class [s|m|h|d|w|mo|y], so 1mo matches 1m (one minute) "
                        + "and the leftover o is ignored. /offend tiers and /maintenance on 1mo "
                        + "would therefore last one minute."
        );
    }
}
