package org.crudfeed.core.dto.account;

public record ShowFriendsListDto(
        long friendId,
        String friendName,
        boolean isUserConfirmed,
        boolean isFriendConfirmed
) {}