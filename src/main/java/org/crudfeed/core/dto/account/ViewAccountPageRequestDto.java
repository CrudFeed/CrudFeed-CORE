package org.crudfeed.core.dto.account;

import java.time.Instant;

public record ViewAccountPageRequestDto (
        long id,
        String nickname,
        long subscribers,
        long subscriptions,
        String bio,
        Instant createdAt,
        Instant lastSeen
        ) {}
