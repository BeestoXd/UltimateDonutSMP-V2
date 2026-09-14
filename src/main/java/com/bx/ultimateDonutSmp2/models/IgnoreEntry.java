package com.bx.ultimateDonutSmp2.models;

import java.util.UUID;

public record IgnoreEntry(
        UUID ownerUuid,
        UUID ignoredUuid,
        String ignoredNameSnapshot,
        long createdAt
) {
}
