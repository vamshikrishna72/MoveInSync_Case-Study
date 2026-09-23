package com.vamshi.smartroute.algorithm;

import com.vamshi.smartroute.model.Graph;
import com.vamshi.smartroute.model.RouteResult;
import com.vamshi.smartroute.model.RoutingContext;
import com.vamshi.smartroute.policy.PolicyChain;

/**
 * Strategy interface for graph pathfinding algorithms.
 */
public interface RoutingAlgorithm {

    /**
     * Calculates the optimal route through the graph under the policy constraints.
     */
    RouteResult findRoute(Graph graph, RoutingContext context, PolicyChain policyChain);
}
