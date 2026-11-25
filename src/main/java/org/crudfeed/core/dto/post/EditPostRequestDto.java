package org.crudfeed.core.dto.post;

public record EditPostRequestDto (
        long userId,
        long postId,
        String content
) {}