package com.cinect.service;

import com.cinect.dto.response.MembershipResponse;
import com.cinect.entity.Membership;
import com.cinect.entity.PointsHistory;
import com.cinect.entity.enums.PointsTxType;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.BookingRepository;
import com.cinect.repository.MembershipRepository;
import com.cinect.repository.MembershipTierRepository;
import com.cinect.repository.PointsHistoryRepository;
import com.cinect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipTierRepository membershipTierRepository;
    private final PointsHistoryRepository pointsHistoryRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public MembershipResponse getProfile(UUID userId) {
        var m = membershipRepository.findByUserId(userId)
                .orElse(null);
        if (m == null) return null;
        return toResponse(m);
    }

    public List<MembershipResponse> getTiers() {
        return membershipTierRepository.findAll().stream()
                .map(t -> MembershipResponse.builder()
                        .id(t.getId())
                        .tierId(t.getId())
                        .tierName(t.getName())
                        .tierLevel(t.getLevel())
                        .pointsRequired(t.getPointsRequired())
                        .benefits(t.getBenefits())
                        .discountPercent(t.getDiscountPercent())
                        .color(t.getColor())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void earnPoints(UUID userId, UUID bookingId, int points) {
        var membership = membershipRepository.findByUserId(userId).orElse(null);
        if (membership == null) return;
        var balance = membership.getCurrentPoints() + points;
        membership.setCurrentPoints(balance);
        membership.setTotalPoints(membership.getTotalPoints() + points);
        membershipRepository.save(membership);

        var user = membership.getUser();
        var booking = bookingRepository.findById(bookingId).orElse(null);
        var ph = PointsHistory.builder()
                .user(user)
                .type(PointsTxType.EARNED)
                .points(points)
                .balance(balance)
                .description("Booking confirmed")
                .booking(booking)
                .build();
        pointsHistoryRepository.save(ph);
        checkTierUpgrade(membership);
    }

    private void checkTierUpgrade(Membership m) {
        var nextTier = membershipTierRepository.findAll().stream()
                .filter(t -> t.getLevel() > m.getTier().getLevel())
                .filter(t -> m.getTotalPoints() >= t.getPointsRequired())
                .max((a, b) -> Integer.compare(a.getLevel(), b.getLevel()))
                .orElse(null);
        if (nextTier != null) {
            m.setTier(nextTier);
            membershipRepository.save(m);
        }
    }

    public Page<PointsHistory> getPointsHistory(UUID userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return pointsHistoryRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
    }

    private MembershipResponse toResponse(Membership m) {
        var t = m.getTier();
        return MembershipResponse.builder()
                .id(m.getId())
                .userId(m.getUser().getId())
                .tierId(t.getId())
                .tierName(t.getName())
                .tierLevel(t.getLevel())
                .currentPoints(m.getCurrentPoints())
                .totalPoints(m.getTotalPoints())
                .pointsRequired(t.getPointsRequired())
                .benefits(t.getBenefits())
                .discountPercent(t.getDiscountPercent())
                .color(t.getColor())
                .memberSince(m.getMemberSince())
                .expiresAt(m.getExpiresAt())
                .build();
    }
}
