package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.NotificationResponse;
import com.cinect.dto.response.PageMeta;
import com.cinect.security.UserPrincipal;
import com.cinect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {
        var data = notificationService.getByUser(principal.getId(), page, limit);
        var meta = PageMeta.builder()
                .page(page)
                .limit(limit)
                .total(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .hasNext(data.hasNext())
                .hasPrev(data.hasPrevious())
                .build();
        return ResponseEntity.ok(ApiResponse.success(data.getContent(), meta));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markRead(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead(
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllRead(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
