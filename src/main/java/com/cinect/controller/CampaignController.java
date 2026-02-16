package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.CampaignResponse;
import com.cinect.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CampaignResponse>>> findActive() {
        var data = campaignService.findActive();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CampaignResponse>> findBySlug(@PathVariable String slug) {
        var data = campaignService.findBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
