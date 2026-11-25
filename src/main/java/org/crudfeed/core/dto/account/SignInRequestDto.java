package org.crudfeed.core.dto.account;

public record SignInRequestDto(
        String email,
        String password
) {
    @Override
    public String toString() {
        return "SignInRequestDto[email=" + email + ", password=***]";
    }
}
