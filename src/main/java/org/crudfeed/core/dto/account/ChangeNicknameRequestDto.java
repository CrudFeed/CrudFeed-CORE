package org.crudfeed.core.dto.account;

public record ChangeNicknameRequestDto (
        long userId,
        String nickname
) {}
