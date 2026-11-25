package org.crudfeed.core.dto.message;

public record DeleteMessageRequestDto (
        long senderId,
        long messageId
) {}