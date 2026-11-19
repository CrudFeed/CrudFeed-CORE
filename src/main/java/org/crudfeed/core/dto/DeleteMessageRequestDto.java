package org.crudfeed.core.dto;

public record DeleteMessageRequestDto (
        long senderId,
        long messageId
) {}