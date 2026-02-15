package com.cinect.repository;

import com.cinect.entity.Coupon;
import com.cinect.entity.enums.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByCode(String code);
    List<Coupon> findByUser_IdAndStatus(UUID userId, CouponStatus status);
}
