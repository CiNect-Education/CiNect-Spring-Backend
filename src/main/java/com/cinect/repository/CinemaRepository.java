package com.cinect.repository;

import com.cinect.entity.Cinema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, UUID> {
    @EntityGraph(attributePaths = {"provinceNew"})
    @Query("SELECT c FROM Cinema c WHERE c.slug = :slug AND c.isActive = true")
    Optional<Cinema> findBySlugAndIsActiveTrue(@Param("slug") String slug);

    @EntityGraph(attributePaths = {"provinceNew"})
    @Query("SELECT c FROM Cinema c WHERE c.isActive = true")
    Page<Cinema> findAllActive(Pageable pageable);

    @EntityGraph(attributePaths = {"provinceNew"})
    @Query("SELECT c FROM Cinema c WHERE c.isActive = true AND c.provinceNew.code = :provinceCode")
    Page<Cinema> findAllByProvinceCode(@Param("provinceCode") String provinceCode, Pageable pageable);

    @EntityGraph(attributePaths = {"provinceNew"})
    @Query("SELECT c FROM Cinema c WHERE c.isActive = true AND LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Cinema> findAllBySearch(@Param("search") String search, Pageable pageable);

    @EntityGraph(attributePaths = {"provinceNew"})
    @Query("SELECT c FROM Cinema c WHERE c.isActive = true AND c.provinceNew.code = :provinceCode "
            + "AND LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Cinema> findAllByProvinceCodeAndSearch(
            @Param("provinceCode") String provinceCode,
            @Param("search") String search,
            Pageable pageable);

    Page<Cinema> findByIsActiveTrue(Pageable pageable);

    long countByIsActiveTrue();

    @EntityGraph(attributePaths = {"rooms", "provinceNew"})
    @Query("SELECT DISTINCT c FROM Cinema c LEFT JOIN FETCH c.rooms WHERE c.isActive = true ORDER BY c.name")
    List<Cinema> findAllActiveWithRooms();
}
