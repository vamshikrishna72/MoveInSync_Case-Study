package com.vamshi.smartroute.policy;

import com.vamshi.smartroute.model.Edge;
import com.vamshi.smartroute.model.RoutingContext;

import java.time.LocalTime;

/**
 * Enforces time-based closure restrictions on edges.
 */
public class TimeWindowPolicy implements EdgePolicy {

    @Override
    public boolean isAllowed(Edge edge, RoutingContext context) {
        LocalTime reqTime = context.requestedTime();
        if (edge.openFrom() != null && edge.openUntil() != null) {
            if (reqTime.isBefore(edge.openFrom()) || reqTime.isAfter(edge.openUntil())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
