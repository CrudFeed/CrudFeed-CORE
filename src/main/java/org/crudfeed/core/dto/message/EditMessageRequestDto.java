package org.crudfeed.core.dto.message;

public record EditMessageRequestDto (
        long messageId,
        String content
) {}