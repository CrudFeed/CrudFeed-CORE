package org.crudfeed.core.dto;

import java.time.Instant;

public record ViewFeedRequestDto(
        long id,
        long authorId,
        String authorName,
        String content,
        long likes,
        long dislikes,
        Instant createdAt,
        Instant updatedAt
) {}
