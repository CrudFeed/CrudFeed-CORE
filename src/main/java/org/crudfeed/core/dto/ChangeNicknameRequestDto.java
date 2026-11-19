package org.crudfeed.core.dto;

public record ChangeNicknameRequestDto (
        long userId,
        String nickname
) {}
