package com.vamshi.smartroute.algorithm;

import com.vamshi.smartroute.model.Edge;
import com.vamshi.smartroute.model.RoutingContext;
import com.vamshi.smartroute.model.RoutingPreference;

/**
 * Modular cost function calculator for graph edge traversal.
 */
public class CostCalculator {

    /**
     * Calculates the edge cost based on the query preference.
     * SHORTEST_DISTANCE -> distance in meters.
     * FASTEST_ROUTE -> walking time in seconds adjusted for congestion.
     */
    public double calculateCost(Edge edge, RoutingContext context) {
        if (context.preference() == RoutingPreference.SHORTEST_DISTANCE) {
            return edge.distanceMeters();
        } else {
            return edge.getEffectiveTimeSeconds(context.considerCongestion());
        }
    }
}
