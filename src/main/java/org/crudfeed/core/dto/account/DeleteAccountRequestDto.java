package org.crudfeed.core.dto.account;

public record DeleteAccountRequestDto(
        String email,
        String nickname
)
{}