package org.crudfeed.core.dto;

public record PostRequestDto (
        long userId,
        long postId
) {}