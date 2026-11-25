package org.crudfeed.core.model;

public record FriendFlags(
        boolean isUserConfirmed,
        boolean isFriendConfirmed
) {}