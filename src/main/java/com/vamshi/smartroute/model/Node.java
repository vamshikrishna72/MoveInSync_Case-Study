package com.vamshi.smartroute.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable in-memory domain model representing a location node in the campus graph.
 */
public record Node(
    UUID id,
    String name,
    NodeType type,
    UUID floorId,
    String floorName,
    String buildingName,
    double xCoordinate,
    double yCoordinate
) {
    public Node {
        Objects.requireNonNull(id, "Node ID cannot be null");
        Objects.requireNonNull(name, "Node name cannot be null");
        Objects.requireNonNull(type, "Node type cannot be null");
        Objects.requireNonNull(floorId, "Floor ID cannot be null");
    }

    public boolean isPoi() {
        return type.isPoi();
    }
}
