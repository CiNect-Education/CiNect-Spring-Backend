package com.cinect.dto.response;

import com.cinect.entity.enums.SeatType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingItemResponse {
    private UUID id;
    private UUID seatId;

    @JsonProperty("row")
    private String rowLabel;

    @JsonProperty("number")
    private Integer seatNumber;

    @JsonProperty("type")
    private SeatType seatType;

    private BigDecimal price;
}
