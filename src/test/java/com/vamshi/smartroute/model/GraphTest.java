package com.vamshi.smartroute.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    private Graph graph;
    private Node receptionNode;
    private Node corridorNode;
    private UUID floorId;

    @BeforeEach
    void setUp() {
        graph = new Graph();
        floorId = UUID.randomUUID();

        receptionNode = new Node(
            UUID.randomUUID(),
            "Reception",
            NodeType.ENTRANCE,
            floorId,
            "Floor 1",
            "Main Block",
            10.0,
            20.0
        );

        corridorNode = new Node(
            UUID.randomUUID(),
            "Corridor A",
            NodeType.CORRIDOR,
            floorId,
            "Floor 1",
            "Main Block",
            15.0,
            25.0
        );
    }

    @Test
    @DisplayName("Should add nodes to graph successfully")
    void testAddNode() {
        graph.addNode(receptionNode);
        graph.addNode(corridorNode);

        assertEquals(2, graph.getNodeCount());
        assertTrue(graph.containsNode(receptionNode.id()));
        assertEquals("Reception", graph.getNode(receptionNode.id()).name());
    }

    @Test
    @DisplayName("Should add edge between existing nodes")
    void testAddEdgeSuccess() {
        graph.addNode(receptionNode);
        graph.addNode(corridorNode);

        Edge edge = new Edge(
            UUID.randomUUID(),
            receptionNode.id(),
            corridorNode.id(),
            50.0,
            42,
            true,
            LocalTime.of(8, 0),
            LocalTime.of(20, 0),
            1.0,
            EdgeType.CORRIDOR_WALK
        );

        graph.addEdge(edge);

        assertEquals(1, graph.getEdgeCount());
        List<Edge> outgoing = graph.getOutgoingEdges(receptionNode.id());
        assertEquals(1, outgoing.size());
        assertEquals(corridorNode.id(), outgoing.get(0).toNodeId());
    }

    @Test
    @DisplayName("Should throw exception when adding edge for non-existent node")
    void testAddEdgeMissingNode() {
        graph.addNode(receptionNode);
        UUID nonExistentNodeId = UUID.randomUUID();

        Edge invalidEdge = new Edge(
            UUID.randomUUID(),
            receptionNode.id(),
            nonExistentNodeId,
            30.0,
            25,
            true,
            null,
            null,
            1.0,
            EdgeType.CORRIDOR_WALK
        );

        assertThrows(IllegalArgumentException.class, () -> graph.addEdge(invalidEdge));
    }

    @Test
    @DisplayName("Should correctly identify POI node types")
    void testPoiNodeType() {
        Node washroomNode = new Node(
            UUID.randomUUID(),
            "Main Washroom",
            NodeType.WASHROOM,
            floorId,
            "Floor 1",
            "Main Block",
            5.0,
            5.0
        );

        assertTrue(washroomNode.isPoi());
        assertFalse(receptionNode.isPoi());
    }
}
