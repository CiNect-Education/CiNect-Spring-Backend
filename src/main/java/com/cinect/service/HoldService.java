package com.cinect.service;

import com.cinect.dto.request.CreateHoldRequest;
import com.cinect.dto.response.HoldResponse;
import com.cinect.entity.Hold;
import com.cinect.entity.HoldSeat;
import com.cinect.entity.Seat;
import com.cinect.entity.Showtime;
import com.cinect.entity.User;
import com.cinect.entity.enums.HoldStatus;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ConflictException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.BookingItemRepository;
import com.cinect.repository.HoldRepository;
import com.cinect.repository.HoldSeatRepository;
import com.cinect.repository.SeatRepository;
import com.cinect.repository.ShowtimeRepository;
import com.cinect.repository.UserRepository;
import com.cinect.websocket.SeatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HoldService {

    private final HoldRepository holdRepository;
    private final HoldSeatRepository holdSeatRepository;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final BookingItemRepository bookingItemRepository;
    private final SeatWebSocketHandler seatWebSocketHandler;

    @Value("${app.hold-ttl-minutes:10}")
    private int holdTtlMinutes;

    @Transactional
    public HoldResponse createHold(UUID userId, CreateHoldRequest req) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var showtime = showtimeRepository.findById(req.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
        if (showtime.getStartTime().isBefore(Instant.now())) {
            throw new BadRequestException("Showtime has already started");
        }

        var seats = seatRepository.findByIdsForUpdate(req.getSeatIds());
        if (seats.size() != req.getSeatIds().size()) {
            throw new BadRequestException("One or more seats not found");
        }
        for (Seat s : seats) {
            if (!s.getRoom().getId().equals(showtime.getRoom().getId())) {
                throw new BadRequestException("Seat does not belong to showtime room");
            }
        }

        var now = Instant.now();
        var heldIds = holdSeatRepository.findHeldSeatIds(req.getShowtimeId(), now);
        var bookedIds = bookingItemRepository.findBookedSeatIds(req.getShowtimeId());

        for (UUID seatId : req.getSeatIds()) {
            if (heldIds.contains(seatId)) {
                throw new ConflictException("Seat already held");
            }
            if (bookedIds.contains(seatId)) {
                throw new ConflictException("Seat already booked");
            }
        }

        var expiresAt = now.plusSeconds(holdTtlMinutes * 60L);
        var hold = Hold.builder()
                .user(user)
                .showtime(showtime)
                .seatIds(req.getSeatIds())
                .status(HoldStatus.ACTIVE)
                .expiresAt(expiresAt)
                .build();
        hold = holdRepository.save(hold);

        for (UUID seatId : req.getSeatIds()) {
            var hs = HoldSeat.builder()
                    .holdId(hold.getId())
                    .seatId(seatId)
                    .showtimeId(req.getShowtimeId())
                    .build();
            holdSeatRepository.save(hs);
        }

        seatWebSocketHandler.broadcastSeatEvent(req.getShowtimeId(), SeatWebSocketHandler.SEAT_HELD, req.getSeatIds());

        return toResponse(hold);
    }

    @Transactional
    public void releaseHold(UUID holdId, UUID userId) {
        var hold = holdRepository.findById(holdId)
                .orElseThrow(() -> new ResourceNotFoundException("Hold not found"));
        if (!hold.getUser().getId().equals(userId)) {
            throw new BadRequestException("Not authorized to release this hold");
        }
        if (hold.getStatus() != HoldStatus.ACTIVE) {
            return;
        }
        hold.setStatus(HoldStatus.RELEASED);
        holdRepository.save(hold);
        holdSeatRepository.deleteByHoldId(holdId);
        seatWebSocketHandler.broadcastSeatEvent(hold.getShowtime().getId(), SeatWebSocketHandler.SEAT_RELEASED, hold.getSeatIds());
    }

    public HoldResponse getActiveHold(UUID userId, UUID showtimeId) {
        var hold = holdRepository.findActiveByUserAndShowtime(userId, showtimeId, Instant.now())
                .orElse(null);
        return hold != null ? toResponse(hold) : null;
    }

    private HoldResponse toResponse(Hold h) {
        return HoldResponse.builder()
                .id(h.getId())
                .userId(h.getUser().getId())
                .showtimeId(h.getShowtime().getId())
                .seatIds(h.getSeatIds())
                .status(h.getStatus())
                .expiresAt(h.getExpiresAt())
                .createdAt(h.getCreatedAt())
                .build();
    }
}
