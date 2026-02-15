package com.cinect.dto.response;

import com.cinect.entity.enums.GiftCardStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GiftCardResponse {
    private UUID id;
    private String title;
    private String description;
    private String imageUrl;
    private BigDecimal value;
    private BigDecimal price;
    private String code;
    private GiftCardStatus status;
    private Instant expiresAt;
    private Instant createdAt;
}
