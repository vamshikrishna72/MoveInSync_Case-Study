package com.vamshi.smartroute.algorithm;

import java.util.Objects;
import java.util.UUID;

/**
 * PriorityQueue state container holding min-cost distance to a node during Dijkstra traversal.
 */
public record PathNode(
    UUID nodeId,
    double cost
) implements Comparable<PathNode> {

    public PathNode {
        Objects.requireNonNull(nodeId, "nodeId cannot be null");
    }

    @Override
    public int compareTo(PathNode other) {
        return Double.compare(this.cost, other.cost);
    }
}
