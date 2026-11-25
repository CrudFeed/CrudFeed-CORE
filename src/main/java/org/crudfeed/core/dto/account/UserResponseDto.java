package org.crudfeed.core.dto.account;

public record UserResponseDto(
        long id,
        String email,
        String nickname,
        String displayName,
        org.crudfeed.core.entity.Role role
) {}