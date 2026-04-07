package com.cinect.repository;

import com.cinect.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BannerRepository extends JpaRepository<Banner, UUID> {
    List<Banner> findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderBySortOrderAsc(Instant now1, Instant now2);
    List<Banner> findByPositionAndIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderBySortOrderAsc(String position, Instant now1, Instant now2);

    /**
     * Eager-fetch campaign for admin list (open-in-view is false; avoids LazyInitializationException).
     * Sorting is done in service — avoid ORDER BY on attribute "position" (reserved in JPQL/HQL).
     */
    @Query("SELECT b FROM Banner b LEFT JOIN FETCH b.campaign")
    List<Banner> findAllWithCampaignForAdmin();
}
