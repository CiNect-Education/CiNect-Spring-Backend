package com.cinect.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MembershipTierResponse {
    private UUID id;
    private String name;
    private Integer level;
    private Integer pointsRequired;
    private List<String> benefits;
    private BigDecimal discountPercent;
    private String color;
    private String icon;
}
