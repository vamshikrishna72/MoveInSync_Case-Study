package com.vamshi.smartroute.dto;

import com.vamshi.smartroute.model.EdgeType;

import java.util.UUID;

public record InstructionDto(
    int step,
    UUID fromNodeId,
    String fromNodeName,
    UUID toNodeId,
    String toNodeName,
    String instruction,
    double distanceMeters,
    int travelTimeSeconds,
    String floorName,
    String buildingName,
    EdgeType edgeType
) {}
