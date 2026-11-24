package org.crudfeed.core.dto;

public record WriteMessageRequestDto (
        long senderId,
        String receiverNickname,
        String content
) {}