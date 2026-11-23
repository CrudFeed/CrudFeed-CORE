package org.crudfeed.core.dto;

public record ModUnbanAccountDtoRequest(
       long modId,
       long targetId
) {}