package com.vamshi.smartroute.algorithm;

import com.vamshi.smartroute.model.*;
import com.vamshi.smartroute.policy.AccessibilityPolicy;
import com.vamshi.smartroute.policy.PolicyChain;
import com.vamshi.smartroute.policy.TimeWindowPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraRoutingAlgorithmTest {

    private DijkstraRoutingAlgorithm dijkstra;
    private Graph graph;
    private UUID floorId;

    private Node startNode;
    private Node corridorANode;
    private Node stairsNode;
    private Node liftNode;
    private Node destNode;

    @BeforeEach
    void setUp() {
        dijkstra = new DijkstraRoutingAlgorithm();
        graph = new Graph();
        floorId = UUID.randomUUID();

        startNode = new Node(UUID.randomUUID(), "Reception", NodeType.ENTRANCE, floorId, "Floor 1", "Main Block", 0, 0);
        corridorANode = new Node(UUID.randomUUID(), "Corridor A", NodeType.CORRIDOR, floorId, "Floor 1", "Main Block", 10, 0);
        stairsNode = new Node(UUID.randomUUID(), "Stairs S1", NodeType.STAIRS, floorId, "Floor 1", "Main Block", 20, 0);
        liftNode = new Node(UUID.randomUUID(), "Lift L1", NodeType.LIFT, floorId, "Floor 1", "Main Block", 15, 10);
        destNode = new Node(UUID.randomUUID(), "Meeting Room 4B", NodeType.ROOM, floorId, "Floor 2", "Main Block", 20, 10);

        graph.addNode(startNode);
        graph.addNode(corridorANode);
        graph.addNode(stairsNode);
        graph.addNode(liftNode);
        graph.addNode(destNode);
    }

    @Test
    @DisplayName("Should find shortest distance path via stairs for normal user")
    void testShortestDistancePath() {
        // Start -> Corridor A -> Stairs -> Destination (Total dist = 10 + 10 + 10 = 30)
        graph.addEdge(new Edge(UUID.randomUUID(), startNode.id(), corridorANode.id(), 10, 8, true, null, null, 1.0, EdgeType.CORRIDOR_WALK));
        graph.addEdge(new Edge(UUID.randomUUID(), corridorANode.id(), stairsNode.id(), 10, 8, true, null, null, 1.0, EdgeType.CORRIDOR_WALK));
        graph.addEdge(new Edge(UUID.randomUUID(), stairsNode.id(), destNode.id(), 10, 15, false, null, null, 1.0, EdgeType.STAIR_CLIMB));

        // Start -> Lift -> Destination (Total dist = 20 + 20 = 40)
        graph.addEdge(new Edge(UUID.randomUUID(), startNode.id(), liftNode.id(), 20, 15, true, null, null, 1.0, EdgeType.CORRIDOR_WALK));
        graph.addEdge(new Edge(UUID.randomUUID(), liftNode.id(), destNode.id(), 20, 20, true, null, null, 1.0, EdgeType.LIFT_TRAVEL));

        RoutingContext context = new RoutingContext(
            startNode.id(),
            destNode.id(),
            false, // Wheelchair NOT required
            LocalTime.of(10, 0),
            RoutingPreference.SHORTEST_DISTANCE,
            false,
            "ROLE_USER"
        );

        PolicyChain policyChain = new PolicyChain(List.of(new AccessibilityPolicy(), new TimeWindowPolicy()));
        RouteResult result = dijkstra.findRoute(graph, context, policyChain);

        assertEquals("SUCCESS", result.status());
        assertEquals(30.0, result.totalDistanceMeters());
        assertEquals(4, result.pathNodes().size());
        assertEquals(stairsNode.id(), result.pathNodes().get(2).id());
    }

    @Test
    @DisplayName("Wheelchair user must avoid stairs and take lift route even if longer")
    void testAccessibilityPolicyFiltering() {
        // Stairs path (inaccessible)
        graph.addEdge(new Edge(UUID.randomUUID(), startNode.id(), stairsNode.id(), 10, 8, false, null, null, 1.0, EdgeType.STAIR_CLIMB));
        graph.addEdge(new Edge(UUID.randomUUID(), stairsNode.id(), destNode.id(), 10, 15, false, null, null, 1.0, EdgeType.STAIR_CLIMB));

        // Lift path (accessible)
        graph.addEdge(new Edge(UUID.randomUUID(), startNode.id(), liftNode.id(), 25, 20, true, null, null, 1.0, EdgeType.CORRIDOR_WALK));
        graph.addEdge(new Edge(UUID.randomUUID(), liftNode.id(), destNode.id(), 25, 25, true, null, null, 1.0, EdgeType.LIFT_TRAVEL));

        RoutingContext context = new RoutingContext(
            startNode.id(),
            destNode.id(),
            true, // Wheelchair REQUIRED
            LocalTime.of(10, 0),
            RoutingPreference.SHORTEST_DISTANCE,
            false,
            "ROLE_USER"
        );

        PolicyChain policyChain = new PolicyChain(List.of(new AccessibilityPolicy(), new TimeWindowPolicy()));
        RouteResult result = dijkstra.findRoute(graph, context, policyChain);

        assertEquals("SUCCESS", result.status());
        assertEquals(50.0, result.totalDistanceMeters());
        assertTrue(result.wheelchairAccessible());
        assertEquals(liftNode.id(), result.pathNodes().get(1).id());
    }

    @Test
    @DisplayName("Should return 0 distance instantly when start equals destination")
    void testStartEqualsDestination() {
        RoutingContext context = new RoutingContext(
            startNode.id(),
            startNode.id(),
            false,
            LocalTime.of(10, 0),
            RoutingPreference.SHORTEST_DISTANCE,
            false,
            "ROLE_USER"
        );

        RouteResult result = dijkstra.findRoute(graph, context, new PolicyChain(List.of()));

        assertEquals("SUCCESS", result.status());
        assertEquals(0.0, result.totalDistanceMeters());
        assertEquals(1, result.pathNodes().size());
        assertEquals("You are already at Reception", result.segments().get(0).instruction());
    }

    @Test
    @DisplayName("Should return NO_ROUTE_FOUND when destination is unreachable")
    void testUnreachableDestination() {
        // No edges added to destination node
        RoutingContext context = new RoutingContext(
            startNode.id(),
            destNode.id(),
            false,
            LocalTime.of(10, 0),
            RoutingPreference.SHORTEST_DISTANCE,
            false,
            "ROLE_USER"
        );

        RouteResult result = dijkstra.findRoute(graph, context, new PolicyChain(List.of()));

        assertEquals("NO_ROUTE_FOUND", result.status());
        assertEquals(Double.POSITIVE_INFINITY, result.totalDistanceMeters());
        assertTrue(result.pathNodes().isEmpty());
    }
}
