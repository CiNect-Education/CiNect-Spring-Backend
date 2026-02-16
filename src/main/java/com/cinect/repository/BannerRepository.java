package com.cinect.repository;

import com.cinect.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BannerRepository extends JpaRepository<Banner, UUID> {
    List<Banner> findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderBySortOrderAsc(Instant now1, Instant now2);
    List<Banner> findByPositionAndIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderBySortOrderAsc(String position, Instant now1, Instant now2);
}
