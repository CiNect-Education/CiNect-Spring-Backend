package com.cinect.repository;

import com.cinect.entity.Promotion;
import com.cinect.entity.enums.PromotionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {
    Optional<Promotion> findByCode(String code);

    Optional<Promotion> findByCodeAndStatus(String code, PromotionStatus status);

    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' AND p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findActivePromotions(@Param("now") Instant now);

    @Query("SELECT p FROM Promotion p WHERE p.isTrending = true AND p.status = 'ACTIVE'")
    List<Promotion> findTrending();

    Page<Promotion> findByStatus(PromotionStatus status, Pageable pageable);
}
