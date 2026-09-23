package com.vamshi.smartroute.dto;

import com.vamshi.smartroute.model.EdgeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;
import java.util.UUID;

public record EdgeDto(
    UUID id,
    @NotNull(message = "fromNodeId is required")
    UUID fromNodeId,
    @NotNull(message = "toNodeId is required")
    UUID toNodeId,
    @Positive(message = "distanceMeters must be positive")
    double distanceMeters,
    @Positive(message = "walkingTimeSeconds must be positive")
    int walkingTimeSeconds,
    boolean wheelchairAccessible,
    LocalTime openFrom,
    LocalTime openUntil,
    double congestionMultiplier,
    @NotNull(message = "edgeType is required")
    EdgeType edgeType
) {}
