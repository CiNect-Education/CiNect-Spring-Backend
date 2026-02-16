package com.cinect.dto.request;

import com.cinect.entity.enums.SeatStatus;
import com.cinect.entity.enums.SeatType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BulkUpdateSeatsRequest {
    private List<SeatUpdateItem> seats;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SeatUpdateItem {
        private UUID id;
        private String rowLabel;
        private Integer number;
        private SeatType type;
        private SeatStatus status;
        private Boolean isAisle;
        private BigDecimal price;
    }
}
