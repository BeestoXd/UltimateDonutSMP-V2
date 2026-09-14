package com.bx.ultimateDonutSmp2.models;

import java.util.UUID;

public class Bounty {

    private final UUID targetUuid;
    private double amount;
    private UUID placerUuid;
    private long timestamp;

    public Bounty(UUID targetUuid, double amount, UUID placerUuid) {
        this(targetUuid, amount, placerUuid, System.currentTimeMillis());
    }

    public Bounty(UUID targetUuid, double amount, UUID placerUuid, long timestamp) {
        this.targetUuid = targetUuid;
        this.amount = amount;
        this.placerUuid = placerUuid;
        this.timestamp = timestamp;
    }

    public UUID getTargetUuid() { return targetUuid; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = Math.max(0, amount); }
    public void addAmount(double extra) {
        this.amount += extra;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getPlacerUuid() { return placerUuid; }
    public void setPlacerUuid(UUID placerUuid) { this.placerUuid = placerUuid; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
