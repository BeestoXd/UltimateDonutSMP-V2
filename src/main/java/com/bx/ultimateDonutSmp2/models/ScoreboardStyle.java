package com.bx.ultimateDonutSmp2.models;

import java.util.Locale;

/**
 * Which sidebar layout a player sees.
 *
 * <p>MODERN is the labelled layout ("$ Money 25.70K"); LEGACY is the compact one that puts the
 * player's name in the title and drops the labels. Turning the sidebar off entirely is a
 * separate flag, {@link PlayerData#isScoreboardVisible()}, so a player who hides the board keeps
 * the style they picked for when they turn it back on.
 */
public enum ScoreboardStyle {
    MODERN,
    LEGACY;

    public ScoreboardStyle next() {
        return this == MODERN ? LEGACY : MODERN;
    }

    /** Config section this style reads its title and lines from. */
    public String configKey() {
        return name();
    }

    public String display() {
        return this == MODERN ? "Modern" : "Legacy";
    }

    public static ScoreboardStyle parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return MODERN;
        }
        try {
            return valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return MODERN;
        }
    }
}
