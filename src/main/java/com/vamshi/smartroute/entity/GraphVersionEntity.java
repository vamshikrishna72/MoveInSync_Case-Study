package com.vamshi.smartroute.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "graph_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraphVersionEntity {

    @Id
    private Integer id = 1;

    @Column(nullable = false)
    private Long version = 1L;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }
}
