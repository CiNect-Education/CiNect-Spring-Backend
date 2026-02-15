package com.cinect.controller;

import com.cinect.dto.request.*;
import com.cinect.dto.response.*;
import com.cinect.entity.AuditLog;
import com.cinect.entity.PricingRule;
import com.cinect.service.*;
import com.cinect.repository.CinemaRepository;
import com.cinect.repository.PromotionRepository;
import com.cinect.repository.PricingRuleRepository;
import com.cinect.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class AdminController {

    private final MovieService movieService;
    private final CinemaService cinemaService;
    private final RoomService roomService;
    private final ShowtimeService showtimeService;
    private final PromotionService promotionService;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final CinemaRepository cinemaRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final AuditLogService auditLogService;
    private final AnalyticsService analyticsService;

    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> listMovies(
            @RequestParam(required = false) com.cinect.entity.enums.MovieStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {
        var data = movieService.findAll(status, search, null, page, limit);
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

    @PostMapping("/movies")
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(@Valid @RequestBody CreateMovieRequest req) {
        var data = movieService.create(req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(
            @PathVariable UUID id,
            @RequestBody UpdateMovieRequest req) {
        var data = movieService.update(id, req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable UUID id) {
        movieService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/cinemas")
    public ResponseEntity<ApiResponse<List<CinemaResponse>>> listCinemas(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {
        var data = cinemaService.findAll(city, search, page, limit);
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

    @PostMapping("/cinemas")
    public ResponseEntity<ApiResponse<CinemaResponse>> createCinema(@Valid @RequestBody CreateCinemaRequest req) {
        var data = cinemaService.create(req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/cinemas/{id}")
    public ResponseEntity<ApiResponse<CinemaResponse>> updateCinema(
            @PathVariable UUID id,
            @RequestBody UpdateCinemaRequest req) {
        var data = cinemaService.update(id, req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/cinemas/{id}/rooms")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @PathVariable UUID id,
            @Valid @RequestBody CreateRoomRequest req) {
        var data = roomService.create(id, req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/showtimes")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> listShowtimes(
            @RequestParam(required = false) UUID movieId,
            @RequestParam(required = false) UUID cinemaId) {
        var data = showtimeService.findFiltered(movieId, cinemaId, null, null);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/showtimes")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> createShowtime(@Valid @RequestBody CreateShowtimeRequest req) {
        var data = showtimeService.create(req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/showtimes/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> updateShowtime(
            @PathVariable UUID id,
            @RequestBody UpdateShowtimeRequest req) {
        var data = showtimeService.update(id, req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> listBookings(
            @RequestParam(required = false) com.cinect.entity.enums.BookingStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {
        var data = bookingService.getBookingsForAdmin(status, search, page, limit);
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

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {
        var pageable = org.springframework.data.domain.PageRequest.of(page, limit);
        var data = userRepository.searchUsers(search, pageable);
        var meta = PageMeta.builder()
                .page(page)
                .limit(limit)
                .total(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .hasNext(data.hasNext())
                .hasPrev(data.hasPrevious())
                .build();
        var content = data.map(u -> UserResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .phone(u.getPhone())
                .avatar(u.getAvatar())
                .dateOfBirth(u.getDateOfBirth())
                .gender(u.getGender())
                .city(u.getCity())
                .isActive(u.getIsActive())
                .emailVerified(u.getEmailVerified())
                .roles(u.getRoles().stream().map(r -> r.getName().name()).collect(java.util.stream.Collectors.toSet()))
                .createdAt(u.getCreatedAt())
                .build());
        return ResponseEntity.ok(ApiResponse.success(content.getContent(), meta));
    }

    @GetMapping("/promotions")
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> listPromotions() {
        var list = promotionRepository.findAll().stream()
                .map(p -> PromotionResponse.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .code(p.getCode())
                        .status(p.getStatus())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/promotions")
    public ResponseEntity<ApiResponse<PromotionResponse>> createPromotion(@Valid @RequestBody CreatePromotionRequest req) {
        var data = promotionService.create(req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/pricing-rules")
    public ResponseEntity<ApiResponse<PricingRule>> createPricingRule(@Valid @RequestBody CreatePricingRuleRequest req) {
        var rule = PricingRule.builder()
                .name(req.getName())
                .seatType(req.getSeatType())
                .format(req.getFormat())
                .dayType(req.getDayType())
                .timeSlot(req.getTimeSlot())
                .isHoliday(req.getIsHoliday())
                .price(req.getPrice())
                .isActive(true)
                .build();
        if (req.getCinemaId() != null) {
            cinemaRepository.findById(req.getCinemaId()).ifPresent(rule::setCinema);
        }
        rule = pricingRuleRepository.save(rule);
        return ResponseEntity.ok(ApiResponse.success(rule));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> listAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int limit) {
        var data = entityType != null
                ? auditLogService.findByEntityType(entityType, page, limit)
                : auditLogService.findAll(page, limit);
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

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        var revenue = analyticsService.getRevenue(from, to);
        var occupancy = analyticsService.getOccupancy(from, to);
        var peakHours = analyticsService.getPeakHours(from, to);
        var topMovies = analyticsService.getTopMovies(10);
        var segments = analyticsService.getCustomerSegments();
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "revenue", revenue,
                "occupancy", occupancy,
                "peakHours", peakHours,
                "topMovies", topMovies,
                "customerSegments", segments
        )));
    }
}
