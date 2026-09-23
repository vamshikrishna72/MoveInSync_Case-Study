package com.vamshi.smartroute.repository;

import com.vamshi.smartroute.entity.BuildingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BuildingRepository extends JpaRepository<BuildingEntity, UUID> {
    Optional<BuildingEntity> findByCode(String code);
}
