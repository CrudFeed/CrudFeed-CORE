package org.crudfeed.core.dto.post;

public record CreatePostRequestDto (
        long userId,
        String content
) {}