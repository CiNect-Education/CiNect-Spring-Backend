package com.cinect.service;

import com.cinect.dto.request.CreateBookingRequest;
import com.cinect.dto.response.BookingItemResponse;
import com.cinect.dto.response.BookingResponse;
import com.cinect.dto.response.BookingSnackResponse;
import com.cinect.dto.response.ShowtimeResponse;
import com.cinect.entity.*;
import com.cinect.entity.enums.BookingStatus;
import com.cinect.entity.enums.HoldStatus;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.*;
import com.cinect.websocket.SeatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HoldRepository holdRepository;
    private final HoldSeatRepository holdSeatRepository;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final SnackRepository snackRepository;
    private final PricingService pricingService;
    private final PromotionService promotionService;
    private final MembershipService membershipService;
    private final SeatWebSocketHandler seatWebSocketHandler;
    private final GiftCardService giftCardService;

    @Value("${app.payment-timeout-minutes:2}")
    private int paymentTimeoutMinutes;

    @Value("${app.points-per-booking:10}")
    private int pointsPerBooking;

    @Transactional
    public BookingResponse createBooking(UUID userId, CreateBookingRequest req) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var hold = holdRepository.findById(req.getHoldId())
                .orElseThrow(() -> new ResourceNotFoundException("Hold not found"));
        if (!hold.getUser().getId().equals(userId)) {
            throw new BadRequestException("Hold does not belong to user");
        }
        if (hold.getStatus() != HoldStatus.ACTIVE || hold.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Hold expired or invalid");
        }
        if (!hold.getShowtime().getId().equals(req.getShowtimeId())) {
            throw new BadRequestException("Showtime mismatch");
        }
        var seatIdsSet = new HashSet<>(hold.getSeatIds());
        if (!seatIdsSet.equals(new HashSet<>(req.getSeatIds()))) {
            throw new BadRequestException("Seat IDs must match hold");
        }

        var showtime = hold.getShowtime();
        var seats = seatRepository.findAllById(req.getSeatIds());
        var seatMap = seats.stream().collect(Collectors.toMap(Seat::getId, s -> s));

        BigDecimal seatTotal = BigDecimal.ZERO;
        for (UUID seatId : req.getSeatIds()) {
            var seat = seatMap.get(seatId);
            if (seat == null) throw new BadRequestException("Seat not found: " + seatId);
            seatTotal = seatTotal.add(pricingService.calculateSeatPrice(showtime, seat, showtime.getCinema().getId()));
        }

        BigDecimal snackTotal = BigDecimal.ZERO;
        if (req.getSnacks() != null && !req.getSnacks().isEmpty()) {
            for (var sn : req.getSnacks()) {
                var snack = snackRepository.findById(sn.getSnackId())
                        .orElseThrow(() -> new ResourceNotFoundException("Snack not found"));
                snackTotal = snackTotal.add(snack.getPrice().multiply(BigDecimal.valueOf(sn.getQuantity())));
            }
        }

        var subtotal = seatTotal.add(snackTotal);
        var discountAmount = promotionService.validateAndApply(
                req.getPromotionCode(), subtotal, userId);

        var pointsUsed = req.getPointsUsed() != null ? req.getPointsUsed() : 0;
        var pointsDiscount = BigDecimal.ZERO;
        if (pointsUsed > 0) {
            var membership = membershipService.getProfile(userId);
            if (membership != null && membership.getCurrentPoints() >= pointsUsed) {
                pointsDiscount = BigDecimal.valueOf(pointsUsed);
                discountAmount = discountAmount.add(pointsDiscount);
            } else {
                pointsUsed = 0;
            }
        }

        var giftCardCode = req.getGiftCardCode();
        if (giftCardCode != null && !giftCardCode.isBlank()) {
            var gcAmount = giftCardService.getBalance(giftCardCode);
            if (gcAmount != null && gcAmount.compareTo(BigDecimal.ZERO) > 0) {
                var gcDiscount = gcAmount.min(subtotal.subtract(discountAmount));
                discountAmount = discountAmount.add(gcDiscount);
            }
        }

        var finalAmount = subtotal.subtract(discountAmount).max(BigDecimal.ZERO);
        var expiresAt = Instant.now().plusSeconds(paymentTimeoutMinutes * 60L);

        var booking = Booking.builder()
                .user(user)
                .showtime(showtime)
                .hold(hold)
                .totalAmount(subtotal)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .status(BookingStatus.PENDING)
                .promotionCode(req.getPromotionCode())
                .pointsUsed(pointsUsed)
                .giftCardCode(giftCardCode)
                .qrCode(UUID.randomUUID().toString())
                .expiresAt(expiresAt)
                .build();
        booking = bookingRepository.save(booking);

        for (UUID seatId : req.getSeatIds()) {
            var seat = seatMap.get(seatId);
            var price = pricingService.calculateSeatPrice(showtime, seat, showtime.getCinema().getId());
            var item = BookingItem.builder()
                    .booking(booking)
                    .seat(seat)
                    .showtime(showtime)
                    .rowLabel(seat.getRowLabel())
                    .seatNumber(seat.getNumber())
                    .seatType(seat.getType())
                    .price(price)
                    .build();
            booking.getItems().add(item);
        }

        if (req.getSnacks() != null) {
            for (var sn : req.getSnacks()) {
                var snack = snackRepository.findById(sn.getSnackId()).orElseThrow();
                var qty = sn.getQuantity();
                var unitPrice = snack.getPrice();
                var total = unitPrice.multiply(BigDecimal.valueOf(qty));
                var bs = BookingSnack.builder()
                        .booking(booking)
                        .snack(snack)
                        .name(snack.getName())
                        .quantity(qty)
                        .unitPrice(unitPrice)
                        .totalPrice(total)
                        .build();
                booking.getSnacks().add(bs);
            }
        }
        bookingRepository.save(booking);

        hold.setStatus(HoldStatus.CONVERTED);
        holdRepository.save(hold);
        holdSeatRepository.deleteByHoldId(hold.getId());

        return toResponse(booking);
    }

    @Transactional
    public BookingResponse confirmBooking(UUID bookingId, UUID userId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new BadRequestException("Not authorized");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Booking already processed");
        }
        var hasPaidPayment = booking.getPayments().stream()
                .anyMatch(p -> p.getStatus() == com.cinect.entity.enums.PaymentStatus.PAID);
        if (!hasPaidPayment) {
            throw new BadRequestException("Payment required to confirm");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);
        membershipService.earnPoints(userId, booking.getId(), pointsPerBooking);

        // Emit seat booked event via WebSocket
        List<UUID> bookedSeatIds = booking.getItems().stream()
                .map(bi -> bi.getSeat().getId())
                .collect(Collectors.toList());
        if (!bookedSeatIds.isEmpty()) {
            seatWebSocketHandler.broadcastSeatEvent(
                    booking.getShowtime().getId(), SeatWebSocketHandler.SEAT_BOOKED, bookedSeatIds);
        }

        return toResponse(booking);
    }

    @Transactional
    public void cancelBooking(UUID bookingId, UUID userId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new BadRequestException("Not authorized");
        }
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Cannot cancel this booking");
        }
        // Collect seat IDs before cancelling for WebSocket notification
        List<UUID> releasedSeatIds = booking.getItems().stream()
                .map(bi -> bi.getSeat().getId())
                .collect(Collectors.toList());

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Emit seat released event via WebSocket
        if (!releasedSeatIds.isEmpty()) {
            seatWebSocketHandler.broadcastSeatEvent(
                    booking.getShowtime().getId(), SeatWebSocketHandler.SEAT_RELEASED, releasedSeatIds);
        }
    }

    public Page<BookingResponse> getUserBookings(UUID userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return bookingRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    public org.springframework.data.domain.Page<BookingResponse> getBookingsForAdmin(
            com.cinect.entity.enums.BookingStatus status, String search, int page, int limit) {
        var pageable = PageRequest.of(page, limit);
        return bookingRepository.findAllFiltered(status, search == null || search.isBlank() ? null : search, pageable)
                .map(this::toResponse);
    }

    public BookingResponse getById(UUID id, UUID userId) {
        var booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new BadRequestException("Not authorized");
        }
        return toResponse(booking);
    }

    private BookingResponse toResponse(Booking b) {
        var st = b.getShowtime();
        var showtimeResp = st != null ? ShowtimeResponse.builder()
                .id(st.getId())
                .movieId(st.getMovie().getId())
                .movieTitle(st.getMovie().getTitle())
                .roomId(st.getRoom().getId())
                .roomName(st.getRoom().getName())
                .cinemaId(st.getCinema().getId())
                .cinemaName(st.getCinema().getName())
                .startTime(st.getStartTime())
                .endTime(st.getEndTime())
                .basePrice(st.getBasePrice())
                .format(st.getFormat())
                .language(st.getLanguage())
                .subtitles(st.getSubtitles())
                .isActive(st.getIsActive())
                .memberExclusive(st.getMemberExclusive())
                .createdAt(st.getCreatedAt())
                .build() : null;
        var items = b.getItems() != null
                ? b.getItems().stream().map(i -> BookingItemResponse.builder()
                .id(i.getId())
                .seatId(i.getSeat().getId())
                .rowLabel(i.getRowLabel())
                .seatNumber(i.getSeatNumber())
                .seatType(i.getSeatType())
                .price(i.getPrice())
                .build()).collect(Collectors.toList())
                : List.<BookingItemResponse>of();
        var snacks = b.getSnacks() != null
                ? b.getSnacks().stream().map(s -> BookingSnackResponse.builder()
                .name(s.getName())
                .quantity(s.getQuantity())
                .unitPrice(s.getUnitPrice())
                .totalPrice(s.getTotalPrice())
                .build()).collect(Collectors.toList())
                : List.<BookingSnackResponse>of();
        return BookingResponse.builder()
                .id(b.getId())
                .userId(b.getUser().getId())
                .showtimeId(b.getShowtime().getId())
                .showtime(showtimeResp)
                .totalAmount(b.getTotalAmount())
                .discountAmount(b.getDiscountAmount())
                .finalAmount(b.getFinalAmount())
                .status(b.getStatus())
                .promotionCode(b.getPromotionCode())
                .pointsUsed(b.getPointsUsed())
                .giftCardCode(b.getGiftCardCode())
                .qrCode(b.getQrCode())
                .expiresAt(b.getExpiresAt())
                .items(items)
                .snacks(snacks)
                .createdAt(b.getCreatedAt())
                .build();
    }
}
