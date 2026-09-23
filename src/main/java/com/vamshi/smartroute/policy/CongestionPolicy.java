package com.vamshi.smartroute.policy;

import com.vamshi.smartroute.model.Edge;
import com.vamshi.smartroute.model.RoutingContext;

/**
 * Evaluates congestion rules and weight scaling during peak campus hours.
 */
public class CongestionPolicy implements EdgePolicy {

    @Override
    public boolean isAllowed(Edge edge, RoutingContext context) {
        // Congestion does not block edge traversal completely,
        // but inflates cost in CostCalculator when considerCongestion is true.
        return true;
    }

    @Override
    public int getOrder() {
        return 4;
    }
}
