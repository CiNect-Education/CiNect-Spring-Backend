package com.cinect.controller;

import com.cinect.dto.request.HoldRequest;
import com.cinect.service.HoldService;
import lombok.RequiredArgsConstructor;
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
        // Lấy email người dùng đang đăng nhập từ JWT Token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        holdService.holdSeats(email, request);
        return ResponseEntity.ok().build();
    }
}