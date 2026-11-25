package org.crudfeed.core.dto;

public record FriendFlags(
        boolean isUserConfirmed,
        boolean isFriendConfirmed
) {}