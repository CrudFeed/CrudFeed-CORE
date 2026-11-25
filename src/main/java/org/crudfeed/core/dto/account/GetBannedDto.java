package org.crudfeed.core.dto.account;

public record GetBannedDto(
       long userId,
       boolean banned
) {}