package com.cinect.controller;

import com.cinect.dto.request.CreateBookingRequest;
import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.BookingResponse;
import com.cinect.dto.response.PageMeta;
import com.cinect.security.UserPrincipal;
import com.cinect.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(
            @Valid @RequestBody CreateBookingRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = bookingService.createBooking(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUserBookings(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        var data = bookingService.getUserBookings(principal.getId(), page, limit);
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = bookingService.getById(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<BookingResponse>> confirm(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = bookingService.confirmBooking(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        bookingService.cancelBooking(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/apply-promo")
    public ResponseEntity<ApiResponse<BookingResponse>> applyPromo(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = bookingService.applyPromo(id, principal.getId(), body.get("code"));
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/{id}/apply-points")
    public ResponseEntity<ApiResponse<BookingResponse>> applyPoints(
            @PathVariable UUID id,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = bookingService.applyPoints(id, principal.getId(), body.get("points") != null ? body.get("points") : 0);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/{id}/apply-gift-card")
    public ResponseEntity<ApiResponse<BookingResponse>> applyGiftCard(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = bookingService.applyGiftCard(id, principal.getId(), body.get("code"));
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
