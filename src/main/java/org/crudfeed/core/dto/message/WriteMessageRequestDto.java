package org.crudfeed.core.dto.message;

public record WriteMessageRequestDto (
        long senderId,
        String receiverNickname,
        String content
) {}