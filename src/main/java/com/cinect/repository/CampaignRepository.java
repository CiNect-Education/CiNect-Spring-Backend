package com.cinect.repository;

import com.cinect.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    List<Campaign> findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Instant now1, Instant now2);
    Optional<Campaign> findBySlug(String slug);
}
