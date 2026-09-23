package com.vamshi.smartroute.repository;

import com.vamshi.smartroute.entity.NodeEntity;
import com.vamshi.smartroute.model.NodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NodeRepository extends JpaRepository<NodeEntity, UUID> {
    List<NodeEntity> findByNodeType(NodeType nodeType);
}
