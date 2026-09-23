package com.vamshi.smartroute.algorithm;

import com.vamshi.smartroute.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms an ordered trajectory of nodes and edges into human-readable turn-by-turn instructions.
 */
public class InstructionGenerator {

    public List<RouteSegment> generateInstructions(Graph graph, List<Node> pathNodes, List<Edge> pathEdges) {
        if (pathNodes == null || pathNodes.isEmpty()) {
            return List.of();
        }

        if (pathNodes.size() == 1) {
            Node singleNode = pathNodes.get(0);
            return List.of(new RouteSegment(
                1,
                singleNode.id(),
                singleNode.name(),
                singleNode.id(),
                singleNode.name(),
                "Start at " + singleNode.name(),
                0.0,
                0,
                singleNode.floorName(),
                singleNode.buildingName(),
                EdgeType.CORRIDOR_WALK
            ));
        }

        List<RouteSegment> segments = new ArrayList<>();
        for (int i = 0; i < pathEdges.size(); i++) {
            Edge edge = pathEdges.get(i);
            Node fromNode = pathNodes.get(i);
            Node toNode = pathNodes.get(i + 1);
            int stepNumber = i + 1;

            String instructionText = buildInstructionText(fromNode, toNode, edge, stepNumber == 1, i == pathEdges.size() - 1);

            segments.add(new RouteSegment(
                stepNumber,
                fromNode.id(),
                fromNode.name(),
                toNode.id(),
                toNode.name(),
                instructionText,
                edge.distanceMeters(),
                edge.walkingTimeSeconds(),
                toNode.floorName(),
                toNode.buildingName(),
                edge.edgeType()
            ));
        }

        return segments;
    }

    private String buildInstructionText(Node fromNode, Node toNode, Edge edge, boolean isFirstStep, boolean isLastStep) {
        StringBuilder sb = new StringBuilder();

        switch (edge.edgeType()) {
            case LIFT_TRAVEL -> {
                sb.append("Take ").append(fromNode.name())
                  .append(" to ").append(toNode.floorName())
                  .append(" (").append(toNode.name()).append(")");
            }
            case STAIR_CLIMB -> {
                sb.append("Climb stairs from ").append(fromNode.name())
                  .append(" to ").append(toNode.floorName())
                  .append(" (").append(toNode.name()).append(")");
            }
            case BRIDGE_CONNECTOR -> {
                sb.append("Cross skywalk connector from ").append(fromNode.buildingName())
                  .append(" to ").append(toNode.buildingName());
            }
            default -> {
                if (isFirstStep) {
                    sb.append("Start at ").append(fromNode.name())
                      .append(" and walk along ").append(toNode.name())
                      .append(" for ").append((int) edge.distanceMeters()).append(" meters");
                } else if (isLastStep) {
                    sb.append("Continue along ").append(edge.edgeType().name().toLowerCase().replace('_', ' '))
                      .append(" to reach ").append(toNode.name());
                } else {
                    sb.append("Proceed to ").append(toNode.name())
                      .append(" (").append((int) edge.distanceMeters()).append(" meters)");
                }
            }
        }

        return sb.toString();
    }
}
