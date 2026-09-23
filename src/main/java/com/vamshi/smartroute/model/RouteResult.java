package com.vamshi.smartroute.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Encapsulates the complete result of a route calculation.
 */
public record RouteResult(
    String status, // SUCCESS, NO_ROUTE_FOUND, START_EQUAL_DESTINATION
    double totalDistanceMeters,
    int totalEstimatedTimeSeconds,
    boolean wheelchairAccessible,
    List<Node> pathNodes,
    List<Edge> pathEdges,
    List<RouteSegment> segments
) {
    public RouteResult {
        Objects.requireNonNull(status, "Status cannot be null");
        pathNodes = pathNodes == null ? Collections.emptyList() : List.copyOf(pathNodes);
        pathEdges = pathEdges == null ? Collections.emptyList() : List.copyOf(pathEdges);
        segments = segments == null ? Collections.emptyList() : List.copyOf(segments);
    }

    public static RouteResult sameNode(Node node) {
        RouteSegment selfSegment = new RouteSegment(
            1,
            node.id(),
            node.name(),
            node.id(),
            node.name(),
            "You are already at " + node.name(),
            0.0,
            0,
            node.floorName(),
            node.buildingName(),
            EdgeType.CORRIDOR_WALK
        );
        return new RouteResult(
            "SUCCESS",
            0.0,
            0,
            true,
            List.of(node),
            Collections.emptyList(),
            List.of(selfSegment)
        );
    }

    public static RouteResult noRouteFound() {
        return new RouteResult(
            "NO_ROUTE_FOUND",
            Double.POSITIVE_INFINITY,
            Integer.MAX_VALUE,
            false,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
    }

    public List<UUID> getNodeIds() {
        return pathNodes.stream().map(Node::id).toList();
    }
}
