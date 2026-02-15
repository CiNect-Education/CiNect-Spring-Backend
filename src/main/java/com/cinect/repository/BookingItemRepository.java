package com.cinect.repository;

import com.cinect.entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingItemRepository extends JpaRepository<BookingItem, UUID> {
    List<BookingItem> findByBookingId(UUID bookingId);

    @Query("SELECT bi.seat.id FROM BookingItem bi " +
           "WHERE bi.showtime.id = :showtimeId " +
           "AND bi.booking.status NOT IN ('CANCELLED')")
    List<UUID> findBookedSeatIds(@Param("showtimeId") UUID showtimeId);
}
