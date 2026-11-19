package org.crudfeed.core.dto;

public record SignInRequestDto(
        String email,
        String password
) {}
