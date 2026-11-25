package org.crudfeed.core.dto.post;

public record PostRequestDto (
        long userId,
        long postId
) {}