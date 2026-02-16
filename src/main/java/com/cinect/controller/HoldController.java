package com.cinect.controller;

import com.cinect.dto.request.CreateHoldRequest;
import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.HoldResponse;
import com.cinect.security.UserPrincipal;
import com.cinect.service.HoldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/holds")
@RequiredArgsConstructor
public class HoldController {

    private final HoldService holdService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HoldResponse>> getHold(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = holdService.getHoldById(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HoldResponse>> createHold(
            @Valid @RequestBody CreateHoldRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = holdService.createHold(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> releaseHold(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        holdService.releaseHold(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
