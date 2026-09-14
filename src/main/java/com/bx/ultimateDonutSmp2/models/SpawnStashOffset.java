package com.bx.ultimateDonutSmp2.models;

public record SpawnStashOffset(int x, int y, int z) {
    public static final SpawnStashOffset ZERO = new SpawnStashOffset(0, 0, 0);
}
