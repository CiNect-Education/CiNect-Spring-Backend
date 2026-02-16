package com.cinect.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseGiftCardRequest {
    @NotNull
    private UUID giftCardId;
    private String recipientEmail;
    private String message;
}
