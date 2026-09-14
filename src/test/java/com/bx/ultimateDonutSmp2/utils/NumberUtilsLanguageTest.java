package com.bx.ultimateDonutSmp2.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberUtilsLanguageTest {

    @AfterEach
    void resetFormatter() {
        NumberUtils.setDurationFormatter(null);
    }

    @Test
    void delegatesAllDurationFormatsToTheActiveLanguageFormatter() {
        NumberUtils.setDurationFormatter(new NumberUtils.DurationFormatter() {
            @Override
            public String formatTime(long totalSeconds) {
                return "short:" + totalSeconds;
            }

            @Override
            public String formatTimeLong(long totalSeconds) {
                return "long:" + totalSeconds;
            }

            @Override
            public String formatCountdown(long totalSeconds) {
                return "countdown:" + totalSeconds;
            }

            @Override
            public String formatTimeScoreboard(long totalSeconds) {
                return "scoreboard:" + totalSeconds;
            }
        });

        assertEquals("short:65", NumberUtils.formatTime(65));
        assertEquals("long:90061", NumberUtils.formatTimeLong(90061));
        assertEquals("countdown:5", NumberUtils.formatCountdown(5));
        assertEquals("scoreboard:4107", NumberUtils.formatTimeScoreboard(4107));
    }

    @Test
    void retainsLegacyEnglishFormattingBeforeLanguageInitialization() {
        NumberUtils.setDurationFormatter(null);

        assertEquals("1h 1m 5s", NumberUtils.formatTime(3665));
        assertEquals("1d 1h 1m 1s", NumberUtils.formatTimeLong(90061));
        assertEquals("9m 34s", NumberUtils.formatTimeLong(574));
        assertEquals("10m 56s", NumberUtils.formatTimeLong(656));
        assertEquals("2h 10m", NumberUtils.formatTimeLong(7800));
        assertEquals("0s", NumberUtils.formatTimeLong(0));
        assertEquals("1m 5s", NumberUtils.formatCountdown(65));
        assertEquals("1h 8m", NumberUtils.formatTimeScoreboard(4107));
        assertEquals("1d 2h", NumberUtils.formatTimeScoreboard(93927));
        assertEquals("1d 5m", NumberUtils.formatTimeScoreboard(86727));
        assertEquals("8m 27s", NumberUtils.formatTimeScoreboard(507));
        assertEquals("27s", NumberUtils.formatTimeScoreboard(27));
        assertEquals("0s", NumberUtils.formatTimeScoreboard(0));
    }
}
