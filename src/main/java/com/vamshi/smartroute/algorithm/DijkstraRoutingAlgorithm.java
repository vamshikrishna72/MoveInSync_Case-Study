package com.vamshi.smartroute.algorithm;

import com.vamshi.smartroute.model.*;
import com.vamshi.smartroute.policy.PolicyChain;

import java.util.*;

/**
 * Dijkstra shortest path routing algorithm implementation using PriorityQueue.
 * Time Complexity: O((V + E) log V)
 * Space Complexity: O(V + E)
 */
public class DijkstraRoutingAlgorithm implements RoutingAlgorithm {

    private final CostCalculator costCalculator;
    private final InstructionGenerator instructionGenerator;

    public DijkstraRoutingAlgorithm() {
        this.costCalculator = new CostCalculator();
        this.instructionGenerator = new InstructionGenerator();
    }

    public DijkstraRoutingAlgorithm(CostCalculator costCalculator, InstructionGenerator instructionGenerator) {
        this.costCalculator = costCalculator;
        this.instructionGenerator = instructionGenerator;
    }

    @Override
    public RouteResult findRoute(Graph graph, RoutingContext context, PolicyChain policyChain) {
        UUID startId = context.startNodeId();
        UUID destId = context.destinationNodeId();

        // 1. Validate node existence
        if (!graph.containsNode(startId) || !graph.containsNode(destId)) {
            return RouteResult.noRouteFound();
        }

        // 2. Handle start == destination edge case
        if (startId.equals(destId)) {
            Node node = graph.getNode(startId);
            return RouteResult.sameNode(node);
        }

        // 3. Initialize distance maps and parent pointers
        Map<UUID, Double> minCostMap = new HashMap<>();
        Map<UUID, Edge> predecessorEdgeMap = new HashMap<>();
        Map<UUID, UUID> predecessorNodeMap = new HashMap<>();
        Set<UUID> visited = new HashSet<>();

        PriorityQueue<PathNode> pq = new PriorityQueue<>();

        minCostMap.put(startId, 0.0);
        pq.add(new PathNode(startId, 0.0));

        // 4. Core Dijkstra Traversal Loop
        while (!pq.isEmpty()) {
            PathNode current = pq.poll();
            UUID u = current.nodeId();

            if (visited.contains(u)) {
                continue;
            }
            visited.add(u);

            // Early exit if target destination reached
            if (u.equals(destId)) {
                break;
            }

            double currentCost = current.cost();

            for (Edge edge : graph.getOutgoingEdges(u)) {
                // Filter out invalid edges via dynamic Policy Chain
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

        // 5. Check if destination was reachable
        if (!minCostMap.containsKey(destId) || minCostMap.get(destId) == Double.POSITIVE_INFINITY) {
            return RouteResult.noRouteFound();
        }

        // 6. Reconstruct shortest path trajectory
        List<Node> pathNodes = new ArrayList<>();
        List<Edge> pathEdges = new ArrayList<>();

        UUID current = destId;
        while (current != null) {
            pathNodes.add(0, graph.getNode(current));
            Edge edge = predecessorEdgeMap.get(current);
            if (edge != null) {
                pathEdges.add(0, edge);
            }
            current = predecessorNodeMap.get(current);
        }

        // 7. Calculate distance and walking time totals
        double totalDistance = pathEdges.stream().mapToDouble(Edge::distanceMeters).sum();
        int totalTime = (int) pathEdges.stream()
                .mapToDouble(e -> e.getEffectiveTimeSeconds(context.considerCongestion()))
                .sum();

        boolean allAccessible = pathEdges.stream().allMatch(Edge::wheelchairAccessible);

        // 8. Generate turn-by-turn instructions
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
