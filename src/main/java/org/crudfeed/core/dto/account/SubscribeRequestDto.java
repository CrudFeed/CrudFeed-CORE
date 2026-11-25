package org.crudfeed.core.dto.account;

public record SubscribeRequestDto (
        long userId,
        String nickname
) {}
