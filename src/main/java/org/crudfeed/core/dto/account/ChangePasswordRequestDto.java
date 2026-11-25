package org.crudfeed.core.dto.account;

public record ChangePasswordRequestDto (
        long userId,
        String password
) {}
