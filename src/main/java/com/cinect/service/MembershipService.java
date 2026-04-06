package com.cinect.service;

import com.cinect.dto.response.MembershipResponse;
import com.cinect.dto.response.MembershipTierResponse;
import com.cinect.dto.response.ShowtimeResponse;
import com.cinect.entity.Membership;
import com.cinect.entity.MembershipTier;
import com.cinect.entity.PointsHistory;
import com.cinect.entity.enums.PointsTxType;
import com.cinect.repository.BookingRepository;
import com.cinect.repository.MembershipRepository;
import com.cinect.repository.MembershipTierRepository;
import com.cinect.repository.PointsHistoryRepository;
import com.cinect.repository.ShowtimeRepository;
import com.cinect.repository.UserRepository;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipTierRepository membershipTierRepository;
    private final PointsHistoryRepository pointsHistoryRepository;
    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;
    private static final int DAILY_CHECKIN_BASE_POINTS = 10;
    private static final int DAILY_CHECKIN_MAX_POINTS = 30;

    @Transactional(readOnly = true)
    public MembershipResponse getProfile(UUID userId) {
        var m = getOrCreateMembership(userId);
        return toResponse(m);
    }

    public List<MembershipTierResponse> getTiers() {
        return membershipTierRepository.findAll().stream()
                .map(t -> MembershipTierResponse.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .level(t.getLevel())
                        .pointsRequired(t.getPointsRequired())
                        .benefits(t.getBenefits())
                        .discountPercent(t.getDiscountPercent())
                        .color(t.getColor())
                        .icon(t.getIcon())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDailyCheckinStatus(UUID userId) {
        var membership = getOrCreateMembership(userId);
        var now = Instant.now();
        var todayStart = startOfToday();
        var yesterdayStart = todayStart.minusSeconds(24 * 60 * 60);
        var last = membership.getLastDailyCheckinAt();
        var eligibleToday = last == null || last.isBefore(todayStart);
        var continuing = last != null && !last.isBefore(yesterdayStart) && last.isBefore(todayStart);
        var nextStreak = eligibleToday ? (continuing ? membership.getDailyCheckinStreak() + 1 : 1)
                : membership.getDailyCheckinStreak();
        var todayClaim = pointsHistoryRepository
                .findTopByUser_IdAndTypeAndDescriptionStartingWithAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
                        userId, PointsTxType.EARNED, "Daily check-in reward", todayStart)
                .orElse(null);
        var rewardPoints = eligibleToday ? calculateDailyReward(nextStreak)
                : Math.max(0, todayClaim != null ? todayClaim.getPoints() : 0);

        var payload = new HashMap<String, Object>();
        payload.put("eligibleToday", eligibleToday);
        payload.put("rewardPoints", rewardPoints);
        payload.put("nextRewardPoints", calculateDailyReward(nextStreak));
        payload.put("streak", membership.getDailyCheckinStreak());
        payload.put("nextStreak", nextStreak);
        payload.put("currentPoints", membership.getCurrentPoints());
        payload.put("totalPoints", membership.getTotalPoints());
        payload.put("lastCheckinAt", membership.getLastDailyCheckinAt());
        payload.put("nextEligibleAt", eligibleToday ? now : nextDayStart());
        return payload;
    }

    @Transactional
    public Map<String, Object> claimDailyCheckin(UUID userId) {
        var membership = membershipRepository.findProfileByUserIdForUpdate(userId)
                .orElseGet(() -> getOrCreateMembership(userId));
        var now = Instant.now();
        var todayStart = startOfToday();
        var yesterdayStart = todayStart.minusSeconds(24 * 60 * 60);
        var last = membership.getLastDailyCheckinAt();

        if (last != null && !last.isBefore(todayStart)) {
            throw new BadRequestException("Daily check-in already claimed today");
        }

        var nextStreak = (last != null && !last.isBefore(yesterdayStart)) ? membership.getDailyCheckinStreak() + 1 : 1;
        var rewardPoints = calculateDailyReward(nextStreak);
        membership.setLastDailyCheckinAt(now);
        membership.setDailyCheckinStreak(nextStreak);
        membership.setCurrentPoints(membership.getCurrentPoints() + rewardPoints);
        membership.setTotalPoints(membership.getTotalPoints() + rewardPoints);
        membershipRepository.save(membership);

        var ph = PointsHistory.builder()
                .user(membership.getUser())
                .type(PointsTxType.EARNED)
                .points(rewardPoints)
                .balance(membership.getCurrentPoints())
                .description("Daily check-in reward (Day " + nextStreak + ")")
                .build();
        pointsHistoryRepository.save(ph);

        checkTierUpgrade(membership);

        return Map.of(
                "success", true,
                "claimedAt", now,
                "rewardPoints", rewardPoints,
                "streak", nextStreak,
                "currentPoints", membership.getCurrentPoints(),
                "totalPoints", membership.getTotalPoints());
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

    public List<ShowtimeResponse> getMemberEvents() {
        var showtimes = showtimeRepository.findByMemberExclusiveAndStartTimeAfter(true, Instant.now());
        return showtimes.stream().map(s -> ShowtimeResponse.builder()
                .id(s.getId())
                .movieId(s.getMovie().getId())
                .movieTitle(s.getMovie().getTitle())
                .cinemaId(s.getCinema().getId())
                .cinemaName(s.getCinema().getName())
                .roomId(s.getRoom().getId())
                .roomName(s.getRoom().getName())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .basePrice(s.getBasePrice())
                .format(s.getFormat())
                .language(s.getLanguage())
                .subtitles(s.getSubtitles())
                .isActive(s.getIsActive())
                .memberExclusive(s.getMemberExclusive())
                .createdAt(s.getCreatedAt())
                .build()
        ).collect(Collectors.toList());
    }

    private MembershipTierResponse toTierResponse(MembershipTier t) {
        return MembershipTierResponse.builder()
                .id(t.getId())
                .name(t.getName())
                .level(t.getLevel())
                .pointsRequired(t.getPointsRequired())
                .benefits(t.getBenefits())
                .discountPercent(t.getDiscountPercent())
                .color(t.getColor())
                .icon(t.getIcon())
                .build();
    }

    private MembershipResponse toResponse(Membership m) {
        var t = m.getTier();
        var nextTier = membershipTierRepository.findAll().stream()
                .filter(x -> x.getLevel() > t.getLevel())
                .min(Comparator.comparing(MembershipTier::getLevel))
                .orElse(null);
        Integer pointsToNext = null;
        if (nextTier != null) {
            pointsToNext = Math.max(0, nextTier.getPointsRequired() - m.getCurrentPoints());
        }
        return MembershipResponse.builder()
                .userId(m.getUser().getId())
                .tier(toTierResponse(t))
                .currentPoints(m.getCurrentPoints())
                .totalPoints(m.getTotalPoints())
                .nextTier(nextTier != null ? toTierResponse(nextTier) : null)
                .pointsToNextTier(pointsToNext)
                .dailyCheckinStreak(m.getDailyCheckinStreak())
                .lastDailyCheckinAt(m.getLastDailyCheckinAt())
                .memberSince(m.getMemberSince())
                .expiresAt(m.getExpiresAt())
                .build();
    }

    private Membership getOrCreateMembership(UUID userId) {
        var found = membershipRepository.findProfileByUserId(userId).orElse(null);
        if (found != null) return found;

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var tier = membershipTierRepository.findAll().stream()
                .min(Comparator.comparing(MembershipTier::getLevel))
                .orElseThrow(() -> new ResourceNotFoundException("Membership tier not found"));

        return membershipRepository.save(Membership.builder()
                .user(user)
                .tier(tier)
                .currentPoints(0)
                .totalPoints(0)
                .dailyCheckinStreak(0)
                .memberSince(Instant.now())
                .build());
    }

    private int calculateDailyReward(int streak) {
        var extra = Math.max(0, streak - 1) * 2;
        return Math.min(DAILY_CHECKIN_BASE_POINTS + extra, DAILY_CHECKIN_MAX_POINTS);
    }

    private Instant startOfToday() {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    private Instant nextDayStart() {
        return LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}
