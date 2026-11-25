package org.crudfeed.core.dto.mod;

public record ModActionRoleResultResponseDto(
        boolean ok,
        String text,
        ModRoleInfoResponseDto mod
)
{}