package com.cinect.controller;

import com.cinect.dto.request.InitiatePaymentRequest;
import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.PaymentResponse;
import com.cinect.security.UserPrincipal;
import com.cinect.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiate(
            @Valid @RequestBody InitiatePaymentRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = paymentService.initiatePayment(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/callback")
    public ResponseEntity<ApiResponse<Void>> callback(@RequestBody Map<String, Object> body) {
        var transactionId = (String) body.get("transactionId");
        var success = body.get("success") instanceof Boolean b ? b : false;
        paymentService.handleCallback(transactionId, success);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PaymentResponse>> getStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = paymentService.getPaymentStatus(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
