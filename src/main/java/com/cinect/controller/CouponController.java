package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.entity.Coupon;
import com.cinect.security.UserPrincipal;
import com.cinect.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Coupon>>> getUserCoupons(
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = couponService.getUserCoupons(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/{code}/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validate(
            @PathVariable String code,
            @RequestParam BigDecimal subtotal,
            @AuthenticationPrincipal UserPrincipal principal) {
        var discount = couponService.validate(code, subtotal, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("valid", discount != null && discount.compareTo(BigDecimal.ZERO) > 0, "discount", discount != null ? discount : BigDecimal.ZERO)));
    }
}
