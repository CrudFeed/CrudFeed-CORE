package org.crudfeed.core.dto;

public record CreatePostRequestDto (
        long userId,
        String content
) {}