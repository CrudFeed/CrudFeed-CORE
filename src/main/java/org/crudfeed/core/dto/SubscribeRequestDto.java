package org.crudfeed.core.dto;

public record SubscribeRequestDto (
        long userId,
        String nickname
) {}
