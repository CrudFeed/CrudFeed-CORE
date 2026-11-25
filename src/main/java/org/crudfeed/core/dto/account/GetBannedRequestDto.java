package org.crudfeed.core.dto.account;

public record GetBannedRequestDto(
       long userId,
       boolean banned
) {}