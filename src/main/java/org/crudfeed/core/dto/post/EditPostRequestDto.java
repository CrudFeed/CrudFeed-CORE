package org.crudfeed.core.dto.post;

public record EditPostRequestDto (
        long postId,
        String content
) {}