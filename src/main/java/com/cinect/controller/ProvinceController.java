package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.ProvinceLegacyResponse;
import com.cinect.dto.response.ProvinceNewResponse;
import com.cinect.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/provinces")
@RequiredArgsConstructor
public class ProvinceController {

    private final ProvinceService provinceService;

    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<ProvinceNewResponse>>> listNew() {
        return ResponseEntity.ok(ApiResponse.success(provinceService.listNew()));
    }

    @GetMapping("/legacy")
    public ResponseEntity<ApiResponse<List<ProvinceLegacyResponse>>> listLegacy() {
        return ResponseEntity.ok(ApiResponse.success(provinceService.listLegacy()));
    }
}
