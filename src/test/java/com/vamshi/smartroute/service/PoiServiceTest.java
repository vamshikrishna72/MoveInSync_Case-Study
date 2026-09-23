package com.vamshi.smartroute.service;

import com.vamshi.smartroute.model.*;
import com.vamshi.smartroute.policy.AccessibilityPolicy;
import com.vamshi.smartroute.policy.PolicyChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PoiServiceTest {

    private PoiService poiService;
    private Graph graph;
    private UUID floorId;

    private Node startNode;
    private Node corridorNode;
    private Node washroom1Node;
    private Node washroom2Node;

    @BeforeEach
    void setUp() {
        poiService = new PoiService();
        graph = new Graph();
        floorId = UUID.randomUUID();

        startNode = new Node(UUID.randomUUID(), "Reception", NodeType.ENTRANCE, floorId, "Floor 1", "Main Block", 0, 0);
        corridorNode = new Node(UUID.randomUUID(), "Corridor A", NodeType.CORRIDOR, floorId, "Floor 1", "Main Block", 10, 0);
        washroom1Node = new Node(UUID.randomUUID(), "Far Washroom", NodeType.WASHROOM, floorId, "Floor 1", "Main Block", 50, 0);
        washroom2Node = new Node(UUID.randomUUID(), "Near Washroom", NodeType.WASHROOM, floorId, "Floor 1", "Main Block", 15, 0);

        graph.addNode(startNode);
        graph.addNode(corridorNode);
        graph.addNode(washroom1Node);
        graph.addNode(washroom2Node);

        // Start -> Far Washroom (Dist = 50)
        graph.addEdge(new Edge(UUID.randomUUID(), startNode.id(), washroom1Node.id(), 50, 40, true, null, null, 1.0, EdgeType.CORRIDOR_WALK));

        // Start -> Corridor A (Dist = 10) -> Near Washroom (Dist = 5) -> Total = 15
        graph.addEdge(new Edge(UUID.randomUUID(), startNode.id(), corridorNode.id(), 10, 8, true, null, null, 1.0, EdgeType.CORRIDOR_WALK));
        graph.addEdge(new Edge(UUID.randomUUID(), corridorNode.id(), washroom2Node.id(), 5, 4, true, null, null, 1.0, EdgeType.CORRIDOR_WALK));
    }

    @Test
    @DisplayName("Should locate mathematically nearest washroom successfully")
    void testFindNearestWashroom() {
        RoutingContext context = new RoutingContext(
            startNode.id(),
            washroom1Node.id(),
            false,
            LocalTime.of(12, 0),
            RoutingPreference.SHORTEST_DISTANCE,
            false,
            "ROLE_USER"
        );

        PolicyChain policyChain = new PolicyChain(List.of(new AccessibilityPolicy()));
        RouteResult result = poiService.findNearestPoi(graph, startNode.id(), NodeType.WASHROOM, context, policyChain);

        assertEquals("SUCCESS", result.status());
        assertEquals(15.0, result.totalDistanceMeters());
        assertEquals(washroom2Node.id(), result.pathNodes().get(result.pathNodes().size() - 1).id());
        assertEquals("Near Washroom", result.pathNodes().get(result.pathNodes().size() - 1).name());
    }
}
