package com.vamshi.smartroute.repository;

import com.vamshi.smartroute.entity.GraphVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphVersionRepository extends JpaRepository<GraphVersionEntity, Integer> {

    @Modifying
    @Query("UPDATE GraphVersionEntity g SET g.version = g.version + 1 WHERE g.id = 1")
    int incrementVersion();
}
