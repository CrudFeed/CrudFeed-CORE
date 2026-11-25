package org.crudfeed.core.dto.account;

public record ShowFriendsListRequestDto(
        long userId,
        long friendId,
        String friendName,
        boolean isUserConfirmed,
        boolean isFriendConfirmed
) {}