package com.vamshi.smartroute.dto;

import java.util.List;
import java.util.UUID;

public record RouteResponseDto(
    String status,
    double totalDistanceMeters,
    int totalEstimatedTimeSeconds,
    boolean wheelchairAccessible,
    List<InstructionDto> path,
    List<UUID> nodeIds
) {}
