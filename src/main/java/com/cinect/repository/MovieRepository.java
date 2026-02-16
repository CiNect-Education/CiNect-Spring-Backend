package com.cinect.repository;

import com.cinect.entity.Movie;
import com.cinect.entity.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    Optional<Movie> findBySlugAndIsDeletedFalse(String slug);

    @Query("SELECT m FROM Movie m WHERE m.isDeleted = false")
    Page<Movie> findAllActive(Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.isDeleted = false AND m.status = :status")
    Page<Movie> findAllByStatus(@Param("status") MovieStatus status, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.isDeleted = false AND LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Movie> findAllBySearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.isDeleted = false AND m.status = :status AND LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Movie> findAllByStatusAndSearch(@Param("status") MovieStatus status, @Param("search") String search, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId AND m.isDeleted = false")
    Page<Movie> findByGenre(@Param("genreId") UUID genreId, Pageable pageable);

    Page<Movie> findByStatusAndIsDeletedFalse(MovieStatus status, Pageable pageable);
}
