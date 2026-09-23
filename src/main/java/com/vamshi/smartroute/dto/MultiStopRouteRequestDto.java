package com.vamshi.smartroute.dto;

import com.vamshi.smartroute.model.RoutingPreference;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record MultiStopRouteRequestDto(
    @NotNull(message = "startNodeId is required")
    UUID startNodeId,

    @NotEmpty(message = "intermediateStopIds list cannot be empty")
    @Size(max = 5, message = "At most 5 intermediate stops allowed")
    List<UUID> intermediateStopIds,

    @NotNull(message = "destinationNodeId is required")
    UUID destinationNodeId,

    boolean wheelchairRequired,

    LocalTime requestedTime,

    RoutingPreference preference,

    boolean considerCongestion
) {}
