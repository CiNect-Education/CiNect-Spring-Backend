package com.cinect.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.MovieResponse;
import com.cinect.dto.response.PageMeta;
import com.cinect.dto.response.ReviewResponse;
import com.cinect.dto.response.ShowtimeResponse;
import com.cinect.entity.enums.MovieStatus;
import com.cinect.entity.enums.AgeRating;
import com.cinect.security.UserPrincipal;
import com.cinect.service.MovieService;
import com.cinect.service.ReviewService;
import com.cinect.service.ShowtimeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final ReviewService reviewService;
    private final ShowtimeService showtimeService;

    /**
     * {@code page} is 1-based (same as NestJS {@code GET /movies}) — Spring Data uses 0-based internally.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponse>>> findAll(
            @RequestParam(required = false) MovieStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) AgeRating ageRating,
            @RequestParam(required = false) Integer durationMin,
            @RequestParam(required = false) Integer durationMax,
            @RequestParam(required = false) String format,
            @RequestParam(required = false, defaultValue = "releaseDate:desc") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        int pageIndex = Math.max(0, page - 1);
        var data = movieService.findAll(
                status, search, genre, language, ageRating, durationMin, durationMax, format, sort, pageIndex, limit
        );
        var meta = PageMeta.builder()
                .page(page)
                .limit(limit)
                .total(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .hasNext(data.hasNext())
                .hasPrev(data.hasPrevious())
                .build();
        return ResponseEntity.ok(ApiResponse.success(data.getContent(), meta));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<MovieResponse>> findBySlug(@PathVariable String slug) {
        var data = movieService.findBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /** {@code page} is 1-based (Nest-compatible). */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviews(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        int pageIndex = Math.max(0, page - 1);
        var data = reviewService.getByMovie(id, pageIndex, limit);
        var meta = PageMeta.builder()
                .page(page)
                .limit(limit)
                .total(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .hasNext(data.hasNext())
                .hasPrev(data.hasPrevious())
                .build();
        return ResponseEntity.ok(ApiResponse.success(data.getContent(), meta));
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable UUID id,
            @RequestBody com.cinect.dto.request.CreateReviewRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        req.setMovieId(id);
        var data = reviewService.create(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}/showtimes")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getShowtimes(
            @PathVariable UUID id,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String city) {
        Instant startFrom = null;
        Instant startTo = null;
        if (date != null && !date.isEmpty()) {
            var localDate = java.time.LocalDate.parse(date);
            startFrom = localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
            startTo = localDate.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
        }
        var data = showtimeService.findFiltered(id, null, city, startFrom, startTo);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
