package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.GiftCardResponse;
import com.cinect.dto.response.PageMeta;
import com.cinect.entity.Coupon;
import com.cinect.entity.PointsHistory;
import com.cinect.security.UserPrincipal;
import com.cinect.service.CouponService;
import com.cinect.service.GiftCardService;
import com.cinect.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {

    private final GiftCardService giftCardService;
    private final MembershipService membershipService;
    private final CouponService couponService;

    @GetMapping("/coupons")
    public ResponseEntity<ApiResponse<List<Coupon>>> myCoupons(
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = couponService.getUserCoupons(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/coupons/redeem")
    public ResponseEntity<ApiResponse<Void>> redeemCoupon(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal principal) {
        couponService.redeem(body.get("code"), principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Coupon redeemed successfully"));
    }

    @GetMapping("/gifts")
    public ResponseEntity<ApiResponse<List<GiftCardResponse>>> myGifts(
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = giftCardService.findByBuyer(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/points/history")
    public ResponseEntity<ApiResponse<List<PointsHistory>>> pointsHistory(
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
}
