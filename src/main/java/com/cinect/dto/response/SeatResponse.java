package com.cinect.dto.response;

import com.cinect.entity.enums.SeatStatus;
import com.cinect.entity.enums.SeatType;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SeatResponse {
    private UUID id;
    private UUID roomId;
    private String rowLabel;
    private Integer number;
    private SeatType type;
    private SeatStatus status;
    private Boolean isAisle;
    private BigDecimal price;
}
