package com.cinect.repository;

import com.cinect.entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingItemRepository extends JpaRepository<BookingItem, UUID> {
    List<BookingItem> findByBookingId(UUID bookingId);

    @Query("SELECT bi.seat.id FROM BookingItem bi " +
           "WHERE bi.showtime.id = :showtimeId " +
           "AND bi.booking.status NOT IN ('CANCELLED')")
    List<UUID> findBookedSeatIds(@Param("showtimeId") UUID showtimeId);

    @Query("SELECT COUNT(bi) FROM BookingItem bi WHERE bi.booking.status IN ('CONFIRMED', 'COMPLETED') " +
           "AND bi.showtime.isActive = true AND bi.showtime.startTime >= :since")
    long countBookedItemsForActiveShowtimesSince(@Param("since") Instant since);

    @Query("SELECT COALESCE(SUM(r.totalSeats), 0) FROM Showtime s JOIN s.room r " +
           "WHERE s.isActive = true AND s.startTime >= :since")
    long sumRoomCapacityForShowtimesSince(@Param("since") Instant since);

    @Query("SELECT bi.showtime.id, COUNT(bi) FROM BookingItem bi WHERE bi.showtime.id IN :ids " +
           "AND bi.booking.status IN ('CONFIRMED', 'COMPLETED') GROUP BY bi.showtime.id")
    List<Object[]> countBookedItemsByShowtimeIds(@Param("ids") List<UUID> ids);
}
