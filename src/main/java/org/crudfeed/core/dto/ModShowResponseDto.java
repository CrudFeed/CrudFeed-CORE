package org.crudfeed.core.dto;

import org.crudfeed.core.entity.Role;

import java.time.Instant;

public record ModShowResponseDto(
        long id,
        String email,
        String nickname,
        String displayName,
        String bio,
        Role role,
        String avatarUrl,
        Instant lastSeen,
        Instant createdAt,
        Instant updatedAt,
        boolean isVerifiedEmail,
        boolean banned,
        boolean deleted
) {}