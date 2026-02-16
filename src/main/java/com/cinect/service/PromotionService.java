package com.cinect.service;

import com.cinect.dto.request.CreatePromotionRequest;
import com.cinect.dto.request.UpdatePromotionRequest;
import com.cinect.dto.response.PromotionResponse;
import com.cinect.entity.Promotion;
import com.cinect.entity.enums.PromotionStatus;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.BookingRepository;
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
    private final BookingRepository bookingRepository;

    public List<PromotionResponse> getActive() {
        var now = Instant.now();
        return promotionRepository.findActivePromotions(now).stream()
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

    public List<PromotionResponse> findEligible(UUID bookingId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        var now = Instant.now();
        var promotions = promotionRepository.findActivePromotions(now);

        return promotions.stream()
                .filter(p -> p.getMinPurchase() == null || booking.getTotalAmount().compareTo(p.getMinPurchase()) >= 0)
                .filter(p -> p.getUsageLimit() == null || p.getUsageCount() < p.getUsageLimit())
                .map(this::toResponse)
                .collect(Collectors.toList());
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

    @Transactional
    public PromotionResponse update(UUID id, UpdatePromotionRequest req) {
        var p = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        if (req.getTitle() != null) p.setTitle(req.getTitle());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getCode() != null) p.setCode(req.getCode());
        if (req.getDiscountType() != null) p.setDiscountType(req.getDiscountType());
        if (req.getDiscountValue() != null) p.setDiscountValue(req.getDiscountValue());
        if (req.getMinPurchase() != null) p.setMinPurchase(req.getMinPurchase());
        if (req.getMaxDiscount() != null) p.setMaxDiscount(req.getMaxDiscount());
        if (req.getUsageLimit() != null) p.setUsageLimit(req.getUsageLimit());
        if (req.getStartDate() != null) p.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) p.setEndDate(req.getEndDate());
        if (req.getImageUrl() != null) p.setImageUrl(req.getImageUrl());
        if (req.getConditions() != null) p.setConditions(req.getConditions());
        if (req.getStatus() != null) p.setStatus(req.getStatus());
        if (req.getIsTrending() != null) p.setIsTrending(req.getIsTrending());
        p = promotionRepository.save(p);
        return toResponse(p);
    }

    @Transactional
    public void delete(UUID id) {
        var p = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        p.setStatus(PromotionStatus.INACTIVE);
        promotionRepository.save(p);
    }

    private PromotionResponse toResponse(Promotion p) {
        return PromotionResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .code(p.getCode())
                .discountType(p.getDiscountType())
                .discountValue(p.getDiscountValue())
                .minPurchase(p.getMinPurchase())
                .maxDiscount(p.getMaxDiscount())
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
