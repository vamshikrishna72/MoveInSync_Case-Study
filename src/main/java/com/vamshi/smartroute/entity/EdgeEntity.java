package com.vamshi.smartroute.entity;

import com.vamshi.smartroute.model.EdgeType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "edges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EdgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_node_id", nullable = false)
    private NodeEntity fromNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_node_id", nullable = false)
    private NodeEntity toNode;

    @Column(name = "distance_meters", nullable = false)
    private Double distanceMeters;

    @Column(name = "walking_time_seconds", nullable = false)
    private Integer walkingTimeSeconds;

    @Column(name = "wheelchair_accessible", nullable = false)
    private Boolean wheelchairAccessible;

    @Column(name = "open_from")
    private LocalTime openFrom;

    @Column(name = "open_until")
    private LocalTime openUntil;

    @Column(name = "congestion_multiplier", nullable = false)
    private Double congestionMultiplier;

    @Enumerated(EnumType.STRING)
    @Column(name = "edge_type", nullable = false, length = 30)
    private EdgeType edgeType;
}
