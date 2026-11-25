package org.crudfeed.core.dto.account;

public record SignUpRequestDto(
        String email,
        String nickname,
        String displayName,
        String password
) {
    @Override
    public String toString() {
        return "SignUpRequestDto[email=" + email + ", password=***]";
    }
}
