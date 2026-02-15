package com.cinect.dto.response;

import com.cinect.entity.enums.SeatType;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingItemResponse {
    private UUID id;
    private UUID seatId;
    private String rowLabel;
    private Integer seatNumber;
    private SeatType seatType;
    private BigDecimal price;
}
