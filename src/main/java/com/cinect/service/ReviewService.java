package com.cinect.service;

import com.cinect.dto.request.CreateReviewRequest;
import com.cinect.dto.response.ReviewResponse;
import com.cinect.entity.Review;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.MovieRepository;
import com.cinect.repository.ReviewRepository;
import com.cinect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public Page<ReviewResponse> getByMovie(UUID movieId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return reviewRepository.findByMovie_Id(movieId, pageable).map(this::toResponse);
    }

    @Transactional
    public ReviewResponse create(UUID userId, CreateReviewRequest req) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var movie = movieRepository.findById(req.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        var existing = reviewRepository.findByUser_IdAndMovie_Id(userId, req.getMovieId());
        if (existing.isPresent()) {
            throw new BadRequestException("You have already reviewed this movie");
        }
        var review = Review.builder()
                .user(user)
                .movie(movie)
                .rating(req.getRating())
                .content(req.getContent())
                .build();
        review = reviewRepository.save(review);
        updateMovieRating(req.getMovieId());
        return toResponse(review);
    }

    @Transactional
    public ReviewResponse update(UUID reviewId, UUID userId, CreateReviewRequest req) {
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("Not authorized to update this review");
        }
        review.setRating(req.getRating());
        review.setContent(req.getContent());
        review = reviewRepository.save(review);
        updateMovieRating(review.getMovie().getId());
        return toResponse(review);
    }

    private void updateMovieRating(UUID movieId) {
        var avg = reviewRepository.getAverageRating(movieId);
        var count = reviewRepository.countByMovie_Id(movieId);
        movieRepository.findById(movieId).ifPresent(m -> {
            m.setRating(avg != null ? BigDecimal.valueOf(avg) : BigDecimal.ZERO);
            m.setRatingCount((int) count);
            movieRepository.save(m);
        });
    }

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .movieId(r.getMovie().getId())
                .userId(r.getUser().getId())
                .userFullName(r.getUser().getFullName())
                .rating(r.getRating())
                .content(r.getContent())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
