package com.cinect.repository;

import com.cinect.entity.Hold;
import com.cinect.entity.enums.HoldStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HoldRepository extends JpaRepository<Hold, UUID> {

    @Query("SELECT h FROM Hold h WHERE h.showtime.id = :showtimeId AND h.status = 'ACTIVE' AND h.expiresAt > :now")
    List<Hold> findActiveByShowtime(@Param("showtimeId") UUID showtimeId, @Param("now") Instant now);

    @Query("SELECT h FROM Hold h WHERE h.user.id = :userId AND h.showtime.id = :showtimeId AND h.status = 'ACTIVE' AND h.expiresAt > :now")
    Optional<Hold> findActiveByUserAndShowtime(@Param("userId") UUID userId,
                                                @Param("showtimeId") UUID showtimeId,
                                                @Param("now") Instant now);

    List<Hold> findByStatusAndExpiresAtBefore(HoldStatus status, Instant now);

    @Query("SELECT h FROM Hold h WHERE h.status = 'ACTIVE' AND h.expiresAt < :now")
    List<Hold> findExpiredActive(@Param("now") Instant now);

    @Modifying
    @Query("UPDATE Hold h SET h.status = 'EXPIRED' WHERE h.status = 'ACTIVE' AND h.expiresAt < :now")
    int expireOldHolds(@Param("now") Instant now);
}
