package org.crudfeed.core.dto;

public record EditMessageRequestDto (
        long senderId,
        long messageId,
        String content
) {}