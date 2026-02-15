package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.PromotionResponse;
import com.cinect.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getAll() {
        var data = promotionService.getActive();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getActive(
            @RequestParam(defaultValue = "8") int limit) {
        var data = promotionService.getActive(limit);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getTrending() {
        var data = promotionService.getTrending();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{code}/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validate(
            @PathVariable String code,
            @RequestParam BigDecimal subtotal,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.cinect.security.UserPrincipal principal) {
        var userId = principal != null ? principal.getId() : null;
        var discount = promotionService.validateAndApply(code, subtotal, userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("valid", discount.compareTo(BigDecimal.ZERO) > 0, "discount", discount)));
    }
}
