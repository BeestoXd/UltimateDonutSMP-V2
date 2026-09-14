package com.bx.ultimateDonutSmp2.utils;

import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;

/**
 * Caps how many ticking mobs may live in one chunk. The Paper watchdog dump from a join was
 * stuck inside {@code Bat.tick} and {@code Sheep.aiStep} collision, which means the world had
 * stored far more ambient mobs and animals than one chunk should ever tick.
 */
public final class EntityPressurePolicy {

    public enum Bucket {
        SKIP,
        AMBIENT,
        ANIMAL,
        MONSTER
    }

    private EntityPressurePolicy() {
    }

    public static int excessCount(int current, int cap) {
        if (cap < 0) {
            return 0;
        }
        return Math.max(0, current - cap);
    }

    public static Bucket bucket(Entity entity) {
        if (entity == null || entity instanceof Player || entity instanceof NPC || entity instanceof Villager) {
            return Bucket.SKIP;
        }
        if (hasCustomName(entity) || isTamed(entity)) {
            return Bucket.SKIP;
        }
        if (entity instanceof Ambient) {
            return Bucket.AMBIENT;
        }
        if (entity instanceof Animals) {
            return Bucket.ANIMAL;
        }
        if (entity instanceof Monster || entity instanceof Slime || entity instanceof Ghast) {
            return Bucket.MONSTER;
        }
        return Bucket.SKIP;
    }

    public static int capFor(Bucket bucket, int ambientCap, int animalCap, int monsterCap) {
        if (bucket == null) {
            return Integer.MAX_VALUE;
        }
        return switch (bucket) {
            case AMBIENT -> ambientCap;
            case ANIMAL -> animalCap;
            case MONSTER -> monsterCap;
            case SKIP -> Integer.MAX_VALUE;
        };
    }

    static boolean hasCustomName(Entity entity) {
        return entity.getCustomName() != null && !entity.getCustomName().isEmpty();
    }

    static boolean isTamed(Entity entity) {
        return entity instanceof Tameable tameable && tameable.isTamed();
    }
}
