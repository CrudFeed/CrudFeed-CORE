package org.crudfeed.core.dto;

public record ModActionRoleResultResponseDto(
        boolean ok,
        String text,
        ModRoleInfoResponseDto mod
)
{}