package com.bx.ultimateDonutSmp2.amethyst;

import java.util.Locale;

public enum AmethystToolType {
    DRILL("Shard Pickaxe"),
    CHOPPER("Shard Axe"),
    SELL_AXE("Shard Sell Axe"),
    SHOVEL("Shard Shovel"),
    BUCKET("Amethyst Bucket"),
    SHARD_BOOSTER("shard booster"),
    HASTE_POTION("Shard Potion of Haste");

    private final String displayName;

    AmethystToolType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Returns null on unknown name */
    public static AmethystToolType fromString(String name) {
        if (name == null) return null;
        try {
            return valueOf(name.toUpperCase(Locale.ROOT).replace(" ", "_").replace("-", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** Config section key for this type */
    public String getConfigKey() {
        return switch (this) {
            case DRILL       -> "DRILL";
            case CHOPPER     -> "CHOPPER";
            case SELL_AXE    -> "SELL-AXE";
            case SHOVEL      -> "SHOVEL";
            case BUCKET      -> "BUCKET";
            case SHARD_BOOSTER -> "SHARD-BOOSTER";
            case HASTE_POTION -> "HASTE-POTION";
        };
    }
}
