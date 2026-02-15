package com.cinect.dto.response;

import com.cinect.entity.enums.HoldStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HoldResponse {
    private UUID id;
    private UUID userId;
    private UUID showtimeId;
    private List<UUID> seatIds;
    private HoldStatus status;
    private Instant expiresAt;
    private Instant createdAt;
}
