package com.cinect.repository;

import com.cinect.entity.HoldSeat;
import com.cinect.entity.HoldSeatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface HoldSeatRepository extends JpaRepository<HoldSeat, HoldSeatId> {

    @Query("SELECT hs.seatId FROM HoldSeat hs " +
           "JOIN Hold h ON h.id = hs.holdId " +
           "WHERE hs.showtimeId = :showtimeId AND h.status = 'ACTIVE' AND h.expiresAt > :now")
    List<UUID> findHeldSeatIds(@Param("showtimeId") UUID showtimeId, @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM HoldSeat hs WHERE hs.holdId = :holdId")
    void deleteByHoldId(@Param("holdId") UUID holdId);
}
