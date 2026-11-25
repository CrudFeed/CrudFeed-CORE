package org.crudfeed.core.dto.account;

import org.crudfeed.core.entity.Role;

import java.time.Instant;

public record ViewAccountPageDto(
        long id,
        String nickname,
        String displayName,
        String bio,
        Role role,
        long subscribers,
        long subscriptions,
        Instant createdAt,
        Instant lastSeen
) {}