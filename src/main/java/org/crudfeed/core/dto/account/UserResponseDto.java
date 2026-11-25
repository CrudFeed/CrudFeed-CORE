package org.crudfeed.core.dto.account;

import org.crudfeed.core.entity.Role;

public record UserResponseDto(
        long id,
        String email,
        String nickname,
        String displayName,
        Role role
) {}