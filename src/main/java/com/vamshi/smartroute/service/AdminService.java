package com.vamshi.smartroute.service;

import com.vamshi.smartroute.dto.EdgeDto;
import com.vamshi.smartroute.dto.NodeDto;
import com.vamshi.smartroute.entity.EdgeEntity;
import com.vamshi.smartroute.entity.FloorEntity;
import com.vamshi.smartroute.entity.NodeEntity;
import com.vamshi.smartroute.repository.EdgeRepository;
import com.vamshi.smartroute.repository.FloorRepository;
import com.vamshi.smartroute.repository.GraphVersionRepository;
import com.vamshi.smartroute.repository.NodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AdminService {

    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;
    private final FloorRepository floorRepository;
    private final GraphVersionRepository graphVersionRepository;
    private final GraphDataLoader graphDataLoader;

    public AdminService(NodeRepository nodeRepository, EdgeRepository edgeRepository,
                        FloorRepository floorRepository, GraphVersionRepository graphVersionRepository,
                        GraphDataLoader graphDataLoader) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.floorRepository = floorRepository;
        this.graphVersionRepository = graphVersionRepository;
        this.graphDataLoader = graphDataLoader;
    }

    @Transactional
    public NodeDto createNode(NodeDto dto) {
        FloorEntity floor = floorRepository.findById(dto.floorId())
                .orElseThrow(() -> new IllegalArgumentException("Floor not found with ID: " + dto.floorId()));

        NodeEntity node = NodeEntity.builder()
                .name(dto.name())
                .nodeType(dto.type())
                .floor(floor)
                .xCoordinate(dto.xCoordinate())
                .yCoordinate(dto.yCoordinate())
                .build();

        NodeEntity saved = nodeRepository.save(node);
        incrementGraphVersionAndReload();

        return new NodeDto(saved.getId(), saved.getName(), saved.getNodeType(), floor.getId(), saved.getXCoordinate(), saved.getYCoordinate());
    }

    @Transactional
    public EdgeDto createEdge(EdgeDto dto) {
        NodeEntity fromNode = nodeRepository.findById(dto.fromNodeId())
                .orElseThrow(() -> new IllegalArgumentException("fromNode not found with ID: " + dto.fromNodeId()));
        NodeEntity toNode = nodeRepository.findById(dto.toNodeId())
                .orElseThrow(() -> new IllegalArgumentException("toNode not found with ID: " + dto.toNodeId()));

        EdgeEntity edge = EdgeEntity.builder()
                .fromNode(fromNode)
                .toNode(toNode)
                .distanceMeters(dto.distanceMeters())
                .walkingTimeSeconds(dto.walkingTimeSeconds())
                .wheelchairAccessible(dto.wheelchairAccessible())
                .openFrom(dto.openFrom())
                .openUntil(dto.openUntil())
                .congestionMultiplier(dto.congestionMultiplier() <= 0 ? 1.0 : dto.congestionMultiplier())
                .edgeType(dto.edgeType())
                .build();

        EdgeEntity saved = edgeRepository.save(edge);
        incrementGraphVersionAndReload();

        return new EdgeDto(
            saved.getId(), saved.getFromNode().getId(), saved.getToNode().getId(),
            saved.getDistanceMeters(), saved.getWalkingTimeSeconds(), saved.getWheelchairAccessible(),
            saved.getOpenFrom(), saved.getOpenUntil(), saved.getCongestionMultiplier(), saved.getEdgeType()
        );
    }

    @Transactional
    public void updateEdgeCongestion(UUID edgeId, double congestionMultiplier) {
        EdgeEntity edge = edgeRepository.findById(edgeId)
                .orElseThrow(() -> new IllegalArgumentException("Edge not found with ID: " + edgeId));

        edge.setCongestionMultiplier(congestionMultiplier);
        edgeRepository.save(edge);
        incrementGraphVersionAndReload();
    }

    private void incrementGraphVersionAndReload() {
        graphVersionRepository.incrementVersion();
        graphDataLoader.reloadGraph();
    }
}
