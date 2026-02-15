package com.cinect.entity;

import com.cinect.entity.enums.GiftCardStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "gift_cards")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GiftCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "gift_card_status")
    @Builder.Default
    private GiftCardStatus status = GiftCardStatus.AVAILABLE;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
