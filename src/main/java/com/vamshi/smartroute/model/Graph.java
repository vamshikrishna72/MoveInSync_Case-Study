package com.vamshi.smartroute.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory graph representation of the campus layout.
 * Uses an Adjacency List representation for O(V + E) memory footprint.
 */
public class Graph {
    private final Map<UUID, Node> nodes = new ConcurrentHashMap<>();
    private final Map<UUID, List<Edge>> adjacencyList = new ConcurrentHashMap<>();

    public void addNode(Node node) {
        Objects.requireNonNull(node, "Node cannot be null");
        nodes.put(node.id(), node);
        adjacencyList.putIfAbsent(node.id(), Collections.synchronizedList(new ArrayList<>()));
    }

    public void addEdge(Edge edge) {
        Objects.requireNonNull(edge, "Edge cannot be null");
        if (!nodes.containsKey(edge.fromNodeId())) {
            throw new IllegalArgumentException("Source node " + edge.fromNodeId() + " does not exist in graph");
        }
        if (!nodes.containsKey(edge.toNodeId())) {
            throw new IllegalArgumentException("Destination node " + edge.toNodeId() + " does not exist in graph");
        }
        adjacencyList.get(edge.fromNodeId()).add(edge);
    }

    public Node getNode(UUID nodeId) {
        return nodes.get(nodeId);
    }

    public boolean containsNode(UUID nodeId) {
        return nodes.containsKey(nodeId);
    }

    public List<Edge> getOutgoingEdges(UUID nodeId) {
        List<Edge> edges = adjacencyList.get(nodeId);
        if (edges == null) {
            return Collections.emptyList();
        }
        synchronized (edges) {
            return List.copyOf(edges);
        }
    }

    public Map<UUID, Node> getNodes() {
        return Collections.unmodifiableMap(nodes);
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return adjacencyList.values().stream().mapToInt(List::size).sum();
    }

    public void clear() {
        nodes.clear();
        adjacencyList.clear();
    }
}
