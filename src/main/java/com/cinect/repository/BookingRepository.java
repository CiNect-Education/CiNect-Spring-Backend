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

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    /**
     * Admin search without status filter. Do not mix nullable enum + search in one JPQL — PostgreSQL
     * rejects {@code (? is null or status = ?)} for enum parameters (42P18).
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN b.user u " +
           "LEFT JOIN b.showtime st " +
           "LEFT JOIN st.movie m " +
           "WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "(m IS NOT NULL AND LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Booking> searchAdminBookingsNoStatus(@Param("search") String search, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN b.user u " +
           "LEFT JOIN b.showtime st " +
           "LEFT JOIN st.movie m " +
           "WHERE b.status = :status AND (" +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "(m IS NOT NULL AND LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))))")
    Page<Booking> searchAdminBookingsWithStatus(
            @Param("status") BookingStatus status,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'CONFIRMED' " +
           "AND b.createdAt >= :from AND b.createdAt < :to")
    long countConfirmedBetween(@Param("from") Instant from, @Param("to") Instant to);

    @Query("SELECT COALESCE(SUM(b.finalAmount), 0) FROM Booking b WHERE b.status = 'CONFIRMED' " +
           "AND b.createdAt >= :from AND b.createdAt < :to")
    java.math.BigDecimal sumRevenueBetween(@Param("from") Instant from, @Param("to") Instant to);

    List<Booking> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :from AND b.createdAt <= :to")
    long countCreatedBetween(@Param("from") Instant from, @Param("to") Instant to);

    @Query("SELECT b FROM Booking b WHERE b.status IN ('CONFIRMED', 'COMPLETED') AND b.createdAt >= :from")
    List<Booking> findConfirmedOrCompletedSince(@Param("from") Instant from);
}
