package org.crudfeed.core.dto;

public record UserResponseDto(
        long id,
        String email,
        String nickname
) {}