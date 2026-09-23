package com.vamshi.smartroute.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RoutingMetrics {

    private final Counter routeRequestsCounter;
    private final Counter routeSuccessCounter;
    private final Counter routeNoPathCounter;
    private final Counter routeFailureCounter;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Timer routeCalculationTimer;

    public RoutingMetrics(MeterRegistry registry) {
        this.routeRequestsCounter = registry.counter("smartroute.requests.total");
        this.routeSuccessCounter = registry.counter("smartroute.requests.success");
        this.routeNoPathCounter = registry.counter("smartroute.requests.no_path");
        this.routeFailureCounter = registry.counter("smartroute.requests.failure");
        this.cacheHitCounter = registry.counter("smartroute.cache.hits");
        this.cacheMissCounter = registry.counter("smartroute.cache.misses");
        this.routeCalculationTimer = registry.timer("smartroute.calculation.time");
    }

    public void incrementRequests() { routeRequestsCounter.increment(); }
    public void incrementSuccess() { routeSuccessCounter.increment(); }
    public void incrementNoPath() { routeNoPathCounter.increment(); }
    public void incrementFailure() { routeFailureCounter.increment(); }
    public void incrementCacheHit() { cacheHitCounter.increment(); }
    public void incrementCacheMiss() { cacheMissCounter.increment(); }

    public void recordCalculationTime(long durationNs) {
        routeCalculationTimer.record(durationNs, TimeUnit.NANOSECONDS);
    }
}
