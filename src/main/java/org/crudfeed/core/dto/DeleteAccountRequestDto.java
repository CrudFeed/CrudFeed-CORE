package org.crudfeed.core.dto;

public record DeleteAccountRequestDto(
        long userId,
        String email,
        String nickname
)
{}