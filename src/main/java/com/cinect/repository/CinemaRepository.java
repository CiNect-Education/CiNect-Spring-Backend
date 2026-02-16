package com.cinect.repository;

import com.cinect.entity.Cinema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, UUID> {
    Optional<Cinema> findBySlugAndIsActiveTrue(String slug);

    @Query("SELECT c FROM Cinema c WHERE c.isActive = true")
    Page<Cinema> findAllActive(Pageable pageable);

    @Query("SELECT c FROM Cinema c WHERE c.isActive = true AND c.city = :city")
    Page<Cinema> findAllByCity(@Param("city") String city, Pageable pageable);

    @Query("SELECT c FROM Cinema c WHERE c.isActive = true AND LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Cinema> findAllBySearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM Cinema c WHERE c.isActive = true AND c.city = :city AND LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Cinema> findAllByCityAndSearch(@Param("city") String city, @Param("search") String search, Pageable pageable);

    Page<Cinema> findByIsActiveTrue(Pageable pageable);
}
