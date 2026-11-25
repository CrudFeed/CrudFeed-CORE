package org.crudfeed.core.dto.account;

public record AuthResultResponseDto(
        boolean ok,
        String error,
        UserResponseDto user
) {}