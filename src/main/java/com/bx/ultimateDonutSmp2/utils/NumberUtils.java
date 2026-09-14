package com.bx.ultimateDonutSmp2.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberUtils {

    public interface DurationFormatter {
        String formatTime(long totalSeconds);
        String formatTimeLong(long totalSeconds);
        String formatCountdown(long totalSeconds);

        default String formatTimeScoreboard(long totalSeconds) {
            return formatCompactScoreboard(totalSeconds);
        }
    }

    private static final DecimalFormat COMMA_FMT;
    private static final DecimalFormat SHORT_FMT;
    private static final String[] SHORT_SUFFIXES = {"", "K", "M", "B", "T", "Q"};
    private static volatile DurationFormatter durationFormatter;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        COMMA_FMT = new DecimalFormat("#,##0.##", symbols);
        SHORT_FMT = new DecimalFormat("#,##0.##", symbols);
    }

    /** Format with commas: 1234567 â†’ 1,234,567 */
    public static String format(double number) {
        return COMMA_FMT.format(number);
    }

    /** Format with suffix: 1500 -> 1.5K */
    public static String formatNice(double number) {
        if (!Double.isFinite(number)) {
            return "0";
        }

        double absolute = Math.abs(number);
        int suffixIndex = 0;

        while (absolute >= 1_000D && suffixIndex < SHORT_SUFFIXES.length - 1) {
            absolute /= 1_000D;
            suffixIndex++;
        }

        if (absolute >= 999.995D && suffixIndex < SHORT_SUFFIXES.length - 1) {
            absolute /= 1_000D;
            suffixIndex++;
        }

        String sign = number < 0D ? "-" : "";
        if (suffixIndex == 0) {
            return sign + COMMA_FMT.format(absolute);
        }

        return sign + SHORT_FMT.format(absolute) + SHORT_SUFFIXES[suffixIndex];
    }

    /** Parse a number string with optional K/M/B/T suffix */
    public static double parse(String input) {
        if (input == null || input.isBlank()) throw new NumberFormatException("Empty input");
        String clean = input.trim().replace(",", "").replace("_", "").toUpperCase(Locale.US);
        double multiplier = 1;
        if (clean.endsWith("Q")) { multiplier = 1_000_000_000_000_000D; clean = clean.substring(0, clean.length() - 1); }
        else if (clean.endsWith("T")) { multiplier = 1_000_000_000_000D; clean = clean.substring(0, clean.length() - 1); }
        else if (clean.endsWith("B")) { multiplier = 1_000_000_000; clean = clean.substring(0, clean.length() - 1); }
        else if (clean.endsWith("M")) { multiplier = 1_000_000; clean = clean.substring(0, clean.length() - 1); }
        else if (clean.endsWith("K")) { multiplier = 1_000;    clean = clean.substring(0, clean.length() - 1); }
        return Double.parseDouble(clean) * multiplier;
    }

    public static boolean isValidPositiveAmount(String input) {
        try {
            return parse(input) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void setDurationFormatter(DurationFormatter formatter) {
        durationFormatter = formatter;
    }

    /** Format seconds as readable time: 3665 → "1h 1m 5s" */
    public static String formatTime(long totalSeconds) {
        DurationFormatter formatter = durationFormatter;
        if (formatter != null) {
            return formatter.formatTime(totalSeconds);
        }
        return formatCompact(totalSeconds, false);
    }

    /** Format seconds with days: 574 → "9m 34s", 90061 → "1d 1h 1m 1s" */
    public static String formatTimeLong(long totalSeconds) {
        DurationFormatter formatter = durationFormatter;
        if (formatter != null) {
            return formatter.formatTimeLong(totalSeconds);
        }
        return formatCompact(totalSeconds, true);
    }

    /** Format remaining seconds for countdown display */
    public static String formatCountdown(long totalSeconds) {
        DurationFormatter formatter = durationFormatter;
        if (formatter != null) {
            return formatter.formatCountdown(totalSeconds);
        }
        long safe = Math.max(0L, totalSeconds);
        long m = safe / 60;
        long s = safe % 60;
        if (m > 0 && s > 0) return m + "m " + s + "s";
        if (m > 0) return m + "m";
        return s + "s";
    }

    /** Format seconds for scoreboard display: max 2 units, dropping seconds when hours or days are present. */
    public static String formatTimeScoreboard(long totalSeconds) {
        DurationFormatter formatter = durationFormatter;
        if (formatter != null) {
            return formatter.formatTimeScoreboard(totalSeconds);
        }
        return formatCompactScoreboard(totalSeconds);
    }

    private static String formatCompactScoreboard(long totalSeconds) {
        long safe = Math.max(0L, totalSeconds);
        long d = safe / 86400L;
        long h = (safe % 86400L) / 3600L;
        long m = (safe % 3600L) / 60L;
        long s = safe % 60L;

        StringBuilder parts = new StringBuilder();
        if (d > 0L) {
            appendUnit(parts, d, "d");
            if (h > 0L) {
                appendUnit(parts, h, "h");
            } else if (m > 0L) {
                appendUnit(parts, m, "m");
            }
        } else if (h > 0L) {
            appendUnit(parts, h, "h");
            if (m > 0L) {
                appendUnit(parts, m, "m");
            }
        } else if (m > 0L) {
            appendUnit(parts, m, "m");
            if (s > 0L) {
                appendUnit(parts, s, "s");
            }
        } else {
            appendUnit(parts, s, "s");
        }
        return parts.toString();
    }

    private static String formatCompact(long totalSeconds, boolean includeDays) {
        long safe = Math.max(0L, totalSeconds);
        long d = safe / 86400;
        long h = (safe % 86400) / 3600;
        long m = (safe % 3600) / 60;
        long s = safe % 60;
        StringBuilder parts = new StringBuilder();
        if (includeDays && d > 0) {
            appendUnit(parts, d, "d");
        }
        if (h > 0) {
            appendUnit(parts, h, "h");
        }
        if (m > 0) {
            appendUnit(parts, m, "m");
        }
        if (s > 0 || parts.isEmpty()) {
            appendUnit(parts, s, "s");
        }
        return parts.toString();
    }

    private static void appendUnit(StringBuilder parts, long value, String unit) {
        if (parts.length() > 0) {
            parts.append(' ');
        }
        parts.append(value).append(unit);
    }

    public static long parseLong(String input) {
        return (long) parse(input);
    }
}
