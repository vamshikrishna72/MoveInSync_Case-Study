package com.vamshi.smartroute.repository;

import com.vamshi.smartroute.entity.EdgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EdgeRepository extends JpaRepository<EdgeEntity, UUID> {
    List<EdgeEntity> findByFromNodeId(UUID fromNodeId);
}
