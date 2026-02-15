package com.cinect.dto.response;

import com.cinect.entity.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingResponse {
    private UUID id;
    private UUID userId;
    private UUID showtimeId;
    private ShowtimeResponse showtime;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BookingStatus status;
    private String promotionCode;
    private Integer pointsUsed;
    private String giftCardCode;
    private String qrCode;
    private Instant expiresAt;
    private List<BookingItemResponse> items;
    private List<BookingSnackResponse> snacks;
    private Instant createdAt;
}
