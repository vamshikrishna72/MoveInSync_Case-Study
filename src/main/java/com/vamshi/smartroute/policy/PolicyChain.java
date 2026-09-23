package com.vamshi.smartroute.policy;

import com.vamshi.smartroute.model.Edge;
import com.vamshi.smartroute.model.RoutingContext;

import java.util.Comparator;
import java.util.List;

/**
 * Composite evaluator applying a series of EdgePolicies in order.
 */
public class PolicyChain {
    private final List<EdgePolicy> policies;

    public PolicyChain(List<EdgePolicy> policies) {
        this.policies = policies == null ? List.of() : policies.stream()
                .sorted(Comparator.comparingInt(EdgePolicy::getOrder))
                .toList();
    }

    public boolean isEdgeAllowed(Edge edge, RoutingContext context) {
        for (EdgePolicy policy : policies) {
            if (!policy.isAllowed(edge, context)) {
                return false;
            }
        }
        return true;
    }
}
