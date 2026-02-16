package com.cinect.repository;

import com.cinect.entity.Booking;
import com.cinect.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Page<Booking> findByUserId(UUID userId, Pageable pageable);
    Page<Booking> findByUserIdAndStatus(UUID userId, BookingStatus status, Pageable pageable);
    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, Instant now);

    @Query("SELECT b FROM Booking b WHERE " +
           "(:status IS NULL OR b.status = :status) " +
           "AND (:search IS NULL OR CAST(b.id AS string) LIKE CONCAT('%', :search, '%'))")
    Page<Booking> findAllFiltered(@Param("status") BookingStatus status,
                                  @Param("search") String search,
                                  Pageable pageable);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'CONFIRMED' " +
           "AND b.createdAt >= :from AND b.createdAt < :to")
    long countConfirmedBetween(@Param("from") Instant from, @Param("to") Instant to);

    @Query("SELECT COALESCE(SUM(b.finalAmount), 0) FROM Booking b WHERE b.status = 'CONFIRMED' " +
           "AND b.createdAt >= :from AND b.createdAt < :to")
    java.math.BigDecimal sumRevenueBetween(@Param("from") Instant from, @Param("to") Instant to);

    List<Booking> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
