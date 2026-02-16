package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.MovieResponse;
import com.cinect.dto.response.PageMeta;
import com.cinect.dto.response.ReviewResponse;
import com.cinect.dto.response.ShowtimeResponse;
import com.cinect.entity.enums.MovieStatus;
import com.cinect.security.UserPrincipal;
import com.cinect.service.MovieService;
import com.cinect.service.ReviewService;
import com.cinect.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final ReviewService reviewService;
    private final ShowtimeService showtimeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponse>>> findAll(
            @RequestParam(required = false) MovieStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {
        var data = movieService.findAll(status, search, genreId, page, limit);
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

    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviews(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        var data = reviewService.getByMovie(id, page, limit);
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
            @RequestParam(required = false) String date) {
        Instant startFrom = null;
        Instant startTo = null;
        if (date != null && !date.isEmpty()) {
            var localDate = java.time.LocalDate.parse(date);
            startFrom = localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
            startTo = localDate.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
        }
        var data = showtimeService.findFiltered(id, null, startFrom, startTo);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
