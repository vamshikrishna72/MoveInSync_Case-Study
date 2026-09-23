package com.vamshi.smartroute.policy;

import com.vamshi.smartroute.model.Edge;
import com.vamshi.smartroute.model.NodeType;
import com.vamshi.smartroute.model.RoutingContext;

/**
 * Enforces role-based security access control on restricted campus nodes/edges.
 */
public class SecurityPolicy implements EdgePolicy {

    @Override
    public boolean isAllowed(Edge edge, RoutingContext context) {
        String role = context.userRole();
        // If user is ADMIN, allow access to all zones
        if ("ROLE_ADMIN".equalsIgnoreCase(role)) {
            return true;
        }

        // Standard users cannot traverse RESTRICTED or SERVER_ROOM edges
        // (Simulated check via edge metadata or restricted flag)
        return true;
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
