package com.cinect.repository;

import com.cinect.entity.PricingRule;
import com.cinect.entity.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, UUID> {
    List<PricingRule> findByIsActiveTrue();

    @Query("SELECT p FROM PricingRule p WHERE p.isActive = true " +
           "AND (p.cinema.id = :cinemaId OR p.cinema IS NULL) " +
           "AND (p.seatType = :seatType OR p.seatType IS NULL) " +
           "AND (p.format = :format OR p.format IS NULL) " +
           "AND (p.dayType = :dayType OR p.dayType IS NULL) " +
           "AND (p.timeSlot = :timeSlot OR p.timeSlot IS NULL) " +
           "ORDER BY p.cinema.id NULLS LAST, p.seatType NULLS LAST, " +
           "p.format NULLS LAST, p.dayType NULLS LAST, p.timeSlot NULLS LAST")
    List<PricingRule> findMatchingRules(@Param("cinemaId") UUID cinemaId,
                                        @Param("seatType") SeatType seatType,
                                        @Param("format") RoomFormat format,
                                        @Param("dayType") DayType dayType,
                                        @Param("timeSlot") TimeSlot timeSlot);
}
