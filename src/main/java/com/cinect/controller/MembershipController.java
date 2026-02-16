package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.MembershipResponse;
import com.cinect.dto.response.MembershipTierResponse;
import com.cinect.dto.response.ShowtimeResponse;
import com.cinect.entity.PointsHistory;
import com.cinect.dto.response.PageMeta;
import com.cinect.security.UserPrincipal;
import com.cinect.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<ApiResponse<List<PointsHistory>>> getPointsHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {
        var data = membershipService.getPointsHistory(principal.getId(), page, limit);
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

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> getEvents(
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = membershipService.getMemberEvents();
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
