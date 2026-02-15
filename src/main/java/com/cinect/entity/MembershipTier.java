package com.cinect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "membership_tiers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class MembershipTier extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private Integer level;

    @Column(name = "points_required", nullable = false)
    @Builder.Default
    private Integer pointsRequired = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> benefits;

    @Column(name = "discount_percent")
    @Builder.Default
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Builder.Default
    private String color = "#6B7280";

    private String icon;
}
