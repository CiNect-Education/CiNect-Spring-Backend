package com.cinect.dto.response;

import com.cinect.entity.enums.PaymentMethod;
import com.cinect.entity.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentResponse {
    private UUID id;
    private UUID bookingId;
    private PaymentMethod method;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;
    private String paymentUrl;
    private String errorReason;
    private Instant paidAt;
    private Instant createdAt;
}
