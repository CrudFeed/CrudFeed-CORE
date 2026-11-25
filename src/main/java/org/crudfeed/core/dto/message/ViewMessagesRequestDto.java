package org.crudfeed.core.dto.message;

public record ViewMessagesRequestDto (
    long senderId,
    String receiverName
) {}