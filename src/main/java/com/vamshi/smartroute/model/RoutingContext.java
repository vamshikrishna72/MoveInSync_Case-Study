package com.vamshi.smartroute.model;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Encapsulates single query parameters for path computation.
 */
public record RoutingContext(
    UUID startNodeId,
    UUID destinationNodeId,
    boolean wheelchairRequired,
    LocalTime requestedTime,
    RoutingPreference preference,
    boolean considerCongestion,
    String userRole
) {
    public RoutingContext {
        Objects.requireNonNull(startNodeId, "Start Node ID cannot be null");
        Objects.requireNonNull(destinationNodeId, "Destination Node ID cannot be null");
        Objects.requireNonNull(requestedTime, "Requested time cannot be null");
        Objects.requireNonNull(preference, "Routing preference cannot be null");
    }

    public boolean isShortestDistance() {
        return preference == RoutingPreference.SHORTEST_DISTANCE;
    }

    public boolean isFastestRoute() {
        return preference == RoutingPreference.FASTEST_ROUTE;
    }
}
