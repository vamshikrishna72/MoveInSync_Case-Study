package com.vamshi.smartroute.dto;

import com.vamshi.smartroute.model.NodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NodeDto(
    UUID id,
    @NotBlank(message = "Node name cannot be blank")
    String name,
    @NotNull(message = "NodeType is required")
    NodeType type,
    @NotNull(message = "floorId is required")
    UUID floorId,
    double xCoordinate,
    double yCoordinate
) {}
