package com.cinect.controller;

import com.cinect.dto.request.*;
import com.cinect.dto.response.*;
import com.cinect.entity.AuditLog;
import com.cinect.entity.PricingRule;
import com.cinect.service.*;
import com.cinect.entity.User;
import com.cinect.entity.enums.UserRole;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.CinemaRepository;
import com.cinect.repository.MembershipRepository;
import com.cinect.repository.MembershipTierRepository;
import com.cinect.repository.PromotionRepository;
import com.cinect.repository.PricingRuleRepository;
import com.cinect.repository.RoleRepository;
import com.cinect.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MembershipRepository membershipRepository;
    private final MembershipTierRepository membershipTierRepository;

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getKpis(
            @RequestParam(required = false) String range) {
        int days = "7d".equals(range) ? 7 : "90d".equals(range) ? 90 : 30;
        java.time.Instant to = Instant.now();
        java.time.Instant from = java.time.LocalDate.now().minusDays(days).atStartOfDay(ZoneOffset.UTC).toInstant();
        var revenue = analyticsService.getRevenue(from, to);
        long totalUsers = userRepository.count();
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "totalRevenue", revenue.get("totalRevenue"),
                "totalBookings", revenue.get("totalBookings"),
                "totalUsers", totalUsers,
                "confirmedBookings", revenue.get("totalBookings")
        )));
    }

    @GetMapping("/bookings/recent")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getRecentBookings(
            @RequestParam(defaultValue = "10") int limit) {
        var list = bookingService.getRecentBookings(limit);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> listRooms() {
        var list = roomService.findAllRooms();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable UUID id,
            @RequestBody UpdateRoomRequest req) {
        var data = roomService.update(id, req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable UUID id) {
        roomService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/rooms/{roomId}/seats")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getRoomSeats(@PathVariable UUID roomId) {
        var list = roomService.getSeats(roomId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PutMapping("/rooms/{roomId}/seats")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> bulkUpdateSeats(
            @PathVariable UUID roomId,
            @RequestBody BulkUpdateSeatsRequest req) {
        var list = roomService.bulkUpdateSeats(roomId, req);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/rooms/{roomId}/seats/import")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> importSeats(
            @PathVariable UUID roomId,
            @RequestBody ImportSeatsRequest req) {
        var list = roomService.importSeats(roomId, req);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> adminCancelBooking(@PathVariable UUID id) {
        var data = bookingService.adminCancelBooking(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/bookings/{id}/refund")
    public ResponseEntity<ApiResponse<BookingResponse>> adminRefundBooking(@PathVariable UUID id) {
        var data = bookingService.adminRefundBooking(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/promotions/{id}")
    public ResponseEntity<ApiResponse<PromotionResponse>> updatePromotion(
            @PathVariable UUID id,
            @RequestBody UpdatePromotionRequest req) {
        var data = promotionService.update(id, req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/promotions/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable UUID id) {
        promotionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/pricing-rules/{id}")
    public ResponseEntity<ApiResponse<PricingRule>> updatePricingRule(
            @PathVariable UUID id,
            @RequestBody UpdatePricingRuleRequest req) {
        var rule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing rule not found"));
        if (req.getName() != null) rule.setName(req.getName());
        if (req.getCinemaId() != null) cinemaRepository.findById(req.getCinemaId()).ifPresent(rule::setCinema);
        if (req.getSeatType() != null) rule.setSeatType(req.getSeatType());
        if (req.getFormat() != null) rule.setFormat(req.getFormat());
        if (req.getDayType() != null) rule.setDayType(req.getDayType());
        if (req.getTimeSlot() != null) rule.setTimeSlot(req.getTimeSlot());
        if (req.getIsHoliday() != null) rule.setIsHoliday(req.getIsHoliday());
        if (req.getPrice() != null) rule.setPrice(req.getPrice());
        if (req.getIsActive() != null) rule.setIsActive(req.getIsActive());
        rule = pricingRuleRepository.save(rule);
        return ResponseEntity.ok(ApiResponse.success(rule));
    }

    @DeleteMapping("/pricing-rules/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePricingRule(@PathVariable UUID id) {
        var rule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing rule not found"));
        rule.setIsActive(false);
        pricingRuleRepository.save(rule);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/showtimes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShowtime(@PathVariable UUID id) {
        showtimeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/reports/sales")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        var data = analyticsService.getSalesReport(from, to);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/reports/movies")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMovieReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        var data = analyticsService.getMoviePerformanceReport(from, to);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/reports/cinemas")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCinemaReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        var data = analyticsService.getCinemaPerformanceReport(from, to);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        UserRole roleEnum = UserRole.USER;
        try { roleEnum = UserRole.valueOf(req.getRole() != null ? req.getRole() : "USER"); } catch (Exception ignored) { }
        var role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        var bronzeTier = membershipTierRepository.findByName("Bronze")
                .orElse(membershipTierRepository.findAll().stream()
                        .min((a, b) -> Integer.compare(a.getLevel(), b.getLevel()))
                        .orElseThrow(() -> new ResourceNotFoundException("No membership tier found")));
        var user = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .isActive(true)
                .emailVerified(false)
                .roles(new HashSet<>(Set.of(role)))
                .build();
        user = userRepository.save(user);
        var membership = com.cinect.entity.Membership.builder()
                .user(user)
                .tier(bronzeTier)
                .currentPoints(0)
                .totalPoints(0)
                .memberSince(Instant.now())
                .build();
        membershipRepository.save(membership);
        String roleStr = user.getRoles().isEmpty() ? "USER" : user.getRoles().iterator().next().getName().name();
        var resp = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(roleStr)
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest req) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getPassword() != null && !req.getPassword().isBlank()) user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getAvatar() != null) user.setAvatar(req.getAvatar());
        if (req.getDateOfBirth() != null) user.setDateOfBirth(req.getDateOfBirth());
        if (req.getGender() != null) user.setGender(req.getGender());
        if (req.getCity() != null) user.setCity(req.getCity());
        if (req.getIsActive() != null) user.setIsActive(req.getIsActive());
        if (req.getEmailVerified() != null) user.setEmailVerified(req.getEmailVerified());
        if (req.getRole() != null) {
            try {
                UserRole r = UserRole.valueOf(req.getRole());
                roleRepository.findByName(r).ifPresent(role -> user.setRoles(new HashSet<>(Set.of(role))));
            } catch (Exception ignored) { }
        }
        userRepository.save(user);
        String roleStr = user.getRoles().isEmpty() ? "USER" : user.getRoles().iterator().next().getName().name();
        var resp = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .role(roleStr)
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .city(user.getCity())
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/analytics/forecast")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getForecast(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        Instant toInst = to != null ? to : Instant.now();
        Instant fromInst = from != null ? from : java.time.LocalDate.now().minusDays(30).atStartOfDay(ZoneOffset.UTC).toInstant();
        var data = analyticsService.getRevenueForecast(fromInst, toInst);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/analytics/customer-segments")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerSegments() {
        var data = analyticsService.getCustomerSegments();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

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
        var content = data.map(u -> {
            String role = u.getRoles().isEmpty() ? "USER"
                    : u.getRoles().iterator().next().getName().name();
            return UserResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .phone(u.getPhone())
                .avatar(u.getAvatar())
                .role(role)
                .dateOfBirth(u.getDateOfBirth())
                .gender(u.getGender())
                .city(u.getCity())
                .isActive(u.getIsActive())
                .emailVerified(u.getEmailVerified())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
        });
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

    @GetMapping("/analytics/revenue")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        Instant toInst = to != null ? to : Instant.now();
        Instant fromInst = from != null ? from : java.time.LocalDate.now().minusDays(30).atStartOfDay(ZoneOffset.UTC).toInstant();
        var data = analyticsService.getRevenue(fromInst, toInst);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/analytics/occupancy")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOccupancy(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        Instant toInst = to != null ? to : Instant.now();
        Instant fromInst = from != null ? from : java.time.LocalDate.now().minusDays(30).atStartOfDay(ZoneOffset.UTC).toInstant();
        var data = analyticsService.getOccupancy(fromInst, toInst);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/analytics/peak-hours")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPeakHours(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        Instant toInst = to != null ? to : Instant.now();
        Instant fromInst = from != null ? from : java.time.LocalDate.now().minusDays(30).atStartOfDay(ZoneOffset.UTC).toInstant();
        var data = analyticsService.getPeakHours(fromInst, toInst);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<Object>>> listRoles() {
        var roles = roleRepository.findAll();
        var data = roles.stream().map(r -> {
            var map = new java.util.LinkedHashMap<String, Object>();
            map.put("id", r.getId());
            map.put("name", r.getName().name());
            map.put("permissions", r.getPermissions());
            return (Object) map;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
