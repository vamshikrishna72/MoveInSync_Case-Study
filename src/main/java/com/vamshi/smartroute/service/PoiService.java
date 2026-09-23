package com.vamshi.smartroute.service;

import com.vamshi.smartroute.algorithm.CostCalculator;
import com.vamshi.smartroute.algorithm.InstructionGenerator;
import com.vamshi.smartroute.algorithm.PathNode;
import com.vamshi.smartroute.model.*;
import com.vamshi.smartroute.policy.PolicyChain;

import java.util.*;

/**
 * Service providing Nearest Point of Interest (POI) routing operations.
 */
public class PoiService {

    private final CostCalculator costCalculator = new CostCalculator();
    private final InstructionGenerator instructionGenerator = new InstructionGenerator();

    /**
     * Finds the nearest POI of a specific type (e.g., WASHROOM, EMERGENCY_EXIT) from a start node.
     * Uses an early-exit Dijkstra traversal guaranteeing mathematical shortest path to the closest matching POI.
     */
    public RouteResult findNearestPoi(Graph graph, UUID startNodeId, NodeType targetPoiType, RoutingContext context, PolicyChain policyChain) {
        if (!graph.containsNode(startNodeId)) {
            return RouteResult.noRouteFound();
        }

        Node startNode = graph.getNode(startNodeId);

        // If start node is already the target POI type
        if (startNode.type() == targetPoiType) {
            return RouteResult.sameNode(startNode);
        }

        Map<UUID, Double> minCostMap = new HashMap<>();
        Map<UUID, Edge> predecessorEdgeMap = new HashMap<>();
        Map<UUID, UUID> predecessorNodeMap = new HashMap<>();
        Set<UUID> visited = new HashSet<>();

        PriorityQueue<PathNode> pq = new PriorityQueue<>();

        minCostMap.put(startNodeId, 0.0);
        pq.add(new PathNode(startNodeId, 0.0));

        UUID foundPoiNodeId = null;

        while (!pq.isEmpty()) {
            PathNode current = pq.poll();
            UUID u = current.nodeId();

            if (visited.contains(u)) {
                continue;
            }
            visited.add(u);

            Node node = graph.getNode(u);
            // Early exit check: First popped node matching targetPoiType is guaranteed to be nearest
            if (node.type() == targetPoiType) {
                foundPoiNodeId = u;
                break;
            }

            double currentCost = current.cost();

            for (Edge edge : graph.getOutgoingEdges(u)) {
                if (policyChain != null && !policyChain.isEdgeAllowed(edge, context)) {
                    continue;
                }

                UUID v = edge.toNodeId();
                if (visited.contains(v)) {
                    continue;
                }

                double edgeCost = costCalculator.calculateCost(edge, context);
                double newCost = currentCost + edgeCost;

                if (newCost < minCostMap.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    minCostMap.put(v, newCost);
                    predecessorEdgeMap.put(v, edge);
                    predecessorNodeMap.put(v, u);
                    pq.add(new PathNode(v, newCost));
                }
            }
        }

        if (foundPoiNodeId == null) {
            return RouteResult.noRouteFound();
        }

        // Reconstruct path to nearest POI
        List<Node> pathNodes = new ArrayList<>();
        List<Edge> pathEdges = new ArrayList<>();

        UUID current = foundPoiNodeId;
        while (current != null) {
            pathNodes.add(0, graph.getNode(current));
            Edge edge = predecessorEdgeMap.get(current);
            if (edge != null) {
                pathEdges.add(0, edge);
            }
            current = predecessorNodeMap.get(current);
        }

        double totalDistance = pathEdges.stream().mapToDouble(Edge::distanceMeters).sum();
        int totalTime = (int) pathEdges.stream()
                .mapToDouble(e -> e.getEffectiveTimeSeconds(context.considerCongestion()))
                .sum();
        boolean allAccessible = pathEdges.stream().allMatch(Edge::wheelchairAccessible);

        List<RouteSegment> segments = instructionGenerator.generateInstructions(graph, pathNodes, pathEdges);

        return new RouteResult(
            "SUCCESS",
            totalDistance,
            totalTime,
            allAccessible,
            pathNodes,
            pathEdges,
            segments
        );
    }
}
