package com.cinect.entity;

import com.cinect.entity.enums.PaymentMethod;
import com.cinect.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "payment_method")
    private PaymentMethod method;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "payment_status")
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_url")
    private String paymentUrl;

    @Column(name = "error_reason")
    private String errorReason;

    @Column(name = "paid_at")
    private Instant paidAt;
}
