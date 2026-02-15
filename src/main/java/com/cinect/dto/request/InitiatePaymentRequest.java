package com.cinect.dto.request;

import com.cinect.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InitiatePaymentRequest {
    @NotNull
    private UUID bookingId;
    @NotNull
    private PaymentMethod method;
}
