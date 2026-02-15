package com.cinect.repository;

import com.cinect.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByMovie_Id(UUID movieId, Pageable pageable);
    Optional<Review> findByUser_IdAndMovie_Id(UUID userId, UUID movieId);
    long countByMovie_Id(UUID movieId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie.id = :movieId")
    Double getAverageRating(@Param("movieId") UUID movieId);
}
