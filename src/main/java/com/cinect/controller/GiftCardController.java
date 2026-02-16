package com.cinect.controller;

import com.cinect.dto.request.PurchaseGiftCardRequest;
import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.GiftCardResponse;
import com.cinect.security.UserPrincipal;
import com.cinect.service.GiftCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gift-cards")
@RequiredArgsConstructor
public class GiftCardController {

    private final GiftCardService giftCardService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GiftCardResponse>>> list() {
        var data = giftCardService.findAll();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GiftCardResponse>> findById(@PathVariable UUID id) {
        var data = giftCardService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<GiftCardResponse>> purchaseWithBody(
            @Valid @RequestBody PurchaseGiftCardRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        var data = giftCardService.purchase(req.getGiftCardId(), principal.getId(), req.getRecipientEmail(), req.getMessage());
        return ResponseEntity.ok(ApiResponse.success(data, "Gift card purchased successfully"));
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<ApiResponse<GiftCardResponse>> purchase(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String recipientEmail,
            @RequestParam(required = false) String message) {
        var data = giftCardService.purchase(id, principal.getId(), recipientEmail, message);
        return ResponseEntity.ok(ApiResponse.success(data, "Gift card purchased successfully"));
    }
}
