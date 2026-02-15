package com.cinect.dto.response;

import com.cinect.entity.enums.DiscountType;
import com.cinect.entity.enums.PromotionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PromotionResponse {
    private UUID id;
    private String title;
    private String description;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minPurchase;
    private BigDecimal maxDiscount;
    private Integer usageLimit;
    private Integer usageCount;
    private Instant startDate;
    private Instant endDate;
    private String imageUrl;
    private String conditions;
    private PromotionStatus status;
    private Boolean isTrending;
    private Instant createdAt;
}
