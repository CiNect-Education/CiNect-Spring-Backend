package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.BannerResponse;
import com.cinect.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BannerResponse>>> findAll(
            @RequestParam(required = false) String position) {
        var data = bannerService.findAll(position);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
