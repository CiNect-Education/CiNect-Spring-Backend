package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StatusController {

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus() {
        var data = Map.<String, Object>of(
                "status", "online",
                "maintenance", false,
                "version", "1.0.0"
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
