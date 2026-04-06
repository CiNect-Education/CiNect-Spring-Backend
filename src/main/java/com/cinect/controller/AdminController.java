package com.cinect.controller;

import com.cinect.dto.request.*;
import com.cinect.dto.response.*;
import com.cinect.util.RoleUtil;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
        var data = analyticsService.getAdminKpis(range);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRevenueSeries(
            @RequestParam(required = false) String range) {
        var data = analyticsService.getRevenueSeries(range);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/occupancy")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOccupancySeries(
            @RequestParam(required = false) String range) {
        var data = analyticsService.getOccupancySeries(range);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/bookings/recent")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getRecentBookings(
            @RequestParam(defaultValue = "10") int limit) {
        var list = bookingService.getRecentBookings(limit);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> listRooms(
            @RequestParam(required = false) UUID cinemaId) {
        var list = roomService.findAllForAdmin(cinemaId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoomDirect(@Valid @RequestBody AdminDirectRoomRequest req) {
        var data = roomService.createDirect(
                req.getCinemaId(),
                req.getName(),
                req.getFormat(),
                req.getTotalSeats(),
                req.getRows(),
                req.getColumns(),
                req.getIsActive());
        return ResponseEntity.ok(ApiResponse.success(data));
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
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSalesReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        LocalDate fromD = from != null ? LocalDate.parse(from) : LocalDate.now(ZoneOffset.UTC).minusDays(30);
        LocalDate toD = to != null ? LocalDate.parse(to) : LocalDate.now(ZoneOffset.UTC);
        Instant fromInst = fromD.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toInst = toD.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        var data = analyticsService.getSalesDailyReport(fromInst, toInst);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/reports/movies")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMovieReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        LocalDate fromD = from != null ? LocalDate.parse(from) : LocalDate.now(ZoneOffset.UTC).minusDays(30);
        LocalDate toD = to != null ? LocalDate.parse(to) : LocalDate.now(ZoneOffset.UTC);
        Instant fromInst = fromD.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toInst = toD.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        var data = analyticsService.getMoviePerformanceReport(fromInst, toInst);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/reports/cinemas")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCinemaReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        LocalDate fromD = from != null ? LocalDate.parse(from) : LocalDate.now(ZoneOffset.UTC).minusDays(30);
        LocalDate toD = to != null ? LocalDate.parse(to) : LocalDate.now(ZoneOffset.UTC);
        Instant fromInst = fromD.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toInst = toD.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        var data = analyticsService.getCinemaPerformanceReport(fromInst, toInst);
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
        String roleStr = RoleUtil.pickPrimaryRoleName(user.getRoles());
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
        String roleStr = RoleUtil.pickPrimaryRoleName(user.getRoles());
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

    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> listMovies(
            @RequestParam(required = false) com.cinect.entity.enums.MovieStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit) {
        if (page == null && limit == null) {
            var list = movieService.findAllForAdmin();
            return ResponseEntity.ok(ApiResponse.success(list));
        }
        int p = page != null ? page : 0;
        int l = limit != null ? limit : 20;
        var data = movieService.findAll(status, search, null, null, null, null, null, null, "releaseDate:desc", p, l);
        var meta = PageMeta.builder()
                .page(p)
                .limit(l)
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
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit) {
        if (page == null && limit == null && city == null && search == null) {
            var list = cinemaService.findAllForAdmin();
            return ResponseEntity.ok(ApiResponse.success(list));
        }
        int p = page != null ? page : 0;
        int l = limit != null ? limit : 20;
        var data = cinemaService.findAll(city, search, p, l);
        var meta = PageMeta.builder()
                .page(p)
                .limit(l)
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
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(required = false) String date) {
        var data = showtimeService.search(movieId, cinemaId, date, null, null);
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
        var pageable = org.springframework.data.domain.PageRequest.of(
                page,
                limit,
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        var data = (search == null || search.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.searchUsersByTerm(search.trim(), pageable);
        var meta = PageMeta.builder()
                .page(page)
                .limit(limit)
                .total(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .hasNext(data.hasNext())
                .hasPrev(data.hasPrevious())
                .build();
        var content = data.map(u -> {
            String role = RoleUtil.pickPrimaryRoleName(u.getRoles());
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
                        .description(p.getDescription())
                        .code(p.getCode())
                        .discountType(p.getDiscountType())
                        .discountValue(p.getDiscountValue() != null ? p.getDiscountValue().doubleValue() : null)
                        .minPurchase(p.getMinPurchase() != null ? p.getMinPurchase().doubleValue() : null)
                        .maxDiscount(p.getMaxDiscount() != null ? p.getMaxDiscount().doubleValue() : null)
                        .usageLimit(p.getUsageLimit())
                        .usageCount(p.getUsageCount())
                        .startDate(p.getStartDate())
                        .endDate(p.getEndDate())
                        .imageUrl(p.getImageUrl())
                        .conditions(p.getConditions())
                        .status(p.getStatus())
                        .isTrending(p.getIsTrending())
                        .createdAt(p.getCreatedAt())
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

    @GetMapping("/pricing-rules")
    public ResponseEntity<ApiResponse<List<Object>>> listPricingRules() {
        var rules = pricingRuleRepository.findAll();
        var data = rules.stream().map(r -> {
            var map = new LinkedHashMap<String, Object>();
            map.put("id", r.getId());
            map.put("seatType", r.getSeatType());
            map.put("dayType", r.getDayType());
            map.put("timeSlot", r.getTimeSlot());
            map.put("roomFormat", r.getFormat() != null ? r.getFormat().name() : null);
            map.put("price", r.getPrice());
            map.put("isActive", r.getIsActive());
            map.put("createdAt", r.getCreatedAt());
            map.put("updatedAt", r.getUpdatedAt());
            return (Object) map;
        }).toList();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> listAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int limit) {
        var data = auditLogService.findFiltered(entityType, search, from, to, action, page, limit);
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
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAnalyticsRevenue(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        var w = resolveAnalyticsWindow(range, from, to);
        var revenue = analyticsService.getRevenue(w.from(), w.to());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> daily = (List<Map<String, Object>>) revenue.get("daily");
        List<Map<String, Object>> chart = new ArrayList<>();
        if (daily != null) {
            for (Map<String, Object> day : daily) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("date", String.valueOf(day.get("date")));
                Object revObj = day.get("revenue");
                double revNum = revObj instanceof BigDecimal
                        ? ((BigDecimal) revObj).doubleValue()
                        : ((Number) revObj).doubleValue();
                row.put("revenue", revNum);
                chart.add(row);
            }
        }
        return ResponseEntity.ok(ApiResponse.success(chart));
    }

    @GetMapping("/analytics/forecast")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAnalyticsForecast(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        var w = resolveAnalyticsWindow(range, from, to);
        var data = analyticsService.getForecastSeries(w.from(), w.to());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/analytics/occupancy")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAnalyticsOccupancy(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        var w = resolveAnalyticsWindow(range, from, to);
        var data = analyticsService.getOccupancyByCinemaDate(w.from(), w.to());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/analytics/customer-segments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCustomerSegmentsChart() {
        var data = analyticsService.getCustomerSegmentsChart();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/analytics/peak-hours")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAnalyticsPeakHours(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        var w = resolveAnalyticsWindow(range, from, to);
        var data = analyticsService.getPeakHoursSeries(w.from(), w.to());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Aligns with admin analytics page: {@code range} (7d/30d/90d) or {@code from}/{@code to} as {@code YYYY-MM-DD}.
     */
    private AnalyticsWindow resolveAnalyticsWindow(String range, String fromDate, String toDate) {
        Instant toInst = Instant.now();
        if (fromDate != null && toDate != null && !fromDate.isBlank() && !toDate.isBlank()) {
            LocalDate fromD = LocalDate.parse(fromDate);
            LocalDate toD = LocalDate.parse(toDate);
            return new AnalyticsWindow(
                    fromD.atStartOfDay(ZoneOffset.UTC).toInstant(),
                    toD.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant());
        }
        if (range != null && !range.isBlank() && !"custom".equals(range)) {
            int days = "7d".equals(range) ? 7 : "90d".equals(range) ? 90 : 30;
            return new AnalyticsWindow(toInst.minusSeconds(days * 24L * 60 * 60), toInst);
        }
        return new AnalyticsWindow(
                LocalDate.now(ZoneOffset.UTC).minusDays(30).atStartOfDay(ZoneOffset.UTC).toInstant(),
                toInst);
    }

    private record AnalyticsWindow(Instant from, Instant to) {
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

    @PutMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<Object>> updateRolePermissions(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> req) {
        var role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        Object rawPermissions = req != null ? req.get("permissions") : null;
        List<String> permissions = rawPermissions instanceof List<?> list
                ? list.stream()
                    .filter(v -> v != null)
                    .map(v -> String.valueOf(v).trim())
                    .filter(v -> !v.isBlank())
                    .distinct()
                    .toList()
                : Collections.emptyList();
        role.setPermissions(permissions);
        roleRepository.save(role);
        var map = new LinkedHashMap<String, Object>();
        map.put("id", role.getId());
        map.put("name", role.getName().name());
        map.put("permissions", role.getPermissions());
        return ResponseEntity.ok(ApiResponse.success((Object) map));
    }
}
