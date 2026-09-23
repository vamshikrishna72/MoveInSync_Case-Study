package com.vamshi.smartroute.policy;

import com.vamshi.smartroute.model.Edge;
import com.vamshi.smartroute.model.RoutingContext;

/**
 * Enforces wheelchair accessibility rules.
 */
public class AccessibilityPolicy implements EdgePolicy {

    @Override
    public boolean isAllowed(Edge edge, RoutingContext context) {
        if (context.wheelchairRequired() && !edge.wheelchairAccessible()) {
            return false;
        }
        return true;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
