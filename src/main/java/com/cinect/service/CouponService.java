package com.cinect.service;

import com.cinect.entity.Coupon;
import com.cinect.exception.BadRequestException;
import com.cinect.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public List<Coupon> getUserCoupons(UUID userId) {
        return couponRepository.findByUser_IdAndStatus(userId, com.cinect.entity.enums.CouponStatus.ACTIVE);
    }

    public BigDecimal validate(String code, BigDecimal subtotal, UUID userId) {
        var c = couponRepository.findByCode(code).orElse(null);
        if (c == null) return null;
        if (c.getStatus() != com.cinect.entity.enums.CouponStatus.ACTIVE) return null;
        if (c.getExpiresAt().isBefore(java.time.Instant.now())) return null;
        if (c.getUser() != null && !c.getUser().getId().equals(userId)) return null;
        if (c.getMinPurchase() != null && subtotal.compareTo(c.getMinPurchase()) < 0) return null;
        BigDecimal discount;
        if (c.getDiscountType() == com.cinect.entity.enums.DiscountType.PERCENTAGE) {
            discount = subtotal.multiply(c.getDiscountValue()).divide(BigDecimal.valueOf(100));
        } else {
            discount = c.getDiscountValue();
        }
        if (c.getMaxDiscount() != null && discount.compareTo(c.getMaxDiscount()) > 0) {
            discount = c.getMaxDiscount();
        }
        return discount;
    }

    @org.springframework.transaction.annotation.Transactional
    public void redeem(String code, UUID userId) {
        var c = couponRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Invalid coupon"));
        if (c.getStatus() != com.cinect.entity.enums.CouponStatus.ACTIVE) {
            throw new BadRequestException("Coupon already used or expired");
        }
        c.setStatus(com.cinect.entity.enums.CouponStatus.USED);
        c.setUsedAt(java.time.Instant.now());
        couponRepository.save(c);
    }
}
