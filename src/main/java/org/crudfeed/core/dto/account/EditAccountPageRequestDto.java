package org.crudfeed.core.dto.account;

public record EditAccountPageRequestDto (
        long userId,
        String content
) {}
