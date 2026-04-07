package com.cinect.repository;

import com.cinect.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    List<Campaign> findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Instant now1, Instant now2);
    Optional<Campaign> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    /**
     * Eager-fetch banners for admin list (open-in-view is false; avoids LazyInitializationException).
     * No DISTINCT — JOIN FETCH on collections can duplicate roots; dedupe in service.
     */
    @Query("SELECT c FROM Campaign c LEFT JOIN FETCH c.banners")
    List<Campaign> findAllWithBannersForAdmin();
}
