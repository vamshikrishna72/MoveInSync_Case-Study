package com.vamshi.smartroute.policy;

import com.vamshi.smartroute.model.Edge;
import com.vamshi.smartroute.model.RoutingContext;

/**
 * Strategy interface for edge traversal filtering policies.
 */
public interface EdgePolicy {
    /**
     * Evaluates if the given edge is allowed to be traversed under the query context.
     */
    boolean isAllowed(Edge edge, RoutingContext context);

    /**
     * Determines execution precedence. Lower numbers execute earlier.
     */
    int getOrder();
}
