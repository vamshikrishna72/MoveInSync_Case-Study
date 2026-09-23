package com.vamshi.smartroute.service;

import com.vamshi.smartroute.algorithm.DijkstraRoutingAlgorithm;
import com.vamshi.smartroute.algorithm.RoutingAlgorithm;
import com.vamshi.smartroute.cache.CacheService;
import com.vamshi.smartroute.dto.*;
import com.vamshi.smartroute.exception.NodeNotFoundException;
import com.vamshi.smartroute.exception.RouteNotFoundException;
import com.vamshi.smartroute.model.*;
import com.vamshi.smartroute.monitoring.RoutingMetrics;
import com.vamshi.smartroute.policy.AccessibilityPolicy;
import com.vamshi.smartroute.policy.PolicyChain;
import com.vamshi.smartroute.policy.TimeWindowPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    private final GraphDataLoader graphDataLoader;
    private final CacheService cacheService;
    private final RoutingMetrics metrics;
    private final PoiService poiService;
    private final MultiStopRoutingService multiStopRoutingService;
    private final RoutingAlgorithm routingAlgorithm;

    public RouteService(GraphDataLoader graphDataLoader, CacheService cacheService, RoutingMetrics metrics) {
        this.graphDataLoader = graphDataLoader;
        this.cacheService = cacheService;
        this.metrics = metrics;
        this.poiService = new PoiService();
        this.multiStopRoutingService = new MultiStopRoutingService();
        this.routingAlgorithm = new DijkstraRoutingAlgorithm();
    }

    public RouteResponseDto calculateRoute(RouteRequestDto request, String userRole) {
        metrics.incrementRequests();
        long startTime = System.nanoTime();

        Graph graph = graphDataLoader.getActiveGraph();
        long graphVersion = graphDataLoader.getCurrentGraphVersion();

        if (!graph.containsNode(request.startNodeId())) {
            metrics.incrementFailure();
            throw new NodeNotFoundException(request.startNodeId());
        }
        if (!graph.containsNode(request.destinationNodeId())) {
            metrics.incrementFailure();
            throw new NodeNotFoundException(request.destinationNodeId());
        }

        RoutingContext context = new RoutingContext(
            request.startNodeId(),
            request.destinationNodeId(),
            request.wheelchairRequired(),
            request.requestedTime(),
            request.preference(),
            request.considerCongestion(),
            userRole
        );

        // Check Redis Cache
        RouteResponseDto cached = cacheService.getCachedRoute(graphVersion, context);
        if (cached != null) {
            metrics.incrementCacheHit();
            metrics.incrementSuccess();
            return cached;
        }
        metrics.incrementCacheMiss();

        PolicyChain policyChain = new PolicyChain(List.of(
            new AccessibilityPolicy(),
            new TimeWindowPolicy()
        ));

        RouteResult result = routingAlgorithm.findRoute(graph, context, policyChain);
        long duration = System.nanoTime() - startTime;
        metrics.recordCalculationTime(duration);

        if (!"SUCCESS".equals(result.status())) {
            metrics.incrementNoPath();
            throw new RouteNotFoundException("No valid route found between " + request.startNodeId() + " and " + request.destinationNodeId());
        }

        RouteResponseDto response = mapToResponseDto(result);
        cacheService.cacheRoute(graphVersion, context, response);
        metrics.incrementSuccess();

        return response;
    }

    public RouteResponseDto findNearestPoi(NearestPoiRequestDto request, String userRole) {
        metrics.incrementRequests();
        Graph graph = graphDataLoader.getActiveGraph();

        if (!graph.containsNode(request.startNodeId())) {
            metrics.incrementFailure();
            throw new NodeNotFoundException(request.startNodeId());
        }

        RoutingContext context = new RoutingContext(
            request.startNodeId(),
            request.startNodeId(),
            request.wheelchairRequired(),
            request.requestedTime(),
            request.preference(),
            request.considerCongestion(),
            userRole
        );

        PolicyChain policyChain = new PolicyChain(List.of(new AccessibilityPolicy(), new TimeWindowPolicy()));
        RouteResult result = poiService.findNearestPoi(graph, request.startNodeId(), request.poiType(), context, policyChain);

        if (!"SUCCESS".equals(result.status())) {
            metrics.incrementNoPath();
            throw new RouteNotFoundException("No nearest POI of type " + request.poiType() + " reachable from start node");
        }

        metrics.incrementSuccess();
        return mapToResponseDto(result);
    }

    public RouteResponseDto findMultiStopRoute(MultiStopRouteRequestDto request, String userRole) {
        metrics.incrementRequests();
        Graph graph = graphDataLoader.getActiveGraph();

        RoutingContext context = new RoutingContext(
            request.startNodeId(),
            request.destinationNodeId(),
            request.wheelchairRequired(),
            request.requestedTime(),
            request.preference(),
            request.considerCongestion(),
            userRole
        );

        PolicyChain policyChain = new PolicyChain(List.of(new AccessibilityPolicy(), new TimeWindowPolicy()));
        RouteResult result = multiStopRoutingService.findMultiStopRoute(graph, request.startNodeId(), request.intermediateStopIds(), request.destinationNodeId(), context, policyChain);

        if (!"SUCCESS".equals(result.status())) {
            metrics.incrementNoPath();
            throw new RouteNotFoundException("No valid multi-stop route found visiting specified intermediate stops");
        }

        metrics.incrementSuccess();
        return mapToResponseDto(result);
    }

    private RouteResponseDto mapToResponseDto(RouteResult result) {
        List<InstructionDto> instructionDtos = result.segments().stream()
            .map(s -> new InstructionDto(
                s.step(), s.fromNodeId(), s.fromNodeName(),
                s.toNodeId(), s.toNodeName(), s.instruction(),
                s.distanceMeters(), s.travelTimeSeconds(),
                s.floorName(), s.buildingName(), s.edgeType()
            )).toList();

        return new RouteResponseDto(
            result.status(),
            result.totalDistanceMeters(),
            result.totalEstimatedTimeSeconds(),
            result.wheelchairAccessible(),
            instructionDtos,
            result.getNodeIds()
        );
    }
}
