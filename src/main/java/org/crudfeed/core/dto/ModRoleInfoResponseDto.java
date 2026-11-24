package org.crudfeed.core.dto;

import org.crudfeed.core.entity.Role;

public record ModRoleInfoResponseDto(
       long modId,
       Role role
) {}