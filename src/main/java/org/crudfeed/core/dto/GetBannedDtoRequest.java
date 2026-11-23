package org.crudfeed.core.dto;

public record GetBannedDtoRequest (
        long userId,
        boolean banned
) {}