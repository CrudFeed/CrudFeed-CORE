package org.crudfeed.core.dto;

public record GetBannedRequestDto(
        long userId,
        boolean banned
) {}