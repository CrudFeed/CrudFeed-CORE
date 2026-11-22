package org.crudfeed.core.dto;

import java.time.Instant;

public record ViewFeedRequestDto(
        long id,
        long authorId,
        String authorName,
        String content,
        long likes,
        Instant createdAt,
        Instant updatedAt
) {}
