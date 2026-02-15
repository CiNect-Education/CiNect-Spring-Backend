package com.cinect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "gift_transactions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GiftTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_card_id", nullable = false)
    private GiftCard giftCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @Column(name = "recipient_email")
    private String recipientEmail;

    private String message;

    @Column(name = "purchased_at", nullable = false)
    @Builder.Default
    private Instant purchasedAt = Instant.now();

    @Column(name = "redeemed_at")
    private Instant redeemedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redeemed_by")
    private User redeemedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
