package com.cinect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "memberships")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Membership extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Column(name = "current_points", nullable = false)
    @Builder.Default
    private Integer currentPoints = 0;

    @Column(name = "total_points", nullable = false)
    @Builder.Default
    private Integer totalPoints = 0;

    @Column(name = "member_since", nullable = false)
    @Builder.Default
    private Instant memberSince = Instant.now();

    @Column(name = "expires_at")
    private Instant expiresAt;
}
