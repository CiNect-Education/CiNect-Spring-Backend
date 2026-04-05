package com.cinect.controller;

import com.cinect.dto.request.HoldRequest;
import com.cinect.service.HoldService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/holds") // Kết hợp với context-path /api/v1 trong application.yml
@RequiredArgsConstructor
public class HoldController {

    private final HoldService holdService;

    @PostMapping
    public ResponseEntity<?> holdSeats(@RequestBody HoldRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
    
        // 1. Kiểm tra xem request có thông tin đăng nhập không
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {

            return ResponseEntity.status(401).body(Map.of("message", "Bạn cần đăng nhập để đặt ghế"));
        }

        String email = authentication.getName();
            try {
                // 2. Gọi Service để giữ ghế
                Map<String, Object> responseData = holdService.holdSeats(email, request);
                return ResponseEntity.ok(responseData);
            } catch (RuntimeException e) {
            // 3. Nếu Service văng lỗi (như "User not found"), trả về lỗi 400 hoặc 404
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
}