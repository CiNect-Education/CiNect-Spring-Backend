package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.MembershipResponse;
import com.cinect.dto.response.MembershipTierResponse;
import com.cinect.dto.response.PointsHistoryResponse;
import com.cinect.dto.response.ShowtimeResponse;
import com.cinect.dto.response.PageMeta;
import com.cinect.security.UserPrincipal;
import com.cinect.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping("/tiers")
    public ResponseEntity<ApiResponse<List<MembershipTierResponse>>> getTiers() {
        var data = membershipService.getTiers();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<MembershipResponse>> getProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = membershipService.getProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/points-history")
    public ResponseEntity<ApiResponse<List<PointsHistoryResponse>>> getPointsHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {
        var data = membershipService.getPointsHistory(principal.getId(), page, limit);
        var rows = data.getContent().stream().map(p -> PointsHistoryResponse.builder()
                .id(p.getId())
                .type(p.getType())
                .points(p.getPoints())
                .balance(p.getBalance())
                .description(p.getDescription())
                .bookingId(p.getBooking() != null ? p.getBooking().getId() : null)
                .createdAt(p.getCreatedAt())
                .build()).collect(Collectors.toList());
        var meta = PageMeta.builder()
                .page(page)
                .limit(limit)
                .total(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .hasNext(data.hasNext())
                .hasPrev(data.hasPrevious())
                .build();
        return ResponseEntity.ok(ApiResponse.success(rows, meta));
    }

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getEvents(
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = membershipService.getMemberEvents();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/daily-checkin/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDailyCheckinStatus(
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = membershipService.getDailyCheckinStatus(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/daily-checkin/claim")
    public ResponseEntity<ApiResponse<Map<String, Object>>> claimDailyCheckin(
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = membershipService.claimDailyCheckin(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
