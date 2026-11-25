package org.crudfeed.core.dto.account;

import java.time.Instant;

public record ViewAccountPageDto(
        long id,
        String nickname,
        long subscribers,
        long subscriptions,
        String bio,
        Instant createdAt,
        Instant lastSeen
        ) {}
