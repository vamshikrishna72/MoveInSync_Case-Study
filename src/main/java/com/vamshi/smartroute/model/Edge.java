package com.vamshi.smartroute.model;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable in-memory domain model representing a weighted directed edge in the campus graph.
 */
public record Edge(
    UUID id,
    UUID fromNodeId,
    UUID toNodeId,
    double distanceMeters,
    int walkingTimeSeconds,
    boolean wheelchairAccessible,
    LocalTime openFrom,
    LocalTime openUntil,
    double congestionMultiplier,
    EdgeType edgeType
) {
    public Edge {
        Objects.requireNonNull(id, "Edge ID cannot be null");
        Objects.requireNonNull(fromNodeId, "fromNodeId cannot be null");
        Objects.requireNonNull(toNodeId, "toNodeId cannot be null");
        Objects.requireNonNull(edgeType, "edgeType cannot be null");
        if (distanceMeters < 0) {
            throw new IllegalArgumentException("distanceMeters cannot be negative");
        }
        if (walkingTimeSeconds < 0) {
            throw new IllegalArgumentException("walkingTimeSeconds cannot be negative");
        }
        if (congestionMultiplier < 1.0) {
            throw new IllegalArgumentException("congestionMultiplier must be >= 1.0");
        }
    }

    /**
     * Computes dynamic effective traversal duration in seconds.
     */
    public double getEffectiveTimeSeconds(boolean considerCongestion) {
        if (!considerCongestion) {
            return walkingTimeSeconds;
        }
        return walkingTimeSeconds * congestionMultiplier;
    }
}
