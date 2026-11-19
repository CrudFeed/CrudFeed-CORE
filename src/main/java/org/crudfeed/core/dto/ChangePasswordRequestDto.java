package org.crudfeed.core.dto;

public record ChangePasswordRequestDto (
        long userId,
        String password
) {}
