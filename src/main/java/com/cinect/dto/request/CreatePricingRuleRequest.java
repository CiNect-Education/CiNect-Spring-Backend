package com.cinect.dto.request;

import com.cinect.entity.enums.DayType;
import com.cinect.entity.enums.RoomFormat;
import com.cinect.entity.enums.SeatType;
import com.cinect.entity.enums.TimeSlot;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreatePricingRuleRequest {
    private String name;
    private UUID cinemaId;
    private SeatType seatType;
    private RoomFormat format;
    private DayType dayType;
    private TimeSlot timeSlot;
    @Builder.Default
    private Boolean isHoliday = false;
    @NotNull @DecimalMin("0")
    private BigDecimal price;
}
