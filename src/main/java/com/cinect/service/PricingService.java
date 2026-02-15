package com.cinect.service;

import com.cinect.entity.Cinema;
import com.cinect.entity.Seat;
import com.cinect.entity.Showtime;
import com.cinect.entity.enums.DayType;
import com.cinect.entity.enums.RoomFormat;
import com.cinect.entity.enums.SeatType;
import com.cinect.entity.enums.TimeSlot;
import com.cinect.repository.PricingRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PricingRuleRepository pricingRuleRepository;

    public BigDecimal calculateSeatPrice(Showtime showtime, Seat seat, UUID cinemaId) {
        var rules = pricingRuleRepository.findMatchingRules(
                cinemaId,
                seat.getType(),
                showtime.getFormat(),
                resolveDayType(showtime.getStartTime()),
                resolveTimeSlot(showtime.getStartTime()));
        if (rules.isEmpty()) {
            return showtime.getBasePrice();
        }
        var best = rules.stream()
                .max((a, b) -> Integer.compare(specificity(a), specificity(b)))
                .orElse(null);
        return best != null ? best.getPrice() : showtime.getBasePrice();
    }

    private int specificity(com.cinect.entity.PricingRule r) {
        int score = 0;
        if (r.getCinema() != null) score += 8;
        if (r.getSeatType() != null) score += 4;
        if (r.getFormat() != null) score += 2;
        if (r.getDayType() != null) score += 1;
        if (r.getTimeSlot() != null) score += 1;
        return score;
    }

    private DayType resolveDayType(Instant instant) {
        var date = LocalDate.ofInstant(instant, ZoneId.of("UTC"));
        var day = date.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) return DayType.WEEKEND;
        return DayType.WEEKDAY;
    }

    private TimeSlot resolveTimeSlot(Instant instant) {
        var hour = instant.atZone(ZoneId.of("UTC")).getHour();
        if (hour < 12) return TimeSlot.MORNING;
        if (hour < 17) return TimeSlot.AFTERNOON;
        if (hour < 21) return TimeSlot.EVENING;
        return TimeSlot.NIGHT;
    }
}
