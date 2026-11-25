package org.crudfeed.core.dto.account;

public record DeleteAccountRequestDto(
        long userId,
        String email,
        String nickname
)
{}