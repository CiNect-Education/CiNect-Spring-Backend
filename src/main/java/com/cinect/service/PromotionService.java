package com.cinect.service;

import com.cinect.dto.request.CreatePromotionRequest;
import com.cinect.dto.response.PromotionResponse;
import com.cinect.entity.Promotion;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public List<PromotionResponse> getActive() {
        var now = Instant.now();
        return promotionRepository.findActivePromotions(now).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<PromotionResponse> getActive(int limit) {
        var now = Instant.now();
        return promotionRepository.findActivePromotions(now).stream()
                .limit(limit)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<PromotionResponse> getTrending() {
        return promotionRepository.findTrending().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PromotionResponse findByCode(String code) {
        var p = promotionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        return toResponse(p);
    }

    public BigDecimal validateAndApply(String code, BigDecimal subtotal, UUID userId) {
        if (code == null || code.isBlank()) return BigDecimal.ZERO;
        var p = promotionRepository.findByCode(code).orElse(null);
        if (p == null) return BigDecimal.ZERO;
        var now = Instant.now();
        if (p.getStatus() != com.cinect.entity.enums.PromotionStatus.ACTIVE
                || p.getStartDate().isAfter(now) || p.getEndDate().isBefore(now)) {
            return BigDecimal.ZERO;
        }
        if (p.getMinPurchase() != null && subtotal.compareTo(p.getMinPurchase()) < 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = BigDecimal.ZERO;
        if (p.getDiscountType() == com.cinect.entity.enums.DiscountType.PERCENTAGE) {
            discount = subtotal.multiply(p.getDiscountValue()).divide(BigDecimal.valueOf(100));
        } else {
            discount = p.getDiscountValue();
        }
        if (p.getMaxDiscount() != null && discount.compareTo(p.getMaxDiscount()) > 0) {
            discount = p.getMaxDiscount();
        }
        return discount;
    }

    @Transactional
    public PromotionResponse create(CreatePromotionRequest req) {
        var p = Promotion.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .code(req.getCode())
                .discountType(req.getDiscountType())
                .discountValue(req.getDiscountValue())
                .minPurchase(req.getMinPurchase())
                .maxDiscount(req.getMaxDiscount())
                .usageLimit(req.getUsageLimit())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .imageUrl(req.getImageUrl())
                .conditions(req.getConditions())
                .isTrending(req.getIsTrending() != null ? req.getIsTrending() : false)
                .build();
        p = promotionRepository.save(p);
        return toResponse(p);
    }

    private PromotionResponse toResponse(Promotion p) {
        return PromotionResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .code(p.getCode())
                .discountType(p.getDiscountType())
                .discountValue(p.getDiscountValue() != null ? p.getDiscountValue().doubleValue() : null)
                .minPurchase(p.getMinPurchase() != null ? p.getMinPurchase().doubleValue() : null)
                .maxDiscount(p.getMaxDiscount() != null ? p.getMaxDiscount().doubleValue() : null)
                .usageLimit(p.getUsageLimit())
                .usageCount(p.getUsageCount())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .imageUrl(p.getImageUrl())
                .conditions(p.getConditions())
                .status(p.getStatus())
                .isTrending(p.getIsTrending())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
