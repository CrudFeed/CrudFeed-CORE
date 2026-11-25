package org.crudfeed.core.dto.message;

public record EditMessageRequestDto (
        long senderId,
        long messageId,
        String content
) {}