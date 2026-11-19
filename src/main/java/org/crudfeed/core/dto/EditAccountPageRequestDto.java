package org.crudfeed.core.dto;

public record EditAccountPageRequestDto (
        long userId,
        String content
) {}
