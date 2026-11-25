package org.crudfeed.core.dto.account;

public record SignInRequestDto(
        String email,
        String password
) {}
