package com.cinect.repository;

import com.cinect.entity.Showtime;
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
public interface ShowtimeRepository extends JpaRepository<Showtime, UUID> {

    @Query("SELECT s FROM Showtime s WHERE s.isActive = true " +
           "AND (:movieId IS NULL OR s.movie.id = :movieId) " +
           "AND (:cinemaId IS NULL OR s.cinema.id = :cinemaId) " +
           "AND s.startTime >= :startFrom " +
           "AND s.startTime < :startTo " +
           "ORDER BY s.startTime")
    List<Showtime> findFiltered(@Param("movieId") UUID movieId,
                                @Param("cinemaId") UUID cinemaId,
                                @Param("startFrom") Instant startFrom,
                                @Param("startTo") Instant startTo);

    @Query("SELECT s FROM Showtime s WHERE s.isActive = true " +
           "AND s.movie.id = :movieId AND s.startTime >= :now ORDER BY s.startTime")
    List<Showtime> findUpcomingByMovie(@Param("movieId") UUID movieId, @Param("now") Instant now);

    @Query("SELECT s FROM Showtime s WHERE s.isActive = true " +
           "AND s.cinema.id = :cinemaId AND s.startTime >= :now ORDER BY s.startTime")
    List<Showtime> findUpcomingByCinema(@Param("cinemaId") UUID cinemaId, @Param("now") Instant now);

    @Query("SELECT s FROM Showtime s WHERE s.room.id = :roomId " +
           "AND s.isActive = true " +
           "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<Showtime> findConflicting(@Param("roomId") UUID roomId,
                                   @Param("startTime") Instant startTime,
                                   @Param("endTime") Instant endTime);

    Page<Showtime> findByIsActiveTrue(Pageable pageable);
}
