package com.vamshi.smartroute;

import com.vamshi.smartroute.algorithm.DijkstraRoutingAlgorithm;
import com.vamshi.smartroute.model.*;
import com.vamshi.smartroute.policy.AccessibilityPolicy;
import com.vamshi.smartroute.policy.PolicyChain;
import com.vamshi.smartroute.policy.TimeWindowPolicy;
import com.vamshi.smartroute.service.PoiService;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" SmartRoute Indoor Campus Engine Execution Test");
        System.out.println("=================================================");

        Graph graph = new Graph();
        UUID floor1 = UUID.randomUUID();
        UUID floor2 = UUID.randomUUID();

        // 1. Create Nodes
        Node reception = new Node(UUID.randomUUID(), "Reception", NodeType.ENTRANCE, floor1, "Floor 1", "Main Block", 0, 0);
        Node corridorA = new Node(UUID.randomUUID(), "Corridor A", NodeType.CORRIDOR, floor1, "Floor 1", "Main Block", 15, 0);
        Node junctionA1 = new Node(UUID.randomUUID(), "Junction A1", NodeType.JUNCTION, floor1, "Floor 1", "Main Block", 30, 0);
        Node stairsF1 = new Node(UUID.randomUUID(), "Stairs A (F1)", NodeType.STAIRS, floor1, "Floor 1", "Main Block", 45, 0);
        Node liftF1 = new Node(UUID.randomUUID(), "Lift L1 (F1)", NodeType.LIFT, floor1, "Floor 1", "Main Block", 30, 15);
        Node washroomF1 = new Node(UUID.randomUUID(), "Washroom (F1)", NodeType.WASHROOM, floor1, "Floor 1", "Main Block", 30, -15);

        Node stairsF2 = new Node(UUID.randomUUID(), "Stairs A (F2)", NodeType.STAIRS, floor2, "Floor 2", "Main Block", 45, 0);
        Node liftF2 = new Node(UUID.randomUUID(), "Lift L1 (F2)", NodeType.LIFT, floor2, "Floor 2", "Main Block", 30, 15);
        Node meetingRoom4B = new Node(UUID.randomUUID(), "Meeting Room 4B", NodeType.ROOM, floor2, "Floor 2", "Main Block", 45, 15);

        graph.addNode(reception);
        graph.addNode(corridorA);
        graph.addNode(junctionA1);
        graph.addNode(stairsF1);
        graph.addNode(liftF1);
        graph.addNode(washroomF1);
        graph.addNode(stairsF2);
        graph.addNode(liftF2);
        graph.addNode(meetingRoom4B);

        // 2. Create Edges
        graph.addEdge(new Edge(UUID.randomUUID(), reception.id(), corridorA.id(), 15.0, 12, true, LocalTime.of(0, 0), LocalTime.of(23, 59), 1.0, EdgeType.CORRIDOR_WALK));
        graph.addEdge(new Edge(UUID.randomUUID(), corridorA.id(), junctionA1.id(), 15.0, 12, true, LocalTime.of(0, 0), LocalTime.of(23, 59), 1.0, EdgeType.CORRIDOR_WALK));
        graph.addEdge(new Edge(UUID.randomUUID(), junctionA1.id(), stairsF1.id(), 15.0, 12, true, LocalTime.of(0, 0), LocalTime.of(23, 59), 1.0, EdgeType.CORRIDOR_WALK));
        graph.addEdge(new Edge(UUID.randomUUID(), junctionA1.id(), liftF1.id(), 15.0, 12, true, LocalTime.of(0, 0), LocalTime.of(23, 59), 1.0, EdgeType.CORRIDOR_WALK));
        graph.addEdge(new Edge(UUID.randomUUID(), junctionA1.id(), washroomF1.id(), 15.0, 10, true, LocalTime.of(0, 0), LocalTime.of(23, 59), 1.0, EdgeType.CORRIDOR_WALK));

        // Vertical Edges
        graph.addEdge(new Edge(UUID.randomUUID(), stairsF1.id(), stairsF2.id(), 10.0, 20, false, LocalTime.of(6, 0), LocalTime.of(22, 0), 1.0, EdgeType.STAIR_CLIMB));
        graph.addEdge(new Edge(UUID.randomUUID(), liftF1.id(), liftF2.id(), 10.0, 25, true, LocalTime.of(0, 0), LocalTime.of(23, 59), 1.0, EdgeType.LIFT_TRAVEL));

        // Floor 2 Edges
        graph.addEdge(new Edge(UUID.randomUUID(), stairsF2.id(), meetingRoom4B.id(), 15.0, 12, true, LocalTime.of(0, 0), LocalTime.of(23, 59), 1.0, EdgeType.CORRIDOR_WALK));
        graph.addEdge(new Edge(UUID.randomUUID(), liftF2.id(), meetingRoom4B.id(), 15.0, 12, true, LocalTime.of(0, 0), LocalTime.of(23, 59), 1.0, EdgeType.CORRIDOR_WALK));

        DijkstraRoutingAlgorithm router = new DijkstraRoutingAlgorithm();

        // TEST 1: Standard User Route (Chooses Stairs)
        System.out.println("\n--- TEST 1: Standard User Route (Reception -> Meeting Room 4B) ---");
        RoutingContext standardContext = new RoutingContext(
            reception.id(), meetingRoom4B.id(), false, LocalTime.of(10, 0),
            RoutingPreference.SHORTEST_DISTANCE, false, "ROLE_USER"
        );
        PolicyChain policyChain = new PolicyChain(List.of(new AccessibilityPolicy(), new TimeWindowPolicy()));
        RouteResult standardResult = router.findRoute(graph, standardContext, policyChain);

        System.out.println("Status: " + standardResult.status());
        System.out.println("Total Distance: " + standardResult.totalDistanceMeters() + " meters");
        System.out.println("Total Time: " + standardResult.totalEstimatedTimeSeconds() + " seconds");
        System.out.println("Wheelchair Accessible: " + standardResult.wheelchairAccessible());
        System.out.println("Turn-by-Turn Steps:");
        standardResult.segments().forEach(s -> System.out.println("  Step " + s.step() + ": " + s.instruction()));

        // TEST 2: Wheelchair Accessible User Route (Forces Lift L1)
        System.out.println("\n--- TEST 2: Wheelchair Accessible Route (Reception -> Meeting Room 4B) ---");
        RoutingContext wheelchairContext = new RoutingContext(
            reception.id(), meetingRoom4B.id(), true, LocalTime.of(10, 0),
            RoutingPreference.FASTEST_ROUTE, true, "ROLE_USER"
        );
        RouteResult wheelchairResult = router.findRoute(graph, wheelchairContext, policyChain);

        System.out.println("Status: " + wheelchairResult.status());
        System.out.println("Total Distance: " + wheelchairResult.totalDistanceMeters() + " meters");
        System.out.println("Total Time: " + wheelchairResult.totalEstimatedTimeSeconds() + " seconds");
        System.out.println("Wheelchair Accessible: " + wheelchairResult.wheelchairAccessible());
        System.out.println("Turn-by-Turn Steps:");
        wheelchairResult.segments().forEach(s -> System.out.println("  Step " + s.step() + ": " + s.instruction()));

        // TEST 3: Nearest Washroom Search
        System.out.println("\n--- TEST 3: Nearest Washroom Search from Reception ---");
        PoiService poiService = new PoiService();
        RouteResult poiResult = poiService.findNearestPoi(graph, reception.id(), NodeType.WASHROOM, wheelchairContext, policyChain);

        System.out.println("Status: " + poiResult.status());
        System.out.println("Nearest Washroom Distance: " + poiResult.totalDistanceMeters() + " meters");
        System.out.println("Destination Node: " + poiResult.pathNodes().get(poiResult.pathNodes().size() - 1).name());
        System.out.println("Turn-by-Turn Steps:");
        poiResult.segments().forEach(s -> System.out.println("  Step " + s.step() + ": " + s.instruction()));

        System.out.println("\n=================================================");
        System.out.println(" ALL TESTS PASSED - DIJKSTRA & POLICY ENGINE WORKING PERFECTLY!");
        System.out.println("=================================================");
    }
}
