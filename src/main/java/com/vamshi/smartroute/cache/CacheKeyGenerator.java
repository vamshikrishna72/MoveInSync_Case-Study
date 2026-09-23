package com.vamshi.smartroute.cache;

import com.vamshi.smartroute.model.RoutingContext;
import org.springframework.stereotype.Component;

@Component
public class CacheKeyGenerator {

    /**
     * Generates a deterministic Redis cache key incorporating the graph version.
     */
    public String generateRouteCacheKey(long graphVersion, RoutingContext context) {
        int timeBucket = context.requestedTime().getHour(); // 1-hour time bucket for time-closure cache alignment
        return String.format(
            "route:v%d:start_%s:dest_%s:wheel_%b:pref_%s:cong_%b:tb_%d",
            graphVersion,
            context.startNodeId(),
            context.destinationNodeId(),
            context.wheelchairRequired(),
            context.preference().name(),
            context.considerCongestion(),
            timeBucket
        );
    }
}
