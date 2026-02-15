package com.cinect.repository;

import com.cinect.entity.PointsHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PointsHistoryRepository extends JpaRepository<PointsHistory, UUID> {
    Page<PointsHistory> findByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
