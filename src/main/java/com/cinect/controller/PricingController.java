package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.entity.PricingRule;
import com.cinect.repository.PricingRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingRuleRepository pricingRuleRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PricingRule>>> getActivePricing() {
        var data = pricingRuleRepository.findByIsActiveTrue();
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
