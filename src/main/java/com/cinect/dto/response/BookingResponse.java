package com.cinect.dto.response;

import com.cinect.entity.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * API shape aligned with cinect-frontend {@code bookingSchema}: flat movie/showtime fields,
 * {@code seats} (not items), ISO {@code showtime} string.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private UUID id;
    private UUID userId;
    private UUID showtimeId;
    /** Show start time (ISO-8601), same as Nest {@code mapBookingToApi}. */
    private String showtime;
    private String movieTitle;
    private String moviePosterUrl;
    private String cinemaName;
    private String roomName;
    /** Display format: 2D, 3D, IMAX, 4DX, DOLBY */
    private String format;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BookingStatus status;
    private String promotionCode;
    private Integer pointsUsed;
    private String giftCardCode;
    private String qrCode;
    private Instant expiresAt;
    private List<BookingItemResponse> seats;
    private List<BookingSnackResponse> snacks;
    private PaymentResponse payment;
    private Instant createdAt;
    private Instant updatedAt;
}
