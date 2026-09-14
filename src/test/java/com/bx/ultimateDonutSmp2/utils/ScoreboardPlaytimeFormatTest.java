package com.bx.ultimateDonutSmp2.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreboardPlaytimeFormatTest {

    @AfterEach
    void resetFormatter() {
        NumberUtils.setDurationFormatter(null);
    }

    @Test
    void formatsSecondsOnly() {
        assertEquals("0s", NumberUtils.formatTimeScoreboard(0));
        assertEquals("1s", NumberUtils.formatTimeScoreboard(1));
        assertEquals("27s", NumberUtils.formatTimeScoreboard(27));
        assertEquals("59s", NumberUtils.formatTimeScoreboard(59));
    }

    @Test
    void formatsMinutesAndSeconds() {
        assertEquals("1m", NumberUtils.formatTimeScoreboard(60));
        assertEquals("1m 1s", NumberUtils.formatTimeScoreboard(61));
        assertEquals("8m 27s", NumberUtils.formatTimeScoreboard(507));
        assertEquals("59m 59s", NumberUtils.formatTimeScoreboard(3599));
    }

    @Test
    void formatsHoursAndMinutesDroppingSeconds() {
        // Exactly hours
        assertEquals("1h", NumberUtils.formatTimeScoreboard(3600));
        // Hours with seconds but 0 minutes: seconds are dropped
        assertEquals("1h", NumberUtils.formatTimeScoreboard(3605));
        assertEquals("1h", NumberUtils.formatTimeScoreboard(3659));
        // Hours with minutes: seconds dropped, user screenshot: 1h 8m 27s -> 1h 8m
        assertEquals("1h 1m", NumberUtils.formatTimeScoreboard(3665));
        assertEquals("1h 8m", NumberUtils.formatTimeScoreboard(4107));
        assertEquals("23h 59m", NumberUtils.formatTimeScoreboard(86399));
    }

    @Test
    void formatsDaysWithHoursOrMinutesDroppingSeconds() {
        // Exactly days
        assertEquals("1d", NumberUtils.formatTimeScoreboard(86400));
        // Days with seconds only
        assertEquals("1d", NumberUtils.formatTimeScoreboard(86420));
        // Days with minutes (hours == 0): d,m
        assertEquals("1d 5m", NumberUtils.formatTimeScoreboard(86400 + 300));
        assertEquals("1d 5m", NumberUtils.formatTimeScoreboard(86400 + 327));
        // Days with hours: d,h
        assertEquals("1d 2h", NumberUtils.formatTimeScoreboard(86400 + 7200));
        assertEquals("1d 2h", NumberUtils.formatTimeScoreboard(86400 + 7200 + 300 + 27));
        assertEquals("5d 12h", NumberUtils.formatTimeScoreboard(5 * 86400 + 12 * 3600 + 30 * 60 + 15));
    }
}
