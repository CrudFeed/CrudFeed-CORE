package org.crudfeed.core.dto;

public record ViewMessagesRequestDto (
    long senderId,
    String receiverName
) {}