package org.crudfeed.core.dto;

public record AuthResultResponseDto(
        boolean ok,
        String error,
        UserResponseDto user
) {}