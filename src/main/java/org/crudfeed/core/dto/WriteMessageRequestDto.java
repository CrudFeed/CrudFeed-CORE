package org.crudfeed.core.dto;

public record WriteMessageRequestDto (
        long senderId,
        long receiverId,
        String content
) {}