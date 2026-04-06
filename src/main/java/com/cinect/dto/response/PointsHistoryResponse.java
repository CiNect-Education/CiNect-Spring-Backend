package com.cinect.dto.response;

import com.cinect.entity.enums.PointsTxType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointsHistoryResponse {
    private UUID id;
    private PointsTxType type;
    private Integer points;
    private Integer balance;
    private String description;
    private UUID bookingId;
    private Instant createdAt;
}

