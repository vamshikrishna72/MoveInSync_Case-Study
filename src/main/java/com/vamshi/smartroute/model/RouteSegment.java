package com.vamshi.smartroute.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a single turn-by-turn instruction segment along a calculated route.
 */
public record RouteSegment(
    int step,
    UUID fromNodeId,
    String fromNodeName,
    UUID toNodeId,
    String toNodeName,
    String instruction,
    double distanceMeters,
    int travelTimeSeconds,
    String floorName,
    String buildingName,
    EdgeType edgeType
) {
    public RouteSegment {
        Objects.requireNonNull(fromNodeName, "fromNodeName cannot be null");
        Objects.requireNonNull(toNodeName, "toNodeName cannot be null");
        Objects.requireNonNull(instruction, "instruction cannot be null");
    }
}
