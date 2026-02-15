package com.cinect.dto.request;

import com.cinect.entity.enums.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreatePromotionRequest {
    @NotNull
    private String title;
    private String description;
    private String code;
    @NotNull
    private DiscountType discountType;
    @NotNull @DecimalMin("0")
    private BigDecimal discountValue;
    private BigDecimal minPurchase;
    private BigDecimal maxDiscount;
    private Integer usageLimit;
    @NotNull
    private Instant startDate;
    @NotNull
    private Instant endDate;
    private String imageUrl;
    private String conditions;
    @Builder.Default
    private Boolean isTrending = false;
}
