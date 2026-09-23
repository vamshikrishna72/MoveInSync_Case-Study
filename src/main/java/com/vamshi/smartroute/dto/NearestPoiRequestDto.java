package com.vamshi.smartroute.dto;

import com.vamshi.smartroute.model.NodeType;
import com.vamshi.smartroute.model.RoutingPreference;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

public record NearestPoiRequestDto(
    @NotNull(message = "startNodeId is required")
    UUID startNodeId,

    @NotNull(message = "poiType is required")
    NodeType poiType,

    boolean wheelchairRequired,

    LocalTime requestedTime,

    RoutingPreference preference,

    boolean considerCongestion
) {}
