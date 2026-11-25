package org.crudfeed.core.dto.mod;

import org.crudfeed.core.entity.Role;

public record ModRoleInfoResponseDto(
       long modId,
       Role role
) {}