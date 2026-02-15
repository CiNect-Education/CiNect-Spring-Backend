package com.cinect.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MembershipResponse {
    private UUID id;
    private UUID userId;
    private UUID tierId;
    private String tierName;
    private Integer tierLevel;
    private Integer currentPoints;
    private Integer totalPoints;
    private Integer pointsRequired;
    private List<String> benefits;
    private BigDecimal discountPercent;
    private String color;
    private Instant memberSince;
    private Instant expiresAt;
}
