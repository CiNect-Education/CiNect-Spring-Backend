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

    @Query(value = "SELECT DISTINCT c FROM Cinema c LEFT JOIN FETCH c.rooms WHERE c.isActive = true " +
           "AND (:city IS NULL OR c.city = :city) " +
           "AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))",
           countQuery = "SELECT COUNT(c) FROM Cinema c WHERE c.isActive = true " +
           "AND (:city IS NULL OR c.city = :city) " +
           "AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Cinema> findAllFiltered(@Param("city") String city,
                                 @Param("search") String search,
                                 Pageable pageable);

    Page<Cinema> findByIsActiveTrue(Pageable pageable);
}
