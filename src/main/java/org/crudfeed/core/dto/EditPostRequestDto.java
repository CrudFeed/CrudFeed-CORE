package org.crudfeed.core.dto;

public record EditPostRequestDto (
        long userId,
        long postId,
        String content
) {}