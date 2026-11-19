package org.crudfeed.core.dto;

public record SignUpRequestDto(
        String email,
        String nickname,
        String displayName,
        String password
) {}
