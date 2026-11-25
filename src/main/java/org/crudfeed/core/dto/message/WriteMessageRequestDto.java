package org.crudfeed.core.dto.message;

public record WriteMessageRequestDto (
        String receiverNickname,
        String content
) {}