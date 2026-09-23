package com.vamshi.smartroute.service;

import com.vamshi.smartroute.algorithm.DijkstraRoutingAlgorithm;
import com.vamshi.smartroute.algorithm.RoutingAlgorithm;
import com.vamshi.smartroute.model.*;
import com.vamshi.smartroute.policy.PolicyChain;

import java.util.*;

/**
 * Service for multi-stop indoor navigation (e.g., Start -> Desk -> Coffee -> Meeting Room).
 * Evaluates optimal stop visiting ordering using brute-force permutation over small N (<= 5 stops).
 */
public class MultiStopRoutingService {

    private final RoutingAlgorithm routingAlgorithm;

    public MultiStopRoutingService() {
        this.routingAlgorithm = new DijkstraRoutingAlgorithm();
    }

    public MultiStopRoutingService(RoutingAlgorithm routingAlgorithm) {
        this.routingAlgorithm = routingAlgorithm;
    }

    /**
     * Calculates the optimal multi-stop route visiting all intermediate stop node IDs.
     */
    public RouteResult findMultiStopRoute(Graph graph, UUID startNodeId, List<UUID> intermediateStopIds, UUID destNodeId, RoutingContext context, PolicyChain policyChain) {
        if (intermediateStopIds == null || intermediateStopIds.isEmpty()) {
            RoutingContext directContext = new RoutingContext(
                startNodeId, destNodeId, context.wheelchairRequired(),
                context.requestedTime(), context.preference(),
                context.considerCongestion(), context.userRole()
            );
            return routingAlgorithm.findRoute(graph, directContext, policyChain);
        }

        if (intermediateStopIds.size() > 5) {
            throw new IllegalArgumentException("Multi-stop routing supports at most 5 intermediate stops");
        }

        // Generate all permutations of intermediate stops
        List<List<UUID>> permutations = generatePermutations(intermediateStopIds);

        List<Node> bestPathNodes = new ArrayList<>();
        List<Edge> bestPathEdges = new ArrayList<>();
        List<RouteSegment> bestSegments = new ArrayList<>();
        double minTotalDistance = Double.POSITIVE_INFINITY;
        int minTotalTime = Integer.MAX_VALUE;

        for (List<UUID> perm : permutations) {
            List<UUID> fullSequence = new ArrayList<>();
            fullSequence.add(startNodeId);
            fullSequence.addAll(perm);
            fullSequence.add(destNodeId);

            boolean validPermutation = true;
            double currentDist = 0.0;
            int currentTime = 0;
            List<Node> currentPathNodes = new ArrayList<>();
            List<Edge> currentPathEdges = new ArrayList<>();
            List<RouteSegment> currentSegments = new ArrayList<>();

            for (int i = 0; i < fullSequence.size() - 1; i++) {
                UUID u = fullSequence.get(i);
                UUID v = fullSequence.get(i + 1);

                RoutingContext legContext = new RoutingContext(
                    u, v, context.wheelchairRequired(),
                    context.requestedTime(), context.preference(),
                    context.considerCongestion(), context.userRole()
                );

                RouteResult legResult = routingAlgorithm.findRoute(graph, legContext, policyChain);
                if (!"SUCCESS".equals(legResult.status())) {
                    validPermutation = false;
                    break;
                }

                currentDist += legResult.totalDistanceMeters();
                currentTime += legResult.totalEstimatedTimeSeconds();

                if (i == 0) {
                    currentPathNodes.addAll(legResult.pathNodes());
                } else {
                    // Omit duplicate start node on intermediate legs
                    if (legResult.pathNodes().size() > 1) {
                        currentPathNodes.addAll(legResult.pathNodes().subList(1, legResult.pathNodes().size()));
                    }
                }
                currentPathEdges.addAll(legResult.pathEdges());
                currentSegments.addAll(legResult.segments());
            }

            if (validPermutation && currentDist < minTotalDistance) {
                minTotalDistance = currentDist;
                minTotalTime = currentTime;
                bestPathNodes = currentPathNodes;
                bestPathEdges = currentPathEdges;
                bestSegments = currentSegments;
            }
        }

        if (Double.isInfinite(minTotalDistance)) {
            return RouteResult.noRouteFound();
        }

        boolean allAccessible = bestPathEdges.stream().allMatch(Edge::wheelchairAccessible);

        return new RouteResult(
            "SUCCESS",
            minTotalDistance,
            minTotalTime,
            allAccessible,
            bestPathNodes,
            bestPathEdges,
            bestSegments
        );
    }

    private List<List<UUID>> generatePermutations(List<UUID> original) {
        List<List<UUID>> result = new ArrayList<>();
        permuteHelper(original, 0, result);
        return result;
    }

    private void permuteHelper(List<UUID> list, int index, List<List<UUID>> result) {
        if (index == list.size() - 1) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = index; i < list.size(); i++) {
            Collections.swap(list, index, i);
            permuteHelper(list, index + 1, result);
            Collections.swap(list, index, i);
        }
    }
}
