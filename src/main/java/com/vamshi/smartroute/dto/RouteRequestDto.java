package com.vamshi.smartroute.dto;

import com.vamshi.smartroute.model.RoutingPreference;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

public record RouteRequestDto(
    @NotNull(message = "startNodeId is required")
    UUID startNodeId,

    @NotNull(message = "destinationNodeId is required")
    UUID destinationNodeId,

    boolean wheelchairRequired,

    LocalTime requestedTime,

    RoutingPreference preference,

    boolean considerCongestion
) {
    public RouteRequestDto {
        if (requestedTime == null) {
            requestedTime = LocalTime.now();
        }
        if (preference == null) {
            preference = RoutingPreference.SHORTEST_DISTANCE;
        }
    }
}
