package com.cinect.controller;

import com.cinect.dto.request.ContactFormRequest;
import com.cinect.dto.response.ApiResponse;
import com.cinect.security.UserPrincipal;
import com.cinect.service.SupportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/contact")
    public ResponseEntity<ApiResponse<Void>> contact(
            @Valid @RequestBody ContactFormRequest req,
            @AuthenticationPrincipal(errorOnInvalidType = false) UserPrincipal principal) {
        var userId = principal != null ? principal.getId() : null;
        supportService.createTicket(req, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Ticket submitted successfully"));
    }

    @PostMapping("/ticket")
    public ResponseEntity<ApiResponse<Void>> submitTicket(
            @Valid @RequestBody ContactFormRequest req,
            @AuthenticationPrincipal(errorOnInvalidType = false) UserPrincipal principal) {
        var userId = principal != null ? principal.getId() : null;
        supportService.createTicket(req, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Ticket submitted successfully"));
    }
}
