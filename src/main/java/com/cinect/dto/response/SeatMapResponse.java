package com.cinect.dto.response;

import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SeatMapResponse {
    private List<SeatResponse> seats;
    private Set<UUID> bookedSeatIds;
    private Set<UUID> heldSeatIds;
}
