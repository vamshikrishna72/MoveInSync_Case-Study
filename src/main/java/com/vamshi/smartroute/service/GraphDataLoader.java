package com.vamshi.smartroute.service;

import com.vamshi.smartroute.entity.EdgeEntity;
import com.vamshi.smartroute.entity.GraphVersionEntity;
import com.vamshi.smartroute.entity.NodeEntity;
import com.vamshi.smartroute.model.Edge;
import com.vamshi.smartroute.model.Graph;
import com.vamshi.smartroute.model.Node;
import com.vamshi.smartroute.repository.EdgeRepository;
import com.vamshi.smartroute.repository.GraphVersionRepository;
import com.vamshi.smartroute.repository.NodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GraphDataLoader {

    private static final Logger log = LoggerFactory.getLogger(GraphDataLoader.class);

    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;
    private final GraphVersionRepository graphVersionRepository;

    private final Graph activeGraph = new Graph();
    private final AtomicLong currentGraphVersion = new AtomicLong(1L);

    public GraphDataLoader(NodeRepository nodeRepository, EdgeRepository edgeRepository, GraphVersionRepository graphVersionRepository) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.graphVersionRepository = graphVersionRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void loadGraphDataOnStartup() {
        reloadGraph();
    }

    public synchronized void reloadGraph() {
        log.info("Reloading campus graph from database into memory...");
        activeGraph.clear();

        List<NodeEntity> nodeEntities = nodeRepository.findAll();
        for (NodeEntity ne : nodeEntities) {
            Node domainNode = new Node(
                ne.getId(),
                ne.getName(),
                ne.getNodeType(),
                ne.getFloor().getId(),
                ne.getFloor().getName(),
                ne.getFloor().getBuilding().getName(),
                ne.getXCoordinate(),
                ne.getYCoordinate()
            );
            activeGraph.addNode(domainNode);
        }

        List<EdgeEntity> edgeEntities = edgeRepository.findAll();
        for (EdgeEntity ee : edgeEntities) {
            Edge domainEdge = new Edge(
                ee.getId(),
                ee.getFromNode().getId(),
                ee.getToNode().getId(),
                ee.getDistanceMeters(),
                ee.getWalkingTimeSeconds(),
                ee.getWheelchairAccessible(),
                ee.getOpenFrom(),
                ee.getOpenUntil(),
                ee.getCongestionMultiplier(),
                ee.getEdgeType()
            );
            activeGraph.addEdge(domainEdge);
        }

        GraphVersionEntity versionEntity = graphVersionRepository.findById(1)
                .orElseGet(() -> graphVersionRepository.save(new GraphVersionEntity(1, 1L, null)));
        currentGraphVersion.set(versionEntity.getVersion());

        log.info("Graph loaded successfully. Nodes: {}, Edges: {}, Version: {}",
                activeGraph.getNodeCount(), activeGraph.getEdgeCount(), currentGraphVersion.get());
    }

    public Graph getActiveGraph() {
        return activeGraph;
    }

    public long getCurrentGraphVersion() {
        return currentGraphVersion.get();
    }
}
