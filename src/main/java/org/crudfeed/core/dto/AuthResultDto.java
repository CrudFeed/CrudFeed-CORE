package org.crudfeed.core.dto;

public record AuthResultDto(
        boolean ok,
        String error,
        UserResponseDto user
) {}