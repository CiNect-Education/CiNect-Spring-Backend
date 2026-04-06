package com.cinect.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Matches Nest/Prisma membership profile JSON: nested {@code tier}, optional progression fields.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipResponse {
    private UUID userId;
    private MembershipTierResponse tier;
    private Integer currentPoints;
    private Integer totalPoints;
    private MembershipTierResponse nextTier;
    private Integer pointsToNextTier;
    private Instant memberSince;
    private Instant expiresAt;
}
